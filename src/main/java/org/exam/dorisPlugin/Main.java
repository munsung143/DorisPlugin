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
import java.util.Map;


public final class Main extends JavaPlugin {
    public static Plugin plugin;

    private File entityDataYmlFile;
    private YamlConfiguration entityDataYaml;
    private Map<String, EntityData> entityDataMap;

    private YamlConfiguration messageYaml;


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

        InputStream stream = getResource("messages.yml");
        if (stream == null) {
            throw new IllegalStateException("리소스를 찾을 수 없습니다");
        }
        messageYaml = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
        createDataFile();
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

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("도")) {
            if (!sender.isOp()) return false;
            ItemSettingCommandManager manager = new ItemSettingCommandManager(sender, args, messageYaml);
            manager.Start();
            return true;
        }
        else if (cmd.getName().equalsIgnoreCase("bos")) {
            if (!sender.isOp()) return false;
            EntitySettingCommandManager manager = new EntitySettingCommandManager(sender, args, entityDataMap, entityDataYmlFile);
            manager.Start();
            return true;
        }
        return  false;
    }


}
