package org.exam.dorisPlugin.enums;

import org.bukkit.event.inventory.InventoryType;

import java.util.HashMap;
import java.util.Map;

public enum FunctionalBlockType {

    작업대(1, InventoryType.WORKBENCH),
    화로(1 << 1, InventoryType.FURNACE),
    인첸트(1 << 2, InventoryType.ENCHANTING),
    모루(1 << 3, InventoryType.ANVIL),
    숫돌(1 << 4, InventoryType.GRINDSTONE),
    석재절단기(1 << 5, InventoryType.STONECUTTER),
    훈연기(1 << 6, InventoryType.SMOKER),
    용광로(1 << 7, InventoryType.BLAST_FURNACE),
    대장장이(1 << 8, InventoryType.SMITHING),
    베틀(1 << 9, InventoryType.LOOM),
    제작기(1 << 10, InventoryType.CRAFTER),
    플레이어(1 << 11, InventoryType.CRAFTING),
    양조기(1 << 12, InventoryType.BREWING),

    전부(-1, null); // 특수 케이스

    private final int mask;
    private final InventoryType type;
    private FunctionalBlockType (int mask, InventoryType type){
        this.type = type;
        this.mask = mask;
    }
    private static final Map<String, Integer> name2Mask = new HashMap<>();
    private static final Map<InventoryType, Integer> type2Mask = new HashMap<>();
    static {
        for (FunctionalBlockType s : values()) {
            name2Mask.put(s.name(), s.mask);
            type2Mask.put(s.type, s.mask);
        }
    }
    public static Integer name2Mask(String name){
        return name2Mask.get(name);
    }
    public static Integer type2Mask(InventoryType type){
        if (type2Mask.containsKey(type)){
            return type2Mask.get(type);
        }
        else {
            return null;
        }
    }
}
