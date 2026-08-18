package org.exam.dorisPlugin.Events;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.exam.dorisPlugin.DamageTestCommandExecutor;
import org.exam.dorisPlugin.Debug;
import org.exam.dorisPlugin.DorisKeys;
import org.exam.dorisPlugin.Main;
import org.exam.dorisPlugin.enums.EffectType;

import java.util.List;

public class AttackEffector implements Listener {

    @EventHandler
    public void OnAttack(EntityDamageByEntityEvent event){
        if (event.getDamager() instanceof HumanEntity attacker && event.getEntity() instanceof LivingEntity victim){
            randomDamage(attacker, event);
            if (attacker.isOp()){
                Integer i = DamageTestCommandExecutor.testTypeMap.get(attacker.getUniqueId());
                if (i == null) return;
                switch (i){
                    case 1: dealTest(attacker, event, victim); break;
                    case 2: dealTest2(attacker, event, victim); break;
                }
            }
        }
    }

    private void randomDamage(HumanEntity attacker, EntityDamageByEntityEvent event){
        EntityEquipment equipmemt = attacker.getEquipment();
        if (equipmemt == null) return;
        ItemMeta meta = equipmemt.getItem(EquipmentSlot.HAND).getItemMeta();
        if (meta == null) return;
        Double val = meta.getPersistentDataContainer().get(DorisKeys.randomDamage, PersistentDataType.DOUBLE);
        if (val == null) return;
        event.setDamage(event.getDamage() + val * Math.random() * (event.isCritical() ? 1.5 : 1) * attacker.getAttackCooldown());
    }
    private void dealTest(HumanEntity attacker, EntityDamageByEntityEvent event, LivingEntity victim){
        double atk = event.getDamage();
        double dmg = event.getFinalDamage();
        double health = victim.getHealth();
        double remain = Math.clamp(health - dmg, 0, Double.MAX_VALUE);
        int tick = victim.getNoDamageTicks();
        StringBuilder sb = new StringBuilder().append("\n§e데미지:§f ");
        if (tick > 10){
            double last = victim.getLastDamage();
            double atkBefore = atk;
            atk -= last;
            sb.append(String.format("%.3f", atk)).append("§7 = ").append(String.format("%.3f", atkBefore)).append(" - ").append(String.format("%.3f", last))
                    .append(" ").append(tick).append("틱");
        }
        else{
            sb.append(String.format("%.3f", atk));
        }
        sb.append(" §7(딜레이 ").append(String.format("%.2f", attacker.getAttackCooldown() * 100)).append("%)");
        if (event.isCritical()) sb.append("§f | §b크리티컬");
        sb.append("\n§a최종뎀:§f ").append(String.format("%.3f", dmg))
                .append(" | §d").append(String.format("%.2f", health)).append(" -> ");
        sb.append(remain <= 0 ? "§c사망" : String.format("%.2f", remain))
                .append("\n§3감소율:§f ").append(String.format("%.2f", (atk - dmg) / atk * 100)).append("%");
        attacker.sendMessage(sb.toString());
    }
    private void dealTest2(HumanEntity attacker, EntityDamageByEntityEvent event, LivingEntity victim){
        double atk = event.getDamage();
        double dmg = event.getFinalDamage();
        double health = victim.getHealth();
        double remain = Math.clamp(health - dmg, 0, Double.MAX_VALUE);
        int tick = victim.getNoDamageTicks();
        StringBuilder sb = new StringBuilder().append("§eA: §f");
        if (tick > 10){
            double last = victim.getLastDamage();
            atk -= last;
            sb.append("§7").append(String.format("%.2f", atk));
        }
        else{
            sb.append(String.format("%.2f", atk));
        }
        sb.append(" §7(D ").append(String.format("%.1f", attacker.getAttackCooldown() * 100)).append("%)");
        sb.append(" §aD:§f ").append(String.format("%.2f", dmg))
                .append(" | §d").append(String.format("%.1f", health)).append(" -> ");
        sb.append(remain <= 0 ? "§cX" : String.format("%.1f", remain))
                .append("§f ").append(String.format("%.1f", (atk - dmg) / atk * 100)).append("%");
        if (event.isCritical()) sb.append("  §bC");
        attacker.sendMessage(sb.toString());
    }
}
