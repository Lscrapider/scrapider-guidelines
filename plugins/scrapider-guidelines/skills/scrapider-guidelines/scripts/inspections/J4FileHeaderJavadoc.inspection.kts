import com.intellij.psi.*

/**
 * Rule J4: require configured tags in class-header javadoc only when the repository has a
 * header template; an empty REQUIRED_TAGS disables the check.
 */
val REQUIRED_TAGS = emptyList<String>()

val j4FileHeaderJavadoc = localInspection { psiFile, inspection ->
    if (REQUIRED_TAGS.isEmpty()) return@localInspection
    val javaFile = psiFile as? PsiJavaFile ?: return@localInspection
    javaFile.classes.forEach { c ->
        val doc = c.docComment
        if (doc == null) {
            inspection.registerProblem(c.nameIdentifier ?: c, "[J4] missing header javadoc")
            return@forEach
        }
        REQUIRED_TAGS.forEach { tag ->
            if (doc.tags.none { it.name.equals(tag, ignoreCase = true) })
                inspection.registerProblem(c.nameIdentifier ?: c, "[J4] missing @${tag}")
        }
    }
}

listOf(InspectionKts(id = "J4FileHeaderJavadoc",
    localTool = j4FileHeaderJavadoc,
    name = "inspections/J4FileHeaderJavadoc.inspection.kts",
    level = HighlightDisplayLevel.WARNING))
