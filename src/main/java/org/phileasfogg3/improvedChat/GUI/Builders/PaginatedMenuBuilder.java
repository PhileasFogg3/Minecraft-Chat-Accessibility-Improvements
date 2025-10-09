package org.phileasfogg3.improvedChat.GUI.Builders;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PaginatedMenuBuilder implements Listener {

    private final JavaPlugin plugin;
    private final String title;
    private final int size;

    private static final Set<String> registeredTitles = new HashSet<>();

    // List of items; each item can dynamically generate its name/lore for each player
    private final List<DynamicItemData> items = new ArrayList<>();

    private final Map<Player, Integer> playerPage = new HashMap<>();
    private final Set<Player> viewers = new HashSet<>();

    public PaginatedMenuBuilder(JavaPlugin plugin, String title, int size) {
        if (size % 9 != 0 || size <= 0 || size > 54)
            throw new IllegalArgumentException("Inventory size must be positive multiple of 9, max 54.");
        this.plugin = plugin;
        this.title = title;
        this.size = size;

        if (!registeredTitles.contains(title)) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
            registeredTitles.add(title);
        }
    }

    /** Add a dynamic item */
    public PaginatedMenuBuilder addItem(Material material,
                                               Function<Player, String> nameProvider,
                                               Function<Player, List<String>> loreProvider,
                                               BiConsumer<Player, InventoryClickEvent> action) {
        items.add(new DynamicItemData(material, nameProvider, loreProvider, action));
        return this;
    }

    /** Open menu for player at page 0 */
    public void open(Player player) {
        playerPage.put(player, 0);
        viewers.add(player);
        openPage(player, 0);
    }

    /** Open a specific page for a player */
    private void openPage(Player player, int page) {
        Inventory inv = Bukkit.createInventory(null, size, title);

        int itemsPerPage = size - 9; // leave bottom row for navigation
        int start = page * itemsPerPage;
        int end = Math.min(start + itemsPerPage, items.size());

        for (int i = start; i < end; i++) {
            DynamicItemData data = items.get(i);
            ItemStack item = new ItemStack(data.material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(data.nameProvider.apply(player));
                List<String> lore = data.loreProvider != null ? data.loreProvider.apply(player) : null;
                if (lore != null) meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inv.setItem(i - start, item);
        }

        // Navigation buttons
        if (page > 0) inv.setItem(size - 9, createButton(Material.ARROW, "Previous Page"));
        if (end < items.size()) inv.setItem(size - 1, createButton(Material.ARROW, "Next Page"));

        player.openInventory(inv);
    }

    /** Refresh menu for all viewers (dynamic updates) */
    public void updateAll() {
        for (Player player : viewers) {
            int page = playerPage.getOrDefault(player, 0);
            openPage(player, page);
        }
    }

    private ItemStack createButton(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals(title)) return;

        event.setCancelled(true);
        int page = playerPage.getOrDefault(player, 0);
        int itemsPerPage = size - 9;
        int start = page * itemsPerPage;
        int clicked = event.getRawSlot();

        // Navigation
        if (clicked == size - 9 && page > 0) {
            openPage(player, page - 1);
            playerPage.put(player, page - 1);
            return;
        }
        if (clicked == size - 1 && (start + itemsPerPage) < items.size()) {
            openPage(player, page + 1);
            playerPage.put(player, page + 1);
            return;
        }

        int itemIndex = start + clicked;
        if (itemIndex >= items.size()) return;

        DynamicItemData data = items.get(itemIndex);
        if (data.action != null) data.action.accept(player, event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!event.getView().getTitle().equals(title)) return;
        viewers.remove(player);
        playerPage.remove(player);
    }

    /** Internal class for dynamic items */
    private static class DynamicItemData {
        Material material;
        Function<Player, String> nameProvider;
        Function<Player, List<String>> loreProvider;
        BiConsumer<Player, InventoryClickEvent> action;

        DynamicItemData(Material material,
                        Function<Player, String> nameProvider,
                        Function<Player, List<String>> loreProvider,
                        BiConsumer<Player, InventoryClickEvent> action) {
            this.material = material;
            this.nameProvider = nameProvider;
            this.loreProvider = loreProvider;
            this.action = action;
        }
    }
}

