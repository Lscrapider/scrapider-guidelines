import com.intellij.psi.*

/**
 * Rule J3: service interface methods carry javadoc — the method itself, @param for every
 * parameter, and @return for non-void results. Source: references/java/spring-boot-backend.md,
 * File and API Documentation ("follow the repository's method-level JavaDoc convention,
 * including its parameter and return tags"). Convention-driven: enable only when the
 * reviewed repository's instructions require service-interface javadoc.
 * Level: WARNING. Config: REQUIRE_SERVICE_JAVADOC (default false).
 */
val REQUIRE_SERVICE_JAVADOC = false

val j3ServiceInterfaceJavadoc = localInspection { psiFile, inspection ->
    if (!REQUIRE_SERVICE_JAVADOC) return@localInspection
    psiFile.descendantsOfType<PsiClass>()
        .filter { it.isInterface && (it.name?.endsWith("Service") == true) }
        .forEach { iface ->
            iface.methods.filter { !it.isConstructor }.forEach { m ->
                val doc = m.docComment
                if (doc == null) {
                    inspection.registerProblem(m.nameIdentifier ?: m,
                        "[J3] missing javadoc on ${m.name}()")
                    return@forEach
                }
                val documented = doc.tags
                    .filter { it.name.equals("param", ignoreCase = true) }
                    .mapNotNull { tag -> tag.text.trim().removePrefix("@param").trim()
                        .split(Regex("\\s+")).firstOrNull() }
                    .filter { it.isNotEmpty() }.toSet()
                m.parameterList.parameters.forEach { p ->
                    if (p.name !in documented)
                        inspection.registerProblem(p,
                            "[J3] missing @param ${p.name} on ${m.name}()")
                }
                if (m.returnType?.canonicalText != "void" &&
                    doc.tags.none { it.name.equals("return", ignoreCase = true) })
                    inspection.registerProblem(m.nameIdentifier ?: m,
                        "[J3] missing @return on ${m.name}()")
            }
        }
}

listOf(InspectionKts(id = "J3ServiceInterfaceJavadoc",
    localTool = j3ServiceInterfaceJavadoc,
    name = "inspections/J3ServiceInterfaceJavadoc.inspection.kts",
    level = HighlightDisplayLevel.WARNING))
