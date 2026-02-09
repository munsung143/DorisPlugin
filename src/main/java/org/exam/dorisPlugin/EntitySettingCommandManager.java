package org.exam.dorisPlugin;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class EntitySettingCommandManager {

    private String[] args;
    private Player sender;
    private RegistryAccess RegiAccess = RegistryAccess.registryAccess();
    private Map<String , EntityData> entityDataMap;
    private File file;

    public EntitySettingCommandManager(CommandSender sender, String[] args, Map<String, EntityData> entityData, File file){
        this.args = args;
        this.entityDataMap = entityData;
        this.sender = (Player)sender;
        this.file = file;
    }

    public void Start(){
        if (args.length < 1) return;
        switch (args[0]){
            case "create": CreateNewEntity(); break;
            case "delete": DeleteEntity(); break;
            case "list": ShowEntityList(); break;
            case "spawn": SpawnEntity(); break;
            case "tag" : SetEntityTags(); break;
            case "save": Save(); break;
            default: break;
        }
    }
    private void SetEntityTags(){
        if (args.length < 2) return;
        switch (args[1]){
            case "name": SetEntityCustomName(); break;
            case "namevisible": SetEntityCustomNameVisible(); break;
            default: break;
        }
    }
    private void CreateNewEntity(){
        if (args.length < 3) return;
        String key = args[1];
        if (entityDataMap.containsKey(key)){
            sender.sendMessage("이미 해당 몬스터가 존재함.");
            return;
        }
        EntityType type = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENTITY_TYPE).get(NamespacedKey.minecraft(args[2]));
        if (type == null){
            sender.sendMessage("엔티티 타입을 불러오지 못함.");
            return;
        }
        EntityData data = new EntityData();
        data.type = type;
        entityDataMap.put(key, data);
        sender.sendMessage("몬스터 추가 됨");
    }
    private void DeleteEntity(){
        if (args.length < 2) return;
        String key = args[1];
        if (!entityDataMap.containsKey(key)){
            sender.sendMessage("해당 몬스터가 존재하지 않음");
            return;
        }
        entityDataMap.remove(key);
        sender.sendMessage("몬스터 제거 됨");
    }
    private void ShowEntityList(){
        for (Map.Entry<String, EntityData> e : entityDataMap.entrySet()){
            sender.sendMessage(e.getKey());
        }
    }
    private void SpawnEntity(){
        if (args.length < 2) return;
        String key = args[1];
        if (!entityDataMap.containsKey(key)){
            sender.sendMessage("해당 몬스터가 존재하지 않음");
            return;
        }
        World world = sender.getWorld();
        Location loc = sender.getLocation();
        EntityData data = entityDataMap.get(key);
        var clazz = data.type.getEntityClass();
        if (clazz == null){
            sender.sendMessage("예상 못한 오류");
            return;
        }
        Entity entity = world.spawn(loc, clazz);
        if (data.custom_name != null){
            entity.customName(new TextFormatBuilder(data.custom_name).Build());
        }
        entity.setCustomNameVisible(data.custom_name_visible);
    }
    private void Save(){
        YamlConfiguration config =  EntityDataSerializer.Serialize(entityDataMap);
        try {
            config.save(file);
            sender.sendMessage("저장 완료");
        } catch (IOException e){
            sender.sendMessage("저장 실패");
        }

    }
    private void SetEntityCustomName(){
        if (args.length < 4) return;
        String key = args[2];
        if (!entityDataMap.containsKey(key)){
            sender.sendMessage("해당 몬스터가 존재하지 않음");
            return;
        }
        entityDataMap.get(key).custom_name = PluginUtil.CombineRestArgstoString(args, 3);
        sender.sendMessage("몬스터 이름 설정");


    }
    private void SetEntityCustomNameVisible(){
        if (args.length < 4) return;
        String key = args[2];
        if (!entityDataMap.containsKey(key)){
            sender.sendMessage("해당 몬스터가 존재하지 않음");
            return;
        }
        Boolean value = PluginUtil.parseBool(args[3]);
        if (value == null){
            sender.sendMessage("올바른 값 입력");
            return;
        }
        entityDataMap.get(key).custom_name_visible = value;
        sender.sendMessage("몬스터 이름 보여지는 여부 설정");

    }

}
