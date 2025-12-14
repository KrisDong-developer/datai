# 使用简单的字符串替换方法更新SQL文件

# 读取SQL文件内容
with open(r'd:\idea_demo\datai\datai-scenes\datai-scene-salesforce\datai-salesforce-config\src\main\resources\sql\salesforce_configuration.sql', 'r', encoding='utf-8') as f:
    lines = f.readlines()

new_lines = []
in_insert = False
insert_line = ""
values_line = ""
update_line = ""

for line in lines:
    stripped_line = line.strip()
    
    if stripped_line.startswith("INSERT INTO datai_configuration"):
        in_insert = True
        # 替换字段列表
        insert_line = "INSERT INTO datai_configuration (config_key, config_value, create_by, create_time, update_by, update_time, remark, environment_id, is_sensitive, is_encrypted, is_active, tenant_id)"
        new_lines.append(insert_line + "\n")
    elif in_insert and stripped_line.startswith("VALUES"):
        # 替换VALUES部分
        # 原始VALUES格式：VALUES ('key', 'value', 'admin', NOW(), 'admin', NOW(), 'remark', 'system', 1)
        # 提取括号内的值
        # 处理可能的换行情况，确保获取完整的VALUES部分
        if '(' in stripped_line and ')' in stripped_line:
            # 简单情况：VALUES在一行
            values_part = stripped_line[stripped_line.find('(')+1:stripped_line.rfind(')')]  # 提取括号内的所有内容
        else:
            # 复杂情况：VALUES跨多行，需要继续读取后续行
            values_part = stripped_line[stripped_line.find('(')+1:]  # 提取当前行的括号内容
            # 这里简化处理，假设VALUES部分在一行内
        
        # 处理可能包含的多余引号和括号
        values_part = values_part.strip()
        if values_part.startswith("'") and values_part.endswith("'"):
            values_part = values_part[1:-1]
        
        values_list = values_part.split(', ')
        # 保留前7个值，然后添加新的字段值
        new_values_list = values_list[:7]  # 保留前7个值
        new_values_list.extend(["NULL", "0", "0", "1", "'1'"])  # 添加新的字段值
        new_values = ', '.join(new_values_list)
        values_line = f"VALUES ({new_values})"
        new_lines.append("  " + values_line + "\n")
    elif in_insert and stripped_line.startswith("ON DUPLICATE KEY UPDATE"):
        # 替换ON DUPLICATE KEY UPDATE部分
        update_line = "ON DUPLICATE KEY UPDATE"
        new_update_content = "  config_value = VALUES(config_value),"
        new_update_content += "\n  update_by = VALUES(update_by),"
        new_update_content += "\n  update_time = VALUES(update_time),"
        new_update_content += "\n  remark = VALUES(remark),"
        new_update_content += "\n  environment_id = VALUES(environment_id),"
        new_update_content += "\n  is_sensitive = VALUES(is_sensitive),"
        new_update_content += "\n  is_encrypted = VALUES(is_encrypted),"
        new_update_content += "\n  is_active = VALUES(is_active),"
        new_update_content += "\n  tenant_id = VALUES(tenant_id)"
        new_lines.append("  " + update_line + "\n")
        new_lines.append(new_update_content + "\n")
    elif in_insert and stripped_line.endswith(";"):
        # 结束INSERT语句
        new_lines.append(";")
        in_insert = False
    elif in_insert:
        # 跳过旧的字段列表和VALUES部分
        continue
    else:
        # 保留其他行
        new_lines.append(line)

# 写入更新后的内容
with open(r'd:\idea_demo\datai\datai-scenes\datai-scene-salesforce\datai-salesforce-config\src\main\resources\sql\salesforce_configuration.sql', 'w', encoding='utf-8') as f:
    f.writelines(new_lines)

print("SQL文件已成功更新！")
