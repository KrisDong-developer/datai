package org.dromara.salesforce.dyna;

import lombok.extern.slf4j.Slf4j;
import org.dromara.salesforce.exception.RelationshipFormatException;


/**
 * ParentIdLookupFieldFormatter 类用于格式化和解析父级对象的ID查找字段信息。
 *
 * 此类主要用于处理Salesforce中对象间的关系字段，特别是涉及父对象ID查找字段的情况。
 * 它支持多种格式的字段名称，包括传统的格式和新的支持多态查找关系的格式：
 *
 * 传统格式（旧格式）：
 * - <relationship name>:<idLookup field of parent sObject>
 * - 示例："Owner:username"，其中Account.Owner字段是User对象的查找字段，username是User中的idLookup字段
 *
 * 新格式（支持多态查找关系）：
 * - <parent object name>:<relationship name>.<idLookup field of parent sObject>
 * - 示例："Account:Owner.username"
 *
 * 支持的格式类型：
 * 1. 格式1：不包含':'或'-'的字母数字字符串，表示子对象的非多态关系字段名称
 * 2. 格式2：包含':'的字母数字字符串，有两种解释：
 *    - 旧格式：<子关系名称>:<父idLookup字段名称>
 *    - 新格式：<父对象名称>:<关系字段的关系名称属性>
 * 3. 格式3：包含一个':'和一个'-'的字母数字字符串，表示子对象中引用父对象idLookup字段的字段
 *
 * 主要功能包括：
 * 1. 解析各种格式的字段名称字符串
 * 2. 提取父对象字段名称、关系名称和父对象名称
 * 3. 根据给定的参数创建格式化字符串
 * 4. 提供获取父字段名称和父对象格式化器的方法
 */
@Slf4j
public class ParentIdLookupFieldFormatter {
    private String parentFieldName = null;
    private ParentSObjectFormatter parentSObjectFormatter = null;

    private static final String OLD_FORMAT_PARENT_IDLOOKUP_FIELD_SEPARATOR_CHAR = ":"; //$NON-NLS-1$

    public static final String NEW_FORMAT_PARENT_IDLOOKUP_FIELD_SEPARATOR_CHAR = "-";

    /**
     * 构造方法，根据格式化的字段名称初始化对象
     *
     * formattedFieldName 参数可以是以下格式之一：
     * 格式1：不包含任何':'或'-'的字母数字字符串，表示子对象的非多态关系字段名称
     * 格式2：包含':'的字母数字字符串，有两种解释：
     *   2a - (旧格式): <子关系名称>:<父idLookup字段名称>
     *   2b - (新格式): <父对象名称>:<关系字段的关系名称属性>
     * 格式3：包含一个':'和一个'-'的字母数字字符串，表示子对象中引用父对象idLookup字段的字段
     *
     * @param formattedFieldName 格式化的字段名称
     * @throws RelationshipFormatException 当字段名称为空或格式不正确时抛出
     */

    public ParentIdLookupFieldFormatter(String formattedFieldName) throws RelationshipFormatException {
        if (formattedFieldName == null || formattedFieldName.isBlank()) {
            throw new RelationshipFormatException("parent idLookup field name not specified");
        }
        String[] fieldNameParts = formattedFieldName.split(ParentIdLookupFieldFormatter.NEW_FORMAT_PARENT_IDLOOKUP_FIELD_SEPARATOR_CHAR);
        boolean hasParentIdLookupFieldName = false;
        if (fieldNameParts.length == 2) {
            // parent name not specified
            parentFieldName = fieldNameParts[1];
            hasParentIdLookupFieldName = true; // '.' char shows up only in format 3
            formattedFieldName = fieldNameParts[0];
        }
        fieldNameParts = formattedFieldName.split(ParentSObjectFormatter.NEW_FORMAT_RELATIONSHIP_NAME_SEPARATOR_CHAR);
        String relationshipName = null;
        String parentObjectName = null;
        if (hasParentIdLookupFieldName) { // format 3
            if (fieldNameParts.length == 2) {
                relationshipName = fieldNameParts[0];
                parentObjectName = fieldNameParts[1];
            } else { // Should not happen - no ':' char in name, may have '-' char
                if (parentFieldName == null) { // no ':' and no '.' in name
                    String errorStr = "field name " + formattedFieldName + " does not have ':' or '-' char";
                    log.error(errorStr);
                    throw new RelationshipFormatException(errorStr);
                } else {
                    // '-' char in name but no ':'
                    String errorStr = "field name " + formattedFieldName + " has '-' but does not have ':' char";
                    log.error(errorStr);
                    throw new RelationshipFormatException(errorStr);
                }
            }
        } else { // format 1 or format 2
            if (fieldNameParts.length == 2) { // format 2
                relationshipName = fieldNameParts[0];
                parentFieldName = fieldNameParts[1];
            } else { // format 1
                relationshipName = formattedFieldName;
            }
        }
        parentSObjectFormatter = new ParentSObjectFormatter(parentObjectName, relationshipName);
    }

    /**
     * 构造方法，根据指定的父对象名称、关系名称和父ID查找字段名称初始化对象
     * @param parentObjectName 父对象名称
     * @param relationshipName 关系名称
     * @param parentIdLookupFieldName 父ID查找字段名称
     * @throws RelationshipFormatException 当参数格式不正确时抛出
     */
    public ParentIdLookupFieldFormatter(String parentObjectName, String relationshipName, String parentIdLookupFieldName) throws RelationshipFormatException {
        parentSObjectFormatter = new ParentSObjectFormatter(parentObjectName, relationshipName);
        this.parentFieldName = parentIdLookupFieldName;
    }

    /**
     * 获取父字段名称
     * @return 父字段名称
     */
    public String getParentFieldName() {
        return parentFieldName;
    }

    /**
     * 获取父对象格式化器
     * @return ParentSObjectFormatter对象
     */
    public ParentSObjectFormatter getParent() {
        return parentSObjectFormatter;
    }

    /**
     * 返回对象的字符串表示形式
     * @return 格式化后的字符串
     */
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String separator = ParentIdLookupFieldFormatter.NEW_FORMAT_PARENT_IDLOOKUP_FIELD_SEPARATOR_CHAR;
        if (parentSObjectFormatter.getParentObjectName() == null) {
            separator = ParentIdLookupFieldFormatter.OLD_FORMAT_PARENT_IDLOOKUP_FIELD_SEPARATOR_CHAR;
        }
        return parentSObjectFormatter.toString() + separator + parentFieldName;
    }
}
