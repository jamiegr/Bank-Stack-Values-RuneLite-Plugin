package com.bankstackvalues;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public final class BankStackValuesLauncher
{
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(BankStackValuesPlugin.class);
        RuneLite.main(args);
    }
}
