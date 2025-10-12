package org.phileasfogg3.improvedChat.Listeners;

import net.nexia.nexiaapi.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final Config config;
    private final Config playerData;

    public ChatListener(Config config, Config playerData) {
        this.config = config;
        this.playerData = playerData;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        String message = event.getMessage();

        event.setCancelled(true);

        String baseFormat = event.getFormat();

        for (Player viewer : Bukkit.getOnlinePlayers()) {
            String viewerMessage = message;

            // Check if message mentions this player
            if (message.contains(viewer.getName())) {
                String path = "players." + viewer.getUniqueId() + ".Notifications";

                // --- Play sound notification ---
                if (playerData.getData().getBoolean(path + ".Sound.Enabled")) {
                    try {
                        String soundName = playerData.getData().getString(path + ".Sound.Value");
                        Sound sound = Sound.valueOf(soundName);
                        viewer.playSound(viewer.getLocation(), sound, 1.0f, 1.0f);
                    } catch (IllegalArgumentException ignored) {
                    }
                }

                // --- Build formatting string ---
                boolean bold = playerData.getData().getBoolean(path + ".Bold");
                boolean underline = playerData.getData().getBoolean(path + ".Underlined");

                // Color
                String colorName = playerData.getData().getString(path + ".Color");
                ChatColor color = ChatColor.RESET;
                if (colorName != null) {
                    try {
                        color = ChatColor.valueOf(colorName.toUpperCase());
                    } catch (IllegalArgumentException ignored) {
                        // Invalid color name, default to RESET
                    }
                }

                StringBuilder format = new StringBuilder();
                format.append(color);
                if (bold) format.append(ChatColor.BOLD);
                if (underline) format.append(ChatColor.UNDERLINE);

                // Replace mentions with formatted text
                if (format.length() > 0) {
                    viewerMessage = viewerMessage.replace(
                            viewer.getName(),
                            format + viewer.getName() + ChatColor.RESET
                    );
                }
            }

            // Send message in vanilla-like format
            viewer.sendMessage(String.format(baseFormat, sender.getDisplayName(), viewerMessage));
        }

        // Log to console as vanilla
        Bukkit.getConsoleSender().sendMessage("<" + sender.getName() + "> " + message);
    }
}
