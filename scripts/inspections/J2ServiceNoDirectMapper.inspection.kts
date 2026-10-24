import com.intellij.psi.*

/**
 * Rule J2: services must not inject *Mapper directly; applies only in repositories with a
 * manage layer. Source: references/java/spring-boot-backend.md, Core Layered Architecture.
 * Level: WARNING. Config: REQUIRE_MANAGE_LAYER=false for repositories without a manage
 * layer; EXCLUDED_TYPES holds *Mapper-named non-database types (ObjectMapper is preset).
 */
val REQUIRE_MANAGE_LAYER = true
val EXCLUDED_TYPES = setOf("com.fasterxml.jackson.databind.ObjectMapper")

val j2ServiceNoDirectMapper = localInspection { psiFile, inspection ->
    if (!REQUIRE_MANAGE_LAYER) return@localInspection
    psiFile.descendantsOfType<PsiClass>()
        .filter { c ->
            val n = c.name ?: ""
            c.getAnnotation("org.springframework.stereotype.Service") != null ||
                n.endsWith("ServiceImpl") || (c.isInterface && n.endsWith("Service"))
        }
        .forEach { c ->
            c.fields.forEach { field ->
                val typeName = field.type.canonicalText
                if (typeName.endsWith("Mapper") && typeName !in EXCLUDED_TYPES)
                    inspection.registerProblem(field, "[J2]")
            }
        }
}

listOf(InspectionKts(id = "J2ServiceNoDirectMapper",
    localTool = j2ServiceNoDirectMapper,
    name = "inspections/J2ServiceNoDirectMapper.inspection.kts",
    level = HighlightDisplayLevel.WARNING))
