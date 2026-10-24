import com.intellij.psi.*

/**
 * Rule J7: no wildcard imports, including static ones. Source: references/java/
 * spring-boot-backend.md, Style and Naming. Level: WARNING. Implementation note: static
 * imports are absent from importList.importStatements; use allImportStatements and detect
 * wildcards by the ".*;" text suffix.
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
