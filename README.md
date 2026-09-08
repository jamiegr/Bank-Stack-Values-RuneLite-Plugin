# Bank Stack Values

See the **total GE or High Alch value of each stack** directly on its bank item icon. For example, 500 items priced at 250 gp each show `125K` beneath the item, leaving its quantity visible above.

## Features

- Choose **GE** (default) or **High Alch** in the **Value type** setting. Changes apply immediately.
- Toggle labels with the **GE: ON / GE: OFF** button near the bank's top-right corner, shown as **HA: ON / HA: OFF** in High Alch mode. Your choices are saved between sessions.
- Colour labels by total stack value, or use one colour for everything.
- Optionally hide values for untradable items.
- Labels follow the bank's visible items as you scroll, search or switch tabs.
- Move the bank button using configurable offsets to accommodate other bank plugins.

## Installation

Bank Stack Values has been approved and merged into the Plugin Hub. Open RuneLite's **Configuration** panel, select **Plugin Hub**, search for **Bank Stack Values**, and install it.

## Settings

| Setting | Default | Behaviour |
| --- | --- | --- |
| Show stack values | On | Also controlled by the in-bank button. |
| Value type | GE | Choose GE or High Alch stack totals; also updates the button label and colour thresholds immediately. |
| Hide untradable values | Off | Hides items marked untradable, including those with mapped prices. |
| Use default colour for all stacks | Off | Uses Default colour for every label. |
| Default colour | White (`#FFFFFF`) | Stacks worth at most 10,000 gp. |
| >10k colour | Blue (`#66B2FF`) | Above 10,000 gp, up to 100,000 gp. |
| >100k colour | Green (`#99FF99`) | Above 100,000 gp, up to 1,000,000 gp. |
| >1m colour | Orange (`#FF9600`) | Above 1,000,000 gp, up to 10,000,000 gp. |
| >10m colour | Pink (`#FF66B2`) | Above 10,000,000 gp. |
| Button right offset | 35 px | Distance from the bank's right edge; allowed range 25–350. |
| Button top offset | 6 px | Distance from the bank's top; allowed range 0–40. |

The highest matching colour threshold wins. Exact thresholds stay in the lower tier: a stack worth exactly 100,000 gp uses the >10k colour.

## Prices and compatibility

GE values are quantity multiplied by RuneLite's cached item price, using `ItemManager` and its item mappings, as RuneLite's built-in Bank plugin does. RuneLite's active-traded-price preference applies to GE values. High Alch values use the item's High Alchemy value multiplied by quantity, without subtracting rune costs. Both modes use face values for coins and platinum tokens, matching RuneLite's Bank plugin. The plugin makes no separate network requests and has no telemetry.

`K`, `M`, `B` and `T` labels are truncated rather than rounded up. Prices are estimates, not guaranteed sale proceeds.

Empty slots, zero-quantity placeholders, Bank Tags layout placeholders and items without a positive price have no label. Some untradable variants have mapped prices; coins and platinum tokens use RuneLite's currency values.

The overlay targets the **ordinary personal bank item grid**. Potion storage, shared storage and Bank Tags sidebar panels are outside its scope. Other overlays may occupy the same space; adjust their settings or the bank button offsets if needed. Disabling this plugin removes its labels and button.

## Development

Install a **JDK 11** and set `JAVA_HOME` to its installation directory. Clone this repository, open a terminal in it, then run:

```powershell
.\gradlew.bat run
```

On macOS/Linux, use `sh ./gradlew run`. The wrapper downloads Gradle and dependencies on first use. Search for **Bank Stack Values** in the development client's plugin list and enable it. For Jagex Accounts, follow RuneLite's [development login guide](https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts).

Build and run the automated tests:

```powershell
.\gradlew.bat clean test jar
```

The build uses RuneLite's `latest.release` by default and targets Java 11 bytecode. To refresh cached dependencies, add `--refresh-dependencies`. For a specific RuneLite version, use a quoted argument such as `"-PruneliteVersion=1.12.38"`.

The generated plugin JAR is in `build/libs/`. It is a build output, not a standalone app or an installation file for the regular client. JUnit and Mockito are test-only dependencies; the plugin adds no runtime dependencies beyond RuneLite.

Tests cover stack arithmetic and overflow, price changes, placeholders, maximum-size stacks, number formatting, configurable colour thresholds, untradable filtering, and button lifecycle and cleanup. Before submitting an update, also run the [manual checks in the submission guide](docs/plugin-hub-submission.md#manual-checks).

## Support and submission

[Report a bug or request a feature](https://github.com/jamiegr/Bank-Stack-Values-RuneLite-Plugin/issues). For rendering issues, include your RuneLite version, layout mode, relevant bank plugins and a screenshot with private information hidden.

Maintainers: see [Plugin Hub submission steps](docs/plugin-hub-submission.md) for the manifest, PR description and update process.

## Licence

[BSD 2-Clause](LICENSE), copyright 2026 Jamie Grech.
