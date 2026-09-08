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

    @ConfigItem(keyName = "useDefaultColorForAllStacks", name = "Use default colour for all stacks",
        description = "Use the default colour regardless of total stack value", position = 2)
    default boolean useDefaultColorForAllStacks() { return false; }

    @ConfigItem(keyName = "textColor", name = "Default colour",
        description = "Colour for stacks worth at most 10k, or all stacks when enabled above", position = 3)
    default Color textColor() { return Color.WHITE; }

    @ConfigItem(keyName = "over10kColor", name = ">10k colour",
        description = "Colour for total stack values greater than 10,000 gp and at most 100,000 gp", position = 4)
    default Color over10kColor() { return new Color(0x66B2FF); }

    @ConfigItem(keyName = "over100kColor", name = ">100k colour",
        description = "Colour for total stack values greater than 100,000 gp and at most 1,000,000 gp", position = 5)
    default Color over100kColor() { return new Color(0x99FF99); }

    @ConfigItem(keyName = "over1mColor", name = ">1m colour",
        description = "Colour for total stack values greater than 1,000,000 gp and at most 10,000,000 gp", position = 6)
    default Color over1mColor() { return new Color(0xFF9600); }

    @ConfigItem(keyName = "over10mColor", name = ">10m colour",
        description = "Colour for total stack values greater than 10,000,000 gp", position = 7)
    default Color over10mColor() { return new Color(0xFF66B2); }

    @ConfigItem(keyName = "buttonRightOffset", name = "Button right offset",
        description = "Pixels from the right edge of the bank; adjust if another plugin overlaps", position = 8)
    @Range(min = 25, max = 350)
    default int buttonRightOffset() { return 35; }

    @ConfigItem(keyName = "buttonTopOffset", name = "Button top offset",
        description = "Pixels from the top of the bank", position = 9)
    @Range(min = 0, max = 40)
    default int buttonTopOffset() { return 6; }
}
