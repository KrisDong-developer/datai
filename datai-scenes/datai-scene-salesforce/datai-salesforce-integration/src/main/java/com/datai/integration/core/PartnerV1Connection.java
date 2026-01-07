package com.datai.integration.core;

import com.sforce.soap.partner.*;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectorConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PartnerV1Connection extends PartnerConnection implements IPartnerV1Connection {

    public PartnerV1Connection(ConnectorConfig config) throws com.sforce.ws.ConnectionException {
        super(config);
    }

    @Override
    public DescribeTab[] describeAllTabs() throws com.sforce.ws.ConnectionException {
        return super.describeAllTabs();
    }

    @Override
    public DescribeDataCategoryGroupStructureResult[] describeDataCategoryGroupStructures(DataCategoryGroupSobjectTypePair[] pairs, boolean topCategoriesOnly) throws com.sforce.ws.ConnectionException {
        return super.describeDataCategoryGroupStructures(pairs, topCategoriesOnly);
    }

    @Override
    public DescribeDataCategoryGroupResult[] describeDataCategoryGroups(String[] sObjectType) throws com.sforce.ws.ConnectionException {
        return super.describeDataCategoryGroups(sObjectType);
    }

    @Override
    public FindDuplicatesResult[] findDuplicates(SObject[] sObjects) throws com.sforce.ws.ConnectionException {
        return super.findDuplicates(sObjects);
    }

    @Override
    public ProcessResult[] process(ProcessRequest[] actions) throws com.sforce.ws.ConnectionException {
        return super.process(actions);
    }

    @Override
    public DescribeGlobalResult describeGlobal() throws com.sforce.ws.ConnectionException {
        return super.describeGlobal();
    }

    @Override
    public GetUserInfoResult getUserInfo() throws com.sforce.ws.ConnectionException {
        return super.getUserInfo();
    }

    @Override
    public DescribeGlobalTheme describeGlobalTheme() throws com.sforce.ws.ConnectionException {
        return super.describeGlobalTheme();
    }

    @Override
    public DescribeApprovalLayoutResult describeApprovalLayout(String sObjectType, String[] approvalProcessNames) throws com.sforce.ws.ConnectionException {
        return super.describeApprovalLayout(sObjectType, approvalProcessNames);
    }

    @Override
    public DescribeCompactLayout[] describePrimaryCompactLayouts(String[] sObjectTypes) throws com.sforce.ws.ConnectionException {
        return super.describePrimaryCompactLayouts(sObjectTypes);
    }

    @Override
    public QueryResult queryMore(String queryLocator) throws com.sforce.ws.ConnectionException {
        return super.queryMore(queryLocator);
    }

    @Override
    public DescribeSearchableEntityResult[] describeSearchableEntities(boolean includeOnlyEntitiesWithTabs) throws com.sforce.ws.ConnectionException {
        return super.describeSearchableEntities(includeOnlyEntitiesWithTabs);
    }

    @Override
    public DescribeLayoutResult describeLayout(String sObjectType, String layoutName, String[] recordTypeIds) throws com.sforce.ws.ConnectionException {
        return super.describeLayout(sObjectType, layoutName, recordTypeIds);
    }

    @Override
    public DescribeAppMenuResult describeAppMenu(AppMenuType appMenuType, String networkId) throws com.sforce.ws.ConnectionException {
        return super.describeAppMenu(appMenuType, networkId);
    }

    @Override
    public LeadConvertResult[] convertLead(LeadConvert[] leadConverts) throws com.sforce.ws.ConnectionException {
        return super.convertLead(leadConverts);
    }

    @Override
    public DescribeSoqlListViewResult describeSObjectListViews(String sObjectType, boolean recentsOnly, ListViewIsSoqlCompatible isSoqlCompatible, int limit, int offset) throws com.sforce.ws.ConnectionException {
        return super.describeSObjectListViews(sObjectType, recentsOnly, isSoqlCompatible, limit, offset);
    }

    @Override
    public DeleteResult[] delete(String[] ids) throws com.sforce.ws.ConnectionException {
        return super.delete(ids);
    }

    @Override
    public LoginResult login(String username, String password) throws com.sforce.ws.ConnectionException {
        return super.login(username, password);
    }

    @Override
    public QueryResult queryAll(String queryString) throws com.sforce.ws.ConnectionException {
        return super.queryAll(queryString);
    }

    @Override
    public SaveResult[] update(SObject[] sObjects) throws com.sforce.ws.ConnectionException {
        return super.update(sObjects);
    }

    @Override
    public EmptyRecycleBinResult[] emptyRecycleBin(String[] ids) throws com.sforce.ws.ConnectionException {
        return super.emptyRecycleBin(ids);
    }

    @Override
    public DescribeCompactLayoutsResult describeCompactLayouts(String sObjectType, String[] recordTypeIds) throws com.sforce.ws.ConnectionException {
        return super.describeCompactLayouts(sObjectType, recordTypeIds);
    }

    @Override
    public ChangeOwnPasswordResult changeOwnPassword(String oldPassword, String newPassword) throws com.sforce.ws.ConnectionException {
        return super.changeOwnPassword(oldPassword, newPassword);
    }

    @Override
    public DescribeSoqlListViewResult describeSoqlListViews(DescribeSoqlListViewsRequest request) throws com.sforce.ws.ConnectionException {
        return super.describeSoqlListViews(request);
    }

    @Override
    public DescribePathAssistantsResult describePathAssistants(String sObjectType, String picklistValue, String[] recordTypeIds) throws com.sforce.ws.ConnectionException {
        return super.describePathAssistants(sObjectType, picklistValue, recordTypeIds);
    }

    @Override
    public DescribeAvailableQuickActionResult[] describeAvailableQuickActions(String contextType) throws com.sforce.ws.ConnectionException {
        return super.describeAvailableQuickActions(contextType);
    }

    @Override
    public GetDeletedResult getDeleted(String sObjectType, java.util.Calendar startDate, java.util.Calendar endDate) throws com.sforce.ws.ConnectionException {
        return super.getDeleted(sObjectType, startDate, endDate);
    }

    @Override
    public DescribeTabSetResult[] describeTabs() throws com.sforce.ws.ConnectionException {
        return super.describeTabs();
    }

    @Override
    public QuickActionTemplateResult[] retrieveMassQuickActionTemplates(String quickActionName, String[] contextIds) throws com.sforce.ws.ConnectionException {
        return super.retrieveMassQuickActionTemplates(quickActionName, contextIds);
    }

    @Override
    public SearchResult search(String searchString) throws com.sforce.ws.ConnectionException {
        return super.search(searchString);
    }

    @Override
    public SendEmailResult[] sendEmail(Email[] messages) throws com.sforce.ws.ConnectionException {
        return super.sendEmail(messages);
    }

    @Override
    public GetUpdatedResult getUpdated(String sObjectType, java.util.Calendar startDate, java.util.Calendar endDate) throws com.sforce.ws.ConnectionException {
        return super.getUpdated(sObjectType, startDate, endDate);
    }

    @Override
    public SendEmailResult[] sendEmailMessage(String[] ids) throws com.sforce.ws.ConnectionException {
        return super.sendEmailMessage(ids);
    }

    @Override
    public DescribeQuickActionResult[] describeQuickActionsForRecordType(String[] quickActions, String recordTypeId) throws com.sforce.ws.ConnectionException {
        return super.describeQuickActionsForRecordType(quickActions, recordTypeId);
    }

    @Override
    public RenderEmailTemplateResult[] renderEmailTemplate(RenderEmailTemplateRequest[] renderRequests) throws com.sforce.ws.ConnectionException {
        return super.renderEmailTemplate(renderRequests);
    }

    @Override
    public UpsertResult[] upsert(String externalIDFieldName, SObject[] sObjects) throws com.sforce.ws.ConnectionException {
        return super.upsert(externalIDFieldName, sObjects);
    }

    @Override
    public QueryResult query(String queryString) throws com.sforce.ws.ConnectionException {
        return super.query(queryString);
    }

    @Override
    public DescribeQuickActionResult[] describeQuickActions(String[] quickActions) throws com.sforce.ws.ConnectionException {
        return super.describeQuickActions(quickActions);
    }

    @Override
    public PerformQuickActionResult[] performQuickActions(PerformQuickActionRequest[] quickActions) throws com.sforce.ws.ConnectionException {
        return super.performQuickActions(quickActions);
    }

    @Override
    public DescribeSObjectResult[] describeSObjects(String[] sObjectType) throws com.sforce.ws.ConnectionException {
        return super.describeSObjects(sObjectType);
    }

    @Override
    public KnowledgeSettings describeKnowledgeSettings() throws com.sforce.ws.ConnectionException {
        return super.describeKnowledgeSettings();
    }

    @Override
    public UndeleteResult[] undelete(String[] ids) throws com.sforce.ws.ConnectionException {
        return super.undelete(ids);
    }

    @Override
    public DescribeThemeResult describeTheme(String[] sobjectType) throws com.sforce.ws.ConnectionException {
        return super.describeTheme(sobjectType);
    }

    @Override
    public DeleteByExampleResult[] deleteByExample(SObject[] sObjects) throws com.sforce.ws.ConnectionException {
        return super.deleteByExample(sObjects);
    }

    @Override
    public DescribeNounResult[] describeNouns(String[] nouns, boolean onlyRenamed, boolean includeFields) throws com.sforce.ws.ConnectionException {
        return super.describeNouns(nouns, onlyRenamed, includeFields);
    }

    @Override
    public FindDuplicatesResult[] findDuplicatesByIds(String[] ids) throws com.sforce.ws.ConnectionException {
        return super.findDuplicatesByIds(ids);
    }

    @Override
    public ExecuteListViewResult executeListView(ExecuteListViewRequest request) throws com.sforce.ws.ConnectionException {
        return super.executeListView(request);
    }

    @Override
    public RenderStoredEmailTemplateResult renderStoredEmailTemplate(RenderStoredEmailTemplateRequest request) throws com.sforce.ws.ConnectionException {
        return super.renderStoredEmailTemplate(request);
    }

    @Override
    public DescribeVisualForceResult describeVisualForce(boolean includeAllDetails, String namespacePrefix) throws com.sforce.ws.ConnectionException {
        return super.describeVisualForce(includeAllDetails, namespacePrefix);
    }

    @Override
    public DescribeSObjectResult describeSObject(String sObjectType) throws com.sforce.ws.ConnectionException {
        return super.describeSObject(sObjectType);
    }

    @Override
    public GetServerTimestampResult getServerTimestamp() throws com.sforce.ws.ConnectionException {
        return super.getServerTimestamp();
    }

    @Override
    public QuickActionTemplateResult[] retrieveQuickActionTemplates(String[] quickActionNames, String contextId) throws com.sforce.ws.ConnectionException {
        return super.retrieveQuickActionTemplates(quickActionNames, contextId);
    }

    @Override
    public SetPasswordResult setPassword(String userId, String password) throws com.sforce.ws.ConnectionException {
        return super.setPassword(userId, password);
    }

    @Override
    public ResetPasswordResult resetPassword(String userId) throws com.sforce.ws.ConnectionException {
        return super.resetPassword(userId);
    }

    @Override
    public DescribeSoftphoneLayoutResult describeSoftphoneLayout() throws com.sforce.ws.ConnectionException {
        return super.describeSoftphoneLayout();
    }

    @Override
    public SaveResult[] create(SObject[] sObjects) throws com.sforce.ws.ConnectionException {
        return super.create(sObjects);
    }

    @Override
    public DescribeSearchLayoutResult[] describeSearchLayouts(String[] sObjectType) throws com.sforce.ws.ConnectionException {
        return super.describeSearchLayouts(sObjectType);
    }

    @Override
    public MergeResult[] merge(MergeRequest[] request) throws com.sforce.ws.ConnectionException {
        return super.merge(request);
    }

    @Override
    public InvalidateSessionsResult[] invalidateSessions(String[] sessionIds) throws com.sforce.ws.ConnectionException {
        return super.invalidateSessions(sessionIds);
    }

    @Override
    public DescribeDataCategoryMappingResult[] describeDataCategoryMappings() throws com.sforce.ws.ConnectionException {
        return super.describeDataCategoryMappings();
    }

    @Override
    public DescribeSearchScopeOrderResult[] describeSearchScopeOrder(boolean includeRealTimeEntities) throws com.sforce.ws.ConnectionException {
        return super.describeSearchScopeOrder(includeRealTimeEntities);
    }

    @Override
    public SObject[] retrieve(String fieldList, String sObjectType, String[] ids) throws com.sforce.ws.ConnectionException {
        return super.retrieve(fieldList, sObjectType, ids);
    }
}
