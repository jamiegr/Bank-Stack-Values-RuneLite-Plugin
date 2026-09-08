package com.bankstackvalues;

import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.inject.Inject;
import net.runelite.api.ItemComposition;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemManager;

final class StackValue
{
    private final ItemManager itemManager;

    @Inject
    StackValue(ItemManager itemManager) { this.itemManager = itemManager; }

    long total(int itemId, int quantity, boolean hideUntradableValues, ValueType valueType)
    {
        if (itemId <= 0 || quantity <= 0) { return 0; }
        ItemComposition item = itemManager.getItemComposition(itemId);
        if (item.getPlaceholderTemplateId() != -1) { return 0; }
        if (hideUntradableValues && !item.isTradeable()) { return 0; }
        // Match RuneLite's Bank plugin, including currency face values in HA mode.
        int price;
        if (valueType == ValueType.HIGH_ALCH)
        {
            price = itemId == ItemID.COINS ? 1
                : itemId == ItemID.PLATINUM ? 1000 : item.getHaPrice();
        }
        else
        {
            price = itemManager.getItemPrice(itemId);
        }
        return (long) Math.max(0, price) * quantity;
    }

    static String format(long value)
    {
        if (value < 1_000) { return Long.toString(value); }
        long divisor;
        String suffix;
        if (value >= 1_000_000_000_000L) { divisor = 1_000_000_000_000L; suffix = "T"; }
        else if (value >= 1_000_000_000) { divisor = 1_000_000_000; suffix = "B"; }
        else if (value >= 1_000_000) { divisor = 1_000_000; suffix = "M"; }
        else { divisor = 1_000; suffix = "K"; }
        return BigDecimal.valueOf(value).divide(BigDecimal.valueOf(divisor),
            value / divisor < 100 ? 1 : 0, RoundingMode.DOWN)
            .stripTrailingZeros().toPlainString() + suffix;
    }
}
