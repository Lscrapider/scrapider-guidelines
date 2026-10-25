import com.intellij.psi.*

/**
 * Rule J19: always flag empty catches; flag non-empty catches only when
 * DETECT_SILENT_SWALLOW is enabled and they do not reference the exception, call a method named
 * trace/debug/info/warn/error/log, or contain a throw keyword. Non-empty InterruptedException
 * catches are exempt; the non-empty check is disabled by default.
 */
val DETECT_SILENT_SWALLOW = false

val j19NoSilentCatch = localInspection { psiFile, inspection ->
    val LOG_METHODS = setOf("trace", "debug", "info", "warn", "error", "log")
    psiFile.descendantsOfType<PsiCatchSection>().forEach { section ->
        val param = section.descendantsOfType<PsiVariable>().firstOrNull() ?: return@forEach
        val body = section.children.lastOrNull() ?: return@forEach
        val hasStatements = body.text.removePrefix("{").removeSuffix("}").trim().isNotEmpty()
        if (!hasStatements) {
            inspection.registerProblem(section, "[J19] empty catch")
            return@forEach
        }
        if (!DETECT_SILENT_SWALLOW) return@forEach
        if (param.type?.canonicalText?.contains("InterruptedException") == true) return@forEach
        val referenced = body.descendantsOfType<PsiReferenceExpression>().any {
            it.reference?.resolve() == param
        }
        if (referenced) return@forEach
        val hasLog = body.descendantsOfType<PsiMethodCallExpression>().any { c ->
            (c.methodExpression as? PsiReferenceExpression)?.referenceName in LOG_METHODS
        }
        if (!hasLog && !body.text.contains(Regex("\\bthrow\\b")))
            inspection.registerProblem(section, "[J19] silent catch")
    }
}

listOf(InspectionKts(id = "J19NoSilentCatch",
    localTool = j19NoSilentCatch,
    name = "inspections/J19NoSilentCatch.inspection.kts",
    level = HighlightDisplayLevel.WARNING))
