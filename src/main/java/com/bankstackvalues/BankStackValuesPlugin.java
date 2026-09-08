package com.bankstackvalues;

import com.google.inject.Provides;
import java.util.Arrays;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.events.ClientTick;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(name = "Bank Stack Values",
    description = "Shows each bank stack's GE value with an in-bank toggle",
    tags = {"bank", "ge", "price", "stack", "value"})
public class BankStackValuesPlugin extends Plugin
{
    @Inject private Client client;
    @Inject private ClientThread clientThread;
    @Inject private ConfigManager configManager;
    @Inject private BankStackValuesConfig config;
    @Inject private OverlayManager overlayManager;
    @Inject private BankStackValuesOverlay overlay;

    private Widget parent;
    private Widget background;
    private Widget button;
    private volatile boolean running;

    @Provides
    BankStackValuesConfig provideConfig(ConfigManager manager)
    {
        return manager.getConfig(BankStackValuesConfig.class);
    }

    @Override
    protected void startUp()
    {
        running = true;
        overlayManager.add(overlay);
        clientThread.invokeLater(() -> { if (running) { updateButton(); } });
    }

    @Override
    protected void shutDown()
    {
        running = false;
        overlayManager.remove(overlay);
        clientThread.invokeLater(() -> { if (!running) { removeButton(); } });
    }

    @Subscribe
    public void onClientTick(ClientTick event)
    {
        if (running) { updateButton(); }
    }

    private void updateButton()
    {
        Widget bank = client.getWidget(InterfaceID.Bankmain.UNIVERSE);
        Widget items = client.getWidget(InterfaceID.Bankmain.ITEMS);
        if (bank == null || bank.isHidden() || items == null || items.isHidden())
        {
            removeButton();
            return;
        }
        // Bank scripts can rebuild the children without replacing the parent.
        if (parent != bank || !attached(button) || !attached(background))
        {
            removeButton();
            parent = bank;
            background = bank.createChild(-1, WidgetType.RECTANGLE);
            background.setFilled(true);
            button = bank.createChild(-1, WidgetType.TEXT);
            button.setFontId(494); // RuneScape's small bitmap font
            button.setTextShadowed(true);
            button.setXTextAlignment(1);
            button.setYTextAlignment(1);
            button.setName("Bank stack GE values");
            button.setHasListener(true);
            button.setOnOpListener((JavaScriptCallback) event ->
            {
                if (running)
                {
                    configManager.setConfiguration(BankStackValuesConfig.GROUP,
                        "overlayEnabled", !config.overlayEnabled());
                    updateButton();
                }
            });
        }
        boolean enabled = config.overlayEnabled();
        background.setTextColor(enabled ? 0x425134 : 0x423b32);
        button.setText(enabled ? "GE: ON" : "GE: OFF");
        button.setTextColor(enabled ? 0xffdc64 : 0xc8c0b0);
        button.setAction(0, enabled ? "Hide stack GE values" : "Show stack GE values");
        for (Widget widget : new Widget[]{background, button})
        {
            widget.setOriginalWidth(64);
            widget.setOriginalHeight(18);
            widget.setXPositionMode(2); // Anchor to the right on resize.
            widget.setOriginalX(config.buttonRightOffset());
            widget.setOriginalY(config.buttonTopOffset());
            widget.revalidate();
        }
    }

    private boolean attached(Widget widget)
    {
        return parent != null && widget != null && parent.getChild(widget.getIndex()) == widget;
    }

    private void removeButton()
    {
        if (parent != null)
        {
            Widget[] children = parent.getChildren();
            if (children != null)
            {
                children = children.clone();
                for (int i = 0; i < children.length; i++)
                {
                    if (children[i] == button || children[i] == background) { children[i] = null; }
                }
                int length = children.length;
                while (length > 0 && children[length - 1] == null) { length--; }
                // Preserve other plugins' widgets and their indices.
                parent.setChildren(Arrays.copyOf(children, length));
            }
        }
        parent = null;
        background = null;
        button = null;
    }
}
