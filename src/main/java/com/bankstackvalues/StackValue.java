package com.bankstackvalues;

import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.inject.Inject;
import net.runelite.api.ItemComposition;
import net.runelite.client.game.ItemManager;

final class StackValue
{
    private final ItemManager itemManager;

    @Inject
    StackValue(ItemManager itemManager) { this.itemManager = itemManager; }

    long total(int itemId, int quantity)
    {
        if (itemId <= 0 || quantity <= 0) { return 0; }
        ItemComposition item = itemManager.getItemComposition(itemId);
        if (item.getPlaceholderTemplateId() != -1) { return 0; }
        // Use the same cached price source and item mappings as RuneLite's Bank plugin.
        return (long) Math.max(0, itemManager.getItemPrice(itemId)) * quantity;
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
