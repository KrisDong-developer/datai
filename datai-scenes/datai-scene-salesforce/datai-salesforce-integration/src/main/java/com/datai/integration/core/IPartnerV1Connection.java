package com.datai.integration.core;

import com.sforce.soap.partner.*;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

import java.util.Calendar;

/**
 * Partner API V1 连接接口
 * 提供 Salesforce Partner API 的核心功能，包括 CRUD 操作、查询和元数据描述
 */
public interface IPartnerV1Connection {

    DescribeGlobalResult describeGlobal() throws ConnectionException;

    GetUserInfoResult getUserInfo() throws ConnectionException;

    DescribeSObjectResult describeSObject(String sObjectType) throws ConnectionException;

    DescribeSObjectResult[] describeSObjects(String[] sObjectType) throws ConnectionException;

    QueryResult query(String queryString) throws ConnectionException;

    QueryResult queryAll(String queryString) throws ConnectionException;

    QueryResult queryMore(String queryLocator) throws ConnectionException;

    SaveResult[] create(SObject[] sObjects) throws ConnectionException;

    SaveResult[] update(SObject[] sObjects) throws ConnectionException;

    DeleteResult[] delete(String[] ids) throws ConnectionException;

    UndeleteResult[] undelete(String[] ids) throws ConnectionException;

    UpsertResult[] upsert(String externalIDFieldName, SObject[] sObjects) throws ConnectionException;

    SObject[] retrieve(String fieldList, String sObjectType, String[] ids) throws ConnectionException;

    SearchResult search(String searchString) throws ConnectionException;

    GetDeletedResult getDeleted(String sObjectType, Calendar startDate, Calendar endDate) throws ConnectionException;

    GetUpdatedResult getUpdated(String sObjectType, Calendar startDate, Calendar endDate) throws ConnectionException;

    ProcessResult[] process(ProcessRequest[] actions) throws ConnectionException;

    MergeResult[] merge(MergeRequest[] request) throws ConnectionException;

    LeadConvertResult[] convertLead(LeadConvert[] leadConverts) throws ConnectionException;

    EmptyRecycleBinResult[] emptyRecycleBin(String[] ids) throws ConnectionException;

    DescribeTab[] describeAllTabs() throws ConnectionException;

    DescribeTabSetResult[] describeTabs() throws ConnectionException;

    DescribeDataCategoryGroupResult[] describeDataCategoryGroups(String[] sObjectType) throws ConnectionException;

    DescribeDataCategoryGroupStructureResult[] describeDataCategoryGroupStructures(DataCategoryGroupSobjectTypePair[] pairs, boolean topCategoriesOnly) throws ConnectionException;

    DescribeDataCategoryMappingResult[] describeDataCategoryMappings() throws ConnectionException;

    FindDuplicatesResult[] findDuplicates(SObject[] sObjects) throws ConnectionException;

    FindDuplicatesResult[] findDuplicatesByIds(String[] ids) throws ConnectionException;

    DescribeLayoutResult describeLayout(String sObjectType, String layoutName, String[] recordTypeIds) throws ConnectionException;

    DescribeSearchLayoutResult[] describeSearchLayouts(String[] sObjectType) throws ConnectionException;

    DescribeSearchScopeOrderResult[] describeSearchScopeOrder(boolean includeRealTimeEntities) throws ConnectionException;

    DescribeSearchableEntityResult[] describeSearchableEntities(boolean includeOnlyEntitiesWithTabs) throws ConnectionException;

    DescribeQuickActionResult[] describeQuickActions(String[] quickActions) throws ConnectionException;

    DescribeQuickActionResult[] describeQuickActionsForRecordType(String[] quickActions, String recordTypeId) throws ConnectionException;

    DescribeAvailableQuickActionResult[] describeAvailableQuickActions(String contextType) throws ConnectionException;

    PerformQuickActionResult[] performQuickActions(PerformQuickActionRequest[] quickActions) throws ConnectionException;

    DescribeSoqlListViewResult describeSObjectListViews(String sObjectType, boolean recentsOnly, ListViewIsSoqlCompatible isSoqlCompatible, int limit, int offset) throws ConnectionException;

    DescribeSoqlListViewResult describeSoqlListViews(DescribeSoqlListViewsRequest request) throws ConnectionException;

    ExecuteListViewResult executeListView(ExecuteListViewRequest request) throws ConnectionException;

    SendEmailResult[] sendEmail(Email[] messages) throws ConnectionException;

    SendEmailResult[] sendEmailMessage(String[] ids) throws ConnectionException;

    RenderEmailTemplateResult[] renderEmailTemplate(RenderEmailTemplateRequest[] renderRequests) throws ConnectionException;

    RenderStoredEmailTemplateResult renderStoredEmailTemplate(RenderStoredEmailTemplateRequest request) throws ConnectionException;

    QuickActionTemplateResult[] retrieveQuickActionTemplates(String[] quickActionNames, String contextId) throws ConnectionException;

    QuickActionTemplateResult[] retrieveMassQuickActionTemplates(String quickActionName, String[] contextIds) throws ConnectionException;

    DescribePathAssistantsResult describePathAssistants(String sObjectType, String picklistValue, String[] recordTypeIds) throws ConnectionException;

    DescribeCompactLayoutsResult describeCompactLayouts(String sObjectType, String[] recordTypeIds) throws ConnectionException;

    DescribeCompactLayout[] describePrimaryCompactLayouts(String[] sObjectTypes) throws ConnectionException;

    DescribeGlobalTheme describeGlobalTheme() throws ConnectionException;

    DescribeThemeResult describeTheme(String[] sobjectType) throws ConnectionException;

    DescribeAppMenuResult describeAppMenu(AppMenuType appMenuType, String networkId) throws ConnectionException;

    DescribeApprovalLayoutResult describeApprovalLayout(String sObjectType, String[] approvalProcessNames) throws ConnectionException;

    DescribeSoftphoneLayoutResult describeSoftphoneLayout() throws ConnectionException;

    DescribeVisualForceResult describeVisualForce(boolean includeAllDetails, String namespacePrefix) throws ConnectionException;

    DescribeNounResult[] describeNouns(String[] nouns, boolean onlyRenamed, boolean includeFields) throws ConnectionException;

    GetServerTimestampResult getServerTimestamp() throws ConnectionException;

    LoginResult login(String username, String password) throws ConnectionException;

    SetPasswordResult setPassword(String userId, String password) throws ConnectionException;

    ResetPasswordResult resetPassword(String userId) throws ConnectionException;

    ChangeOwnPasswordResult changeOwnPassword(String oldPassword, String newPassword) throws ConnectionException;

    InvalidateSessionsResult[] invalidateSessions(String[] sessionIds) throws ConnectionException;

    KnowledgeSettings describeKnowledgeSettings() throws ConnectionException;

    DeleteByExampleResult[] deleteByExample(SObject[] sObjects) throws ConnectionException;
}
