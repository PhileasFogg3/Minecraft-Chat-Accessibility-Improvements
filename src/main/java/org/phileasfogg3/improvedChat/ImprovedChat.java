package org.phileasfogg3.improvedChat;

import net.nexia.nexiaapi.Config;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.phileasfogg3.improvedChat.Commands.ImprovedChatCommand;
import org.phileasfogg3.improvedChat.Listeners.ChatListener;
import org.phileasfogg3.improvedChat.Listeners.PlayerListener;

public final class ImprovedChat extends JavaPlugin {

    public static ImprovedChat Instance;

    Config config  = new Config(this, "settings.yml");
    Config playerData = new Config(this, "playerData.yml");

    @Override
    public void onEnable() {
        // Plugin startup logic

        Instance = this;

        registerEvents();
        registerCommands();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void registerEvents() {

        Bukkit.getPluginManager().registerEvents(new ChatListener(config, playerData), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(config, playerData), this);

    }

    public void registerCommands() {

        ImprovedChatCommand improvedChatCommand = new ImprovedChatCommand(config, playerData);
        getCommand("improvedchat").setExecutor(improvedChatCommand);
        getCommand("improvedchat").setTabCompleter(improvedChatCommand);

    }
}
