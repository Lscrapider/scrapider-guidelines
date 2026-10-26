# Design and Coding Capability Examples

Use these complete entries as examples for a project's `AGENTS.md`; select only routes that match
the author's chosen standards and the work the project actually performs. Combine capabilities when
each has a distinct responsibility. Keep the standalone entry when only one of `frontend-design` or
`impeccable` is selected; when both are selected, use the grouped entry and apply each skill only
under its own trigger. Add a coding standard only when it has been selected for implementation work.

## Coding Standards

### Scrapider Guidelines Only

```md
- **Scrapider Guidelines**
  - **Scenario:** Implementing, refactoring, debugging, or reviewing backend, data, or general application code in a project that selected Scrapider as its coding standard.
  - **Boundary:** Scrapider governs engineering decisions and review; it does not establish a visual direction. Existing project contracts and explicit task requirements remain authoritative.
```

### Existing Project Standards, Without Scrapider

```md
- **Existing project coding guidance**
  - **Scenario:** The project already has maintained `AGENTS.md` instructions, coding standards, or architecture guidance, and its author has not selected Scrapider.
  - **Boundary:** Use the project's guidance as the coding standard; do not add Scrapider rules by default or infer its adoption from the presence of this example.
```

## Web UI Design and Implementation

Selection note: Choose `frontend-design` alone for direction-only work, `impeccable` alone for
polish within an existing direction, and their combined route when the author selects both. In the
combined route, use each skill only for its task-specific trigger; add Scrapider separately when it
has been selected for code changes. These selections do not imply one another.

### `frontend-design` Alone

```md
- **frontend-design**
  - **Scenario:** The author wants a new web visual direction, design exploration, or a design specification without selecting a separate UI-refinement or coding layer for this work.
  - **Boundary:** `frontend-design` owns the new direction. It does not replace the project's implementation standards; any later code work follows the separately selected project guidance.
  - **Skip:** An existing interface only needs targeted polish within its current design language.
  - **Fallback:** If the direction cannot be resolved from the brief, preserve the project's established design system and identify the remaining decision rather than inventing a brand direction.
```

### `impeccable` Alone

```md
- **impeccable**
  - **Scenario:** An existing web interface needs a UX or visual audit, clearer hierarchy, stronger states, accessibility improvements, or polish while its design language remains in place.
  - **Boundary:** `impeccable` refines the existing or already chosen direction; it does not introduce a new visual identity or replace the project's coding rules.
  - **Skip:** A new application or an explicitly requested visual redesign needs a new direction.
  - **Fallback:** If the existing system or evidence is incomplete, keep the parts that are established and avoid speculative restyling.
```

### `frontend-design` → `impeccable` + Scrapider Guidelines

```md
- **frontend-design + impeccable** — Web visual direction and refinement.
  - **Scenario:** Use `frontend-design` for a new web UI or an explicitly requested new visual direction; use `impeccable` for hierarchy, interaction states, accessibility, consistency, and polish within an existing or accepted direction.
  - **Boundary:** When both are needed, `frontend-design` establishes the direction and `impeccable` refines it. Existing UI refinement uses only `impeccable`. Preserve project tokens, behavior, contracts, and explicit requirements; code standards stay with the selected coding guidance.
- **Scrapider Guidelines** — Apply the selected engineering standards to implementation.
  - **Scenario:** Implementing the approved direction and refinements in application code.
  - **Boundary:** Owns engineering decisions and review; it does not choose the visual direction, and existing contracts and explicit task requirements remain authoritative.
```

### `impeccable` + Scrapider Guidelines for an Existing UI

```md
- **impeccable** — Refine an existing web UI in place.
  - **Scenario:** The existing web interface needs targeted UX or visual improvement while its current design language stays in place.
  - **Boundary:** Owns UI refinement and preserves existing tokens, layout conventions, and behavior; it does not establish a new direction or coding rules.
  - **Fallback:** If a visual issue cannot be verified, limit changes to the evidence-backed request and state what remains unverified.
- **Scrapider Guidelines** — Apply the selected engineering standards to UI code changes.
  - **Scenario:** Implementing the approved local refinements in the existing interface.
  - **Boundary:** Owns engineering standards for code changes; nonvisual logic and API work remain governed by their applicable project rules.
```

## Android and Compose

Selection note: Use `compose-expert` alone for a Compose-specific task when Scrapider is not the
selected coding standard. Add Scrapider Android guidance when the project selected it for code
work; use that guidance alone when no separate Compose specialist is selected or needed.

### `compose-expert` Alone

```md
- **compose-expert**
  - **Scenario:** A focused Compose API question, Compose-specific UI implementation, or Compose review where no separate Scrapider coding layer has been selected for the task.
  - **Boundary:** `compose-expert` owns Compose-specific reasoning; the project's existing Android architecture, design system, and coding instructions still govern the application.
  - **Skip:** The task concerns general Android or non-Compose code with no Compose-specific question.
```

### `compose-expert` + Scrapider Android Guidance

```md
- **compose-expert** — Resolve Compose-specific implementation and review concerns.
  - **Scenario:** Implementing, refactoring, debugging, or reviewing Compose code in an Android project that selected Scrapider as its coding standard.
  - **Boundary:** Owns Compose APIs and UI-specific concerns; the project's design system and explicit product requirements remain authoritative. Scrapider Android guidance owns broader engineering standards.
- **Scrapider Android guidance** — Apply the selected Android engineering standards.
  - **Scenario:** The same Compose code change requires the project's selected Android coding and review rules.
  - **Boundary:** Owns broader Android architecture, contracts, and validation; `compose-expert` owns specialized Compose questions, so neither entry replaces the other's responsibility.
```

### Scrapider Android Guidance Only

```md
- **Scrapider Guidelines — Android Kotlin and Compose guidance**
  - **Scenario:** Android Kotlin implementation or review in a project that selected Scrapider, when the task can be handled by the project's Android guidance without a separate Compose specialist.
  - **Boundary:** Scrapider guidance is the Android coding standard, including the applicable Compose reference; it does not select Apple Design, `impeccable`, Product Design, or another UI direction.
  - **Skip:** A material Compose-specific API or interaction issue requires specialist reasoning beyond the project's selected Android reference.
```

## Author-Selected Android Design Extensions

These extensions are examples of existing author choices, not default recommendations. Preserve
the project's own components, tokens, interaction patterns, and product requirements over borrowed
visual conventions.

Selection note: Use Apple Design alone only for an author-selected interaction or motion approach.
Product Design can assess real screens and flows on its own, or join the full Compose combination
when those findings must inform Compose UI refinement. ImageGen is a separate asset-only choice; the
complete design-generation-coding example below applies only when that full route has been selected.

### Apple Design Alone

```md
- **Apple Design**
  - **Scenario:** Use only when the author has selected Apple Design for a gesture-driven, motion-focused, or spatial interaction design task.
  - **Boundary:** Apple Design contributes interaction and motion principles; the product's existing platform patterns, visual system, and explicit project constraints remain authoritative. This entry does not imply Apple-specific visual styling or select implementation standards.
  - **Fallback:** If a proposed pattern conflicts with the product's Android or web conventions, retain the product convention and follow its existing design guidance.
```

### Product Design Alone for Real Screens or Flows

```md
- **Product Design**
  - **Scenario:** Use when the author has selected Product Design and the task is to assess or improve a real product screen, screenshot, page sequence, or user flow without adding a separate design-direction or coding capability.
  - **Boundary:** Product Design owns the evidence-based flow or screen assessment; the existing product design system and project coding guidance remain the authorities for visual identity and implementation.
  - **Skip:** No real screen or flow evidence is available, or the task asks for a new direction without an existing experience to assess.
  - **Fallback:** If the evidence is incomplete, preserve unknown behavior, state the gap, and continue only from the supplied brief and existing project system.
```

### Author-Selected: Apple Design + `impeccable` + `compose-expert`

```md
- **Apple Design** — Provide the selected interaction and motion direction.
  - **Scenario:** Use only if the author has already selected this combination and the Android Compose screen needs interaction or motion design.
  - **Boundary:** Contributes motion and interaction principles; it does not replace Android conventions. `impeccable` owns visual refinement and `compose-expert` owns Compose implementation; existing product components and tokens take priority.
  - **Fallback:** If a proposed pattern conflicts with existing components or tokens, retain the product system.
- **impeccable** — Refine the selected Android screen.
  - **Scenario:** The same selected screen also needs hierarchy, state, accessibility, or visual refinement.
  - **Boundary:** Refines within the product's design language and the approved interaction direction; it does not override Android conventions or select implementation standards.
  - **Fallback:** If the evidence does not support a refinement, retain the existing design rather than adding speculative changes.
- **compose-expert** — Implement the approved interaction and visual decisions in Compose.
  - **Scenario:** The approved interaction and visual refinement must be implemented in Compose.
  - **Boundary:** Owns Compose-specific implementation; the product design system, platform conventions, accessibility requirements, and explicit project rules remain authoritative.
```

### Author-Selected: Apple Design + `impeccable` + `compose-expert` + Product Design

```md
- **Product Design** — Supply evidence about the selected product screen or flow.
  - **Scenario:** Use only when the author has selected this full combination and supplied actual Android screenshots, a page sequence, or an authorized flow to assess or implement faithfully.
  - **Boundary:** Owns evidence about the existing screen and flow; Apple Design, `impeccable`, and `compose-expert` use that evidence for their separate responsibilities. Product Design does not override the existing system or authorize unobserved behavior.
  - **Fallback:** If the supplied flow is incomplete, preserve unknown behavior and state what cannot be established from the evidence.
- **Apple Design** — Apply selected interaction and motion principles to the evidenced flow.
  - **Scenario:** The author has selected the full combination and the evidenced Android Compose flow needs a motion or interaction direction.
  - **Boundary:** Uses Product Design evidence to inform interaction and motion; it does not invent behavior or replace Android conventions. `impeccable` owns visual refinement and `compose-expert` owns implementation.
  - **Fallback:** If the screenshots or flow leave behavior unknown, preserve the existing product behavior and state the gap.
- **impeccable** — Refine the evidenced experience within its design system.
  - **Scenario:** The same evidence-based Compose flow needs visual hierarchy, state, accessibility, or polish.
  - **Boundary:** Refines within the product's existing design language and approved interaction approach; it does not extend the flow beyond Product Design evidence or override platform conventions.
  - **Fallback:** If a refinement is unsupported by screen evidence or the existing design system, keep the established pattern.
- **compose-expert** — Implement the approved flow in Compose.
  - **Scenario:** The approved flow and visual refinements must be implemented in Compose.
  - **Boundary:** Owns Compose-specific implementation; the product's design system, Android platform conventions, and separately selected coding guidance remain authoritative. This entry does not add Scrapider.
```

### ImageGen Alone for a Missing Asset

```md
- **ImageGen**
  - **Scenario:** Use only when the author has selected image generation, a required image asset is missing, and the author has authorized generating that asset.
  - **Boundary:** ImageGen supplies the missing raster asset only; the product's visual identity and asset use remain governed by the existing design system. This entry does not select UI design or coding standards.
  - **Fallback:** Reuse an approved asset or leave the asset unresolved for the author; do not generate a substitute automatically.
```

### Author-Selected: `frontend-design` → `impeccable` + ImageGen + Scrapider Guidelines

```md
- **frontend-design + impeccable** — Web visual direction and refinement.
  - **Scenario:** Use `frontend-design` for a new web UI or an explicitly requested new visual direction; use `impeccable` for UX, hierarchy, interaction states, accessibility, consistency, and polish within an existing or accepted direction.
  - **Boundary:** When both are needed, `frontend-design` establishes the direction and `impeccable` refines it. Existing UI refinement uses only `impeccable`. The project's design system, contracts, and explicit requirements constrain the result; ImageGen supplies the selected missing asset and Scrapider governs code changes.
- **ImageGen** — Create only the missing raster asset.
  - **Scenario:** The selected route requires an asset that is missing, and the author has authorized its generation.
  - **Boundary:** Supplies the asset only; the approved direction and product identity govern its use, while Scrapider governs code integration.
  - **Fallback:** If an approved asset exists or generation is not authorized, reuse it or leave the image unresolved.
- **Scrapider Guidelines** — Govern implementation and review.
  - **Scenario:** Implementing the approved design, refinements, and authorized asset in code under the project's selected coding standard.
  - **Boundary:** Owns engineering standards; it does not select the visual direction or authorize generation of brand assets.
```
