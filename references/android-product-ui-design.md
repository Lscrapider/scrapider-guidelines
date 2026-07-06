# Android Product UI Design Reference

Use this reference when designing or generating Android product UI without a fixed screenshot or mockup. Use it together with `android-kotlin-compose.md`.

## Product Design Order

Before creating screens:

1. Define the information relationship.
2. Identify the primary product scene.
3. Define state-specific layouts.
4. Choose the visual asset and icon strategy.
5. Then compose the screen structure and interaction path.

Do not start with colors, gradients, or decorative cards before the information relationship is clear.

## Information Relationship First

- Separate candidates, selected items, active execution, history, profile, and settings when they represent different product concepts.
- Do not mix multiple business states into one visual area just because they share data.
- Make the user's current task and next action obvious.
- Remove old UI sections when they no longer serve the confirmed product flow.

## Primary Scene

- Let the main product scene occupy the primary visual area.
- For map products, keep the map visually central.
- For content products, keep the content central.
- For tool products, keep the operation area clear and reachable.
- Do not let decorative cards, headers, or secondary buttons compete with the primary scene.

## State-Specific Layout

- Design generating, success, executing, completed, failed, empty, and loading states as distinct UI states.
- Do not only change a label while keeping an incompatible card structure.
- Give failure states a clear recovery path.
- Give loading and generating states meaningful progress or working feedback.
- Keep empty states product-facing and actionable when action is possible.

## Card And Component Semantics

- Do not use one generic card style for every object.
- Give route results, history records, user data, task execution, warnings, and summaries different structures when their semantics differ.
- Use repeated cards only for repeated objects of the same type.
- Avoid nested cards unless the design explicitly needs a contained repeated item.

## Icon, Illustration, And Motion Strategy

- Use icons and illustrations as product identity, not filler.
- Keep icons from one source or one generated asset set.
- Do not hand-draw VectorDrawable, inline SVG, or Compose vector paths for product icons.
- Do not use Material Icons as a default fallback when they do not match the product tone.
- Generate or request bitmap assets for important navigation icons, city/map imagery, empty states, success feedback, route, map, warning, favorite, like, and dislike visuals when no approved asset set exists.
- Use motion to explain state: location changed, system working, result completed, action confirmed.
- Do not add motion only to make the page look busy.

## Mobile Ergonomics

- Prioritize thumb-reachable primary actions.
- Keep maps, content, camera, or editor surfaces large enough to inspect.
- Collapse or defer secondary filters and actions when space is tight.
- Do not copy desktop horizontal density into mobile screens.
- Design detail pages, failed states, empty states, confirmation dialogs, and edit flows with the same product quality as the first screen.

