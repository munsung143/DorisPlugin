package org.exam.dorisPlugin;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.exam.dorisPlugin.enums.EffectType;

import java.util.List;

public class PotionAttackEffector implements Listener {

    @EventHandler
    public void OnAttack(EntityDamageByEntityEvent event){
        if (event.getDamager() instanceof LivingEntity attacker && event.getEntity() instanceof LivingEntity victim){
            ApplyEffect(EquipmentSlot.HEAD, "potion_attack_armor", attacker, victim);
            ApplyEffect(EquipmentSlot.CHEST, "potion_attack_armor", attacker, victim);
            ApplyEffect(EquipmentSlot.LEGS, "potion_attack_armor", attacker, victim);
            ApplyEffect(EquipmentSlot.FEET, "potion_attack_armor", attacker, victim);
            ApplyEffect(EquipmentSlot.HAND, "potion_attack_mainhand", attacker, victim);
            ApplyEffect(EquipmentSlot.OFF_HAND, "potion_attack_offhand", attacker, victim);
        }
    }
    private void ApplyEffect(EquipmentSlot slot, String keyStr, LivingEntity attacker, LivingEntity victim){
        EntityEquipment equipmemt = attacker.getEquipment();
        if (equipmemt == null) return;
        ItemMeta meta = equipmemt.getItem(slot).getItemMeta();
        if (meta == null) return;
        NamespacedKey key = NamespacedKey.fromString(keyStr, Main.plugin);
        List<int[]> list = meta.getPersistentDataContainer().get(key, PersistentDataType.LIST.integerArrays());
        if (list == null) return;
        for (int[] pair : list){
            int chance = (int)(Math.random() * 1000);
            if (chance > pair[3]) continue;
            PotionEffectType type = EffectType.GetType(pair[0]);
            int amp = pair[1];
            PotionEffect curEffect = attacker.getPotionEffect(type);
            if (curEffect == null || curEffect.getAmplifier() < amp){
                victim.addPotionEffect(new PotionEffect(type, pair[2], amp));
            }

        }

    }
}
