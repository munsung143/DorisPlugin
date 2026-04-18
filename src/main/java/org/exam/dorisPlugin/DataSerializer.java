package org.exam.dorisPlugin;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.exam.dorisPlugin.Legacy.EntityData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DataSerializer {
    private static File entityDataYmlFile;
    private static YamlConfiguration entityDataYml;
    public static Map<String, EntityData> entityDataMap = new HashMap<>();

    private static File randomDataYmlFile;
    private static YamlConfiguration randomTableYml;
    public static Map<String, RandomTable> randomTableMap = new HashMap<>();

    private static File itemSyncYmlFile;
    private static YamlConfiguration itemSyncYml;
    public static Map<String, ItemSyncData> itemSyncMap = new HashMap<>();
    public static Map<String, String> itemSyncIdMap = new HashMap<>();


    public static void LoadFile(File dataFolder){
        entityDataYmlFile = new File(dataFolder, "EntityData.yml");
        if (!entityDataYmlFile.exists()) {
            try {
                entityDataYmlFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        entityDataYml = YamlConfiguration.loadConfiguration(entityDataYmlFile);
        entityDataDeserialize();

        randomDataYmlFile = new File(dataFolder, "RandomData.yml");
        if (!randomDataYmlFile.exists()) {
            try {
                randomDataYmlFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        randomTableYml = YamlConfiguration.loadConfiguration(randomDataYmlFile);
        randomDataDeserialize();


        itemSyncYmlFile = new File(dataFolder, "Item.yml");
        if (!itemSyncYmlFile.exists()) {
            try {
                itemSyncYmlFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        itemSyncYml = YamlConfiguration.loadConfiguration(itemSyncYmlFile);
        itemSyncDeserialize();
    }

    public static void SaveEntityData(){
        try {
            entityDataYml.save(entityDataYmlFile);

        } catch (IOException e){
            return;
        }
    }
    public static void SaveRandomData(){
        try {
            randomTableYml.save(entityDataYmlFile);
            return;
        } catch (IOException e){
            return;
        }
    }
    public static void SaveItemSyncData(){
        try{
            itemSyncYml.save(itemSyncYmlFile);
        } catch (IOException e){
            return;
        }
    }

    public static void entityDataDeserialize(){
        Map<String, EntityData> deSerializedData = new HashMap<>();

        for (String key : entityDataYml.getKeys(false)){
            ConfigurationSection section = entityDataYml.getConfigurationSection(key);
            EntityData data = new EntityData();
            if (section == null) return;
            String typeStr = section.getString(EntityData.typeStr);
            if (typeStr != null){
                data.type = Registries.EntityType.get(NamespacedKey.minecraft(typeStr));
            }
            if (section.contains(EntityData.customNameStr)) data.custom_name = section.getString(EntityData.customNameStr);
            if (section.contains(EntityData.customNameVisibleStr)) data.custom_name_visible = section.getBoolean(EntityData.customNameVisibleStr);
            if (section.contains(EntityData.fireStr)) data.fire = (short)section.getInt(EntityData.fireStr);
            if (section.contains(EntityData.glowingStr)) data.glowing = section.getBoolean(EntityData.glowingStr);
            if (section.contains(EntityData.hasVisualFireStr)) data.has_visual_fire = section.getBoolean(EntityData.hasVisualFireStr);
            if (section.contains(EntityData.invulnerableStr)) data.invulnerable = section.getBoolean(EntityData.invulnerableStr);
            if (section.contains(EntityData.noGravityStr)) data.no_gravity = section.getBoolean(EntityData.noGravityStr);
            if (section.contains(EntityData.silentStr)) data.silent = section.getBoolean(EntityData.silentStr);
            if (section.contains(EntityData.passengersStr)) data.passengers = section.getStringList(EntityData.passengersStr);
            if (section.contains(EntityData.absorptionAmountStr)) data.absorption_amount = (float)section.getDouble(EntityData.absorptionAmountStr);
            if (section.contains(EntityData.activeEffectsStr)){
                List<Map<?,?>> potionMapList = section.getMapList(EntityData.activeEffectsStr);
                if (!potionMapList.isEmpty()){
                    data.active_effects = new ArrayList<>(0);
                    for (Map<?,?> m : potionMapList){
                        PotionEffectType type = Registries.MobEffect.get(NamespacedKey.minecraft((String)m.get("name")));
                        if (type == null) continue;
                        int duration = (int)m.get("duration");
                        int amplifier = (int)m.get("amplifier");
                        data.active_effects.add(new PotionEffect(type, duration, amplifier));
                    }
                }
            }
            if (section.contains(EntityData.attributesStr)){
                List<Map<?,?>> attributeMapList = section.getMapList(EntityData.attributesStr);
                if (!attributeMapList.isEmpty()){
                    data.attributes = new ArrayList<>(0);
                    for (Map<?,?> m : attributeMapList){
                        Attribute type = Registries.Attribute.get(NamespacedKey.minecraft((String)m.get("name")));
                        if (type == null) continue;
                        double base = (double)m.get("base");
                        data.attributes.add(new EntityData.BaseAttribute(type, base));
                    }
                }

            }
            if (section.contains(EntityData.dropChancesStr)){
                List<Double> dcList = section.getDoubleList(EntityData.dropChancesStr);
                if (!dcList.isEmpty()){
                    data.drop_chances = new ArrayList<>();
                    for (double d : dcList){
                        data.drop_chances.add((float)d);
                    }
                }
            }
            if (section.contains(EntityData.equipmentStr)){
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
            }
            if (section.contains(EntityData.canPickUpLootStr)) data.can_pick_up_loot = section.getBoolean(EntityData.canPickUpLootStr);
            if (section.contains(EntityData.leftHandedStr)) data.left_handed = section.getBoolean(EntityData.leftHandedStr);
            if (section.contains(EntityData.noAIStr)) data.no_ai = section.getBoolean("no_ai");
            if (section.contains(EntityData.persistenceRequiredStr)) data.persistence_required = section.getBoolean("persistence_required");
            if (section.contains(EntityData.deathLootTableStr)) data.death_loot_table = section.getString("death_loot_table");
            if (section.contains(EntityData.healthStr)) data.health = (float)section.getDouble("health");

            deSerializedData.put(key, data);
        }
        entityDataMap = deSerializedData;
    }
    public static void entityDataSerialize(){
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<String, EntityData> e : entityDataMap.entrySet()){
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
            if (value.passengers != null && !value.passengers.isEmpty()) section.set("passengers", value.passengers);
            if (value.absorption_amount != null) section.set("absorption_amount", value.absorption_amount);

            if (value.active_effects != null && !value.active_effects.isEmpty()){
                ConfigurationSection effectsection = section.createSection("active_effects");
                for (PotionEffect p : value.active_effects){
                    if (p == null) continue;
                    effectsection.set("name", p.getType().getKey().getKey());
                    effectsection.set("duration", p.getDuration());
                    effectsection.set("amplifier", p.getAmplifier());
                }
            }
            if (value.attributes != null && !value.attributes.isEmpty()){
                ConfigurationSection attributeSection = section.createSection("attributes");
                for (EntityData.BaseAttribute b : value.attributes){
                    if (b == null) continue;
                    attributeSection.set("name", b.attribute.getKey().getKey());
                    attributeSection.set("base", b.base);
                }
            }
            if (value.drop_chances != null && !value.drop_chances.isEmpty()){
                section.set("drop_chances", value.drop_chances);
            }
            if (value.equipment != null){
                ConfigurationSection equipmentSection = section.createSection("equipment");
                if (value.equipment.head != null) equipmentSection.set("head", value.equipment.head);
                if (value.equipment.chest != null) equipmentSection.set("chest", value.equipment.chest);
                if (value.equipment.legs != null) equipmentSection.set("legs", value.equipment.legs);
                if (value.equipment.feet != null) equipmentSection.set("feet", value.equipment.feet);
                if (value.equipment.mainhand != null) equipmentSection.set("mainhand", value.equipment.mainhand);
                if (value.equipment.offhand != null) equipmentSection.set("offhand", value.equipment.offhand);
            }

            if (value.can_pick_up_loot != null) section.set("can_pick_up_loot", value.can_pick_up_loot);
            if (value.left_handed != null) section.set("left_handed", value.left_handed);
            if (value.no_ai != null) section.set("no_ai", value.no_ai);
            if (value.persistence_required != null) section.set("persistence_required", value.persistence_required);
            if (value.death_loot_table != null) section.set("death_loot_table", value.death_loot_table);
            if (value.health != null) section.set("health", value.health);

        }
        entityDataYml = config;
    }
    public static void randomDataDeserialize(){
        Map<String, RandomTable> deSerializedData = new HashMap<>();

        for (String key : randomTableYml.getKeys(false)){
            ConfigurationSection section = randomTableYml.getConfigurationSection(key);
            RandomTable table = new RandomTable();
            if (section == null) continue;
            if (section.contains("groups")){
                List<Map<?,?>> groups = section.getMapList("groups");
                if (groups.isEmpty()) continue;
                table.groups = new ArrayList<>();
                for (Map<?,?> g : groups){
                    RandomGroup group = new RandomGroup();
                    group.weight = (int)g.get("weight");
                    group.message = (String)g.get("message");
                    List<?> items = (List<?>)g.get("items");
                    if (items != null && !items.isEmpty()){
                        group.stacks = new ArrayList<>();
                        for (Object obj : items) {
                            if (obj instanceof ItemStack item) {
                                group.stacks.add(item);
                            }
                        }
                    }
                    table.groups.add(group);
                }
                table.CalcSum();
            }
            deSerializedData.put(key, table);
        }
        randomTableMap = deSerializedData;
    }

    public static void randomTableSerialize(){
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<String, RandomTable> e : randomTableMap.entrySet()){
            String key = e.getKey();
            RandomTable value = e.getValue();
            ConfigurationSection section = config.createSection(key);
            List<RandomGroup> groups = value.groups;
            if (groups != null && !groups.isEmpty()){
                List<Map<String, Object>> groupList = new ArrayList<>();
                for (RandomGroup group : groups){
                    Map<String, Object> groupMap = new HashMap<>();
                    groupMap.put("weight", group.weight);
                    String message = group.message;
                    if (message != null){
                        groupMap.put("message", message);
                    }
                    List<ItemStack> stacks = group.stacks;
                    if (stacks != null && !stacks.isEmpty()){
                        groupMap.put("items", stacks);
                    }
                    groupList.add(groupMap);
                }
                section.set("groups", groupList);
            }
        }
        randomTableYml = config;
    }

    public static void itemSyncDeserialize(){
        Map<String, ItemSyncData> deSerializedData = new HashMap<>();
        Map<String, String> deSerializedIdData = new HashMap<>();

        for (String key : itemSyncYml.getKeys(false)){
            ConfigurationSection section = itemSyncYml.getConfigurationSection(key);
            ItemSyncData table = new ItemSyncData();
            if (section == null) continue;
            table.version = section.getInt("version");
            table.manageCode = section.getString("manage_code");
            table.item = section.getItemStack("item");
            table.asyncEnchant = section.getBoolean("async_enchant");
            deSerializedData.put(key, table);
            deSerializedIdData.put(section.getString("manage_code"), key);
        }
        itemSyncMap = deSerializedData;
        itemSyncIdMap = deSerializedIdData;
    }

    public static void itemSyncSerialize(){
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<String, ItemSyncData> e : itemSyncMap.entrySet()){
            String key = e.getKey();
            ItemSyncData value = e.getValue();
            ConfigurationSection section = config.createSection(key);
            section.set("version", value.version);
            section.set("manage_code", value.manageCode);
            section.set("item", value.item);
            section.set("async_enchant", value.asyncEnchant);
        }
        itemSyncYml = config;
    }

}
