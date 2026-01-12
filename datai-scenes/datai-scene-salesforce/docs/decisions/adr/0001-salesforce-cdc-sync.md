# 架构决策记录 (ADR) 模板

## 背景

描述决策的背景和上下文，包括面临的问题、约束条件和相关的业务需求。

本项目需要实现Salesforce数据的及时同步至本地数据库，以确保本地系统能够实时获取Salesforce中的最新数据。传统的全量同步和基于时间戳的增量同步方案存在数据延迟和不一致的问题，无法满足实时性要求。因此，本项目采用Salesforce Pub/Sub API 同步方案，仅支持基于Pub/Sub API的实时同步模式。同时，需要在现有的对象表中增加字段用于判断是否开启实时数据同步，并为实时数据同步创建专门的日志表用于记录详细信息。

## 决策

明确陈述所做出的架构决策，包括具体的技术选择、设计方案或实现策略。

1. **采用Salesforce Pub/Sub API 同步方案**：
   - 利用Salesforce Pub/Sub API订阅Salesforce事件总线，实时捕获数据变更
   - 仅支持基于Pub/Sub API的实时同步模式
   - 直接通过Pub/Sub API机制同步数据变更，无需单独的初始化同步
   - 利用Pub/Sub API的高性能和可靠性，提高数据同步的实时性和稳定性

2. **对象表结构扩展**：
   - 在现有的对象表中增加`is_realtime_sync`字段（tinyint(1)类型）
   - 用于标识该对象是否开启实时数据同步
   - 在对象启用Pub/Sub API时，检查本地数据库中的batch表是否已经全量拉取存量数据

3. **实时同步日志表设计**：
   - 创建专门的`datai_integration_realtime_sync_log`表
   - 记录对象数据同步的详细信息，包括操作类型、变更数据、同步状态等
   - 已实现DataiIntegrationRealtimeSyncLog实体类和DataiIntegrationRealtimeSyncLogController控制器
   - 提供日志查询、导出和详情查看功能

4. **技术实现策略**：
   - 基于现有的集成核心功能，集成Salesforce Pub/Sub API客户端库
   - 利用现有的会话管理和连接池机制
   - 实现事件驱动的同步处理架构，事件处理采用异步执行方式
   - upsert操作参考DataiIntegrationBatchServiceImpl中的processQueryResult方法实现
   - 利用Pub/Sub API的自动重连和消息重试机制，提高系统可靠性
   - 暂不考虑额外的失败重试机制

## 备选方案

列出考虑过的其他备选方案，包括每种方案的优缺点。

1. **传统定时同步方案**：
   - **优点**：实现简单，易于理解和维护
   - **缺点**：数据延迟高，无法满足实时性要求；频繁的API调用可能导致限流

2. **基于时间戳的增量同步**：
   - **优点**：实现相对简单，数据延迟较定时同步低
   - **缺点**：无法捕获所有类型的变更；时间戳机制可能导致数据不一致

3. **Salesforce Bulk API批量同步**：
   - **优点**：适合大规模数据同步，API调用次数少
   - **缺点**：实时性差，不适合频繁的小批量数据同步

4. **Salesforce CDC (Change Data Capture) 同步方案**：
   - **优点**：支持实时数据变更捕获
   - **缺点**：相比Pub/Sub API，性能和可靠性较低；缺乏自动重连和消息重试机制

## 影响

分析该决策对系统架构、开发流程、运维管理等方面的影响。

1. **系统架构影响**：
   - 引入事件驱动架构，增加系统复杂性
   - 需要集成Salesforce Pub/Sub API客户端库
   - 数据流转路径发生变化，从定时拉取变为实时推送
   - 利用Pub/Sub API的高性能特性，提升系统整体响应速度

2. **开发流程影响**：
   - 需要学习Salesforce Pub/Sub API相关知识
   - 增加开发和测试工作量
   - 需要设计新的同步监控和故障处理机制
   - 利用Pub/Sub API的标准化接口，简化开发流程

3. **运维管理影响**：
   - 需要监控Pub/Sub API连接状态和事件处理情况
   - 增加系统资源消耗，需要评估服务器配置
   - 需要制定新的运维流程和故障处理预案
   - 利用Pub/Sub API的可靠性特性，减少运维工作量

4. **数据库影响**：
   - 需要修改现有的对象表结构，增加`is_realtime_sync`字段
   - 需要创建新的实时同步日志表
   - 增加数据库存储和写入压力

## 风险

识别该决策可能带来的风险，包括技术风险、业务风险和实施风险。

1. **技术风险**：
   - Salesforce Pub/Sub API客户端库可能存在版本兼容性问题
   - 事件处理机制可能成为性能瓶颈
   - 数据类型映射和转换可能出现问题
   - Pub/Sub API的配置和调优需要专业知识

2. **业务风险**：
   - 实时同步可能增加Salesforce API调用次数，触发限流
   - 数据同步失败可能导致业务数据不一致
   - 系统复杂度增加可能影响整体稳定性

3. **实施风险**：
   - 开发和测试工作量可能超出预期
   - 现有系统架构可能需要较大调整
   - 运维团队可能需要额外培训
   - Pub/Sub API的使用需要Salesforce管理员的配置支持

## 回滚策略

描述如果决策实施后出现问题，如何进行回滚或调整。

1. **回滚步骤**：
   - 停止Pub/Sub API订阅和事件处理
   - 切换回传统的定时同步方案
   - 禁用对象表中的`is_realtime_sync`字段
   - 暂停实时同步日志表的使用

2. **调整策略**：
   - 优化Pub/Sub API客户端配置，减少消息处理压力
   - 增加事件处理的并行度和容错能力
   - 调整同步频率和批量处理大小
   - 优化Pub/Sub API连接参数，提高可靠性

3. **应急方案**：
   - 准备手动触发的全量同步脚本
   - 建立数据一致性检查机制
   - 制定故障处理流程和应急预案

## 验收标准

定义验证该决策有效性的具体标准和测试方法。

1. **功能验收**：
   - 成功使用Pub/Sub API订阅Salesforce事件总线并捕获变更
   - 能够成功处理和同步捕获的变更数据
   - 同步数据与Salesforce中的数据一致
   - 对象表中的`is_realtime_sync`字段正确工作
   - 实时同步日志表能够记录详细信息

2. **性能验收**：
   - 数据同步延迟不超过30秒
   - 系统能够处理高峰期的变更量
   - API调用次数在Salesforce限流范围内
   - Pub/Sub API连接稳定，无频繁断开重连

3. **可靠性验收**：
   - 系统能够自动处理网络中断和重试
   - 故障恢复后能够继续同步未处理的变更
   - 数据一致性检查通过率达到100%
   - Pub/Sub API的自动重连机制正常工作

4. **运维验收**：
   - 监控系统能够实时显示同步状态
   - 日志系统能够记录详细的同步信息
   - 运维团队能够熟练操作和维护系统
   - Pub/Sub API的监控指标能够正常采集

## 视觉锚点

### Visual Reference

引用 Canvas 的具体节点或快照：
- [Authentication.canvas](../../Authentication.canvas) - 项目架构视觉化展示
- **具体节点**: [集成任务](node_integration_task) - 处理Salesforce的定时同步任务
- **具体节点**: [SessionManager](node_session_manager_detail) - 会话管理，提供登录服务
- **具体节点**: [Salesforce连接类型](node_integration_connections) - 提供与Salesforce的各种连接方式

### Status

- [x] Draft
- [ ] Accepted
- [ ] Superceded

## 参考资料

列出与该决策相关的参考资料，包括文档、文章或其他资源。

- [Salesforce CDC documentation](https://developer.salesforce.com/docs/atlas.en-us.change_data_capture.meta/change_data_capture/cdc_intro.htm)
- [Salesforce Event Bus documentation](https://developer.salesforce.com/docs/atlas.en-us.platform_events.meta/platform_events/platform_events_intro.htm)
- [0001-salesforce-realtime-sync.md](../../requirements/0001-salesforce-realtime-sync.md) - Salesforce数据及时同步至本地数据库需求文档
