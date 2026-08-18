package org.exam.dorisPlugin.Events;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.exam.dorisPlugin.*;
import org.exam.dorisPlugin.enums.EffectType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class PotionUseEffector implements Listener {


    public static Map<Integer, List<PotionUseData>> PotionUseItemData = new HashMap<>();
    public static Map<UUID, Map<Integer, Map<PotionEffectType, Long>>> PlayerLastData = new HashMap<>();

    @EventHandler
    public void OnRightClick(PlayerInteractEvent event){
        var action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK){
            return;
        }
        if (event.getHand() == EquipmentSlot.OFF_HAND){
            return;
        }
        ItemStack handItem = event.getItem();
        if (handItem == null) return;
        ItemMeta meta = handItem.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer loot = meta.getPersistentDataContainer().get(DorisKeys.potion_use, PersistentDataType.TAG_CONTAINER);
        if (loot == null) return;
        int code = loot.get(DorisKeys.potion_use_code, PersistentDataType.INTEGER);
        List<int[]> list = loot.get(DorisKeys.potion_use_list, PersistentDataType.LIST.integerArrays());
        List<PotionUseData> data = PotionUseItemData.get(code);
        if (data == null){
            data = new ArrayList<>();
            PotionUseItemData.put(code, data);
            for (int[] pair : list){
                PotionUseData pd = new PotionUseData();
                pd.effect = new PotionEffect(EffectType.GetType(pair[0]), pair[2], pair[1]);
                pd.chance = pair[3];
                pd.cooldown = pair[4] * 100;
                pd.sneak = pair[5] == 1;
                data.add(pd);
                }
            }
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Map<Integer, Map<PotionEffectType, Long>> personalItemsLastData = PlayerLastData.get(uuid);
        if (personalItemsLastData == null) {
            personalItemsLastData = new HashMap<>();
            PlayerLastData.put(uuid, personalItemsLastData);
        }
        Map<PotionEffectType, Long> targetItemLastData = personalItemsLastData.get(code);
        if (targetItemLastData == null) {
            targetItemLastData = new HashMap<>();
            personalItemsLastData.put(code, targetItemLastData);
        }

        StringBuilder successStr = new StringBuilder("§a효과 적용됨 : ");
        StringBuilder failStr = new StringBuilder("§7적용 실패 : ");
        StringBuilder coolStr = new StringBuilder("§c남은 시간 : ");
        boolean successOnce = false;
        boolean failOnce = false;
        boolean coolOnce = false;

        for (PotionUseData d : data){
            if (MenuUIOpener.playerSneakState.get(uuid) != d.sneak) continue;
            PotionEffectType type = d.effect.getType();
            Long last = targetItemLastData.get(type);
            if (last == null){
                last = 0L;
                targetItemLastData.put(type, last);
            }
            long current = System.currentTimeMillis();
            long passing = current - last;
            long remain = d.cooldown - passing;
            if (remain < 0) {
                if (d.chance > ThreadLocalRandom.current().nextInt(1, 1000)){
                    player.addPotionEffect(d.effect);
                    successStr.append(EffectType.GetName(type)).append(" ");
                    successOnce = true;
                }
                else {
                    failStr.append(EffectType.GetName(type)).append(" ");
                    failOnce = true;
                }
                targetItemLastData.put(type, current);
            }
            else {
                coolStr.append(EffectType.GetName(type)).append(remain / 1000).append(" ");
                coolOnce = true;
            }
        }
        if (successOnce){
            player.sendMessage(successStr.toString());
            player.damageItemStack(EquipmentSlot.HAND, loot.get(DorisKeys.potion_use_dur, PersistentDataType.INTEGER));
        }
        if (failOnce) player.sendMessage(failStr.toString());
        if (coolOnce) player.sendMessage(coolStr.toString());
    }
}
