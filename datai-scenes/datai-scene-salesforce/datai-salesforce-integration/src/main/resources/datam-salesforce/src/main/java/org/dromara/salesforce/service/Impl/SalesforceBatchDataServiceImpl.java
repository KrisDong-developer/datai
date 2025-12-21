package org.dromara.salesforce.service.Impl;

import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.model.ExecuteResult;
import com.aizuda.snailjob.common.log.SnailJobLog;
import com.alibaba.fastjson.JSON;
import com.sforce.soap.partner.PartnerConnection;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.salesforce.config.SalesforceSingletonConfig;
import org.dromara.salesforce.constant.SalesforceConstant;
import org.dromara.salesforce.domain.bo.MigrationBatchBo;
import org.dromara.salesforce.domain.bo.MigrationObjectBo;
import org.dromara.salesforce.domain.param.SalesforceParam;
import org.dromara.salesforce.domain.vo.MigrationObjectVo;
import org.dromara.salesforce.factory.SOAPConnectionFactory;
import org.dromara.salesforce.service.*;
import org.dromara.salesforce.util.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Salesforce批次数据服务实现类
 * <p>
 * 该服务用于根据对象信息创建数据同步批次，支持按年、月、周、日等不同时间维度创建批次，
 * 并根据数据量自动拆分过大的批次以提高同步效率和系统稳定性。
 * </p>
 *
 * @author lingma
 * @since 1.0.0
 */
@Slf4j
@Service
public class SalesforceBatchDataServiceImpl implements SalesforceBatchDataService {

    @Autowired
    private IMigrationBatchService migrationBatchService;

    @Autowired
    private SobjectSyncService sobjectSyncService;

    @Autowired
    private IMigrationObjectService migrationObjectService;

    @Autowired
    private IMigrationFieldService migrationFieldService;

    /**
     * 创建批次数据
     * <p>
     * 根据对象信息创建数据同步批次，如果对象有最后同步时间，则从最后同步时间开始创建批次，
     * 否则从配置的源组织开始时间创建批次。支持按任务参数指定特定对象或处理所有对象。
     * </p>
     *
     * @param jobArgs 任务参数，可包含特定API列表
     * @return ExecuteResult 执行结果，包含成功或失败信息
     */
    @Override
    public ExecuteResult createBatchData(JobArgs jobArgs) {
        SnailJobLog.LOCAL.info("开始执行Salesforce批次数据创建任务");

        String value = String.valueOf(jobArgs.getJobParams());

        if (StringUtils.isBlank(value)) {
            SnailJobLog.LOCAL.error("任务参数为空，无法创建批次数据");
            return ExecuteResult.failure("任务参数为空，无法创建批次数据");
        }

        try {
            SalesforceParam param = JSON.parseObject(value, SalesforceParam.class);
            Set<String> apiList = extractApiList(param);

            if (!apiList.isEmpty()) {
                for (String api : apiList) {
                    // 创建批次数据
                    createBatch(api);
                }
            }

            return ExecuteResult.success("Salesforce批次数据创建成功");
        } catch (Exception e) {
            SnailJobLog.LOCAL.error("Salesforce批次数据创建任务执行失败", e);
            return ExecuteResult.failure("Salesforce批次数据创建失败: " + e.getMessage());
        }
    }

    /**
     * 提取API列表
     * <p>
     * 根据任务参数提取需要处理的API列表，如果参数中指定了API则使用指定的，
     * 否则获取所有可用对象的API列表。
     * </p>
     *
     * @param param Salesforce参数
     * @return API列表
     */
    private Set<String> extractApiList(SalesforceParam param) {
        if (param != null && StringUtils.isNotBlank(param.getApi())) {
            // 按逗号拆分API列表
            return DataUtil.toApiSet(param.getApi());
        } else {
            return migrationObjectService.listAllObjects().stream()
                    .map(MigrationObjectVo::getApi)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toSet());
        }
    }

    /**
     * 为指定API创建批次
     * <p>
     * 根据对象的最后同步时间确定批次起始时间，创建时间范围内的批次数据，
     * 并更新对象的最后同步时间。
     * </p>
     *
     * @param api API名称
     */
    private void createBatch(String api) {
        try {
            MigrationObjectVo objectVo = migrationObjectService.queryByApiName(api);

            if (objectVo == null) {
                log.warn("未找到API为 {} 的对象信息", api);
                return;
            }

            SalesforceSingletonConfig instance = SalesforceSingletonConfig.getInstance();

            String batchType = null;
            Date startDate = null;
            String dateField = null;
            if (objectVo.getLastSyncDate() != null) {
                batchType = SalesforceConstant.BATCH_TYPE_INCREMENTAL;
                startDate = objectVo.getLastSyncDate();
                dateField = migrationFieldService.getUpdateField(api);
            } else {
                batchType = SalesforceConstant.BATCH_TYPE_STOCK;
                startDate = java.sql.Date.valueOf(instance.getSourceOrgStartDate());
                dateField = migrationFieldService.getDateField(api);

            }

            String batchdateType = instance.getBatchType();

            Date endDate = new Date(); // 当前时间

            // 根据batchType创建不同的时间区间
            List<Date[]> dateRanges = createDateRanges(batchdateType, startDate, endDate);

            // 创建批次数据并保存到数据库
            List<MigrationBatchBo> batchList = new ArrayList<>();

            for (Date[] range : dateRanges) {
                // 检查当前批次数据量
                List<MigrationBatchBo> batches = checkAndSplitBatchIfNeeded(objectVo, range[0], range[1], batchdateType,batchType,dateField);
                batchList.addAll(batches);
            }

            // 批量插入批次数据
            if (!batchList.isEmpty()) {
                migrationBatchService.insertBatch(batchList);
                log.info("为对象 {} 创建了 {} 个批次", api, batchList.size());
            } else {
                log.info("对象 {} 无需创建批次", api);
            }
        } catch (Exception e) {
            log.error("为对象 {} 创建批次时出错: {}", api, e.getMessage(), e);
            return; // 出错时直接返回，不更新最后同步时间
        }

        // 更新当前对象的最后同步时间
        updateLastSyncDate(api);
    }


    /**
     * 更新对象最后同步时间
     * <p>
     * 将指定API对象的最后同步时间更新为当前时间。
     * </p>
     *
     * @param api API名称
     */
    private void updateLastSyncDate(String api) {
        try {
            MigrationObjectBo objectBo = new MigrationObjectBo();
            objectBo.setApi(api);
            objectBo.setLastSyncDate(new Date());
            migrationObjectService.updateByApiName(objectBo);
        } catch (Exception e) {
            log.error("更新对象 {} 的最后同步时间失败: {}", api, e.getMessage(), e);
        }
    }

    /**
     * 根据批次类型创建日期范围列表
     * <p>
     * 根据指定的批次类型（年、月、周、日）创建从开始日期到结束日期的时间区间列表。
     * </p>
     *
     * @param batchType 批次类型（年、月、周、日）
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 日期范围列表
     */
    private List<Date[]> createDateRanges(String batchType, Date startDate, Date endDate) {
        List<Date[]> dateRanges = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        switch (batchType.toUpperCase()) {
            case SalesforceConstant.BATCH_TYPE_YEAR:
                // 按年创建批次
                createDateRangesByField(calendar, endDate, Calendar.YEAR, dateRanges);
                break;

            case SalesforceConstant.BATCH_TYPE_MONTH:
                // 按月创建批次
                createDateRangesByField(calendar, endDate, Calendar.MONTH, dateRanges);
                break;

            case SalesforceConstant.BATCH_TYPE_WEEK:
                // 按周创建批次
                createDateRangesByField(calendar, endDate, Calendar.WEEK_OF_YEAR, dateRanges);
                break;

            case SalesforceConstant.BATCH_TYPE_DAY:
                // 按天创建批次
                createDateRangesByField(calendar, endDate, Calendar.DAY_OF_YEAR, dateRanges);
                break;

            default:
                log.warn("未知的批次类型: {}，使用默认按月创建批次", batchType);
                // 默认按月创建批次
                createDateRangesByField(calendar, endDate, Calendar.MONTH, dateRanges);
                break;
        }

        return dateRanges;
    }

    /**
     * 根据指定日历字段创建日期范围
     * <p>
     * 根据指定的日历字段（年、月、周、日）创建从开始日期到结束日期的时间区间。
     * </p>
     *
     * @param calendar   日历实例
     * @param endDate    结束日期
     * @param field      日历字段
     * @param dateRanges 日期范围列表
     */
    private void createDateRangesByField(Calendar calendar, Date endDate, int field, List<Date[]> dateRanges) {
        while (calendar.getTime().before(endDate)) {
            Date rangeStart = calendar.getTime();
            calendar.add(field, 1);
            Date rangeEnd = calendar.getTime();
            dateRanges.add(new Date[]{rangeStart, rangeEnd});
        }
    }

    /**
     * 检查批次数据量，如果超过阈值则进一步拆分
     * <p>
     * 查询指定时间范围内的数据量，如果超过阈值（50万）则使用更小的时间单位进行拆分，
     * 直到数据量在合理范围内或已拆分到最小单位（天）。
     * </p>
     * @param objectVo  对象信息
     * @param startDate 批次开始时间
     * @param endDate   批次结束时间
     * @param batchType 当前批次类型
     * @return 批次列表
     */
    private List<MigrationBatchBo> checkAndSplitBatchIfNeeded(MigrationObjectVo objectVo, Date startDate, Date endDate, String batchdateType,String batchType,String dateField) {
        List<MigrationBatchBo> batches = new ArrayList<>();

        try {
            // 获取源ORG连接
            PartnerConnection connection = SOAPConnectionFactory.sourceInstance();

            // 查询当前批次数据量
            SalesforceParam param = new SalesforceParam();
            param.setApi(objectVo.getApi());
            param.setBeginDate(startDate);
            param.setEndDate(endDate);
            param.setDateField(migrationFieldService.getDateField(objectVo.getApi()));

            int count = sobjectSyncService.countSfNumByDateField(connection, param);

            log.info("对象 {} 在 {} 到 {} 范围内数据量为: {}", objectVo.getApi(), startDate, endDate, count);

            // 如果数据量大于阈值，则进一步拆分
            if (count > SalesforceConstant.BATCH_SIZE_THRESHOLD) {
                log.info("对象 {} 批次数据量超过{}，进行进一步拆分", objectVo.getApi(), SalesforceConstant.BATCH_SIZE_THRESHOLD);
                // 根据当前批次类型选择更小的时间单位进行拆分
                String nextBatchType = getNextSmallerBatchType(batchdateType);
                List<Date[]> subRanges = createDateRanges(nextBatchType, startDate, endDate);

                for (Date[] subRange : subRanges) {
                    // 递归检查拆分后的子批次
                    List<MigrationBatchBo> subBatches = checkAndSplitBatchIfNeeded(objectVo, subRange[0], subRange[1],batchdateType, nextBatchType,dateField);
                    batches.addAll(subBatches);
                }
            } else {
                // 数据量未超过阈值，创建批次
                MigrationBatchBo batchBo = new MigrationBatchBo();
                batchBo.setApi(objectVo.getApi());
                batchBo.setLabel(objectVo.getLabel());
                batchBo.setBatchField(dateField);
                batchBo.setBatchField(objectVo.getBatchField());
                batchBo.setSyncType(batchType);
                batchBo.setSyncStartDate(startDate);
                batchBo.setSyncEndDate(endDate);
                batchBo.setSfNum(count);
                batches.add(batchBo);
            }
        } catch (Exception e) {
            log.error("检查对象 {} 批次数据量时出错: {}", objectVo.getApi(), e.getMessage(), e);
            // 出错时仍然创建批次，避免丢失数据
            MigrationBatchBo batchBo = new MigrationBatchBo();
            batchBo.setApi(objectVo.getApi());
            batchBo.setLabel(objectVo.getLabel());
            batchBo.setBatchField(dateField);
            batchBo.setSyncType(batchType);
            batchBo.setSyncStartDate(startDate);
            batchBo.setSyncEndDate(endDate);
            batchBo.setSfNum(0);
            batches.add(batchBo);
        }

        return batches;
    }

    /**
     * 获取更小的批次类型
     * <p>
     * 根据当前批次类型返回更小一级的批次类型，用于数据量过大时的批次拆分。
     * </p>
     *
     * @param currentBatchType 当前批次类型
     * @return 更小的批次类型
     */
    private String getNextSmallerBatchType(String currentBatchType) {
        switch (currentBatchType.toUpperCase()) {
            case SalesforceConstant.BATCH_TYPE_YEAR:
                return SalesforceConstant.BATCH_TYPE_MONTH;
            case SalesforceConstant.BATCH_TYPE_MONTH:
                return SalesforceConstant.BATCH_TYPE_WEEK;
            case SalesforceConstant.BATCH_TYPE_WEEK:
                return SalesforceConstant.BATCH_TYPE_DAY;
            case SalesforceConstant.BATCH_TYPE_DAY:
            default:
                // 已经是最小单位，无法再拆分
                return SalesforceConstant.BATCH_TYPE_DAY;
        }
    }

}
