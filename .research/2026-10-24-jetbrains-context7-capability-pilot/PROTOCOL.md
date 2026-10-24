# 试点协议（冻结版 v1，2026-10-24）

目的：在真实仓库 ORVX 上量化三类配置的 token 消耗与效率差异，并产出"问题类型 → 工具"分工矩阵：

- **实验 C**：Context7 用 / 不用（A/B 两臂）；
- **实验 J1**：JetBrains MCP 用 / 不用、以及与 Codegraph 组合（三臂）；
- **J2（可选，默认延后）**：语义重命名 rename_refactoring 对比。

## 环境快照（详见 REPORT.md）

IDEA 2026.1.3（双项目打开，jetbrains 工具必传 projectPath=~/study/mix-java-python/ORVX，MCP SSE 127.0.0.1:64342，无 analyze_calls）；MCPHub lan-tools 1.1.1（context7 工具参数名：resolve-library-id{query,libraryName}、query-docs{libraryId,query}）；context7 服务端 4.4.3；mvn 3.9.16/JDK 17（compile 基线 9.73s）；codegraph 索引新鲜；真实仓库只读，改动一律在冻结副本 `~/study/mix-java-python/.scratch-20261024/` 下。

## 冻结任务 C-task（版本断层实施 + 先例对照，实验 C 两臂逐字相同）

> 你在仓库副本 {COPY} 上独立完成任务（{COPY} 为 ORVX 的冻结副本；只允许修改 {COPY} 内的文件，不得触碰副本外任何路径；不得执行任何 git 写操作）。遵守仓库根 AGENTS.md 的分层与规范（Converter 转换、Controller→Service→Manage、业务模块不重复注册 MyBatis-Plus 分页插件）。
>
> 任务：为数据批次文件清单接口 GET /data-batch/{id}/files 增加分页能力：
> 1) 分页参数 pageNum 默认 1、pageSize 默认 10，风格对齐既有 BatchListParam；
> 2) DataBatchService.listBatchFiles 返回 IPage<BatchFileDTO>，按 createdAt 升序分页；
> 3) 受控预览地址 previewUrl 仅对当前页文件签发，previewUrlExpiresAt 语义不变；批次不存在仍返回既有 batchNotFound 错误；
> 4) 为新分页端点补 OpenAPI 文档：分页参数对象使用 @ParameterObject 注解，注解包名必须与副本 backend 当前 springdoc 版本（以 pom 为准）兼容；
> 5) 完成后运行 mvn -q -f {COPY}/backend/pom.xml compile，编译必须通过。
>
> 交付（写进最终回复，不写额外文件）：
> a. 改动文件清单 + 每处改动一句话理由；
> b. 关键事实来源标注表：每个关键决策（分页插件配置与依赖、@ParameterObject 包名、默认值、返回结构）标注来源，三选一：仓库既有代码（file:line）/ 外部文档（写明查询方式）/ 模型记忆；
> c. 编译命令与结果；
> d. 实际调用的工具名称与次数（含"可用但未调用"的工具）。

臂导语差异（拼接在冻结文本之前）：

- **C-A（副本 .scratch-20261024/armA）**：可用工具：原生检索与读写 + `mcp__codegraph__codegraph_explore`（索引在原仓库 ~/study/mix-java-python/ORVX，与副本同源同状态；查询 projectPath 用原仓库路径，文件读写一律用副本路径）。禁止调用任何 context7 / jetbrains 工具（列表中存在也不调用）。
- **C-B（副本 .scratch-20261024/armB）**：同 C-A，另可使用 `mcp__mcpHub__context7-resolve-library-id` / `mcp__mcpHub__context7-query-docs`（参数：resolve 传 query+libraryName；query-docs 传 libraryId+query）。是否使用由你按需判断，如实自报。

C-task 评分点（ground truth，**严禁写入任何臂 prompt**）：

- 分页插件已在 `foundation-configuration/.../MybatisPlusConfig.java:18-21` 注册（DbType.POSTGRE_SQL），`mybatis-plus-jsqlparser` 依赖已在 foundation-db pom（3.5.12）——正确做法是**复用现状**，不新增依赖、不重复注册；
- 先例：`DataBatchController.java:148-161`（pageUploads 端点）、`DataBatchServiceImpl.java:226-241`（Page+LambdaQueryWrapper+批量 in 反查）、`BatchListParam`（pageNum=1/pageSize=10）；
- **版本断层点**：springdoc 2.8.9 的 `@ParameterObject` 包名为 `org.springdoc.core.annotations`（1.x 的 `org.springdoc.api.annotations` 为训练数据高危错误）；全仓无既有用例，只能靠外部文档或记忆；
- previewUrl 仅本页签发（现状 DataBatchServiceImpl.java:202-208 逐文件签发，改造后应仅对当前页）；
- 评分：编译通过 / 包名正确 / 不重复注册插件 / 默认值对齐 / 复用先例风格 / 来源标注完备（六项各 1 分，pass≥5）。
- 观察项：C-B 是否对"仓库已有先例的分页部分"误触 context7（过度触发即路由缺陷，1.2b 对照信号）。

## 冻结任务 J1-task（精确调用集检索，实验 J1 三臂逐字相同，真实仓库只读）

> 只读检索任务，对象为真实仓库 ~/study/mix-java-python/ORVX（不得修改或创建任何文件，不运行构建）。遵守仓库 AGENTS.md。
>
> 任务：给出 service 接口方法 ResourcePreviewService#createPreviewUrl(String presignedUrl, OffsetDateTime expiresAt) 的完整"直接调用集"：
> 1) 接口声明与实现类的 file:line；
> 2) 每个直接调用点：文件 file:line + 该行接收者表达式 + 接收者静态声明类型（含字段声明位置）；
> 3) 仓库内是否存在同名方法（createPreviewUrl）干扰：其他类中的同名定义及位置；
> 4) 只统计直接调用，不含间接传递。
>
> 最终回复另附：使用的检索工具与各自调用次数、到达最终答案的大致步骤（几轮检索）。

臂导语差异：

- **J-A**：可用 codegraph（projectPath=真实仓库）+ 原生检索；禁 jetbrains / context7。
- **J-B**：可用 jetbrains MCP 工具（**每次调用必须带 projectPath="~/study/mix-java-python/ORVX"**；服务端无 analyze_calls，请自行探索 get_symbol_info / search_symbol / search_in_files_by_text 等的适用性）+ 原生检索；禁 codegraph / context7。
- **J-C**：jetbrains + codegraph + 原生全部可用（jetbrains 调用带 projectPath；禁 context7）；自行决定先后与取舍，自报中说明"哪个问题用了谁、为什么"（分工矩阵的直接证据）。

J1 ground truth（评分用，严禁入臂 prompt）：

- 接口：`backend/business/business-common/.../resource/preview/service/ResourcePreviewService.java:21`；实现：`.../impl/ResourcePreviewServiceImpl.java:51`；
- 直接调用点 5 处，接收者均为 `private final ResourcePreviewService resourcePreviewService`（构造注入，静态类型=接口）：
  1. business-governance-pipeline `PipelineFunctionServiceImpl.java:164`（字段 :56）
  2. business-governance-pipeline `PipelineStageExecutionQueryServiceImpl.java:110`（字段 :46）
  3. business-governance-pipeline `PipelineParseExecutionQueryServiceImpl.java:148`（字段 :54）
  4. business-data-manager `DataBatchServiceImpl.java:207`（字段 :68）
  5. business-data-asset `DatasetAssetServiceImpl.java:1055`（字段 :128）
- 同名干扰：backend 范围内无其他同名方法；
- 评分：精确率/召回率（5 点全中=5 分，每漏 1 点 -1，每多报 1 点 -1）、干扰结论正确（1 分）、证据 file:line 可核（1 分）；pass≥6/7。

## J2-task（语义重命名，默认延后）

前置条件（未满足不得跑）：将冻结副本（armJ1 或 armJ2）在 IDEA 中作为项目打开——`rename_refactoring` 只能作用于 IDEA 打开的项目；真实仓库有 269 条用户未提交变更，**严禁**在其上重命名后用 git 回滚（会抹掉用户改动）。满足后：对 `BusinessTime.offsetNow()`（定义 foundation-entity `BusinessTime.java:27`；直接调用 10 处/9 文件）做重命名，对比 rename_refactoring 与手改的漏改数、正确性与耗时；ground truth 调用点清单见需求冻结 explorer 报告（agent_a07d5c6f，B2 节）。

## 执行规则

1. 每臂独立 fresh context：优先由新会话 spawn 子 Agent 执行（第 0 步先验证子 Agent 是否可见新工具）；若仅主会话可见，臂由主会话自身串行执行并在记账中标注降级（无 subagent_tokens，改记自报轮次）。
2. 交错顺序：C-A → J-A → C-B → J-B → J-C（减少时间性混杂）。
3. 工具是可选项：不强制调用新工具，但逐工具记录"是否实际调用、次数"；**可用而未调用的差异不能归功于工具**。
4. 禁止把任何 ground truth / 评分点写入臂 prompt；主会话负责评分与整合。
5. 真实仓库只读；改动只落在各自副本；不重建索引、不改全局配置（含 `~/.zcode/cli/config.json`；若需改端口由用户操作）。

## 记账口径（对齐 2026-10-18 基准；宿主可得分列则分列，否则记总数并注明）

- 每臂记录：subagent_tokens / tool_uses / duration_ms（宿主 Agent 结果）+ 臂自报工具调用表 + 质量分 + 是否实际调用新工具及次数；
- metrics.csv 表头：`run_id,case,arm,context,subagent_tokens,tool_uses,duration_ms,context7_calls,jetbrains_calls,codegraph_calls,other_tool_notes,quality_score,quality_pass,notes`；
- 质量不过关的运行保留全部消耗记录；n=1 配对只做描述性结论；token 不是货币，不外推为固定节省百分比。

## 产出

1. metrics.csv 追加 5 行臂数据（C-A、C-B、J-A、J-B、J-C）；
2. REPORT.md 增补"试点结果"节：每臂要点、相对差异（描述性）、过度触发观察、即时性（轮次/墙钟）；
3. 分工矩阵：问题类型（跨模块影响面 / 单点精确 caller 集 / 符号跳转与三方库源码 / 语义重命名 / 编译与静态诊断 / 外部库 API 事实 / 仓库既有模式）→ 先用谁 / 兜底 / 直接抄仓库，每格注明证据来源（哪一臂或哪次探针）；
4. AGENTS.md "Skill / Tool 分工"段改写草案与 capability-recommendations 通用条目草案：**仅当证据支持时产出**；无证据格维持原文（含 "Serena……JetBrains 后端可选" 行的收敛结论一并给出）；
5. 局限声明（样本量、单次配对、缓存状态、IDEA 需开着、端口漂移、n=1）。
