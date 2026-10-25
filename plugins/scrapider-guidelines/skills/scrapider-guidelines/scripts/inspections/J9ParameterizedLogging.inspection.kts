import com.intellij.psi.*

/**
 * Rule J9 (opt-in): flag concatenation in the first argument of recognized logger calls;
 * recognition uses a log/logger/LOG/LOGGER-named field or a Logger-typed field.
 */
val j9ParameterizedLogging = localInspection { psiFile, inspection ->
    val LOG_METHODS = setOf("trace", "debug", "info", "warn", "error")
    val LOGGER_NAMES = setOf("log", "logger", "LOG", "LOGGER")
    psiFile.descendantsOfType<PsiMethodCallExpression>().forEach { call ->
        val me = call.methodExpression
        val mName = (me as? PsiReferenceExpression)?.referenceName ?: return@forEach
        if (mName !in LOG_METHODS) return@forEach
        val q = me.qualifierExpression ?: return@forEach
        val target = (q as? PsiReferenceExpression)?.reference?.resolve() as? PsiField ?: return@forEach
        val isLogger = target.name in LOGGER_NAMES || (target.type?.canonicalText?.contains("Logger") == true)
        if (!isLogger) return@forEach
        val first = call.argumentList.expressions.firstOrNull() ?: return@forEach
        if (first is PsiBinaryExpression || first is PsiPolyadicExpression)
            inspection.registerProblem(call, "[J9]")
    }
}

listOf(InspectionKts(id = "J9ParameterizedLogging",
    localTool = j9ParameterizedLogging,
    name = "inspections/J9ParameterizedLogging.inspection.kts",
    level = HighlightDisplayLevel.WEAK_WARNING))
