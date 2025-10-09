package org.phileasfogg3.improvedChat.Commands;

import net.nexia.nexiaapi.Config;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.phileasfogg3.improvedChat.GUI.MainMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ImprovedChatCommand implements CommandExecutor, TabCompleter {

    private Config config;
    private Config playerData;

    public ImprovedChatCommand(Config config, Config playerData) {
        this.config = config;
        this.playerData = playerData;
    }

    private static final List<String> SUBCOMMANDS = Arrays.asList("format", "ping", "help");
    private static final List<String> FORMAT_OPTIONS = Arrays.asList("basecolour", "bold", "underlined");
    private static final List<String> PING_OPTIONS = Arrays.asList("sound", "colour", "bold", "underlined");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (args.length == 0) {

                // Send a help message here

            }

            switch (args[0].toLowerCase()) {
                case "format":
                    player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 1.0f);
                    break;
                case "ping":
                    break;
                case "help":
                    MainMenu MM = new MainMenu(config, playerData);
                    MM.openMainMenu(player);
                    break;
                default:
                    // sends help message
            }


        } else {
            Bukkit.getLogger().info("You must be a player to use this command.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase("improvedchat")) return Collections.emptyList();

        if (args.length == 1) {
            return partialMatch(args[0], SUBCOMMANDS);
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "format":
                    return partialMatch(args[1], FORMAT_OPTIONS);
                case "ping":
                    return partialMatch(args[1], PING_OPTIONS);
            }
        }

        return Collections.emptyList();
    }

    // --- Helper method to match partial strings ---
    private List<String> partialMatch(String arg, List<String> options) {
        List<String> matches = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase().startsWith(arg.toLowerCase())) {
                matches.add(option);
            }
        }
        return matches;
    }

}
