import com.intellij.psi.*

/**
 * Rule PY3 (opt-in): apply vague-name checks only to modules with business logic; exclude
 * infrastructure-only modules.
 */
val VAGUE_NAMES = setOf("common", "utils", "util", "helpers", "helper", "misc")

val py3NoVagueModuleName = localInspection { psiFile, inspection ->
    val name = psiFile.name
    if (!name.endsWith(".py")) return@localInspection
    val base = name.removeSuffix(".py")
    if (base in VAGUE_NAMES)
        inspection.registerProblem(psiFile, "[PY3] vague name")
}

listOf(InspectionKts(id = "PY3NoVagueModuleName",
    localTool = py3NoVagueModuleName,
    name = "inspections/PY3NoVagueModuleName.inspection.kts",
    level = HighlightDisplayLevel.WEAK_WARNING))
