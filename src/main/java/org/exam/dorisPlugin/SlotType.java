package org.exam.dorisPlugin;

import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum SlotType {
    몸(EquipmentSlot.BODY, EquipmentSlotGroup.BODY),
    동물(EquipmentSlot.BODY, EquipmentSlotGroup.BODY),
    상체(EquipmentSlot.CHEST, EquipmentSlotGroup.CHEST),
    상의(EquipmentSlot.CHEST, EquipmentSlotGroup.CHEST),
    하의(EquipmentSlot.LEGS, EquipmentSlotGroup.LEGS),
    바지(EquipmentSlot.LEGS, EquipmentSlotGroup.LEGS),
    하체(EquipmentSlot.LEGS, EquipmentSlotGroup.LEGS),
    신발(EquipmentSlot.FEET, EquipmentSlotGroup.FEET),
    발(EquipmentSlot.FEET, EquipmentSlotGroup.FEET),
    머리(EquipmentSlot.HEAD, EquipmentSlotGroup.HEAD),
    모자(EquipmentSlot.HEAD, EquipmentSlotGroup.HEAD),
    투구(EquipmentSlot.HEAD, EquipmentSlotGroup.HEAD),
    오른손(EquipmentSlot.HAND, EquipmentSlotGroup.MAINHAND),
    손(EquipmentSlot.HAND, EquipmentSlotGroup.HAND),
    왼손(EquipmentSlot.OFF_HAND, EquipmentSlotGroup.OFFHAND),
    안장(EquipmentSlot.SADDLE, EquipmentSlotGroup.SADDLE),
    갑옷(EquipmentSlot.CHEST, EquipmentSlotGroup.ARMOR),
    옷(EquipmentSlot.CHEST, EquipmentSlotGroup.ARMOR),
    전체(EquipmentSlot.HAND, EquipmentSlotGroup.ANY);

    private final EquipmentSlot type;
    private final EquipmentSlotGroup group;
    private SlotType (EquipmentSlot slot, EquipmentSlotGroup group){
        this.type = slot;
        this.group = group;
    }
    private static final Map<String, EquipmentSlot> To_Type = new HashMap<>();
    private static final Map<String, EquipmentSlotGroup> To_Group = new HashMap<>();
    static {
        for (SlotType s : values()) {
            To_Type.put(s.name(), s.type);
            To_Group.put(s.name(), s.group);
        }
    }
    public static EquipmentSlot GetSlot(String name){
        return To_Type.get(name);
    }
    public static EquipmentSlotGroup GetSlotGroup(String name){
        return To_Group.get(name);
    }

}
