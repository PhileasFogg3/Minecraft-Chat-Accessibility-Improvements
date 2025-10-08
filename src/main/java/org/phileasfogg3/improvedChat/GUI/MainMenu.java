package org.phileasfogg3.improvedChat.GUI;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.phileasfogg3.improvedChat.GUI.Builders.MenuBuilder;
import org.phileasfogg3.improvedChat.ImprovedChat;

import java.util.List;

public class MainMenu {

    public static void openMainMenu(Player player) {
        // Create the menu
        MenuBuilder menu = new MenuBuilder(ImprovedChat.Instance, ChatColor.DARK_PURPLE + "ImprovedChat Menu", 27);

        // Add a Diamond item
        menu.setItem(12, Material.NAME_TAG, ChatColor.YELLOW + "Chat Ping",
                List.of("Click to edit ping notification settings"), (p, event) -> {
                    player.closeInventory();
                    PingMenu.openPingMenu(player);
                });

        // Add an Apple item
        menu.setItem(14, Material.OAK_SIGN, ChatColor.GREEN + "Chat Format",
                List.of("Click to edit chat format"), (p, event) -> {
                    p.getInventory().addItem(new org.bukkit.inventory.ItemStack(Material.APPLE));
                    p.sendMessage("You got a apple!");
                });

        // Open the menu for the player
        menu.open(player);
    }

}
