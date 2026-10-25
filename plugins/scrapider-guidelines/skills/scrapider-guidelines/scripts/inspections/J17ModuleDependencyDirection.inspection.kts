import com.intellij.psi.*
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.roots.ModuleRootManager

/**
 * Rule J17: enforce module dependency direction and placement bans in multi-module repositories.
 *
 * Tiers resolve in order: MODULE_MAP package-prefix overrides, module-name tokens, package
 * tokens. A file whose tier stays unresolved emits a [J17][SKIP] notice instead of silently
 * passing. Import bans need the tier token in the file's own package to derive the root
 * package; the declared module dependency graph and placement bans run regardless.
 */

// Package-prefix overrides for non-conventional naming, e.g.
// mapOf("com.example.yourapp.foundation." to "shared")
val MODULE_MAP: Map<String, String> = emptyMap()

// business/bootstrap naming wins over later specificity tokens (business-common stays business);
// foundation compounds take the most specific segment (foundation-entity -> model).
val TIER_BY_TOKEN = mapOf(
    "bootstrap" to "bootstrap", "app" to "bootstrap", "application" to "bootstrap",
    "business" to "business", "biz" to "business",
    "foundation" to "shared", "shared" to "shared", "common" to "shared", "core" to "shared",
    "persistence" to "persistence", "db" to "persistence", "mapper" to "persistence",
    "model" to "model", "entity" to "model"
)
val BANNED_TIERS = mapOf(
    "model" to setOf("business", "bootstrap", "persistence", "shared"),
    "persistence" to setOf("business", "bootstrap"),
    "shared" to setOf("business", "bootstrap"),
    "business" to setOf("bootstrap")
)

fun importFqn(st: PsiElement): String {
    var t = st.text.trim()
    if (t.startsWith("import")) t = t.removePrefix("import").trim()
    if (t.startsWith("static")) t = t.removePrefix("static").trim()
    return t.trimEnd(';').trim()
}

fun tierFromTokens(tokens: List<String>): String? {
    val hits = tokens.mapIndexedNotNull { i, t -> TIER_BY_TOKEN[t]?.let { i to it } }
    if (hits.isEmpty()) return null
    val top = hits.firstOrNull { it.second == "bootstrap" } ?: hits.firstOrNull { it.second == "business" }
    return (top ?: hits.last()).second
}

fun tierOf(f: PsiJavaFile): String? {
    val pkg = f.packageName
    MODULE_MAP.forEach { (prefix, tier) -> if (pkg.startsWith(prefix)) return tier }
    val modName = ModuleUtilCore.findModuleForFile(f)?.name ?: ""
    tierFromTokens(modName.lowercase().split('-', '_', '.').filter { it.isNotEmpty() })?.let { return it }
    return tierFromTokens(pkg.lowercase().split('.').filter { it.isNotEmpty() })
}

fun rootPackageOf(pkg: String): String? {
    val segs = pkg.lowercase().split('.').filter { it.isNotEmpty() }
    val first = segs.indexOfFirst { TIER_BY_TOKEN.containsKey(it) }
    if (first <= 0) return null
    return segs.subList(0, first).joinToString(".") + "."
}

fun looksLikeDbAccess(n: String) =
    n != "ObjectMapper" && (n.endsWith("Mapper") || n.endsWith("Manage"))

val j17ModuleDependencyDirection = localInspection { psiFile, inspection ->
    val f = psiFile as? PsiJavaFile ?: return@localInspection
    val RC = "org.springframework.web.bind.annotation.RestController"
    val CC = "org.springframework.web.bind.annotation.Controller"
    val SVC = "org.springframework.stereotype.Service"
    val RA = "org.springframework.web.bind.annotation.RestControllerAdvice"
    val mod = ModuleUtilCore.findModuleForFile(f)
    val tier = tierOf(f)
    if (tier == null) {
        inspection.registerProblem(f, "[J17][SKIP] tier unresolved for ${f.packageName} (module ${mod?.name ?: "?"}); set MODULE_MAP prefixes")
        return@localInspection
    }
    val banned = BANNED_TIERS[tier] ?: emptySet()
    val root = rootPackageOf(f.packageName)
    if (root != null) {
        f.importList?.allImportStatements?.forEach { st ->
            val q = importFqn(st)
            val hit = banned.firstOrNull { q.startsWith(root + it + ".") }
            if (hit != null)
                inspection.registerProblem(st, "[J17] $tier file imports $hit package: $q")
        }
    }
    if (mod != null) {
        ModuleRootManager.getInstance(mod).dependencies.forEach { dep ->
            val depTier = tierFromTokens(dep.name.lowercase().split('-', '_', '.'))
            if (depTier != null && depTier in banned)
                inspection.registerProblem(f, "[J17] module ${mod.name} ($tier) declares dependency on ${dep.name} ($depTier)")
        }
    }
    f.classes.forEach { c ->
        val n = c.name ?: return@forEach
        val dbAccess = looksLikeDbAccess(n)
        val web = c.getAnnotation(RC) != null || c.getAnnotation(CC) != null
        val svc = c.getAnnotation(SVC) != null
        val advice = c.getAnnotation(RA) != null
        val misplaced = when (tier) {
            "bootstrap" -> web || svc || dbAccess
            "model" -> web || svc || advice || dbAccess
            "persistence" -> web
            else -> false
        }
        if (misplaced)
            inspection.registerProblem(c, "[J17] $tier-tier class not allowed here: $n")
    }
}

listOf(InspectionKts(id = "J17ModuleDependencyDirection",
    localTool = j17ModuleDependencyDirection,
    name = "inspections/J17ModuleDependencyDirection.inspection.kts",
    level = HighlightDisplayLevel.ERROR))
