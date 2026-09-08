package com.bankstackvalues;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import net.runelite.api.ItemComposition;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.ItemQuantityMode;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BankStackValuesOverlayTest
{
    private ItemManager manager;
    private Widget widget;
    private WidgetItem item;
    private BankStackValuesOverlay overlay;
    private boolean hideUntradableValues;
    private BankStackValuesConfig config;

    @Before public void setUp()
    {
        manager = mock(ItemManager.class);
        ItemComposition definition = mock(ItemComposition.class);
        when(definition.getPlaceholderTemplateId()).thenReturn(-1);
        when(manager.getItemComposition(100)).thenReturn(definition);
        when(manager.getItemPrice(100)).thenReturn(250);
        widget = mock(Widget.class);
        when(widget.getId()).thenReturn(InterfaceID.Bankmain.ITEMS);
        item = mock(WidgetItem.class);
        when(item.getWidget()).thenReturn(widget);
        when(item.getCanvasBounds()).thenReturn(new Rectangle(0, 0, 36, 32));
        config = spy(new BankStackValuesConfig()
        {
            @Override public boolean hideUntradableValues() { return hideUntradableValues; }
        });
        overlay = new BankStackValuesOverlay(config, new StackValue(manager));
    }

    private BufferedImage render()
    {
        BufferedImage image = new BufferedImage(36, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try { overlay.renderItemOverlay(graphics, 100, item); }
        finally { graphics.dispose(); }
        return image;
    }

    private void assertLabelColor(Color expected)
    {
        BufferedImage image = render();
        boolean found = false;
        for (int y = 0; y < image.getHeight(); y++)
        {
            for (int x = 0; x < image.getWidth(); x++)
            {
                int pixel = image.getRGB(x, y);
                if (pixel == 0 || pixel == Color.BLACK.getRGB()) { continue; }
                assertEquals(expected.getRGB(), pixel);
                found = true;
            }
        }
        assertTrue("Expected a visible value label", found);
    }

    @Test public void usesDefaultTierColorsWithStrictThresholds()
    {
        int[] prices = {1, 10_000, 10_001, 100_000, 100_001,
            1_000_000, 1_000_001, 10_000_000, 10_000_001};
        int[] colors = {0xFFFFFF, 0xFFFFFF, 0x66B2FF, 0x66B2FF, 0x99FF99,
            0x99FF99, 0xFF9600, 0xFF9600, 0xFF66B2};
        when(item.getQuantity()).thenReturn(1);
        for (int i = 0; i < prices.length; i++)
        {
            when(manager.getItemPrice(100)).thenReturn(prices[i]);
            assertLabelColor(new Color(colors[i]));
        }
    }

    @Test public void usesTotalStackValueAndAppliesColorSettingsImmediately()
    {
        when(item.getQuantity()).thenReturn(500); // 250 gp each, 125k total.
        assertLabelColor(new Color(0x99FF99));
        when(config.over100kColor()).thenReturn(Color.RED);
        assertLabelColor(Color.RED);
        when(config.textColor()).thenReturn(Color.CYAN);
        when(config.useDefaultColorForAllStacks()).thenReturn(true);
        assertLabelColor(Color.CYAN);
        when(config.useDefaultColorForAllStacks()).thenReturn(false);
        assertLabelColor(Color.RED);
    }

    @Test public void usesCustomColorsForEveryTier()
    {
        when(config.textColor()).thenReturn(Color.RED);
        when(config.over10kColor()).thenReturn(Color.BLUE);
        when(config.over100kColor()).thenReturn(Color.GREEN);
        when(config.over1mColor()).thenReturn(Color.YELLOW);
        when(config.over10mColor()).thenReturn(Color.MAGENTA);
        int[] prices = {1, 10_001, 100_001, 1_000_001, 10_000_001};
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA};
        when(item.getQuantity()).thenReturn(1);
        for (int i = 0; i < prices.length; i++)
        {
            when(manager.getItemPrice(100)).thenReturn(prices[i]);
            assertLabelColor(colors[i]);
        }
    }

    @Test public void switchingValueTypeImmediatelyChangesValueAndColorTier()
    {
        when(item.getQuantity()).thenReturn(500);
        when(manager.getItemComposition(100).getHaPrice()).thenReturn(30);
        assertLabelColor(config.over100kColor()); // GE: 125k.
        when(config.valueType()).thenReturn(ValueType.HIGH_ALCH);
        assertLabelColor(config.over10kColor()); // HA: 15k.
        when(config.valueType()).thenReturn(ValueType.GE);
        assertLabelColor(config.over100kColor());
    }

    @Test public void appliesUntradableSettingChanges()
    {
        when(item.getQuantity()).thenReturn(500);
        hideUntradableValues = true;
        render();
        verify(manager, never()).getItemPrice(anyInt());
        hideUntradableValues = false;
        render();
        verify(manager).getItemPrice(100);
    }

    @Test public void skipsBankTagLayoutPlaceholdersWithRealItemIds()
    {
        when(item.getQuantity()).thenReturn(Integer.MAX_VALUE);
        when(widget.getItemQuantityMode()).thenReturn(ItemQuantityMode.NEVER);
        render();
        verifyNoInteractions(manager);
    }

    @Test public void valuesRealMaximumSizeStacks()
    {
        when(item.getQuantity()).thenReturn(Integer.MAX_VALUE);
        when(widget.getItemQuantityMode()).thenReturn(ItemQuantityMode.STACKABLE);
        render();
        verify(manager).getItemPrice(100);
    }

    @Test public void valuesOrdinaryStacksWithHiddenQuantityText()
    {
        when(item.getQuantity()).thenReturn(500);
        when(widget.getItemQuantityMode()).thenReturn(ItemQuantityMode.NEVER);
        render();
        verify(manager).getItemPrice(100);
    }
}
