import com.intellij.psi.*

/**
 * Rule J10: compare enums with ==, never equals or ordinal() comparisons. Source:
 * references/java/spring-boot-backend.md, Utilities and Value Handling. Level: WARNING.
 * Enum receivers are detected via super types containing java.lang.Enum<...>.
 */
val j10EnumIdentityComparison = localInspection { psiFile, inspection ->
    psiFile.descendantsOfType<PsiMethodCallExpression>().forEach { call ->
        val method = call.methodExpression.reference?.resolve() as? PsiMethod ?: return@forEach
        if (method.name == "equals") {
            val recv = call.methodExpression.qualifierExpression ?: return@forEach
            val recvType = recv.type ?: return@forEach
            if (recvType.getAllSuperTypes().any { it.canonicalText.startsWith("java.lang.Enum<") })
                inspection.registerProblem(call, "[J10]")
        }
        if (method.name == "ordinal" && method.containingClass?.qualifiedName == "java.lang.Enum") {
            var p: PsiElement? = call.parent
            var hops = 0
            while (p != null && hops < 3) {
                if (p is PsiBinaryExpression) {
                    inspection.registerProblem(call, "[J10]")
                    break
                }
                if (p is PsiExpressionStatement || p is PsiMethod) break
                p = p.parent; hops++
            }
        }
    }
}

listOf(InspectionKts(id = "J10EnumIdentityComparison",
    localTool = j10EnumIdentityComparison,
    name = "inspections/J10EnumIdentityComparison.inspection.kts",
    level = HighlightDisplayLevel.WARNING))
