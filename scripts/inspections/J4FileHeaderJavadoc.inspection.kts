import com.intellij.psi.*

/**
 * Rule J4: class header javadoc carries the repository's template tags. Source:
 * references/java/spring-boot-backend.md, File and API Documentation ("inspect equivalent
 * files and repository instructions for a required file-level JavaDoc template... follow an
 * established template exactly; do not impose one when the repository has no such rule").
 * Convention-driven: fill REQUIRED_TAGS with the repository's template tags; it stays empty
 * (= check disabled) for repositories without a header template.
 * Level: WARNING. Config: REQUIRED_TAGS (default empty = off).
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
