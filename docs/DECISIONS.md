# DECISIONS — Mogg Mining World

Зафиксированные архитектурные решения. Смена любого из них — только через
отдельное обсуждение (запись даты + причины + «do not»).

## 2026-08-14 — Генерация измерений через датапаки, а не Java-код
Decision: измерение, биом, noise-settings, фичи — все файлы в
`src/main/resources/data/`, Java-код только точка входа и клиентские эффекты.
Reason: так Forge 1.20.1 регистрирует измерения; меньше кода, легче
итерировать, полностью видно в git.
Do not: не переносить генерацию в Java без веской причины.

## 2026-08-14 — Кастомный noise-settings `moggminingworld:caves`
Decision: свой файл вместо ссылки на ванильный `minecraft:caves`.
Reason: ванильный пресет строит рельеф Overworld-стиля (континенты/эрозия/
аквиферы) — это давало воду и «перевёрнутый Overworld». Свой пресет:
`final_density` константа → сплошной камень, `aquifers_enabled: false`,
`ore_veins_enabled: false`, `sea_level: 0` при `min_y: 0`.
Do not: не возвращать ванильный `minecraft:caves` без пересмотра.

## 2026-08-14 — Вода и лава отсутствуют по построению
Decision: `sea_level: 0`, `min_y: 0`, `aquifers_enabled: false` — воде и лаве
физически негде появиться. Лава внизу не добавляется.
Reason: ТЗ — «только пещеры и руды, без поверхности». Вода/лава усложняют
майнинг и не нужны.
Do not: не включать воду/лаву в базовую генерацию (исключение — только
если это осознанная фича отдельного этапа).

## 2026-08-14 — Руды — собственные configured/placed фичи
Decision: каждая руда — пара файлов `ore_*.json` (`minecraft:ore`,
`discard_chance_on_air_exposure: 0`, таргеты stone→`*_ore`, deepslate→
`deepslate_*_ore`) + placed с `count`/`in_square`/`height_range`/`biome`.
Reason: не зависеть от имён ванильных `minecraft:ore_*`, обрезать диапазоны
под колонну 0..320, не генерировать руды на недостижимых Y.
Do not: не переходить на ванильные ссылки; плотности менять только через
планируемый конфиг (Этап 10).

## 2026-08-14 — Bedrock-потолок/пол через `fill_layer`
Decision: `minecraft:fill_layer` configured feature на шаге RAW_GENERATION
(как в ванильном Nether), а не правка surface_rule чужого noise settings.
Reason: низкий риск, идемпотентно, гарантированные границы мира.
Do not: не менять на surface_rule-правки в чужих пресетах.

## 2026-08-14 — Потолок плоский (Y=319), пол (Y=0)
Decision: один сплошной слой bedrock без «рваного» края.
Reason: просто и безопасно; рваный вид — только полировка (Этап 12).
Do not: не добавлять неровный край до этапа полировки.

## JSON-нюансы 1.20.1 (ловим краши; не забывать)
- `carvers` в биоме — объект `{"air": [...], "liquid": [...]}`, НЕ массив.
- RuleTest использует `predicate_type`, а не `type`.
- HeightProvider `uniform` — `max_inclusive`, не `max_exclusive`.