import com.intellij.psi.*

/**
 * Rule PY2: flag text matching sys.path.append/insert/extend/remove; verify the matched line
 * is executable code and resolve imports at the package, installation, or entry-point layer.
 */
val py2NoSysPathMutation = localInspection { psiFile, inspection ->
    if (!psiFile.name.endsWith(".py")) return@localInspection
    val text = psiFile.text
    val m = Regex("sys\\s*\\.\\s*path\\s*\\.\\s*(append|insert|extend|remove)\\s*\\(").find(text) ?: return@localInspection
    val line = text.substring(0, m.range.first).count { it == '\n' } + 1
    val lineText = text.lineSequence().elementAt(line - 1).trim().let { if (it.length > 80) it.take(80) + "…" else it }
    inspection.registerProblem(psiFile, "[PY2] L$line $lineText")
}

listOf(InspectionKts(id = "PY2NoSysPathMutation",
    localTool = py2NoSysPathMutation,
    name = "inspections/PY2NoSysPathMutation.inspection.kts",
    level = HighlightDisplayLevel.ERROR))
