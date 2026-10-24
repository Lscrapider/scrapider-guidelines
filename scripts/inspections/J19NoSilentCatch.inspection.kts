import com.intellij.psi.*

/**
 * Rule J19: no silently swallowed exceptions, in two tiers.
 * Tier 1 (always on, purely structural): an empty catch block — zero statements — swallows
 * the exception with no handling at all.
 * Tier 2 (convention-driven, DETECT_SILENT_SWALLOW): a non-empty catch block that never
 * references the exception, logs nothing, and throws nothing. InterruptedException is
 * exempt (the standard interrupt-restore pattern). Defined by the inspection library;
 * enable when the reviewed repository's instructions require logging swallowed exceptions.
 * Level: WARNING. Config: DETECT_SILENT_SWALLOW (default false).
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
