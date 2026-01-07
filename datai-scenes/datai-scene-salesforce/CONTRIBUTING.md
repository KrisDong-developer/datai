# 协作规则

## 代码提交规范
- 提交信息格式：`<type>: <description>`
- 类型包括：feat、fix、docs、style、refactor、test、chore
- 描述简洁明了，不超过50字符

## 分支管理
- `main`：主分支，稳定版本
- `dev`：开发分支，集成测试
- 特性分支：`feat/<feature-name>`
- 修复分支：`fix/<issue-number>`

## 开发流程
1. 从`dev`分支创建特性分支
2. 完成开发后提交PR到`dev`分支
3. 代码审查通过后合并
4. 发布前从`dev`分支合并到`main`分支

## 文档规范
- 所有文档统一存放于`docs/`目录
- 遵循Single Source of Truth原则
- 文档更新需同步更新`docs/index.md`索引

## 启用Git钩子
```bash
git config core.hooksPath .githooks
```