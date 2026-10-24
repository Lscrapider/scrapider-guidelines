import com.intellij.psi.*

/**
 * Rule J12 (opt-in): qualify instance-method calls on the current object with this.
 * Source: references/java/spring-boot-backend.md, Style and Naming. Level: WEAK_WARNING.
 * High hit volume on legacy code — run opt-in. Only calls resolving to methods declared
 * in the enclosing class are flagged; inherited and static methods are not.
 */
val j12ExplicitThisQualifier = localInspection { psiFile, inspection ->
    psiFile.descendantsOfType<PsiMethodCallExpression>().forEach { call ->
        val me = call.methodExpression
        if (me.qualifierExpression != null) return@forEach
        val m = me.reference?.resolve() as? PsiMethod ?: return@forEach
        if (m.hasModifierProperty("static") || m.isConstructor) return@forEach
        val owner = m.containingClass ?: return@forEach
        val enclosing = call.parents.filterIsInstance<PsiClass>().firstOrNull() ?: return@forEach
        if (owner.isEquivalentTo(enclosing))
            inspection.registerProblem(call, "[J12]")
    }
}

listOf(InspectionKts(id = "J12ExplicitThisQualifier",
    localTool = j12ExplicitThisQualifier,
    name = "inspections/J12ExplicitThisQualifier.inspection.kts",
    level = HighlightDisplayLevel.WEAK_WARNING))
