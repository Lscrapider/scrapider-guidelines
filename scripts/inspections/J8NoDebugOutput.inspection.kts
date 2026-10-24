import com.intellij.psi.*

/**
 * Rule J8: no System.out/err printing and no printStackTrace(); use the project logging
 * framework and pass exception objects to the logger. Source: references/java/
 * spring-boot-backend.md, Logging. Level: WARNING. Detection resolves calls against
 * java.io.PrintStream / java.lang.Throwable rather than matching text.
 */
val j8NoDebugOutput = localInspection { psiFile, inspection ->
    psiFile.descendantsOfType<PsiMethodCallExpression>().forEach { call ->
        val method = call.methodExpression.reference?.resolve() as? PsiMethod ?: return@forEach
        val owner = method.containingClass?.qualifiedName
        if (method.name in setOf("println", "print", "printf") && owner == "java.io.PrintStream")
            inspection.registerProblem(call, "[J8]")
        if (method.name == "printStackTrace" && owner == "java.lang.Throwable")
            inspection.registerProblem(call, "[J8]")
    }
}

listOf(InspectionKts(id = "J8NoDebugOutput",
    localTool = j8NoDebugOutput,
    name = "inspections/J8NoDebugOutput.inspection.kts",
    level = HighlightDisplayLevel.WARNING))
