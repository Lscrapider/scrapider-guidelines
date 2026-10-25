import com.intellij.psi.*

/**
 * Rule J8: flag resolved PrintStream print/println/printf calls and Throwable.printStackTrace().
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
