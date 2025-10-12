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
    private MenuBuilder backMenu;

    public ColoursMenu(Config config, Config playerData, MenuBuilder backMenu) {
        this.config = config;
        this.playerData = playerData;
        this.backMenu = backMenu;
    }

    public void openColoursMenu(Player player) {

        if (colourMenuBuilder == null) {
            colourMenuBuilder = new MenuBuilder(ImprovedChat.Instance, ChatColor.DARK_PURPLE + "Ping Colour Picker", 27);
        }

        String path = "players." + player.getUniqueId() + ".Notifications.Color";

        int slot = 0;
        for (ChatColor color : ChatColor.values()) {
            if (!color.isColor()) continue;

            boolean isCurrent = color.name().equalsIgnoreCase(playerData.getData().getString(path));

            ChatColor itemColor = isCurrent ? ChatColor.GREEN : ChatColor.RED;
            List<String> lore = isCurrent ?
                    List.of(
                            ChatColor.WHITE + "Click to make this your notification colour!",
                            "",
                            ChatColor.GREEN + "This is your current notification colour"
                    ) :
                    List.of(
                            ChatColor.WHITE + "Click to make this your notification colour!"
                    );

            colourMenuBuilder.setItem(slot, getColouredMaterial(color), itemColor + getColorData(color).friendlyName, lore,
                    (p, e) -> handleClick(p, path, color));

            slot++;
        }

        if (backMenu.getTitle().equals("§5Ping Notification Settings")) {
            PingMenu PM = new PingMenu(config, playerData);
            colourMenuBuilder.enableBackButton(Material.ARROW, ChatColor.YELLOW + "Previous Menu",
                    List.of(ChatColor.WHITE + "Go back to the previous menu"), () -> PM.openPingMenu(player));

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

    private void handleClick(Player player, String path, ChatColor color) {

        playerData.getData().set(path, color.name());
        playerData.save();
        player.sendMessage("You have set your notification colour to: " + getColorData(color).friendlyName);
        openColoursMenu(player);

    }

    public MenuBuilder getMenuBuilder() {
        return colourMenuBuilder;
    }
}
