# 可逆结构化编码与热门压缩项目补充核验（2026-10-18）

人工阅读的开发者研究补充，不加入 Skill 自动加载路径。本轮没有安装、配置、运行压缩器或发起模型对照实验。用户已排除普通输出过滤、额外工作指令和重型 Context Mode；本补充专门区分数据编码、模型压缩、请求代理三个技术层次。

**第一种机制到底减少什么。**

均匀 JSON 对象数组会在每条记录重复字段名和对象结构。列式表示把字段名声明一次，然后保留每行的全部值。节省来自数据表示中的重复结构，不来自删除记录或让另一个 LLM 摘要。Token 由 tokenizer 决定，不能按字符、字节或字段次数计算实际节省。

安全接入应在模型可见工具结果形成前，执行：解析支持的数据格式；生成 raw、compact JSON、列式等候选；按完整数据模型验证往返一致；计入解释格式的必要指令；比较目标 tokenizer；无收益或不支持时回退原文；保留原始结果与恢复位置。工具协议外层、错误、分页和独有元数据不能随意删除。若宿主同时把原始 structuredContent 和编码后的文本放入上下文，编码可能反而增加 Token。

往返一致只证明数据内容可恢复，不证明模型理解或完整 coding task 质量不变。还需核验 null/缺失、布尔值、数字精度、空字符串、分隔符、嵌套与字段关联等边界。Parsed JSON 值等价也不等于源文件字节等价；需要原文格式或行号时应读取原件。

后续规范复核进一步收紧“无损”的适用范围：[TOON 官方规范 §2/§3](https://github.com/toon-format/spec/blob/main/SPEC.md)允许实现对数值域外的输入采用近似值或字符串，且规定数字规范化和宿主类型归一化。因此，不可仅凭 TOON 的名称承诺所有输入值、类型、原始字面量均不变。Java 长整数 ID、高精度 Decimal、负零及业务依赖的小数位数应单独验证，无法保持原契约时透传原文。往返校验的基准必须是未经有损解析或宿主归一化的原始数据；不能先把原文解析丢精度，再对这个结果做一致性校验。模型理解质量还要另作同任务对照，没有一个项目的可逆标签可以取代这一步。

此机制最适合大量同构 JSON、JSONL、表格型结果。一个 MCP 结果若只有很短的 JSON 外壳和很长的源码字符串，压缩外壳的收益很小；普通 Java/Python 源码也不能直接套用表格编码。格式说明、回取、推理与修复开销均会影响完整任务净收益。

**热度与成熟度快照。**

以下为本轮查询 GitHub 页面显示的近似 stars，不是增长速度、审计或效果证明。

| 项目 | Stars 显示值 | 许可 | 类型与判断 |
| --- | ---: | --- | --- |
| [TOON](https://github.com/toon-format/toon) | 25.5k | MIT | JSON 数据模型的紧凑格式、SDK 和 CLI；最直接解释结构化可逆编码，适合先测 |
| [Headroom](https://github.com/headroomlabs-ai/headroom) | 75.6k | Apache-2.0 | 请求代理/SDK/中间件/MCP；覆盖更广，但同时包含选择、摘要、缓存回取等非纯可逆编码机制，不能整体称为无损 |
| [LLMLingua](https://github.com/microsoft/LLMLingua) | 6.8k | MIT | 研究型模型压缩库；利用小模型/token 分类删减输入，增加本地模型推理，不能保证编码 Agent 不退化 |
| [context-compression](https://github.com/saminkhan1/context-compression) | 4 | MIT | 小型早期选择器，适合参考接口和验证规则；不能按热门成熟产品推荐 |

Headroom 的 canonical repo 使用 `headroomlabs-ai/headroom`。旧的 `bigdatasciencegroup/headroom-token-compression` 页面当前标注为 fork，不把它当成主仓重定向。

**TOON：可验证数据表示，不是普适省 Token 开关。**

[官方 README](https://github.com/toon-format/toon)说明均匀对象数组和 keyed tabular 形式共享字段列表，并提供确定性的编解码。项目自己的计数基准用 `o200k_base`，不等于所有 provider 的实际 usage，也不是端到端 coding task。

更公平地对比 compact JSON：混合结构总量 TOON 264,734、compact JSON 260,451，TOON 多约 1.6%；flat-only 轨道比 compact JSON 少约 35.2%，但比 CSV 多约 5.9%。这些是上游特定数据与 tokenizer 的结果，不能作为本项目预期。

[理解基准](https://github.com/toon-format/toon/tree/main/benchmarks)覆盖 244 个问题、4 个模型。README 的混合轨道准确率为 72.2% 对 compact JSON 69.0%，同时说明置信区间重叠，不能宣布准确性优胜。读取查询基准不代表源码修改、异常处理或补丁生成质量。[Issue #207](https://github.com/toon-format/toon/issues/207)也涉及另行评估生成的问题。

**context-compression：具体选择器实现的参考。**

[源码入口](https://github.com/saminkhan1/context-compression/blob/main/selector.py)读取源文件、取得模型计数 profile、选择候选、生成 sidecar 并可校验报告。README 明确选择规则在 `round_trip(candidate) == parsed_source_value` 下比较 `decoder_instructions + candidate`，默认 safe 候选包括原文、compact JSON、columnar JSON 和带 JSON cells 的 CSV/TSV。

该项目 headline 的 13.3% 来自 28 个结构化文件的离线原始文件基线，并使用 advanced 候选；不是默认 safe hook 已测得的完整任务收益，也没有真实模型答案一致性证据。[上游报告](https://github.com/saminkhan1/context-compression/blob/main/reports/benchmark-report.md)

其自述 hook 主要覆盖整文件 Bash 读取，MCP 提供显式 stdio 工具，不会自动接管全部工具结果。宿主适配声明需在实际版本核验，不能从 README 推断所有桌面端都已支持。

**Headroom：热门的请求代理层，但不是纯编码器。**

[架构及 README](https://github.com/headroomlabs-ai/headroom)将内容路由到 JSON、代码、文本等压缩路径，提供代理、SDK 与中间件。它处理模型请求中的内容，因此 API 代理层原则上不需要直接挂载源码文件；这与把读文件/执行命令的 MCP 放到远程不同。远程代理与具体宿主的端点/鉴权接入仍未在本轮验证。

[CCR 文档](https://github.com/headroomlabs-ai/headroom/blob/main/docs/content/docs/ccr.mdx)明确展示 1000 items 压成 20 items 后保存原件并注入 retrieval tool 的路径。这里的可恢复依赖缓存与后续调用，不等于当前 prompt 保留了全部信息。取回成本会进入后续模型 usage。[Issue #2616](https://github.com/headroomlabs-ai/headroom/issues/2616)报告 headroom-ai 0.32.1 的 30 分钟 TTL 在长会话中导致取回失败；这属于历史版本/配置的用户报告，不能据此断言当前所有模式仍有同样故障。

不同启动方式、profile 与版本的默认策略不同；`wrap` 还会默认安装/注册 Serena code-memory，可用 `--code-memory none` 排除。它也包含 memory 和输出缩减等其他机制。[proxy 文档](https://github.com/headroomlabs-ai/headroom/blob/main/docs/content/docs/proxy.mdx)是后续选择最小实验配置的起点，不在本轮直接执行一键安装。

[Dasein 的第三方 coding benchmark](https://github.com/daseinlabs/code-compression-bench)对 100 个 SWE-bench Verified 任务报告 Headroom 输入 329.6M、baseline 312.2M（约多 6%），通过数为 58/100 与 57/100。其统计来自特定 2026-07-04 批次、headless Claude Code 与 Sonnet 4.6；由赞助方运营，部分历史版本信息未记录，不能当作中立普适排名或当前配置的结果。它只提醒：局部压缩不保证全任务净收益，不应引用 README 比例替代自己的配对实验。

**LLMLingua：研究成熟度高，但改变信息保留策略。**

[微软项目说明](https://www.microsoft.com/en-us/research/project/llmlingua/)与[源码](https://github.com/microsoft/LLMLingua/blob/main/llmlingua/prompt_compressor.py)描述由小模型识别和删除低重要性 token，LLMLingua-2 使用 token 分类模型。它需要模型权重与本地推理资源，不是列式编码；压缩器的运行成本也需单列。

[Transparency FAQ](https://github.com/microsoft/LLMLingua/blob/main/Transparency_FAQ.md)说明质量限制。项目的高压缩率与研究基准不能证明在同模型、同验收的 Coding Agent 上没有能力下降。本轮把它保留为有损算法参考，不作为优先部署候选。

**后续判断。**

若继续实验，先用纯结构化数据比较原始表示、compact JSON、适用时的 CSV、TOON/列式编码；使用同一模型和固定查询/聚合/边界要求，先检验完整往返和理解质量，再比较整个任务 usage。固定格式说明、回取与所有额外轮次一并计入。普通源码任务和不规则 JSON 作为负面对照，允许选择原文。

Headroom 若另行评估，只测一个冻结的最小 profile，排除未授权附加安装、记忆、额外检索与输出缩短；把“直接可逆编码”和“缓存后按需回取”分开记账。此时才有条件判断更大的覆盖范围是否带来净收益。

本轮没有本机 tokenizer 库，因此没有生成本地 Token 数，也没有以字符/字节估算填充结果。上述比例均明确归属上游研究或第三方报告；两名只读 Luna/max 研究 Agent 的关键结论经过主线页面与源码复核。
