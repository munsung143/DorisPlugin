package org.exam.dorisPlugin;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;

public final class EntityDataSerializer {

    public static Map<String, EntityData> Deserialize(YamlConfiguration snapshot){
        RegistryAccess RegiAccess = RegistryAccess.registryAccess();
        Map<String, EntityData> UnserializedData = new HashMap<>();

        for (String key : snapshot.getKeys(false)){
            ConfigurationSection section = snapshot.getConfigurationSection(key);
            EntityData data = new EntityData();
            String typeStr = section.getString("type");
            if (typeStr != null){
                data.type = RegiAccess.getRegistry(RegistryKey.ENTITY_TYPE).get(NamespacedKey.minecraft(typeStr));
            }
            data.custom_name = section.getString("custom_name");

            data.custom_name_visible = section.getBoolean("custom_name_visible");

            UnserializedData.put(key, data);
        }
        return UnserializedData;
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
        }
        return config;
    }

}
