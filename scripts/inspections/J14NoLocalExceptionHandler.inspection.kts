import com.intellij.psi.*

/**
 * Rule J14: flag controller-local @ExceptionHandler methods; @ControllerAdvice and
 * @RestControllerAdvice classes are exempt.
 */
val j14NoLocalExceptionHandler = localInspection { psiFile, inspection ->
    val RC = "org.springframework.web.bind.annotation.RestController"
    val CC = "org.springframework.web.bind.annotation.Controller"
    val EH = "org.springframework.web.bind.annotation.ExceptionHandler"
    val RA = "org.springframework.web.bind.annotation.RestControllerAdvice"
    val CA = "org.springframework.web.bind.annotation.ControllerAdvice"
    psiFile.descendantsOfType<PsiMethod>()
        .filter { it.getAnnotation(EH) != null }
        .forEach { m ->
            val owner = m.parents.filterIsInstance<PsiClass>().firstOrNull() ?: return@forEach
            val isController = owner.getAnnotation(RC) != null || owner.getAnnotation(CC) != null
            val isAdvice = owner.getAnnotation(RA) != null || owner.getAnnotation(CA) != null
            if (isController && !isAdvice)
                inspection.registerProblem(m.nameIdentifier ?: m, "[J14]")
        }
}

listOf(InspectionKts(id = "J14NoLocalExceptionHandler",
    localTool = j14NoLocalExceptionHandler,
    name = "inspections/J14NoLocalExceptionHandler.inspection.kts",
    level = HighlightDisplayLevel.ERROR))
