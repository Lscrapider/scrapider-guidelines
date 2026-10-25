import com.intellij.psi.*

/**
 * Rule J17: for mapped Maven modules, block model imports from business/bootstrap/persistence/
 * shared, persistence/shared imports from business/bootstrap, and business imports from bootstrap.
 * Placement bans: model excludes controller/service/advice/database-access classes, bootstrap
 * excludes controllers/services/database access, and persistence excludes controllers.
 * Update the moduleOf mappings and banned-import prefixes to the target topology; unmatched
 * packages are ignored.
 * EXCLUDED_MAPPER_NAMES excludes name-based collisions (ObjectMapper is preset).
 */

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
