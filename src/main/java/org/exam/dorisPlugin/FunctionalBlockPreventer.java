package org.exam.dorisPlugin;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class FunctionalBlockPreventer implements Listener {
    public static String keyString = "prevent";

    @EventHandler
    public void OnClick(InventoryClickEvent event){
        int shift = 0;
        switch (event.getView().getTopInventory().getType()){
            case InventoryType.WORKBENCH: break;
            case InventoryType.FURNACE: shift = 1; break;
            case InventoryType.ENCHANTING: shift = 2; break;
            case InventoryType.ANVIL: shift = 3; break;
            case InventoryType.GRINDSTONE: shift = 4; break;
            case InventoryType.STONECUTTER: shift = 5; break;
            case InventoryType.SMOKER: shift = 6; break;
            case InventoryType.BLAST_FURNACE: shift = 7; break;
            case InventoryType.SMITHING: shift = 8; break;
            case InventoryType.LOOM: shift = 9; break;
            case InventoryType.CRAFTER: shift = 10; break;
            case InventoryType.CRAFTING: shift = 11; break;
            default: return;
        }

        ClickType clickType = event.getClick();
        InventoryType.SlotType slotType = event.getSlotType();
        ItemStack eventItem = null;
        if (clickType == ClickType.NUMBER_KEY){
            eventItem = event.getView().getBottomInventory().getItem(event.getHotbarButton());
        }
        else if (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT){
            if (shift == 11) return;
            eventItem = event.getCurrentItem();
        }
        else if (slotType == InventoryType.SlotType.CRAFTING || slotType == InventoryType.SlotType.FUEL){
            eventItem = event.getCursor();
        }
        if (eventItem == null) return;
        if (eventItem.getType().isAir()) return;
        Integer mask = eventItem.getItemMeta()
                .getPersistentDataContainer()
                .get(NamespacedKey.fromString(keyString, Main.plugin), PersistentDataType.INTEGER);
        if (mask == null) return;
        if (((mask & 1 << shift) != 0)){
            event.setCancelled(true);
        }

    }


    public void OnClick2(InventoryClickEvent event){
        int shift = 0;
        switch (event.getView().getTopInventory().getType()){
            case InventoryType.WORKBENCH: break;
            case InventoryType.FURNACE: shift = 1; break;
            case InventoryType.ENCHANTING: shift = 2; break;
            case InventoryType.ANVIL: shift = 3; break;
            case InventoryType.GRINDSTONE: shift = 4; break;
            case InventoryType.STONECUTTER: shift = 5; break;
            case InventoryType.SMOKER: shift = 6; break;
            case InventoryType.BLAST_FURNACE: shift = 7; break;
            case InventoryType.SMITHING: shift = 8; break;
            case InventoryType.LOOM: shift = 9; break;
            case InventoryType.CRAFTER: shift = 10; break;
            default: return;
        }
        event.getWhoClicked().sendMessage(event.getView().getTopInventory().getType().name());
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        if (item.getType().isAir()) return;
        Integer mask = event.getCurrentItem().getItemMeta()
                .getPersistentDataContainer()
                .get(NamespacedKey.fromString(keyString, Main.plugin), PersistentDataType.INTEGER);
        if (mask == null) return;
        if (((mask & 1 << shift) != 0)){
            event.setCancelled(true);
        }
    }

}
