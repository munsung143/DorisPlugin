package org.exam.dorisPlugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

public class SettingInventoryClose implements Listener {

    private Runnable saveFunc;
    public SettingInventoryClose(Runnable saveFunc){
        this.saveFunc = saveFunc;
    }

    @EventHandler
    public void OnInventoryClose(InventoryCloseEvent event){
        InventoryHolder holder =  event.getView().getTopInventory().getHolder();
        if (holder instanceof EntityEquipmentInventoryHolder entityHolder){
            entityHolder.setData(event.getView().getTopInventory());
        }
        else if (holder instanceof RandomGroupItemsHolder entityHolder){
            entityHolder.setData(event.getView().getTopInventory());
            saveFunc.run();

        }
    }
}
