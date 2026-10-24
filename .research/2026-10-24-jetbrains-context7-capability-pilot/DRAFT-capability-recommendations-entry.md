# 草案：capability-recommendations 可选层条目（宿主无关）

> 依据：同目录 PROTOCOL.md（第一轮 5 臂）+ PROTOCOL-ROUND2.md（第二轮 25 臂，2026-10-24）。格式对齐 `references/project/capability-recommendations.md` 的「Optional Retrieval and Output Layers」表。
> 性质：草案条目，供该参考文件增补可选层时采用；不绑定 MCPHub 或任何具体宿主。
> v2 修订：R2 证据推翻了 v1 的"本地实证优先"单一口径——本地依赖产物实证存在**系统性版本滞后**且无法提供兼容性矩阵。

## 条目 1：外部库 API 事实层（external library API facts）

| Optional combination | When it helps | Boundaries and fallback |
| --- | --- | --- |
| 已选检索 profile + 一个外部库文档服务（如 context7 类 docs 查询） | **双触发口径**：① 引入新依赖（仓库无先例）或使用断代/新版本库时——查"最新稳定版本 + 与当前框架版本的兼容性矩阵"（实测：redisson 4.8.0 starter 默认配 Boot 4 适配模块，Boot 3.5 项目必须降级 spring-data 适配，此知识本地依赖产物逆向不可得）；② 版本敏感且本地证据不可得时 | 仓库已有先例的日常实施**不触发**（实测可用时零调用）。API 用法核对可用本地依赖产物实证（unzip/javap 解包本地仓库 jar），但该路径有系统性版本滞后（实测两对对照中禁联网臂版本分别停留在本地缓存旧版 0.20.0/3.52.0，而 Central 已有 0.20.2/4.8.0）。resolve 查询时**必须核对库坐标/描述**：同名库混淆真实存在（实测 resolve "fastexcel" 首条返回 dhatim 的同名 XLSX 库，而非 easyexcel 继任者）。语言层坑（受检异常等）文档救不了，靠编译验证 |

## 条目 2：IDE 集成操作层（IDE-exclusive operations）

| Optional combination | When it helps | Boundaries and fallback |
| --- | --- | --- |
| 已选检索 profile + IDE 的 MCP 工具面（如 JetBrains IDE 内置 MCP） | 仅用于 IDE 独占操作：语义重命名（仅作用于 IDE 当前打开的项目）、文件级静态诊断/问题列表、构建触发、调试器会话 | 检索类问题不加此层。R2 七场景 × 3 臂实测：质量 21/21 三配置完全等效，jetbrains 检索总量成本最高（tokens +20.2%、调用 +42% vs 图服务+原生），系统性弱项是文件读取工具对大文件切片失效（返回整文件截断，实测多臂被迫改用 shell 行截取）。两个窄强项：字符串锚点/跨目录配置检索墙钟最快（-50%）；符号搜索可排除同名/近名噪声。多项目同开时调用必须显式传 projectPath；无调用分析能力时精确 caller 集退化为文本+整读 |
| 组合用法（全部工具同开） | 仅"答案完备性优先于成本"的审计型问题（如生产部署链取证需要 nginx/compose/docs 多源证据） | R2 实测组合收益为负：tokens/调用/墙钟三项合计均非最优，工具间复核与切换开销超过单一配置。组合不是默认更优，是特定问题才值得 |

## 证据与局限

- 条目 1：C1/C2 四实施臂（fastexcel 断代库 / redisson 成熟库，各 A/B 对照，质量均 6/6）+ 第一轮 C-B（有先例场景零调用）+ 协议级验证 pf-005。断代库场景 context7 臂 tokens +59.6%（外部信息路径有成本），成熟库场景 -12.3%/-51% 墙钟——**成本随场景波动，价值在版本/兼容正确性维度**。
- 条目 2：R2 J 组 7 场景（前端影响面 / Python 校验链 / 配置全链 / 同名消歧 / 跨语言路由 / 多实现装配 / 错误码全链）× 3 臂，全部由 fresh 子 Agent 执行，质量 21/21 满分，file:line 级评分主会话权威复核。
- 结构型问题（配置链、多实现装配/继承链）图服务+原生碾压（-45%~-55% tokens）；纯函数调用点文本检索即全覆盖，符号索引非必需（IDEA Python 符号索引实测可用但未被需要）。
- 均为 n=1/格、单日单机的描述性结论，不构成固定节省百分比或排名；token 不是货币。
