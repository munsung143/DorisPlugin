package org.exam.dorisPlugin.Events;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.exam.dorisPlugin.DataSerializer;
import org.exam.dorisPlugin.DorisKeys;
import org.exam.dorisPlugin.ItemSyncData;
import org.exam.dorisPlugin.enums.FunctionalBlockType;

public class ItemSyncer implements Listener {

    @EventHandler
    public void OnClick(InventoryClickEvent event){
        ItemStack eventItem = event.getCurrentItem();
        if (eventItem == null || eventItem.getType().isAir()) return;
        Sync(eventItem);
    }

    @EventHandler
    public void OnDrop(PlayerDropItemEvent event){
        ItemStack eventItem = event.getItemDrop().getItemStack();
        if (eventItem.getType().isAir()) return;
        Sync(eventItem);
    }

    @EventHandler
    public void OnHold(PlayerItemHeldEvent event){
        ItemStack eventItem = event.getPlayer().getInventory().getItemInMainHand();
        if (eventItem.getType().isAir()) return;
        Sync(eventItem);

    }

    private void Sync(ItemStack eventItem){
        PersistentDataContainer cont = eventItem.getItemMeta()
                .getPersistentDataContainer()
                .get(DorisKeys.sync, PersistentDataType.TAG_CONTAINER);
        if (cont == null) return;
        String code = cont.get(DorisKeys.sync_code, PersistentDataType.STRING);
        ItemSyncData data = DataSerializer.itemSyncMap.get(code);
        if (data == null) return;
        if (cont.get(DorisKeys.sync_version, PersistentDataType.INTEGER) == data.version) return;
        ItemMeta before = eventItem.getItemMeta();
        eventItem.setItemMeta(data.item.getItemMeta());
        eventItem.editMeta(meta -> {
            if (meta instanceof Damageable d){
                d.setDamage(((Damageable)before).getDamage());
            }
            PersistentDataContainer container = meta.getPersistentDataContainer();
            // 빈 container를 전달하여 빈 공간 생성
            container.set(DorisKeys.sync, PersistentDataType.TAG_CONTAINER, container);
            PersistentDataContainer loot = container.get(DorisKeys.sync, PersistentDataType.TAG_CONTAINER);
            loot.set(DorisKeys.sync_code, PersistentDataType.STRING, code);
            loot.set(DorisKeys.sync_version, PersistentDataType.INTEGER, data.version);
            container.set(DorisKeys.sync, PersistentDataType.TAG_CONTAINER, loot);
        });
        if (data.asyncEnchant){
            eventItem.addUnsafeEnchantments(before.getEnchants());
        }
    }
}
