# Scrapider Guidelines

[English](README.md) | 简体中文

面向 Scrapider 项目编码代理的工程规范。本仓库以可复用的 Codex Skill 形式提供指导，帮助在支持的技术栈中完成小范围、契约安全且可验证的改动。

## 提供什么

- 实用的工程约束：保持改动最小、优先复用既有能力、避免无意引入抽象层。
- 面向 Spring Boot、Python、Android Kotlin 与 Jetpack Compose 的按需规范。
- 清晰定义校验归属、异常语义、业务契约保护与验证方式。
- 一个独立、只读的规范审查 Agent，用于检查改动是否符合本规范。

## 支持范围

| 范围 | 对应指导 |
| --- | --- |
| Spring Boot 后端 | 包职责、分层架构与对象放置规则。 |
| Maven 多模块项目 | 模块归属、依赖关系与 Spring 配置的位置。 |
| Python | 包与模块的组织方式。 |
| Android Kotlin 与 Jetpack Compose | UI 状态、数据映射、异常处理与 Compose 架构。 |
| Android UI 实现 | 面向设计稿还原和产品 UI 设计的补充指导。 |
| 规范审查 | 完整的只读一致性审查流程。 |
| 本地全栈部署 | 在该部署场景适用时，涵盖 Docker Compose、Jenkins 主机构建、运行时镜像和可选 Nginx 路由。 |

## 在 Codex 中使用

请用你常用的 Skill 安装方式将本仓库安装为 Codex Skill，然后在任务提示中调用：

```text
Use $scrapider-guidelines to implement this Spring Boot change.
```

当编码代理需要在 Scrapider 工程规范下编写、修改、重构、调试或审查代码时，也可以自动选用此 Skill。

示例：

```text
Use $scrapider-guidelines to review this Python diff.

Use $scrapider-guidelines to refactor this Jetpack Compose screen without changing its public behavior.

Use $scrapider-guidelines to implement this Spring Boot endpoint and preserve the existing API contract.
```

## 引用资料如何按需加载

主文件 [SKILL.md](SKILL.md) 只会加载与当前任务有关的参考资料。比如，Spring Boot 后端任务会加载后端规范；Maven 多模块任务还会加载模块规范。这样既能让任务保持聚焦，也能应用与实际范围相匹配的规则。

若要进行规范一致性审查，请使用 `scrapider-standards-reviewer` Agent。它被刻意设计为只读，并遵循 [`references/shared/standards-review.md`](references/shared/standards-review.md) 中的审查流程。

## 核心原则

1. **简单优先** —— 以解决当前需求所需的最小、完整改动为目标。
2. **先复用，后新建** —— 新增能力前，先查找项目、框架或标准库中行为等价的现有能力。
3. **避免无意分层** —— 只有在确实承担领域或技术边界时，才引入新层。
4. **手术式改动** —— 不顺带重构无关代码，也不修改无关格式。
5. **保护业务契约** —— 将默认值、阈值、枚举和策略参数视为既有行为。
6. **按风险验证** —— 使用最小但有效的验证方式，并报告验证依据。

## 仓库结构

```text
.
├── SKILL.md
├── agents/
│   ├── openai.yaml
│   └── scrapider-standards-reviewer.md
└── references/
    ├── android/
    ├── java/
    ├── python/
    └── shared/
```

## 贡献

请让每次贡献保持聚焦、与既有规范一致，并清楚说明它解决的问题。新增或修改某条规则时，应更新对应的按需加载参考文档，而不是在无关文档中重复相同规则。

## 许可证

本项目基于 [MIT License](LICENSE) 发布。
