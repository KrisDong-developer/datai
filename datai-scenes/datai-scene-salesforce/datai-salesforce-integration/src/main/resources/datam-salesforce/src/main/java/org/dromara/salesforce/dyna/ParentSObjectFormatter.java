package org.dromara.salesforce.dyna;

import org.dromara.salesforce.exception.RelationshipFormatException;

/**
 * ParentSObjectFormatter 类用于格式化和解析父级对象的关系字段信息。
 *
 * 此类主要用于处理Salesforce中对象间的关系字段，特别是多态查找关系。
 * 它可以解析和格式化以下格式的字符串：
 * 1. 简单关系名称（非多态查找关系）：relationshipName
 * 2. 新格式（支持多态查找关系）：relationshipName:parentObjectName
 *
 * 主要功能包括：
 * 1. 解析格式化的字段名称字符串，提取关系名称和父对象名称
 * 2. 根据给定的关系名称和父对象名称创建格式化字符串
 * 3. 提供获取关系名称和父对象名称的方法
 * 4. 支持匹配比较功能

 */
public class ParentSObjectFormatter {
    private String relationshipName;
    private String parentObjectName = null;

    public static final String NEW_FORMAT_RELATIONSHIP_NAME_SEPARATOR_CHAR = ":";

    /**
     * 构造方法，根据指定的父对象名称和关系名称初始化对象
     * @param parentObjectName 父对象名称
     * @param relationshipName 关系名称
     * @throws RelationshipFormatException 当关系名称为空或格式不正确时抛出
     */
    public ParentSObjectFormatter(String parentObjectName, String relationshipName) throws RelationshipFormatException{
        initialize(parentObjectName, relationshipName);
    }

    /**
     * 构造方法，根据格式化的字符串初始化对象
     * 支持以下格式：
     * 1. relationshipName （非多态查找关系）
     * 2. relationshipName:parentObjectName （多态查找关系）
     *
     * @param formattedName 格式化的名称字符串
     * @throws RelationshipFormatException 当输入字符串格式不正确时抛出
     */
    public ParentSObjectFormatter(String formattedName) throws RelationshipFormatException {
        String relationshipName = null;
        String parentObjectName = null;
        if (formattedName == null) {
            throw new RelationshipFormatException("relationship parent name not specified");
        }
        String[] fieldNameParts = formattedName.split(ParentIdLookupFieldFormatter.NEW_FORMAT_PARENT_IDLOOKUP_FIELD_SEPARATOR_CHAR);
        if (fieldNameParts.length == 2) { // discard the part containing parent's idLookup field name
            formattedName = fieldNameParts[0];
        }
        fieldNameParts = formattedName.split(ParentSObjectFormatter.NEW_FORMAT_RELATIONSHIP_NAME_SEPARATOR_CHAR);
        if (fieldNameParts.length == 2) { // format 2, interpretation 1
            relationshipName = fieldNameParts[0];
            parentObjectName = fieldNameParts[1];
        } else { // format 1
            relationshipName = formattedName;
        }
        initialize(parentObjectName, relationshipName);
    }

    /**
     * 设置父对象名称
     * @param name 父对象名称
     */
    public void setParentObjectName(String name) {
        this.parentObjectName = name;
    }

    /**
     * 初始化方法，验证并设置关系名称和父对象名称
     * @param parentObjectName 父对象名称
     * @param relationshipName 关系名称
     * @throws RelationshipFormatException 当关系名称为空或格式不正确时抛出
     */
    private void initialize(String parentObjectName, String relationshipName) throws RelationshipFormatException{
        if ((relationshipName == null || relationshipName.isBlank())) {
            throw new RelationshipFormatException("Relationship name not specified");
        }
        this.parentObjectName = parentObjectName;
        this.relationshipName = relationshipName;
    }

    /**
     * 获取关系名称
     * @return 关系名称
     */
    public String getRelationshipName() {
        return relationshipName;
    }

    /**
     * 获取父对象名称
     * @return 父对象名称
     */
    public String getParentObjectName() {
        return parentObjectName;
    }

    /**
     * 返回格式化后的字符串表示
     * @return 格式化字符串，格式为 relationshipName:parentObjectName 或 relationshipName
     */
    public String toString() {
        if (parentObjectName == null) {
            return relationshipName;
        }
        return relationshipName
                + ParentSObjectFormatter.NEW_FORMAT_RELATIONSHIP_NAME_SEPARATOR_CHAR
                + parentObjectName;
    }

    /**
     * 比较当前对象与指定名称是否匹配
     * @param nameToCompareWith 要比较的名称
     * @return 如果匹配返回true，否则返回false
     */
    public boolean matches(String nameToCompareWith) {
        if (relationshipName == null) {
            return false;
        }
        if (parentObjectName == null) {
            return nameToCompareWith.toLowerCase().startsWith(relationshipName.toLowerCase());
        } else {
            return nameToCompareWith.toLowerCase().equalsIgnoreCase(relationshipName + NEW_FORMAT_RELATIONSHIP_NAME_SEPARATOR_CHAR + parentObjectName);
        }
    }
}
