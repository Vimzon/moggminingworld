import type { Plugin } from "@opencode-ai/plugin";

/**
 * Auto-commit plugin for Mogg Mining World.
 *
 * After every assistant message finishes (i.e. a model turn completes),
 * this plugin stages and commits any uncommitted project changes so the
 * git history stays in sync with the docs (AGENTS.md / docs/*) that the
 * model is required to update each step.
 *
 * No-op when there is nothing to commit.
 */
export default (async ({ worktree, $ }) => {
  return {
    event: async ({ event }) => {
      if (event.type !== "message.updated") return;

      const info = (event as { properties?: { info?: { role?: string; time?: { completed?: number } } } })
        .properties?.info;
      if (!info || info.role !== "assistant" || !info.time?.completed) return;

      try {
        const status = await $`git -C ${worktree} status --porcelain`.text();
        if (!status.trim()) return; // nothing to commit

        await $`git -C ${worktree} add -A`;
        await $`git -C ${worktree} commit -m "chore: auto-commit after model turn" --no-verify`.text();
        console.log(`[auto-commit] committed ${status.split("\n").length} changed path(s)`);
      } catch (err) {
        console.error(`[auto-commit] failed: ${(err as Error).message}`);
      }
    },
  };
}) satisfies Plugin;