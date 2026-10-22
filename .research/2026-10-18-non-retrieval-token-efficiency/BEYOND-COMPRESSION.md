# 跳出输入压缩：ZCode / Codex 的进一步候选（2026-10-18）

供人工阅读的开发者研究，不进入 Skill 自动加载。本轮只调研与设计；没有安装、配置、改业务代码、创建测试、运行模型实验或 Git 写操作。用户使用 ZCode 与 Codex，订阅额度与 API 都用，接受高频任务中可积累的小幅收益。本文替代初始报告的后续推荐顺序。

**筛选标准改为：减少重复生成、重复推理或失败后重做。**

| 方向 | 预期减少什么 | 值得试的前提 | 不应宣称的效果 |
| --- | --- | --- | --- |
| 语义重构 / 规范驱动代码生成 | 机械代码的模型 output、逐处修补的推理与模型往返；较少往返还可能减少重复计入的 input | 有可复用变换或既有规范；当前正常工作流确实还由模型逐处完成 | 工具能修改代码不等于整项任务已经省 Token；脚本生成、recipe 调整、review、失败恢复与全部验证都要计入 |
| 原生推理状态连续性 | 工具调用或连续阶段之间，重新建立分析与方案的 reasoning output；可能减少修错往返 | 所选模型支持，实际客户端或网关存在状态丢失，或者跨阶段模式有可测差异 | 不是 prompt-cache 折扣；输入可能增加，最终仍须比较整个任务 input + output |
| 条件候选：断流后取回同一次响应 | 网络异常时重新提交生成造成的重复 input/output | 长响应、可控制 API 调用、实际存在重生成；上游提供响应 ID 和恢复协议 | 网络正常时没有可预设收益；不能把 WebSocket 本身的加速当 Token 节省 |

**一、直接执行代码变换。**

把模型需要决定的内容与重复执行的内容分开：模型判断目标 API、类型、契约和例外，由语义工具匹配并改写全部适用位置，模型检查结果与异常，完成原有验证。源码无需被摘要或改成特殊压缩文本。最终文件和完整 diff 都保留，工具漏改或改错时可以检查并回退相应 patch。

候选应优先使用已经存在的 formatter、lint autofix、IDE/LSP code action、项目生成器或免费核心变换。需要新建自定义 codemod 时，它的编写和调试成本属于方案，不是免费准备。对于几处修改、业务逻辑设计、复杂算法修复，编写一个通用变换往往比直接改代码贵。

可落到三个不同的执行入口，按实际任务选择，不要求全部引入：

- Java：OpenRewrite 免费核心的类型/方法变换，例如 `ChangeType`、`ChangeMethodName`，通过本地 Maven 插件执行。需要正确 classpath 和类型归属；不能声称它自然覆盖反射、外部配置或所有动态引用。首轮依赖解析、构树和执行的内存/耗时也要报告，不需要常驻 MCP。仅复用已存在的规则优先；复杂新 recipe 的开发成本必须算入。[核心与 recipe 许可边界](https://docs.openrewrite.org/licensing/openrewrite-licensing)。
- Python：已有 Ruff 的 safe autofix 处理其覆盖的诊断；LibCST 可在语法树层批量变换，并提供 diff、成功/跳过/失败结果。LibCST 本身不证明业务等价，解析失败、动态名称和未处理文件都必须暴露。自定义 codemod 只有足够多的同类改动才可能摊平成本。[Ruff 修复安全性](https://docs.astral.sh/ruff/linter/#fix-safety)、[LibCST codemod](https://libcst.readthedocs.io/en/latest/codemods.html)。
- 新接口/SDK：已有 OpenAPI 契约与固定模板时，Apache-2.0 的 OpenAPI Generator 可以生成 Java/Python 客户端与服务端桩。它替代重复 DTO、协议映射和桩代码的模型生成，不替代事务、鉴权与业务决策；模板不适配或需要从零另造规范时，可能没有净收益。[官方支持范围与源码](https://github.com/OpenAPITools/openapi-generator)。

许可必须逐模块核对。OpenRewrite 的 Java 核心与额外语言、Spring recipes、Moderne 服务不能混为一个许可；本轮不把需要商业许可或额外源可用许可的功能列为默认前提。自动修复也不自动等于语义无损：Ruff 官方区分 safe/unsafe，safe 仍可能随整条语句删除注释，unsafe 可改变运行行为。预先固定允许规则与文件范围，并保留全部诊断和 patch。这里的 CLI/LSP 是执行机制；是否已经安装、是否暴露了所需 code action 仍需在后续实验前核对，不能把“工具名存在”算为实际调用。

**二、保留模型原生推理状态。**

可见对话历史与模型推理状态不同。OpenAI 官方说明，正确回传工具调用前的 reasoning items，可以避免函数返回后重新开始推理；这与删除信息、摘要历史或降低 reasoning effort 是不同机制。当前文档支持存储响应关联，也支持原样回传不透明的 encrypted reasoning items。[推理连续性](https://developers.openai.com/api/docs/guides/reasoning#preserve-reasoning-across-calls)、[官方最佳实践](https://developers.openai.com/api/docs/guides/reasoning-best-practices#how-to-keep-costs-low-and-accuracy-high)。

同一工具循环内的正确回传应先作为协议完整性检查。跨用户轮次的 `reasoning.context: all_turns` 是另一项条件实验：仅用于支持的模型，并检查响应返回的有效模式。旧推理可能固化过时假设，不能对所有会话强开。加密状态不是可供人工阅读的证据，源码、工具结果与验证记录仍须完整保留；状态丢失后也不能从文字摘要恢复同一原生推理状态。

当前 Codex 官方要求自定义网关支持 Responses 协议和完整 continuation/tool loop，因此不能把“改用 Responses”作为 Codex 的新增优化；重点是已有路径是否被中转网关破坏。ZCode 支持兼容 provider，但不能仅凭“OpenAI compatible”认定 reasoning state 正确往返；官方配置文档也指出 GLM `clear_thinking` 当前没有可配置入口，不能建议往配置里随意加字段。[Codex 网关协议](https://learn.chatgpt.com/docs/enterprise/gateway-compatibility)、[ZCode 配置能力边界](https://zcode.z.ai/en/docs/configuration)。

GitHub 有 ZCode v3.11.2 用户报告自定义网关路径偶发协议切换、空 reasoning 字段和整轮失败；它属于特定版本、模型和网关的使用者报告，尚不能证明当前版本普遍有问题，更不能证明用户的安装存在该问题。[报告 #676](https://github.com/zai-org/feedback/issues/676)。历史官方模型案例也不能直接换算为当前模型的节省比例。本轮没有在用户宿主上实测。

**三、只在异常频繁时验证响应恢复。**

OpenAI background + stream 支持按 response ID 与 `sequence_number` 恢复同一次后台响应。若旧流程在断流后新建生成，取回原响应可能避免重复生成；若已有恢复，增益为零。该功能属于 API 协议，不是 ZCode/Codex 任意 Hook 都能打开的设置；后台模式有额外首事件延迟与临时状态维护，不对普通短任务默认启用。必须保证工具调用只执行一次，按 call ID 去重，并保留原始事件与最终响应。[官方恢复协议与限制](https://developers.openai.com/api/docs/guides/background#streaming-a-background-response)。

**不再主推的方向。**

- TOON 作为结构化数据的专项选择保留，不能覆盖日常源码与方案工作；不以它作为这轮主线。
- 不用通用语义压缩器处理源码、需求、验收和关键日志。可取回原文不等于模型一定会识别漏掉的信息。
- 精确 LLM 响应缓存确实可能完全跳过上游请求，但多轮编码每次历史和工具结果都变，通常没有相同请求。无工具的重复只读问答才可能受益；仓库状态未编码进请求时，完全相同的请求也可能已经过期。不能重放有副作用的工具调用，也不能用“语义相似”命中代替完全一致。[LiteLLM 官方机制与 agentic traffic 限制](https://docs.litellm.ai/docs/proxy/caching)。
- ZCode 的 PostToolUse 只能追加模型上下文，不能替换原始输出；把压缩结果追加上去会重复。Codex 的阻止/替换反馈也有控制流含义，不能当通用透明压缩接口。PreToolUse 改写命令与接入请求代理是不同能力。[ZCode Hooks](https://zcode.z.ai/en/docs/hooks)、[Codex Hooks](https://learn.chatgpt.com/docs/hooks)。
- 原生补丁编辑、工具 schema 延迟加载、程序化工具调用若已经生效，保留它们，不能再算成新方案的收益。普通构建缓存和工具缓存主要省执行时间，不自动省模型 Token。

**小试只比较当前正常流程与实际生效的候选。**

每个可接入的方向先做有利/简单各一对 A/B，即四次完整任务。项目快照、任务、模型、有效推理强度与验收先冻结；两个宿主分别比较，不把宿主差异当方案收益。通过机制和质量检查后，再补有利案例的重复运行。没有接入点或当前已经覆盖的方向，记录“不适用/无新增机制”，不编造一组故意退化的 A。

| 方向 | 有利案例 | 简单/反例 | 必须确认实际生效 |
| --- | --- | --- | --- |
| 代码变换 | 同一类型/API 契约在多个 Java 模块中迁移，或既有规范下更新 Java/Python 客户端 | 单文件少量修改；没有现成变换；同名符号与字符串/反射引用的例外 | 实际执行了指定版本的变换，产出 patch；同时报告模型原本仍需处理的例外 |
| 推理连续性 | 同一目标跨多个工具步骤、分析后继续实现与验证 | 单步任务；后续用户纠正前提的任务 | 真实请求/响应的状态关联、完整 reasoning item 往返、支持模式的有效值；不记录原始私密推理文本 |
| 响应恢复（条件） | 既有流程的长响应中途断流 | 相同任务无断流；原生已能恢复 | 恢复仍是同一 response ID，事件不重复消费，工具副作用不重复，没有第二次生成 |

先按原验收评质量：功能、完整性、例外、全部必要证据与既定验证均保留；代码变换用完整 diff、已有测试与编译等确认，不能因为是生成器就免 review。推理复用增加“前提改变后仍能修正”的验收；恢复验证完整终态及操作恰好一次。未通过样本保留全部消耗。

计量遵循主报告的完整规则：input、cached input、uncached input、output、total、耗时、轮次、质量；reasoning output 若可得单列为 output 的子集，不再次相加。主/子 Agent、方案内脚本与恢复开销计入任务；一次性准备、调度、独立评估单列。通过 provider response ID 去重，累计 usage 不重复求和；缺失留空。缓存响应可能携带旧 usage，须用上游实际请求核对。Token、API 金额和订阅额度分别报告，不按固定比例互相推算。

**现成 MCP 的后续补查：存在可接入项目，尚无本项目净收益结论。**

2026-10-19 实测更新：[Token Optimizer MCP 小试](../2026-10-19-token-optimizer-mcp-pilot/REPORT.md)确认场景说明可触发自主调用，但本次工作流未节省 Token；7.5.1 默认差异读取还会漏报 Python 缩进与字符串空白的语义变化。下列条目保留为测试前的候选记录，不应解读为当前采纳建议。

- [Token Optimizer MCP](https://github.com/ooples/token-optimizer-mcp)：MIT；此前候选遗漏。作者提供 Codex MCP/插件接入说明及通用 stdio 入口，未找到专门的 ZCode 适配声明。其 [工具说明](https://github.com/ooples/token-optimizer-mcp/blob/master/docs/TOOLS.md)包含 `smart_read`（首读全文、后续按差异返回，`diffMode` 可关闭）、文本存取与日志处理，因而覆盖源码与日志。新会话、上下文压缩后仍否持有基线、缓存失效与完整读取回退必须测试，不能只验证同一会话的热缓存。
- 必须分开评估它的显式 MCP 工具、宿主 Hook 与请求代理。README 的部分压缩结果来自代理；不能推算只安装 MCP 也有相同效果。作者发布的 [Codex 对照记录](https://github.com/ooples/token-optimizer-mcp/blob/master/bench/live/evidence/joint-confirmation-2026-09-16/README.md)也明确未通过完整研究优越性门槛，且比较对象包括 HeadRoom；它不能证明相对用户正常流程普遍节省。工具描述的目标比例和本地 tokenizer 计数不能替代真实全任务 usage。本轮只核对公开实现说明与作者记录，未安装运行或完成源码安全/正确性审核。
- [MCP Slim](https://github.com/dopatools/mcp-slim)：MIT 的现成工具发现代理；适用于大量工具 eager loading。当前 Codex 会话已有延迟发现时，不默认叠加；通用 stdio 兼容不等于已验证两个宿主的自动安装和实际收益。
- [Context Mode](https://github.com/mksglu/context-mode)：此次当前 README 明示 ELv2（source-available），应与 MIT/Apache 意义上的开源区分；用户已排除其重量，不继续主推。
