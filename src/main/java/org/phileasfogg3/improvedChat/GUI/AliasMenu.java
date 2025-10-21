package org.phileasfogg3.improvedChat.GUI;

import net.nexia.nexiaapi.Config;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.phileasfogg3.improvedChat.GUI.Builders.MenuBuilder;
import org.phileasfogg3.improvedChat.ImprovedChat;

import java.util.List;

public class AliasMenu {

    private Config config;
    private Config playerData;

    private MenuBuilder aliasMenuBuilder;
    private MenuBuilder backMenu;

    List<String> aliases;

    public AliasMenu(Config config, Config playerData, MenuBuilder backMenu) {
        this.config = config;
        this.playerData = playerData;
        this.backMenu = backMenu;
    }

    public void openAliasMenu(Player player) {

        aliasMenuBuilder = new MenuBuilder(ImprovedChat.Instance, ChatColor.DARK_PURPLE + "Alias Menu", 27);


        String path = "players." + player.getUniqueId() + ".Notifications.Aliases";
        aliases = playerData.getData().getStringList(path);

        int slot = 0;

        for (String alias : aliases) {

            aliasMenuBuilder.setItem(slot, Material.NAME_TAG, ChatColor.YELLOW + alias,
                    List.of(
                            ChatColor.WHITE + "Click me to remove this alias"
                    ),
                    (p, e) -> {
                        aliases.remove(alias);
                        playerData.getData().set(path, aliases);
                        playerData.save();
                        openAliasMenu(p);
                    }
            );

            slot++;
        }

        PingMenu PM = new PingMenu(config, playerData);
        aliasMenuBuilder.enableBackButton(Material.ARROW, ChatColor.YELLOW + "Previous Menu",
                List.of(ChatColor.WHITE + "Go back to the previous menu"), () -> PM.openPingMenu(player));

        aliasMenuBuilder.open(player);

    }

}
