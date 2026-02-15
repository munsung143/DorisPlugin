package org.exam.dorisPlugin;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class EntitySettingCommandManager {

    private String[] args;
    private Player sender;
    private RegistryAccess RegiAccess = RegistryAccess.registryAccess();
    private Map<String , EntityData> entityDataMap;

    public EntitySettingCommandManager(CommandSender sender, Map<String, EntityData> entityData, String[] args){
        this.args = args;
        this.entityDataMap = entityData;
        this.sender = (Player)sender;
    }
    private String returnKey(String arg){
        if (entityDataMap.containsKey(arg)){
            return arg;
        }
        else {
            sendMessage("해당 몬스터 존재하지 않음");
            return null;
        }
    }
    private void sendMessage(String message){
        sender.sendMessage(message);
    }
    private boolean isArgsLengthLessThenWithMessage(int len){
        if (args.length < len){
            sendMessage("명령어에 필요한 인자가 부족함");
            return true;
        }
        return false;
    }
    private boolean isArgsLengthLessThen(int len){
        return args.length < len;
    }
    private boolean isArgsLengthEquals(int len){
        return args.length == len;
    }
    private EntityData getEntityData(String key){
        return entityDataMap.get(returnKey(key));
    }
    public Boolean parseBool(String str){
        Boolean b = PluginUtil.parseBool(str);
        if (b == null) {
            sendMessage("올바른 true/false 입력이 아님");
        }
        return b;
    }

    public void CreateNewEntity(int codeLength, int requiredLength){
        if (isArgsLengthLessThenWithMessage(requiredLength)) return;
        String key = args[codeLength - 1];
        if (entityDataMap.containsKey(key)){
            sendMessage("이미 해당 몬스터가 존재함.");
            return;
        }
        EntityType type = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENTITY_TYPE).get(NamespacedKey.minecraft(args[codeLength]));
        if (type == null){
            sender.sendMessage("엔티티 타입을 불러오지 못함.");
            return;
        }
        EntityData data = new EntityData();
        data.type = type;
        entityDataMap.put(key, data);
        sender.sendMessage("몬스터 추가 됨");
    }
    public void DeleteEntity(int codeLength){
        if (isArgsLengthLessThenWithMessage(codeLength)) return;
        String key = returnKey(args[codeLength - 1]);
        if (key == null) return;
        entityDataMap.remove(key);
        sender.sendMessage("몬스터 제거 됨");
    }
    public void ShowEntityList(){
        for (Map.Entry<String, EntityData> e : entityDataMap.entrySet()){
            sender.sendMessage(e.getKey());
        }
    }
    public void SpawnEntity(int codeLength){
        if (isArgsLengthLessThenWithMessage(codeLength)) return;
        EntityData data = getEntityData(args[codeLength - 1]);
        if (data == null) return;
        World world = sender.getWorld();
        Location loc = sender.getLocation();
        var clazz = data.type.getEntityClass();
        if (clazz == null){
            sender.sendMessage("예상 못한 오류");
            return;
        }
        Entity entity = world.spawn(loc, clazz);
        if (data.custom_name != null){
            entity.customName(new TextFormatBuilder(data.custom_name).Build());
        }
        if (data.custom_name_visible != null){
            entity.setCustomNameVisible(data.custom_name_visible);
        }
        if (data.fire != null){
            entity.setFireTicks(data.fire);
        }
        if (data.glowing != null){
            entity.setGlowing(data.glowing);
        }
        if (data.has_visual_fire != null){
            entity.setVisualFire(data.has_visual_fire);
        }
        if (data.invulnerable != null){
            entity.setInvulnerable(data.invulnerable);
        }
        if (data.no_gravity != null){
            entity.setNoPhysics(data.no_gravity);
        }
        if (data.silent != null){
            entity.setSilent(data.silent);
        }
        if (data.persistence_required != null){
            entity.setPersistent(data.persistence_required);
        }
        if (entity instanceof Damageable damageable){
            if (data.absorption_amount != null){
                damageable.setAbsorptionAmount(data.absorption_amount);
            }
            if (data.health != null){
                damageable.setHealth(data.health);
            }
        }
        if (entity instanceof LivingEntity livingEntity){
            if (data.can_pick_up_loot != null){
                livingEntity.setCanPickupItems(data.can_pick_up_loot);
            }
            if (data.no_ai != null){
                livingEntity.setAI(data.no_ai);
            }
        }
        if (entity instanceof Mob mob){
            if (data.left_handed != null){
                mob.setLeftHanded(data.left_handed);
            }
        }
    }
    public void Save(){
        YamlConfiguration config =  EntityDataSerializer.Serialize(entityDataMap);
        if (Main.SaveEntityData(config)){
            sender.sendMessage("저장 성공");
        }
        else{
            sender.sendMessage("저장 실패");
        }

    }
    public <T> void SetValue(
            int codeLength,
            int requiredLength,
            Function<EntityData, T> getter,
            Function<String, T> parser,
            BiConsumer<EntityData, T> setter,
            String nullMessage,
            String clearMessage
            ){
        if (isArgsLengthLessThenWithMessage(codeLength)) return;
        EntityData data = getEntityData(args[codeLength - 1]);
        if (data == null) return;
        if (isArgsLengthEquals(codeLength)){
            T dataValue = getter.apply(data);
            if (dataValue == null){
                sendMessage(nullMessage);
                return;
            }
            sendMessage(String.valueOf(dataValue));
            return;
        }
        if (isArgsLengthLessThenWithMessage(requiredLength)) return;
        T value = parser.apply(args[codeLength]);
        setter.accept(data, value);
        sendMessage(clearMessage);
    }
    public void SetEntityCustomName(int codeLength, int requiredLength){
        if (isArgsLengthLessThenWithMessage(codeLength)) return;
        EntityData data = getEntityData(args[codeLength - 1]);
        if (data == null) return;
        if (isArgsLengthEquals(codeLength)){
            if (data.custom_name == null){
                sendMessage("설정된 이름이 없음");
                return;
            }
            sender.sendMessage(new TextFormatBuilder(data.custom_name).Build());
            return;
        }
        if (isArgsLengthLessThenWithMessage(requiredLength)) return;
        data.custom_name = PluginUtil.CombineRestArgstoString(args, codeLength);
        sendMessage("몬스터 이름 설정");
    }
    public void SetEntityDropChance(int codeLength, int requiredLength){
        if (isArgsLengthLessThenWithMessage(codeLength)) return;
        EntityData data = getEntityData(args[codeLength - 1]);
        if (data == null) return;
        if (isArgsLengthEquals(codeLength)){
            if (data.drop_chances == null){
                sendMessage("설정된 드롭 확률이 없음");
                return;
            }
            sender.sendMessage(String.valueOf(data.drop_chances));
            return;
        }
        if (isArgsLengthLessThenWithMessage(requiredLength)) return;
        Float head = PluginUtil.parseFloat(args[codeLength], 0, 1.0f);
        if (head == null) return;
        Float chest = PluginUtil.parseFloat(args[codeLength + 1], 0, 1.0f);
        if (chest == null) return;
        Float legs = PluginUtil.parseFloat(args[codeLength + 2], 0, 1.0f);
        if (legs == null) return;
        Float feet = PluginUtil.parseFloat(args[codeLength + 3], 0, 1.0f);
        if (feet == null) return;
        Float mainhand = PluginUtil.parseFloat(args[codeLength + 4], 0, 1.0f);
        if (mainhand == null) return;
        Float offhand = PluginUtil.parseFloat(args[codeLength + 5], 0, 1.0f);
        if (offhand == null) return;
        data.drop_chances = new ArrayList<>();
        data.drop_chances.add(head);
        data.drop_chances.add(chest);
        data.drop_chances.add(legs);
        data.drop_chances.add(feet);
        data.drop_chances.add(mainhand);
        data.drop_chances.add(offhand);
        data.custom_name = PluginUtil.CombineRestArgstoString(args, codeLength);
        sendMessage("몬스터 드롭 확률 설정됨");
    }
    public void setEquipment(int codeLength){
        if (isArgsLengthLessThenWithMessage(codeLength)) return;
        EntityData data = getEntityData(args[codeLength - 1]);
        if (data == null) return;
        Inventory inv = Bukkit.createInventory(new EntityEquipmentInventoryHolder(data), 9,Component.empty());
        if (inv.getHolder() instanceof EntityEquipmentInventoryHolder holder){
            holder.setItems(inv);
        }
        sender.openInventory(inv);

    }
    public void setEntityEffects(){

    }
    public void setEntityAttributes(){

    }
    public void addPassenger(){

    }
    public void deletePassenger(){

    }

}
