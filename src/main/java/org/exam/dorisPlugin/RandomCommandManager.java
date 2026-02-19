package org.exam.dorisPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class RandomCommandManager implements CommandExecutor{

    private Map<String , RandomTable> randomTableMap;
    private String[] args;
    private CommandSender sender;

    public RandomCommandManager(Map<String, RandomTable> random){
        this.randomTableMap = random;
    }
    private void yamlMessage(String message){
        Main.sendMessage(message, sender);
    }
    private void senderMessage(String message){
        sender.sendMessage(message);
    }
    private boolean argsNotContainIndex(int index){
        return args.length <= index;
    }
    private boolean mapContainsKey(String key){
        return randomTableMap.containsKey(key);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!sender.isOp()) return false;
        this.args = args;
        this.sender = sender;

        if (argsNotContainIndex(0)){
            yamlMessage("messages.random.usage");
            return true;
        }
        switch (args[0]){
            case "목록": listTable(); break;
            case "추가": addTable(); break;
            case "제거": removeTable(); break;
            case "적용": break;
            case "그룹": setGroup(); break;
            default: yamlMessage("messages.random.usage"); break;
        }
        return true;
    }
    private void listTable(){
        senderMessage("테이블 목록");
        senderMessage("-----");
        for (String key : randomTableMap.keySet()){
            senderMessage(key);
        }
    }

    private void addTable(){
        if (argsNotContainIndex(1)){
            yamlMessage("messages.random.need_table_name"); return;
        }
        String key = args[1];
        if (mapContainsKey(key)){
            yamlMessage("messages.random.table_already_exist"); return;
        }
        randomTableMap.put(key, new RandomTable());
        yamlMessage("messages.random.add_table");
    }

    private void removeTable(){
        if (argsNotContainIndex(1)){
            yamlMessage("messages.random.need_table_name"); return;
        }
        String key = args[1];
        if (!mapContainsKey(key)){
            yamlMessage("messages.random.table_not_exist"); return;
        }
        randomTableMap.remove(key);
        yamlMessage("messages.random.add_table");
    }
    private void setGroup(){
        if (argsNotContainIndex(1)){
            yamlMessage("messages.random.need_table_name"); return;
        }
        String key = args[1];
        if (!mapContainsKey(key)){
            yamlMessage("messages.random.table_not_exist"); return;
        }
        if (argsNotContainIndex(2)){
            yamlMessage("messages.random.need_table_name"); return;
        }
        switch (args[2]){
            case "추가": break;
            case "아이템": break;
            case "제거": break;
            case "가중치": break;
            case "메시지": break;
            default: yamlMessage("messages.random.usage"); break;
        }
    }
    private void listGroup(){

    }

    private void addGroup(){

    }

}
