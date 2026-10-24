import com.intellij.psi.*

/**
 * Rule J1: controllers must not inject *Mapper / *Manage types (fields and constructor
 * parameters). Source: references/java/spring-boot-backend.md, Core Layered Architecture.
 * Level: ERROR. Config: EXCLUDED_TYPES holds *Mapper-named types that are not database
 * access (Jackson ObjectMapper is preset).
 */
val EXCLUDED_TYPES = setOf("com.fasterxml.jackson.databind.ObjectMapper")

val j1ControllerNoMapperManage = localInspection { psiFile, inspection ->
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

listOf(InspectionKts(id = "J1ControllerNoMapperManage",
    localTool = j1ControllerNoMapperManage,
    name = "inspections/J1ControllerNoMapperOrManage.inspection.kts",
    level = HighlightDisplayLevel.ERROR))
