import com.intellij.psi.*

/**
 * Rule J13: flag controller return signatures containing PO-suffixed types, including wrapper
 * generics; runtime objects hidden inside Map values are not inspected.
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
