import com.intellij.psi.*

/**
 * Rule J6: no @Autowired field or setter injection; use constructor injection with final
 * fields (Lombok @RequiredArgsConstructor is acceptable). Source: references/java/
 * spring-boot-backend.md, Dependency Injection, Configuration, and Lombok. Level: WARNING.
 */
val j6NoAutowiredFieldInjection = localInspection { psiFile, inspection ->
    val AW = "org.springframework.beans.factory.annotation.Autowired"
    psiFile.descendantsOfType<PsiField>()
        .filter { it.getAnnotation(AW) != null }
        .forEach { f -> inspection.registerProblem(f, "[J6]") }
    psiFile.descendantsOfType<PsiMethod>()
        .filter { it.getAnnotation(AW) != null }
        .forEach { m -> inspection.registerProblem(m, "[J6]") }
}

listOf(InspectionKts(id = "J6NoAutowiredFieldInjection",
    localTool = j6NoAutowiredFieldInjection,
    name = "inspections/J6NoAutowiredFieldInjection.inspection.kts",
    level = HighlightDisplayLevel.WARNING))
