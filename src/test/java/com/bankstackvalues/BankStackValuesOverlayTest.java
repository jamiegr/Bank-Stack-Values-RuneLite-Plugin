package com.bankstackvalues;

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
import static org.mockito.Mockito.*;

public class BankStackValuesOverlayTest
{
    private ItemManager manager;
    private Widget widget;
    private WidgetItem item;
    private BankStackValuesOverlay overlay;
    private boolean hideUntradableValues;

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
        overlay = new BankStackValuesOverlay(new BankStackValuesConfig()
        {
            @Override public boolean hideUntradableValues() { return hideUntradableValues; }
        }, new StackValue(manager));
    }

    private void render()
    {
        Graphics2D graphics = new BufferedImage(36, 32, BufferedImage.TYPE_INT_ARGB).createGraphics();
        try { overlay.renderItemOverlay(graphics, 100, item); }
        finally { graphics.dispose(); }
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
