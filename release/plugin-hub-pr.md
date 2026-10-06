RuneLite 1.13 changed `ItemManager.getItemPrice(int)` to return `long`, causing Bank Stack Values 1.1.0 to fail compilation and appear outdated in Plugin Hub.

Update to 1.1.1: retain long GE unit prices, prevent stack multiplication overflow, and update test stubs for the new API. Existing GE/High Alch modes, filtering and button behavior are preserved.

Validation: all 29 automated tests pass against RuneLite 1.13.1 and the Java 11 plugin JAR builds. Regression tests cover prices above the int limit and totals at/beyond the long multiplication boundary. In-game visual checks have not been performed.
