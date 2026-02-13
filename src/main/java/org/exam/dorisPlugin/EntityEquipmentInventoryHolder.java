package org.exam.dorisPlugin;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class EntityEquipmentInventoryHolder implements InventoryHolder {

    EntityData data;

    public EntityEquipmentInventoryHolder(EntityData entityData){
        this.data = entityData;
    }

    public void setItems(Inventory inv){
        if (data.equipment == null) data.equipment = new EntityData.EquipItemStacks();
        if (data.equipment.head != null) inv.setItem(0, data.equipment.head);
        if (data.equipment.chest != null) inv.setItem(1, data.equipment.chest);
        if (data.equipment.legs != null) inv.setItem(2, data.equipment.legs);
        if (data.equipment.feet != null) inv.setItem(3, data.equipment.feet);
        if (data.equipment.mainhand != null) inv.setItem(4, data.equipment.mainhand);
        if (data.equipment.offhand != null) inv.setItem(5, data.equipment.offhand);
    }
    public void setData(Inventory inv){
        data.equipment.head = inv.getItem(0);
        data.equipment.chest = inv.getItem(1);
        data.equipment.legs = inv.getItem(2);
        data.equipment.feet = inv.getItem(3);
        data.equipment.mainhand = inv.getItem(4);
        data.equipment.offhand = inv.getItem(5);
    }


    @Override
    @NotNull
    public Inventory getInventory(){
        return null;
    }
}
