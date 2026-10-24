import com.intellij.psi.*

/**
 * Rule PY2: no sys.path mutation; fix import problems at the package, installation, or
 * entry-point configuration layer. Source: references/python/python-code-organization.md,
 * Import Rules. Level: ERROR. Text-level regex check; file-level anchor, so the message
 * carries the real line number and the matched line.
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
