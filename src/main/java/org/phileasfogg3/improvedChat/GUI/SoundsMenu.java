package org.phileasfogg3.improvedChat.GUI;

import net.nexia.nexiaapi.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.phileasfogg3.improvedChat.GUI.Builders.MenuBuilder;
import org.phileasfogg3.improvedChat.ImprovedChat;

import java.util.ArrayList;
import java.util.List;

public class SoundsMenu {

    private final Config config;
    private final Config playerData;
    private MenuBuilder soundMenuBuilder;

    private static final int MENU_SIZE = 45;

    public SoundsMenu(Config config, Config playerData) {
        this.config = config;
        this.playerData = playerData;
    }

    public void openSoundsMenu(Player player) {
        List<String> keys = new ArrayList<>(config.getData().getConfigurationSection("NotificationSounds").getKeys(false));

        if (keys.isEmpty()) {
            Bukkit.getLogger().severe("No notification sounds found!");
            return;
        }

        soundMenuBuilder = new MenuBuilder(ImprovedChat.Instance,ChatColor.DARK_PURPLE + "Notification Sounds", MENU_SIZE);

        for (int slot = 0; slot < keys.size(); slot++) {
            String key = keys.get(slot);
            String materialName = config.getData().getString("NotificationSounds." + key + ".material");
            String friendlyName = config.getData().getString("NotificationSounds." + key + ".FriendlyName");

            Material material = Material.getMaterial(materialName);
            Sound sound = Sound.valueOf(key);

            boolean isCurrent = key.equals(playerData.getData()
                    .getString("players." + player.getUniqueId() + ".Notifications.Sound.Value"));

            ChatColor color = isCurrent ? ChatColor.GREEN : ChatColor.WHITE;
            List<String> lore = isCurrent ?
                    List.of(
                            ChatColor.WHITE + "Right Click to hear this sound",
                            ChatColor.WHITE + "Left Click to set this sound",
                            "",
                            ChatColor.GREEN + "This is your current notification sound"
                    ) :
                    List.of(
                            ChatColor.WHITE + "Right Click to hear this sound",
                            ChatColor.WHITE + "Left Click to set this sound"
                    );

            soundMenuBuilder.setItem(slot, material, color + friendlyName, lore,
                    (p, e) -> handleClick(p, key, friendlyName, sound, e));
        }

        // Back button
        PingMenu PM = new PingMenu(config, playerData);
        soundMenuBuilder.enableBackButton(Material.ARROW, ChatColor.YELLOW + "Previous Menu",
                List.of(ChatColor.WHITE + "Go back to the previous menu"), () -> PM.openPingMenu(player));

        soundMenuBuilder.open(player);
    }

    private void handleClick(Player player, String soundName, String friendlyName, Sound sound, InventoryClickEvent e) {
        switch (e.getClick()) {
            case LEFT -> {
                playerData.getData().set("players." + player.getUniqueId() + ".Notifications.Sound.Value", soundName);
                playerData.save();
                player.sendMessage("You have set your notification sound to: " + friendlyName);
                openSoundsMenu(player); // refresh
            }
            case RIGHT -> player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
    }

    public MenuBuilder getMenuBuilder() {
        return soundMenuBuilder;
    }

}