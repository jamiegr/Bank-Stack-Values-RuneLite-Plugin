package com.bankstackvalues;

import java.awt.Color;
import net.runelite.client.config.*;

@ConfigGroup(BankStackValuesConfig.GROUP)
public interface BankStackValuesConfig extends Config
{
    String GROUP = "bankstackvalues";

    @ConfigItem(keyName = "overlayEnabled", name = "Show stack values",
        description = "Also controlled by the GE button in the bank", position = 0)
    default boolean overlayEnabled() { return true; }

    @ConfigItem(keyName = "hideUntradableValues", name = "Hide untradable values",
        description = "Hide values for items marked as untradable, even when they have a mapped price", position = 1)
    default boolean hideUntradableValues() { return false; }

    @ConfigItem(keyName = "textColor", name = "Value colour",
        description = "Colour of the value at the bottom of each item", position = 2)
    default Color textColor() { return new Color(255, 220, 100); }

    @ConfigItem(keyName = "buttonRightOffset", name = "Button right offset",
        description = "Pixels from the right edge of the bank; adjust if another plugin overlaps", position = 3)
    @Range(min = 25, max = 350)
    default int buttonRightOffset() { return 35; }

    @ConfigItem(keyName = "buttonTopOffset", name = "Button top offset",
        description = "Pixels from the top of the bank", position = 4)
    @Range(min = 0, max = 40)
    default int buttonTopOffset() { return 6; }
}
