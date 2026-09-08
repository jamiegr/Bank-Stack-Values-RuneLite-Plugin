package com.bankstackvalues;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.runelite.api.Client;
import net.runelite.api.events.ClientTick;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.overlay.OverlayManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BankStackValuesPluginTest
{
    private BankStackValuesPlugin plugin;
    private Client client;
    private Widget bank;
    private Widget other;
    private List<Widget> children;
    private ConfigManager manager;
    private AtomicBoolean enabled;
    private ClientThread thread;
    private ValueType valueType = ValueType.GE;

    private void inject(String name, Object value) throws Exception
    {
        Field field = BankStackValuesPlugin.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(plugin, value);
    }

    @Before public void setUp() throws Exception
    {
        plugin = new BankStackValuesPlugin();
        client = mock(Client.class);
        bank = mock(Widget.class);
        other = mock(Widget.class);
        children = new ArrayList<>();
        children.add(other);
        when(client.getWidget(InterfaceID.Bankmain.UNIVERSE)).thenReturn(bank);
        when(client.getWidget(InterfaceID.Bankmain.ITEMS)).thenReturn(mock(Widget.class));
        when(bank.getChildren()).thenAnswer(i -> children.toArray(new Widget[0]));
        when(bank.getChild(anyInt())).thenAnswer(i ->
        {
            int index = i.getArgument(0);
            return index < children.size() ? children.get(index) : null;
        });
        doAnswer(i ->
        {
            children = new ArrayList<>(Arrays.asList((Widget[]) i.getArgument(0)));
            return null;
        }).when(bank).setChildren(any(Widget[].class));
        when(bank.createChild(eq(-1), anyInt())).thenAnswer(i ->
        {
            Widget widget = mock(Widget.class);
            when(widget.getIndex()).thenReturn(children.size());
            children.add(widget);
            return widget;
        });
        enabled = new AtomicBoolean(true);
        BankStackValuesConfig config = new BankStackValuesConfig()
        {
            @Override public boolean overlayEnabled() { return enabled.get(); }
            @Override public ValueType valueType() { return valueType; }
        };
        manager = mock(ConfigManager.class);
        thread = mock(ClientThread.class);
        inject("client", client);
        inject("clientThread", thread);
        inject("configManager", manager);
        inject("config", config);
        inject("overlayManager", mock(OverlayManager.class));
        inject("overlay", mock(BankStackValuesOverlay.class));
        plugin.startUp();
        plugin.onClientTick(new ClientTick());
    }

    @Test public void buttonIsCreatedOnceAndReflectsSetting()
    {
        assertEquals(3, children.size());
        Widget button = children.get(2);
        verify(button).setText("GE: ON");
        enabled.set(false);
        plugin.onClientTick(new ClientTick());
        assertEquals(3, children.size());
        verify(button).setText("GE: OFF");
    }

    @Test public void buttonReflectsValueTypeChangesWithoutRecreation()
    {
        Widget button = children.get(2);
        valueType = ValueType.HIGH_ALCH;
        plugin.onClientTick(new ClientTick());
        verify(button).setText("HA: ON");
        verify(button).setName("Bank stack High Alch values");
        verify(button).setAction(0, "Hide stack High Alch values");
        enabled.set(false);
        plugin.onClientTick(new ClientTick());
        verify(button).setText("HA: OFF");
        verify(button).setAction(0, "Show stack High Alch values");
        valueType = ValueType.GE;
        plugin.onClientTick(new ClientTick());
        verify(button).setText("GE: OFF");
        assertEquals(3, children.size());
    }

    @Test public void clickPersistsBothToggleDirections()
    {
        ArgumentCaptor<JavaScriptCallback> callback = ArgumentCaptor.forClass(JavaScriptCallback.class);
        verify(children.get(2)).setOnOpListener(callback.capture());
        callback.getValue().run(null);
        verify(manager).setConfiguration(BankStackValuesConfig.GROUP, "overlayEnabled", false);
        enabled.set(false);
        callback.getValue().run(null);
        verify(manager).setConfiguration(BankStackValuesConfig.GROUP, "overlayEnabled", true);
    }

    @Test public void closeAndReopenPreserveOtherWidgets()
    {
        when(bank.isHidden()).thenReturn(true);
        plugin.onClientTick(new ClientTick());
        assertEquals(1, children.size());
        assertSame(other, children.get(0));
        when(bank.isHidden()).thenReturn(false);
        plugin.onClientTick(new ClientTick());
        assertEquals(3, children.size());
    }

    @Test public void rebuildRestoresButton()
    {
        children = new ArrayList<>(Arrays.asList(other));
        plugin.onClientTick(new ClientTick());
        assertEquals(3, children.size());
        verify(children.get(2)).setText("GE: ON");
    }

    @Test public void shutdownRemovesOnlyOurWidgets()
    {
        Widget laterPluginWidget = mock(Widget.class);
        children.add(laterPluginWidget);
        // Execute queued client-thread cleanup synchronously in this test.
        doAnswer(i -> { ((Runnable) i.getArgument(0)).run(); return null; })
            .when(thread).invokeLater(any(Runnable.class));
        plugin.shutDown();
        assertSame(other, children.get(0));
        assertNull(children.get(1));
        assertNull(children.get(2));
        assertSame(laterPluginWidget, children.get(3));
    }
}
