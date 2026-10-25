import com.intellij.psi.*
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.roots.ModuleRootManager

/**
 * Batch driver: apply the rule library to configured changed-file paths; an empty FILES list
 * checks only the context file. Use each result's file:line message and ignore structured
 * location fields in batch mode. Output aggregates after PER_RULE_PER_FILE matches and truncates
 * after MAX_FINDINGS; split the file list into smaller batches when truncated.
 * J17 tiers resolve from MODULE_MAP overrides, then module/package naming tokens; unresolved
 * tiers emit one [J17][SKIP] notice per module instead of silently passing.
 */
val FILES = emptyList<String>()
val OPT_IN = true
val MAX_FINDINGS = 60
val PER_RULE_PER_FILE = 5
val REQUIRE_MANAGE_LAYER = true
val EXCLUDED_TYPES = setOf("com.fasterxml.jackson.databind.ObjectMapper")
val EXCLUDED_MAPPER_NAMES = setOf("ObjectMapper")
val EXPLICIT_UTILITY_CLASSES = setOf("java.util.Objects", "org.apache.commons.lang3.StringUtils", "org.apache.commons.lang3.ObjectUtils")
val VAGUE_NAMES = setOf("common", "utils", "util", "helpers", "helper", "misc")
// Defaults disable convention-only checks; J19's empty-catch check remains active.
val REQUIRE_SERVICE_JAVADOC = false
val REQUIRED_TAGS = emptyList<String>()
val REQUIRE_PARAM_FIELD_COMMENTS = false
val DETECT_SILENT_SWALLOW = false

fun lineOf(f: PsiFile, el: PsiElement): Int =
    f.text.substring(0, el.textRange.startOffset).count { it == '\n' } + 1

fun snip(el: PsiElement): String =
    el.text.replace(Regex("\\s+"), " ").trim().let { if (it.length > 80) it.take(80) + "…" else it }

fun importFqn(st: PsiElement): String {
    var t = st.text.trim()
    if (t.startsWith("import")) t = t.removePrefix("import").trim()
    if (t.startsWith("static")) t = t.removePrefix("static").trim()
    return t.trimEnd(';').trim()
}

// J17 tier resolution: MODULE_MAP overrides first, then module-name tokens, then package tokens.
// business/bootstrap naming wins over later specificity tokens (business-common stays business);
// foundation compounds take the most specific segment (foundation-entity -> model).
val MODULE_MAP: Map<String, String> = emptyMap()
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
val j17Noted = mutableSetOf<String>()

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

fun checkJ1(f: PsiJavaFile, out: MutableList<String>) {
    val RC = "org.springframework.web.bind.annotation.RestController"
    val CC = "org.springframework.web.bind.annotation.Controller"
    f.descendantsOfType<PsiClass>().filter { it.getAnnotation(RC) != null || it.getAnnotation(CC) != null }
        .forEach { c ->
            c.fields.forEach { field ->
                val t = field.type.canonicalText
                if (t !in EXCLUDED_TYPES && (t.endsWith("Mapper") || t.endsWith("Manage")))
                    out.add("[J1] ${f.name}:${lineOf(f, field)} ${snip(field)}")
            }
            c.constructors.forEach { ctor ->
                ctor.parameterList.parameters.forEach { p ->
                    val t = p.type.canonicalText
                    if (t !in EXCLUDED_TYPES && (t.endsWith("Mapper") || t.endsWith("Manage")))
                        out.add("[J1] ${f.name}:${lineOf(f, p)} ${snip(p)}")
                }
            }
        }
}

fun checkJ2(f: PsiJavaFile, out: MutableList<String>) {
    if (!REQUIRE_MANAGE_LAYER) return
    f.descendantsOfType<PsiClass>()
        .filter { c ->
            val n = c.name ?: ""
            c.getAnnotation("org.springframework.stereotype.Service") != null || n.endsWith("ServiceImpl") || (c.isInterface && n.endsWith("Service"))
        }
        .forEach { c ->
            c.fields.forEach { field ->
                val t = field.type.canonicalText
                if (t.endsWith("Mapper") && t !in EXCLUDED_TYPES)
                    out.add("[J2] ${f.name}:${lineOf(f, field)} ${snip(field)}")
            }
        }
}

fun checkJ3(f: PsiJavaFile, out: MutableList<String>) {
    if (!REQUIRE_SERVICE_JAVADOC) return
    f.descendantsOfType<PsiClass>()
        .filter { it.isInterface && (it.name?.endsWith("Service") == true) }
        .forEach { iface ->
            iface.methods.filter { !it.isConstructor }.forEach { m ->
                val doc = m.docComment
                if (doc == null) {
                    out.add("[J3] ${f.name}:${lineOf(f, m.nameIdentifier ?: m)} missing javadoc on ${m.name}()")
                    return@forEach
                }
                val documented = doc.tags
                    .filter { it.name.equals("param", ignoreCase = true) }
                    .mapNotNull { tag -> tag.text.trim().removePrefix("@param").trim().split(Regex("\\s+")).firstOrNull() }
                    .filter { it.isNotEmpty() }.toSet()
                m.parameterList.parameters.forEach { p ->
                    if (p.name !in documented)
                        out.add("[J3] ${f.name}:${lineOf(f, p)} missing @param ${p.name} on ${m.name}()")
                }
                if (m.returnType?.canonicalText != "void" && doc.tags.none { it.name.equals("return", ignoreCase = true) })
                    out.add("[J3] ${f.name}:${lineOf(f, m.nameIdentifier ?: m)} missing @return on ${m.name}()")
            }
        }
}

fun checkJ4(f: PsiJavaFile, out: MutableList<String>) {
    if (REQUIRED_TAGS.isEmpty()) return
    f.classes.forEach { c ->
        val doc = c.docComment
        if (doc == null) {
            out.add("[J4] ${f.name}:${lineOf(f, c.nameIdentifier ?: c)} missing header javadoc")
            return@forEach
        }
        REQUIRED_TAGS.forEach { tag ->
            if (doc.tags.none { it.name.equals(tag, ignoreCase = true) })
                out.add("[J4] ${f.name}:${lineOf(f, c.nameIdentifier ?: c)} missing @${tag}")
        }
    }
}

fun checkJ5(f: PsiJavaFile, out: MutableList<String>) {
    if (!REQUIRE_PARAM_FIELD_COMMENTS) return
    val SCHEMA = "io.swagger.v3.oas.annotations.media.Schema"
    f.descendantsOfType<PsiClass>()
        .filter { (it.name ?: "").endsWith("Param") }
        .forEach { c ->
            c.fields.filter { !it.hasModifierProperty("static") }.forEach { fl ->
                val schemaDesc = fl.annotations.any {
                    it.qualifiedName == SCHEMA && it.findDeclaredAttributeValue("description") != null
                }
                val t = fl.text
                val leadingComment = t.startsWith("//") || (t.startsWith("/*") && !t.startsWith("/**"))
                if (!schemaDesc && fl.docComment == null && !leadingComment)
                    out.add("[J5] ${f.name}:${lineOf(f, fl.nameIdentifier ?: fl)} ${snip(fl)}")
            }
        }
}

fun checkJ6(f: PsiJavaFile, out: MutableList<String>) {
    val AW = "org.springframework.beans.factory.annotation.Autowired"
    f.descendantsOfType<PsiField>().filter { it.getAnnotation(AW) != null }.forEach { fl ->
        out.add("[J6] ${f.name}:${lineOf(f, fl)} ${snip(fl)}")
    }
    f.descendantsOfType<PsiMethod>().filter { it.getAnnotation(AW) != null }.forEach { m ->
        out.add("[J6] ${f.name}:${lineOf(f, m)} ${snip(m)}")
    }
}

fun checkJ7(f: PsiJavaFile, out: MutableList<String>) {
    f.importList?.allImportStatements?.forEach { st ->
        if (st.text.replace(" ", "").endsWith(".*;"))
            out.add("[J7] ${f.name}:${lineOf(f, st)} ${snip(st)}")
    }
}

fun checkJ8(f: PsiJavaFile, out: MutableList<String>) {
    f.descendantsOfType<PsiMethodCallExpression>().forEach { call ->
        val method = call.methodExpression.reference?.resolve() as? PsiMethod ?: return@forEach
        val owner = method.containingClass?.qualifiedName
        if (method.name in setOf("println", "print", "printf") && owner == "java.io.PrintStream")
            out.add("[J8] ${f.name}:${lineOf(f, call)} ${snip(call)}")
        if (method.name == "printStackTrace" && owner == "java.lang.Throwable")
            out.add("[J8] ${f.name}:${lineOf(f, call)} ${snip(call)}")
    }
}

fun checkJ9(f: PsiJavaFile, out: MutableList<String>) {
    if (!OPT_IN) return
    val LOG_METHODS = setOf("trace", "debug", "info", "warn", "error")
    val LOGGER_NAMES = setOf("log", "logger", "LOG", "LOGGER")
    f.descendantsOfType<PsiMethodCallExpression>().forEach { call ->
        val me = call.methodExpression
        val mName = (me as? PsiReferenceExpression)?.referenceName ?: return@forEach
        if (mName !in LOG_METHODS) return@forEach
        val q = me.qualifierExpression ?: return@forEach
        val target = (q as? PsiReferenceExpression)?.reference?.resolve() as? PsiField ?: return@forEach
        val isLogger = target.name in LOGGER_NAMES || (target.type?.canonicalText?.contains("Logger") == true)
        if (!isLogger) return@forEach
        val first = call.argumentList.expressions.firstOrNull() ?: return@forEach
        if (first is PsiBinaryExpression || first is PsiPolyadicExpression)
            out.add("[J9] ${f.name}:${lineOf(f, call)} ${snip(call)}")
    }
}

fun checkJ10(f: PsiJavaFile, out: MutableList<String>) {
    f.descendantsOfType<PsiMethodCallExpression>().forEach { call ->
        val method = call.methodExpression.reference?.resolve() as? PsiMethod ?: return@forEach
        if (method.name == "equals") {
            val recv = call.methodExpression.qualifierExpression ?: return@forEach
            val recvType = recv.type ?: return@forEach
            if (recvType.getAllSuperTypes().any { it.canonicalText.startsWith("java.lang.Enum<") })
                out.add("[J10] ${f.name}:${lineOf(f, call)} ${snip(call)}")
        }
        if (method.name == "ordinal" && method.containingClass?.qualifiedName == "java.lang.Enum") {
            var p: PsiElement? = call.parent
            var hops = 0
            while (p != null && hops < 3) {
                if (p is PsiBinaryExpression) {
                    out.add("[J10] ${f.name}:${lineOf(f, call)} ${snip(call)}")
                    break
                }
                if (p is PsiExpressionStatement || p is PsiMethod) break
                p = p.parent; hops++
            }
        }
    }
}

fun checkJ11(f: PsiJavaFile, out: MutableList<String>) {
    f.importList?.allImportStatements?.forEach { st ->
        val stat = st as? PsiImportStaticStatement ?: return@forEach
        if (stat.isOnDemand) return@forEach
        val refText = stat.importReference?.text ?: return@forEach
        val classFqn = refText.substringBeforeLast('.')
        if (classFqn.endsWith("Util") || classFqn in EXPLICIT_UTILITY_CLASSES)
            out.add("[J11] ${f.name}:${lineOf(f, st)} ${snip(st)}")
    }
}

fun checkJ12(f: PsiJavaFile, out: MutableList<String>) {
    if (!OPT_IN) return
    f.descendantsOfType<PsiMethodCallExpression>().forEach { call ->
        val me = call.methodExpression
        if (me.qualifierExpression != null) return@forEach
        val m = me.reference?.resolve() as? PsiMethod ?: return@forEach
        if (m.hasModifierProperty("static") || m.isConstructor) return@forEach
        val owner = m.containingClass ?: return@forEach
        val enclosing = call.parents.filterIsInstance<PsiClass>().firstOrNull() ?: return@forEach
        if (owner.isEquivalentTo(enclosing))
            out.add("[J12] ${f.name}:${lineOf(f, call)} ${snip(call)}")
    }
}

fun checkJ13(f: PsiJavaFile, out: MutableList<String>) {
    val RC = "org.springframework.web.bind.annotation.RestController"
    val CC = "org.springframework.web.bind.annotation.Controller"
    f.descendantsOfType<PsiClass>()
        .filter { it.getAnnotation(RC) != null || it.getAnnotation(CC) != null }
        .forEach { c ->
            c.methods.forEach { m ->
                val rt = m.returnType?.canonicalText ?: return@forEach
                if (rt.split(Regex("[<>,\\s]+")).any { it.endsWith("PO") })
                    out.add("[J13] ${f.name}:${lineOf(f, m.returnTypeElement ?: m)} ${snip(m.returnTypeElement ?: m)}")
            }
        }
}

fun checkJ14(f: PsiJavaFile, out: MutableList<String>) {
    val RC = "org.springframework.web.bind.annotation.RestController"
    val CC = "org.springframework.web.bind.annotation.Controller"
    val EH = "org.springframework.web.bind.annotation.ExceptionHandler"
    val RA = "org.springframework.web.bind.annotation.RestControllerAdvice"
    val CA = "org.springframework.web.bind.annotation.ControllerAdvice"
    f.descendantsOfType<PsiMethod>().filter { it.getAnnotation(EH) != null }.forEach { m ->
        val owner = m.parents.filterIsInstance<PsiClass>().firstOrNull() ?: return@forEach
        val isController = owner.getAnnotation(RC) != null || owner.getAnnotation(CC) != null
        val isAdvice = owner.getAnnotation(RA) != null || owner.getAnnotation(CA) != null
        if (isController && !isAdvice)
            out.add("[J14] ${f.name}:${lineOf(f, m.nameIdentifier ?: m)} ${snip(m.nameIdentifier ?: m)}")
    }
}

fun checkJ15(f: PsiJavaFile, out: MutableList<String>) {
    if (!OPT_IN) return
    val RC = "org.springframework.web.bind.annotation.RestController"
    val CC = "org.springframework.web.bind.annotation.Controller"
    val RB = "org.springframework.web.bind.annotation.RequestBody"
    f.descendantsOfType<PsiClass>()
        .filter { it.getAnnotation(RC) != null || it.getAnnotation(CC) != null }
        .forEach { c ->
            c.methods.forEach { m ->
                m.parameterList.parameters.forEach { p ->
                    if (p.getAnnotation(RB) == null) return@forEach
                    val t = p.type.canonicalText
                    if (t.startsWith("java.util.Map") || t.endsWith("JsonNode"))
                        out.add("[J15] ${f.name}:${lineOf(f, p)} ${snip(p)}")
                }
            }
        }
}

fun checkJ16(f: PsiJavaFile, out: MutableList<String>) {
    val last = f.packageName.substringAfterLast('.')
    val required: Pair<String, Boolean>? = when (last) {
        "controller" -> "Controller" to false
        "mapper" -> "Mapper" to false
        "manage" -> "Manage" to false
        "param" -> "Param" to false
        "dto" -> "DTO" to false
        "vo" -> "VO" to false
        "po" -> "PO" to false
        "converter" -> "Converter" to false
        "impl" -> "Impl" to false
        "service" -> "Service" to true
        else -> null
    }
    if (required == null) return
    val suffix = required.first
    val ifaceOnly = required.second
    f.classes.forEach { c ->
        if (ifaceOnly && !c.isInterface) return@forEach
        val n = c.name ?: return@forEach
        if (!n.endsWith(suffix))
            out.add("[J16] ${f.name}:${lineOf(f, c.nameIdentifier ?: c)} ${snip(c.nameIdentifier ?: c)}")
    }
}

fun checkJ17(f: PsiJavaFile, out: MutableList<String>) {
    val RC = "org.springframework.web.bind.annotation.RestController"
    val CC = "org.springframework.web.bind.annotation.Controller"
    val SVC = "org.springframework.stereotype.Service"
    val RA = "org.springframework.web.bind.annotation.RestControllerAdvice"
    val mod = ModuleUtilCore.findModuleForFile(f)
    val tier = tierOf(f)
    if (tier == null) {
        if (j17Noted.add("skip:" + (mod?.name ?: f.packageName)))
            out.add("[J17][SKIP] ${f.name} tier unresolved (module ${mod?.name ?: "?"}, package ${f.packageName}); set MODULE_MAP prefixes")
        return
    }
    val banned = BANNED_TIERS[tier] ?: emptySet()
    val root = rootPackageOf(f.packageName)
    if (root != null) {
        f.importList?.allImportStatements?.forEach { st ->
            val q = importFqn(st)
            val hit = banned.firstOrNull { q.startsWith(root + it + ".") }
            if (hit != null)
                out.add("[J17] ${f.name}:${lineOf(f, st)} $tier imports $hit package: ${snip(st)}")
        }
    }
    if (mod != null) {
        ModuleRootManager.getInstance(mod).dependencies.forEach { dep ->
            val depTier = tierFromTokens(dep.name.lowercase().split('-', '_', '.'))
            if (depTier != null && depTier in banned && j17Noted.add("graph:${mod.name}->${dep.name}"))
                out.add("[J17] ${f.name}:1 module ${mod.name} ($tier) depends on ${dep.name} ($depTier)")
        }
    }
    f.classes.forEach { c ->
        val n = c.name ?: return@forEach
        val dbAccess = n !in EXCLUDED_MAPPER_NAMES && (n.endsWith("Mapper") || n.endsWith("Manage"))
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
            out.add("[J17] ${f.name}:${lineOf(f, c.nameIdentifier ?: c)} $tier-tier class ${snip(c.nameIdentifier ?: c)}")
    }
}

fun checkJ18(f: PsiJavaFile, out: MutableList<String>) {
    val pkg = f.packageName
    val cls = f.classes.firstOrNull()?.name ?: ""
    if (pkg.endsWith(".config") || cls.endsWith("Config")) return
    f.importList?.allImportStatements?.forEach { st ->
        val q = importFqn(st)
        if (q.startsWith("com.baomidou.mybatisplus.extension.plugins.") &&
            !q.startsWith("com.baomidou.mybatisplus.extension.plugins.pagination."))
            out.add("[J18] ${f.name}:${lineOf(f, st)} ${snip(st)}")
    }
}

fun checkJ19(f: PsiJavaFile, out: MutableList<String>) {
    val LOG_METHODS = setOf("trace", "debug", "info", "warn", "error", "log")
    f.descendantsOfType<PsiCatchSection>().forEach { section ->
        val param = section.descendantsOfType<PsiVariable>().firstOrNull() ?: return@forEach
        val body = section.children.lastOrNull() ?: return@forEach
        val hasStatements = body.text.removePrefix("{").removeSuffix("}").trim().isNotEmpty()
        if (!hasStatements) {
            out.add("[J19] ${f.name}:${lineOf(f, section)} ${snip(section)}")
            return@forEach
        }
        if (!DETECT_SILENT_SWALLOW) return@forEach
        if (param.type?.canonicalText?.contains("InterruptedException") == true) return@forEach
        val referenced = body.descendantsOfType<PsiReferenceExpression>().any { it.reference?.resolve() == param }
        if (referenced) return@forEach
        val hasLog = body.descendantsOfType<PsiMethodCallExpression>().any { c ->
            (c.methodExpression as? PsiReferenceExpression)?.referenceName in LOG_METHODS
        }
        if (!hasLog && !body.text.contains(Regex("\\bthrow\\b")))
            out.add("[J19] ${f.name}:${lineOf(f, section)} ${snip(section)}")
    }
}

fun checkPY(f: PsiFile, out: MutableList<String>) {
    val name = f.name
    if (!name.endsWith(".py")) return
    val base = name.removeSuffix(".py")
    if (!Regex("^[a-z_][a-z0-9_]*$").matches(base))
        out.add("[PY1] $name:1 not snake_case")
    val m = Regex("sys\\s*\\.\\s*path\\s*\\.\\s*(append|insert|extend|remove)\\s*\\(").find(f.text)
    if (m != null) {
        val line = f.text.substring(0, m.range.first).count { it == '\n' } + 1
        val lineText = f.text.lineSequence().elementAt(line - 1).trim().let { if (it.length > 80) it.take(80) + "…" else it }
        out.add("[PY2] $name:$line $lineText")
    }
    if (OPT_IN && base in VAGUE_NAMES)
        out.add("[PY3] $name:1 vague name")
}

val batchAllRules = localInspection { psiFile, inspection ->
    val findings = mutableListOf<String>()
    if (FILES.size > 20)
        findings.add("[BATCH] ${FILES.size} files in one call; prefer chunks of ~15 per call")
    val targets: List<PsiFile> =
        FILES.mapNotNull { inspection.findPsiFileByRelativeToProjectPath(it) } +
            listOfNotNull(if (FILES.isEmpty()) psiFile else null)
    val missing = FILES.size - (targets.size - if (FILES.isEmpty()) 1 else 0)
    if (missing > 0) findings.add("[BATCH] $missing file(s) not found in project; check the FILES paths")
    targets.forEach { f ->
        if (f.name.endsWith(".java")) {
            val jf = f as? PsiJavaFile ?: return@forEach
            checkJ1(jf, findings); checkJ2(jf, findings); checkJ3(jf, findings); checkJ4(jf, findings)
            checkJ5(jf, findings); checkJ6(jf, findings); checkJ7(jf, findings); checkJ8(jf, findings)
            checkJ9(jf, findings); checkJ10(jf, findings); checkJ11(jf, findings); checkJ12(jf, findings)
            checkJ13(jf, findings); checkJ14(jf, findings); checkJ15(jf, findings); checkJ16(jf, findings)
            checkJ17(jf, findings); checkJ18(jf, findings); checkJ19(jf, findings)
        } else {
            checkPY(f, findings)
        }
    }
    val ruleFilePrefix = Regex("^(\\[[^]]+\\] [^:]+):")
    val aggregated = mutableListOf<String>()
    findings.groupBy { s -> ruleFilePrefix.find(s)?.groupValues?.get(1) ?: s }
        .forEach { (prefix, group) ->
            if (prefix != null && group.size > PER_RULE_PER_FILE)
                aggregated += group.take(PER_RULE_PER_FILE) +
                    "$prefix:… (+${group.size - PER_RULE_PER_FILE} more occurrences of this rule in this file, aggregated)"
            else aggregated += group
        }
    val batchNotes = aggregated.filter { it.startsWith("[BATCH]") }
    val real = aggregated.filterNot { it.startsWith("[BATCH]") }
    val output: List<String> =
        if (real.size <= MAX_FINDINGS) batchNotes + real
        else {
            val overflow = real.drop(MAX_FINDINGS)
                .groupBy { s -> s.substring(1, s.indexOf(']')) }
                .entries.joinToString(", ") { (rule, hits) -> "$rule ×${hits.size}" }
            batchNotes + real.take(MAX_FINDINGS) +
                "[BATCH] output truncated at $MAX_FINDINGS; not shown: $overflow — split FILES into smaller batches"
        }
    if (output.isEmpty())
        inspection.registerProblem(psiFile, "[BATCH] 0 findings in ${targets.size} file(s)")
    else
        output.forEach { inspection.registerProblem(psiFile, it) }
}

listOf(InspectionKts(id = "BatchAllRules", localTool = batchAllRules,
    name = "inspections/BatchAllRules.inspection.kts",
    level = HighlightDisplayLevel.WARNING))
