package org.exam.dorisPlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.units.qual.C;
import org.exam.dorisPlugin.Events.RandomItemConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemSyncCommandExecutor implements CommandExecutor {

    public Map<String, ItemSyncData> itemSyncMap;
    public Map<String, String> itemSyncIdMap;

    public ItemSyncCommandExecutor(){
        this.itemSyncMap = DataSerializer.itemSyncMap;
        this.itemSyncIdMap = DataSerializer.itemSyncIdMap;
    }
    private void senderMessage(CommandSender sender, String message){
        sender.sendMessage(message);
    }
    private void senderMessage(CommandSender sender, Component message){
        sender.sendMessage(message);
    }
    private void Save(){
        DataSerializer.itemSyncSerialize();
        DataSerializer.SaveItemSyncData();
    }
    private boolean CheckArgsLength(CommandSender sender, String[] args, int index, String message){
        if (args.length <= index){
            yamlMessage(sender, message);
            return true;
        }
        return false;
    }
    private void yamlMessage(CommandSender sender,String message){
        Main.sendMessage(message, sender);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!sender.isOp()) return false;
        if (CheckArgsLength(sender, args, 0, "messages.is.usage")) return true;
        switch (args[0]){
            case "list": list(sender); break;
            case "get" : get(sender, args, 1); break;
            case "create" : create(sender, args, 2); break;
            case "remove" : remove(sender, args, 1); break;
            case "info" : check(sender, args); break;
            case "enchantasync" : EnchantAsync(sender, args, 2); break;
            default: yamlMessage(sender,"messages.is.usage");
        }
        return true;
    }
    private void list(CommandSender s){
        senderMessage(s, "");
        senderMessage(s, "§b[동기화 아이템 목록]");
        senderMessage(s, "-----");
        for (var value : itemSyncIdMap.entrySet()){
            ItemSyncData data = itemSyncMap.get(value.getValue());
            s.sendMessage(Component.text(value.getKey(), TextColor.fromHexString("#00ff00"))
                    .append(Component.text("§f - "))
                    .append(data.item.effectiveName())
                    .append(Component.text("§7 - "))
                    .append(Component.text(data.item.getType().toString(), TextColor.fromHexString("#555555"))));
        }
        senderMessage(s, "-----");
        senderMessage(s, "");
    }
    private void create(CommandSender s, String[] args, int reqIndex){
        if (args.length <= reqIndex) {
            yamlMessage(s,"messages.is.usage");
            return;
        }
        String id = args[reqIndex - 1];
        Material m = Registries.Material.get(NamespacedKey.minecraft(args[reqIndex]));
        if (m == null) {
            s.sendMessage("§c정확한 아이템명을 입력");
            return;
        }
        if (itemSyncIdMap.containsKey(id)) {
            s.sendMessage("§c이미 존재하는 동기화 아이템입니다");
            return;
        }
        String code = PluginUtil.GetRandomID();
        ItemSyncData data = new ItemSyncData();
        data.version = 0;
        data.manageCode = id;
        data.item = new ItemStack(m,1);
        itemSyncIdMap.put(id, code);
        itemSyncMap.put(code, data);
        Save();
        senderMessage(s, "§a새로운 동기화 아이템이 추가되었습니다.");
    }
    private void get(CommandSender s, String[] args, int reqIndex){
        if (!(s instanceof Player player)) {
            s.sendMessage("§c플레이어만 실행 가능한 명령어");
            return;
        }
        if (args.length <= reqIndex) {
            yamlMessage(s, "messages.is.usage");
            return;
        }
        String id = args[reqIndex];
        if (!itemSyncIdMap.containsKey(id)) {
            s.sendMessage("§c동기화 아이템이 아닙니다");
            return;
        }
        String code = itemSyncIdMap.get(id);
        ItemSyncData data = itemSyncMap.get(code);
        ItemStack item = data.item.clone();
        item.editMeta(meta -> {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            // 빈 container를 전달하여 빈 공간 생성
            container.set(DorisKeys.sync, PersistentDataType.TAG_CONTAINER, container);
            PersistentDataContainer loot = container.get(DorisKeys.sync, PersistentDataType.TAG_CONTAINER);
            loot.set(DorisKeys.sync_code, PersistentDataType.STRING, code);
            loot.set(DorisKeys.sync_version, PersistentDataType.INTEGER, data.version);
            container.set(DorisKeys.sync, PersistentDataType.TAG_CONTAINER, loot);
        });
        player.give(item);
        player.sendMessage("§a동기화 아이템이 지급되었습니다.");
    }
    private void remove(CommandSender s, String[] args, int reqIndex){
        if (args.length <= reqIndex) {
            yamlMessage(s, "messages.is.usage");
            return;
        }
        String id = args[reqIndex];
        if (!itemSyncIdMap.containsKey(id)) {
            s.sendMessage("§c해당 아이템이 존재하지 않습니다");
            return;
        }
        String code = itemSyncIdMap.get(id);
        itemSyncIdMap.remove(id);
        itemSyncMap.remove(code);
        Save();
        s.sendMessage("§a동기화 아이템이 제거되었습니다.");
    }
    private void check(CommandSender s, String[] args){
        ItemSyncData data;
        String code;
        if (args.length <= 1){
            if (!(s instanceof Player player)) {
                s.sendMessage("§c플레이어만 손에 든 아이템 확인 가능");
                return;
            }
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType().isAir()) {
                s.sendMessage("§c아이템을 들어주세요");
                return;
            }
            PersistentDataContainer cont = item.getItemMeta()
                    .getPersistentDataContainer()
                    .get(DorisKeys.sync, PersistentDataType.TAG_CONTAINER);
            if (cont == null) {
                s.sendMessage("§c동기화 아이템이 아닙니다");
                return;
            }
            code = cont.get(DorisKeys.sync_code, PersistentDataType.STRING);
            if (!itemSyncMap.containsKey(code)){
                s.sendMessage("§c존재하지 않는 동기화 아이템");
                return;
            }
            data = itemSyncMap.get(code);
        }
        else{
            String id = args[1];
            if (!itemSyncIdMap.containsKey(id)) {
                s.sendMessage("§c해당하는 아이템이 없음");
                return;
            }
            code = itemSyncIdMap.get(id);
            data = itemSyncMap.get(code);
        }
        senderMessage(s, "");
        senderMessage(s, "§a아이디 : " + data.manageCode);
        s.sendMessage(Component.text("§e이름 : ")
                .append(data.item.effectiveName()));
        senderMessage(s, "§e아이템 : §f" + data.item.getType());
        senderMessage(s, "§e인첸트 비동기화 : §f" + data.asyncEnchant);
        senderMessage(s, "§7버전 " + data.version);
        senderMessage(s, "§7내부 코드 : " + code);
        senderMessage(s, "");
    }

    private void EnchantAsync(CommandSender s, String[] args, int req){
        if (args.length <= req) {
            yamlMessage(s, "messages.is.usage");
            return;
        }
        String id = args[req - 1];
        if (!itemSyncIdMap.containsKey(id)) {
            s.sendMessage("§c해당 아이템이 존재하지 않습니다");
            return;
        }
        String code = itemSyncIdMap.get(id);
        boolean b = PluginUtil.parseBool2(args[req], false);
        ItemSyncData data = itemSyncMap.get(code);
        data.asyncEnchant = b;
        if (b){
            data.item.removeEnchantments();
        }
        Save();
        s.sendMessage("§a인첸트 비동기화 여부 설정 : " + Boolean.toString(b));
    }

}
