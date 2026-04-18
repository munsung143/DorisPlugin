package org.exam.dorisPlugin.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import org.exam.dorisPlugin.DataSerializer;
import org.exam.dorisPlugin.EntityEquipmentInventoryHolder;
import org.exam.dorisPlugin.RandomGroupItemsHolder;

public class SettingInventoryClose implements Listener {

    public SettingInventoryClose(){

    }

    @EventHandler
    public void OnInventoryClose(InventoryCloseEvent event){
        InventoryHolder holder =  event.getView().getTopInventory().getHolder();
        if (holder instanceof EntityEquipmentInventoryHolder entityHolder){
            entityHolder.setData(event.getView().getTopInventory());
        }
        else if (holder instanceof RandomGroupItemsHolder entityHolder){
            entityHolder.setData(event.getView().getTopInventory());
            DataSerializer.randomDataDeserialize();
            DataSerializer.SaveRandomData();

        }
    }
}
