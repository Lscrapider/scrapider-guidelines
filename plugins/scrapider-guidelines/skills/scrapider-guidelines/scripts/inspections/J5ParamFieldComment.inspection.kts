import com.intellij.psi.*

/**
 * Rule J5: require comments on non-static *Param fields only when repository instructions or
 * established style require them; accept @Schema(description=...), javadoc, or a leading comment.
 * The check is disabled by default.
 */
val REQUIRE_PARAM_FIELD_COMMENTS = false

val j5ParamFieldComment = localInspection { psiFile, inspection ->
    if (!REQUIRE_PARAM_FIELD_COMMENTS) return@localInspection
    val SCHEMA = "io.swagger.v3.oas.annotations.media.Schema"
    psiFile.descendantsOfType<PsiClass>()
        .filter { (it.name ?: "").endsWith("Param") }
        .forEach { c ->
            c.fields.filter { !it.hasModifierProperty("static") }.forEach { f ->
                val schemaDesc = f.annotations.any {
                    it.qualifiedName == SCHEMA && it.findDeclaredAttributeValue("description") != null
                }
                val t = f.text
                val leadingComment = t.startsWith("//") ||
                    (t.startsWith("/*") && !t.startsWith("/**"))
                if (!schemaDesc && f.docComment == null && !leadingComment)
                    inspection.registerProblem(f.nameIdentifier ?: f, "[J5]")
            }
        }
}

listOf(InspectionKts(id = "J5ParamFieldComment",
    localTool = j5ParamFieldComment,
    name = "inspections/J5ParamFieldComment.inspection.kts",
    level = HighlightDisplayLevel.WARNING))
