package org.exam.dorisPlugin;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EntityDataSerializer {

    private final static Registry<EntityType> entityTypeRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENTITY_TYPE);
    private final static Registry<PotionEffectType> effectTypeRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.MOB_EFFECT);
    private final static Registry<Attribute> attributeRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE);

    public static Map<String, EntityData> Deserialize(YamlConfiguration snapshot){
        RegistryAccess RegiAccess = RegistryAccess.registryAccess();
        Map<String, EntityData> deSerializedData = new HashMap<>();

        for (String key : snapshot.getKeys(false)){
            ConfigurationSection section = snapshot.getConfigurationSection(key);
            EntityData data = new EntityData();
            if (section == null) return null;
            String typeStr = section.getString(EntityData.typeStr);
            if (typeStr != null){
                data.type = entityTypeRegistry.get(NamespacedKey.minecraft(typeStr));
            }
            data.custom_name = section.getString(EntityData.customNameStr);
            data.custom_name_visible = section.getBoolean(EntityData.customNameVisibleStr);
            data.fire = (short)section.getInt(EntityData.fireStr);
            data.glowing = section.getBoolean(EntityData.glowingStr);
            data.has_visual_fire = section.getBoolean(EntityData.hasVisualFireStr);
            data.invulnerable = section.getBoolean(EntityData.invulnerableStr);
            data.no_gravity = section.getBoolean(EntityData.noGravityStr);
            data.silent = section.getBoolean(EntityData.silentStr);
            data.passengers = section.getStringList(EntityData.passengersStr);
            data.absorption_amount = (float)section.getDouble(EntityData.absorptionAmountStr);
            List<Map<?,?>> potionMapList = section.getMapList(EntityData.activeEffectsStr);
            if (!potionMapList.isEmpty()){
                data.active_effects = new ArrayList<>(0);
                for (Map<?,?> m : potionMapList){
                    PotionEffectType type = effectTypeRegistry.get(NamespacedKey.minecraft((String)m.get("name")));
                    if (type == null) continue;
                    int duration = (int)m.get("duration");
                    int amplifier = (int)m.get("amplifier");
                    data.active_effects.add(new PotionEffect(type, duration, amplifier));
                }
            }
            List<Map<?,?>> attributeMapList = section.getMapList(EntityData.attributesStr);
            if (!attributeMapList.isEmpty()){
                data.attributes = new ArrayList<>(0);
                for (Map<?,?> m : attributeMapList){
                    Attribute type = attributeRegistry.get(NamespacedKey.minecraft((String)m.get("name")));
                    if (type == null) continue;
                    double base = (double)m.get("base");
                    data.attributes.add(new EntityData.BaseAttribute(type, base));
                }
            }
            List<Double> dcList = section.getDoubleList(EntityData.dropChancesStr);
            if (!dcList.isEmpty()){
                data.drop_chances = new ArrayList<>();
                for (double d : dcList){
                    data.drop_chances.add((float)d);
                }
            }
            ConfigurationSection equipSection = section.getConfigurationSection(EntityData.equipmentStr);
            if (equipSection != null){
                data.equipment = new EntityData.EquipItemStacks();
                data.equipment.head = equipSection.getItemStack("head");
                data.equipment.chest = equipSection.getItemStack("chest");
                data.equipment.legs = equipSection.getItemStack("legs");
                data.equipment.feet = equipSection.getItemStack("feet");
                data.equipment.mainhand = equipSection.getItemStack("mainhand");
                data.equipment.offhand = equipSection.getItemStack("offhand");
            }
            data.can_pick_up_loot = section.getBoolean(EntityData.canPickUpLootStr);
            data.left_handed = section.getBoolean(EntityData.leftHandedStr);
            data.no_ai = section.getBoolean("no_ai");
            data.persistence_required = section.getBoolean("persistence_required");
            data.death_loot_table = section.getString("death_loot_table");
            data.health = (float)section.getDouble("health");

            deSerializedData.put(key, data);
        }
        return deSerializedData;
    }
    public static YamlConfiguration Serialize(Map<String, EntityData> entityData){
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<String, EntityData> e : entityData.entrySet()){
            String key = e.getKey();
            EntityData value = e.getValue();
            ConfigurationSection section = config.createSection(key);

            section.set("type", value.type.getKey().getKey());
            if (value.custom_name != null){
                section.set("custom_name", value.custom_name);
            }
            if (value.custom_name_visible != null){
                section.set("custom_name_visible", value.custom_name_visible);
            }
            if (value.fire != null){
                section.set("fire", value.fire);
            }
            if (value.glowing != null) section.set("glowing", value.glowing);
            if (value.has_visual_fire != null) section.set("has_visual_fire", value.has_visual_fire);
            if (value.invulnerable != null) section.set("invulnerable", value.invulnerable);
            if (value.no_gravity != null) section.set("no_gravity", value.no_gravity);
            if (value.silent != null) section.set("silent", value.silent);
            if (!value.passengers.isEmpty()) section.set("passengers", value.passengers);
            if (value.absorption_amount != null) section.set("absorption_amount", value.absorption_amount);

            if (!value.active_effects.isEmpty()){
                ConfigurationSection effectsection = section.getConfigurationSection("active_effects");
                for (PotionEffect p : value.active_effects){
                    if (p == null) continue;
                    effectsection.set("name", p.getType().getKey().getKey());
                    effectsection.set("duration", p.getDuration());
                    effectsection.set("amplifier", p.getAmplifier());
                }
            }
            if (!value.attributes.isEmpty()){
                ConfigurationSection attributeSection = section.getConfigurationSection("attributes");
                for (EntityData.BaseAttribute b : value.attributes){
                    if (b == null) continue;
                    attributeSection.set("name", b.attribute.getKey().getKey());
                    attributeSection.set("base", b.base);
                }
            }
            if (!value.drop_chances.isEmpty()){
                section.set("drop_chances", value.drop_chances);
            }
            if (value.equipment != null){
                ConfigurationSection equipmentSection = section.getConfigurationSection("equipment");
                if (value.equipment.head != null) equipmentSection.set("head", value.equipment.head.serialize());
                if (value.equipment.chest != null) equipmentSection.set("chest", value.equipment.chest.serialize());
                if (value.equipment.legs != null) equipmentSection.set("legs", value.equipment.legs.serialize());
                if (value.equipment.feet != null) equipmentSection.set("feet", value.equipment.feet.serialize());
                if (value.equipment.mainhand != null) equipmentSection.set("mainhand", value.equipment.mainhand.serialize());
                if (value.equipment.offhand != null) equipmentSection.set("offhand", value.equipment.offhand.serialize());
            }

            if (value.can_pick_up_loot != null) section.set("can_pick_up_loot", value.can_pick_up_loot);
            if (value.left_handed != null) section.set("left_handed", value.left_handed);
            if (value.no_ai != null) section.set("no_ai", value.no_ai);
            if (value.persistence_required != null) section.set("persistence_required", value.persistence_required);
            if (value.death_loot_table != null) section.set("death_loot_table", value.death_loot_table);
            if (value.health != null) section.set("health", value.health);

        }
        return config;
    }

}
