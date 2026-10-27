# JetBrains MCP 工具白名单调研

日期：2026-10-27（Asia/Shanghai）。状态：审核完成，用户批准仅开启十项，调试全部关闭。

最终配置、正式文档修改和当前会话十项工具的验证结果见 [最终应用记录](approved/RESULT.md)。
以下正文保留审核前二十二项提案及原始实验，作为历史研究记录；不代表最终启用范围。

## 背景与结论

用户要求在 Capability Setup 的 JetBrains MCP 配置流程中增加显式工具白名单，并先审核开启范围。
现有流程只要求注册连接与调整 IDE Exposed Tools，没有要求在宿主 MCP 参数中声明工具白名单。
这应成为仓库的初始化要求；JetBrains 产品本身并不把提供客户端白名单作为连接的必选条件。

本机使用 Codex 直接注册的 `jetbrains-mcp` Streamable HTTP 服务。建议在它现有的
`[mcp_servers.jetbrains-mcp]` 表中合并 `enabled_tools = [...]`，使用实际导出的工具名。
官方文档将此字段定义为允许列表，`disabled_tools` 在其后应用；这是本机最直接的配置方式。
[OpenAI MCP 文档](https://learn.chatgpt.com/docs/extend/mcp?surface=cli)
[配置字段参考](https://learn.chatgpt.com/docs/config-file/config-reference)

候选清单为 **22 项：基础 IDE 9 项 + 规范脚本 1 项 + 调试 12 项**。
现有能力文档已包含调试，因此完整建议保留调试工作流。明确不采用调试的项目可只选择前 10 项。
这是一份待审建议，不把本机这组工具永久固定为所有项目的通用清单。

## 方法与范围

1. 阅读根目录规则、Capability Setup、当前工作区中的 IDE 能力说明及 inspection 脚本使用说明。
2. 仅提取本机 JetBrains MCP 相关客户端配置、IDE MCP 设置和插件身份；不归档完整客户端配置。
3. 导出当前会话可见的 45 个工具定义，与本机 HTTP `tools/list` 交叉核对。
4. 阅读官方文档和官方开源过滤实现，并用已安装插件的 `javap` 输出核对本机实现。
5. 建立 10 个临时 HTTP 会话，只执行 `initialize`、`notifications/initialized`、`tools/list`；
   每个会话最后 DELETE 关闭。没有执行 `tools/call`、构建、调试、SQL、脚本或源文件修改。
6. 用 `codex -c ... mcp get jetbrains-mcp --json` 验证候选字段可被本机 CLI 解析。
   `-c` 是本次命令的临时覆盖，没有写回用户配置。

原始证据与可复现探针见 [README.md](README.md)。本次是外部 MCP 能力调研，按仓库规则归档。

## 本机观察

| 项目 | 观察结果 |
| --- | --- |
| IDE / 服务端身份 | IntelliJ IDEA 2026.1.3；HTTP initialize 返回同一版本身份 |
| 已安装更新的 MCP Server | 261.25134.203；仅作为实验环境记录，不构成版本要求 |
| 客户端连接 | 直接注册的 Streamable HTTP，端点已脱敏 |
| 客户端允许/禁止列表 | `enabled_tools = null`、`disabled_tools = null`（CLI 的缺省字段输出） |
| 当前会话目录与 HTTP 无 header 目录 | 均为 45 项，工具名集合相同 |
| IDE Exposed Tools 的本地禁止列表 | 10 个数据库相关名称；当前可见目录不含数据库工具 |
| IDE 命令执行设置 | `enableBraveMode = true`；本次没有修改 |
| 项目级 Codex 配置 | 工作区不存在 `.codex/config.toml` |

本机快照见 [local-environment.json](raw/local-environment.json)。Brave mode 是另外一项命令确认设置；
官方帮助说明它影响 shell / run configuration 的逐次确认，它不等于工具白名单。
[JetBrains MCP Server 帮助](https://www.jetbrains.com/help/idea/mcp-server.html)

## 推荐开启范围

### 基础 IDE：9 项

| 工具 | 保留原因与边界 |
| --- | --- |
| `get_project_modules` | 只读核对目标 IDE 项目及模块，作为初始化验收入口。 |
| `rename_refactoring` | 对代码符号进行引用感知的重命名；后续仍须审查完整 diff。 |
| `get_file_problems` | 修改文件后的 IDE 内置诊断；不包含仓库的自定义规范脚本。 |
| `build_project` | 保留 `filesToRebuild` 指定文件的增量编译能力，以当前 IDE 支持为前提。 |
| `reformat_file` | 保留修改后的尽力格式化步骤。 |
| `get_project_dependencies` | 辅助查看当前项目依赖库；工具只承诺库名称，不能单独作为精确版本结论。 |
| `search_symbol` | 定位尚未解决的符号或第三方声明，需要时 `include_external=true`。 |
| `get_symbol_info` | 检查 IDE 当前解析到的声明、签名、类型及文档。 |
| `read_file` | 读取依赖 Jar/Jrt 源码或反编译 class；不作为大文件切片通道。 |

这些语义与依赖工具支持当前 IDE 工作流；普通检索继续遵守项目已选择的检索分工。

### 规范检查：1 项

`run_inspection_kts`：供已选定的规范审查工作流执行仓库已有 `.inspection.kts` 规则。
它会编译并执行脚本，白名单本身不授予执行任意脚本的权限。
API 文档、示例和 PSI 辅助生成工具适合维护脚本时再选，运行现有规则不依赖它们。

### 调试：12 项

| 用途 | 工具 |
| --- | --- |
| 发现启动入口 | `get_run_configurations` |
| 状态、启动与控制 | `xdebug_get_debugger_status`、`xdebug_start_debugger_session`、`xdebug_control_session` |
| 线程、栈与值 | `xdebug_get_threads`、`xdebug_get_stack`、`xdebug_get_frame_values`、`xdebug_get_value_by_path` |
| 断点与定位 | `xdebug_list_breakpoints`、`xdebug_set_breakpoint`、`xdebug_remove_breakpoint`、`xdebug_run_to_line` |

这些工具用于静态证据不足时调查已复现的运行失败。启动、继续程序以及断点条件均可能执行代码；
具体调用继续遵守项目已有的运行与调试授权。断点清理默认限定 agent 所属断点。
本次没有实际启动调试或验证这些操作的执行结果。

## 暂不默认开启：23 项

| 组别 | 数量 | 理由 |
| --- | ---: | --- |
| 文件路径/文本/正则检索接口 | 7 | 与现有项目检索路径重叠；保留 IDE 用于已采用的语义操作和检查。 |
| 普通文件读取、编辑器上下文、目录、VCS 根及打开编辑器 | 5 | 当前能力范围没有要求增加这些宿主已有或无需默认开启的操作。 |
| 通用文件写入 | 3 | `apply_patch`、`create_new_file`、`replace_text_in_file` 由宿主现有文件操作覆盖。 |
| 终端、直接运行配置、Notebook | 3 | `execute_terminal_command`、`execute_run_configuration`、`runNotebookCell` 暂无独立需求。 |
| 脚本开发辅助 | 3 | `generate_inspection_kts_api`、`generate_inspection_kts_examples`、`generate_psi_tree` 在维护脚本时再选。 |
| 运行时求值与赋值 | 2 | `xdebug_evaluate_expression` 可调用方法；`xdebug_set_variable` 直接改变运行时值，待具体需求后再选。 |

45 项的逐项用途、建议状态和理由见 [tool-review.csv](tool-review.csv)。数据库工具本就不在本次 45 项中，
仍不纳入候选白名单；数据库任务继续遵循项目的 MCPHub 路由。

## 两种白名单的区别与实测

Codex `enabled_tools` 按客户端看到的服务端导出名称配置，例如 `xdebug_get_stack`，不加宿主的
`mcp__...__` 调用前缀。候选 TOML 的 22 个名称均存在于本机会话目录，本机 CLI 临时覆盖也返回同样 22 项。
这验证了配置形状及名称来源，**没有**验证当前会话已重新加载。

JetBrains 另有 `IJ_MCP_ALLOWED_TOOLS`：Stdio runner 读取环境变量并转成请求头，
直接 HTTP/SSE 客户端则需用其自定义 header 配置。值是逗号分隔的名称。
这属于 JetBrains 实现参数，不是通用 MCP 协议字段。
[官方 Stdio 实现](https://github.com/JetBrains/intellij-community/blob/master/plugins/mcp-server/mcpserver.stdio/src/McpStdioRunner.kt)

| 本机 HTTP 探针 | 返回工具数 | 说明 |
| --- | ---: | --- |
| 不传 header | 45 | 当前暴露目录基线。 |
| `get_project_modules` | 1 | 单工具白名单生效。 |
| 两个名称，含逗号两侧空格 | 2 | 空格被裁剪。 |
| 不存在的名称 | 0 | 非空未知名称没有匹配项。 |
| 空 header | 45 | 不能用空值表达禁用全部工具。 |
| 仅 `execute_sql_query` | 0 | 本机该名称没有暴露；没有执行 SQL。 |
| 把 22 个导出名称直接放入 header | 11 | 11 个 `xdebug_*` 未匹配。 |
| 仅 `xdebug_get_debugger_status` | 0 | 导出名称无法直接用于此内部过滤。 |
| 仅 `get_debugger_status` | 1 | 实际导出 `xdebug_get_debugger_status`。 |
| 将候选中的 `xdebug_` 前缀去掉后传 header | 22 | 返回的导出名集合与候选 22 项一致。 |

官方过滤源码取内部完全限定名最后一个点号后的部分，本机 jar 的 bytecode 也如此；
因此把 Codex 名单直接复制到 JetBrains header 并不可靠。
[官方过滤实现](https://github.com/JetBrains/intellij-community/blob/master/plugins/mcp-server/src/com/intellij/mcpserver/McpToolFilter.kt)

官方 master 的会话实现还显示 header 路径可以切换 filter-provider 的使用方式；
不能从中推断本机版本总会绕过或总会叠加 IDE 禁止项。本次没有通过更改 IDE 开关验证这种交互。
因此本机方案优先使用有官方文档支持的 Codex `enabled_tools`，保留 IDE Exposed Tools 设置，
不默认增加第二份重复的 JetBrains header 名单。
[官方会话实现（master，非本机固定版本）](https://github.com/JetBrains/intellij-community/blob/master/plugins/mcp-server/src/com/intellij/mcpserver/impl/McpServerService.kt)

## 后续文档改动草案

用户审核范围后，将初始化流程补成：复制/注册连接 → 按已选能力配置宿主显式白名单 →
重连/重启 → 核对实际工具目录的包含与排除 → 对目标项目做一次只读调用。
不会把安装成功或连接成功单独视为设置完成。

- [待审 Codex 参数片段](proposed/codex-allowlist.proposed.toml)
- [待审 Capability Setup 文案](proposed/capability-setup-addition.proposed.md)

参数片段仅用于合并 `enabled_tools`，不可整体替换用户配置；保留原连接、认证、超时和无关设置。

## 限制与未证明事项

- 官网当前帮助页标为 2026.2；本机服务端为 2026.1.3。没有把官网新增但本机未暴露的
  `analyze_calls`、`lint_files`、`validate_inspection_kts`、`router` 等工具加入名单。
- 本次验证了目录过滤和 CLI 参数解析，没有验证候选工具的构建、重构、格式化、脚本及调试效果。
- 没有重启客户端，因此没有宣称新白名单已应用到用户的持久配置或当前会话。
- Stdio 环境变量的转发依据官方源码和本机 bytecode；没有启动另一个 Stdio runner 做端到端测试。
- 工具数量减少不等于实测 token 或延迟收益，本次没有进行性能基准。
- 原始 HTTP 会话标识没有归档；本地连接端点与用户目录已脱敏。
