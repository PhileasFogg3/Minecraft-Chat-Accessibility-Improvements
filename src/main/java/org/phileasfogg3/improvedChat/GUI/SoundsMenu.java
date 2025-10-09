package org.phileasfogg3.improvedChat.GUI;

import net.nexia.nexiaapi.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.phileasfogg3.improvedChat.GUI.Builders.MenuBuilder;
import org.phileasfogg3.improvedChat.GUI.Builders.PaginatedMenuBuilder;
import org.phileasfogg3.improvedChat.ImprovedChat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SoundsMenu {

    private Config config;
    private Config playerData;

    private static final int SINGLE_MENU_SIZE = 27;

    public SoundsMenu(Config config, Config playerData) {
        this.config = config;
        this.playerData = playerData;
    }

    public void openSoundsMenu(Player player) {

        List<String> keys = new ArrayList<>(config.getData().getConfigurationSection("NotificationSounds").getKeys(false));

        if (config.getData().getConfigurationSection("NotificationSounds").getKeys(false).isEmpty()) {
            Bukkit.getLogger().severe("No notification sounds found!");
        } else if (config.getData().getConfigurationSection("NotificationSounds").getKeys(false).size() <= SINGLE_MENU_SIZE) {
            openSingleMenu(player, keys);
        } else {
            openPaginatedMenu(player, keys);
        }
    }

    private void openSingleMenu(Player player, List<String> keys) {
        MenuBuilder menu = new MenuBuilder(ImprovedChat.Instance,ChatColor.DARK_PURPLE + "Notification Sounds", SINGLE_MENU_SIZE);

        for (int slot = 0; slot < keys.size(); slot++) {
            String key = keys.get(slot);
            String soundName = key;
            String materialName = config.getData().getString("NotificationSounds." + key + ".material");
            String friendlyName = config.getData().getString("NotificationSounds." + key + ".FriendlyName");

            Material material = Material.getMaterial(materialName);
            Sound sound = Sound.valueOf(soundName);

            List<String> lore;

            ChatColor color;

            if (soundName.equals(playerData.getData().getString("players." + player.getUniqueId() + ".Notifications.Sound.Value"))) {
                color = ChatColor.GREEN;
                lore = List.of(
                        "Right Click to hear this sound",
                        "Left Click to set this sound as your notification",
                        "",
                        ChatColor.GREEN + "This is your current notification sound"
                );
            } else {
                color = ChatColor.WHITE;
                lore = List.of(
                        "Right Click to hear this sound",
                        "Left Click to set this sound as your notification"
                );
            }

            menu.setItem(slot, material, color + friendlyName, lore, (p, e) -> handleClick(player, soundName, friendlyName, sound, e));
        }

        menu.open(player);
    }

    private void openPaginatedMenu(Player player, List<String> keys) {
        PaginatedMenuBuilder menu = new PaginatedMenuBuilder(ImprovedChat.Instance,ChatColor.DARK_PURPLE + "Notification Sounds", 54);

        for (int slot = 0; slot < keys.size(); slot++) {
            String key = keys.get(slot);
            String soundName = key;
            String materialName = config.getData().getString("NotificationSounds." + key + ".material");
            String friendlyName = config.getData().getString("NotificationSounds." + key + ".FriendlyName");

            Material material = Material.getMaterial(materialName);
            Sound sound = Sound.valueOf(soundName);

            List<String> lore;

            ChatColor color;

            if (soundName.equals(playerData.getData().getString("players." + player.getUniqueId() + ".Notifications.Sound.Value"))) {
                color = ChatColor.GREEN;
                lore = List.of(
                        "Right Click to hear this sound",
                        "Left Click to set this sound as your notification",
                        "",
                        ChatColor.GREEN + "This is your current notification sound"
                );
            } else {
                color = ChatColor.WHITE;
                lore = List.of(
                        "Right Click to hear this sound",
                        "Left Click to set this sound as your notification"
                );
            }

            menu.addItem(
                    material,
                    p -> color + friendlyName, // name provider
                    p -> lore,
                    (p, e) -> handleClick(player, soundName, friendlyName, sound, e)
            );
        }

        menu.open(player);
    }

    public void handleClick(Player player, String soundName, String friendlyName, Sound sound, InventoryClickEvent e) {
        switch (e.getClick()) {
            case LEFT:
                player.sendMessage("You have set your notification sound to: " + friendlyName);
                playerData.getData().set("players." + player.getUniqueId() + ".Notifications.Sound.Value", soundName);
                playerData.save();
                player.closeInventory();
                break;
            case RIGHT:
                player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
                break;
        }
    }
}