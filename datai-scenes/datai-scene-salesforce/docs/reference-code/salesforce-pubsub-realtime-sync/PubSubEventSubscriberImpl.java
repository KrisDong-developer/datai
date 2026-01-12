package com.datai.integration.realtime.impl;

import com.datai.integration.factory.impl.PubSubConnectionFactory;
import com.datai.integration.realtime.EventSubscriber;
import com.salesforce.eventbus.protobuf.FetchRequest;
import com.salesforce.eventbus.protobuf.FetchResponse;
import com.salesforce.eventbus.protobuf.ReplayPreset;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class PubSubEventSubscriberImpl implements EventSubscriber {

    @Autowired
    private PubSubConnectionFactory connectionFactory;

    @Autowired
    private EventProcessorImpl eventProcessor;

    private StreamObserver<FetchRequest> requestObserver;
    private final AtomicBoolean subscribed = new AtomicBoolean(false);
    private final String TOPIC = "/data/ChangeEvents"; // 监听所有变更事件
    private ScheduledExecutorService monitorExecutor;

    @Override
    @PostConstruct // 应用启动时自动尝试订阅（可选）
    public void startSubscription() {
        if (subscribed.get()) return;

        log.info("启动 Salesforce Pub/Sub API 订阅: {}", TOPIC);

        // 启动后台监控线程，处理断线重连
        monitorExecutor = Executors.newSingleThreadScheduledExecutor();
        monitorExecutor.scheduleWithFixedDelay(this::checkAndReconnect, 0, 30, TimeUnit.SECONDS);
    }

    private void checkAndReconnect() {
        if (!subscribed.get()) {
            try {
                doSubscribe();
            } catch (Exception e) {
                log.error("订阅尝试失败，将在下个周期重试: {}", e.getMessage());
            }
        }
    }

    private void doSubscribe() {
        StreamObserver<FetchResponse> responseObserver = new StreamObserver<FetchResponse>() {
            @Override
            public void onNext(FetchResponse response) {
                // 1. 处理接收到的事件列表
                if (response.getEventsCount() > 0) {
                    eventProcessor.processGrpcEvents(response.getEventsList());
                }

                // 2. 流控：处理完毕后，请求更多数据
                // 必须不断发送请求以保持流活跃
                requestMore(5);
            }

            @Override
            public void onError(Throwable t) {
                log.error("Pub/Sub 订阅流发生错误", t);
                subscribed.set(false);
                requestObserver = null;
            }

            @Override
            public void onCompleted() {
                log.info("Pub/Sub 订阅流由服务端结束");
                subscribed.set(false);
                requestObserver = null;
            }
        };

        // 获取请求流
        this.requestObserver = connectionFactory.getAsyncStub().subscribe(responseObserver);

        // 发送初始订阅请求
        FetchRequest initialRequest = FetchRequest.newBuilder()
                .setTopicName(TOPIC)
                .setReplayPreset(ReplayPreset.LATEST) // 从最新开始，或使用 CUSTOM + ReplayId
                .setNumRequested(5) // 初始请求数量
                .build();

        this.requestObserver.onNext(initialRequest);
        subscribed.set(true);
        log.info("订阅请求已发送");
    }

    private void requestMore(int num) {
        if (requestObserver != null && subscribed.get()) {
            // 后续请求只需告知 Topic 和数量
            FetchRequest req = FetchRequest.newBuilder()
                    .setTopicName(TOPIC)
                    .setNumRequested(num)
                    .build();
            requestObserver.onNext(req);
        }
    }

    @Override
    @PreDestroy
    public void stopSubscription() {
        subscribed.set(false);
        if (requestObserver != null) {
            try {
                requestObserver.onCompleted();
            } catch (Exception e) {
                // ignore
            }
        }
        if (monitorExecutor != null) {
            monitorExecutor.shutdown();
        }
    }

    @Override
    public boolean isSubscribed() {
        return subscribed.get();
    }
}