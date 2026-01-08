#!/usr/bin/env pwsh

# 自动提交脚本 - 基于 SSOT 2.0 规则

# 配置变量
$ProjectRoot = "$PSScriptRoot\.."
$DocsDir = "$ProjectRoot\docs"
$IndexFile = "$DocsDir\index.md"
$SessionsDir = "$DocsDir\sessions"
$ChangelogDir = "$DocsDir\changelog"
$CurrentDate = Get-Date -Format "yyyyMMdd"
$SessionFile = "$SessionsDir\$CurrentDate-session.md"

# 函数：更新文档索引
function Update-Index {
    Write-Host "正在更新文档索引..."
    
    # 获取所有文档文件
    $DocFiles = Get-ChildItem -Path $DocsDir -Recurse -File | Where-Object { $_.Extension -eq ".md" -or $_.Extension -eq ".canvas" }
    
    # 读取当前索引文件内容
    $IndexContent = Get-Content -Path $IndexFile -Raw
    
    # 这里可以添加更复杂的索引更新逻辑
    # 例如：自动添加新文件到索引
    
    Write-Host "文档索引更新完成"
}

# 函数：创建会话记录
function Create-Session {
    Write-Host "正在创建会话记录..."
    
    # 检查会话目录是否存在
    if (!(Test-Path $SessionsDir)) {
        New-Item -ItemType Directory -Path $SessionsDir -Force | Out-Null
    }
    
    # 创建会话记录内容
    $SessionContent = @"
# 会话记录

## 现状

[描述当前项目或任务的现状，包括已完成的工作、遇到的问题等]

## 目标

[描述本次会话的具体目标，包括要实现的功能、解决的问题等]

## 输入链接

- [需求文档](https://example.com) - 相关需求描述
- [设计文档](https://example.com) - 相关设计说明
- [架构决策](https://example.com) - 相关架构决策

## Prompt 文件

- [Prompt 模板](https://example.com) - 使用的提示词文件

## Context Snapshot

记录本次会话参考了哪些 Canvas 节点：

- [Authentication.canvas](../Authentication.canvas) - 项目架构视觉化展示
- **参考节点**: [节点名称](路径) - 描述
- **快照时间**: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")

## 执行过程

详细记录本次会话的执行过程，包括：

1. 执行的命令或操作
2. 遇到的问题及解决方案
3. 关键决策点
4. 时间线

## 关键产出

记录本次会话的关键产出，例如：

- 生成的代码文件
- 更新的文档
- 解决的问题
- 达成的共识

## 质疑与替代方案

记录在执行过程中提出的质疑和考虑的替代方案：

- 质疑：[具体质疑内容]
  - 替代方案：[替代方案描述]
  - 评估：[对替代方案的评估]

## 结论

总结本次会话的结果，包括：

- 完成的工作
- 达成的目标
- 后续的行动计划
- 需要跟进的事项

## Design Update

- [ ] 是否需要更新 Canvas?
- [ ] Authentication.canvas
- [ ] 其他 Canvas 文件: ____________________

## 复现步骤

提供复现本次会话结果的具体步骤：

1. [步骤 1 描述]
2. [步骤 2 描述]
3. [步骤 3 描述]
4. [验证方法描述]
"@
    
    # 写入会话记录文件
    $SessionContent | Out-File -FilePath $SessionFile -Encoding UTF8
    Write-Host "会话记录已创建: $SessionFile"
}

# 函数：创建变更记录
function Create-Changelog {
    Write-Host "正在创建变更记录..."
    
    # 检查变更日志目录是否存在
    if (!(Test-Path $ChangelogDir)) {
        New-Item -ItemType Directory -Path $ChangelogDir -Force | Out-Null
    }
    
    # 创建变更记录内容
    $ChangelogContent = @"
# 变更记录 - $CurrentDate

## 变更内容

- [变更 1] - 描述
- [变更 2] - 描述
- [变更 3] - 描述

## 影响范围

- 模块 1
- 模块 2
- 模块 3

## 实施人员

- [实施人员]

## 日期

$CurrentDate
"@
    
    # 写入变更记录文件
    $ChangelogFile = "$ChangelogDir\$CurrentDate-changelog.md"
    $ChangelogContent | Out-File -FilePath $ChangelogFile -Encoding UTF8
    Write-Host "变更记录已创建: $ChangelogFile"
}

# 函数：执行 git 提交
function Execute-GitCommit {
    Write-Host "正在执行 git 提交..."
    
    # 切换到项目根目录
    Set-Location -Path $ProjectRoot
    
    # 添加所有变更
    git add .
    
    # 提交变更
    $CommitMessage = "chore: auto-commit - $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")"
    git commit -m $CommitMessage
    
    Write-Host "git 提交完成"
}

# 主函数
function Main {
    Write-Host "开始执行自动提交脚本..."
    
    # 更新文档索引
    Update-Index
    
    # 创建会话记录
    Create-Session
    
    # 创建变更记录
    Create-Changelog
    
    # 执行 git 提交
    Execute-GitCommit
    
    Write-Host "自动提交脚本执行完成!"
}

# 执行主函数
Main
