package org.exam.dorisPlugin;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.exam.dorisPlugin.enums.FunctionalBlockType;

public class FunctionalBlockPreventer implements Listener {
    public static String keyString = "prevent";

    @EventHandler
    public void OnClick(InventoryClickEvent event){
        Integer shift = FunctionalBlockType.type2Mask(event.getView().getTopInventory().getType());
        if (shift == null) return;
        ClickType clickType = event.getClick();
        InventoryType.SlotType slotType = event.getSlotType();
        ItemStack eventItem = null;
        if (clickType == ClickType.NUMBER_KEY){
            eventItem = event.getView().getBottomInventory().getItem(event.getHotbarButton());
        }
        else if (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT){
            if (shift == 1 << 11) return;
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
        if (((mask & shift) != 0)){
            event.setCancelled(true);
        }
    }

}
