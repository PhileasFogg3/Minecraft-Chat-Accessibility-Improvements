package org.phileasfogg3.improvedChat.GUI;

import net.nexia.nexiaapi.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.phileasfogg3.improvedChat.GUI.Builders.MenuBuilder;
import org.phileasfogg3.improvedChat.ImprovedChat;

import java.util.List;

public class ColoursMenu {

    private Config config;
    private Config playerData;

    private MenuBuilder colourMenuBuilder;

    public ColoursMenu(Config config, Config playerData) {
        this.config = config;
        this.playerData = playerData;
    }

    public void openColoursMenu(Player player, String previousMenuTitle) {

        String title = "";

        switch (previousMenuTitle) {
            case "§5Ping Notification Settings":
                title = "Colour Picker (Notifications)";
                break;
            case "§5ImprovedChat Menu":
                title = "Colour Picker (Chat)";
                break;
            default:
                Bukkit.getLogger().severe("Something has gone very wrong.");
        }

        colourMenuBuilder = new MenuBuilder(ImprovedChat.Instance, ChatColor.DARK_PURPLE + title, 27);

        String path = "players." + player.getUniqueId() + ".Notifications.Color";

        int slot = 0;
        for (ChatColor color : ChatColor.values()) {
            if (!color.isColor()) continue;

            boolean isCurrent;

            String type= "";

            switch (previousMenuTitle) {
                case "§5Ping Notification Settings":
                    type = "notification";
                    isCurrent = color.name().equalsIgnoreCase(playerData.getData().getString(path));
                    break;
                case "§5ImprovedChat Menu":
                    type = "chat";
                    isCurrent = color.name().equalsIgnoreCase(playerData.getData().getString("players." + player.getUniqueId() + ".Chat.Color"));
                    break;
                default:
                    Bukkit.getLogger().severe("Something has gone very wrong.");
                    isCurrent = false;
            }

            ChatColor itemColor = isCurrent ? ChatColor.GREEN : ChatColor.RED;
            List<String> lore = isCurrent ?
                    List.of(
                            ChatColor.WHITE + "Click to make this your " + type + " colour!",
                            "",
                            ChatColor.GREEN + "This is your current " + type + " colour"
                    ) :
                    List.of(
                            ChatColor.WHITE + "Click to make this your " + type +  " colour!"
                    );

            colourMenuBuilder.setItem(slot, getColouredMaterial(color), itemColor + getColorData(color).friendlyName, lore,
                    (p, e) -> handleClick(p, path, color, previousMenuTitle));

            slot++;
        }

        switch (previousMenuTitle) {
            case "§5Ping Notification Settings":
                PingMenu PM = new PingMenu(config, playerData);
                colourMenuBuilder.enableBackButton(Material.ARROW, ChatColor.YELLOW + "Previous Menu",
                        List.of(ChatColor.WHITE + "Go back to the previous menu"), () -> PM.openPingMenu(player));
                break;
            case "§5ImprovedChat Menu":
                MainMenu MM = new MainMenu(config, playerData);
                colourMenuBuilder.enableBackButton(Material.ARROW, ChatColor.YELLOW + "Previous Menu",
                        List.of(ChatColor.WHITE + "Go back to the previous menu"), () -> MM.openMainMenu(player));
                break;
            default:
                Bukkit.getLogger().severe("Something has gone very wrong.");
        }

        colourMenuBuilder.open(player);

    }

    private Material getColouredMaterial(ChatColor color) {

        String itemType = config.getData().getString("ColourPicker.GUI-Item");
        String baseType;

        switch (itemType) {
            case "wool":
                baseType = "_WOOL";
                break;
            case "concrete":
                baseType = "_CONCRETE";
                break;
            case "terracotta":
                baseType = "_TERRACOTTA";
                break;
            default:
                Bukkit.getLogger().severe("Invalid GUI Item: " + itemType + ". Defaulting to WOOL.");
                baseType = "_WOOL";
                break;
        }

        String colorPrefix = getColorData(color).prefix;
        if (colorPrefix == null) colorPrefix = "WHITE";

        Material material = Material.matchMaterial(colorPrefix + baseType);

        if (material == null) {
            Bukkit.getLogger().warning("No matching material for: " + colorPrefix + baseType);
            material = Material.WHITE_WOOL;
        }

        return material;
    }

    public record ColorData(String prefix, String friendlyName) {}

    public ColorData getColorData(ChatColor color) {
        switch (color) {
            case BLACK: return new ColorData("BLACK", "Black");
            case DARK_BLUE: return new ColorData("BLUE", "Dark Blue");
            case DARK_GREEN: return new ColorData("GREEN", "Dark Green");
            case DARK_AQUA: return new ColorData("CYAN", "Dark Aqua");
            case DARK_RED: return new ColorData("RED", "Dark Red");
            case DARK_PURPLE: return new ColorData("PURPLE", "Dark Purple");
            case GOLD: return new ColorData("ORANGE", "Gold");
            case GRAY: return new ColorData("LIGHT_GRAY", "Gray");
            case DARK_GRAY: return new ColorData("GRAY", "Dark Gray");
            case BLUE: return new ColorData("LIGHT_BLUE", "Blue");
            case GREEN: return new ColorData("LIME", "Green");
            case AQUA: return new ColorData("CYAN", "Aqua");
            case RED: return new ColorData("RED", "Red");
            case LIGHT_PURPLE: return new ColorData("MAGENTA", "Light Purple");
            case YELLOW: return new ColorData("YELLOW", "Yellow");
            case WHITE: return new ColorData("WHITE", "White");
            default: return new ColorData("WHITE", "White");
        }
    }

    private void handleClick(Player player, String path, ChatColor color, String previousMenuTitle) {

        switch (previousMenuTitle) {
            case "§5Ping Notification Settings":
                playerData.getData().set(path, color.name());
                playerData.save();
                player.sendMessage("You have set your notification colour to: " + getColorData(color).friendlyName);
                openColoursMenu(player, "§5Ping Notification Settings");
                break;
            case "§5ImprovedChat Menu":
                String chatPath = "players." + player.getUniqueId() + ".Chat.Color";
                playerData.getData().set(chatPath, color.name());
                playerData.save();
                player.sendMessage("You have set your chat colour to: " + getColorData(color).friendlyName);
                openColoursMenu(player, "§5ImprovedChat Menu");
                break;
            default:
        }

    }

    public MenuBuilder getMenuBuilder() {
        return colourMenuBuilder;
    }
}
