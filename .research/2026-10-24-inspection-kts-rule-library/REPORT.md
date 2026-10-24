# inspection.kts 规则库建设实录（2026-10-24）

从 scrapider-guidelines 各规范中抽取机械可判定规则 → 22 条 `.inspection.kts`（17 默认 + 5 opt-in），
落位 `scripts/inspections/`；操作性文档在 `references/shared/inspection-scripts.md`（英文，按需路由）。
本文件是过程归档，不属于 skill 加载路径。

## 实测环境与总量

- 目标项目：ORVX（backend 多模块 + worker Python），IDEA 2026.1 同窗三开；
- 试验台：armJ2（ORVX 的 scratch 副本，`.scratch-20261024/armJ2`）；
- 方法：真实文件 + `run_inspection_kts` 的 `targetFileContent` 合成用例（违规与干净对照同文件）；
- 总量：43 轮 `run_inspection_kts`（含 7 轮编译迭代与复审修复复测）+ 4 轮辅助工具（examples/api/psi_tree）；
- 平均每规则 1.5 轮迭代，最差 J3/J5/J17 各 3 轮。

## 逐规则实测记录

| 规则 | 迭代 | 命中/用例 | 真实仓库结论 |
| --- | --- | --- | --- |
| J1 | 2 | 3/3 合成（字段 Mapper/Manage、构造器 Manage） | 主代码无存量违规；ObjectMapper 排除后不误报 |
| J2 | 2 | 1/1 真实 + ObjectMapper 0 误报 | 存量违规仅 1 处：SysConfigServiceImpl:29 直连 SysConfigMapper |
| J3 | 3 | 5/5 合成 | 抽查 OperateLogService 合规；未全量扫 |
| J4 | 1 | 3/3 真实 | 88/758 主代码文件缺 @Author（分布：foundation-entity 42、business-system 22、foundation-db 13、其他 11）；规范要求存量不重写 |
| J5 | 3 | 2/2 + 4 干净 | 抽查 CreateDatasetParam 合规 |
| J6 | 1 | 2/2 合成 | 存量违规：AnnotationSpotCheckServiceImpl:75 @Autowired(required=false) |
| J7 | 2 | 2/2 合成 | 全仓（含测试）0 wildcard import |
| J8 | 1 | 3/3 合成 | 主代码 0 违规；测试代码 2 处 System.out（DatasetRegistrationServiceImplTest:484,509） |
| J9 | 2 | 2/2 合成 | 主代码无日志拼接 |
| J10 | 1 | 2/2 + 4 干净 | 主代码无枚举 equals（grep 候选全为 String.equals，类型解析正确排除） |
| J11 | 1 | 2/2 + 2 干净 | 主代码 0 违规；测试断言 static import 不误报 |
| J12 | 1 | 1/1 + 3 干净 | 未对存量全量跑（opt-in 定位） |
| J13 | 1 | 3/3 + 2 干净 | 抽查 Controller 无 PO 返回 |
| J14 | 1 | 1/1 + Advice 干净 | 主代码 0 违规 |
| J15 | 1 | 1/1 真实 | 存量违规：QualityRuleController:173 @RequestBody Map<String,String> |
| J16 | 2 | 1/1 + 真实干净 | vo/dto/controller 包全量后缀合规 |
| J17 | 3 | 4/4 合成 + 真实干净 | 全仓无反向 import（bash 交叉验证） |
| J18 | 2 | 2/2 + 真实豁免 | 仅 MybatisPlusConfig 引用插件，合规 |
| J19 | 2 | 2/2 + 4 干净 | 主代码未全量跑 |
| PY1 | 1 | 干净通过 | worker 无非 snake_case 文件（违规路径无真实靶子） |
| PY2 | 1 | 1/1 合成（行号正确） | worker 无 sys.path 变更 |
| PY3 | 1 | 干净通过 | worker 无模糊命名文件 |

## 复审修正（交叉核对发现）

初版 J1/J2/J17 的 `endsWith("Mapper")` 会把 Jackson `ObjectMapper` 误判为数据库 Mapper——
全仓 9 处 `private final *Mapper` 中 8 处是 ObjectMapper，初版 grep 口径误报为"5 个 ServiceImpl 违规"。
修复：三份脚本加 `EXCLUDED_TYPES` 排除清单；复测真 Mapper 仍命中、ObjectMapper 零误报。

## 通道实测（armJ2）

- `run_inspection_kts`：重启前后均可靠，agent 唯一通道。
- `get_file_problems`：只返回内置检查（含 Spring 注入建议、装配歧义等），自定义脚本装入
  `.idea/inspections/` 后即时与重启后均不出现（内置消息 ≠ 脚本消息，看 `[J2]`/`[J6]` 前缀区分）。
- 编辑器高亮：重启后用户目视未见；该版本 IDE 对 `.idea/inspections/` 下 kts 的拾取未确认。
- 附带发现（内置检查送的真问题）：AnnotationSpotCheckServiceImpl:71 ERROR 级装配歧义，
  `MinioStorageService` 有两个实现 Bean（minioBackendStorageService / rustFsStorageService）。

## 架构演进过程（最终形态见英文 reference，此处仅记过程）

1. 初版：规则库 + README 放 `references/inspections/`（草案）→ 用户裁定代码归 `scripts/`。
2. 二版：`scripts/inspections/` + README；「Scripted Mechanical Checks」小节嵌入 5 个规范文件。
3. 三版：README 迁 `references/shared/`；发现"速查表在 README、又说 agent 不读 README"的自我矛盾
   → 小节补适用范围。
4. 终版（用户裁定）：规范文件回退纯净（工具层信息不得进入任何常规加载路径）；inspection 能力
   收敛为单一英文 reference（`references/shared/inspection-scripts.md`），由 reviewer 定义与 SKILL.md
   路由行按条件（review + IDE 通道可用）加载；过程性内容全部移出加载路径，归档于本文件。

## inspection.kts 编写踩坑（实测得出，英文 reference 收操作版）

1. 一个脚本只放一条 InspectionKts（多条目只执行第一条）。
2. 部分 PSI 类型显式引用不可解析（PsiDocComment、PsiDocParamRef、PsiImportStatementBase.qualifiedName、
   PsiCatchSection.catchParameter/catchBody、PsiReference.referenceName）——隐式属性链可用。
3. 紧贴声明的注释绑定进声明节点 text 区间，判定注释用声明自身 text 前缀。
4. `targetFileContent` 模式下 `virtualFile.path` 不可靠，用 `packageName` 前缀定位。
5. static import 不在 `importList.importStatements`，用 `allImportStatements`；通配判定用文本 `.*;`。
6. 调试三板斧：`generate_psi_tree`、`generate_inspection_kts_api/examples`、`registerProblem` 临时打点。

## 批量驱动（追加，2026-10-24 晚）

子 agent 全链路实测后用户追问扩展性（几十文件场景）。三路试验：
1. localInspection + findPsiFileByRelativeToProjectPath + 跨文件 registerProblem(element) →
   失败：运行器按 context 文件偏移校验描述符（Wrong offset），一个越界毁掉整单。
2. globalInspection 变体 → 失败：MCP 运行器拒绝（"Compiled inspection is not a local inspection tool"）。
3. 消息聚合（跨文件扫描 + findings 带内嵌 file:line 锚回 context 文件）→ 成功。

据此新增 scripts/inspections/BatchAllRules.inspection.kts：22 条规则逻辑（与单发版逐字一致）
× FILES 清单循环 × 消息聚合，内置 java/py 路由与 OPT_IN 开关。4 文件 ground truth 回归：
一次编译通过，10/10 findings 与子 agent 27 发逐条等价（J2:29 / J6:74 / J15:173 / J12×7），0 误报。

调用数对比：逐发模式 4 文件 27 发（预筛后）；批量模式任意文件数 1 发。
reviewer 协议与 reference 已改为"批量优先，单发仅用于单文件单规则定位"。

## 批量模式限流（追加，用户复审指出返回体积无上界）

初版批量驱动只解耦了调用数，未限返回体积：100 文件 × 高噪声规则（J12 存量单文件即数十条）
一次可回传数百条 findings（每条还带重复的 elementText 片段），reviewer 上下文会被击穿。
修复为双侧限制并实测：
- 调用侧：reference 规定每批 ~15 个文件，脚本对 FILES>20 输出警告；
- 脚本侧：PER_RULE_PER_FILE=5（同规则同文件超出部分折叠为一行计数）、MAX_FINDINGS=60
  （超出截断并按规则统计 overflow，提示缩块或关 OPT_IN）。
实测：压力参数（3/6）下聚合与截断两条路径输出符合预期（9 条→6 条 + "not shown: J12 ×2, J15 ×1"）；
默认参数（5/60）下 4 文件 10/10 原样输出、零误触发。最坏返回体积被钉在 ~60 条 + overflow 行。

## 规范 review 与通用化整改（追加，用户裁定）

按 skill 规范整体 review 后用户裁定两项 P2 + 三项 P1：
1. **仓库家规出库**（"通用 skill 不收别的仓库的规则"）：删除 J3（Service javadoc）、J4（@Author
   文件头）、J5（Param 字段注释）、J19（catch 吞异常）四个脚本及其表行——规范出处分别是
   ORVX 的 AGENTS.md 与仓库文件风格，非 skill 通用 references。这四块回归
   standards-reviewer 语义层。库收敛为 18 条 + BatchAllRules（批量驱动同步删 4 个 check 与
   REQUIRED_TAGS 常量）。
2. **头注释英文化 + 去实测块**：18 个脚本头部改为英文（Rule/Source/Level/Config），实测记录
   仅存本文件；运行时消息保持中文（服务中文语境项目）。
3. BatchAllRules 默认 FILES 清空（去 ORVX 路径残留，空表回退 contextPath）；
   reference 开头描述改为"one script per rule plus a batch driver"；boundaries 段明示
   仓库约定型检查（接口 javadoc/文件头模板/字段注释/静默吞异常）不在库内；
   capability-recommendations 的能力描述措辞同步（去 "required javadoc"）。
J2/J17 保留：规则本体出自通用 references（manage 层禁令/模块方向禁令），仓库差异走配置开关，
且配置不匹配时静默不报（无误报风险）。

## 四条规则通用化回归（用户裁定）

用户复核删除清单后裁定：四条要求本身正确，"想办法改成通用的，但不能出现 orvx"。
处理：逻辑进库、适用性由仓库激活（约定驱动开关，agent 读目标仓库指令后设置），全部
默认关闭、零 orvx 措辞/默认值/路径：
- J3：REQUIRE_SERVICE_JAVADOC（默认 false）；规范锚点 = backend.md 文档节
  "follow the repository's method-level JavaDoc convention, including its parameter and return tags"。
- J4：REQUIRED_TAGS 默认空列表 = 关闭；仓库填自己的模板 tag 才生效
  （锚点 = backend.md "follow an established template exactly; do not impose one when absent"）。
- J5：REQUIRE_PARAM_FIELD_COMMENTS（默认 false）；判定三形态任一即合规（通用）。
- J19 拆两档：空 catch 块（零语句，纯结构判定）默认常开；"非空但静默吞"为
  DETECT_SILENT_SWALLOW（默认 false），InterruptedException 豁免保留。
BatchAllRules 常量块集中四开关并注明"running agent 按目标仓库指令设置"。
库恢复为 22 规则 + 批量驱动 = 23 文件。开关机制与 J2 的 REQUIRE_MANAGE_LAYER 同式。

## 消息极简化（用户裁定：返回只留位置+行数+关键代码）

按 MCP 实际返回行为分模式设计（用户提示"MCP 返回是英文的、不同脚本返回情况不同，按实际考虑"）：
- 单发锚定模式：结构化 lineNumber/elementText 已承载真实行号与代码 → 消息仅 "[Jn]"
  （缺失型加 "missing @param x on m()"；文件级 PY 规则加 "not snake_case"/行号+命中行）。
- 批量模式：结构化字段指向 context 文件（无意义）→ 消息自足 "[Jn] file:line 代码片段"
  （snip：压空白、截 80 字符）。
顺带删除全部 22 个脚本的 htmlDescription（IDE 描述文本，agent 通道死重）。
回归：单发 J2 → message="[J2]"+lineNumber=29+elementText 真实字段代码；
批量 4 文件 → 10/10 命中，消息 50-90 字符/条（旧版 150-250）。reference 增补
Message minimalism 与 Convention switches 两条协议说明（开关=运行 agent 改脚本文本常量）。

## 连续 Edit 缺陷事故与文件级验证纪律（追加）

消息极简化的连续 Edit 中，J1/J2/J3 的 new_string 丢失了常量定义（EXCLUDED_TYPES/
REQUIRE_MANAGE_LAYER/REQUIRE_SERVICE_JAVADOC 被 lambda 引用但未定义，编译必挂），
而回归测试传的是手工拼装文本、非磁盘文件原文，掩盖了损坏。教训固化为纪律：
**规则脚本的回归必须传磁盘文件原文**（Write 后文件即原文）。已按此补验：
J1（合成 2/2，ObjectMapper 排除生效）、J2（真实 1/1）、J3（默认关惰性）、
BatchAllRules（空 FILES 回退 contextPath，全文件编译运行通过）。

## J17 拓扑中性化（"不能出现 orvx"的完全落地）

moduleOf 与 banned 列表的 com.orvx 默认前缀全部替换为
com.example 占位（model/persistence/shared/bootstrap/business 通用模块名）——出厂即惰性，
运行 agent 按目标仓库真实前缀/模块名替换后生效（与其他约定开关同一哲学）。
双向实测：占位默认 × orvx 真实文件 = 0 误报惰性；agent 适配版 × entity 合成违规 = 3/3。
脚本目录现已零 orvx 痕迹（grep 终检通过）。
