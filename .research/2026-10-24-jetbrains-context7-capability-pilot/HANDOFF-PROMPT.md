# 新会话执行提示词（把本文件全文复制粘贴给一个新 ZCode 会话即可开跑）

你是"JetBrains MCP / Context7 / Codegraph 能力对比试点"的执行会话。完整协议、冻结任务文本与评分标准在本目录 `PROTOCOL.md`，预检结论在 `REPORT.md`——**先完整读这两个文件再动手**。本提示词是执行指令。

- 工作目录仓库：`/Users/qinzeyu/study/skill/scrapider-guidelines`（遵守 scrapider-guidelines 规则；本仓库只新增/更新 `.research/2026-10-24-jetbrains-context7-capability-pilot/` 下的文件）。
- 真实测试仓库：`/Users/qinzeyu/study/mix-java-python/ai-data-governance`（**只读**，遵守其 AGENTS.md；改动只落在冻结副本 `/Users/qinzeyu/study/mix-java-python/.scratch-20261024/` 下）。

## 第 0 步：可见性预检（任一失败 → 在 REPORT.md 记 Blocked 并停止对应实验，不许顶替）

1. 列出你可用的全部 `mcp__` 工具名。要求存在：`mcp__mcpHub__context7-resolve-library-id`、`mcp__mcpHub__context7-query-docs`、任一 `mcp__jetbrain` 前缀工具。
2. 实调验证：
   - context7：`resolve-library-id` 传 `{"query":"MyBatis-Plus","libraryName":"MyBatis-Plus"}`（MCPHub 包装层要求两个参数都传；`query-docs` 传 `{"libraryId":"<上一步返回的 ID>","query":"<主题>"}`，官方参数名 context7CompatibleID/topic 不适用）。
   - jetbrains：任选一个工具（如 `get_project_modules` 或 `search_symbol`）调用，**必须带 `projectPath="/Users/qinzeyu/study/mix-java-python/ai-data-governance"`**（IDEA 双项目同开，不带必失败）。注意服务端 52 个工具中**没有 analyze_calls**，调用集分析需自行探索 `get_symbol_info`（file/line/column 定位）/ `search_symbol` / `search_in_files_by_text` 的适用性。
3. spawn 一个最小子 Agent（general-purpose）重复第 1 步，确认子 Agent 是否也可见这些工具：
   - 可见 → 五个臂全部用子 Agent 跑（独立 fresh context）；
   - 仅主会话可见 → 臂由主会话自身串行执行，记账标注降级（无 subagent_tokens，改记自报轮次）；
   - 都不可见 → 大概率 ZCode 应用未重启（`jetbrain-mcp` 配置 2026-10-24 13:53 才写入 `~/.zcode/cli/config.json`，早于它启动的应用读不到）：检查 IDEA 是否在跑、端口是否仍 63342/64342（`lsof -nP -iTCP -sTCP:LISTEN | grep idea`），然后请用户重启 ZCode 应用后重开会话；仍不可见 → 记 Blocked 结束。**不要自行修改全局配置。**
4. 环境自检：`mvn -q -f /Users/qinzeyu/study/mix-java-python/ai-data-governance/backend/pom.xml compile`（基线约 10 秒）；codegraph explore 一次确认索引可用（projectPath 指真实仓库）。

## 第 1 步：五个臂（交错顺序执行；臂 prompt 逐字取自 PROTOCOL.md，禁止改动任务文本，禁止把评分点/ground truth 带入任何臂 prompt）

顺序：**C-A → J-A → C-B → J-B → J-C**

- C 臂（实施类，各自副本）：任务文本 = PROTOCOL.md「冻结任务 C-task」引用块逐字；导语 = 对应臂导语（C-A / C-B）；`{COPY}` 分别替换为 `/Users/qinzeyu/study/mix-java-python/.scratch-20261024/armA`（C-A）与 `.../armB`（C-B）。开跑前先刷新对应副本：
  `rsync -a --exclude=.git --exclude=.codegraph --exclude=node_modules --exclude=target --exclude=.venv --exclude=__pycache__ --exclude=.mypy_cache --exclude=.ruff_cache --exclude=.pytest_cache /Users/qinzeyu/study/mix-java-python/ai-data-governance/ <副本路径>/`
- J 臂（只读检索，对象为真实仓库）：任务文本 = PROTOCOL.md「冻结任务 J1-task」引用块逐字；导语 = J-A / J-B / J-C。
- 每个臂返回后立即记录：宿主 usage（subagent_tokens / tool_uses / duration_ms）+ 臂自报的工具调用表；先评分，再跑下一个臂。

## 第 2 步：评分与记账

- 评分标准与 ground truth 在 PROTOCOL.md 各任务的「评分点」节。
- 每臂一行追加 `metrics.csv`（表头已就位，字段勿改）；质量不过关的运行也保留全部消耗。
- 重点观察：C-B 是否对"仓库已有先例"的部分误触 context7（过度触发=路由缺陷）；J-C 自述的"哪个问题用了谁、为什么"（分工矩阵直接证据）；各臂到达首个正确答案的轮次与墙钟（即时性）。

## 第 3 步：产出（全部写入 `.research/2026-10-24-jetbrains-context7-capability-pilot/`）

1. `metrics.csv` 追加 5 行臂数据；
2. `REPORT.md` 增补 `## 试点结果`：每臂要点表（tokens / 工具调用次数 / 墙钟 / 质量分 / 是否实际调用新工具）、相对差异（描述性，n=1 不做显著性结论）、过度触发与即时性观察；
3. `## 分工矩阵`：问题类型（跨模块影响面 / 单点精确 caller 集 / 符号跳转与三方库源码 / 语义重命名 / 编译与静态诊断 / 外部库 API 事实 / 仓库既有模式）→ 先用谁 / 兜底 / 直接抄仓库，**每格注明证据来源（哪一臂或哪次探针）**；
4. 两份草案（仅当证据支持时产出，无证据的格维持原文并说明）：
   - `DRAFT-agents-md-capability-section.md`：面向 ai-data-governance `AGENTS.md`「Skill / Tool 分工」段的改写草案，必须给出 "Serena……JetBrains 后端可选" 一行的收敛结论（直连 JetBrains MCP 与经 Serena 的 IDE 后端是什么关系）；
   - `DRAFT-capability-recommendations-entry.md`：按 `references/project/capability-recommendations.md` 的 profile 表格式（trigger / skip / 职责 / handoff），宿主无关、不绑定 MCPHub；
5. 局限声明：n=1、单次配对、缓存状态不可控、IDEA 需保持打开、内置 MCP 端口重启可能漂移。

## 边界

- 真实仓库只读；改动只在冻结副本；不重建索引、不安装工具、不改全局配置（含 `~/.zcode/cli/config.json`）；不做任何 git 提交/推送（归档文件写盘即可）。
- J2（语义重命名）默认延后，前置条件见 PROTOCOL.md（需先把冻结副本在 IDEA 中打开），不满足不跑。
- 全程遵守 scrapider-guidelines 与两个仓库的 AGENTS.md；面向用户的输出用中文。
