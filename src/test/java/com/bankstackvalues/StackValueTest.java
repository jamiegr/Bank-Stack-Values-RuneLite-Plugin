package com.bankstackvalues;

import net.runelite.api.ItemComposition;
import net.runelite.client.game.ItemManager;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StackValueTest
{
    private ItemManager manager;
    private ItemComposition item;
    private StackValue values;

    @Before public void setUp()
    {
        manager = mock(ItemManager.class);
        item = mock(ItemComposition.class);
        when(item.getPlaceholderTemplateId()).thenReturn(-1);
        when(manager.getItemComposition(100)).thenReturn(item);
        values = new StackValue(manager);
    }

    @Test public void multipliesUnitPriceByStackQuantity()
    {
        when(manager.getItemPrice(100)).thenReturn(250);
        assertEquals(125_000L, values.total(100, 500, false));
    }

    @Test public void hidesUntradableItemsWithMappedPrices()
    {
        when(manager.getItemPrice(100)).thenReturn(250);
        assertEquals(0, values.total(100, 500, true));
        verify(manager, never()).getItemPrice(anyInt());
    }

    @Test public void keepsTradableValuesWhenHidingUntradables()
    {
        when(item.isTradeable()).thenReturn(true);
        when(manager.getItemPrice(100)).thenReturn(250);
        assertEquals(125_000L, values.total(100, 500, true));
    }

    @Test public void showsUntradableValuesByDefault()
    {
        BankStackValuesConfig config = new BankStackValuesConfig() {};
        assertFalse(config.hideUntradableValues());
        when(manager.getItemPrice(100)).thenReturn(250);
        assertEquals(125_000L, values.total(100, 500, config.hideUntradableValues()));
    }

    @Test public void doesNotOverflowInt()
    {
        when(manager.getItemPrice(100)).thenReturn(Integer.MAX_VALUE);
        assertEquals(4_611_686_014_132_420_609L, values.total(100, Integer.MAX_VALUE, false));
    }

    @Test public void skipsEmptySlotsAndZeroQuantity()
    {
        assertEquals(0, values.total(-1, 10, false));
        assertEquals(0, values.total(100, 0, false));
        assertEquals(0, values.total(100, -1, false));
        verifyNoInteractions(manager);
    }

    @Test public void skipsPlaceholders()
    {
        when(item.getPlaceholderTemplateId()).thenReturn(14401);
        assertEquals(0, values.total(100, 1, false));
        verify(manager, never()).getItemPrice(anyInt());
    }

    @Test public void skipsUnknownOrInvalidPrices()
    {
        assertEquals(0, values.total(100, 30, false));
        when(manager.getItemPrice(100)).thenReturn(-1);
        assertEquals(0, values.total(100, 30, false));
    }

    @Test public void seesPriceAndQuantityChanges()
    {
        when(manager.getItemPrice(100)).thenReturn(20, 30);
        assertEquals(200, values.total(100, 10, false));
        assertEquals(600, values.total(100, 20, false));
    }

    @Test public void abbreviatesWithoutRoundingUp()
    {
        assertEquals("999", StackValue.format(999));
        assertEquals("1K", StackValue.format(1_000));
        assertEquals("1.2K", StackValue.format(1_299));
        assertEquals("999K", StackValue.format(999_999));
        assertEquals("2.4M", StackValue.format(2_499_999));
        assertEquals("2.1B", StackValue.format(Integer.MAX_VALUE));
        assertEquals("1T", StackValue.format(1_000_000_000_000L));
    }
}
