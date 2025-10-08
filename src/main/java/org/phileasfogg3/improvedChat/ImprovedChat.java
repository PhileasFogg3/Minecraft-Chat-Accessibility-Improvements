package org.phileasfogg3.improvedChat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.phileasfogg3.improvedChat.Commands.ImprovedChatCommand;
import org.phileasfogg3.improvedChat.Listeners.ChatListener;

public final class ImprovedChat extends JavaPlugin {

    public static ImprovedChat Instance;

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

        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);

    }

    public void registerCommands() {

        ImprovedChatCommand improvedChatCommand = new ImprovedChatCommand();
        getCommand("improvedchat").setExecutor(improvedChatCommand);
        getCommand("improvedchat").setTabCompleter(improvedChatCommand);

    }
}
