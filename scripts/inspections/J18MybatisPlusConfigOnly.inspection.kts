import com.intellij.psi.*

/**
 * Rule J18: flag MyBatis-Plus plugin imports outside .config packages or files whose first
 * class name ends in Config.
 */
fun importFqn(st: PsiElement): String {
    var t = st.text.trim()
    if (t.startsWith("import")) t = t.removePrefix("import").trim()
    if (t.startsWith("static")) t = t.removePrefix("static").trim()
    return t.trimEnd(';').trim()
}

val j18MybatisPlusConfigOnly = localInspection { psiFile, inspection ->
    val javaFile = psiFile as? PsiJavaFile ?: return@localInspection
    val pkg = javaFile.packageName
    val cls = javaFile.classes.firstOrNull()?.name ?: ""
    if (pkg.endsWith(".config") || cls.endsWith("Config")) return@localInspection
    javaFile.importList?.allImportStatements?.forEach { st ->
        if (importFqn(st).startsWith("com.baomidou.mybatisplus.extension.plugins."))
            inspection.registerProblem(st, "[J18]")
    }
}

listOf(InspectionKts(id = "J18MybatisPlusConfigOnly",
    localTool = j18MybatisPlusConfigOnly,
    name = "inspections/J18MybatisPlusConfigOnly.inspection.kts",
    level = HighlightDisplayLevel.ERROR))
