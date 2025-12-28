package com.datai.integration.core;

import com.sforce.soap.partner.*;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectorConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PartnerV1Connection extends PartnerConnection {

    public PartnerV1Connection(ConnectorConfig config) throws com.sforce.ws.ConnectionException {
        super(config);
    }

    @Override
    public DescribeTab[] describeAllTabs() throws com.sforce.ws.ConnectionException {
        log.debug("执行describeAllTabs操作");
        return super.describeAllTabs();
    }

    @Override
    public DescribeDataCategoryGroupStructureResult[] describeDataCategoryGroupStructures(DataCategoryGroupSobjectTypePair[] pairs, boolean topCategoriesOnly) throws com.sforce.ws.ConnectionException {
        log.debug("执行describeDataCategoryGroupStructures操作，对数: {}, 仅顶级分类: {}", pairs != null ? pairs.length : 0, topCategoriesOnly);
        return super.describeDataCategoryGroupStructures(pairs, topCategoriesOnly);
    }

    @Override
    public DescribeDataCategoryGroupResult[] describeDataCategoryGroups(String[] sObjectType) throws com.sforce.ws.ConnectionException {
        log.debug("执行describeDataCategoryGroups操作，对象类型数: {}", sObjectType != null ? sObjectType.length : 0);
        return super.describeDataCategoryGroups(sObjectType);
    }

    @Override
    public FindDuplicatesResult[] findDuplicates(SObject[] sObjects) throws com.sforce.ws.ConnectionException {
        log.debug("执行findDuplicates操作，对象数: {}", sObjects != null ? sObjects.length : 0);
        return super.findDuplicates(sObjects);
    }

    @Override
    public ProcessResult[] process(ProcessRequest[] actions) throws com.sforce.ws.ConnectionException {
        log.debug("执行process操作，操作数: {}", actions != null ? actions.length : 0);
        return super.process(actions);
    }

    @Override
    public DescribeGlobalResult describeGlobal() throws com.sforce.ws.ConnectionException {
        log.debug("执行describeGlobal操作");
        return super.describeGlobal();
    }

    @Override
    public GetUserInfoResult getUserInfo() throws com.sforce.ws.ConnectionException {
        log.debug("执行getUserInfo操作");
        return super.getUserInfo();
    }

    @Override
    public DescribeGlobalTheme describeGlobalTheme() throws com.sforce.ws.ConnectionException {
        log.debug("执行describeGlobalTheme操作");
        return super.describeGlobalTheme();
    }

    @Override
    public DescribeApprovalLayoutResult describeApprovalLayout(String sObjectType, String[] approvalProcessNames) throws com.sforce.ws.ConnectionException {
        log.debug("执行describeApprovalLayout操作，对象类型: {}, 审批流程数: {}", sObjectType, approvalProcessNames != null ? approvalProcessNames.length : 0);
        return super.describeApprovalLayout(sObjectType, approvalProcessNames);
    }

    @Override
    public DescribeCompactLayout[] describePrimaryCompactLayouts(String[] sObjectTypes) throws com.sforce.ws.ConnectionException {
        log.debug("执行describePrimaryCompactLayouts操作，对象类型数: {}", sObjectTypes != null ? sObjectTypes.length : 0);
        return super.describePrimaryCompactLayouts(sObjectTypes);
    }

    @Override
    public QueryResult queryMore(String queryLocator) throws com.sforce.ws.ConnectionException {
        log.debug("执行queryMore操作，查询定位符: {}", queryLocator);
        return super.queryMore(queryLocator);
    }

    @Override
    public DescribeSearchableEntityResult[] describeSearchableEntities(boolean includeOnlyEntitiesWithTabs) throws com.sforce.ws.ConnectionException {
        log.debug("执行describeSearchableEntities操作，仅包含有标签的实体: {}", includeOnlyEntitiesWithTabs);
        return super.describeSearchableEntities(includeOnlyEntitiesWithTabs);
    }

    @Override
    public DescribeLayoutResult describeLayout(String sObjectType, String layoutName, String[] recordTypeIds) throws com.sforce.ws.ConnectionException {
        log.debug("执行describeLayout操作，对象类型: {}, 布局名称: {}, 记录类型数: {}", sObjectType, layoutName, recordTypeIds != null ? recordTypeIds.length : 0);
        return super.describeLayout(sObjectType, layoutName, recordTypeIds);
    }

    @Override
    public DescribeAppMenuResult describeAppMenu(AppMenuType appMenuType, String networkId) throws com.sforce.ws.ConnectionException {
        log.debug("执行describeAppMenu操作，应用菜单类型: {}, 网络ID: {}", appMenuType, networkId);
        return super.describeAppMenu(appMenuType, networkId);
    }

    @Override
    public LeadConvertResult[] convertLead(LeadConvert[] leadConverts) throws com.sforce.ws.ConnectionException {
        log.debug("执行convertLead操作，线索转换数: {}", leadConverts != null ? leadConverts.length : 0);
        return super.convertLead(leadConverts);
    }

    @Override
    public DescribeSoqlListViewResult describeSObjectListViews(String sObjectType, boolean recentsOnly, ListViewIsSoqlCompatible isSoqlCompatible, int limit, int offset) throws com.sforce.ws.ConnectionException {
        log.debug("执行describeSObjectListViews操作，对象类型: {}, 仅最近: {}, SOQL兼容: {}, 限制: {}, 偏移: {}", sObjectType, recentsOnly, isSoqlCompatible, limit, offset);
        return super.describeSObjectListViews(sObjectType, recentsOnly, isSoqlCompatible, limit, offset);
    }

    @Override
    public DeleteResult[] delete(String[] ids) throws com.sforce.ws.ConnectionException {
        log.debug("执行delete操作，记录数: {}", ids != null ? ids.length : 0);
        return super.delete(ids);
    }

    @Override
    public LoginResult login(String username, String password) throws com.sforce.ws.ConnectionException {
        log.debug("执行login操作，用户名: {}", username);
        return super.login(username, password);
    }

    @Override
    public QueryResult queryAll(String queryString) throws com.sforce.ws.ConnectionException {
        log.debug("执行queryAll操作，查询语句: {}", queryString);
        return super.queryAll(queryString);
    }

    @Override
    public SaveResult[] update(SObject[] sObjects) throws com.sforce.ws.ConnectionException {
        log.debug("执行update操作，记录数: {}", sObjects != null ? sObjects.length : 0);
        return super.update(sObjects);
    }

    @Override
    public EmptyRecycleBinResult[] emptyRecycleBin(String[] ids) throws com.sforce.ws.ConnectionException {
        log.debug("执行emptyRecycleBin操作，记录数: {}", ids != null ? ids.length : 0);
        return super.emptyRecycleBin(ids);
    }

    @Override
    public DescribeCompactLayoutsResult describeCompactLayouts(String sObjectType, String[] recordTypeIds) throws com.sforce.ws.ConnectionException {
        log.debug("执行describeCompactLayouts操作，对象类型: {}, 记录类型数: {}", sObjectType, recordTypeIds != null ? recordTypeIds.length : 0);
        return super.describeCompactLayouts(sObjectType, recordTypeIds);
    }

    @Override
    public ChangeOwnPasswordResult changeOwnPassword(String oldPassword, String newPassword) throws com.sforce.ws.ConnectionException {
        log.debug("执行changeOwnPassword操作");
        return super.changeOwnPassword(oldPassword, newPassword);
    }

    @Override
    public DescribeSoqlListViewResult describeSoqlListViews(DescribeSoqlListViewsRequest request) throws com.sforce.ws.ConnectionException {
        log.debug("执行describeSoqlListViews操作");
        return super.describeSoqlListViews(request);
    }

    @Override
    public DescribePathAssistantsResult describePathAssistants(String sObjectType, String picklistValue, String[] recordTypeIds) throws com.sforce.ws.ConnectionException {
        log.debug("执行describePathAssistants操作，对象类型: {}, 选择列表值: {}, 记录类型数: {}", sObjectType, picklistValue, recordTypeIds != null ? recordTypeIds.length : 0);
        return super.describePathAssistants(sObjectType, picklistValue, recordTypeIds);
    }

    @Override
    public DescribeAvailableQuickActionResult[] describeAvailableQuickActions(String contextType) throws com.sforce.ws.ConnectionException {
        log.debug("执行describeAvailableQuickActions操作，上下文类型: {}", contextType);
        return super.describeAvailableQuickActions(contextType);
    }

    @Override
    public GetDeletedResult getDeleted(String sObjectType, java.util.Calendar startDate, java.util.Calendar endDate) throws com.sforce.ws.ConnectionException {
        log.debug("执行getDeleted操作，对象类型: {}, 开始时间: {}, 结束时间: {}", sObjectType, startDate, endDate);
        return super.getDeleted(sObjectType, startDate, endDate);
    }

    @Override
    public DescribeTabSetResult[] describeTabs() throws com.sforce.ws.ConnectionException {
        log.debug("执行describeTabs操作");
        return super.describeTabs();
    }

    @Override
    public QuickActionTemplateResult[] retrieveMassQuickActionTemplates(String quickActionName, String[] contextIds) throws com.sforce.ws.ConnectionException {
        log.debug("执行retrieveMassQuickActionTemplates操作，快速操作名称: {}, 上下文ID数: {}", quickActionName, contextIds != null ? contextIds.length : 0);
        return super.retrieveMassQuickActionTemplates(quickActionName, contextIds);
    }

    @Override
    public SearchResult search(String searchString) throws com.sforce.ws.ConnectionException {
        log.debug("执行search操作，搜索字符串: {}", searchString);
        return super.search(searchString);
    }

    @Override
    public SendEmailResult[] sendEmail(Email[] messages) throws com.sforce.ws.ConnectionException {
        log.debug("执行sendEmail操作，邮件数: {}", messages != null ? messages.length : 0);
        return super.sendEmail(messages);
    }

    @Override
    public GetUpdatedResult getUpdated(String sObjectType, java.util.Calendar startDate, java.util.Calendar endDate) throws com.sforce.ws.ConnectionException {
        log.debug("执行getUpdated操作，对象类型: {}, 开始时间: {}, 结束时间: {}", sObjectType, startDate, endDate);
        return super.getUpdated(sObjectType, startDate, endDate);
    }

    @Override
    public SendEmailResult[] sendEmailMessage(String[] ids) throws com.sforce.ws.ConnectionException {
        log.debug("执行sendEmailMessage操作，邮件ID数: {}", ids != null ? ids.length : 0);
        return super.sendEmailMessage(ids);
    }

    @Override
    public DescribeQuickActionResult[] describeQuickActionsForRecordType(String[] quickActions, String recordTypeId) throws com.sforce.ws.ConnectionException {
        log.debug("执行describeQuickActionsForRecordType操作，快速操作数: {}, 记录类型ID: {}", quickActions != null ? quickActions.length : 0, recordTypeId);
        return super.describeQuickActionsForRecordType(quickActions, recordTypeId);
    }

    @Override
    public RenderEmailTemplateResult[] renderEmailTemplate(RenderEmailTemplateRequest[] renderRequests) throws com.sforce.ws.ConnectionException {
        log.debug("执行renderEmailTemplate操作，请求数: {}", renderRequests != null ? renderRequests.length : 0);
        return super.renderEmailTemplate(renderRequests);
    }

    @Override
    public UpsertResult[] upsert(String externalIDFieldName, SObject[] sObjects) throws com.sforce.ws.ConnectionException {
        log.debug("执行upsert操作，外部ID字段: {}, 记录数: {}", externalIDFieldName, sObjects != null ? sObjects.length : 0);
        return super.upsert(externalIDFieldName, sObjects);
    }

    @Override
    public QueryResult query(String queryString) throws com.sforce.ws.ConnectionException {
        log.debug("执行query操作，查询语句: {}", queryString);
        return super.query(queryString);
    }

    @Override
    public DescribeQuickActionResult[] describeQuickActions(String[] quickActions) throws com.sforce.ws.ConnectionException {
        log.debug("执行describeQuickActions操作，快速操作数: {}", quickActions != null ? quickActions.length : 0);
        return super.describeQuickActions(quickActions);
    }

    @Override
    public PerformQuickActionResult[] performQuickActions(PerformQuickActionRequest[] quickActions) throws com.sforce.ws.ConnectionException {
        log.debug("执行performQuickActions操作，快速操作数: {}", quickActions != null ? quickActions.length : 0);
        return super.performQuickActions(quickActions);
    }

    @Override
    public DescribeSObjectResult[] describeSObjects(String[] sObjectType) throws com.sforce.ws.ConnectionException {
        log.debug("执行describeSObjects操作，对象类型数: {}", sObjectType != null ? sObjectType.length : 0);
        return super.describeSObjects(sObjectType);
    }

    @Override
    public KnowledgeSettings describeKnowledgeSettings() throws com.sforce.ws.ConnectionException {
        log.debug("执行describeKnowledgeSettings操作");
        return super.describeKnowledgeSettings();
    }

    @Override
    public UndeleteResult[] undelete(String[] ids) throws com.sforce.ws.ConnectionException {
        log.debug("执行undelete操作，记录数: {}", ids != null ? ids.length : 0);
        return super.undelete(ids);
    }

    @Override
    public DescribeThemeResult describeTheme(String[] sobjectType) throws com.sforce.ws.ConnectionException {
        log.debug("执行describeTheme操作，对象类型数: {}", sobjectType != null ? sobjectType.length : 0);
        return super.describeTheme(sobjectType);
    }

    @Override
    public DeleteByExampleResult[] deleteByExample(SObject[] sObjects) throws com.sforce.ws.ConnectionException {
        log.debug("执行deleteByExample操作，对象数: {}", sObjects != null ? sObjects.length : 0);
        return super.deleteByExample(sObjects);
    }

    @Override
    public DescribeNounResult[] describeNouns(String[] nouns, boolean onlyRenamed, boolean includeFields) throws com.sforce.ws.ConnectionException {
        log.debug("执行describeNouns操作，名词数: {}, 仅重命名: {}, 包含字段: {}", nouns != null ? nouns.length : 0, onlyRenamed, includeFields);
        return super.describeNouns(nouns, onlyRenamed, includeFields);
    }

    @Override
    public FindDuplicatesResult[] findDuplicatesByIds(String[] ids) throws com.sforce.ws.ConnectionException {
        log.debug("执行findDuplicatesByIds操作，ID数: {}", ids != null ? ids.length : 0);
        return super.findDuplicatesByIds(ids);
    }

    @Override
    public ExecuteListViewResult executeListView(ExecuteListViewRequest request) throws com.sforce.ws.ConnectionException {
        log.debug("执行executeListView操作");
        return super.executeListView(request);
    }

    @Override
    public RenderStoredEmailTemplateResult renderStoredEmailTemplate(RenderStoredEmailTemplateRequest request) throws com.sforce.ws.ConnectionException {
        log.debug("执行renderStoredEmailTemplate操作");
        return super.renderStoredEmailTemplate(request);
    }

    @Override
    public DescribeVisualForceResult describeVisualForce(boolean includeAllDetails, String namespacePrefix) throws com.sforce.ws.ConnectionException {
        log.debug("执行describeVisualForce操作，包含所有详情: {}, 命名空间前缀: {}", includeAllDetails, namespacePrefix);
        return super.describeVisualForce(includeAllDetails, namespacePrefix);
    }

    @Override
    public DescribeSObjectResult describeSObject(String sObjectType) throws com.sforce.ws.ConnectionException {
        log.debug("执行describeSObject操作，对象类型: {}", sObjectType);
        return super.describeSObject(sObjectType);
    }

    @Override
    public GetServerTimestampResult getServerTimestamp() throws com.sforce.ws.ConnectionException {
        log.debug("执行getServerTimestamp操作");
        return super.getServerTimestamp();
    }

    @Override
    public QuickActionTemplateResult[] retrieveQuickActionTemplates(String[] quickActionNames, String contextId) throws com.sforce.ws.ConnectionException {
        log.debug("执行retrieveQuickActionTemplates操作，快速操作名称数: {}, 上下文ID: {}", quickActionNames != null ? quickActionNames.length : 0, contextId);
        return super.retrieveQuickActionTemplates(quickActionNames, contextId);
    }

    @Override
    public SetPasswordResult setPassword(String userId, String password) throws com.sforce.ws.ConnectionException {
        log.debug("执行setPassword操作，用户ID: {}", userId);
        return super.setPassword(userId, password);
    }

    @Override
    public ResetPasswordResult resetPassword(String userId) throws com.sforce.ws.ConnectionException {
        log.debug("执行resetPassword操作，用户ID: {}", userId);
        return super.resetPassword(userId);
    }

    @Override
    public DescribeSoftphoneLayoutResult describeSoftphoneLayout() throws com.sforce.ws.ConnectionException {
        log.debug("执行describeSoftphoneLayout操作");
        return super.describeSoftphoneLayout();
    }

    @Override
    public SaveResult[] create(SObject[] sObjects) throws com.sforce.ws.ConnectionException {
        log.debug("执行create操作，记录数: {}", sObjects != null ? sObjects.length : 0);
        return super.create(sObjects);
    }

    @Override
    public DescribeSearchLayoutResult[] describeSearchLayouts(String[] sObjectType) throws com.sforce.ws.ConnectionException {
        log.debug("执行describeSearchLayouts操作，对象类型数: {}", sObjectType != null ? sObjectType.length : 0);
        return super.describeSearchLayouts(sObjectType);
    }

    @Override
    public MergeResult[] merge(MergeRequest[] request) throws com.sforce.ws.ConnectionException {
        log.debug("执行merge操作，请求数: {}", request != null ? request.length : 0);
        return super.merge(request);
    }

    @Override
    public InvalidateSessionsResult[] invalidateSessions(String[] sessionIds) throws com.sforce.ws.ConnectionException {
        log.debug("执行invalidateSessions操作，会话ID数: {}", sessionIds != null ? sessionIds.length : 0);
        return super.invalidateSessions(sessionIds);
    }

    @Override
    public DescribeDataCategoryMappingResult[] describeDataCategoryMappings() throws com.sforce.ws.ConnectionException {
        log.debug("执行describeDataCategoryMappings操作");
        return super.describeDataCategoryMappings();
    }

    @Override
    public DescribeSearchScopeOrderResult[] describeSearchScopeOrder(boolean includeRealTimeEntities) throws com.sforce.ws.ConnectionException {
        log.debug("执行describeSearchScopeOrder操作，包含实时实体: {}", includeRealTimeEntities);
        return super.describeSearchScopeOrder(includeRealTimeEntities);
    }

    @Override
    public SObject[] retrieve(String fieldList, String sObjectType, String[] ids) throws com.sforce.ws.ConnectionException {
        log.debug("执行retrieve操作，字段列表: {}, 对象类型: {}, 记录数: {}", fieldList, sObjectType, ids != null ? ids.length : 0);
        return super.retrieve(fieldList, sObjectType, ids);
    }
}
