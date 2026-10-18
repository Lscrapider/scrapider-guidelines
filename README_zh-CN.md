# Scrapider Guidelines

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
| 规范审查 | 完整的只读一致性审查流程。 |
| 项目 AGENTS.md | 帮助作者组织清晰、有层次的项目说明，在原有章节中按需更新能力组合、协作等内容。 |
| 本地全栈部署 | 在该部署场景适用时，涵盖 Docker Compose、Jenkins 主机构建、运行时镜像和可选 Nginx 路由。 |

## 使用方式

通过所用 coding agent 的 Skill 安装与调用方式使用本仓库。指令正文和参考资料面向通用 coding agent；各宿主的发现、调用和可用能力以其实际支持为准。以下示例使用 Codex 的调用语法：

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

AGENTS.md 编写流程只修改指定的说明文件，保留项目惯例，不安装工具或创建配置、子 Agent 文件。

## 引用资料如何按需加载

主文件 [SKILL.md](SKILL.md) 负责工作原则、编码流程和加载规则。具体规范按它们约束的开发决策分类：

| 类别 | 规则来源 |
| --- | --- |
| 改动范围、业务契约、复用、职责与重构 | [代码设计与变更](references/shared/code-design-and-changes.md)，所有代码及构建／运行配置任务都加载。 |
| 输入保证、校验归属、异常处理 | [校验与边界](references/shared/validation-and-boundaries.md)，涉及这些决策时加载。 |
| 审查适用条件、调度、只读流程与报告 | [规范审查](references/shared/standards-review.md)，明确要求审查或改动可能跨越审查边界时加载。 |
| Git 操作授权与提交信息格式 | [Git 操作与提交](references/shared/git-commits.md)，执行任何 Git 状态变更前加载。 |
| 项目说明与可选能力组合 | [AGENTS.md Authoring](references/project/agents-md-authoring.md)，仅在编写请求中加载；选型时再读取 [Capability Recommendations](references/project/capability-recommendations.md)。 |
| 技术栈专用职责 | 按需加载 Java、Python、Android 或本地部署参考。 |

比如，Spring Boot 任务加载通用设计规则和后端规范；Maven 多模块任务再加载模块归属规范。参考中的示例不要求项目补齐不需要的分层。

规范一致性审查遵循 [`references/shared/standards-review.md`](references/shared/standards-review.md)，由该文档定义何时委派只读的 `scrapider-standards-reviewer` Agent，何时直接审查。

## 工作原则与思想来源

[工作原则](SKILL.md#working-principles) 保留编码前思考、简单优先、精准控制改动范围、以可验证结果驱动执行的思想。其组织方式参考社区维护的 [Karpathy-inspired coding guidelines](https://github.com/multica-ai/andrej-karpathy-skills)，该文档根据 Andrej Karpathy 对编码代理问题的观察整理。

[编码流程](SKILL.md#coding-workflow) 将这些思想与项目架构、业务契约、校验边界、验证和审查要求结合。具体规范按实际任务加载，每条规则在对应的职责类别中维护。

## 仓库结构

```text
.
├── .research/
├── SKILL.md
├── agents/
│   ├── openai.yaml
│   └── scrapider-standards-reviewer.md
└── references/
    ├── android/
    ├── java/
    ├── project/
    ├── python/
    └── shared/
```

## 调研与评估

[代码检索 Token 实验](.research/2026-10-18-retrieval-token-benchmark/REPORT.md) 记录测试场景、
实际用量、质量复核、限制和方案选择，也提供可核对的
[逐次指标](.research/2026-10-18-retrieval-token-benchmark/metrics.csv)。
[12 次 Serena 补测](.research/2026-10-18-serena-incremental-benefit/REPORT.md) 记录去重修复、预先固定的评分、
重复对照结果，以及未实际调用的可选工具无法归因收益这一限制。`.research/` 供浏览仓库的开发者查阅，
安装 Skill 时忽略该目录；Skill 的加载路由不引用这些记录。

## 贡献

请让每次贡献保持聚焦、与既有规范一致，并清楚说明它解决的问题。新增或修改某条规则时，应更新对应的按需加载参考文档，而不是在无关文档中重复相同规则。

## 许可证

本项目基于 [MIT License](LICENSE) 发布。
