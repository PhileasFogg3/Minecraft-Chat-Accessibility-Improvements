package org.phileasfogg3.improvedChat.Listeners;

import net.nexia.nexiaapi.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

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

            List<String> aliases = playerData.getData().getStringList("players." + viewer.getUniqueId() + ".Notifications.Aliases");

            // Check if message mentions this player
            if (message.contains(viewer.getName()) || aliases.stream().anyMatch(message::contains)) {
                String path = "players." + viewer.getUniqueId() + ".Notifications";

                // --- Play sound notification ---
                String[] words = message.split("\\s+"); // split by spaces

                boolean matched = Arrays.stream(words)
                        .anyMatch(word -> word.equalsIgnoreCase(viewer.getName()) ||
                                aliases.stream().anyMatch(alias -> alias.equalsIgnoreCase(word)));

                if (matched) {

                    // --- Play sound notification ---
                    if (playerData.getData().getBoolean(path + ".Sound.Enabled")) {
                        try {
                            String soundName = playerData.getData().getString(path + ".Sound.Value");
                            Sound sound = Sound.valueOf(soundName);
                            viewer.playSound(viewer.getLocation(), sound, 1.0f, 1.0f);
                        } catch (IllegalArgumentException ignored) {}
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
                    // Replace full player name (whole word only)
                    viewerMessage = viewerMessage.replaceAll(
                            "(?i)\\b" + Pattern.quote(viewer.getName()) + "\\b",
                            format + viewer.getName() + ChatColor.RESET
                    );

                    // Replace aliases (whole words only)
                    for (String alias : aliases) {
                        viewerMessage = viewerMessage.replaceAll(
                                "(?i)\\b" + Pattern.quote(alias) + "\\b",
                                format + alias + ChatColor.RESET
                        );
                    }
                }


            }

            // Send message in vanilla-like format
            viewer.sendMessage(String.format(baseFormat, sender.getDisplayName(), viewerMessage));
        }

        // Log to console as vanilla
        Bukkit.getConsoleSender().sendMessage("<" + sender.getName() + "> " + message);
    }
}
