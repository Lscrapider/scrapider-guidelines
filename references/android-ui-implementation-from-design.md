# Android UI Implementation From Design Reference

Use this reference when implementing Android UI from screenshots, mockups, design images, or an existing product UI. Use it together with `android-kotlin-compose.md`.

## Implementation Order

Before writing Compose code:

1. Compare the real UI structure block by block.
2. Compare the data fields by business meaning.
3. Compare icon and visual asset sources.
4. Decide the layout constraints, spacing, alignment, and list behavior.
5. Then implement the smallest Compose change that matches the design.

Do not write an approximate version first and rely on later visual corrections.

## Structure Matching

- Preserve the design's real hierarchy: cards, independent regions, overlays, tabs, bottom actions, and list items.
- Do not merge independent areas into one card because it is easier to code.
- Do not keep old UI containers when the confirmed design removes their purpose.
- Do not let old interaction paths force new UI structure.
- If the design has tabs, bottom sheets, floating actions, or separated sections, model those structures explicitly.
- Treat structural mismatches as implementation bugs, not visual polish.

## Data Mapping

- Bind UI fields by business semantics, not by names that look similar.
- Do not use `label`, `name`, mock text, or fallback display values to impersonate missing business fields.
- Do not use `firstOrNull()` or a single item when the UI represents aggregated or grouped state.
- If distance, duration, count, summary, status, or stops already exist in a route summary or backend VO, use those fields directly.
- If the needed field is unclear or absent, inspect the backend VO, mapper, API contract, or existing state model before inventing UI logic.
- Keep DTO-to-UI mapping outside Composables.

## List And State Counts

- Do not hard-code item counts from the design image.
- A design showing three sample items means the sample data has three items, not that production UI must show three items.
- Render all available items unless the product requirement explicitly limits the count.
- Use scrolling, paging, collapsing, or size constraints when the real list can exceed the visual sample.
- Aggregate states from real data. If two failed groups exist, show two failed groups. If no item is generating, show the empty or idle state.
- Do not replace real state aggregation with a single card because the design image only shows one card.

## Icon And Visual Asset Rules

- Treat icons as part of UI fidelity, not as minor implementation details.
- Use one consistent icon source for the same screen or feature.
- Do not hand-draw VectorDrawable, inline SVG, or Compose vector paths for product icons.
- Do not use Material Icons as a casual fallback. Use Material Icons only when the existing app design system already uses them and the target icon matches the required style.
- Do not mix icon styles, stroke widths, corner styles, filled/outlined styles, or visual weights in the same feature.
- For key product icons such as favorite, like, dislike, route, map, warning, navigation, empty state, and success feedback, prefer existing approved app assets or generated bitmap assets as a matched set.
- If the design image shows custom icons and no matching asset exists, stop and ask whether to generate bitmap assets or choose a specific icon library.

## Mobile Layout Quality

- Treat spacing, alignment, fixed dimensions, row height, button width, and padding as functional quality.
- Decide stable column widths, row heights, horizontal weights, and touch targets before filling content.
- Keep primary actions reachable by thumb when practical.
- Preserve enough visual area for the primary scene, such as map, content, camera, or editor surface.
- Do not let text, badges, icons, or dynamic content resize controls unexpectedly.
- Verify that text fits and aligns on realistic mobile widths.

