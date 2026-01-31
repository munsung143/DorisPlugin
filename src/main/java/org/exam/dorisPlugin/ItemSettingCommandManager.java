package org.exam.dorisPlugin;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.data.type.Switch;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.components.EquippableComponent;
import org.bukkit.persistence.ListPersistentDataTypeProvider;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemSettingCommandManager {

    private String[] args;
    private CommandSender sender;
    private ItemStack handItem;
    private RegistryAccess RegiAccess = RegistryAccess.registryAccess();
    private YamlConfiguration message;
    private ItemMeta meta;

    public ItemSettingCommandManager(CommandSender sender, String[] args, YamlConfiguration message){
        this.args = args;
        this.sender = sender;
        this.message = message;
        // Player 인터페이스는 CommandSender 인터페이스를 상속
        // 플레이어가 명령어 호출 시 Player 인터페이스를 구현한 클래스가 sender 매개변수로 올 것.
        // 따라서 아래와 같이 캐스팅 가능.
        Player player = (Player)sender;
        PlayerInventory inv = player.getInventory();
        handItem = inv.getItemInMainHand();
    }
    private void sendMessage(String path){
        List<String> str = message.getStringList(path);
        for (String line : str) {
            sender.sendMessage(line);
        }
    }
    private boolean IsHandItemAir(){
        if (handItem.getType().isAir()){
            sender.sendMessage("§c: 손에 아이템을 들어주세요");
            return true;
        }
        return false;
    }
    public String CombineRestArgstoString(int startAt){
        StringBuilder inputBuilder = new StringBuilder();
        for(int i = startAt; i < args.length; i++){
            inputBuilder.append(args[i]);
            if(i < args.length - 1){
                inputBuilder.append(' ');
            }
        }
        return inputBuilder.toString();
    }
    private boolean CheckArgsLength(int length, String message){
        if (args.length < length){
            sendMessage(message);
            return true;
        }
        return false;
    }
    private Integer parseInt(String arg, int min, int max) {
        try {
            int value = Integer.parseInt(arg);
            return value >= min && value <= max ? value : null;
        } catch (Exception e) {
            return null;
        }
    }
    public void Start(){
        meta = handItem.getItemMeta();
        if (CheckArgsLength(1, "messages.do.usage")) return;
        switch (args[0]){
            case "인첸트": SetEnchantment(); break;
            case "이름": SetItemName(); break;
            case "설명": SetItemLore(); break;
            case "속성": SetItemAttribute(); break;
            case "색상": SetItemColor(); break;
            case "스택": SetMaxStack(); break;
            case "착용버프": SetPotionPassive(); break;
            case "착용": SetEquip();break;
            case "모델": SetModel(); break;
            case "내구도": SetDurability(); break;
            case "포션": SetPotion(); break;
            default: sendMessage("messages.do.usage"); break;
        }
    }
    public void SetItemLore(){
        if (CheckArgsLength(2, "messages.do.lore.usage")) return;
        switch (args[1]){
            case "추가": AddLore(); break;
            case "삭제": DeleteLore(); break;
            case "삽입", "변경": InsertLore(); break;
            default: sendMessage("messages.do.lore.usage"); break;
        }
    }
    public void SetItemAttribute(){
        if (CheckArgsLength(2, "messages.do.attribute.usage")) return;
        switch (args[1]){
            case "목록": sendMessage("messages.do.attribute.list"); break;
            case "기본데미지","기본공격속도": SetBaseAttribute(); break;
            case "초기화": ClearAttribute(); break;
            default: SetAttribute(); break;
        }
    }
    public void SetPotionPassive(){
        if (CheckArgsLength(2, "messages.do.potion_passive.usage")) {
            sender.sendMessage(Arrays.toString(EffectType.values())); return;
        }
        switch (args[1]){
            case "추가": SetPotionPassiveAdd(); break;
            case "제거": SetPotionPassiveRemove(); break;
            case "확인": SetPotionPassiveCheck(); break;
            default: sendMessage("messages.do.potion_passive_usage"); break;
        }
    }
    private void SetEquip(){
        if (CheckArgsLength(2, "messages.do.equip.usage")) return;
        switch (args[1]){
            case "부위": SetEquipSlot(); break;
            case "소리": SetEquipSound(); break;
            case "모델": SetEquipModel(); break;
            default: sendMessage("messages.do.equip.usage");
        }
    }
    private void SetDurability(){
        if (CheckArgsLength(2, "messages.do.durability.usage")) return;
        switch (args[1]){
            case "무한": SetUnbreakable(); break;
            case "최대": SetMaxDurability(); break;
            case "감소": SetCurrentDurability(); break;
            default: sendMessage("messages.do.durability.usage"); break;
        }
    }
    public void SetItemName(){
        if (CheckArgsLength(2, "messages.do.name.usage")) return;
        if (IsHandItemAir()) return;
        TextFormatBuilder builder = new TextFormatBuilder(CombineRestArgstoString(1));
        Component cmp = builder.Build();
        meta.customName(cmp);
        handItem.setItemMeta(meta);
    }

    public void AddLore(){
        if (CheckArgsLength(3, "messages.do.lore.usage")) return;
        if (IsHandItemAir()) return;
        TextFormatBuilder builder = new TextFormatBuilder(CombineRestArgstoString(2));
        Component cmp = builder.Build();
        List<Component> existLores = meta.lore();
        if (existLores == null){
            existLores = new ArrayList<>();
        }
        existLores.addLast(cmp);
        meta.lore(existLores);
        handItem.setItemMeta(meta);

    }
    public void DeleteLore(){
        if (IsHandItemAir()) return;
        List<Component> existLores = meta.lore();
        if (existLores == null){
            sender.sendMessage("설명이 없습니다");
            return;
        }
        Integer index = null;
        if (args.length < 3){
            existLores.removeLast();
        }
        else{
            index = parseInt(args[2], 0, existLores.size() - 1);
            if (index == null) return;
            existLores.remove(index.intValue());
        }
        meta.lore(existLores);
        handItem.setItemMeta(meta);

    }
    public void InsertLore(){
        if (CheckArgsLength(4, "messages.do.lore.usage")) return;
        if (IsHandItemAir()) return;
        List<Component> existLores = meta.lore();
        if (existLores == null){
            sender.sendMessage("설명이 없습니다");
            return;
        }
        Integer index = parseInt(args[2], 0, existLores.size() - 1);
        if (index == null) return;
        TextFormatBuilder builder = new TextFormatBuilder(CombineRestArgstoString(3));
        Component cmp = builder.Build();
        existLores.add(index, cmp);
        if (args[1].equalsIgnoreCase("변경")){
            existLores.remove(index + 1);
        }
        meta.lore(existLores);
        handItem.setItemMeta(meta);
    }
    public void SetEnchantment(){
        if (CheckArgsLength(2, "messages.do.enchant.usage")){
            sender.sendMessage(Arrays.toString(EnchantType.values()));
            return;
        }
        if (IsHandItemAir()) return;
        if (!EnchantType.ContainsKey(args[1])){
            sender.sendMessage("잘못된 인첸트");
            return;
        }
        Enchantment enchant = RegiAccess.getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.minecraft(EnchantType.GetValue(args[1])));
        if (enchant == null){
            return;
        }
        Integer level = 1;
        if (args.length > 2){
            level = parseInt(args[2], 0, Integer.MAX_VALUE);
            if (level == null) return;
        }
        handItem.addUnsafeEnchantment(enchant, level);
    }
    private void SetAttribute(){
        if (CheckArgsLength(5, "message.do.attribute.usage")) return;
        if (IsHandItemAir()) return;
        EquipmentSlotGroup slot;
        AttributeModifier.Operation operation;
        double amount = 0;
        if (!AttributeType.ContainsKey(args[1])){
            sender.sendMessage("잘못된 속성");
            return;
        }
        Attribute attribute = RegiAccess.getRegistry(RegistryKey.ATTRIBUTE).get(NamespacedKey.minecraft(AttributeType.GetValue(args[1])));
        if (attribute == null){
            return;
        }
        slot = SlotType.GetSlotGroup(args[2]);
        if (slot == null){
            sender.sendMessage("잘못된 슬롯");
            return;
        }
        switch (args[3]){
            case "더하기":
                operation = AttributeModifier.Operation.ADD_NUMBER;
                break;
            case "곱하기":
                operation = AttributeModifier.Operation.ADD_SCALAR;
                break;
            case "누적곱하기":
                operation = AttributeModifier.Operation.MULTIPLY_SCALAR_1;
                break;
            default:
                return;
        }
        try {
            amount = Double.parseDouble(args[4]);
        }
        catch (Exception e){
            return;
        }
        if (operation != AttributeModifier.Operation.ADD_NUMBER){
            amount /= 100;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(slot.toString());
        sb.append('.');
        sb.append(operation.name().toLowerCase());

        NamespacedKey key = NamespacedKey.fromString(sb.toString(), Main.plugin);
        AttributeModifier modifier = new AttributeModifier(key, amount, operation, slot);
        meta.removeAttributeModifier(attribute, modifier);
        if (amount > 0 || amount < 0) meta.addAttributeModifier(attribute, modifier);
        handItem.setItemMeta(meta);

    }
    private void ClearAttribute(){
        if (IsHandItemAir()) return;
        meta.setAttributeModifiers(null);
        handItem.setItemMeta(meta);
    }

    public void SetBaseAttribute(){
        if (CheckArgsLength(3, "message.do.attribute.usage")) return;
        if (IsHandItemAir()) return;
        Attribute attribute = Attribute.ATTACK_DAMAGE;
        String str = "base_attack_damage";
        double amount = 0;
        if (args[1].equalsIgnoreCase("기본공격속도")){
            attribute = Attribute.ATTACK_SPEED;
            str = "base_attack_speed";
        }
        try {
            amount = Double.parseDouble(args[2]);
        }
        catch (Exception e){
            return;
        }
        NamespacedKey key = NamespacedKey.fromString(str, null);
        AttributeModifier modifier = new AttributeModifier(key, amount, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
        meta.removeAttributeModifier(attribute, modifier);
        if (amount > 0 || amount < 0) meta.addAttributeModifier(attribute, modifier);
        handItem.setItemMeta(meta);
    }

    public void SetMaxStack(){
        if (CheckArgsLength(2, "messages.do.stack.usage")) return;
        if (IsHandItemAir()) return;
        Integer amount = parseInt(args[1], 1, 64);
        if (amount == null) return;
        meta.setMaxStackSize(amount);
        handItem.setItemMeta(meta);
    }

    public void SetItemColor(){
        if (CheckArgsLength(2, "messages.do.color.usage")) return;
        if (IsHandItemAir()) return;
        Color color = null;
        if (!args[1].equalsIgnoreCase("초기화")){
            if (!PluginUtil.IsRGB(args[1])){
                sender.sendMessage("정확한 6자리 RGB 값을 입력하세요");
                return;
            }
            color = PluginUtil.RGBToColor(args[1]);
        }
        if (meta instanceof LeatherArmorMeta leatherMeta) {
            leatherMeta.setColor(color);
        } else if (meta instanceof PotionMeta potionMeta) {
            potionMeta.setColor(color);
        } else{
            sender.sendMessage("색상 적용 불가 아이템");
            return;
        }
        handItem.setItemMeta(meta);

    }
    public void SetPotionPassiveAdd(){
        if (CheckArgsLength(5, "messages.do.potion_passive.usage")) {
            sender.sendMessage(Arrays.toString(EffectType.values())); return;
        }
        if (IsHandItemAir()) return;
        String key = "";
        int effectCode = 0;
        switch (args[2]){
            case "갑옷":
                key = "potion_passive_armor";
                break;
            case "오른손":
                key = "potion_passive_mainhand";
                break;
            case "왼손":
                key = "potion_passive_offhand";
                break;
            default:
                sender.sendMessage("정확한 부위 입력");
                return;
        }
        NamespacedKey namespacedKey = NamespacedKey.fromString(key, Main.plugin);
        if (namespacedKey == null){
            sender.sendMessage("undefined error");
            return;
        }
        if (!EffectType.HasCode(args[3])){
            sender.sendMessage("정확한 효과 입력");
            return;
        }
        effectCode = EffectType.GetCode(args[3]);
        Integer level = parseInt(args[4], 0, 32767);
        if (level == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        List<int[]> list = container.get(namespacedKey, PersistentDataType.LIST.integerArrays());
        if (list == null){
            list = new ArrayList<>();
        } else{
            list = new ArrayList<>(list);
        }
        int[] pair = (new int[]{effectCode, level});
        int i = 0;
        for (i = 0; i < list.size(); i++){
            if (list.get(i)[0] == effectCode){
                list.get(i)[1] = level;
                break;
            }
        }
        if (i == list.size()){
            list.add(pair);
        }
        container.set(namespacedKey, PersistentDataType.LIST.integerArrays(), list);
        handItem.setItemMeta(meta);

    }
    public void SetPotionPassiveRemove(){
        if (CheckArgsLength(4, "messages.do.potion_passive.usage")) {
            sender.sendMessage(Arrays.toString(EffectType.values())); return;
        }
        if (IsHandItemAir()) return;
        String key = "";
        int effectCode = 0;
        switch (args[2]){
            case "갑옷":
                key = "potion_passive_armor";
                break;
            case "오른손":
                key = "potion_passive_mainhand";
                break;
            case "왼손":
                key = "potion_passive_offhand";
                break;
            default:
                sender.sendMessage("정확한 부위 입력");
                return;
        }
        NamespacedKey namespacedKey = NamespacedKey.fromString(key, Main.plugin);
        if (namespacedKey == null){
            sender.sendMessage("undefined error");
            return;
        }
        if (!EffectType.HasCode(args[3])){
            sender.sendMessage("정확한 효과 입력");
            return;
        }
        effectCode = EffectType.GetCode(args[3]);
        PersistentDataContainer container = meta.getPersistentDataContainer();
        List<int[]> list = container.get(namespacedKey, PersistentDataType.LIST.integerArrays());
        if (list == null){
            sender.sendMessage("제거할 효과가 없음");
            return;
        }
        int i = 0;
        for (i = 0; i < list.size(); i++){
            if (list.get(i)[0] == effectCode){
                list.remove(i);
                break;
            }
        }
        if (list.isEmpty()){
            container.remove(namespacedKey);
        }
        else{
            container.set(namespacedKey, PersistentDataType.LIST.integerArrays(), list);
        }
        handItem.setItemMeta(meta);


    }
    public void SetPotionPassiveCheck(){
        if (IsHandItemAir()) return;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        SendPassiveCheck("§b착용 효과:", "potion_passive_armor", container);
        SendPassiveCheck("§b오른손 효과:", "potion_passive_mainhand", container);
        SendPassiveCheck("§b왼손 효과:", "potion_passive_offhand", container);
    }
    private void SendPassiveCheck(String title, String keyName, PersistentDataContainer container){
        NamespacedKey key = NamespacedKey.fromString(keyName, Main.plugin);
        List<int[]> list = container.get(key, PersistentDataType.LIST.integerArrays());
        sender.sendMessage(title);
        if (list == null){
            sender.sendMessage("없음");
        }
        else{
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < list.size(); i++){
                sb.append(EffectType.GetName(list.get(i)[0]));
                sb.append(" ");
                sb.append(list.get(i)[1]);
                sender.sendMessage(sb.toString());
                sb.setLength(0);
            }
        }

    }
    private void SetEquipSlot(){
        if(CheckArgsLength(3, "messages.do.equip.usage")) return;
        if (IsHandItemAir()) return;
        EquipmentSlot slot = SlotType.GetSlot(args[2]);
        if (slot == null) return;
        EquippableComponent ec = meta.getEquippable();
        ec.setSlot(slot);
        meta.setEquippable(ec);
        handItem.setItemMeta(meta);

    }
    private void SetEquipSound(){
        if(CheckArgsLength(3, "messages.do.equip.usage")) return;
        if (IsHandItemAir()) return;
        Sound sound = RegiAccess.getRegistry(RegistryKey.SOUND_EVENT).get(NamespacedKey.minecraft(args[2]));
        EquippableComponent ec = meta.getEquippable();
        ec.setEquipSound(sound);
        meta.setEquippable(ec);
        handItem.setItemMeta(meta);

    }
    private void SetEquipModel(){
        if(CheckArgsLength(3, "messages.do.equip.usage")) return;
        if (IsHandItemAir()) return;
        EquippableComponent ec = meta.getEquippable();
        ec.setModel(NamespacedKey.minecraft(args[2]));
        meta.setEquippable(ec);
        handItem.setItemMeta(meta);
    }
    private void SetModel(){
        if(CheckArgsLength(2, "messages.do.model.usage")) return;
        if (IsHandItemAir()) return;
        meta.setItemModel(NamespacedKey.minecraft(args[1]));
        handItem.setItemMeta(meta);
    }
    private void SetMaxDurability(){
        if (CheckArgsLength(3, "messages.do.stack.usage")) return;
        if (IsHandItemAir()) return;
        Integer amount = parseInt(args[2], 1, Integer.MAX_VALUE);
        if (amount == null) return;
        if (meta instanceof Damageable damageable){
            damageable.setMaxDamage(amount);
            handItem.setItemMeta(damageable);
        }
    }
    private void SetUnbreakable(){
        if (IsHandItemAir()) return;
        meta.setUnbreakable(!meta.isUnbreakable());
        handItem.setItemMeta(meta);
    }
    private void SetCurrentDurability(){
        if (CheckArgsLength(3, "messages.do.stack.usage")) return;
        if (IsHandItemAir()) return;
        Integer amount = parseInt(args[2], 0, Integer.MAX_VALUE);
        if (amount == null) return;
        if (meta instanceof Damageable damageable){
            damageable.setDamage(amount);
            handItem.setItemMeta(damageable);
        }

    }
    private void SetPotion(){
        if (CheckArgsLength(4, "messages.do.potion.usage")) return;
        if (IsHandItemAir()) return;
        if (meta instanceof PotionMeta potionMeta){
            if (!EffectType.HasCode(args[1])) return;
            PotionEffectType type =  EffectType.GetType(EffectType.GetCode(args[1]));
            Integer amp = parseInt(args[2], 0, 32767);
            if (amp == null) return;
            Integer dur = parseInt(args[3], 0, Integer.MAX_VALUE);
            if (dur == null) return;
            PotionEffect effect = new PotionEffect(type, dur, amp);
            potionMeta.addCustomEffect(effect, true);

        }
    }


}
