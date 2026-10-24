import com.intellij.psi.*

/**
 * Rule PY1: Python module file names are snake_case. Source: references/python/
 * python-code-organization.md, Naming Rules. Level: WARNING. Text-level check; no Python
 * plugin needed. File-level anchor: the message carries the verdict token.
 */
val py1SnakeCaseModuleName = localInspection { psiFile, inspection ->
    val name = psiFile.name
    if (!name.endsWith(".py")) return@localInspection
    val base = name.removeSuffix(".py")
    if (!Regex("^[a-z_][a-z0-9_]*$").matches(base))
        inspection.registerProblem(psiFile, "[PY1] not snake_case")
}

listOf(InspectionKts(id = "PY1SnakeCaseModuleName",
    localTool = py1SnakeCaseModuleName,
    name = "inspections/PY1SnakeCaseModuleName.inspection.kts",
    level = HighlightDisplayLevel.WARNING))
