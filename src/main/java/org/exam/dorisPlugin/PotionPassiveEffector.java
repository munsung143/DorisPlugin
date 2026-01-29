package org.exam.dorisPlugin;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class PotionPassiveEffector implements Listener {
    private static final Map<UUID, List<int[]>> playerBuffs = new HashMap<>();

    @EventHandler
    public void OnEquipChange(EntityEquipmentChangedEvent event){
        if (event.getEntity() instanceof Player player){
            playerBuffs.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>());
            var changes = event.getEquipmentChanges();
            RemoveEffect(changes.get(EquipmentSlot.HEAD),"potion_passive_armor", player);
            RemoveEffect(changes.get(EquipmentSlot.CHEST),"potion_passive_armor", player);
            RemoveEffect(changes.get(EquipmentSlot.LEGS),"potion_passive_armor", player);
            RemoveEffect(changes.get(EquipmentSlot.FEET),"potion_passive_armor", player);
            RemoveEffect(changes.get(EquipmentSlot.HAND),"potion_passive_mainhand", player);
            RemoveEffect(changes.get(EquipmentSlot.OFF_HAND),"potion_passive_offhand", player);
            ApplyEffect(EquipmentSlot.HEAD, "potion_passive_armor", player);
            ApplyEffect(EquipmentSlot.CHEST, "potion_passive_armor", player);
            ApplyEffect(EquipmentSlot.LEGS, "potion_passive_armor", player);
            ApplyEffect(EquipmentSlot.FEET, "potion_passive_armor", player);
            ApplyEffect(EquipmentSlot.HAND, "potion_passive_mainhand", player);
            ApplyEffect(EquipmentSlot.OFF_HAND, "potion_passive_offhand", player);

        }
    }
    private void RemoveEffect(EntityEquipmentChangedEvent.EquipmentChange c, String keyStr, Player player){
        if (c == null) return;
        NamespacedKey key = NamespacedKey.fromString(keyStr, Main.plugin);
        List<int[]> buffList = playerBuffs.get(player.getUniqueId());
        ItemMeta oldMeta = c.oldItem().getItemMeta();
        if (oldMeta == null) return;
        List<int[]> list = oldMeta.getPersistentDataContainer().get(key, PersistentDataType.LIST.integerArrays());
        if (list == null) return;
        for (int[] pair : list){
            player.removePotionEffect(EffectType.GetType(pair[0]));
        }


    }
    private void ApplyEffect(EquipmentSlot slot,String keyStr, Player player){
        ItemMeta meta = player.getInventory().getItem(slot).getItemMeta();
        if (meta == null) return;
        NamespacedKey key = NamespacedKey.fromString(keyStr, Main.plugin);
        List<int[]> list = meta.getPersistentDataContainer().get(key, PersistentDataType.LIST.integerArrays());
        if (list == null) return;
        for (int[] pair : list){
            PotionEffectType type = EffectType.GetType(pair[0]);
            int amp = pair[1];
            PotionEffect curEffect = player.getPotionEffect(type);
            if (curEffect == null || curEffect.getAmplifier() < amp){
                player.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, amp));
            }

        }

    }
}
