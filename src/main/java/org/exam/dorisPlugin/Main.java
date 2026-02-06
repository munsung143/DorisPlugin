package org.exam.dorisPlugin;


import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.io.InputStreamReader;


public final class Main extends JavaPlugin {

    public YamlConfiguration message;
    public static Plugin plugin;


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

        InputStream stream = getResource("messages.yml");
        if (stream == null) {
            throw new IllegalStateException("리소스를 찾을 수 없습니다");
        }
        message = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Test plugin unloaded");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("도")) {
            if (!sender.isOp()) return false;
            ItemSettingCommandManager manager = new ItemSettingCommandManager(sender, args, message);
            manager.Start();
            return true;
        }
        return  false;
    }


}
