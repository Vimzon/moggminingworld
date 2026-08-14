# CURRENT STATE — Mogg Mining World

Updated: 2026-08-14
Версия: 0.4.0-stage11 | Minecraft 1.20.1 | Forge 47.2.0 | Java 17

## Current Task
**Этап 11 — полировка и CI** реализован (скрыт нижний бедрок, добавлен
README). Все 11 этапов ROADMAP закрыты. Осталось — live-проверка Этапов
4–11.

## What Has Been Completed
- **Этап 10**: мобы и атмосфера:
  - Спавнеры биома (ванильные мобы): зомби/скелет/паук/пещерный паук/
    крипер/эндермен (monster) + летучие мыши (ambient).
  - `loot/OreDropModifier.java` + `loot/ModLootModifiers.java` — Forge
    Global Loot Modifier: моб, убитый в Mining World, с шансом из конфига
    (`mob_ore_drops.chance`, 0.35) дропает случайную руду из пула
    `loot_modifiers/ore_drops.json`; GLM зарегистрирован в
    `global_loot_modifiers.json`; работает ТОЛЬКО в Mining World.
  - `client/MiningWorldFog.java` — туман глубины (ViewportEvent: far plane
    короче до 75% и цвет темнеет к глубине).
  - `dimension_type/mining_world.json` — `fixed_time: 18000` (нет дневного
    цикла, всегда «полночь»).
  - Конфиг: секция `mob_ore_drops` (`enabled`, `chance`).
  - Версия `0.4.0-stage10`; `gradle build --offline` — BUILD SUCCESSFUL,
    JSON датапаков валидны.
- **Этап 11**: полировка и CI:
  - Скрыт нижний бедрок: удалена плоская `fill_layer`-фича
    `bottom_bedrock` (configured/placed + ссылка в слоте 0 биома); в
    `caves.json` `final_density` обёрнут внешним `max` с
    `y_clamped_gradient` (+10 на Y=0 → −10 на Y=20) — нижние ~10 блоков
    всегда камень, пещеры не режутся до низа; рваный бедрок-пол из
    `surface_rule` (`bedrock_floor`, `above_bottom` 0..5) теперь скрыт
    толщей камня, как в Overworld. [Фикс: изначально был `min` с градиентом
    −10..+10 — это давало ПУСТОТУ на дне (в noise блок ставится при значении
    > 0); заменено на `max` +10..−10.]
  - `README.md` — финальный README проекта.
  - Версия `0.4.0-stage11`; `gradle build --offline` — BUILD SUCCESSFUL,
    JSON noise_settings/биома валидны.
  - Фиксы по адверсариальному ревью: портал больше не телепортирует
    внутрь камня (поиск точки по колонне + спиралью); «руды с мобов»
    падают только с мобов, а не с любых блоков (проверка `instanceof Mob`).
- **Этап 6**: телепорт-блок `mining_portal`:
  - `block/MiningPortalBlock.java` — ПКМ: из Overworld → в Mining World
    (безопасный поиск точки на 0,100,0), из Mining World → обратно в
    Overworld. Выход только порталом (решение из DECISIONS).
  - `block/ModBlocks.java` — DeferredRegister блока + BlockItem; блок
    админский (strength -1, без лут-таблицы, без крафта — `/give`).
  - Ассеты: model/blockstates/item/textures/lang (en_us + ru_ru).
  - Версия поднята до `0.4.0-stage6` (gradle.properties + build.gradle).
  - `gradle build --offline` — BUILD SUCCESSFUL, jar
    `build\libs\moggminingworld-0.4.0-stage6.jar` (44411 байт).
- **Этап 7**: интеграция с DimWorldBorder + предгенерация чанков:
  - Исследование: DimWorldBorder автоматически работает с любым измерением
    (навешивает слушатели границы на все измерения на старте сервера,
    `/dimworldborder moggminingworld:mining_world` работает без правок).
  - `pregen/PregenManager.java` — очередь чанков вокруг центра границы,
    генерация порциями 8 чанков/тик, прогресс в лог `[Mogg] Pregen ...`.
  - `command/PregenCommand.java` — `/moggminingworld pregen start [radius]`
    (без аргумента — берёт текущую границу мира из `/dimworldborder`;
    с радиусом — квадрат вокруг центра границы), `stop`, `status`.
  - `ModEvents.java` — регистрация команды + тик-обработчик.
  - `mods.toml` — необязательная зависимость `dimensionalworldborder`.
  - Версия `0.4.0-stage7`; `gradle build --offline` — BUILD SUCCESSFUL.
- **Этап 9**: конфигурация (TOML):
  - `config/MiningWorldConfig.java` — ForgeConfigSpec, автогенерация
    `moggminingworld-common.toml`. Секции:
    - `modded_ores`: глобальные `veins_per_chunk` (по умолч. 6) и
      `size_multiplier` (1.0) + per-tier `min_y`/`max_y`/`size`/`weight`
      для COMMON/MID/DEEP/BOTTOM. Динамические руды Этапа 5 читают значения
      из конфига вместо захардкоженного `OreTier` enum;
    - `pregen`: `chunks_per_tick` (по умолч. 8) — скорость предгенерации
      Этапа 7;
    - `dig_limit`: `enabled` (false) + `max_y` (319) — при включении блоки
      выше `max_y` в Mining World ломать нельзя (красное сообщение игроку).
  - `MoggMiningWorld.java` — регистрация конфига; `ModEvents.java` —
    обработчик `BlockEvent.BreakEvent`; lang `en_us.json`
    (`message.moggminingworld.dig_limit`).
  - Версия `0.4.0-stage9`; `gradle build --offline` — BUILD SUCCESSFUL.
- **Этап 8**: совместимость с WorldEdit:
  - Исследование: WorldEdit для Forge работает с любым `ServerLevel`
    автоматически; наше измерение — стандартный `minecraft:noise`-генератор
    с обычными границами Y (0..320) — совместимо без правок.
  - `mods.toml` — добавлена необязательная зависимость `worldedit`.
  - Версия `0.4.0-stage8`; `gradle build --offline` — BUILD SUCCESSFUL.
- **Этап 1**: каркас проекта, регистрация измерения через датапак.
- **Этап 2**: своя пещерная генерация — биом, noise-settings `caves`
  (без воды/лавы по построению), bedrock-потолок/пол, клиентский рендер
  без неба/облаков. Фикс 2026-08-14: `final_density` переписан по образцу
  `minecraft:caves` — закрытое измерение без поверхности/неба.
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

## Crash fix 2 (2026-08-14) — краш при создании мира: датапаки Этапа 4
- **Симптом**: игра доходила до создания мира и падала
  `Failed to load registries due to above errors` (RegistryDataLoader).
- **Причина**: ошибки в датапаках Этапа 4 (не тестировались в игре):
  1. `dripstone_cluster` — в 1.20.1 поля называются `*_dripstone_column_*`
     и `dripstone_block_layer_thickness`, а не `*_speleothem_*`;
  2. `pointed_dripstone` (placed) — формат `minecraft:count` + `uniform`
     требует обёртку `value`;
  3. `mineshaft` структура — неверный формат: в 1.20.1 у структуры поля
     `mineshaft_type` и `step` на верхнем уровне, без `config`/`probability`;
  4. `monster_room` — в 1.20.1 это НЕ структура (`Unknown registry key
     ...structure_type: minecraft:monster_room`), а обычная фича:
     `configured_feature/monster_room.json` (`minecraft:monster_room`) +
     `placed_feature/monster_room.json` + слот 3 биома. Удалены
     `structure/monster_room.json` и `structure_set/monster_rooms.json`.
- **Фикс**: переписал все 4 файла по ванильным образцам 1.20.1
  (сверено с jar клиента), удалил структуру/структур-сет monster_room,
  добавил фичу monster_room в слот 3 биома.
- Также: версия в `build.gradle` была захардкожена `0.4.0-stage3` (хотя
  gradle.properties уже stage5) — это вводило в заблуждение по имени jar.
  Исправлено на `0.4.0-stage5`.
- Проверено: `gradle build --offline` — BUILD SUCCESSFUL, все JSON валидны.

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

## Live-test feedback 2 (2026-08-14, после фикса краша датапаков)
- **Проблема**: ТП в Mining World показал «равнину» с «небом» сверху и
  бедроком сразу под поверхностью — вместо закрытых пещер.
- **Причина**: `worldgen/noise_settings/caves.json` был клоном ванильного
  OVERWORLD пресета — его `final_density` строит РЕЛЬЕФ С ПОВЕРХНОСТЬЮ
  (террейн из cheese-шума). В зависимости от сида поверхность ложится
  низко → видна «равнина», выше — пустота (небо), ниже — сразу бедрок-пол.
- **Фикс**: `final_density` переписан по образцу ванильного пресета
  `minecraft:caves` (и nether): сплошной камень по всей колонне + пещеры
  (`nether/base_3d_noise` + `overworld/caves/noodle`), пол/потолок из
  бедрока через `surface_rule` (bedrock_roof/floor), deepslate внизу.
  Мин/высота согласованы (0..320). Поверхности и неба больше НЕТ —
  измерение полностью закрытое, как в Extra Mining World.
- **Проверено**: все 45 JSON валидны.

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
Этап 10 (мобы и атмосфера) реализован, **жду live-теста**. Этапы 4–9
тоже реализованы «по формулам», без полного live-теста. Плотности/частоты
(кэши, данжи, speleotems, динамические руды), спавн мобов и дроп руд
могут требовать подстройки.

## Current Problem / Open Questions
1. Этап 8: WorldEdit-команды в Mining World не проверены в игре (ожидается,
   что работают из коробки).
2. Этап 7: команда предгенерации компилируется, но в игре не проверена
   (тайминги тика, учёт границы, прогресс-логи).
3. Этап 6: телепорт-блок компилируется, но в игре не проверен (точка входа
   на 0,100,0 и безопасный поиск позиции — по формулам).
4. Плотности Этапов 4–5 подобраны «на глаз» — нужен баланс по итогам теста
   (кэши: chance/size; данжи: spacing; speleotem: частота; динамика: тиры).
5. Этап 5 компилируется, но чтение `forge:ores` и распределение по глубинам
   в живом мире не проверены (fallback по имени `_ore` сработает всегда).
6. Этап 11: новая форма пола (бедрок скрыт камнем, пещеры не доходят до
   самого низа) в игре не проверена.
7. **Фиксы 2026-08-14 (адверсариальное ревью)**: (а) пол Этапа 11 был
   перевёрнут — `min` с отрицательным градиентом давал ПУСТОТУ на дне,
   исправлено на `max` (+10..−10); (б) вход порталом мог телепортировать
   игрока ВНУТРЬ камня (удушение), теперь поиск точки по колонне + спираль;
   (в) GLM «руды с мобов» срабатывал и на ломании блоков, теперь только
   `instanceof Mob`.
6. **Фикс 2026-08-14**: пользователь не находил руды Mekanism — частота
   генерации была слишком низкой (1 жила на 24 чанка). По выбору
   пользователя установлен средний вариант: 1..3 попытки на чанк +
   INFO-логи сканирования/размещения в логе игры.
7. **Фикс 2026-08-14 (live-feedback «что не так с миром»)**: пользователь
   заметил: (а) на большой высоте только уголь/изумруд/золото, без железа
   и модовых руд; (б) руд модов почти нет; (в) пещеры «гигантские и
   просторные, как на поверхности». Исправлено:
   - высотные диапазоны руд выровнены: iron/copper — 0..320 (вместо
     0..80/0..112), gold — 0..128 (было 80..320 — золото ОШИБОЧНО росло
     только вверху), lapis 0..128, redstone 0..64, diamond 0..32;
   - руды модов: тиры высот расширены (COMMON 0..320, MID 0..256,
     DEEP 0..192, BOTTOM 0..128), размеры жил увеличены (8..14),
     height_range placed-фичи 0..320;
   - пещеры: `final_density` переписан по ванильной схеме Overworld
     (cave_layer + cave_cheese + spaghetti_2d + spaghetti_roughness +
     noodle) вместо `nether/base_3d_noise` — пещеры стали умеренными,
     как в обычном мире, а не гигантскими залами Незера.
8. **Краш-фикс 2026-08-14**: после фикса пещер игра падала при загрузке
   noise_settings «No key argument2 in MapLike[...]» — в `final_density`
   остался вложенный `minecraft:min` без `argument2` (лишняя обёртка при
   правке). Исправлено: убран лишний min, теперь
   `min(min(add(cave_layer+cheese), spaghetti), noodle)` — валидно.
   Сборка BUILD SUCCESSFUL, caves.json OK.
9. **Фикс 2026-08-14 (модовые руды слишком редкие)**: пользователь сообщил,
   что руды других модов всё ещё почти не встречаются. Увеличено число
   попыток размещения жил с 1..3 до 4..8 на чанк
   (`dynamic_modded_ores.json`, `minecraft:count` uniform) и увеличены
   размеры жил во всех тирах (COMMON 14→16, MID 12→14, DEEP 10→12,
   BOTTOM 8→10). Сборка BUILD SUCCESSFUL. Требуется live-проверка с
   удалением старых чанков измерения.

## Next Steps
1. Проверить Этап 10 в игре: зайти в Mining World — в темноте спавнятся
   ванильные мобы, убитые мобы иногда роняют руду (уголь/железо/золото/
   медь/редстоун/лазурит/алмаз/изумруд), внизу густой тёмный туман, время
   всегда «ночь».
2. Проверить Этап 8 в игре: установить WorldEdit, зайти в Mining World,
   выделить область (//wand), `//set stone`, `//replace stone air` — должно
   работать как в обычном мире.
3. Проверить Этап 9 в игре: после первого запуска в `config/` появится
   `moggminingworld-common.toml`; поменять `veins_per_chunk`/`chunks_per_tick`,
   включить `dig_limit` и убедиться, что: руды модов стали гуще/реже,
   предгенерация быстрее/медленнее, блоки выше `max_y` в Mining World не
   ломаются (красное сообщение).
4. Проверить Этап 7 в игре: установить DimWorldBorder, задать границу мира
   майнинга `/dimworldborder moggminingworld:mining_world set 2000`, затем
   `/moggminingworld pregen start` → убедиться, что чанки генерируются в
   пределах границы (прогресс в логе `[Mogg] Pregen ...`), зайти в мир и
   увидеть сгенерированную зону.
5. Проверить Этап 6 в игре: `/give @s moggminingworld:mining_portal`,
   поставить в Overworld, ПКМ → вход в Mining World, там ПКМ → выход.
6. Проверить Этапы 4–5 в игре (см. «Как проверить» ниже), удалить старые
   чанки измерения, подправить плотности/тиры.
7. Проверить Этап 11 в игре: после удаления старых чанков зайти на дно
   Mining World — голый ровный бедрок не виден, внизу сплошной камень
   (нижние ~10 блоков), при раскопке до Y=0 — рваный бедрок под толщей
   породы.

## How to Verify (Этапы 4–11)
1. Собрать мод (GitHub Actions → Artifacts → `.jar` → `mods`) или
   `./gradlew build` локально.
2. Поставить второй мод с рудами (например Create/Mekanism/Thermal) в `mods`.
3. (Этап 7) Поставить **DimWorldBorder** (Dimensional Worldborder) в `mods`.
4. (Этап 8) Поставить **WorldEdit** для Forge в `mods`.
5. **Обязательно** удалить старые чанки измерения:
   `world/dimensions/moggminingworld` (правки шума/фич не применяются
   к уже сгенерированным чанкам). Старый jar из `mods` заменить.
6. Тест конфига (Этап 9):
   - первый запуск создаст `config/moggminingworld-common.toml`;
   - поменять `modded_ores.veins_per_chunk` (например 12) и
     `pregen.chunks_per_tick` (например 32), включить `dig_limit.enabled`
     и `dig_limit.max_y` (например 200);
   - перезапустить сервер; в Mining World: руд модов стало гуще,
     предгенерация идёт быстрее, блоки выше Y=200 не ломаются
     (красное сообщение `You cannot dig higher than Y=200...`).
7. Тест WorldEdit (Этап 8):
   - зайти в Mining World (`/execute in moggminingworld:mining_world run
     tp @s 0 60 0`);
   - `/wand` → выделить область, `//set stone`, `//replace stone air` —
     должно работать как в обычном мире.
8. Тест предгенерации (Этап 7):
   - задать границу: `/dimworldborder moggminingworld:mining_world set 2000`;
   - запустить: `/moggminingworld pregen start` (берёт границу мира) или
     `/moggminingworld pregen start 1000` (радиус в блоках от центра границы);
   - статус: `/moggminingworld pregen status`; остановить:
     `/moggminingworld pregen stop`;
   - в логе сервера ожидать `[Mogg] Pregen started: ... chunks`,
     прогресс каждые 100 чанков, `Pregen finished`.
9. Тест телепорт-блока (Этап 6):
   - `/give @s moggminingworld:mining_portal`;
   - поставить блок в Overworld (админ-блок, ломается только в креативе);
   - ПКМ → телепорт в Mining World (на 0,100,0 с поиском безопасной точки);
   - ПКМ в Mining World → возврат в Overworld;
   - вход/выход работают в обе стороны, без краша и воды.
10. Войти в Mining World: `/execute in moggminingworld:mining_world run tp @s 0 60 0`.
11. Ожидать (ПОСЛЕ фикса поверхности): полностью закрытое измерение —
    бедрок-пол, сплошной камень с пещерами, бедрок-потолок, НЕТ поверхности
    и неба. Изредка заброшенные шахты (деревянные балки/рельсы), комнаты
    монстров со спавнером, жирные «карманы» руды (особенно внизу, Y 0..16),
    сталактиты/сталагмиты из dripstone, а в нижних слоях — руды установленного
    второго мода. Воды быть не должно.
12. Вернуться: `/execute in minecraft:overworld run tp @s 0 100 0`.
13. Тест мобов и атмосферы (Этап 10):
    - в темноте Mining World спавнятся ванильные мобы: зомби, скелеты,
      пауки, пещерные пауки, криперы, изредка эндермены, летают летучие
      мыши;
    - убить несколько мобов — часть должна дропнуть руду (уголь/железо/
      золото/медь/редстоун/лазурит/алмаз/изумруд) ДОПОЛНИТЕЛЬНО к обычному
      дропу (шанс 0.35, пул в
      `data/moggminingworld/loot_modifiers/ore_drops.json`);
    - внизу (Y близко к 0) туман плотный и тёмный, вверху — светлее;
    - время в измерении всегда «полночь» (`fixed_time: 18000`);
    - если дроп руд не нужен — выключить в конфиге `mob_ore_drops.enabled`.
14. Тест пола (Этап 11): спуститься на самое дно Mining World —
    голый ровный бедрок НЕ виден; внизу (~10 блоков) сплошной камень
    (НЕ пустота!); при раскопке до Y=0 — рваный бедрок под толщей породы,
    как в Overworld. Тест входа: портал никогда не должен заспавнить
    игрока внутри блока (удушение). Тест руд с мобов: руда падает с
    убитого моба; при ломании блоков/камня руда НЕ падает.
15. В логе сервера при генерации первых чанков будет `[Mogg] Failed to scan
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
- НЕ возвращать `bottom_bedrock` (fill_layer-фича) и НЕ опускать min_y/пол —
  бедрок скрыт толщей камня через final_density (Этап 11, DECISIONS).
- НЕ начинать новые этапы до live-проверки Этапов 4–11.
- НЕ захардкоживать значения руд/скорости предгенерации/дропа руд в Java —
  всё через `MiningWorldConfig` (Этап 9) или пулы датапаков (Этап 10).
- НЕ менять ванильный дроп мобов за пределами Mining World (GLM проверяет
  измерение).

## How to Continue (для новой ИИ-модели)
1. Прочитать `AGENTS.md`, этот файл, `docs/ROADMAP.md`, `docs/DECISIONS.md`.
2. Проверить `git status` и последние commits.
3. Изучить `DEVELOPMENT_STATUS.md` (детальный журнал всех шагов и как
   проверять каждый).
4. Не переписывать работающие файлы без необходимости.
5. Продолжить со следующего незавершённого этапа из ROADMAP, не начиная
   реализацию до подтверждения текущего состояния.