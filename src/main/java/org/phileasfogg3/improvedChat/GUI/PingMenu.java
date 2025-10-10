package org.phileasfogg3.improvedChat.GUI;

import net.nexia.nexiaapi.Config;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.phileasfogg3.improvedChat.GUI.Builders.MenuBuilder;
import org.phileasfogg3.improvedChat.ImprovedChat;

import java.util.List;

public class PingMenu {

    private Config config;
    private Config playerData;

    public PingMenu(Config config, Config playerData) {
        this.config = config;
        this.playerData = playerData;
    }

    public void openPingMenu(Player player) {

        MenuBuilder menu = new MenuBuilder(ImprovedChat.Instance, ChatColor.DARK_PURPLE + "Ping Settings", 27);

        String pingState;
        ChatColor color;

        if (playerData.getData().getBoolean("players." + player.getUniqueId() + ".Notifications.Sound.Enabled")) {
            pingState = "enabled";
            color = ChatColor.GREEN;
        } else {
            pingState = "disabled";
            color = ChatColor.RED;
        }

        menu.setItem(11, Material.NOTE_BLOCK, ChatColor.YELLOW + "Ping Sound",
                List.of(ChatColor.WHITE + "Right Click me to toggle ping on/off",
                        ChatColor.WHITE + "Left Click me to edit the ping sound.",
                        "",
                        ChatColor.WHITE + "Sound notifications are currently " + color + pingState
                ),
                (p, event) -> {
                    handleClick(player, event);
                });

        menu.open(player);

    }

    public void handleClick(Player player, InventoryClickEvent e) {

        switch (e.getClick()) {
            case LEFT:
                player.closeInventory();
                SoundsMenu SM = new SoundsMenu(config, playerData);
                SM.openSoundsMenu(player);
                break;
            case RIGHT:
                String path = "players." + player.getUniqueId() + ".Notifications.Sound.Enabled";

                boolean currentState = playerData.getData().getBoolean(path);

                if (currentState) {
                    playerData.getData().set(path, false);
                } else {
                    playerData.getData().set(path, true);
                }

                playerData.save();
                openPingMenu(player);
                break;
        }

    }

}
