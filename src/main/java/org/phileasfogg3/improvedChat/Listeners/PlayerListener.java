package org.phileasfogg3.improvedChat.Listeners;

import net.nexia.nexiaapi.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;

public class PlayerListener implements Listener {

    private Config config;
    private Config playerData;

    public PlayerListener(Config config, Config playerData) {
        this.config = config;
        this.playerData = playerData;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (!playerData.getData().contains("players." + player.getUniqueId())) {

            Map<String, Object> playerDataMap = new HashMap<String, Object>(){{
               put("UserName", player.getName());
               put("Notifications.Bold", false);
               put("Notifications.Color", "WHITE");
               put("Notifications.Underlined", false);
               put("Notifications.Sound.Enabled", false);
               put("Notifications.Sound.Value", "ENTITY_PLAYER_LEVELUP"); //Default value
               put("Notifications.Aliases", "");
               //TODO Add more fields for general chat formatting.
            }};

            savePlayerData(player, playerDataMap);

        }
    }

    private void savePlayerData(Player player, Map<String, Object> playerDataMap) {
        // Method to save the playerData.yml file.
        playerData.getData().createSection("players." + player.getUniqueId(), playerDataMap);
        playerData.save();
    }

}
