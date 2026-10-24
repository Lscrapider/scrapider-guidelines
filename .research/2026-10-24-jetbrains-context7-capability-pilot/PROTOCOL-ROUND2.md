# 第二轮试点协议（ROUND2，2026-10-24 冻结）

目的（用户指令）：第一轮 C 组场景未触发 Context7 特性（本地证据全可得）。第二轮面向"新东西的使用文档"重设 C 场景（断代新库 / 成熟新库），度量**记忆+编译试错成本 vs 一步查文档成本**；J 组扩到 7 个不同类型场景 × 三臂（G=codegraph+原生 / B=jetbrains+原生 / F=全部可用）。**度量优先级：tokens > 墙钟**；C 臂加记编译尝试次数（试错轮次）。

边界同第一轮：真实仓库只读；实施改动只在冻结副本 `.scratch-20261024/` 下；不重建索引、不改全局配置、无 git 写操作；臂 prompt 禁止携带任何 ground truth / 评分点。

## C 组（实施，各 2 臂：X-A 禁 context7 / X-B 可用 context7；两臂均禁 WebSearch/WebFetch/webReader，保证"外部信息"唯一差异是 context7）

### R2-C1 冻结任务（断代新库 org.dhatim:fastexcel，context7 有其准确文档 /dhatim/fastexcel）

> 你在仓库副本 {COPY} 上独立完成任务（{COPY} 为 ai-data-governance 的冻结副本；只允许修改 {COPY} 内的文件，不得触碰副本外任何路径；不得执行任何 git 写操作）。遵守仓库根 AGENTS.md 的分层与规范（Converter 转换、Controller→Service→Manage）。
>
> 任务：为数据批次增加"文件清单导出 Excel"能力：
> 1) 新增端点 GET /data-batch/{id}/files/export：将该批次的文件清单导出为 .xlsx 下载流（Content-Disposition 附件名 batch-files-{id}.xlsx），列：文件名、对象键、大小（字节）、状态、创建时间；
> 2) Excel 生成必须使用 org.dhatim:fastexcel（Maven 坐标 org.dhatim:fastexcel，版本自选最新稳定版），禁止使用 POI、EasyExcel 等其他 Excel 库；新依赖加在职责合适的模块，导出逻辑遵循 Controller→Service 分层，PO→导出行数据的组装放 Converter 或等价位置；
> 3) 批次不存在仍返回既有 batchNotFound 错误；文件清单查询复用既有数据路径，不新写 SQL/Mapper；
> 4) 完成后运行 mvn -q -f {COPY}/backend/pom.xml compile，编译必须通过。
>
> 交付（写进最终回复，不写额外文件）：
> a. 改动文件清单 + 每处改动一句话理由；
> b. 关键事实来源标注表：fastexcel 坐标/版本、核心写入 API 用法、依赖放置模块、响应流结构——每项标注来源三选一：仓库既有代码（file:line）/ 外部文档（写明查询方式）/ 模型记忆；
> c. 编译命令与结果 + 编译尝试总次数（首次与每次失败重试各算一次，失败原因各一句话）；
> d. 实际调用的工具名称与次数（含"可用但未调用"的工具）。

臂导语：
- **C1-A**（副本 c1a）：可用工具：原生检索与读写 + `mcp__codegraph__codegraph_explore`（索引在原仓库 /Users/qinzeyu/study/mix-java-python/ai-data-governance，与副本同源同状态；查询 projectPath 用原仓库路径，文件读写一律用副本路径）。禁止调用任何 context7 / jetbrains 工具；同时禁止 WebSearch / WebFetch / `mcp__web_reader__webReader`（本任务不使用任何联网检索）。
- **C1-B**（副本 c1b）：同 C1-A，另可使用 `mcp__mcpHub__context7-resolve-library-id` / `mcp__mcpHub__context7-query-docs`（参数：resolve 传 query+libraryName；query-docs 传 libraryId+query）。是否使用由你按需判断，如实自报。同样禁止 WebSearch / WebFetch / webReader。

GT/评分点（6 项各 1 分，pass≥5）：编译通过 / 坐标正确（org.dhatim:fastexcel，无其他 Excel 库）/ 分层正确（Controller→Service，PO→行组装不在 Controller）/ batchNotFound 保留 / 来源标注完备 / 编译尝试次数自报与 Bash 调用记录可交叉核。观察项：B 臂是否调用 context7 及查询内容；A 臂失败重试次数（试错成本）。

### R2-C2 冻结任务（成熟新库 redisson-spring-boot-starter）

> 你在仓库副本 {COPY} 上独立完成任务（{COPY} 为 ai-data-governance 的冻结副本；只允许修改 {COPY} 内的文件，不得触碰副本外任何路径；不得执行任何 git 写操作）。遵守仓库根 AGENTS.md 的分层与规范。
>
> 任务：为"删除数据批次"操作增加分布式互斥锁，防止同一批次被并发删除：
> 1) 引入 org.redisson:redisson-spring-boot-starter（版本自选最新稳定版），Redis 连接复用仓库既有 spring data redis 配置，不得硬编码任何连接参数；
> 2) 删除入口（DataBatchService 既有删除方法）以 Redisson 分布式锁包裹，锁粒度为单个批次（key 包含批次 id）；获取锁失败时按仓库既有错误风格返回明确业务错误（可在 ErrorDefinitions 新增条目）；
> 3) 不得改变删除操作既有业务语义（校验、级联行为保持）；
> 4) 完成后运行 mvn -q -f {COPY}/backend/pom.xml compile，编译必须通过。
>
> 交付 a-d 同 R2-C1（来源标注含：redisson 坐标/版本、starter 自动装配与 Redis 连接来源、RLock 用法）。

臂导语：同 C1（C2-A 副本 c2a / C2-B 副本 c2b）。

GT/评分点（6 项各 1 分，pass≥5）：编译通过 / 坐标正确（org.redisson）/ 未硬编码连接参数 / 锁粒度批次级且失败有业务错误 / 删除语义不变 / 来源标注完备。观察项：成熟库场景下 B 臂是否仍触发 context7（对照 C1 的"断代库"预期差）。

## J 组（只读检索，7 场景 × 3 臂；三臂导语沿用第一轮 J1 的 J-A / J-B / J-C 文本，逐字）

G 臂导语：可用 codegraph（projectPath=真实仓库）+ 原生检索；禁 jetbrains / context7。
B 臂导语：可用 jetbrains MCP 工具（**每次调用必须带 projectPath="/Users/qinzeyu/study/mix-java-python/ai-data-governance"**；服务端无 analyze_calls，请自行探索 get_symbol_info / search_symbol / search_in_files_by_text 等的适用性）+ 原生检索；禁 codegraph / context7。
F 臂导语：jetbrains + codegraph + 原生全部可用（jetbrains 调用带 projectPath；禁 context7）；自行决定先后与取舍，自报中说明"哪个问题用了谁、为什么"。

### R2-J1 前端响应结构变更影响面（TS/Vue）

> 只读检索任务，对象为真实仓库 /Users/qinzeyu/study/mix-java-python/ai-data-governance（不得修改或创建任何文件，不运行构建）。遵守仓库 AGENTS.md。
>
> 背景：后端计划将 GET /data-batch/{id}/files 的响应从 BatchFileDTO 数组改为 MyBatis-Plus IPage 分页结构（records/total/size/current/pages）。
> 任务：给出前端需要适配的完整清单：
> 1) 该接口的前端 API 封装（file:line）与当前返回类型声明；
> 2) 所有调用该封装的文件与行，及各处当前如何消费返回值（是否直接当数组遍历/传参）；
> 3) 除直接调用外，是否存在把该返回值继续传递/存储的二次影响点（如组件 props、store）；
> 4) 结论：必须修改的文件清单与每处适配要点（一句话）。
>
> 最终回复另附：使用的检索工具与各自调用次数、到达最终答案的大致步骤（几轮检索）。

GT：封装 dataBatch.ts:71（Promise<IApiResult<BatchFileDTO[]>>，URL /governance/api/data-batch/${id}/files）；调用 useFileList.ts:5,45 / useBatchActions.ts:6,60 / useBatchDetail.ts:5,47；消费细节与二次影响评分时权威 grep 复核。评分（8 分制，pass≥6）：4 文件全中 4 分（漏/多 -1/处）、消费方式描述正确 2 分、二次影响结论正确 1 分、证据可核 1 分。

### R2-J2 Python worker 校验函数调用链（.py；IDEA 无 .py 符号索引）

> 只读检索任务，对象为真实仓库 /Users/qinzeyu/study/mix-java-python/ai-data-governance（不得修改或创建任何文件，不运行构建）。遵守仓库 AGENTS.md。
>
> 任务：worker（Python）src/ai_data_governance_worker/messaging/protocol/validators.py 定义了三个公开校验函数：
> 1) 给出 validate_command_amqp_properties / validate_notification_amqp_properties / validate_model_config_sync_amqp_properties 的定义 file:line；
> 2) 每个函数在生产代码（worker/src，不含 tests）中的直接调用点 file:line 与所在处理类/函数名；
> 3) 校验失败抛出的异常类型是什么、在哪定义（file:line）、调用侧捕获后如何处理（一句话）；
> 4) tests 中对这三个函数的引用是否构成同名干扰，列出 test 引用文件与大致处数。
>
> 最终回复另附：使用的检索工具与各自调用次数、到达最终答案的大致步骤（几轮检索）。

GT：定义 validators.py:45/:70/:95；调用 command_consumer.py:68、notification_consumer.py:57、model_config_sync_handler.py:59（import 行 28/28/29）；异常 MessageValidationException（定义位置评分时核）；tests 干扰 test_pipeline_cancellation.py 6 处（472,513,546,575,668,702）。评分（7 分制，pass≥5）：3 定义+3 调用点全中 3 分、异常类型与处理 2 分、test 干扰正确 1 分、证据可核 1 分。

### R2-J3 配置键全链追踪（yaml→绑定→使用）

> 只读检索任务，对象为真实仓库 /Users/qinzeyu/study/mix-java-python/ai-data-governance（不得修改或创建任何文件，不运行构建）。遵守仓库 AGENTS.md。
>
> 任务：追踪配置项 file-upload.preview-token-ttl-seconds 的完整链路：
> 1) yml 中定义位置 file:line、默认值、环境变量覆盖名；
> 2) 绑定到哪个 @ConfigurationProperties 类与字段（file:line）、启动校验位置（file:line）；
> 3) 业务代码中所有读取该配置的位置（file:line）及各自用途（一句话/处）；
> 4) 仓库内是否存在同名旧拷贝或相似配置（如 target/classes 下），是否构成检索干扰。
>
> 最终回复另附：使用的检索工具与各自调用次数、到达最终答案的大致步骤（几轮检索）。

GT：application.yml:69（默认 600，env FILE_UPLOAD_PREVIEW_TOKEN_TTL_SECONDS）→ FileUploadProperties.java:39（字段）+:72（Assert 校验）→ 读取 DataBatchServiceImpl:170、DatasetAssetServiceImpl:1033；干扰 backend/bootstrap/target/classes/application.yml:69 同名拷贝。评分（8 分制，pass≥6）：链 4 环节各 1 分、默认值/env 正确 1 分、两处业务读取全中 2 分、干扰识别 1 分。

### R2-J4 同名方法消歧调用集（toDTO）

> 只读检索任务，对象为真实仓库 /Users/qinzeyu/study/mix-java-python/ai-data-governance（不得修改或创建任何文件，不运行构建）。遵守仓库 AGENTS.md。
>
> 任务：Converter 静态方法 DataBatchConverter#toDTO(DataBatchPO, List<SourceCategory>)：
> 1) 定义 file:line；
> 2) 完整直接调用集（file:line + 调用处所在方法名）；
> 3) 全仓所有名为 toDTO 的方法定义清单（file:line + 所属类），指出除目标外哪些是同名干扰；
> 4) DataBatchConverter 中其他 toXxx 方法（如 toFileDTO/toUploadDTO）是否可能被误算入调用集，如何区分。
>
> 最终回复另附：使用的检索工具与各自调用次数、到达最终答案的大致步骤（几轮检索）。

GT：定义 DataBatchConverter.java:31；调用点与同名清单以评分时权威 grep 为准（主会话同命令生成 GT 再比对）。评分（7 分制，pass≥5）：调用集精确 3 分（漏/多 -1/处）、同名清单完备 2 分、区分说明 1 分、证据可核 1 分。

### R2-J5 跨语言 API 路由追踪（前端→Controller→Service→数据访问）

> 只读检索任务，对象为真实仓库 /Users/qinzeyu/study/mix-java-python/ai-data-governance（不得修改或创建任何文件，不运行构建）。遵守仓库 AGENTS.md。
>
> 任务：给出前端"获取批次文件清单"请求从前端到后端数据访问的完整链路：
> 1) 前端 API 封装函数名与 file:line、请求 URL、HTTP 方法；
> 2) URL 前缀 /governance/api 如何映射到后端（context-path 或网关约定），给出证据 file:line；
> 3) 后端 Controller 类与方法（file:line + 路由注解）→ Service 接口方法 file:line → 实现方法 file:line；
> 4) 实现内最终通过哪个 Mapper/Manage 完成数据访问（类+调用 file:line）。
>
> 最终回复另附：使用的检索工具与各自调用次数、到达最终答案的大致步骤（几轮检索）。

GT：listBatchFiles dataBatch.ts:71（GET）→ context-path 证据（application.yml server.servlet.context-path，评分时核）→ DataBatchController.java:43（@RequestMapping("/data-batch")）+:108（@GetMapping("/{id}/files")）→ DataBatchService.java:60 → DataBatchServiceImpl.java:161 → batchFileManage（ServiceImpl 内 MyBatis-Plus BaseMapper 通道，评分时核）。评分（7 分制，pass≥5）：前端 1 分、前缀映射证据 1 分、Controller 2 分、Service 接口/实现 2 分、数据访问 1 分。

### R2-J6 接口多实现与装配路由（StageExecutionStrategy）

> 只读检索任务，对象为真实仓库 /Users/qinzeyu/study/mix-java-python/ai-data-governance（不得修改或创建任何文件，不运行构建）。遵守仓库 AGENTS.md。
>
> 任务：接口 StageExecutionStrategy：
> 1) 接口定义 file:line 与方法签名（几个方法、各是什么）；
> 2) 全部实现类清单（类名 + file:line）与每个实现对应的 stage 类型/含义（一句话）；
> 3) 运行时如何从多个实现路由到具体策略：注册/装配机制所在类与 file:line、路由 key 是什么、在哪消费；
> 4) 是否存在未被路由覆盖的实现或 stage 类型缺 strategy（说明判断依据）。
>
> 最终回复另附：使用的检索工具与各自调用次数、到达最终答案的大致步骤（几轮检索）。

GT：7 实现（Annotation/Processing/Parsing/SelectData/Chunking/DatasetBuild/DataAugmentation StageExecutionStrategy，business-governance-execution/.../handler/stage/）；路由 StageExecutionStrategyRegistry.java:21（Map<String,StageExecutionStrategy>）+:23（构造注入 List）；key=stage 类型名（评分时核 registry 实现）。评分（7 分制，pass≥5）：实现类 7 全中 3 分（漏 1 -1）、路由机制 2 分、key 说明 1 分、第 4 点有依据 1 分。

### R2-J7 错误码定义→使用→HTTP 响应全链（batchNotFound）

> 只读检索任务，对象为真实仓库 /Users/qinzeyu/study/mix-java-python/ai-data-governance（不得修改或创建任何文件，不运行构建）。遵守仓库 AGENTS.md。
>
> 任务：错误 batchNotFound：
> 1) 定义位置（ErrorDefinitions 中 file:line）与其错误码值、HTTP 语义（状态码是多少、错误在响应体哪个字段）；
> 2) 全部使用点（file:line + 所在方法一句话用途）；
> 3) BusinessException 抛出后到 HTTP 响应的路径：处理器类 file:line、响应体如何序列化（字段结构）；
> 4) 前端如何消费该错误（统一拦截/静默参数），给出前端证据 file:line（若能找到）。
>
> 最终回复另附：使用的检索工具与各自调用次数、到达最终答案的大致步骤（几轮检索）。

GT：ErrorDefinitions.java:156 定义；使用 10 处（DataBatchServiceImpl 108,164,182,215,252,261,274,409,451,514）；处理器 GlobalExceptionHandler.java（bootstrap，评分时核行号与序列化结构）；前端拦截（评分时宽松核对）。评分（8 分制，pass≥6）：定义+语义 2 分、10 使用点 3 分（漏 1 -0.5）、处理器路径与序列化 2 分、前端证据 1 分。

## 执行与记账

- 顺序：C1-A→C1-B→C2-A→C2-B（各臂前 rsync 新建/刷新副本 c1a/c1b/c2a/c2b）；J1..J7 每场景 G/B/F 三臂同批并行（fresh context），场景间主会话评分。
- metrics.csv 追加行：run_id 前缀 r2-（r2c1-a/r2c1-b/r2c2-a/r2c2-b/r2j1-g/r2j1-b/r2j1-f/...），字段同表头；notes 记编译尝试次数（C 臂）与实际工具调用。
- 评分一律主会话权威复核（副本 grep + 真实仓库 grep），不只采信自报。
