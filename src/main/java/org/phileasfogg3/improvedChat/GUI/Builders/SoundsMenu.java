package org.phileasfogg3.improvedChat.GUI.Builders;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.phileasfogg3.improvedChat.ImprovedChat;

import java.util.List;

public class SoundsMenu {

    /** Open the main categories menu */
    public void openMainMenu(Player player) {
        MenuBuilder menu = new MenuBuilder(ImprovedChat.Instance, "Sound Categories", 27);

        menu.setItem(9, Material.STONE, "Block Sounds", List.of("All BLOCK_ sounds"),
                (p, e) -> openCategoryMenu(p, "BLOCK"));
        menu.setItem(11, Material.SKELETON_SKULL, "Entity Sounds", List.of("All ENTITY_ sounds"),
                (p, e) -> openCategoryMenu(p, "ENTITY"));
        menu.setItem(13, Material.GLASS, "Ambient Sounds", List.of("All AMBIENT_ sounds"),
                (p, e) -> openCategoryMenu(p, "AMBIENT"));
        menu.setItem(15, Material.WATER_BUCKET, "Weather Sounds", List.of("All WEATHER_ sounds"),
                (p, e) -> openCategoryMenu(p, "WEATHER"));
        menu.setItem(17, Material.PAPER, "UI Sounds", List.of("All UI_ sounds"),
                (p, e) -> openCategoryMenu(p, "UI"));

        menu.open(player);
    }

    /** Open a menu for a specific category */
    private void openCategoryMenu(Player player, String category) {
        PaginatedMenuBuilder menu = new PaginatedMenuBuilder(ImprovedChat.Instance, category + " Sounds", 54);

        for (Sound sound : Sound.values()) {
            // Skip MUSIC_ sounds
            if (sound.name().startsWith("MUSIC_")) continue;

            // Only include sounds in this category
            if (!sound.name().startsWith(category + "_")) continue;

            Material material = getMaterialForCategory(category);

            menu.addItem(
                    material,
                    p -> sound.name(),
                    p -> List.of("Namespace: " + sound.getKey().getNamespace()),
                    (p, e) -> {
                        p.playSound(p.getLocation(), sound, 1.0f, 1.0f);
                        p.sendMessage("Playing sound: " + sound.name());
                    }
            );
        }

        menu.open(player);
    }

    /** Simple fixed mapping per category */
    private Material getMaterialForCategory(String category) {
        return switch (category) {
            case "BLOCK" -> Material.STONE;
            case "ENTITY" -> Material.SKELETON_SKULL;
            case "AMBIENT" -> Material.GLASS;
            case "WEATHER" -> Material.WATER_BUCKET;
            case "UI" -> Material.PAPER;
            default -> Material.NOTE_BLOCK;
        };
    }
}