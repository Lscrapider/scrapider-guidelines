import com.intellij.psi.*

/**
 * Rule J16: class names in role packages carry the matching suffix (controller→Controller,
 * mapper→Mapper, manage→Manage, param→Param, dto→DTO, vo→VO, po→PO, converter→Converter,
 * impl→*Impl; the service package requires it on interfaces only). Source: references/java/
 * spring-boot-backend.md Style and Naming + spring-boot-architecture.md Package Map.
 * Level: WARNING. Config: the package-to-suffix map in checkJ16.
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
