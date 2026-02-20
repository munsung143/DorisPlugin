package org.exam.dorisPlugin;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;

public class RandomItemConsumer implements Listener {
    public static String keyString = "random";
    private Map<String, RandomTable> randomTableMap;
    private NamespacedKey namespacedKey;

    public RandomItemConsumer(Map<String, RandomTable> randomTableMap){
        this.randomTableMap = randomTableMap;
        namespacedKey = NamespacedKey.fromString(RandomItemConsumer.keyString, Main.plugin);
    }

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
        String key = meta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);;
        if (key == null) return;
        if (!randomTableMap.containsKey(key)) return;
        if (action == Action.RIGHT_CLICK_BLOCK){
            event.setCancelled(true);
        }
        RandomTable table = randomTableMap.get(key);
        List<RandomGroup> groups = table.groups;
        handItem.setAmount(handItem.getAmount() - 1);
        int select =  (int)(Math.random() * table.weightSum);
        int sum = 0;
        for (RandomGroup group : groups){
            sum += group.weight;
            if (select < sum){
                Player p = event.getPlayer();
                if (group.stacks != null){
                    p.give(group.stacks);
                }
                if (group.message != null){
                    p.sendMessage(new TextFormatBuilder(group.message).Build());
                }
                break;
            }
        }

    }
}
