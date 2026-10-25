import com.intellij.psi.*

/**
 * Rule J1: flag controller fields and constructor parameters typed as database *Mapper or *Manage.
 * Add non-database name collisions to EXCLUDED_TYPES (ObjectMapper is preset).
 */
val EXCLUDED_TYPES = setOf("com.fasterxml.jackson.databind.ObjectMapper")

val j1ControllerNoMapperOrManage = localInspection { psiFile, inspection ->
    val RC = "org.springframework.web.bind.annotation.RestController"
    val CC = "org.springframework.web.bind.annotation.Controller"
    fun isDbAccess(typeName: String) =
        typeName !in EXCLUDED_TYPES && (typeName.endsWith("Mapper") || typeName.endsWith("Manage"))
    psiFile.descendantsOfType<PsiClass>()
        .filter { it.getAnnotation(RC) != null || it.getAnnotation(CC) != null }
        .forEach { c ->
            c.fields.forEach { field ->
                val typeName = field.type.canonicalText
                if (isDbAccess(typeName))
                    inspection.registerProblem(field, "[J1]")
            }
            c.constructors.forEach { ctor ->
                ctor.parameterList.parameters.forEach { p ->
                    val typeName = p.type.canonicalText
                    if (isDbAccess(typeName))
                        inspection.registerProblem(p, "[J1]")
                }
            }
        }
}

listOf(InspectionKts(id = "J1ControllerNoMapperOrManage",
    localTool = j1ControllerNoMapperOrManage,
    name = "inspections/J1ControllerNoMapperOrManage.inspection.kts",
    level = HighlightDisplayLevel.ERROR))
