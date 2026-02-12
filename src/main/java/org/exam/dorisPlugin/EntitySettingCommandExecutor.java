package org.exam.dorisPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntitySettingCommandExecutor  implements CommandExecutor, TabCompleter {
    private EntitySettingCommandManager manager;
    private Map<String , EntityData> entityDataMap;
    private String[] args;
    private CommandSender sender;

    public EntitySettingCommandExecutor(Map<String, EntityData> entityDataMap){
        this.entityDataMap = entityDataMap;
    }
    private boolean CheckArgsLength(int length, String message){
        if (args.length < length){
            sendMessage(message);
            return true;
        }
        return false;
    }
    private void sendMessage(String message){
        Main.sendMessage(message, sender);
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!sender.isOp()) return false;
        this.args = args;
        this.sender = sender;
        if (CheckArgsLength(1, "messages.bos.usage")) return true;
        manager = new EntitySettingCommandManager(sender,entityDataMap, args);
        switch (args[0]){
            case "create": manager.CreateNewEntity(2); break;
            case "delete": manager.DeleteEntity(2); break;
            case "list": manager.ShowEntityList(); break;
            case "spawn": manager.SpawnEntity(2); break;
            case "tag" : SetEntityTags(); break;
            case "save": manager.Save(); break;
            default: break;
        }
        return true;
    }
    private void SetEntityTags(){
        if (args.length < 2) return;
        String s = "aa";
        switch (args[1]){
            case "name": manager.SetEntityCustomName(3); break;
            case "namevisible": manager.SetEntityCustomNameVisible(3); break;
            default: break;
        }
    }
}
