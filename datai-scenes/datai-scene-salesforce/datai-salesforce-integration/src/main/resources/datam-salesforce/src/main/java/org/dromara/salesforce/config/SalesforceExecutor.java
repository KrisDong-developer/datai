package org.dromara.salesforce.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.dromara.salesforce.mapper.CustomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Salesforce专用线程池执行器
 * <p>
 * 该类提供了Salesforce数据处理任务的线程池管理功能，支持任务优先级排序和批量处理。
 * 使用PriorityBlockingQueue作为任务队列，确保高优先级任务优先执行。
 * </p>
 *
 */
@Service
@Slf4j
@Lazy
public class SalesforceExecutor {

    /**
     * Salesforce任务执行线程池
     */
    public ThreadPoolExecutor executorService;



    /**
     * 线程池大小
     */
    private Integer executorSize;

    /**
     * 初始化线程池
     * <p>
     * 从系统配置中获取线程池大小配置，创建具有优先级任务队列的线程池。
     * 默认线程池大小为系统CPU核心数。
     * </p>
     */
    @PostConstruct
    public void init() {
        executorSize = Runtime.getRuntime().availableProcessors();

        this.executorService = new ThreadPoolExecutor(
                executorSize,
                executorSize,
                30,
                TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("salesforce-executor-%d").build(),
                (r, executor) -> {
                    log.error("Salesforce任务被拒绝: {}", r);
                    throw new RejectedExecutionException("Salesforce任务被拒绝: " + r.toString());
                });

        log.info("Salesforce线程池初始化完成，线程数: {}", executorSize);
    }

    /**
     * 等待所有任务执行完成
     * <p>
     * 阻塞当前线程，直到所有给定的Future任务执行完成。
     * </p>
     *
     * @param futures 要等待的Future任务数组
     * @throws InterruptedException 如果等待过程中线程被中断
     */
    public void waitForFutures(Future<?>... futures) throws InterruptedException {
        if (ArrayUtils.isEmpty(futures)) {
            return;
        }

        // 等待所有任务执行完成
        for (Future<?> future : futures) {
            try {
                if (future != null) {
                    future.get(); // 阻塞直到任务完成
                }
            } catch (ExecutionException e) {
                log.error("任务执行异常", e);
            }
        }
    }

    /**
     * 取消并移除任务
     * <p>
     * 取消给定的Future任务，并从线程池队列中移除。
     * </p>
     *
     * @param futures 要取消的Future任务数组
     */
    public void remove(Future<?>... futures) {
        if (ArrayUtils.isEmpty(futures)) {
            return;
        }

        for (Future<?> future : futures) {
            if (future != null) {
                future.cancel(true);
                if (future instanceof RunnableFuture) {
                    executorService.remove((RunnableFuture<?>) future);
                }
            }
        }
    }

    /**
     * 执行任务
     * <p>
     * 将任务提交到线程池执行，支持优先级设置。
     * </p>
     *
     * @param runnable 要执行的任务
     * @param batch    批次号（优先级相同时，值越小优先级越高）
     * @param index    优先级索引（值越大优先级越高）
     * @return 任务的Future对象
     */
    public Future<?> execute(Runnable runnable, int batch, int index) {
        ComparableFutureTask futureTask = new ComparableFutureTask(runnable, batch, index);
        executorService.execute(futureTask);
        return futureTask;
    }

    /**
     * 检查线程池是否为空
     * <p>
     * 判断线程池中是否有正在执行的任务或等待执行的任务。
     * </p>
     *
     * @return 如果线程池为空返回true，否则返回false
     */
    public boolean isEmpty() {
        return executorService.getActiveCount() == 0 && executorService.getQueue().isEmpty();
    }

    /**
     * 关闭线程池
     * <p>
     * 平滑关闭线程池，不再接受新任务，等待已提交任务执行完成。
     * </p>
     */
    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
            try {
                // 等待最多60秒关闭
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
            log.info("Salesforce线程池已关闭");
        }
    }

    /**
     * 立即关闭线程池
     * <p>
     * 立即关闭线程池，中断所有正在执行的任务。
     * </p>
     */
    public void shutdownNow() {
        if (executorService != null) {
            executorService.shutdownNow();
            log.info("Salesforce线程池已强制关闭");
        }
    }

    /**
     * 获取线程池大小
     *
     * @return 线程池大小
     */
    public Integer getExecutorSize() {
        return executorSize;
    }
}
