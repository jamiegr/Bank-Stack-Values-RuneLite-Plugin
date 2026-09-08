package com.bankstackvalues;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.inject.Inject;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.ItemQuantityMode;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

class BankStackValuesOverlay extends WidgetItemOverlay
{
    private final BankStackValuesConfig config;
    private final StackValue values;

    @Inject
    BankStackValuesOverlay(BankStackValuesConfig config, StackValue values)
    {
        this.config = config;
        this.values = values;
        drawAfterLayer(InterfaceID.Bankmain.ITEMS);
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem item)
    {
        // Excludes tab icons, inventory, potion storage and other bank controls.
        if (!config.overlayEnabled() || item.getWidget().getId() != InterfaceID.Bankmain.ITEMS)
        {
            return;
        }
        // Bank Tags layouts use a real item ID with this dummy quantity for missing items.
        // Check the quantity mode too so genuine maximum-size stacks still get a value.
        if (item.getQuantity() == Integer.MAX_VALUE
            && item.getWidget().getItemQuantityMode() == ItemQuantityMode.NEVER)
        {
            return;
        }
        long total = values.total(itemId, item.getQuantity(), config.hideUntradableValues(), config.valueType());
        if (total <= 0) { return; }
        String text = StackValue.format(total);
        Rectangle bounds = item.getCanvasBounds();
        Graphics2D g = (Graphics2D) graphics.create();
        try
        {
            g.setFont(FontManager.getRunescapeSmallFont());
            int width = g.getFontMetrics().stringWidth(text);
            // Keep even unusually large values within their own item slot.
            if (width > bounds.width && bounds.width > 0)
            {
                g.setFont(g.getFont().deriveFont(g.getFont().getSize2D() * bounds.width / width));
                width = g.getFontMetrics().stringWidth(text);
            }
            int x = bounds.x + (bounds.width - width) / 2;
            int y = bounds.y + bounds.height - 1;
            g.setColor(Color.BLACK);
            g.drawString(text, x + 1, y + 1);
            g.setColor(colorForValue(total));
            g.drawString(text, x, y);
        }
        finally { g.dispose(); }
    }

    private Color colorForValue(long total)
    {
        if (config.useDefaultColorForAllStacks()) { return config.textColor(); }
        if (total > 10_000_000) { return config.over10mColor(); }
        if (total > 1_000_000) { return config.over1mColor(); }
        if (total > 100_000) { return config.over100kColor(); }
        if (total > 10_000) { return config.over10kColor(); }
        return config.textColor();
    }
}
