package org.phileasfogg3.improvedChat.GUI;

import net.nexia.nexiaapi.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.phileasfogg3.improvedChat.GUI.Builders.MenuBuilder;
import org.phileasfogg3.improvedChat.ImprovedChat;

import java.util.List;

public class MainMenu {

    private Config config;
    private Config playerData;

    private MenuBuilder mainMenuBuilder;

    public MainMenu(Config config, Config playerData) {
        this.config = config;
        this.playerData = playerData;
    }

    public void openMainMenu(Player player) {
        // Create the menu
        mainMenuBuilder = new MenuBuilder(ImprovedChat.Instance, ChatColor.DARK_PURPLE + "ImprovedChat Menu", 27);

        mainMenuBuilder.setItem(12, Material.NAME_TAG, ChatColor.YELLOW + "Chat Ping",
                List.of(ChatColor.WHITE + "Click to edit ping notification settings"), (p, event) -> {
                    p.closeInventory();
                    PingMenu PM = new PingMenu(config, playerData);
                    PM.openPingMenu(p);
                });

        ChatColor currentColour = ChatColor.valueOf(playerData.getData().getString("players." + player.getUniqueId() + ".Chat.Color"));
        ColoursMenu CM = new ColoursMenu(config, playerData);

        mainMenuBuilder.setItem(14, Material.OAK_SIGN, ChatColor.GREEN + "Chat Colour",
                List.of(
                        ChatColor.WHITE + "Click to edit chat colours",
                        "",
                        ChatColor.WHITE + "The current chat colour is " + currentColour + CM.getColorData(currentColour).friendlyName()
                ),
                (p, event) -> CM.openColoursMenu(p, "§5ImprovedChat Menu"));

        // Open the menu for the player
        mainMenuBuilder.open(player);

    }

    public MenuBuilder getMenuBuilder() {
        return mainMenuBuilder;
    }

}
