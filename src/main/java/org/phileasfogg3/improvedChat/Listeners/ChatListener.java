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

        for (Player viewer : Bukkit.getOnlinePlayers()) {

            // Get viewer's preferred chat color
            String chatColorName = playerData.getData()
                    .getString("players." + viewer.getUniqueId() + ".Chat.Color");
            ChatColor baseChatColor = ChatColor.RESET;
            if (chatColorName != null) {
                try {
                    baseChatColor = ChatColor.valueOf(chatColorName.toUpperCase());
                } catch (IllegalArgumentException ignored) {}
            }

            List<String> aliases = playerData.getData()
                    .getStringList("players." + viewer.getUniqueId() + ".Notifications.Aliases");

            // Prepare notification formatting
            String path = "players." + viewer.getUniqueId() + ".Notifications";
            ChatColor mentionColor = ChatColor.RESET;
            boolean bold = false;
            boolean underline = false;
            boolean soundEnabled = false;
            Sound sound = null;

            if (!aliases.isEmpty() || viewer.getName() != null) {
                // Get formatting from config
                String colorName = playerData.getData().getString(path + ".Color");
                if (colorName != null) {
                    try {
                        mentionColor = ChatColor.valueOf(colorName.toUpperCase());
                    } catch (IllegalArgumentException ignored) {}
                }
                bold = playerData.getData().getBoolean(path + ".Bold");
                underline = playerData.getData().getBoolean(path + ".Underlined");
                soundEnabled = playerData.getData().getBoolean(path + ".Sound.Enabled");
                if (soundEnabled) {
                    try {
                        String soundName = playerData.getData().getString(path + ".Sound.Value");
                        sound = Sound.valueOf(soundName);
                    } catch (IllegalArgumentException ignored) {}
                }
            }

            // Build message word by word
            StringBuilder formattedMessage = new StringBuilder();
            formattedMessage.append("<").append(sender.getDisplayName()).append("> ");

            String[] words = message.split(" ");
            for (int i = 0; i < words.length; i++) {
                String word = words[i];

                boolean isMention = word.equalsIgnoreCase(viewer.getName()) ||
                        aliases.stream().anyMatch(alias -> alias.equalsIgnoreCase(word));

                if (isMention) {
                    // Apply mention formatting
                    StringBuilder format = new StringBuilder();
                    format.append(mentionColor);
                    if (bold) format.append(ChatColor.BOLD);
                    if (underline) format.append(ChatColor.UNDERLINE);

                    formattedMessage.append(format).append(word).append(baseChatColor);

                    // Play sound once
                    if (soundEnabled && sound != null) {
                        viewer.playSound(viewer.getLocation(), sound, 1.0f, 1.0f);
                    }
                } else {
                    // Regular word: base chat color
                    formattedMessage.append(baseChatColor).append(word);
                }

                if (i < words.length - 1) formattedMessage.append(" ");
            }

            viewer.sendMessage(formattedMessage.toString());
        }

        Bukkit.getConsoleSender().sendMessage("<" + sender.getName() + "> " + message);
    }
}
