package com.datai.integration.factory.impl;

import com.salesforce.eventbus.protobuf.PubSubGrpc;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * Pub/Sub API 连接工厂
 * 负责构建带有认证头的 gRPC Stub
 */
@Slf4j
@Component
public class PubSubConnectionFactory {

    @Autowired
    private SOAPConnectionFactory soapConnectionFactory; // 复用现有的 SOAP 登录逻辑

    private ManagedChannel channel;
    private PubSubGrpc.PubSubStub asyncStub;
    private PubSubGrpc.PubSubBlockingStub blockingStub;

    /**
     * 获取或创建 gRPC Stub (异步)
     */
    public synchronized PubSubGrpc.PubSubStub getAsyncStub() {
        ensureConnected();
        return asyncStub;
    }

    /**
     * 获取或创建 gRPC Stub (同步，用于获取 Schema)
     */
    public synchronized PubSubGrpc.PubSubBlockingStub getBlockingStub() {
        ensureConnected();
        return blockingStub;
    }

    private void ensureConnected() {
        if (channel != null && !channel.isShutdown() && !channel.isTerminated()) {
            return;
        }
        log.info("初始化 Salesforce Pub/Sub API gRPC 连接...");
        createChannelAndStubs();
    }

    private void createChannelAndStubs() {
        try {
            // 1. 获取认证信息 (Session ID & Endpoint)
            PartnerConnection connection = (PartnerConnection) soapConnectionFactory.getConnection();
            String sessionId = connection.getConfig().getSessionId();
            String serverUrl = connection.getConfig().getServiceEndpoint();

            // 解析 Endpoint (e.g., https://instance.salesforce.com/...)
            URI uri = new URI(serverUrl);
            String host = uri.getHost();
            int port = 443; // gRPC 默认端口

            // 2. 构造 Metadata Headers
            Metadata headers = new Metadata();
            Metadata.Key<String> AUTH_KEY = Metadata.Key.of("accesstoken", Metadata.ASCII_STRING_MARSHALLER);
            Metadata.Key<String> INSTANCE_KEY = Metadata.Key.of("instanceurl", Metadata.ASCII_STRING_MARSHALLER);
            Metadata.Key<String> TENANT_KEY = Metadata.Key.of("tenantid", Metadata.ASCII_STRING_MARSHALLER);

            headers.put(AUTH_KEY, sessionId);
            headers.put(INSTANCE_KEY, serverUrl);
            try {
                // 尝试获取 Tenant ID，如果 SOAP API 未提供，部分环境可忽略
                headers.put(TENANT_KEY, connection.getUserInfo().getOrganizationId());
            } catch (Exception e) {
                log.warn("无法自动获取 TenantId，将尝试不带 TenantId 连接");
            }

            // 3. 创建 Channel
            this.channel = ManagedChannelBuilder.forAddress(host, port)
                    .useTransportSecurity() // 必须使用 TLS
                    .keepAliveTime(60, TimeUnit.SECONDS)
                    .build();

            // 4. 创建 Stubs 并注入 Header
            this.asyncStub = MetadataUtils.attachHeaders(PubSubGrpc.newStub(channel), headers);
            this.blockingStub = MetadataUtils.attachHeaders(PubSubGrpc.newBlockingStub(channel), headers);

            log.info("Salesforce Pub/Sub API 连接初始化成功。Host: {}", host);

        } catch (Exception e) {
            log.error("创建 Pub/Sub 连接失败", e);
            throw new RuntimeException("无法创建 Pub/Sub 连接", e);
        }
    }

    @PreDestroy
    public void close() {
        if (channel != null) {
            channel.shutdown();
            try {
                if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
                    channel.shutdownNow();
                }
            } catch (InterruptedException e) {
                channel.shutdownNow();
            }
        }
    }
}