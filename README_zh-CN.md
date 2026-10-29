# Scrapider Guidelines

<p align="center">
  <img src="plugins/scrapider-guidelines/assets/icon.png" width="128" height="128" alt="Scrapider 工程规范插件图标" />
</p>

[English](README.md) | 简体中文

面向 Scrapider 项目编码代理的工程规范。本仓库以可复用的 Agent Skill 形式提供指导，帮助代理理解需求、遵循项目架构、在改动范围内维护业务契约，并以适当的复杂度完成可验证的改动。

## 提供什么

- 从理解问题、实现或调试，到验证行为、报告依据的编码流程。
- 关于改动范围、业务契约、能力复用、职责边界和重构的通用规则。
- 依据真实调用路径、已有保证和业务需求确定校验与异常处理的规则。
- 面向 Spring Boot、Python、Android Kotlin 与 Jetpack Compose 的按需规范。
- 按影响范围触发的规范审查，以及独立的 Git 操作规范。
- 只创建或修改目标 `AGENTS.md` 的编写引导，包含可选 MCP / Skill / 插件组合与协作分工推荐。

## 支持范围

| 范围 | 对应指导 |
| --- | --- |
| Spring Boot 后端 | 包职责、分层架构与对象放置规则。 |
| Maven 多模块项目 | 模块归属、依赖关系与 Spring 配置的位置。 |
| Python | 包与模块的组织方式。 |
| Android Kotlin 与 Jetpack Compose | UI 状态、数据映射、异常处理与 Compose 架构。 |
| Android UI 实现 | 面向设计稿还原和产品 UI 设计的补充指导。 |
| 规范审查 | 完整的只读一致性审查流程，可用 JetBrains 检查脚本通道执行机械检查。 |
| 项目 AGENTS.md | 帮助作者组织清晰、有层次的项目说明，在原有章节中按需更新能力组合、协作等内容。 |

## 使用方式

通过所用 coding agent 的 Skill 安装与调用方式使用本仓库。指令正文和参考资料面向通用 coding agent；各宿主的发现、调用和可用能力以其实际支持为准。本仓库同时是一个插件市场：在 ZCode 或 Claude Code 中将 `https://github.com/Lscrapider/scrapider-plugins` 添加为插件市场并安装 `scrapider-guidelines` 插件，即可获得此 Skill 及五个协作角色 Agent；可选的 `scrapider-capabilities` 插件会注册推荐的 MCP 服务（Context7 文档查询、Codegraph 代码索引），宿主已有这些服务的请勿安装。以下示例使用 Codex 的调用语法：

```text
Use $scrapider-guidelines to implement this Spring Boot change.
```

当编码代理需要在 Scrapider 工程规范下编写、修改、重构、调试或审查代码时，也可以自动选用此 Skill。

示例：

```text
Use $scrapider-guidelines to review this Python diff.

Use $scrapider-guidelines to refactor this Jetpack Compose screen without changing its public behavior.

Use $scrapider-guidelines to implement this Spring Boot endpoint and preserve the existing API contract.

Use $scrapider-guidelines to create, update, or rebuild this project's AGENTS.md with suitable workflows and optional tool combinations.
```

在 Claude Code 中，使用带插件命名空间的命令显式发起规范审查：

```text
/scrapider-guidelines:scrapider-review
```

在命令后添加范围或路径可缩小审查范围；不带参数时审查当前工作区改动。

AGENTS.md 编写流程只修改指定的说明文件，保留项目惯例，不安装工具或创建配置、子 Agent 文件。

## 引用资料如何按需加载

主文件 [SKILL.md](plugins/scrapider-guidelines/skills/scrapider-guidelines/SKILL.md) 负责工作原则、编码流程和加载规则。具体规范按它们约束的开发决策分类：

| 类别 | 规则来源 |
| --- | --- |
| 改动范围、业务契约、复用、职责与重构 | [代码设计与变更](plugins/scrapider-guidelines/skills/scrapider-guidelines/references/shared/code-design-and-changes.md)，所有代码及构建／运行配置任务都加载。 |
| 输入保证、校验归属、异常处理 | [校验与边界](plugins/scrapider-guidelines/skills/scrapider-guidelines/references/shared/validation-and-boundaries.md)，涉及这些决策时加载。 |
| 审查适用条件、调度、只读流程与报告 | [规范审查](plugins/scrapider-guidelines/skills/scrapider-guidelines/references/shared/standards-review.md)，明确要求审查或改动可能跨越审查边界时加载。 |
| Git 操作授权与提交信息格式 | [Git 操作与提交](plugins/scrapider-guidelines/skills/scrapider-guidelines/references/shared/git-commits.md)，执行任何 Git 状态变更前加载。 |
| 规范检查脚本库 | [Inspection Scripts](plugins/scrapider-guidelines/skills/scrapider-guidelines/references/shared/inspection-scripts.md)，指派的规范审查者在 JetBrains 检查通道可用时加载；显式维护、适配或安装脚本库时也加载。 |
| 项目说明与可选能力组合 | [AGENTS.md Authoring](plugins/scrapider-guidelines/skills/scrapider-guidelines/references/project/agents-md-authoring.md) 是编写请求的入口，再按条件进入能力推荐、已选能力示例、角色定义，以及另行请求的安装初始化流程。 |
| 技术栈专用职责 | 先读适用的 [Spring Boot](plugins/scrapider-guidelines/skills/scrapider-guidelines/references/java/spring-boot-backend.md)、[Python](plugins/scrapider-guidelines/skills/scrapider-guidelines/references/python/python-code-organization.md) 或 [Android](plugins/scrapider-guidelines/skills/scrapider-guidelines/references/android/android-kotlin-compose.md) 基线，再按其场景条件读取下游参考。 |

各入口负责下游参考的加载条件，读到入口不代表加载其中全部链接。Spring Boot 任务由后端基线按
多模块归属、架构、HTTP／领域对象、数据访问和消息队列场景继续路由。Python 任务由 Python 基线
在新建、移动或重组代码时引入包结构规则。Android 任务由 Android 基线分别进入设计稿实现或无固定
设计图的产品 UI 设计流程。混合任务加载所有适用分支，也包括经调用方受到影响的行为；范围扩大时
重新检查路由，实现和审查采用相同规则。参考中的示例不要求项目补齐不需要的分层。

通用设计规则区分用户指出的位置与问题的实际归属：有证据关联时检查其他路径，在授权范围内完整修复
共同原因，同时保留场景特有的行为；先确定修复范围，再决定是否引入抽象。

规范一致性审查遵循 [`references/shared/standards-review.md`](plugins/scrapider-guidelines/skills/scrapider-guidelines/references/shared/standards-review.md)，由该文档定义何时委派只读的 `scrapider-standards-reviewer` Agent，何时直接审查。

## 工作原则与思想来源

[工作原则](plugins/scrapider-guidelines/skills/scrapider-guidelines/SKILL.md#working-principles) 保留编码前思考、简单优先、精准控制改动范围、以可验证结果驱动执行的思想。其组织方式参考社区维护的 [Karpathy-inspired coding guidelines](https://github.com/multica-ai/andrej-karpathy-skills)，该文档根据 Andrej Karpathy 对编码代理问题的观察整理。

[编码流程](plugins/scrapider-guidelines/skills/scrapider-guidelines/SKILL.md#coding-workflow) 将这些思想与项目架构、业务契约、校验边界、验证和审查要求结合。具体规范按实际任务加载，每条规则在对应的职责类别中维护。

## 仓库结构

```text
.
├── .claude-plugin/
│   └── marketplace.json          # 市场目录，本仓库即市场根
├── .research/
└── plugins/
    ├── scrapider-guidelines/     # 插件根
    │   ├── .codex-plugin/
    │   │   └── plugin.json
    │   ├── .claude-plugin/
    │   │   └── plugin.json
    │   ├── .zcode-plugin/
    │   │   └── plugin.json
    │   ├── assets/
    │   │   └── icon.png
    │   ├── commands/            # 显式规范审查命令
    │   │   └── scrapider-review.md
    │   ├── agents/              # 五个协作角色定义
    │   └── skills/
    │       └── scrapider-guidelines/
    │           ├── SKILL.md
    │           ├── agents/
    │           │   └── openai.yaml
    │           ├── references/
    │           │   ├── android/
    │           │   ├── java/
    │           │   ├── project/
    │           │   ├── python/
    │           │   └── shared/
    │           └── scripts/
    │               ├── run_inspection.mjs # Git 范围、本地脚本与 JetBrains MCP
    │               └── inspections/
    └── scrapider-capabilities/   # 可选 MCP 组合：context7 + codegraph
        ├── .claude-plugin/
        │   └── plugin.json
        └── .zcode-plugin/
            └── plugin.json
```

## 调研与评估

[代码检索 Token 实验](.research/2026-10-18-retrieval-token-benchmark/REPORT.md) 记录测试场景、
实际用量、质量复核、限制和方案选择，也提供可核对的
[逐次指标](.research/2026-10-18-retrieval-token-benchmark/metrics.csv)。
[12 次 Serena 补测](.research/2026-10-18-serena-incremental-benefit/REPORT.md) 记录去重修复、预先固定的评分、
重复对照结果，以及未实际调用的可选工具无法归因收益这一限制。后续归档记录了近期功能背后的评估：机械检查通道对应的
[inspection.kts 规则库](.research/2026-10-24-inspection-kts-rule-library/REPORT.md)、外部库文档服务与 IDE 工具面推荐对应的
[JetBrains MCP 与 Context7 能力试点](.research/2026-10-24-jetbrains-context7-capability-pilot/REPORT.md)。`.research/` 供浏览仓库的开发者查阅，
不进入插件载荷；Skill 的加载路由也不引用这些记录。

## 贡献

请让每次贡献保持聚焦、与既有规范一致，并清楚说明它解决的问题。新增或修改某条规则时，应更新对应的按需加载参考文档，而不是在无关文档中重复相同规则。

## 许可证

本项目基于 [MIT License](LICENSE) 发布。
