import com.intellij.psi.*

/**
 * Rule J15 (opt-in): no Map/JsonNode request bodies where a stable typed contract exists.
 * Source: references/java/spring-boot-http-and-domain.md. Level: WEAK_WARNING. The rule's
 * condition is semantic: findings are candidates that need manual confirmation.
 */
val j15NoMapRequestBody = localInspection { psiFile, inspection ->
    val RC = "org.springframework.web.bind.annotation.RestController"
    val CC = "org.springframework.web.bind.annotation.Controller"
    val RB = "org.springframework.web.bind.annotation.RequestBody"
    psiFile.descendantsOfType<PsiClass>()
        .filter { it.getAnnotation(RC) != null || it.getAnnotation(CC) != null }
        .forEach { c ->
            c.methods.forEach { m ->
                m.parameterList.parameters.forEach { p ->
                    if (p.getAnnotation(RB) == null) return@forEach
                    val t = p.type.canonicalText
                    if (t.startsWith("java.util.Map") || t.endsWith("JsonNode"))
                        inspection.registerProblem(p, "[J15]")
                }
            }
        }
}

listOf(InspectionKts(id = "J15NoMapRequestBody",
    localTool = j15NoMapRequestBody,
    name = "inspections/J15NoMapRequestBody.inspection.kts",
    level = HighlightDisplayLevel.WEAK_WARNING))
