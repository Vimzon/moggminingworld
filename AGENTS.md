# AGENTS.md — Mogg Mining World

## Project
Forge-мод для Minecraft 1.20.1 (Forge 47.2.x, Java 17). Добавляет отдельное
измерение **Mining World** (`moggminingworld:mining_world`) — подземный мир
для добычи руд, без поверхности, неба, воды и лавы.

## Tech Stack
- Minecraft 1.20.1
- Forge 47.2.0 (MDK)
- Java 17
- Gradle (сборка через GitHub Actions, `gradle/actions/setup-gradle`)
- Генерация мира — исключительно через датапаки (`src/main/resources/data/`)

## Architecture
- `MoggMiningWorld.java` — точка входа `@Mod`, содержит `MINING_WORLD_KEY`.
- `ModEvents.java` — серверные события (лог о загрузке измерения).
- `client/MiningWorldEffects.java` + `client/ClientModEvents.java` —
  клиентский рендер (нет неба/облаков/солнца).
- Датапаки:
  - `dimension/` — регистрация измерения (`mining_world.json`)
  - `dimension_type/` — тип измерения (`min_y: 0`, `height: 320`, потолок)
  - `worldgen/noise_settings/caves.json` — кастомный шум: `sea_level: 0`,
    `aquifers_enabled: false`, `ore_veins_enabled: false` → сплошной камень,
    пещеры режутся ванильными noise-пещерами, воды/лавы нет по построению.
  - `worldgen/biome/mining_world.json` — свой пустой биом
  - `worldgen/configured_feature/` + `placed_feature/` — bedrock-потолок/пол
    (`top_bedrock`, `bottom_bedrock`) и ванильные руды (`ore_*`).

## Important Rules
- НЕ переписывать работающую генерацию без необходимости.
- НЕ удалять существующий функционал ради упрощения.
- НЕ менять подход «генерация через датапаки» на Java-код без отдельного
  решения (зафиксировано в `docs/DECISIONS.md`).
- Перед изменением модуля изучить связанные файлы (шум ↔ биом ↔ фичи).
- Любые правки noise_settings/biome/carvers НЕ применяются к уже
  сгенерированным чанкам — указывать в тесте «удалить
  `world/dimensions/moggminingworld`».
- Ключевые JSON-нюансы 1.20.1 (уже ловили краши): `carvers` — объект
  (`{"air": []}`), `predicate_type` (не `type`) в RuleTest, `max_inclusive`
  (не `max_exclusive`) в HeightProvider.
- Сборка/тест часто недоступны в песочнице — помечать непроверенные шаги
  в `docs/CURRENT_STATE.md` разделом «Как проверить».

## Workflow (для любой ИИ-модели)
1. Прочитать `AGENTS.md`, `docs/CURRENT_STATE.md`, `docs/ROADMAP.md`,
   `docs/DECISIONS.md`.
2. Проверить `git status` и последние commits.
3. Не начинать реализацию, пока не понятно текущее состояние.
4. Работать над ОДНИМ этапом за раз (порядок в ROADMAP).
5. **Авто-коммит включён** (плагин `.opencode/plugin/auto-commit.ts`
   коммитит изменения после каждого ответа модели). Модель НЕ обязана
   сама коммитить — но должна обновлять документацию (см. ниже), иначе
   закоммичен будет только код.

## ОБЯЗАТЕЛЬНОЕ ПРАВИЛО: обновлять документацию самому, без напоминаний
Модель ДОЛЖНА сама, автоматически, без просьб пользователя, обновлять
документацию проекта при КАЖДОЙ значимой работе с кодом:

1. **После каждого завершённого шага/этапа** (не откладывать на конец
   сессии) обновить:
   - `docs/CURRENT_STATE.md` — разделы `What Has Been Completed`,
     `Currently Working On`, `Current Problem`, `Next Steps`, `Updated`;
   - `docs/ROADMAP.md` — пометить этап `✅`, сдвинуть «⬅️ СЛЕДУЮЩИЙ».
2. **Если появилось/изменилось архитектурное решение** — добавить запись
   в `docs/DECISIONS.md` (дата, Decision, Reason, Do not).
3. **Если правились JSON-генерация/фичи** — добавить раздел «Как проверить»
   в `docs/CURRENT_STATE.md` (как протестировать в игре, что должно
   получиться, что удалить из `world/dimensions/moggminingworld`).
4. Обновление документации — это часть задачи, а не опциональный шаг.
   Файлы обновляются сразу после внесения изменений в код, в том же ответе.
5. Если документация не обновлена — задача не считается завершённой.

## Commands
```
./gradlew build        # сборка (требует интернет, Forge MDK + маппинги)
./gradlew runClient    # запуск клиента (локально)
```
Сборка в репозитории: GitHub Actions → Artifacts → `.jar` → папка `mods`.

## Current Priorities
1. Этап 4 — структуры/данжи.
2. Этап 5 — телепорт-блок.
3. Этап 6 — интеграция с DimWorldBorder.
Полный порядок — в `docs/ROADMAP.md`.

## Important Decisions
См. `docs/DECISIONS.md` (не переделывать без согласования):
- Генерация измерений через датапаки, а не Java-код.
- Только ванильные руды — модовые руды НЕ добавлять (отменено).
- Выход из Mining World — только порталом, команды варпа/возврата НЕ
  планировать (отменено).
- Кастомный noise-settings вместо ванильного `minecraft:caves`.
- Вода и лава отсутствуют по построению (намеренно).
- Руды — через собственные configured/placed фичи, привязанные к
  `block_match` (stone/deepslate), диапазоны обрезаны под колонну 0..320.