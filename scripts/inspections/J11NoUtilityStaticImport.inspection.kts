import com.intellij.psi.*

/**
 * Rule J11: invoke utility statics through their declaring class; no single-member static
 * imports of utility classes (static wildcards are covered by J7). Source: references/java/
 * spring-boot-backend.md, Utilities and Value Handling. Level: WARNING. Config:
 * EXPLICIT_UTILITY_CLASSES extends the *Util-suffix match.
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
