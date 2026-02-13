package org.exam.dorisPlugin;


import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;


public final class Main extends JavaPlugin {
    public static Plugin plugin;
    private static YamlConfiguration messageYaml;
    private static File entityDataYmlFile;
    private YamlConfiguration entityDataYaml;
    private Map<String, EntityData> entityDataMap;


    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        Debug.init(getLogger());
        Debug.log("doris plugin loaded");
        Server server = getServer();
        PluginManager manager = server.getPluginManager();
        manager.registerEvents(new PotionPassiveEffector(), this);
        manager.registerEvents(new PotionAttackEffector(), this);
        manager.registerEvents(new RandomTeleport(), this);
        manager.registerEvents(new FunctionalBlockPreventer(), this);
        manager.registerEvents(new EntityEquipmentSettingInventoryClose(), this);

        InputStream stream = getResource("messages.yml");
        if (stream == null) {
            throw new IllegalStateException("리소스를 찾을 수 없습니다");
        }
        messageYaml = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
        createDataFile();
        getCommand("도").setExecutor(new ItemSettingCommandExecutor());
        getCommand("도").setTabCompleter(new ItemSettingCommandExecutor());
        getCommand("bos").setExecutor(new EntitySettingCommandExecutor(entityDataMap));
        getCommand("bos").setTabCompleter(new EntitySettingCommandExecutor(null));
    }

    private void createDataFile() {
        // plugins/MyPlugin/
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        entityDataYmlFile = new File(getDataFolder(), "EntityData.yml");
        if (!entityDataYmlFile.exists()) {
            try {
                entityDataYmlFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        entityDataYaml = YamlConfiguration.loadConfiguration(entityDataYmlFile);
        entityDataMap = EntityDataSerializer.Deserialize(entityDataYaml);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Test plugin unloaded");
    }
    public static void sendMessage(String path, CommandSender sender){
        List<String> str = messageYaml.getStringList(path);
        for (String line : str) {
            sender.sendMessage(line);
        }
    }
    public static boolean SaveEntityData(YamlConfiguration config){
        try {
            config.save(entityDataYmlFile);
            return true;
        } catch (IOException e){
            return false;
        }
    }


}
