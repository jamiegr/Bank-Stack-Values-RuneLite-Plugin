# Plugin Hub updates

## Version 1.1.1 compatibility fix

RuneLite 1.13.1's `ItemManager.getItemPrice(int)` returns `long`. Version 1.1.0 assigned it to an `int`, causing the Plugin Hub build to fail. The 1.13.1 full manifest records `buildFailAt` for `bank-stack-values`.

Version 1.1.1 uses long unit prices and safely caps totals exceeding `Long.MAX_VALUE`. GE/High Alch selection, currency values, filters and button behavior are preserved. Test price stubs now match the new API; a stale button test expectation now matches the existing Hide/Show actions.

Validation: all 29 tests pass against RuneLite 1.13.1; the plugin JAR builds with Java 11 bytecode. New regression cases cover prices above `Integer.MAX_VALUE`, exact multiplication near the long limit, and overflow. In-game visual checks have not been performed.

## Publish the update

1. Commit and push the plugin changes to its public repository.
2. In a branch of `jamiegr/plugin-hub`, change only `plugins/bank-stack-values` to the contents of the prepared `release/bank-stack-values` marker, which points to the new plugin commit.
3. Open a pull request to `runelite/plugin-hub` with title **bank-stack-values: fix RuneLite 1.13 compatibility (1.1.1)**. Use the description in `release/plugin-hub-pr.md`.
4. Wait for Plugin Hub build checks and maintainer review/merge. The regular RuneLite client receives the update through Plugin Hub after publication; a local JAR does not clear the outdated warning.

Official process: https://github.com/runelite/plugin-hub#updating-your-plugin

## Manual checks

Run the development client with `./gradlew.bat run`. Enable Bank Stack Values and check:

- Open the bank; verify GE totals and quantity labels in fixed and resizable layouts.
- Scroll, search and change tabs; labels follow the visible bank items.
- Switch between GE and High Alch; totals and button labels change immediately.
- Toggle values with the bank button and reopen the bank; the saved state persists.
- Verify currency, untradable filters, placeholders and Bank Tags layouts.
- Disable the plugin; its labels and button disappear without removing other plugins' widgets.
