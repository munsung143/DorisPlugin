package org.exam.dorisPlugin;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.EntityType;

public class EntityData {
    public static class EffectForm{
        public String id;
        public byte amplifier;
        public int duration;
    }
    public static class AttributeForm{
        public String id;
        public String operation;
        public Double amount;
    }
    public EntityType type;
    public String custom_name;
    public boolean custom_name_visible;
    public short Fire;
    public boolean Glowing;
    public boolean HasVisualFire;
    public boolean Invulnerable;
    public boolean NoGravity;
    public String[] Passengers;
    public boolean Silent;

    public float AbsorptionAmount;
    public EffectForm[] ActiveEffects;
    public AttributeForm[] Attributes;
    public boolean CanPickUpLoot;
    public String DeathLootTable;
    public float[] DropChances;
    public String[] Equipment;
    public float Health;
    public boolean LeftHanded;
    public boolean NoAI;
    public boolean PersistenceRequired;

}
