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
            case "create": manager.CreateNewEntity(2, 3); break;
            case "delete": manager.DeleteEntity(2); break;
            case "list": manager.ShowEntityList(); break;
            case "spawn": manager.SpawnEntity(2); break;
            case "tag" : SetEntityTags(); break;
            case "save": manager.Save(); break;
            default: sendMessage("messages.bos.usage"); break;
        }
        return true;
    }
    private void SetEntityTags() {
        if (args.length < 2) return;
        switch (args[1]) {
            case "name": manager.SetEntityCustomName(3, 4);break;
            case "namevisible": manager.SetValue(
                    3,
                    4,
                    data -> data.custom_name_visible,
                    str -> PluginUtil.parseBool(str),
                    (data, value) -> data.custom_name_visible = value,
                    "이름 상시표시여부 지정 안됨", "이름 상시표시여부 설정 완료");break;
            case "fire": manager.SetValue(
                    3,
                    4,
                    data -> data.fire,
                    str -> PluginUtil.parseShort(str, Short.MIN_VALUE, Short.MAX_VALUE),
                    (data, value) -> data.fire = value,
                    "fire 값 지정 안됨", "fire 값 지정 완료");break;
            case "glowing": manager.SetValue(
                    3,
                    4,
                    data -> data.glowing,
                    str -> PluginUtil.parseBool(str),
                    (data, value) -> data.glowing = value,
                    "glowing 지정 안됨", "glowing 설정 완료");break;
            case "visualfire": manager.SetValue(
                    3,
                    4,
                    data -> data.has_visual_fire,
                    str -> PluginUtil.parseBool(str),
                    (data, value) -> data.has_visual_fire = value,
                    "visual fire 지정 안됨", "visual fire 설정 완료");break;
            case "invulnerable": manager.SetValue(
                    3,
                    4,
                    data -> data.invulnerable,
                    str -> PluginUtil.parseBool(str),
                    (data, value) -> data.invulnerable = value,
                    "무적 여부 지정 안됨", "무적 여부 설정 완료");break;
            case "gravity": manager.SetValue(
                    3,
                    4,
                    data -> data.no_gravity,
                    str -> PluginUtil.parseBool(str),
                    (data, value) -> data.no_gravity = value,
                    "중력 여부 지정 안됨", "중력 여부 설정 완료");break;
            case "silent": manager.SetValue(
                    3,
                    4,
                    data -> data.silent,
                    str -> PluginUtil.parseBool(str),
                    (data, value) -> data.silent = value,
                    "silent 지정 안됨", "silent 설정 완료");break;
            case "absorption": manager.SetValue(
                    3,
                    4,
                    data -> data.silent,
                    str -> PluginUtil.parseBool(str),
                    (data, value) -> data.silent = value,
                    "silent 지정 안됨", "silent 설정 완료");break;
            case "pickup": manager.SetValue(
                    3,
                    4,
                    data -> data.can_pick_up_loot,
                    str -> PluginUtil.parseBool(str),
                    (data, value) -> data.can_pick_up_loot = value,
                    "pickup 지정 안됨", "pickup 설정 완료");break;
            case "health": manager.SetValue(
                    3,
                    4,
                    data -> data.health,
                    str -> PluginUtil.parseFloat(str, 0, Float.MAX_VALUE),
                    (data, value) -> data.health = value,
                    "체력 지정 안됨", "체력 설정 완료");break;
            case "lefthanded": manager.SetValue(
                    3,
                    4,
                    data -> data.left_handed,
                    str -> PluginUtil.parseBool(str),
                    (data, value) -> data.left_handed = value,
                    "왼손 여부 지정 안됨", "왼손 여부 설정 완료");break;

            case "ai": manager.SetValue(
                    3,
                    4,
                    data -> data.no_ai,
                    str -> PluginUtil.parseBool(str),
                    (data, value) -> data.no_ai = value,
                    "no ai 지정 안됨", "no ai 설정 완료");break;

            case "persistence": manager.SetValue(
                    3,
                    4,
                    data -> data.persistence_required,
                    str -> PluginUtil.parseBool(str),
                    (data, value) -> data.persistence_required = value,
                    "persistence 지정 안됨", "persistence 설정 완료");break;
            case "passengers": SetEntityPassengers();break;
            case "dropchance": manager.SetEntityDropChance(3, 9);break;
            case "equipment": manager.setEquipment(3); break;
            case "effects": manager.setEntityEffects();break;
            case "attributes": manager.setEntityAttributes(); break;
            default: sendMessage("messages.bos.usage"); break;
        }
    }

    private void SetEntityPassengers(){
        if (args.length < 3) return;
        switch (args[2]){
            case "add": manager.addPassenger(); break;
            case "del": manager.deletePassenger(); break;
            default: break;
        }
    }
}
