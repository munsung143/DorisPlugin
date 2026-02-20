package org.exam.dorisPlugin;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.exam.dorisPlugin.enums.FunctionalBlockType;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class RandomCommandManager implements CommandExecutor{

    private Map<String , RandomTable> randomTableMap;
    private String[] args;
    private Player sender;
    private Runnable saveFunc;

    public RandomCommandManager(Map<String, RandomTable> random, Runnable saveFunc){
        this.randomTableMap = random;
        this.saveFunc = saveFunc;
    }
    private void yamlMessage(String message){
        Main.sendMessage(message, sender);
    }
    private void senderMessage(String message){
        sender.sendMessage(message);
    }
    private void senderMessage(Component message){
        sender.sendMessage(message);
    }
    private boolean argsNotContainIndex(int index){
        return args.length <= index;
    }
    private boolean mapContainsKey(String key){
        return randomTableMap.containsKey(key);
    }
    private RandomTable getTable(String key){
        return randomTableMap.get(key);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!sender.isOp()) return false;
        this.args = args;
        this.sender = (Player) sender;

        if (argsNotContainIndex(0)){
            yamlMessage("messages.random.usage");
            return true;
        }
        switch (args[0]){
            case "목록": listTable(); break;
            case "추가": addTable(); break;
            case "제거": removeTable(); break;
            case "적용": apply(); break;
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
        saveFunc.run();
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
        saveFunc.run();
        yamlMessage("messages.random.remove_table");
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
            listGroup(); return;
        }
        switch (args[2]){
            case "추가": addGroup(); break;
            case "아이템": setGroupItems(); break;
            case "제거": removeGroup(); break;
            case "가중치": setGroupWeight(); break;
            case "메시지": setGroupMessage(); break;
            default: yamlMessage("messages.random.usage"); break;
        }
    }
    private void apply(){
        if (argsNotContainIndex(1)){
            yamlMessage("messages.random.need_table_name"); return;
        }
        String key = args[1];
        if (!mapContainsKey(key)){
            yamlMessage("messages.random.table_not_exist"); return;
        }
        ItemStack handItem = sender.getInventory().getItemInMainHand();
        if (handItem.getType().isAir()) {
            senderMessage("손에 아이템들 들어야 함"); return;
        }
        NamespacedKey namespacedKey = NamespacedKey.fromString(RandomItemConsumer.keyString, Main.plugin);
        if (namespacedKey == null){
            sender.sendMessage("undefined error"); return;
        }
        ItemMeta meta = handItem.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(namespacedKey, PersistentDataType.STRING, key);
        handItem.setItemMeta(meta);
        senderMessage("손에 든 아이템에 랜덤 테이블을 적용함");
    }

    private void listGroup(){
        senderMessage("해당 테이블의 그룹 목록");
        senderMessage("-----");
        RandomTable table = getTable(args[1]);
        senderMessage("가중치 합 : " + table.weightSum);
        var groups = table.groups;
        if (groups == null){
            return;
        }
        for (int i = 0; i < groups.size(); i++){
            RandomGroup group = groups.get(i);
            senderMessage(i + " - " + "가중치: " + group.weight);
            if (group.message != null){
                senderMessage(new TextFormatBuilder("메시지: " + group.message).Build());
            }
        }

    }

    private void addGroup(){
        RandomTable table = getTable(args[1]);
        if (table.groups == null){
            table.groups = new ArrayList<>();
        }
        RandomGroup group = new RandomGroup();
        if (!argsNotContainIndex(3)){
            Integer weight = PluginUtil.parseInt(args[3], 1, Integer.MAX_VALUE);
            if (weight != null){
                group.weight = weight;
            }
        }
        Component title = Component.text(table.groups.size() + "번 그룹 내 아이템 목록, 가중치: " + group.weight);
        Inventory inv = Bukkit.createInventory(new RandomGroupItemsHolder(group), 9, title);
        sender.openInventory(inv);
        if (inv.getHolder() instanceof RandomGroupItemsHolder holder){
            holder.setItems(inv);
        }
        table.groups.add(group);
        table.CalcSum();
        saveFunc.run();
        senderMessage("테이블에 그룹을 추가했습니다.");
    }

    private void removeGroup(){
        if (argsNotContainIndex(3)){
            senderMessage("인자가 부족합니다"); return;
        }
        RandomTable table = getTable(args[1]);
        var groups = table.groups;
        if (groups == null){
            senderMessage("해당 테이블에 그룹이 존재하지 않습니다."); return;
        }
        Integer index = PluginUtil.parseInt(args[3], 0, groups.size() - 1);
        if (index == null){
            senderMessage("범위를 벗어나거나 올바르지 않은 인덱스"); return;
        }
        groups.remove(index.intValue());
        table.CalcSum();
        saveFunc.run();
        senderMessage("테이블에서 그룹을 제거했습니다.");
    }

    private void setGroupWeight(){
        if (argsNotContainIndex(4)){
            senderMessage("인자가 부족합니다"); return;
        }
        RandomTable table = getTable(args[1]);
        var groups = table.groups;
        if (groups == null){
            senderMessage("해당 테이블에 그룹이 존재하지 않습니다."); return;
        }
        Integer index = PluginUtil.parseInt(args[3], 0, groups.size() - 1);
        if (index == null){
            senderMessage("범위를 벗어나거나 올바르지 않은 인덱스"); return;
        }
        Integer weight = PluginUtil.parseInt(args[4], 1, Integer.MAX_VALUE);
        if (weight == null){
            senderMessage("1 이상의 정수를 입력하세요"); return;
        }
        groups.get(index).weight = weight;
        table.CalcSum();
        saveFunc.run();
        senderMessage("해당 그룹의 가중치를 수정했습니다");
    }
    private void setGroupMessage(){
        if (argsNotContainIndex(4)){
            senderMessage("인자가 부족합니다"); return;
        }
        RandomTable table = getTable(args[1]);
        var groups = table.groups;
        if (groups == null){
            senderMessage("해당 테이블에 그룹이 존재하지 않습니다."); return;
        }
        Integer index = PluginUtil.parseInt(args[3], 0, groups.size() - 1);
        if (index == null){
            senderMessage("범위를 벗어나거나 올바르지 않은 인덱스"); return;
        }
        groups.get(index).message = PluginUtil.CombineRestArgstoString(args, 4);
        saveFunc.run();
        senderMessage("해당 그룹의 메시지를 수정했습니다");
    }
    private void setGroupItems(){
        if (argsNotContainIndex(3)){
            senderMessage("인자가 부족합니다"); return;
        }
        RandomTable table = getTable(args[1]);
        var groups = table.groups;
        if (groups == null){
            senderMessage("해당 테이블에 그룹이 존재하지 않습니다."); return;
        }
        Integer index = PluginUtil.parseInt(args[3], 0, groups.size() - 1);
        if (index == null){
            senderMessage("범위를 벗어나거나 올바르지 않은 인덱스"); return;
        }
        RandomGroup group = groups.get(index);
        Component title = Component.text(index + "번 그룹 내 아이템 목록, 가중치: " + group.weight);
        Inventory inv = Bukkit.createInventory(new RandomGroupItemsHolder(group), 9, title);
        sender.openInventory(inv);
        if (inv.getHolder() instanceof RandomGroupItemsHolder holder){
            holder.setItems(inv);
        }


    }

}
