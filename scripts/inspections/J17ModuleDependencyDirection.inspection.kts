import com.intellij.psi.*

/**
 * Rule J17: module dependency directions and placement bans for Maven multi-module
 * repositories — lower modules must not import business/bootstrap packages, bootstrap holds
 * no business components, the model module holds no runtime components, and the persistence
 * module holds no controllers. Source: references/java/spring-boot-multi-module.md.
 * Level: ERROR. Config: moduleOf maps package prefixes to modules; it ships with
 * placeholder prefixes, so the running agent replaces them with the target repository's
 * real topology before use — unmatched packages leave the rule inert. Name-suffix checks
 * exclude non-database *Mapper names (ObjectMapper is preset).
 */

// Repository adaptation: replace the placeholder prefixes with the target repository's
// real package prefixes and module names; unmatched packages leave the rule inert.
fun moduleOf(pkg: String): String = when {
    pkg.startsWith("com.example.model.") -> "model"              // entity/contract module
    pkg.startsWith("com.example.persistence.") -> "persistence"  // mapper/manage module
    pkg.startsWith("com.example.shared.") -> "shared"            // shared config/foundation
    pkg.startsWith("com.example.bootstrap.") -> "bootstrap"      // application assembly
    pkg.startsWith("com.example.business.") -> "business"        // business modules
    else -> ""
}

fun importFqn(st: PsiElement): String {
    var t = st.text.trim()
    if (t.startsWith("import")) t = t.removePrefix("import").trim()
    if (t.startsWith("static")) t = t.removePrefix("static").trim()
    return t.trimEnd(';').trim()
}

val EXCLUDED_MAPPER_NAMES = setOf("ObjectMapper")

fun looksLikeDbAccess(n: String) =
    n !in EXCLUDED_MAPPER_NAMES && (n.endsWith("Mapper") || n.endsWith("Manage"))

val j17ModuleDependencyDirection = localInspection { psiFile, inspection ->
    val RC = "org.springframework.web.bind.annotation.RestController"
    val CC = "org.springframework.web.bind.annotation.Controller"
    val SVC = "org.springframework.stereotype.Service"
    val RA = "org.springframework.web.bind.annotation.RestControllerAdvice"
    val javaFile = psiFile as? PsiJavaFile ?: return@localInspection
    val module = moduleOf(javaFile.packageName)
    if (module.isEmpty()) return@localInspection
    val banned: List<String> = when (module) {
        "model" -> listOf("com.example.business.", "com.example.bootstrap.",
            "com.example.persistence.", "com.example.shared.")
        "persistence" -> listOf("com.example.business.", "com.example.bootstrap.")
        "shared" -> listOf("com.example.business.", "com.example.bootstrap.")
        "business" -> listOf("com.example.bootstrap.")
        else -> emptyList()
    }
    javaFile.importList?.allImportStatements?.forEach { st ->
        val q = importFqn(st)
        banned.firstOrNull { q.startsWith(it) }?.let { inspection.registerProblem(st, "[J17]") }
    }
    javaFile.classes.forEach { c ->
        val n = c.name ?: ""
        if (module == "bootstrap" && (c.getAnnotation(RC) != null || c.getAnnotation(CC) != null ||
                c.getAnnotation(SVC) != null || looksLikeDbAccess(n)))
            inspection.registerProblem(c, "[J17]")
        if (module == "model" && (c.getAnnotation(RC) != null || c.getAnnotation(CC) != null ||
                c.getAnnotation(SVC) != null || c.getAnnotation(RA) != null || looksLikeDbAccess(n)))
            inspection.registerProblem(c, "[J17]")
        if (module == "persistence" && (c.getAnnotation(RC) != null || c.getAnnotation(CC) != null))
            inspection.registerProblem(c, "[J17]")
    }
}

listOf(InspectionKts(id = "J17ModuleDependencyDirection",
    localTool = j17ModuleDependencyDirection,
    name = "inspections/J17ModuleDependencyDirection.inspection.kts",
    level = HighlightDisplayLevel.ERROR))
