package org.phileasfogg3.improvedChat.Listeners;

import net.nexia.nexiaapi.Config;
import org.bukkit.Bukkit;
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

            if (message.contains(player.getName())) {

                String soundName = playerData.getData().getString("players." + player.getUniqueId() + ".Notifications.Sound.Value");

                Sound sound = Sound.valueOf(soundName);

                player.playSound(player.getLocation(), sound, 1.0f, 1.0f);

            }

        }

    }

}
