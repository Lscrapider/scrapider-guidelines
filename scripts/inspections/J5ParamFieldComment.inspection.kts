import com.intellij.psi.*

/**
 * Rule J5: *Param fields carry a comment — @Schema(description=...), a javadoc, or a
 * leading comment. Convention-driven, defined by the inspection library itself: enable only
 * when the reviewed repository's instructions or established file style document request
 * fields. Implementation note: a comment directly above a field binds into that field's own
 * text range, so leading comments are detected by the field's text prefix.
 * Level: WARNING. Config: REQUIRE_PARAM_FIELD_COMMENTS (default false).
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
