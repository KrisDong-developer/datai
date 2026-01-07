#!/bin/bash

# 验证脚本：检查文档和代码的完整性

set -e

echo "=== 开始验证 ==="

# 1. 检查目录结构
echo "\n1. 检查目录结构..."
required_dirs=("docs" "docs/requirements" "docs/design" "docs/decisions/adr" "docs/prompts" "docs/sessions" "docs/retros" "docs/changelog")

for dir in "${required_dirs[@]}"; do
    if [ ! -d "$dir" ]; then
        echo "错误：目录 $dir 不存在"
        exit 1
    else
        echo "✓ 目录 $dir 存在"
    fi
done

# 2. 检查关键文件
echo "\n2. 检查关键文件..."
required_files=("README.md" "CONTRIBUTING.md" "CHANGELOG.md" "docs/index.md" "docs/decisions/adr/0000-template.md" "docs/prompts/0000-template.md" "docs/sessions/YYYYMMDD-template.md" "docs/retros/YYYYMMDD-template.md")

for file in "${required_files[@]}"; do
    if [ ! -f "$file" ]; then
        echo "错误：文件 $file 不存在"
        exit 1
    else
        echo "✓ 文件 $file 存在"
    fi
done

# 3. 检查文档链接
echo "\n3. 检查文档链接..."

# 检查docs/index.md中的链接
echo "检查 docs/index.md 中的链接..."
while read -r line; do
    if [[ $line =~ \[([^\]]+)\]\(([^\)]+)\) ]]; then
        link=${BASH_REMATCH[2]}
        # 跳过外部链接
        if [[ $link != http* ]]; then
            if [ ! -f "docs/$link" ]; then
                echo "警告：链接 $link 指向的文件不存在"
            else
                echo "✓ 链接 $link 有效"
            fi
        fi
    fi
done < "docs/index.md"

# 4. 检查ADR编号
echo "\n4. 检查ADR编号..."
adr_files=$(find "docs/decisions/adr" -name "*.md" | grep -v "template.md")
adr_numbers=()

for file in $adr_files; do
    if [[ $file =~ ([0-9]{4})- ]]; then
        number=${BASH_REMATCH[1]}
        adr_numbers+=($number)
    fi
done

# 检查重复编号
if [ ${#adr_numbers[@]} -gt 0 ]; then
    sorted_numbers=($(echo "${adr_numbers[@]}" | tr ' ' '\n' | sort | uniq -d))
    if [ ${#sorted_numbers[@]} -gt 0 ]; then
        echo "错误：发现重复的ADR编号：${sorted_numbers[@]}"
        exit 1
    else
        echo "✓ 无重复的ADR编号"
    fi
else
    echo "✓ 无ADR文件需要检查"
fi

# 5. 检查Prompt模板字段
echo "\n5. 检查Prompt模板字段..."
prompt_template="docs/prompts/0000-template.md"

required_fields=(
    "输入引用"
    "目标"
    "输出格式"
    "约束"
    "验收标准"
    "风险"
    "使用记录"
)

for field in "${required_fields[@]}"; do
    if ! grep -q "$field" "$prompt_template"; then
        echo "警告：Prompt模板缺少字段：$field"
    else
        echo "✓ Prompt模板包含字段：$field"
    fi
done

# 6. 检查Markdown语法
echo "\n6. 检查Markdown语法..."

# 检查是否有未闭合的Markdown标记
md_files=$(find "docs" -name "*.md")
for file in $md_files; do
    # 检查未闭合的标题
    if grep -q "^#\+[^#]" "$file" && ! grep -q "^#\+[^#].*$" "$file"; then
        echo "警告：$file 可能有未闭合的标题"
    fi
    
    # 检查未闭合的列表
    if grep -q "^- " "$file" || grep -q "^[0-9]\+\. " "$file"; then
        # 简单检查，确保列表项有内容
        if grep -q "^- *$" "$file" || grep -q "^[0-9]\+\. *$" "$file"; then
            echo "警告：$file 可能有空的列表项"
        fi
    fi
done

# 7. 检查Git钩子
echo "\n7. 检查Git钩子..."
hook_dir=".githooks"
required_hooks=("pre-commit" "commit-msg")

if [ -d "$hook_dir" ]; then
    for hook in "${required_hooks[@]}"; do
        if [ -f "$hook_dir/$hook" ]; then
            echo "✓ Git钩子 $hook 存在"
            if [ -x "$hook_dir/$hook" ]; then
                echo "✓ Git钩子 $hook 有执行权限"
            else
                echo "警告：Git钩子 $hook 缺少执行权限"
            fi
        else
            echo "警告：Git钩子 $hook 不存在"
        fi
    done
else
    echo "警告：Git钩子目录 $hook_dir 不存在"
fi

# 8. 检查PR模板
echo "\n8. 检查PR模板..."
if [ -f ".github/PULL_REQUEST_TEMPLATE.md" ]; then
    echo "✓ PR模板存在"
else
    echo "警告：PR模板不存在"
fi

# 9. 检查工作协议和并行任务清单
echo "\n9. 检查工作协议和并行任务清单..."
if [ -f "docs/working-agreement.md" ]; then
    echo "✓ 工作协议存在"
else
    echo "警告：工作协议不存在"
fi

if [ -f "docs/parallel-backlog.md" ]; then
    echo "✓ 并行任务清单存在"
else
    echo "警告：并行任务清单不存在"
fi

echo "\n=== 验证完成 ==="
echo "所有检查通过！"
