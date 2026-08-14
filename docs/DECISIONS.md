# DECISIONS — Mogg Mining World

Зафиксированные архитектурные решения. Смена любого из них — только через
отдельное обсуждение (запись даты + причины + «do not»).

## 2026-08-14 — Configured/PlacedFeature НЕ регистрировать через DeferredRegister
Decision: Java-код регистрирует только сам `Feature` (`Registries.FEATURE`,
`moggminingworld:dynamic_ore`), а configured/placed фичи объявляются в
датапаке (`worldgen/configured_feature/dynamic_modded_ores.json` +
`worldgen/placed_feature/dynamic_modded_ores.json`, rarity 1/24, Y 0..120).
Reason: краш при старте игры — `Unable to find registry with key
minecraft:worldgen/placed_feature` из `DeferredRegister.register()`
(ModWorldGen.<clinit>). ConfiguredFeature/PlacedFeature — datapack-реестры,
которые создаются только при загрузке датапаков, поэтому на этапе
регистрации мода их нет, и `RegistryObject` падает.
Do not: не возвращать DeferredRegister для `Registries.CONFIGURED_FEATURE` /
`Registries.PLACED_FEATURE`; Java-фичи всегда объявлять через датапак.

## 2026-08-14 — Авто-подхват руд из других модов (Этап 5, ТЗ п.3) ✅
Decision: руды других модов автоматически появляются в Mining World:
- Java `DynamicOreFeature` сканирует реестр блоков при первой генерации
  чанка (id на `_ore` или тег `forge:ores`, ванильные `minecraft:*`
  исключены — они уже в Этапе 3);
- stone/deepslate варианты по имени, тиры глубины COMMON/MID/DEEP/BOTTOM
  (Y 0..120 / 0..64 / 0..48 / 0..24), ванильный blob-алгоритм, size 5..10;
- configured/placed `dynamic_modded_ores` через DeferredRegister + biome
  modifier `forge:add_features` (step `underground_ores`) в биом `mining_world`.
- Новый мод → перезагрузка сервера → руды появляются; чанки пересоздать.
Reason: ТЗ п.3; руды чужих модов заранее неизвестны → чистый датапак
невозможен, нужно сканирование реестра.
Do not: не переносить остальную генерацию в Java; не сканировать не-рудные
блоки; не трогать ванильные руды Этапа 3; не менять частоту/тиры без
live-теста.

## 2026-08-14 — Нижний бедрок должен быть скрыт (фикс по фидбеку теста)
Decision: видимый ровный `bottom_bedrock` (fill_layer на Y=0) — баг с точки
зрения игрока: в верхнем мире бедрок не виден, он укрыт толщей камня сверху
и имеет неровный «рваный» край. Требование: снизу должно быть как в верхнем
мире (бедрок под слоями породы). Реализация — в Этапе 10 (варианты: слой
камня поверх бедрока, min_y < 0, рваный край через несколько fill_layer-слоёв).
Reason: реальный игровой тест показал голый бедрок в нижних пещерах.
Do not: НЕ удалять бедрок полностью — только скрыть его под породой.

## 2026-08-14 — Пещеры/данжи/структуры «как в верхнем мире»
Decision: в Mining World должны быть такие же механики, как в верхнем мире
под землёй: данжи (mineshaft, monster_room), рудные кэши, декорации пещер
(speleotems), баланс шансов руд по глубине. Всё через датапаки.
Reason: фидбек игрового теста — сейчас пещеры пустые, нет структур.
Do not: не делать структуры Java-кодом; не менять подход датапаков.

## 2026-08-14 — Структуры-данжи через ванильные типы + свои structure_set
Decision: для данжей в Mining World переиспользуются ванильные типы
структур (`minecraft:monster_room`, `minecraft:mineshaft`) — они генерируются
из кода и не требуют jigsaw-шаблонов/NBT. Созданы свои
`worldgen/structure/` (привязка к биому `mining_world`) и
`worldgen/structure_set/` (monster_rooms spacing 32/separation 12,
mineshafts spacing 24/separation 8). Свои NBT-структуры не создаём.
Reason: минимум файлов, максимум совместимости, нет риска битых шаблонов.
Do not: не делать свои jigsaw-структуры без необходимости.

## 2026-08-14 — Рудные кэши и декорации через configured/placed фичи
Decision: «карманы» руды — `minecraft:ore` с крупным `size` (12..18) и
`rarity_filter` по зонам Y (редкие, но жирные). Декорации пещер —
`minecraft:pointed_dripstone` и `minecraft:dripstone_cluster` с
`wetness: 0.0` (без луж воды). Слоты фич биома: 3 — декорации,
7 — базовые руды, 8 — кэши.
Reason: единый датапак-подход (как руды Этапа 3), вода остаётся
невозможной.
Do not: не подмешивать vanilla-аквиферы/воду; не ставить кэши в слот 7
(пересекались бы с базовыми рудами по порядку генерации без необходимости).

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
- `minecraft:count` + `minecraft:uniform` IntProvider — обёртка `value`
  (`"count": {"type": "minecraft:uniform", "value": {"min_inclusive": ..,
  "max_inclusive": ..}}`).
- Структуры 1.20.1: поля `step` и спец-поля типа `mineshaft_type` — на
  верхнем уровне (не в `config`); `config`/`probability` у mineshaft не
  нужны.
- `monster_room` (данж со спавнером) — в 1.20.1 это ФИЧА
  (`worldgen/configured_feature/monster_room.json` + placed), а НЕ структура;
  `structure_type: minecraft:monster_room` не существует.
- Поля dripstone_cluster в 1.20.1 — `*_dripstone_column_*`
  (`chance_of_dripstone_column_at_max_distance_from_center`,
  `dripstone_block_layer_thickness`,
  `max_distance_from_edge_affecting_chance_of_dripstone_column`), а не
  `*_speleothem_*` (старое имя 1.19).

## Генерация «закрытого» пещерного измерения (фикс 2026-08-14)
- **Проблема**: `final_density` как клон OVERWORLD пресета строит РЕЛЬЕФ с
  поверхностью (cheese-террейн). В Mining World это давало «равнину» + небо +
  бедрок под ней (высота поверхности зависела от сида).
- **Решение**: `final_density` по образцу пресета `minecraft:caves`/nether —
  `squeeze(0.64 * interpolated(blend_density(add(2.5, mul(y_clamped_gradient
  (0..32, 0→1), add(-2.5, add(0.9375, mul(y_clamped_gradient(288..312, 1→0),
  add(-0.9375, base_3d_noise)))))))))` + min с `overworld/caves/noodle`.
  Нижний градиент — сплошной пол, верхний — сплошной потолок, между —
  пещеры. Бедрок-пол/потолок — через `surface_rule` (bedrock_floor/roof).
- **Do not**: НЕ возвращать в `final_density` террейн-функции overworld
  (continents/depth/erosion/sloped_cheese), пока цель — закрытое измерение.