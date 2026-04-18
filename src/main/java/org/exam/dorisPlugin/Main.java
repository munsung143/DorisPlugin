package org.exam.dorisPlugin;


import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.exam.dorisPlugin.Events.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


public final class Main extends JavaPlugin {
    public static Plugin plugin;
    private static YamlConfiguration messageYaml;

    @Override
    public void onEnable() {
        plugin = this;
        Debug.init(getLogger());
        Debug.log("doris plugin loaded");
        Server server = getServer();
        PluginManager manager = server.getPluginManager();

        InputStream stream = getResource("messages.yml");
        if (stream == null) throw new IllegalStateException("리소스를 찾을 수 없습니다");
        messageYaml = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));

        createDataFile();
        RegisterEvents(manager);
        SetCommands();
    }

    private void createDataFile() {
        // plugins/MyPlugin/
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        DataSerializer.LoadFile(getDataFolder());
    }

    private void RegisterEvents(PluginManager manager){
        manager.registerEvents(new PotionPassiveEffector(), this);
        manager.registerEvents(new PotionAttackEffector(), this);
        manager.registerEvents(new RandomTeleport(), this);
        manager.registerEvents(new FunctionalBlockPreventer(), this);
        manager.registerEvents(new SettingInventoryClose(), this);
        manager.registerEvents(new RandomItemConsumer(), this);
        manager.registerEvents(new ItemSyncer(), this);
    }

    private void SetCommands(){
        getCommand("도").setExecutor(new ItemSettingCommandExecutor());
        getCommand("도").setTabCompleter(new ItemSettingCommandExecutor());
        //getCommand("bos").setExecutor(new EntitySettingCommandExecutor());
        //getCommand("bos").setTabCompleter(new EntitySettingCommandExecutor(null));
        getCommand("random").setExecutor(new RandomCommandManager());
        getCommand("random").setTabCompleter(new RandomCommandTabCompleter());
        getCommand("랜덤추첨").setExecutor(new RandomGiveCommandExecutor());
        getCommand("is").setExecutor(new ItemSyncCommandExecutor());
        getCommand("is").setTabCompleter(new ItemSyncCommandTabCompleter());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Doris plugin unloaded");
    }

    public static void sendMessage(String path, CommandSender sender){
        List<String> str = messageYaml.getStringList(path);
        for (String line : str) {
            sender.sendMessage(line);
        }
    }


}
