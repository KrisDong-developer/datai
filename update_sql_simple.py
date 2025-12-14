# 使用简单的字符串替换方法更新SQL文件

# 读取SQL文件内容
with open(r'd:\idea_demo\datai\datai-scenes\datai-scene-salesforce\datai-salesforce-config\src\main\resources\sql\salesforce_configuration.sql', 'r', encoding='utf-8') as f:
    content = f.read()

# 定义要替换的旧内容和新内容
old_fields = "config_key, config_value, create_by, create_time, update_by, update_time, remark, create_dept, tenant_id"
new_fields = "config_key, config_value, create_by, create_time, update_by, update_time, remark, environment_id, is_sensitive, is_encrypted, is_active, tenant_id"

old_update = "  config_value = VALUES(config_value),\n  update_by = VALUES(update_by),\n  update_time = VALUES(update_time),\n  remark = VALUES(remark),\n  create_dept = VALUES(create_dept),\n  tenant_id = VALUES(tenant_id)"

new_update = "  config_value = VALUES(config_value),\n  update_by = VALUES(update_by),\n  update_time = VALUES(update_time),\n  remark = VALUES(remark),\n  environment_id = VALUES(environment_id),\n  is_sensitive = VALUES(is_sensitive),\n  is_encrypted = VALUES(is_encrypted),\n  is_active = VALUES(is_active),\n  tenant_id = VALUES(tenant_id)"

# 替换字段列表
content = content.replace(old_fields, new_fields)

# 替换UPDATE部分
content = content.replace(old_update, new_update)

# 修复VALUES部分的多余括号
content = content.replace("VALUES ((", "VALUES (")
content = content.replace(")),", "),")

# 写入更新后的内容
with open(r'd:\idea_demo\datai\datai-scenes\datai-scene-salesforce\datai-salesforce-config\src\main\resources\sql\salesforce_configuration.sql', 'w', encoding='utf-8') as f:
    f.write(content)

print("SQL文件已成功更新！")