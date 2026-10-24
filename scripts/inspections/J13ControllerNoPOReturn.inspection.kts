import com.intellij.psi.*

/**
 * Rule J13: controllers must not return PO types, including inside wrapper generics such as
 * IPage<PO> or ApiResponse<PO>; convert to VO at the boundary. Source: references/java/
 * spring-boot-http-and-domain.md. Level: ERROR.
 */
val j13ControllerNoPOReturn = localInspection { psiFile, inspection ->
    val RC = "org.springframework.web.bind.annotation.RestController"
    val CC = "org.springframework.web.bind.annotation.Controller"
    psiFile.descendantsOfType<PsiClass>()
        .filter { it.getAnnotation(RC) != null || it.getAnnotation(CC) != null }
        .forEach { c ->
            c.methods.forEach { m ->
                val rt = m.returnType?.canonicalText ?: return@forEach
                if (rt.split(Regex("[<>,\\s]+")).any { it.endsWith("PO") })
                    inspection.registerProblem(m.returnTypeElement ?: m, "[J13]")
            }
        }
}

listOf(InspectionKts(id = "J13ControllerNoPOReturn",
    localTool = j13ControllerNoPOReturn,
    name = "inspections/J13ControllerNoPOReturn.inspection.kts",
    level = HighlightDisplayLevel.ERROR))
