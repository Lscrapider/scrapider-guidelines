import com.intellij.psi.*

/**
 * Rule J16: require role suffixes for classes in mapped packages; service-package classes are
 * checked only when they are interfaces. The package-to-suffix map is in the when block.
 */
val j16PackageSuffixAlignment = localInspection { psiFile, inspection ->
    val javaFile = psiFile as? PsiJavaFile ?: return@localInspection
    val last = javaFile.packageName.substringAfterLast('.')
    val required: Pair<String, Boolean>? = when (last) {
        "controller" -> "Controller" to false
        "mapper" -> "Mapper" to false
        "manage" -> "Manage" to false
        "param" -> "Param" to false
        "dto" -> "DTO" to false
        "vo" -> "VO" to false
        "po" -> "PO" to false
        "converter" -> "Converter" to false
        "impl" -> "Impl" to false
        "service" -> "Service" to true
        else -> null
    }
    if (required == null) return@localInspection
    val suffix = required.first
    val ifaceOnly = required.second
    javaFile.classes.forEach { c ->
        if (ifaceOnly && !c.isInterface) return@forEach
        val n = c.name ?: return@forEach
        if (!n.endsWith(suffix))
            inspection.registerProblem(c.nameIdentifier ?: c, "[J16]")
    }
}

listOf(InspectionKts(id = "J16PackageSuffixAlignment",
    localTool = j16PackageSuffixAlignment,
    name = "inspections/J16PackageSuffixAlignment.inspection.kts",
    level = HighlightDisplayLevel.WARNING))
