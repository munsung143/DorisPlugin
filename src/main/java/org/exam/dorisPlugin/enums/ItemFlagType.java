package org.exam.dorisPlugin.enums;

import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import org.bukkit.inventory.ItemFlag;

import java.util.HashMap;
import java.util.Map;

public enum ItemFlagType {
    갑옷장식(ItemFlag.HIDE_ARMOR_TRIM),
    속성(ItemFlag.HIDE_ATTRIBUTES),
    인첸트(ItemFlag.HIDE_ENCHANTS),
    염색(ItemFlag.HIDE_DYE),
    부서지지않음(ItemFlag.HIDE_UNBREAKABLE);
    private ItemFlag flag;
    private ItemFlagType(ItemFlag flag){
        this.flag = flag;
    }
    private static final Map<String, ItemFlag> To_Flag = new HashMap<>();
    static {
        for (ItemFlagType t : values()) {
            To_Flag.put(t.name(), t.flag);
        }
    }
    public static ItemFlag GetFlag(String str){
        return To_Flag.get(str);
    }
}
