package org.exam.dorisPlugin.Events;
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.exam.dorisPlugin.DorisKeys;
import org.exam.dorisPlugin.Main;
import org.exam.dorisPlugin.enums.EffectType;

import java.util.*;

public class PotionPassiveEffector implements Listener {
    private static final Map<UUID, List<int[]>> playerBuffs = new HashMap<>();

    @EventHandler
    public void OnEquipChange(EntityEquipmentChangedEvent event){
        if (event.getEntity() instanceof LivingEntity entity){
            //playerBuffs.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>());
            var changes = event.getEquipmentChanges();
            for (var a : changes.keySet()){
                NamespacedKey key;
                switch (a){
                    case EquipmentSlot.HAND -> key = DorisKeys.potion_passive_mainhand;
                    case EquipmentSlot.OFF_HAND -> key = DorisKeys.potion_passive_offhand;
                    default -> key = DorisKeys.potion_passive_armor;
                }
                RemoveEffect(changes.get(a),key, entity);
            }
            ApplyEffectAllSlots(entity);

        }
    }
    @EventHandler
    public void OnRespawn(PlayerPostRespawnEvent event){
        Player player = event.getPlayer();
        ApplyEffectAllSlots(player);
    }

    @EventHandler
    public void OnEntitySpawn(EntitySpawnEvent event){
        if (event.getEntity() instanceof LivingEntity entity){
            ApplyEffectAllSlots(entity);
        }
    }
    private void ApplyEffectAllSlots(LivingEntity entity){
        ApplyEffect(EquipmentSlot.HEAD, DorisKeys.potion_passive_armor, entity);
        ApplyEffect(EquipmentSlot.CHEST, DorisKeys.potion_passive_armor, entity);
        ApplyEffect(EquipmentSlot.LEGS, DorisKeys.potion_passive_armor, entity);
        ApplyEffect(EquipmentSlot.FEET, DorisKeys.potion_passive_armor, entity);
        ApplyEffect(EquipmentSlot.HAND, DorisKeys.potion_passive_mainhand, entity);
        ApplyEffect(EquipmentSlot.OFF_HAND, DorisKeys.potion_passive_offhand, entity);
    }
    private void RemoveEffect(EntityEquipmentChangedEvent.EquipmentChange c, NamespacedKey key, LivingEntity entity){
        if (c == null) return;
        //List<int[]> buffList = playerBuffs.get(player.getUniqueId());
        ItemMeta oldMeta = c.oldItem().getItemMeta();
        if (oldMeta == null) return;
        List<int[]> list = oldMeta.getPersistentDataContainer().get(key, PersistentDataType.LIST.integerArrays());
        if (list == null) return;
        for (int[] pair : list){
            PotionEffectType type = EffectType.GetType(pair[0]);
            if (entity.hasPotionEffect(type)){
                PotionEffect ef = entity.getPotionEffect(type);
                entity.removePotionEffect(type);
                ApplyUnintended(ef, entity, pair[1]);
            }
        }
    }
    private void ApplyUnintended(PotionEffect effect, LivingEntity entity, int targetLevel){
        if (effect.getAmplifier() != targetLevel){
            entity.addPotionEffect(new PotionEffect(effect.getType(), effect.getDuration(), effect.getAmplifier()));
        }
        PotionEffect next = effect.getHiddenPotionEffect();
        if (next != null) ApplyUnintended(effect.getHiddenPotionEffect(), entity, targetLevel);

    }
    private void ApplyEffect(EquipmentSlot slot, NamespacedKey key, LivingEntity entity){
        EntityEquipment equipmemt = entity.getEquipment();
        if (equipmemt == null) return;
        ItemMeta meta = equipmemt.getItem(slot).getItemMeta();
        //ItemMeta meta = player.getInventory().getItem(slot).getItemMeta();
        if (meta == null) return;
        List<int[]> list = meta.getPersistentDataContainer().get(key, PersistentDataType.LIST.integerArrays());
        if (list == null) return;
        for (int[] pair : list){
            PotionEffectType type = EffectType.GetType(pair[0]);
            int amp = pair[1];
            PotionEffect curEffect = entity.getPotionEffect(type);
            //if (curEffect == null || curEffect.getAmplifier() < amp){
            //    entity.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, amp));
            //}
            entity.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, amp));

        }

    }
}
