import com.intellij.psi.*

/**
 * Rule J11: flag single-member static imports from *Util or configured utility classes;
 * static wildcard imports are handled by J7.
 */
val EXPLICIT_UTILITY_CLASSES = setOf("java.util.Objects", "org.apache.commons.lang3.StringUtils", "org.apache.commons.lang3.ObjectUtils")

val j11NoUtilityStaticImport = localInspection { psiFile, inspection ->
    val javaFile = psiFile as? PsiJavaFile ?: return@localInspection
    javaFile.importList?.allImportStatements?.forEach { st ->
        val stat = st as? PsiImportStaticStatement ?: return@forEach
        if (stat.isOnDemand) return@forEach
        val refText = stat.importReference?.text ?: return@forEach
        val classFqn = refText.substringBeforeLast('.')
        if (classFqn.endsWith("Util") || classFqn in EXPLICIT_UTILITY_CLASSES)
            inspection.registerProblem(st, "[J11]")
    }
}

listOf(InspectionKts(id = "J11NoUtilityStaticImport",
    localTool = j11NoUtilityStaticImport,
    name = "inspections/J11NoUtilityStaticImport.inspection.kts",
    level = HighlightDisplayLevel.WARNING))
