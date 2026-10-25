import com.intellij.psi.*

/**
 * Rule J7: flag wildcard imports, including static imports.
 */
val j7NoWildcardImport = localInspection { psiFile, inspection ->
    val javaFile = psiFile as? PsiJavaFile ?: return@localInspection
    javaFile.importList?.allImportStatements?.forEach { st ->
        if (st.text.replace(" ", "").endsWith(".*;"))
            inspection.registerProblem(st, "[J7]")
    }
}

listOf(InspectionKts(id = "J7NoWildcardImport",
    localTool = j7NoWildcardImport,
    name = "inspections/J7NoWildcardImport.inspection.kts",
    level = HighlightDisplayLevel.WARNING))
