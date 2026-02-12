package org.exam.dorisPlugin;

import net.kyori.adventure.text.Component;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import java.util.List;

public class EntityData {
    public static String typeStr = "type";
    public static String customNameStr = "custom_name";
    public static String customNameVisibleStr = "custom_name_visible";
    public static String fireStr = "fire";
    public static String glowingStr = "glowing";
    public static String hasVisualFireStr = "has_visual_fire";
    public static String invulnerableStr = "invulnerable";
    public static String noGravityStr = "no_gravity";
    public static String passengersStr = "passengers";
    public static String silentStr = "silent";
    public static String absorptionAmountStr = "absorption_amount";
    public static String activeEffectsStr = "active_effects";
    public static String attributesStr = "attributes";
    public static String canPickUpLootStr = "can_pick_up_loot";
    public static String deathLootTableStr = "death_loot_table";
    public static String equipmentStr = "equipment";
    public static String dropChancesStr = "drop_chances";
    public static String healthStr = "health";
    public static String leftHandedStr = "left_handed";
    public static String noAIStr = "no_ai";
    public static String persistenceRequiredStr = "persistence_required";

    public static class BaseAttribute {

        public BaseAttribute(Attribute attribute, Double base){
            this.attribute = attribute;
            this.base = base;
        }
        public Attribute attribute;
        public Double base;
    }
    public static class EquipItemStacks {

        public ItemStack head;
        public ItemStack chest;
        public ItemStack legs;
        public ItemStack feet;
        public ItemStack mainhand;
        public ItemStack offhand;
    }

    public EntityType type;
    public String custom_name;
    public Boolean custom_name_visible;
    public Short fire;
    public Boolean glowing;
    public Boolean has_visual_fire;
    public Boolean invulnerable;
    public Boolean no_gravity;
    public List<String> passengers;
    public Boolean silent;

    public Float absorption_amount;
    public List<PotionEffect> active_effects;
    public List<BaseAttribute> attributes;
    public Boolean can_pick_up_loot;
    public String death_loot_table;
    public List<Float> drop_chances;
    public EquipItemStacks equipment;
    public Float health;
    public Boolean left_handed;
    public Boolean no_ai;
    public Boolean persistence_required;

}
