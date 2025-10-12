package org.phileasfogg3.improvedChat.GUI;

import com.sun.tools.javac.Main;
import net.nexia.nexiaapi.Config;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.phileasfogg3.improvedChat.GUI.Builders.MenuBuilder;
import org.phileasfogg3.improvedChat.ImprovedChat;

import java.util.List;
import java.util.function.Function;

public class PingMenu {

    private final Config config;
    private final Config playerData;
    private MenuBuilder pingMenuBuilder;

    public PingMenu(Config config, Config playerData) {
        this.config = config;
        this.playerData = playerData;
    }

    public void openPingMenu(Player player) {
        if (pingMenuBuilder == null) {
            pingMenuBuilder = new MenuBuilder(ImprovedChat.Instance,
                    ChatColor.DARK_PURPLE + "Ping Notification Settings", 27);
        }

        String path = "players." + player.getUniqueId() + ".Notifications";

        Function<Boolean, String> stateText = state -> state ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled";

        boolean soundEnabled = playerData.getData().getBoolean(path + ".Sound.Enabled");

        // Ping Sound item
        pingMenuBuilder.setItem(10, Material.NOTE_BLOCK, ChatColor.YELLOW + "Ping Sound",
                List.of(
                        ChatColor.WHITE + "Right Click to toggle ping on/off",
                        ChatColor.WHITE + "Left Click to edit the ping sound.",
                        "",
                        ChatColor.WHITE + "Sound notifications are currently " + stateText.apply(soundEnabled)
                ),
                (p, e) -> handlePingSoundClick(p, e)
        );

        // Bold toggle
        createToggleItem(pingMenuBuilder, player, path, 12, Material.DIAMOND_SWORD,
                "Bold Text", "bold mentions", ".Bold");

        // Underline toggle
        createToggleItem(pingMenuBuilder, player, path, 14, Material.OAK_SIGN,
                "Underlined Text", "underlined mentions", ".Underlined");

        ChatColor currentColour = ChatColor.valueOf(playerData.getData().getString("players." + player.getUniqueId() + ".Notifications.Color"));
        ColoursMenu CM = new ColoursMenu(config, playerData, pingMenuBuilder);

        pingMenuBuilder.setItem(16, Material.YELLOW_DYE, ChatColor.YELLOW + "Ping Colour",
                List.of(
                        ChatColor.WHITE + "Click me to set your ping colour",
                        "",
                        ChatColor.WHITE + "Your current colour is " + currentColour + CM.getColorData(currentColour).friendlyName()
                ),
                (p, e) -> {
                    CM.openColoursMenu(p);
                }
        );

        // Back button
        MainMenu MM = new MainMenu(config, playerData);
        pingMenuBuilder.enableBackButton(Material.ARROW, ChatColor.YELLOW + "Previous Menu",
                List.of(ChatColor.WHITE + "Go back to the previous menu"), () -> MM.openMainMenu(player));

        pingMenuBuilder.open(player);
    }

    private void handlePingSoundClick(Player player, InventoryClickEvent e) {
        String path = "players." + player.getUniqueId() + ".Notifications.Sound.Enabled";
        switch (e.getClick()) {
            case LEFT -> {
                SoundsMenu SM = new SoundsMenu(config, playerData);
                SM.openSoundsMenu(player);
            }
            case RIGHT -> {
                boolean current = playerData.getData().getBoolean(path);
                playerData.getData().set(path, !current);
                playerData.save();
                openPingMenu(player); // refresh menu items
            }
        }
    }

    private void createToggleItem(MenuBuilder menu, Player player, String path, int slot,
                                  Material icon, String displayName, String description, String key) {

        boolean current = playerData.getData().getBoolean(path + key);
        ChatColor color = current ? ChatColor.GREEN : ChatColor.RED;
        String state = current ? "enabled" : "disabled";

        menu.setItem(slot, icon, ChatColor.YELLOW + displayName,
                List.of(
                        ChatColor.WHITE + "Click to toggle " + description + " on/off",
                        "",
                        ChatColor.WHITE + description.substring(0, 1).toUpperCase() + description.substring(1)
                                + " are currently " + color + state
                ),
                (p, event) -> {
                    boolean newState = !playerData.getData().getBoolean(path + key);
                    playerData.getData().set(path + key, newState);
                    playerData.save();
                    openPingMenu(p); // update dynamically
                }
        );
    }

    public MenuBuilder getMenuBuilder() {
        return pingMenuBuilder;
    }

}