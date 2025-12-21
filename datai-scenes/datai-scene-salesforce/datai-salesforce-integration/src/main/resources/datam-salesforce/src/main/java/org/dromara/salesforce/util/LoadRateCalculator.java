package org.dromara.salesforce.util;


import org.apache.logging.log4j.Logger;
import org.dromara.salesforce.model.Messages;

import java.util.Date;

/**
 * 加载速率计算器，用于计算和显示数据加载操作的进度信息。
 *
 * 该类在项目中的作用：
 * 1. 计算数据加载操作的处理速率
 * 2. 估算剩余处理时间
 * 3. 生成进度信息字符串供用户界面显示
 * 4. 跟踪跨多个作业的处理统计信息
 *
 * 主要功能：
 * 1. 记录操作开始时间
 * 2. 计算每小时处理记录数
 * 3. 估算完成剩余记录所需时间
 * 4. 生成格式化的进度信息字符串
 * 5. 跟踪成功和错误记录数
 *
 * 设计特点：
 * 1. 线程安全设计，关键方法使用synchronized关键字
 * 2. 支持跨多个作业的统计信息累计
 * 3. 提供详细的进度信息，包括处理速率和预计完成时间
 * 4. 处理边界情况，如处理时间计算不准确的情况
 *
 * 使用场景：
 * - 在数据加载操作中显示进度信息
 * - 计算和显示数据处理速率
 * - 估算操作完成时间
 * - 提供用户友好的进度反馈
 *
 */
public class LoadRateCalculator {

    /** 操作开始时间 */
    private Date startTime = null;

    /** 所有作业中的总记录数 */
    private long totalRecordsAcrossAllJobs = 0;

    /** 是否已开始处理 */
    private boolean started = false;

    /** 已完成作业中的成功记录数 */
    private long numSuccessesAcrossCompletedJobs = 0;

    /** 已完成作业中的错误记录数 */
    private long numErrorsAcrossCompletedJobs = 0;

    /** 日志记录器 */
    private Logger logger;

    /** 是否已调用calculateSubTask方法 */
    private boolean isCalculateSubTaskInvoked = false;

    /**
     * 构造函数，初始化日志记录器
     */
    public LoadRateCalculator() {
        logger = DLLogManager.getLogger(LoadRateCalculator.class);
    }

    /**
     * 开始处理操作
     *
     * @param numRecords 总记录数
     */
    public synchronized void start(int numRecords) {
        if (numSuccessesAcrossCompletedJobs != 0) {
            logger.debug("numSuccessesAcrossCompletedJobs = " + numSuccessesAcrossCompletedJobs);
        }
        if (numErrorsAcrossCompletedJobs != 0) {
            logger.debug("numErrorsAcrossCompletedJobs = " + numErrorsAcrossCompletedJobs);
        }
        if (!started) {
            started = true;
            this.startTime = new Date();
            this.totalRecordsAcrossAllJobs = numRecords;
        }
    }

    /**
     * 计算子任务进度信息
     *
     * @param processedRecordsInJob 作业中已处理的记录数
     * @param numErrorsInJob 作业中的错误记录数
     * @return 格式化的进度信息字符串
     */
    public String calculateSubTask(long processedRecordsInJob, long numErrorsInJob) {

        // 首次调用时记录警告日志（如果已完成作业的统计数据不为0）
        if (!isCalculateSubTaskInvoked) {
            isCalculateSubTaskInvoked = true;
            if (numSuccessesAcrossCompletedJobs != 0) {
                logger.warn("首次调用 calculateSubTask() 时，已完成作业的成功记录数不为0: " + numSuccessesAcrossCompletedJobs);
            }
            if (numErrorsAcrossCompletedJobs != 0) {
                logger.warn("首次调用 calculateSubTask() 时，已完成作业的错误记录数不为0: " + numErrorsAcrossCompletedJobs);
            }
        }

        // 获取当前时间作为加载时间点
        final Date currentLoadTime = new Date();

        // 计算总的已处理记录数（包括当前作业和已完成作业的成功及错误记录）
        final long totalProcessedRecords = processedRecordsInJob + this.numErrorsAcrossCompletedJobs + this.numSuccessesAcrossCompletedJobs;

        // 计算总的错误记录数
        final long totalErrors =  this.numErrorsAcrossCompletedJobs + numErrorsInJob;

        // 计算总的成功记录数
        final long totalSuccesses = totalProcessedRecords - totalErrors;

        // 每小时处理记录数（处理速率）
        long hourlyProcessingRate;

        // 计算总经过时间（秒）
        final long totalElapsedTimeInSec = (currentLoadTime.getTime() - this.startTime.getTime())/1000;

        // 计算经过的分钟数
        final long elapsedTimeInMinutes = totalElapsedTimeInSec / 60;

        // 如果总经过时间为0，则处理速率为0，否则计算每小时处理记录数
        if (totalElapsedTimeInSec == 0) {
            hourlyProcessingRate = 0;
        } else {
            hourlyProcessingRate = (totalProcessedRecords * 60 * 60) / totalElapsedTimeInSec;
        }

        // 剩余时间和预估总时间（秒）
        long remainingTimeInSec = 0;
        long estimatedTotalTimeInSec = 0;

        // 只有当总记录数大于0且已处理记录数大于0时，才能估算剩余时间
        if (this.totalRecordsAcrossAllJobs > 0 && totalProcessedRecords > 0) {
            // 预估总时间 = 总经过时间 * 总记录数 / 已处理记录数
            estimatedTotalTimeInSec = (long) (totalElapsedTimeInSec * this.totalRecordsAcrossAllJobs / totalProcessedRecords);
            // 剩余时间 = 预估总时间 - 总经过时间
            remainingTimeInSec = estimatedTotalTimeInSec - totalElapsedTimeInSec;
        }

        // 如果已处理记录数超过总记录数，则更新总记录数
        if (totalProcessedRecords > this.totalRecordsAcrossAllJobs) {
            this.totalRecordsAcrossAllJobs = totalProcessedRecords;
        }

        // 如果剩余时间小于0，记录警告日志
        if (remainingTimeInSec < 0) {
            logger.warn("剩余时间计算错误。");
        }

        // 计算剩余时间的分钟数和秒数
        final long remainingTimeInMinutes = remainingTimeInSec / 60;
        final long remainingSeconds = remainingTimeInSec - remainingTimeInMinutes * 60;

        // 如果剩余时间小于0或处理速率小于等于0或剩余时间超过7天，则返回未知时间的进度信息
        if (remainingTimeInMinutes < 0 || hourlyProcessingRate <= 0 || (remainingTimeInMinutes > 7 * 24 * 60)) { // 处理时间未计算或不精确
            // LoadRateCalculator.processedTimeUnknown=已处理 {0} 条记录，总共 {1} 条记录。
            // 其中有 {2} 条成功记录和 {3} 条错误记录。
            return Messages.getMessage(getClass(), "processedTimeUnknown",
                    totalProcessedRecords, // {0} 已处理记录数
                    this.totalRecordsAcrossAllJobs,  // {1} 总记录数
                    totalSuccesses,       // {2} 成功记录数
                    totalErrors);       // {3} 错误记录数
        }

        // 返回包含详细进度信息的消息
        return Messages.getMessage(getClass(), "processed",
                totalProcessedRecords, // {0} 已处理记录数
                this.totalRecordsAcrossAllJobs,  // {1} 总记录数
                hourlyProcessingRate,    // {2} 每小时处理记录数
                remainingTimeInMinutes,  // {3} 剩余分钟数
                remainingSeconds,        // {4} 剩余秒数
                totalSuccesses,       // {5} 成功记录数
                totalErrors,       // {6} 错误记录数
                totalElapsedTimeInSec - (60 * elapsedTimeInMinutes), // {7} 经过的秒数（不包括分钟部分）
                elapsedTimeInMinutes // {8} 经过的分钟数
            );
    }

    /**
     * 设置已完成作业中的成功记录数
     *
     * @param num 成功记录数
     */
    public void setNumSuccessesAcrossCompletedJobs(long num) {
        this.numSuccessesAcrossCompletedJobs = num;
    }

    /**
     * 设置已完成作业中的错误记录数
     *
     * @param num 错误记录数
     */
    public void setNumErrorsAcrossCompletedJobs(long num) {
        this.numErrorsAcrossCompletedJobs = num;
    }
}
