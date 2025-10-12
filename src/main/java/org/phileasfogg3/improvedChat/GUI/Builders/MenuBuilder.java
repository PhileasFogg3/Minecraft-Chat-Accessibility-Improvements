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

public class MenuBuilder implements Listener {

    private final JavaPlugin plugin;
    private Inventory inventory;
    private final String title;
    private final int size;

    private static final Set<String> registeredTitles = new HashSet<>();

    // Map slot -> ItemData (material, name, lore, action)
    private final Map<Integer, ItemData> items = new HashMap<>();
    private final Set<Player> viewers = new HashSet<>();

    // Back button fields
    private Integer backButtonSlot = null;
    private MenuBuilder backMenu = null;

    public MenuBuilder(JavaPlugin plugin, String title, int size) {
        if (size % 9 != 0 || size <= 0 || size > 54) {
            throw new IllegalArgumentException("Inventory size must be a positive multiple of 9, max 54.");
        }
        this.plugin = plugin;
        this.title = title;
        this.size = size;
        this.inventory = Bukkit.createInventory(null, size, title);

        if (!registeredTitles.contains(title)) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
            registeredTitles.add(title);
        }
    }

    /**
     * Set an item in the menu with name, optional lore, and click action
     */
    public MenuBuilder setItem(int slot, Material material, String name, List<String> lore, BiConsumer<Player, InventoryClickEvent> action) {
        ItemData data = new ItemData(material, name, lore, action);
        items.put(slot, data);
        updateSlot(slot); // Initial update
        return this;
    }

    /**
     * Enable a back button that opens a specific menu
     */
    public MenuBuilder enableBackButton(Material material, String name, List<String> lore, Runnable action) {
        this.backButtonSlot = size - 1; // last slot in bottom row
        setItem(backButtonSlot, material, name, lore, (player, event) -> action.run());
        return this;
    }

    /**
     * Open the menu for a player
     */
    public void open(Player player) {
        player.openInventory(inventory);
        viewers.add(player);
    }

    /**
     * Update all items in the inventory (dynamic updates)
     */
    public void updateAll() {
        for (Integer slot : items.keySet()) {
            updateSlot(slot);
        }
        // Update open inventories for viewers
        for (Player player : viewers) {
            player.updateInventory();
        }
    }

    /**
     * Update a specific slot based on ItemData
     */
    private void updateSlot(int slot) {
        ItemData data = items.get(slot);
        if (data == null) return;

        ItemStack item = new ItemStack(data.material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(data.name);
            if (data.lore != null) {
                meta.setLore(data.lore);
            }
            item.setItemMeta(meta);
        }
        inventory.setItem(slot, item);
    }

    public String getTitle() {
        return title;
    }

    /**
     * Handle clicks
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(title)) return;
        if (event.getClickedInventory() == null) return;

        event.setCancelled(true); // Prevent taking items

        int clickedSlot = event.getRawSlot();
        if (clickedSlot >= inventory.getSize()) return;

        ItemData data = items.get(event.getSlot());
        if (data != null && data.action != null) {
            if (event.getWhoClicked() instanceof Player player) {
                data.action.accept(player, event);
            }
        }
    }

    /**
     * Handle closing
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(title)) return;
        if (event.getPlayer() instanceof Player player) {
            viewers.remove(player);
        }
    }

    /**
     * Internal class to hold item information
     */
    private static class ItemData {
        Material material;
        String name;
        List<String> lore;
        BiConsumer<Player, InventoryClickEvent> action;

        ItemData(Material material, String name, List<String> lore, BiConsumer<Player, InventoryClickEvent> action) {
            this.material = material;
            this.name = name;
            this.lore = lore;
            this.action = action;
        }
    }
}