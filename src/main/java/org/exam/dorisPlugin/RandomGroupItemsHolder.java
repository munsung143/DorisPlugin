package org.exam.dorisPlugin;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RandomGroupItemsHolder implements InventoryHolder {

    RandomGroup group;

    @Override
    @NotNull
    public Inventory getInventory(){
        return null;
    }

    public RandomGroupItemsHolder(RandomGroup group){
        this.group = group;
    }

    public void setItems(Inventory inv){
        List<ItemStack> items = group.stacks;
        if (items == null) return;
        for (int i = 0; i < items.size(); i++){
            inv.setItem(i, items.get(i));
        }
    }
    public void setData(Inventory inv){
        group.stacks = new ArrayList<>();
        for (int i = 0; i < inv.getSize(); i++){
            ItemStack item = inv.getItem(i);
            if (item == null) continue;
            group.stacks.add(item);
        }
    }

}
