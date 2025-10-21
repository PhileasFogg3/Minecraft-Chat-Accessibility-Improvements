package org.phileasfogg3.improvedChat.GUI;

import com.sun.tools.javac.Main;
import net.nexia.nexiaapi.Config;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.units.qual.A;
import org.phileasfogg3.improvedChat.GUI.Builders.MenuBuilder;
import org.phileasfogg3.improvedChat.ImprovedChat;

import java.util.Arrays;
import java.util.Collections;
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
                this::handlePingSoundClick
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

        String aliasPath = "players." + player.getUniqueId() + ".Notifications.Aliases";
        List<String> aliases = playerData.getData().getStringList(aliasPath);

        pingMenuBuilder.setItem(22, Material.NAME_TAG, ChatColor.YELLOW + "Alias Manager",
                List.of(
                        ChatColor.WHITE + "Right Click to set a new alias",
                        ChatColor.WHITE + "Left Click to manage current aliases",
                        "",
                        ChatColor.WHITE + "Your current aliases are: " + String.join(", ", aliases)
                ),
                this::handleAliasClick
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

    private void handleAliasClick(Player player, InventoryClickEvent e) {
        String path = "players." + player.getUniqueId() + ".Notifications.Aliases";
        List<String> aliases = playerData.getData().getStringList(path);
        switch (e.getClick()) {
            case LEFT -> {

                AliasMenu AM = new AliasMenu(config, playerData, pingMenuBuilder);
                AM.openAliasMenu(player);

            }
            case RIGHT -> {

                if (aliases.size() >= 18) {
                    player.sendMessage("You cannot have more than 18 aliases");
                    return;
                }

                ItemStack output = new ItemStack(Material.NAME_TAG);
                ItemMeta meta = output.getItemMeta();
                meta.setDisplayName(ChatColor.WHITE+ "Click once you've decided on an alias.");
                output.setItemMeta(meta);

                new AnvilGUI.Builder()
                        .onClick((slot, stateSnapshot) -> {
                            if(slot != AnvilGUI.Slot.OUTPUT) {
                                return Collections.emptyList();
                            }

                            if (stateSnapshot.getText().isEmpty()) {
                                return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Your alias cannot be blank"));
                            } else if (aliases.contains(stateSnapshot.getText())) {
                                return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("You've already set this as an alias"));
                            } else if (stateSnapshot.getText().contains(" ")) {
                                return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("The alias should be 1 word"));
                            } else {
                                String alias = stateSnapshot.getText();
                                aliases.add(alias);
                                playerData.getData().set(path, aliases);
                                playerData.save();
                                stateSnapshot.getPlayer().sendMessage("You have set " + alias + " as an alias");
                                openPingMenu(player);
                                return Arrays.asList(AnvilGUI.ResponseAction.close());
                            }

                        })
                        .text("Enter an alias")
                        .itemOutput(output)
                        .title(ChatColor.DARK_PURPLE + "Enter an Alias")
                        .plugin(ImprovedChat.Instance)
                        .open(player);
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