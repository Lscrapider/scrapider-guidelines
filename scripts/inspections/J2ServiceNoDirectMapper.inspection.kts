import com.intellij.psi.*

/**
 * Rule J2: flag *Mapper fields in @Service classes, *ServiceImpl classes, and *Service
 * interfaces only when REQUIRE_MANAGE_LAYER is true; disable it without a manage layer and
 * exclude name collisions with EXCLUDED_TYPES (ObjectMapper is preset).
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
