# JetBrains MCP 与 Context7 能力试点：预检与会话内可见性报告

Date: 2026-10-24
Status: 试点完成（2026-10-24 执行会话）：第 0 步预检通过（主会话与子 Agent 均可见并实调 context7 / jetbrains-mcp / codegraph），五臂全部由子 Agent fresh context 执行并评分（C-A/C-B 各 6/6、J-A/J-B/J-C 各 7/7，J2 延后）；结果见文末「试点结果」与「分工矩阵」节

> Developer research archive. Omit `.research/` when installing the Skill; the Skill does not load this report.

## 结论（本会话）

**本次会话未产生任何可写入 AGENTS.md 草案或 capability-recommendations 条目的对比证据；两份草案未产出，Serena/JetBrains 现行表述维持原样。** 原因与事实如下：

1. **Context7（经 MCPHub）**：lan-tools 组已注册 `context7-resolve-library-id` / `context7-query-docs`（工具清单 18→20），并**协议级端到端调通**（resolve 返回 `/baomidou/mybatis-plus`；query-docs 返回"分页插件 v3.5.9 起拆分、需单独引入 mybatis-plus-jsqlparser"的官方文档片段）。但本会话与新建子 Agent 的工具清单均未获得该工具（注册表未刷新到本会话的 spawn）。
2. **JetBrains MCP**：ZCode 中为**直连 SSE**（`~/.zcode/cli/config.json` 的 `jetbrain-mcp` → `http://127.0.0.1:64342/sse`），**不经 MCPHub**。该配置条目 2026-10-24 13:53 写入，晚于 ZCode 应用启动时间（前日 22:43），故本会话与子 Agent 均无 `mcp__jetbrain-mcp__*` 工具。服务端本身健康：SSE 握手成功，`IntelliJ IDEA MCP Server 2026.1.3`，暴露 52 个工具。
3. **服务端工具面缺口**：预检四件套中 `search_symbol` / `get_file_problems` / `read_file` 在列，**`analyze_calls` 不在**暴露清单；调用集分析需在试点中验证 `get_symbol_info`（file/line/column 定位）等替代路径。IDEA 同时打开两个项目（scrapider-guidelines、ai-data-governance）→ **所有 jetbrains 工具调用必须显式传 `projectPath`**，否则返回 "Unable to determine the target project"。
4. 阶段一/二未在本会话运行（预检失败项记 Blocked、不许顶替）。冻结任务文本、4 份冻结副本、评分 ground truth 与记账口径已就绪（PROTOCOL.md），移交新会话按 HANDOFF-PROMPT.md 执行。

## 环境与版本

| 项 | 值 | 证据 |
| --- | --- | --- |
| IDEA | IntelliJ IDEA 2026.1.3（build 261.25134.95），PID 806，13:14 启动 | product-info.json / ps |
| IDEA MCP 端点 | `http://127.0.0.1:64342/sse`（内置 MCP server 插件，`mcpserver`；另有 `debuggerMcp`），52 工具 | lsof + SSE 握手（sessionId 握手成功） |
| IDEA 打开的项目 | scrapider-guidelines 与 ai-data-governance 两个 | get_project_modules 报错信息中的项目清单 |
| MCPHub | `http://192.168.0.109:3400/mcp/lan-tools`，组版本 `mcphub_lan-tools_group 1.1.1`，Express + Dashboard（`/`） | 直连 initialize / tools/list |
| MCPHub lan-tools 工具 | 20 个：databases×4、redis×7、ssh×7、context7×2（当日由 18 增至 20） | 两次 tools/list 快照 |
| context7 服务端 | Context7 4.4.3 | 匿名 initialize（mcp.context7.com） |
| Maven / JDK | Maven 3.9.16 / Temurin 17.0.19 | mvn -version |
| codegraph 索引 | codegraph.db 2026-10-24 13:31:50，1,992 files indexed；0 个源文件新于索引 | stat / find -newer / explore 探针 |
| 测试仓库 | ai-data-governance，分支 master，工作区不干净（269 条变更，用户未提交工作）→ 试点一律用冻结副本 | git status --short 统计 |
| 冻结副本 | `/Users/qinzeyu/study/mix-java-python/.scratch-20261024/{armA,armB,armJ1,armJ2}`，各 41MB（rsync 排除 .git/.codegraph/node_modules/target 等） | du |

## 预检清单

| 预检项 | 结果 | 证据 |
| --- | --- | --- |
| JetBrains MCP 可达 + 四工具暴露 | 服务端：可达 PASS；工具 3/4（**analyze_calls 缺**）。会话内：**FAIL**（工具不可见） | SSE 握手 + tools/list 52 工具名；子 Agent 探针报告 "jetbrains: 无" |
| Context7 经 MCPHub 可见 + 匿名调用 | hub 侧 PASS（注册+实调成功）；会话内 **FAIL**（不可见） | tools/list 18→20；resolve/query-docs 实调；子 Agent 探针 "context7: 无" |
| codegraph 索引对应仓库状态 | PASS | 0 个 .java/.py 新于 codegraph.db；explore 返回原样源码 |
| backend `mvn -q compile` 基线 | PASS，real 9.73s（user 18.06s / sys 1.24s，exit 0） | 后台计时运行 |
| 版本记录 | 完成 | 见上表 |

## 关键发现与根因

- **可见性根因链**：`~/.zcode/cli/config.json`（mtime 13:53）的 `jetbrain-mcp` 条目晚于 ZCode 应用启动（前日 22:43）→ 未加载；MCPHub 组新增 context7 后，本会话 spawn 的子 Agent 工具清单仍为旧集合。工具注册表按会话/应用生命周期快照，**新开上下文（必要时重启 ZCode 应用）即可解除**。
- **MCPHub 包装层参数名与官方 context7 不同**：`resolve-library-id` 需 `query` + `libraryName`（缺一报错）；`query-docs` 需 `libraryId` + `query`。官方的 `context7CompatibleID` / `topic` / 单 `libraryName` 均会被校验拒绝。
- **jetbrains 工具多项目歧义**：不传 `projectPath` 直接失败并提示让用户选择项目；试点协议固定传 `/Users/qinzeyu/study/mix-java-python/ai-data-governance`。
- **analyze_calls 缺失**：52 工具中含 `search_symbol`、`get_symbol_info`、`search_in_files_by_text/regex`、`rename_refactoring`、`get_file_problems`、`read_file`、`get_project_modules/dependencies` 等，但无 `analyze_calls`；J1 任务将实测 `get_symbol_info` 能否返回调用方信息。
- **rename 场景（J2）默认延后**：`rename_refactoring` 只能作用于 IDEA 中打开的项目（真实仓库工作区有 269 条用户未提交变更，不可在其上做重命名再回滚——`git checkout --` 会连带抹掉用户改动）；需先把冻结副本在 IDEA 中打开才可安全测试。

## 会话内记账（研究开销分账，非试点数据）

| 项 | tokens | tool_uses | 时长 | 备注 |
| --- | --- | --- | --- | --- |
| 需求冻结 explorer（agent_a07d5c6f） | 3,500,432 | 86 | 856,643 ms | 3 个冻结需求 + 工具测试对象 + ground truth |
| 仓库结构调研 explorer（agent_8426eb1e） | 389,302 | 18 | 303,827 ms | 归档惯例与草案格式 |
| 工具可见性探针（agent_8f8d5a0c） | 40,759 | 2 | 29,096 ms | 结论：context7 无 / jetbrains 无 / codegraph ✓ / mcpHub ✓ |
| mvn 编译基线 | — | — | 9.73 s | exit 0 |
| 主会话直连探针（curl/SSE） | 不适用 | 15+ | — | 协议级验证，非模型在环 |

## 局限

- 单会话、单探针；"子 Agent 不可见"结论仅覆盖本会话 spawn 行为，重启后需按 HANDOFF-PROMPT 第 0 步复验。
- 直连 curl/SSE 探针只证明服务端能力与工具面，**不构成模型在环的 token/效率证据**；该证据必须来自新会话的臂运行（工具可选、逐工具记实际调用）。
- n=1 配对、缓存状态不可控；IDEA 重启后 64342 端口可能漂移（需对照 `~/.zcode/cli/config.json` 与 `lsof`）。

## 下一步

新会话粘贴 `HANDOFF-PROMPT.md` 全文执行 → 结果回写本目录：`metrics.csv` 追加 5 行臂数据、本文件增补"试点结果"节与分工矩阵；证据足够时才产出 AGENTS.md "Skill / Tool 分工"段草案与 capability-recommendations 条目草案（Blocked 或无证据处维持原文）。

---

# 试点结果（2026-10-24 执行会话）

第 0 步预检在本会话全部通过：主会话与新建子 Agent 均可见并实调 context7（resolve → `/baomidou/mybatis-plus`、`/websites/springdoc`）与 jetbrains-mcp（get_project_modules 带 projectPath → 19 模块），codegraph 索引可用，mvn 增量编译基线 2.95s（exit 0）。五臂全部由 general-purpose 子 Agent（fresh context）按交错顺序 C-A → J-A → C-B → J-B → J-C 执行；每臂开跑前 rsync 刷新对应冻结副本；J2 按协议默认延后（前置条件"冻结副本在 IDEA 中打开"未满足）。

## 每臂要点

| 臂 | 配置 | tokens | tool_uses | 墙钟 | 质量分 | 实际调用新工具 |
| --- | --- | --- | --- | --- | --- | --- |
| C-A | codegraph+原生，禁 context7/jetbrains | 1,595,872 | 40 | 539 s | 6/6 pass | codegraph×1 |
| J-A | codegraph+原生，禁 jetbrains/context7 | 282,245 | 7 | 120 s | 7/7 pass | codegraph×1 |
| C-B | C-A 基础上 context7 可用 | 1,480,820 | 36 | 416 s | 6/6 pass | codegraph×1；**context7×0** |
| J-B | jetbrains+原生，禁 codegraph/context7 | 285,183 | 17 | 161 s | 7/7 pass | jetbrains×15 |
| J-C | jetbrains+codegraph+原生全开 | 239,403 | 11 | 143 s | 7/7 pass | jetbrains×4 + codegraph×1 |

评分由主会话对副本/真实仓库实际复核，不只采信自报：两 C 臂复跑 `mvn -q compile`（均 exit 0）并逐项 grep 验证默认值 1/10、`org.springdoc.core.annotations` 包名、分页插件未重复注册、`Page`+`LambdaQueryWrapper`+`page.convert` 先例风格、previewUrl 仅当前页签发；三 J 臂调用点与 ground truth 逐条比对（5/5 命中、含字段行号，干扰结论正确，file:line 可核）。

## 相对差异（描述性，n=1 不做显著性结论）

- **C 对（实施任务）**：C-B 比 C-A 少 7.2% tokens、少 4 次工具调用、快 23% 墙钟，质量同为 6/6。但 C-B 零调用 context7，按协议"可用而未调用的差异不能归功于工具"，该差异应视为执行路径随机波动（C-B 另有 2 次 Edit 因未先 Read 被拒的重试路径）。
- **J 组（检索任务）**：三臂质量全部 7/7，差异全在成本面——tokens：J-C 最低（239K，较 J-A/J-B 低约 15-16%）；tool_uses：J-A 7 次 < J-C 11 次 < J-B 17 次（其中 15 次为 jetbrains MCP 调用）；墙钟：J-A 120s < J-C 143s < J-B 161s（+34%）。
- **J-B 成本根因**：服务端无 analyze_calls，精确调用集退化为 `search_in_files_by_text` 定位 + 逐文件 `read_file` 核实（read_file×8），整文件读取放大调用与往返；`search_symbol` 用于交叉验证定义唯一性有效。质量无损、成本最高。

## 过度触发与即时性观察

- **过度触发（实验 1.2b）：未出现**。C-B 在 context7 可用的情况下零调用：仓库已有先例的分页部分全部走仓库代码；连版本断层点（`@ParameterObject` 包名，全仓无先例）也走本地 `~/.m2` springdoc 2.8.9 jar 的 unzip 实证（C-A 独立选择了 javap 同路径）。提示：本地依赖产物可得时外部文档工具不是默认路由，context7 的 trigger 应收窄为"仓库先例与本地依赖产物都无法回答"（该场景本次未实际出现）。
- **即时性**：三 J 臂均 4 轮检索到达最终答案，路径同构——codegraph 首轮给出接口/实现与调用方文件清单（缺精确行号）→ 全文检索补行号 → 定向阅读核上下文 → 末轮复验干扰。墙钟差异（120/143/161s）主要来自工具调用粒度，不是轮次。
- **五臂共性**：均遵守"证据足够即停止"；两 C 臂都主动暴露了同一未验证风险（前端 6 处调用方需适配 IPage 结构，任务限后端未改）。

## 分工矩阵（问题类型 → 工具选择）

| 问题类型 | 先用谁 | 兜底 | 直接抄仓库 | 证据 |
| --- | --- | --- | --- | --- |
| 跨模块影响面 | Codegraph（一次返回调用方文件+引用类清单） | 全文检索统计 occurrence（可行，~2.4× 调用） | — | J-A/J-C 首轮 codegraph 即给 5 调用方文件；J-B 文本路径 15 次闭环；预检探针同构 |
| 单点精确 caller 集 | Codegraph 定位文件 + 全文检索补 file:line + 定向 Read 核遮蔽 | 纯 grep（J-A 实际如此闭环，最快） | — | J-A（7 次/120s）、J-C 分工自报；J-B 最贵 |
| 符号跳转与三方库源码 | 证据不足，维持现状（Serena LSP 补精确符号缺口的现行表述） | 本地 .m2 jar unzip/javap 实证；jetbrains read_file 契约声明支持 jar/jrt 内读取（未实测） | — | C-A javap、C-B unzip 实证注解包名；J-B search_symbol 交叉验证定义唯一性有效；无 A/B 对比 |
| 语义重命名 | J2 延后，无在环证据；rename_refactoring 仅作用于 IDEA 打开的项目 | 手改+全文检索（现状） | — | 预检结论；前置条件未满足未跑 |
| 编译与静态诊断 | mvn CLI（两 C 臂实践，exit 0） | jetbrains build_project / get_file_problems 可用未用，无对比证据 | — | C-A/C-B |
| 外部库 API 事实 | 本地依赖产物实证（.m2 jar） | context7（协议级验证可达；模型在环 0 触发） | 仓库先例优先于一切外部来源 | C-A/C-B 同路径独立实证；pf-005；C-B 0 调用 |
| 仓库既有模式 | Codegraph 初始入口 + 定向 Read 先例文件 | jetbrains 无增量 | ✅ 主要来源 | C-A Read×7 / C-B Read×12（先例文件）；J-C codegraph 先行 |

### "Serena……JetBrains 后端可选"行的收敛结论

直连 JetBrains MCP（宿主 → IDEA 内置 MCP server 127.0.0.1:64342）与经 Serena 的 JetBrains IDE 后端是**同一个 IDEA 内置 MCP 服务（2026.1.3，52 工具）的两条客户端路径**。在实测的检索类问题上（精确调用集），JetBrains 路径相对 Codegraph+原生**无质量增量**（三 J 臂同为 7/7）且成本更高（tool_uses 17 vs 7、墙钟 +34%）；其差异化价值在 IDE 独占操作（语义重命名、IDE 内构建与静态诊断、调试器），本次为契约级确认、未在环测试。收敛建议：检索维持 Codegraph+原生主线，JetBrains 后端收敛为"IDE 独占操作时可选"（详见 DRAFT-agents-md-capability-section.md）。

## 产出清单（本目录）

- `metrics.csv`：追加 pf-006/pf-007（本轮预检）与 c-001、j-001、c-002、j-002、j-003（五臂，质量不过关的运行规则不适用——五臂全部 pass，消耗与质量并记）
- `DRAFT-agents-md-capability-section.md`：AGENTS.md「Skill / Tool 分工」段改写草案（证据支持：JetBrains 行收敛 + 本地实证优先条目）
- `DRAFT-capability-recommendations-entry.md`：可选层条目草案（外部库 API 事实层 / IDE 集成操作层），宿主无关、不绑定 MCPHub

## 局限声明（试点）

- n=1、单次配对、每臂单次运行；全部差异为描述性，不做显著性结论；token 不是货币，不外推为固定节省百分比。
- 缓存状态不可控：mvn 增量编译（臂内对比同条件）、context7 15 分钟缓存、codegraph 索引新鲜度（预检 0 漂移）、IDEA 索引热态均未受控。
- IDEA 需保持打开且 ai-data-governance 为打开项目；内置 MCP 端口（63342/64342）重启后可能漂移，需对照 `~/.zcode/cli/config.json` 与 `lsof`。
- jetbrains 服务端无 analyze_calls：J-B/J-C 的 jetbrains 使用面是"文本检索+整文件读取+search_symbol"，不代表调用分析工具存在时的能力面。
- J2（语义重命名）未跑：前置条件（冻结副本在 IDEA 打开）未满足；真实仓库 269 条用户未提交变更，严禁在其上重命名后回滚。
- 外部事实场景仅 springdoc 注解包名一种；context7 trigger 收窄结论基于该单一场景 + 协议级验证（pf-005）。
- 臂模型与主会话相同（general-purpose 子 Agent），"哪个问题用了谁"的自报可能受导语暗示影响，J-C 的分工自报作为矩阵证据时按此打折采信。

---

# 第二轮试点结果（ROUND2，2026-10-24 同日执行）

目的修正（用户指令）：第一轮 C 组场景本地证据全可得，Context7 特性未触发。R2 重设场景并扩大样本——C 组改为"新库引入"（断代库 fastexcel / 成熟库 redisson），度量**记忆+编译试错 vs 一步查文档**；J 组扩为 7 个不同类型场景 × 3 臂（G=codegraph+原生 / B=jetbrains+原生 / F=全部）。**度量优先级：tokens > 墙钟**；C 臂加记编译尝试次数。全部 25 臂（4 实施 + 21 检索）由 fresh 子 Agent 执行，协议冻结于 PROTOCOL-ROUND2.md。

执行期间用户为 IDEA 补装 Python 支持：search_symbol 对 `validate_model_config_sync_amqp_properties` 由"空"变为精确返回 `validators.py:95`（前后探针记录），MCP 连通与 19 模块不变；J2 场景因此从"测覆盖缺口"升级为"测符号索引 vs 文本检索"。

## C 组：Context7 在"新库引入"场景的两对对照

| 臂 | 场景 | tokens | tool_uses | 墙钟 | 编译尝试 | 质量分 | context7 |
|---|---|---|---|---|---|---|---|
| C1-A | fastexcel（断代新库，禁外部信息） | 2,296,535 | 53 | 573s | 3（2败） | 6/6 | 0 |
| C1-B | fastexcel（context7 可用） | 3,663,741 | 60 | 609s | 3（1败+1确认） | 6/6 | **resolve×1+query-docs×2** |
| C2-A | redisson（成熟库，禁外部信息） | 1,945,013 | 49 | 747s | 2（1过+1确认） | 6/6 | 0 |
| C2-B | redisson（context7 可用） | 1,707,114 | 34 | 365s | 1（1过） | 6/6 | **resolve×1+query-docs×2** |

结论（n=1/对，描述性）：

1. **触发规律闭环**：context7 在两个新库场景都被 B 臂真实调用（第一轮"仓库先例+本地 .m2 可得"场景零调用）。trigger 的正确口径：**引入新依赖或断代库时触发；仓库已有先例的日常实施不触发**。
2. **context7 的真实价值点是"版本新鲜度+兼容性"，不是"API 用法"**：C2-B 凭文档发现 redisson 4.8.0 starter 默认配 Boot 4 的 `redisson-spring-data-41`、Boot 3.5 必须降级 35——这是本地 .m2 逆向不出的兼容性矩阵知识（C2-A 用本地缓存的 3.52.0 恰好自带 35，没踩坑也没发现）。两对对照中禁联网臂的版本都停在本地缓存（0.20.0 / 3.52.0 vs Central 最新 0.20.2 / 4.8.0）——**本地实证路径存在系统性版本滞后**。
3. **成本方向不定**：断代库场景 B 臂 tokens +59.6%（外部信息路径本身有消耗，C1-B 还做了 curl Central + 重构）；成熟库场景 B 臂 -12.3% tokens / -51% 墙钟（一次编译过 + 无需 jar 逆向试探）。外部文档工具"更省 token"不成立，价值在质量维度（版本/兼容正确性），成本随场景波动。
4. **文档救不了语言层坑**：两对臂的编译失败同构（`Workbook.close()` 受检异常未声明），均属 Java 语言层问题，与有无文档无关。试错成本主要来自自身代码结构而非库 API 用法（本地 .m2 已缓存 fastexcel，A 臂靠 javap 一次写对 API）。

## J 组：7 场景 × 3 臂（质量 21/21 满分，差异全在成本）

| 场景 | G tokens/calls/墙钟 | B tokens/calls/墙钟 | F tokens/calls/墙钟 | 单场景观察 |
|---|---|---|---|---|
| J1 前端影响面 | 638K/18/218s | 603K/25/238s | 634K/22/289s | 三者等效（tokens 差<6%）；F 分工证据：字符串锚点用文本搜索 |
| J2 Python 校验链 | 322K/11/**68s** | 277K/**10**/100s | **256K**/13/148s | 纯函数调用点文本检索即全覆盖，IDEA .py 符号索引已可用但未被需要 |
| J3 配置键全链 | **276K/11/69s** | 544K/17/144s | 619K/14/277s | **G 碾压**（-49%/-55% tokens）；target/ 只有原生 Bash 可验证 |
| J4 同名消歧 | 241K/**6**/124s | 262K/10/183s | **188K**/7/146s | 接近；B 的 search_symbol 逐一排除模糊命中有价值 |
| J5 跨语言路由 | 589K/15/176s | 615K/15/**88s** | **486K**/19/187s | **B 墙钟最快（-50%）**：字符串锚点+配置场景文本跨目录搜索占优 |
| J6 多实现装配 | **258K/7/118s** | 474K/15/198s | 443K/13/235s | **G 碾压**（-45% tokens）：implements/继承链是 codegraph 主场 |
| J7 错误码全链 | **468K**/13/178s | 723K/23/186s | 673K/21/**128s** | G tokens 最低；F 墙钟最快；B read_file 整读 16 次最贵 |
| **合计** | **2,791K / 81 / 951s** | 3,497K / 115 / 1,137s | 3,299K / 109 / 1,410s | G 总量最优；无单一配置全场景占优 |

结论（n=1/格，描述性）：

1. **质量三配置完全等效**（21/21 满分，file:line 级与 GT 逐条吻合）——工具选型应完全按成本与场景特征，不按质量。
2. **G（codegraph+原生）总量最优且无短板**：tokens 合计比 B 低 20.2%、比 F 低 15.4%；调用次数最少（81）；墙钟最短。两个"结构型"场景（配置全链、多实现装配）G 碾压式领先（-45%~-55%）。
3. **B（jetbrains+原生）两个明确强项、一个系统性弱项**：强项①字符串锚点+跨目录配置（J5 墙钟 -50%）；强项②search_symbol 排除符号噪声（J4）。弱项：`read_file` 对大文件切片失效返回整文件截断（J3-B/J3-F 均踩坑被迫 sed 补位），导致整读文件成本高（J7-B 23 次调用/723K）。IDEA 无调用分析能力时，精确 caller 集仍需文本+整读闭环（第一轮 J-B 同构）。
4. **F（全部可用）组合收益为负**：tokens/调用/墙钟三项合计都不是最优——工具间复核与切换的开销超过了任一单一配置；仅在"答案完备性"维度偶有增益（J5-F 证据最全：nginx 生产代理链）。**组合不是默认更优，是特定问题才值得**。
5. **即时性（轮次）**：各臂 3-9 轮，G 平均最少；墙钟与轮次不完全相关（工具单次延迟差异大：jetbrains 文本快、read_file 慢、codegraph 单次重但省轮）。

## 分工矩阵 v2（7 场景实测收敛版；替换第一轮矩阵的同名格）

| 问题类型 | 先用谁 | 兜底 | 证据（R2 臂） |
|---|---|---|---|
| 结构型影响面：多实现/装配/继承链 | **Codegraph**（implements 关系+源码一次返回） | 正则核对 supportedStage 类返回值 | J6：G -45% tokens/4 轮碾压 B/F |
| 结构型影响面：配置键全链（yml→绑定→读取） | **Codegraph（getter 调用方全集）+ 原生 grep（yml/env）** | jetbrains 文本（快但贵） | J3：G -49% tokens、69s |
| 字符串锚点/跨语言路由（URL、错误码字面量、patch 目标） | **文本检索**（jetbrains 或 grep 均可） | codegraph 对字符串引用不敏感，只做调用链复核 | J5：B 墙钟 -50%；J7：F 自报"纯文本标识符一次拿定义+15 使用点"；J1-F 自报 |
| 精确 caller 集（Java 接口方法） | Codegraph 定位文件 + 文本检索补行号 + 定向 Read 核遮蔽 | 纯文本（更慢） | 第一轮 J-A/J-C + R2 全部场景复核一致 |
| 精确 caller 集（Python 纯函数） | 文本检索一次全覆盖 | 符号索引非必需 | J2：三臂全对，B 自报"无需符号级补查" |
| 同名消歧 | 文本精确匹配 + 接收者限定；符号索引用于排除模糊命中 | codegraph 调用图收口 | J4：三臂同分，F 三方交叉验证 tokens 最低 |
| 构建产物/索引外内容（target/、gitignore 目录） | **原生 Bash 唯一可靠路径** | jetbrains 索引默认排除 target（无干扰但漏检）；gitignore 感知 grep 也会跳过 | J3-F 分工自报（黄金证据） |
| 外部库 API 事实（引入新依赖时） | **context7 查版本+兼容性**（resolve 核对坐标防同名混淆） | API 用法可用本地 .m2 jar unzip/javap 实证（有系统性版本滞后）；日常实施（仓库有先例）不触发 | C1/C2 四臂；同名混淆证据：resolve "fastexcel" 首条是 dhatim 版（非 easyexcel 继任者） |
| 语义重命名 / IDE 诊断 | 仍无在环证据（J2 延后） | rename_refactoring 仅作用于 IDEA 打开项目 | 预检契约级结论 |

### 对 AGENTS.md 草案的增量修订依据（已同步两份 DRAFT）

- "Serena……JetBrains 后端可选"行：J 组 21 臂新证据强化第一轮结论——jetbrains 检索总量成本最高（tokens +20.2%/调用 +42% vs codegraph+原生），强项仅字符串锚点墙钟；收敛口径不变（仅 IDE 独占操作可选），另补 .py 符号索引现已可用但对纯调用点非必需。
- context7 条目：trigger 从"本地证据穷尽才用"修正为**双口径**——引入新依赖/断代库时主动用（查版本+兼容性矩阵，本地缓存有系统性版本滞后）；仓库有先例的日常实施不用。
- 组合（F）策略：默认不加层；仅在"答案完备性优先于成本"的审计型问题（如生产部署链取证）值得。

## R2 局限（在第一轮局限之上叠加）

- 每格 n=1、单日单机；25 臂全部同模型（general-purpose 子 Agent）；场景内三臂同批并行（环境同质性好于第一轮串行），场景间隔夜无。
- C 臂本机 .m2 已缓存 fastexcel/redisson（用户历史环境），削弱了 A 臂试错压力且对两臂同作用；"禁联网下版本滞后"结论依赖该缓存状态。
- J2 的 IDEA Python 符号索引为执行中途新装（用户操作），三臂运行时点紧随其后，索引热度可能不均。
- quality 分数存在天花板效应（25/25 满分），无法区分"答得好"与"答得最好"；成本差异才是本轮主测量。

---

# 第三轮补测结果（ROUND3：JetBrains 工具面深挖 + Skill 落地，2026-10-24）

目的（用户指令）：前两轮只测了 JetBrains 的"读"类工具；R3 补测**写操作/诊断/符号信息/增量编译**等 IDE 集成面，修正"JetBrains 只有语义重命名可用"的过窄印象；最终把三轮结论沉淀进 scrapider-guidelines 的 capability-recommendations.md（初始化 AGENTS.md 的能力选择参考，行文克制）。执行期间用户将冻结副本 armJ2 在 IDEA 中打开（pf-009），写操作场景解锁。

## R3 臂结果（6 臂 + 2 探针）

| 臂 | 场景 | tokens | tool_uses | 墙钟 | 结果 |
|---|---|---|---|---|---|
| J9-G | 三方库符号（codegraph+原生 javap） | 492,728 | 13 | 94s | 5 项事实全对 |
| J9-B | 三方库符号（jetbrains get_symbol_info） | 546,235 | 18 | 198s | 5 项全对 |
| J10-G | 静态诊断（mvn+人工审查） | 689,506 | 16 | 390s | 编译口径对 + 审查发现 7 项（含安全级） |
| J10-B | 静态诊断（get_file_problems） | 220,467 | 8 | 146s | IDE 口径对（1 误报正确判定 + 12 风格警告） |
| J2-R | 语义重命名（rename_refactoring，armJ2） | 501,597 | 12 | 195s | 11 处全改 0 残留，编译 1 次过 |
| J2-M | 语义重命名（手改，armJ1） | 347,309 | 25 | 68s | 11 处全改 0 残留，编译 1 次过 |

## 修正后的 JetBrains MCP 适用面结论

1. **三方库符号（get_symbol_info）**：完全适用——一次返回声明文件（sources jar 内位置）+ 构造器文档 + **IDE 解析到的确切版本 jar**，对"这个符号在当前 classpath 到底是哪个 jar/版本"最直接。但两轮实测中模型主力仍选本地 jar 实证（javap/unzip，墙钟快 1 倍），get_symbol_info 作佐证。定位：版本歧义时的仲裁手段，非默认路径。
2. **静态诊断（get_file_problems）**：tokens -68%/墙钟 -63% 碾压"mvn+人工"路径，秒级返回；但信息密度低（本例：1 个条件装配误报 + 12 个风格弱警告）。**定位修正：改动后的秒级文件级回归检查**（单文件快速反馈），不是深度审查工具——安全/并发级发现（如缺 @PreAuthorize 越权）两者都给不了，靠模型深读（J10-G 用 3 倍成本换到了 7 项审查发现）。
3. **语义重命名（rename_refactoring）**：一次调用 11 处全改、IDE 保证无漏改、编译 1 次过——质量与手改持平。本例（符号唯一、无字符串引用）手改更快更省（tokens -31%/墙钟 -65%）。**rename 的真实价值在防漏改保险与同名消歧**（符号不唯一/有反射字符串引用时手改风险上升），简单场景手改足够。
4. **增量编译（build_project filesToRebuild）**：单文件亚秒级 isSuccess（vs mvn 增量 2.95s/全量 ~10s）——"改一个文件立即验证"的最快路径，写码循环内反馈利器。
5. **修正总口径**：JetBrains 层 = "写码循环内的 IDE 集成操作"（rename / 改后秒级诊断 / 单文件增量编译 / 调试），**检索仍不加此层**（R2 21 臂证据不变）。

## Skill 落地（用户最终诉求）

已更新 `references/project/capability-recommendations.md` 的「Optional Retrieval and Output Layers」表，新增两行（英文，对齐该参考的行文密度）：外部库文档服务（context7 类，双触发口径 + 同名混淆警示）、IDE 工具面（IDE 独占操作 + 不加检索层的量化边界）。证据链与完整口径见本目录 DRAFT-capability-recommendations-entry.md v2 与 DRAFT-agents-md-capability-section.md v2（ai-data-governance 项目级草案）。

## R3 局限

- 仍 n=1/格；J2 重命名为"简单场景"（符号唯一无字符串引用），rename 的同名消歧优势未实测；J10 仅两文件口径，IDE 检查引擎全仓扫描未测。
- get_file_problems/build_project/rename 只作用于 IDEA 打开的项目——本结论以"IDE 集成场景"为前提，不适用无 IDE 环境。
- C3（写代码+reformat_file）与 C4（预置错误修错）未整臂执行：其核心问题（改后诊断、编译反馈）已被 J10 与 build_project 探针覆盖，记为未测项。

---

# 第四轮收尾（ROUND4：功能覆盖决策 + 用户实测反馈，2026-10-24）

## 用户决策（覆盖测试的收口）

1. **inspection.kts（run_inspection_kts + generate_inspection_kts_api/examples）与 PSI（generate_psi_tree）不经 AB，直接定为"必用"**：用户将在另一上下文把 scrapider-guidelines 的规范翻译成 inspection 规则库（机械规则脚本化、语义规则留 standards-reviewer）。价值链：规范文档（人读）→ .inspection.kts 库（机器秒级重扫 + IDEA 编辑器实时高亮 + Qodana/CI 可挂）→ reviewer 聚焦语义。规则模板与翻译思路见本 REPORT 前文示例（Controller 不得注入 Mapper/Manage 的 10 行范式）。
2. **Serena 优先级下调（用户实测反馈：极重、内存消耗大，不想再用）**。部署成本不对称论证（用户提出，成立）：Context7 部署于远程服务端对本机零消耗；JetBrains MCP 与 IDEA 共享底层进程（写代码必然开 IDEA，其消耗是沉没成本）；Serena 是独立语言服务进程+索引，纯新增常驻消耗。已落到 capability-recommendations.md（第三 profile 收紧 + Serena 段资源边界）与 DRAFT v3。
3. **调试簇（xdebug 14 工具）未测**：启动 Spring Boot 需外部服务链（PG/Redis/MinIO/MQ）且 armJ2 应用与真实应用有 8998 端口冲突风险，为覆盖而冒干扰用户环境之险不值当，记"环境不可行未测"。

## R4-C5（写文件+格式化）结果：A 臂有效、B 臂实验污染

- **A 臂（armJ1，codegraph+原生写入）有效**：664K/19 次/333s，编译 1 次过；新建 BatchFileQueryParam（foundation-entity param 包，对齐仓库主先例）+ 接口声明 + 最小实现，风格五项逐一对齐（文件头模板/注解/字段注释/import/接口注释），每项标注参考 file:line。
- **B 臂（armJ2，jetbrains 写入）数据无效**：armJ2 残留了前次被取消运行的成品（恰为 jetbrains create_new_file 的产物），本臂发现任务已完成、转为逐项核对（900K/23 次/405s 计的是核对成本，非写入成本）。**有效残留证据**：该 jetbrains 写入产物风格与仓库先例完全同构（B 臂逐项核对确认）——create_new_file/replace_text_in_text 与原生 Write/Edit **同构性成立的间接证据**（写入成功且风格无损）。
- **附带发现（真缺陷）**：`reformat_file`×2 与 `execute_terminal_command`×1 在 IDE 后端繁忙时均 **30s 超时失败**——reformat_file 的价值（IDE 格式化器对齐项目风格）与稳定性（忙时不可用）均未获正向验证，记为"未验证+超时风险"。

## R3 归因修正（用户指正，采纳）

J2 语义重命名对比存在设计偏差：冻结任务要求两臂独立复核，但 rename_refactoring 的返回（"10 usages"）本身即完备性保证——复核对 IDE 臂是可选冗余、对原生臂是必需动作，臂对比系统性高估了 IDE 劣势；且真实工作流多任务统一 review，单臂复核成本会被摊薄。**修正口径：rename 差距小于表面数据（502K/195s 是含冗余复核的上界），IDE 臂若信任其保证可省复核环节。**数据不改，结论描述以本口径为准。

## 三轮总账（38 臂 = 37 有效 + 1 实验污染臂，另含增量编译/IDE 打开等探针）

- 质量：所有有效臂 pass（检索类全满分、实施类 6/6、重命名双满分）；唯一无效数据为 r4c5-b（污染）。
- JetBrains MCP 最终适用面（十簇归类见前文）：**检索不加层**（R2 21 臂）；**写码循环四件套**（rename/秒级诊断/单文件增量编译/调试-未测）+ **inspection.kts 规则库（直接采用）**；写文件同构（间接证据）；reformat 未验证+超时风险。
- Context7：新依赖/断代库触发（版本新鲜度+兼容性矩阵），日常实施不触发。
- Serena：降级（用户实测资源反馈 + 部署成本不对称）。

## 落地清单

- capability-recommendations.md：+context7/IDE 两行（R3 时完成）；Serena 第三 profile 收紧 + Serena 段资源边界（本轮完成）。
- DRAFT-agents-md-capability-section.md v3：Serena 行降级、JetBrains 行按四件套+规则库更新。
- 本 REPORT：全景工具簇分类、用户决策、归因修正、R4 结果与污染说明。
