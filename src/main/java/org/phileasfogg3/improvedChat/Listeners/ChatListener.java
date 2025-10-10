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

    private Config config;
    private Config playerData;

    public ChatListener(Config config, Config playerData) {
        this.config = config;
        this.playerData = playerData;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        String message = event.getMessage();

        for (Player player: Bukkit.getOnlinePlayers()) {

            String name = player.getName();

            String path = "players." + player.getUniqueId() + ".Notifications";

            if (message.contains(player.getName())) {

                if (playerData.getData().getBoolean(path + ".Sound.Enabled")) {

                    String soundName = playerData.getData().getString(path + ".Sound.Value");

                    Sound sound = Sound.valueOf(soundName);

                    player.playSound(player.getLocation(), sound, 1.0f, 1.0f);

                }

                boolean bold = playerData.getData().getBoolean(path + ".Bold");
                boolean underline = playerData.getData().getBoolean(path + ".Underlined");

                StringBuilder format = new StringBuilder();

                if (bold) format.append(ChatColor.BOLD);
                if (underline) format.append(ChatColor.UNDERLINE);

                if (format.length() > 0) {

                    message = message.replace(name, format + name + ChatColor.RESET);
                    event.setMessage(message);

                }

            }

        }

    }

}
