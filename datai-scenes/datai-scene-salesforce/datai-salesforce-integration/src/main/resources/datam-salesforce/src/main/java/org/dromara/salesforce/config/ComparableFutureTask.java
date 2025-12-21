package org.dromara.salesforce.config;

import lombok.Getter;

import java.util.concurrent.FutureTask;

/**
 * 自定义FutureTask，支持优先级排序
 * <p>
 * 该类扩展了FutureTask，实现了Comparable接口，可以根据优先级对任务进行排序。
 * 优先级规则：index值越大优先级越高；index相等时，batch值越小优先级越高。
 * </p>
 */
@Getter
public class ComparableFutureTask extends FutureTask<Object> implements Comparable<ComparableFutureTask> {

    /**
     * 优先级索引，值越大优先级越高
     * -- GETTER --
     *  获取任务的优先级索引
     *
     * @return 优先级索引

     */
    private final Integer index;

    /**
     * 批次号，值越小优先级越高（当index相等时）
     * -- GETTER --
     *  获取任务的批次号
     *
     * @return 批次号

     */
    private final Integer batch;

    /**
     * 构造函数
     *
     * @param runnable 要执行的任务
     * @param batch    批次号
     * @param index    优先级索引
     */
    public ComparableFutureTask(Runnable runnable, int batch, int index) {
        super(runnable, null);
        this.index = index;
        this.batch = batch;
    }

    /**
     * 比较任务优先级
     * <p>
     * 比较规则：
     * 1. index值大的优先级高
     * 2. index相等时，batch值小的优先级高
     * </p>
     *
     * @param other 要比较的另一个任务
     * @return 比较结果：负数表示当前任务优先级高，0表示优先级相等，正数表示当前任务优先级低
     */
    @Override
    public int compareTo(ComparableFutureTask other) {
        if (other == null) {
            return -1;
        }

        // 首先比较index，index大的优先级高
        int indexComparison = Integer.compare(other.index, this.index);
        if (indexComparison != 0) {
            return indexComparison;
        }

        // index相等时，比较batch，batch小的优先级高
        return Integer.compare(this.batch, other.batch);
    }

}
