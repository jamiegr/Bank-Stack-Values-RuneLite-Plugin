# Bank Stack Values

A RuneLite plugin that draws the **total GE value of each individual stack** at the bottom of its bank item icon. For example, 500 items priced at 250 gp each show `125K`.

Click **GE: ON / GE: OFF** near the bank's top-right corner to toggle the labels. This setting persists through RuneLite's configuration system and is also available in the plugin settings.

## Run on Windows

1. Extract this project and install a **JDK 11 or 17** if you do not already have one. Set `JAVA_HOME` to its installation directory. A Java runtime alone is insufficient to compile the project.
2. Open PowerShell in the extracted `bank-stack-values` directory.
3. Run:

   ```powershell
   .\gradlew.bat run
   ```

4. In the development client's plugin list, search for **Bank Stack Values** and enable it if necessary.
5. Log in and open your bank. Use the **GE** button to switch the values on or off.

On macOS/Linux, use `chmod +x gradlew` followed by `./gradlew run`.

The Gradle wrapper downloads the build tools and dependencies on the first run. No separate Gradle installation is needed. For Jagex Account login, follow RuneLite's [official development login instructions](https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts).

This is a local development plugin, not an installed Plugin Hub entry. The included JAR is a compiled plugin artifact, not a standalone application or a file that can simply be dropped into the normal RuneLite client. The supplied `run` task loads it via RuneLite's development launcher. Public Plugin Hub distribution requires the [RuneLite submission process](https://github.com/runelite/plugin-hub#creating-new-plugins).

## Behaviour and settings

- Labels use the stack quantity multiplied by RuneLite's cached item price, following the same price source and item mappings as RuneLite's built-in Bank plugin. RuneLite's active-traded-price preference therefore applies.
- `K`, `M`, `B` and `T` abbreviations are truncated to fit item slots. Values are in gp.
- Zero-quantity placeholders, bank tag layout placeholders, empty slots and items with no positive price have no label. Some untradeable variants can have a mapped value through RuneLite's item mappings; coins and platinum tokens use RuneLite's currency values.
- **Hide untradable values** hides labels for items marked as untradable, including those with mapped prices. It is off by default.
- Labels follow the displayed bank items as the bank scrolls, filters or changes tabs. Quantity text at the top of an icon remains visible.
- This version targets the ordinary personal bank item grid. Potion storage, shared storage and bank-tag sidebar display panels are outside its scope.
- Label colours are configurable by total stack value: default `#FFFFFF`, >10k `#66B2FF`, >100k `#99FF99`, >1m `#FF9600`, and >10m `#FF66B2`. The highest matching threshold wins; exact thresholds stay in the lower tier. **Use default colour for all stacks** disables tier colours and uses **Default colour** everywhere. An existing saved value colour is retained as the default colour.
- **Button right offset** and **Button top offset** move the button if a long bank title or another plugin overlaps it. The button remains anchored to the bank's right edge when resized.
- Disabling the plugin removes its overlay and its own button widgets.

Price data comes from RuneLite's `ItemManager`; the plugin makes no separate price requests. Prices are estimates from the configured source rather than guaranteed sale proceeds.

## Build and verification

```powershell
.\gradlew.bat test jar
```

Compiled against RuneLite **1.12.38**, targeting Java 11 bytecode. The plugin JAR is generated in `build/libs/`.

The automated tests cover price multiplication, integer overflow, empty stacks, placeholders (including bank tag layout placeholders), real maximum-size stacks, unavailable prices, changing prices and quantities, abbreviations, button state, both click directions, bank close/reopen, widget rebuilding, and shutdown cleanup that preserves other plugins' children.

**Live in-game verification has not been performed.** Before regular use, check the following in your development client:

1. Compare one known stack with unit price × quantity.
2. Toggle off/on, close/reopen the bank, and restart the client to check persistence.
3. Scroll and search, switch tabs, withdraw/deposit items, and drag a stack; confirm labels follow the displayed items.
4. Try fixed and resizable layouts with your usual bank plugins. Adjust the button offsets if needed.
5. Disable the plugin while the bank is open; confirm the button and labels disappear.

After a RuneLite update, rebuild against the new version if necessary:

```powershell
.\gradlew.bat test jar -PruneliteVersion=latest.release
```

## API references

- [RuneLite example plugin and development launcher](https://github.com/runelite/example-plugin)
- [RuneLite bank implementation](https://github.com/runelite/runelite/blob/master/runelite-client/src/main/java/net/runelite/client/plugins/bank/BankPlugin.java)
- [Widget item overlay API](https://static.runelite.net/runelite-client/apidocs/net/runelite/client/ui/overlay/WidgetItemOverlay.html)
