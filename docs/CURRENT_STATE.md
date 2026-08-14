# CURRENT STATE — Mogg Mining World

Updated: 2026-08-14
Версия: 0.4.0-stage5 | Minecraft 1.20.1 | Forge 47.2.0 | Java 17

## Current Task
**Этап 5 — авто-подхват руд из других модов (ТЗ п.3)** реализован (Java).
Следующий по плану — **Этап 6: телепорт-блок**. Сначала — проверить Этапы
4 и 5 в игре.

## What Has Been Completed
- **Этап 1**: каркас проекта, регистрация измерения через датапак.
- **Этап 2**: своя пещерная генерация — биом, noise-settings `caves`
  (без воды/лавы по построению), bedrock-потолок/пол, клиентский рендер
  без неба/облаков.
- **Этап 3**: 8 ванильных руд (coal, copper, iron, gold, redstone, lapis,
  diamond, emerald) своими configured/placed фичами с привязкой к
  stone/deepslate и диапазонами Y под колонну 0..320.
- **Этап 4**: структуры/данжи + баланс руд:
  - Структуры `monster_room` и `mineshaft` (свои `worldgen/structure/` +
    `worldgen/structure_set/`, привязаны к биому `mining_world`);
  - Рудные кэши `cache_coal/iron/gold/redstone/diamond` (жирные жилы через
    `rarity_filter` по зонам Y, см. ROADMAP для точных чисел);
  - Декорации пещер `pointed_dripstone` + `dripstone_cluster` (wetness=0,
    без воды) — слот 3 фич биома;
  - Все новые JSON валидны (43/43 проверено парсингом).
- **Этап 5 (Java, первый код генерации)**: авто-подхват руд других модов:
  - `DynamicOreFeature.java` — сканирует реестр блоков (id на `_ore` или
    тег `forge:ores`, исключая `minecraft:*`), stone/deepslate варианты по
    имени, тиры глубины COMMON/MID/DEEP/BOTTOM, ванильный blob-алгоритм;
  - `DynamicOreConfig.java` + `ModWorldGen.java` (DeferredRegister только
    самого `Feature` `dynamic_ore`; configured/placed — в датапаке);
  - `forge/biome_modifier/modded_ores.json` (`forge:add_features`, step
    `underground_ores`) → биом `mining_world`;
  - `gradle build --offline` — BUILD SUCCESSFUL (после фикса краша).
- Багфиксы (Этап 3): `carvers` объект, `predicate_type` в RuleTest,
  `max_inclusive` в HeightProvider.

## Crash fix (2026-08-14) — краш при старте игры после обновления мода
- **Симптом**: игра падала с `java.lang.RuntimeException: null` →
  `Failed to apply some object holders` →
  `Unable to find registry with key minecraft:worldgen/placed_feature` из
  `ModWorldGen.<clinit>` (DeferredRegister.register).
- **Причина**: `ConfiguredFeature`/`PlacedFeature` — datapack-реестры
  (создаются при загрузке датапаков), их НЕЛЬЗЯ регистрировать через
  `DeferredRegister` на этапе регистрации мода.
- **Фикс**: `ModWorldGen` теперь регистрирует только сам `Feature`
  (`Registries.FEATURE`, `moggminingworld:dynamic_ore`), а configured/placed
  фичи объявлены в датапаке:
  `worldgen/configured_feature/dynamic_modded_ores.json` +
  `worldgen/placed_feature/dynamic_modded_ores.json`.
- Подробности — в `docs/DECISIONS.md`.

## Real Test Feedback (2026-08-14, от пользователя)
- Пещеры без воды — ✅ работает, выглядит хорошо.
- **Проблема 1**: внизу (нижняя точка) в некоторых местах виден голый
  ровный bedrock (fill_layer на Y=0) — пещеры режутся до самого низа и он
  торчит. Хочется, чтобы снизу было как в верхнем мире. → фикс в Этапе 11.
- **Пожелание**: пещеры, структура, шанс определённой руды, данжи — как в
  верхнем мире. → реализовано в Этапе 4, требует проверки в игре.
- **Новое требование (2026-08-14)**: руды ДРУГИХ модов должны появляться в
  Mining World как в Overworld (ТЗ п.3) → реализовано в Этапе 5.

## Currently Working On
Этапы 4–5 реализованы «по формулам», **без live-теста**. Плотности/частоты
(кэши, данжи, speleotems, динамические руды) могут требовать подстройки.
Этап 6 не начинать, пока Этапы 4–5 не проверены в игре.

## Current Problem / Open Questions
1. Плотности Этапов 4–5 подобраны «на глаз» — нужен баланс по итогам теста
   (кэши: chance/size; данжи: spacing; speleotem: частота; динамика: rarity
   24 и тиры глубины).
2. Этап 5 компилируется, но чтение `forge:ores` и распределение по глубинам
   в живом мире не проверены (fallback по имени `_ore` сработает всегда).
3. Пустота между Y=128 и Y=318 (рельеф пресета заканчивается на Y=128).
4. Нижний бедрок виден голым — фикс в Этапе 11.
5. gradle-wrapper.jar не в репозитории — сборка только через GitHub Actions.

## Next Steps
1. Проверить Этапы 4–5 в игре (см. «Как проверить» ниже), удалить старые
   чанки измерения, подправить плотности/тиры.
2. Этап 6 — телепорт-блок (портал в Overworld, обратная телепортация).
3. Этап 11 — скрыть нижний бедрок (слой камня над ним + рваный край).

## How to Verify (Этапы 4–5)
1. Собрать мод (GitHub Actions → Artifacts → `.jar` → `mods`) или
   `./gradlew build` локально.
2. Поставить второй мод с рудами (например Create/Mekanism/Thermal) в `mods`.
3. **Обязательно** удалить старые чанки измерения:
   `world/dimensions/moggminingworld` (правки структур/фич не применяются
   к уже сгенерированным чанкам).
4. Войти: `/execute in moggminingworld:mining_world run tp @s 0 60 0`.
5. Ожидать: изредка заброшенные шахты (деревянные балки/рельсы), комнаты
   монстров со спавнером, жирные «карманы» руды (особенно внизу, Y 0..16),
   сталактиты/сталагмиты из dripstone, а в нижних слоях — руды установленного
   второго мода. Воды быть не должно.
6. Вернуться: `/execute in minecraft:overworld run tp @s 0 100 0`.
7. В логе сервера при генерации первых чанков будет `[Mogg] Failed to scan
   modded ores...` при проблеме; в норме — тихо.

## Do NOT
- НЕ переписывать работающую датапак-генерацию на Java-код (исключение —
  только Этап 5, зафиксировано в `docs/DECISIONS.md`).
- НЕ добавлять воду/лаву в генерацию (сознательное решение, см.
  `docs/DECISIONS.md`; `dripstone_cluster` сделан с `wetness: 0`).
- НЕ менять подходы `block_match`/диапазоны Y без причины.
- НЕ добавлять собственные модовые руды (отменено). НО руды ДРУГИХ модов
  авто-подхватывать — это Этап 5, не считать «отменённым».
- НЕ добавлять команды возврата/варпа (отменено; выход — порталом).
- НЕ убирать нижний бедрок полностью — его нужно скрыть, а не удалить.
- НЕ начинать Этап 6 до live-проверки Этапов 4–5.

## How to Continue (для новой ИИ-модели)
1. Прочитать `AGENTS.md`, этот файл, `docs/ROADMAP.md`, `docs/DECISIONS.md`.
2. Проверить `git status` и последние commits.
3. Изучить `DEVELOPMENT_STATUS.md` (детальный журнал всех шагов и как
   проверять каждый).
4. Не переписывать работающие файлы без необходимости.
5. Продолжить со следующего незавершённого этапа из ROADMAP, не начиная
   реализацию до подтверждения текущего состояния.