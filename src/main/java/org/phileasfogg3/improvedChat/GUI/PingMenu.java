package org.phileasfogg3.improvedChat.GUI;

import net.nexia.nexiaapi.Config;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.phileasfogg3.improvedChat.GUI.Builders.MenuBuilder;
import org.phileasfogg3.improvedChat.ImprovedChat;

import java.util.List;

public class PingMenu {

    private Config config;
    private Config playerData;

    public PingMenu(Config config, Config playerData) {
        this.config = config;
        this.playerData = playerData;
    }

    public void openPingMenu(Player player) {

        MenuBuilder menu = new MenuBuilder(ImprovedChat.Instance, ChatColor.DARK_PURPLE + "Ping Settings", 27);

        menu.setItem(11, Material.NOTE_BLOCK, ChatColor.YELLOW + "Ping Sound",
                List.of("Click me to edit the ping sound"), (p, event) -> {
                    player.closeInventory();
                    SoundsMenu SM = new SoundsMenu(config, playerData);
                    SM.openSoundsMenu(player);
                });

        menu.open(player);

    }

}
