# 交接 Prompt：scrapider-guidelines 规范 → inspection.kts 规则库

> 用法：新开一个 ZCode 会话（工作目录 = scrapider-guidelines 或目标项目），把下面横线以下的全文粘贴进去执行。
> 背景产出：.research/2026-10-24-jetbrains-context7-capability-pilot/（能力试点 REPORT.md 的 ROUND4 节记录了本决策）。

---

你是"scrapider 代码规范 → JetBrains inspection.kts 规则库"的执行会话。目标：把 scrapider-guidelines skill 中**机械可判定**的代码规范抽取成 `.inspection.kts` 检查脚本库，一次固化、可存档复用（agent 全仓扫描 + IDEA 编辑器实时高亮 + 可挂 Qodana/CI）。

## 输入（规范源，先完整阅读）

scrapider-guidelines skill 位于 ~/.zcode/skills/scrapider-guidelines/（或本仓库），规范正文在：
- references/shared/code-design-and-changes.md（分层、职责边界）
- references/shared/validation-and-boundaries.md（校验与异常）
- references/java/spring-boot-backend.md 及其路由的子参考（Spring Boot 分层：Controller→Service→Manage、Converter 转换、param/DTO 规范）
- references/java/spring-boot-multi-module.md（模块依赖方向）
- references/python/python-code-organization.md（Python 组织）
- 目标项目自身的 AGENTS.md（如有项目级规范，如 ORVX 的"业务层 service interface 类需要写注释"）

## 工具面（JetBrains MCP，宿主为 ZCode 直连 jetbrain-mcp）

- `run_inspection_kts`：执行检查脚本（参数：inspectionKtsCode + contextPath 相对项目根，可用 targetFileContent 对未落盘内容）
- `generate_inspection_kts_examples` / `generate_inspection_kts_api`（language=Java/Kotlin）：模板与 API 字典
- `generate_psi_tree`：看代码的 PSI 结构（写脚本前的"结构字典"）
- **所有调用必须带 projectPath**（IDEA 多项目同开）；脚本只能作用于 IDEA 当前打开的项目

## 规则筛选标准（关键：只固化"机械"规则）

进规则库的标准——能写成 PSI 判断、无需业务语义：
- 分层依赖禁令：Controller 不得注入 `*Mapper`/`*Manage`；业务模块不重复注册 MyBatis-Plus 分页插件
- 结构要求：Service 接口 public 方法必须有 javadoc；param 类字段必须有注释；文件头 @Author/@Date/@Description 模板
- import/包规则：模块依赖方向禁令（可参考官方模板的 restricted-imports.json 配置驱动范式）

**不进规则库**（留给 scrapider-standards-reviewer agent 审查）：最小改动符合度、职责边界是否合理、异常语义是否吞并、业务契约判断。

## 范式（10 行起步，官方模板语法）

```kotlin
import com.intellij.psi.*

val noMapperInController = localInspection { psiFile, inspection ->
    val controllers = psiFile.descendantsOfType<PsiClass>()
        .filter { it.hasAnnotation("org.springframework.web.bind.annotation.RestController") }
    controllers.forEach { c ->
        c.fields.forEach { field ->
            val typeName = field.type.canonicalText   // 语义级类型解析，非文本匹配
            if (typeName.endsWith("Mapper") || typeName.endsWith("Manage"))
                inspection.registerProblem(field, "Controller 不得注入 $typeName（分层：Controller 只依赖 Service）")
        }
    }
}
listOf(InspectionKts(id = "NoMapperInController", localTool = noMapperInController,
        name = "inspections/NoMapperInController.inspection.kts", level = HighlightDisplayLevel.WARNING))
```

## 执行步骤

1. 读规范源，列出候选规则清单（每条标注：机械/语义、目标语言、预期 PSI 判定方式），先给用户过目再写脚本；
2. 按优先级实现（先 Java 高频分层规则，后 Python——Python PSI 依赖 IDEA 的 Python 插件已装，先用 generate_psi_tree 探一个 .py 文件确认结构可用性）；
3. 每条规则在目标项目（IDEA 已打开的真实仓库）上 `run_inspection_kts` 实测：记录脚本迭代次数（学习曲线）、扫出的违规清单、误报/漏报；
4. 规则库落位（与用户确认）：建议本 skill 仓库新建 `references/inspections/`（每规则一个 .inspection.kts + README 说明对应规范条目与用法）；
5. 产出：规则库 + 每条规则的实测记录 + 在 skill 文档中的接入说明草案（哪个规范文件加一行"本条已脚本化，见 references/inspections/xxx"）。

## 已知注意事项（来自试点实测）

- `reformat_file` 与 `execute_terminal_command` 在 IDE 后端繁忙时会 30s 超时——不要依赖它们，调试检查用 run_inspection_kts 的返回 + 只读命令；
- 检查结果只对"IDEA 打开的项目"生效；真实仓库有用户未提交变更时只读扫描安全，但不得写操作；
- 首次写 inspection.kts 允许迭代 2-3 轮（编译错误/PSI 类型不对很正常），examples/api/psi_tree 三个辅助工具就是为此准备的；
- Git 提交需用户明确授权。
