# 最终批准及应用结果

日期：2026-10-27（Asia/Shanghai）。

用户审核决定：调试全部关闭，只开启基础 IDE 九项和已有规范检查脚本入口一项。

## 已应用

- Codex 的 `jetbrains-mcp` 服务已设置明确的十项 `enabled_tools`。
- `codex mcp get jetbrains-mcp --json` 返回的允许列表与批准名单一致。
- 当前会话实际可见的 JetBrains 工具也已收敛到同样十项，调试及运行配置工具均不可见。
- 技能格式校验、文档链接、差异格式和归档脱敏检查均通过；独立规范复核结论为 Pass。
- Capability Setup 已加入必须配置白名单、重连后核对实际目录的要求及十项参数示例。
- IDE 能力推荐、五处组合示例和 AGENTS.md 编写验收清单已同步移除调试能力声明。
- 未修改 IDE 全局开关，也未进行 Git 提交、推送或创建分支。

## 最终工具

- `get_project_modules`
- `rename_refactoring`
- `get_file_problems`
- `build_project`
- `reformat_file`
- `get_project_dependencies`
- `search_symbol`
- `get_symbol_info`
- `read_file`
- `run_inspection_kts`

## 证据与历史

- `codex-allowlist.toml`：最终批准的参数片段。
- `enabled-tools.json`：最终批准的十项工具名。
- `verification.json`：当前会话实际工具目录核对结果。
- `codex-config-check.json`：已保存配置的读取结果，仅保留工具列表等必要字段。

上级 `proposed/` 中的二十二项提案及 `raw/` 中的原始目录探针保留为审核前历史；
它们不代表最终启用范围。原始研究探针继续使用当时的提案文件，避免改写已有实验数据。
