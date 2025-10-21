# Minecraft-Chat-Accessibility-Improvements
A Minecraft Java Edition plugin to make accessibility improvements to the game's chat. Players are given more control over how the Minecraft Chat appears for them.

## Planned Features
- [x] Player alias manager (allow players to set aliases for themselves)
- [x] Mention notifier (sound played when a player's name, or alias, is mentioned in chat).
- [x] Mention highlight (players will be able to set a highlight in chat for aliases and name).
- [x] Players have the ability to not use this system at all.
- [ ] Chat formatting (players can change chat colour to something that suits them the best).
- [ ] Filtering system (Includes some presets plus player control too).


## How to use Paginated Menu

```java

PaginatedMenuBuilder menu = new PaginatedMenuBuilder(plugin, "Paginated Menu", 27);

for (int i = 1; i <= 50; i++) {
int finalI = i;
menu.addItem(Material.DIAMOND,
player -> "Diamond #" + finalI, // dynamic name per player
player -> List.of("This is diamond number " + finalI,
"Your UUID: " + player.getUniqueId()), // dynamic lore
(player, event) -> player.sendMessage("You clicked Diamond #" + finalI));
}

// Open for player
menu.open(player);

// Schedule dynamic updates every second
Bukkit.getScheduler().runTaskTimer(plugin, menu::updateAll, 20L, 20L);

```


## How to use MenuBuilder

```java
    public static void openTestMenu(Player player) {
        // Create the menu
        MenuBuilder menu = new MenuBuilder(ImprovedChat.Instance, "ImprovedChat Menu", 27);

        // Add a Diamond item
        menu.setItem(12, Material.NAME_TAG, ChatColor.YELLOW + "Chat Ping",
                List.of("Click to edit ping notification settings"), (p, event) -> {
                    p.getInventory().addItem(new org.bukkit.inventory.ItemStack(Material.DIAMOND));
                    p.sendMessage("You got a diamond!");
                });

        // Add an Apple item
        menu.setItem(14, Material.OAK_SIGN, ChatColor.GREEN + "Chat Format",
                List.of("Click to edit chat format"), (p, event) -> {
                    p.getInventory().addItem(new org.bukkit.inventory.ItemStack(Material.APPLE));
                    p.sendMessage("You got a apple!");
                });

        // Open the menu for the player
        menu.open(player);
    }
```
