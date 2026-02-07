package org.exam.dorisPlugin;

import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
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
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.bukkit.persistence.ListPersistentDataTypeProvider;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.swing.border.EmptyBorder;
import java.util.*;

public class ItemSettingCommandManager {

    private String[] args;
    private CommandSender sender;
    private ItemStack handItem;
    private RegistryAccess RegiAccess = RegistryAccess.registryAccess();
    private YamlConfiguration message;
    private ItemMeta meta;
    private PlayerInventory inventory;

    public ItemSettingCommandManager(CommandSender sender, String[] args, YamlConfiguration message){
        this.args = args;
        this.sender = sender;
        this.message = message;
        // Player 인터페이스는 CommandSender 인터페이스를 상속
        // 플레이어가 명령어 호출 시 Player 인터페이스를 구현한 클래스가 sender 매개변수로 올 것.
        // 따라서 아래와 같이 캐스팅 가능.
        Player player = (Player)sender;
        inventory = player.getInventory();
        handItem = inventory.getItemInMainHand();
    }
    private void sendMessage(String path){
        List<String> str = message.getStringList(path);
        for (String line : str) {
            sender.sendMessage(line);
        }
    }
    private boolean IsHandItemAir(){
        if (handItem.getType().isAir()){
            sender.sendMessage("§c손에 아이템을 들어주세요");
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
            sender.sendMessage("§c올바른 정수값 입력이 아니거나 범위를 벗어납니다");
            return null;
        }
    }
    private Float parstFloat(String arg, float min, float max){
        try {
            float value = Float.parseFloat(arg);
            return value >= min && value <= max ? value : null;
        } catch (Exception e) {
            sender.sendMessage("§c올바른 실수값 입력이 아니거나 범위를 벗어납니다");
            return null;
        }
    }
    private void SendResult(String... args){
        String str = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
        sender.sendMessage(str);

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
            case "공격버프": SetPotionAttack(); break;
            case "착용": SetEquip();break;
            case "모델": SetModel(); break;
            case "내구도": SetDurability(); break;
            case "포션": SetPotion(); break;
            case "음식": SetFood(); break;
            case "소비": SetConsume(); break;
            case "쿨타임": SetCooldown(); break;
            case "잔여물": SetRemainder(); break;
            case "방지" : SetPrevent(); break;
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
    public void SetPotionAttack(){
        if (CheckArgsLength(2, "messages.do.potion_attack.usage")) {
            sender.sendMessage(Arrays.toString(EffectType.values())); return;
        }
        switch (args[1]){
            case "추가": SetPotionAttackAdd(); break;
            case "제거": SetPotionAttackRemove(); break;
            case "확인": SetPotionAttackCheck(); break;
            default: sendMessage("messages.do.potion_attack_usage"); break;
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
    private void SetPotion(){
        if (CheckArgsLength(2, "messages.do.potion.usage")) return;
        switch (args[1]){
            case "제거": RemovePotion(); break;
            default: AddPotion(); break;
        }
    }
    private void SetFood(){
        if (CheckArgsLength(2, "messages.do.food.usage")) return;
        switch (args[1]){
            case "회복량": SetFoodNutrition(); break;
            case "포만도": SetFoodSaturation(); break;
            case "항상": SetFoodAlways(); break;
            default: sendMessage("messages.do.food.usage"); break;
        }
    }
    private void SetConsume(){
        if (CheckArgsLength(2, "messages.do.consume.usage")) return;
        switch (args[1]){
            case "설정": SetConsumable(); break;
            case "시간": SetConsumeSeconds(); break;
            case "소리": SetConsumeSound(); break;
            case "애니메이션": SetConsumeAnim(); break;
            case "파티클": SetConsumeParticle(); break;
            case "포션": SetConsumePotionEffect(); break;
            case "포션해제": SetConsumePotionRemoveEffect(); break;
            case "소리효과": SetConsumeSoundEffect(); break;
            case "이동거리": SetConsumeTelepotationEffectDistance(); break;
            case "모든포션해제": SetConsumeClearAllEffect(); break;
            default: sendMessage("messages.do.consume.usage"); break;
        }
    }
    private void SetCooldown(){
        if (CheckArgsLength(2, "messages.do.cooldown.usage")) return;
        switch (args[1]){
            case "설정": SetCooldownSeconds(); break;
            case "그룹": SetCooldownGroup(); break;
            default: sendMessage("messages.do.cooldown.usage"); break;
        }

    }
    private void SetConsumePotionEffect(){
        if (CheckArgsLength(3, "messages.do.consume.usage")) return;
        switch (args[2]){
            case "추가": AddConsumePotionEffect(); break;
            case "제거": RemoveConsumePotionEffect(); break;
            case "확률": SetConsumePotionEffectChance(); break;
            default: sendMessage("messages.do.consume.usage"); break;
        }
    }
    private void SetConsumePotionRemoveEffect(){
        if (CheckArgsLength(3, "messages.do.consume.usage")) return;
        switch (args[2]){
            case "추가": AddConsumePotionRemoveEffect(); break;
            case "제거": RemoveConsumePotionRemoveEffect(); break;
            default: sendMessage("messages.do.consume.usage"); break;
        }
    }
    public void SetItemName(){
        if (CheckArgsLength(2, "messages.do.name.usage")) return;
        if (IsHandItemAir()) return;
        TextFormatBuilder builder = new TextFormatBuilder(CombineRestArgstoString(1));
        Component cmp = builder.Build();
        meta.customName(cmp);
        handItem.setItemMeta(meta);
        sender.sendMessage("§a아이템의 이름을 변경했습니다.");
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
        sender.sendMessage("§a아이템에 설명을 추가했습니다.");

    }
    public void DeleteLore(){
        if (IsHandItemAir()) return;
        List<Component> existLores = meta.lore();
        if (existLores == null){
            sender.sendMessage("§c아이템에 설명이 없습니다.");
            return;
        }
        Integer index = existLores.size() - 1;
        if (args.length >= 3){
            index = parseInt(args[2], 0, existLores.size() - 1);
            if (index == null) return;
            existLores.remove(index.intValue());
        }
        meta.lore(existLores);
        handItem.setItemMeta(meta);
        sender.sendMessage("§a아이템의 설명을 제거했습니다.");
        SendResult("§a아이템의 ", index.toString(), "번째줄 설명을 제거했습니다");

    }
    public void InsertLore(){
        if (CheckArgsLength(4, "messages.do.lore.usage")) return;
        if (IsHandItemAir()) return;
        List<Component> existLores = meta.lore();
        if (existLores == null){
            sender.sendMessage("§c아이템에 설명이 없습니다.");
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
        SendResult("§a아이템의 ", index.toString(), "번째줄 설명을 수정했습니다");
    }
    public void SetEnchantment(){
        if (CheckArgsLength(2, "messages.do.enchant.usage")){
            sender.sendMessage(Arrays.toString(EnchantType.values()));
            return;
        }
        if (IsHandItemAir()) return;
        if (!EnchantType.ContainsKey(args[1])){
            sender.sendMessage("§c잘못된 인첸트가 입력됨");
            return;
        }
        Enchantment enchant = RegiAccess.getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.minecraft(EnchantType.GetValue(args[1])));
        if (enchant == null) return;
        Integer level = 1;
        if (args.length > 2){
            level = parseInt(args[2], 0, Integer.MAX_VALUE);
            if (level == null) return;
        }
        handItem.addUnsafeEnchantment(enchant, level);
        SendResult("§a인첸트 추가 ", enchant.toString(), " 레벨 ", level.toString());
    }
    private void SetAttribute(){
        if (CheckArgsLength(5, "message.do.attribute.usage")) return;
        if (IsHandItemAir()) return;
        EquipmentSlotGroup slot;
        AttributeModifier.Operation operation;
        double amount = 0;
        if (!AttributeType.ContainsKey(args[1])){
            sender.sendMessage("§c잘못된 속성이 입력됨");
            return;
        }
        Attribute attribute = RegiAccess.getRegistry(RegistryKey.ATTRIBUTE).get(NamespacedKey.minecraft(AttributeType.GetValue(args[1])));
        if (attribute == null){
            return;
        }
        slot = SlotType.GetSlotGroup(args[2]);
        if (slot == null){
            sender.sendMessage("§c잘못된 슬롯명이 입력됨");
            return;
        }
        switch (args[3]){
            case "더하기": operation = AttributeModifier.Operation.ADD_NUMBER; break;
            case "곱하기": operation = AttributeModifier.Operation.ADD_SCALAR; break;
            case "누적곱하기": operation = AttributeModifier.Operation.MULTIPLY_SCALAR_1; break;
            default: sender.sendMessage("§c잘못된 계산 방식이 입력됨"); return;
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
        sender.sendMessage("§a아이템에 속성을 추가했습니다.");

    }
    private void ClearAttribute(){
        if (IsHandItemAir()) return;
        meta.setAttributeModifiers(null);
        handItem.setItemMeta(meta);
        sender.sendMessage("§a아이템의 모든 속성을 제거했습니다.");
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
            sender.sendMessage("§c잘못된 값이 입력됨");
            return;
        }
        NamespacedKey key = NamespacedKey.fromString(str, null);
        AttributeModifier modifier = new AttributeModifier(key, amount, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
        meta.removeAttributeModifier(attribute, modifier);
        if (amount > 0 || amount < 0) meta.addAttributeModifier(attribute, modifier);
        handItem.setItemMeta(meta);
        sender.sendMessage("§a아이템에 기본 속성을 적용");
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
                sender.sendMessage("§c정확한 6자리 RGB 값을 입력하세요");
                return;
            }
            color = PluginUtil.RGBToColor(args[1]);
        }
        if (meta instanceof LeatherArmorMeta leatherMeta) {
            leatherMeta.setColor(color);
        } else if (meta instanceof PotionMeta potionMeta) {
            potionMeta.setColor(color);
        } else{
            sender.sendMessage("§c색상 적용 불가 아이템");
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
            case "갑옷": key = "potion_passive_armor"; break;
            case "오른손": key = "potion_passive_mainhand"; break;
            case "왼손": key = "potion_passive_offhand"; break;
            default: sender.sendMessage("§c잘못된 부위가 입력됨"); return;
        }
        NamespacedKey namespacedKey = NamespacedKey.fromString(key, Main.plugin);
        if (namespacedKey == null){
            sender.sendMessage("undefined error");
            return;
        }
        if (!EffectType.HasCode(args[3])){
            sender.sendMessage("§c잘못된 포션 효과가 입력됨");
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
            case "갑옷": key = "potion_passive_armor"; break;
            case "오른손": key = "potion_passive_mainhand"; break;
            case "왼손": key = "potion_passive_offhand"; break;
            default: sender.sendMessage("§c잘못된 부위가 입력됨"); return;
        }
        NamespacedKey namespacedKey = NamespacedKey.fromString(key, Main.plugin);
        if (namespacedKey == null){
            sender.sendMessage("undefined error");
            return;
        }
        if (!EffectType.HasCode(args[3])){
            sender.sendMessage("§c잘못된 포션 효과가 입력됨");
            return;
        }
        effectCode = EffectType.GetCode(args[3]);
        PersistentDataContainer container = meta.getPersistentDataContainer();
        List<int[]> list = container.get(namespacedKey, PersistentDataType.LIST.integerArrays());
        if (list == null){
            sender.sendMessage("§c해당 효과는 이미 존재하지 않음");
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
    public void SetPotionAttackAdd(){
        if (CheckArgsLength(7, "messages.do.potion_attack.usage")) {
            sender.sendMessage(Arrays.toString(EffectType.values())); return;
        }
        if (IsHandItemAir()) return;
        String key = "";
        int effectCode = 0;
        switch (args[2]){
            case "갑옷": key = "potion_attack_armor"; break;
            case "오른손": key = "potion_attack_mainhand"; break;
            case "왼손": key = "potion_attack_offhand"; break;
            default: sender.sendMessage("§c잘못된 부위가 입력됨"); return;
        }
        NamespacedKey namespacedKey = NamespacedKey.fromString(key, Main.plugin);
        if (namespacedKey == null){
            sender.sendMessage("undefined error");
            return;
        }
        if (!EffectType.HasCode(args[3])){
            sender.sendMessage("§c잘못된 포션 효과가 입력됨");
            return;
        }
        effectCode = EffectType.GetCode(args[3]);
        Integer level = parseInt(args[4], 0, 32767);
        if (level == null) return;
        Integer duration = parseInt(args[5], 0, Integer.MAX_VALUE);
        if (duration == null) return;
        Integer chance = parseInt(args[6], 0, 1000);
        if (chance == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        List<int[]> list = container.get(namespacedKey, PersistentDataType.LIST.integerArrays());
        if (list == null){
            list = new ArrayList<>();
        } else{
            list = new ArrayList<>(list);
        }
        int[] pair = (new int[]{effectCode, level, duration, chance});
        int i = 0;
        for (i = 0; i < list.size(); i++){
            if (list.get(i)[0] == effectCode){
                list.get(i)[1] = level;
                list.get(i)[2] = duration;
                list.get(i)[3] = chance;
                break;
            }
        }
        if (i == list.size()){
            list.add(pair);
        }
        container.set(namespacedKey, PersistentDataType.LIST.integerArrays(), list);
        handItem.setItemMeta(meta);
    }
    public void SetPotionAttackRemove(){
        if (CheckArgsLength(4, "messages.do.potion_attack.usage")) {
            sender.sendMessage(Arrays.toString(EffectType.values())); return;
        }
        if (IsHandItemAir()) return;
        String key = "";
        int effectCode = 0;
        switch (args[2]){
            case "갑옷": key = "potion_attack_armor"; break;
            case "오른손": key = "potion_attack_mainhand"; break;
            case "왼손": key = "potion_attack_offhand"; break;
            default: sender.sendMessage("§c잘못된 부위가 입력됨"); return;
        }
        NamespacedKey namespacedKey = NamespacedKey.fromString(key, Main.plugin);
        if (namespacedKey == null){
            sender.sendMessage("undefined error");
            return;
        }
        if (!EffectType.HasCode(args[3])){
            sender.sendMessage("§c잘못된 포션 효과가 입력됨");
            return;
        }
        effectCode = EffectType.GetCode(args[3]);
        PersistentDataContainer container = meta.getPersistentDataContainer();
        List<int[]> list = container.get(namespacedKey, PersistentDataType.LIST.integerArrays());
        if (list == null){
            sender.sendMessage("§c해당 효과는 이미 존재하지 않음");
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
    private void SetEquipSlot(){
        if(CheckArgsLength(3, "messages.do.equip.usage")) return;
        if (IsHandItemAir()) return;
        EquipmentSlot slot = SlotType.GetSlot(args[2]);
        if (slot == null) {
            sender.sendMessage("§c잘못된 슬롯이 입력됨");
            return;
        }
        EquippableComponent ec = meta.getEquippable();
        ec.setSlot(slot);
        meta.setEquippable(ec);
        handItem.setItemMeta(meta);

    }
    public void SetPotionAttackCheck(){
        if (IsHandItemAir()) return;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        SendAttackCheck("§b착용 효과:", "potion_attack_armor", container);
        SendAttackCheck("§b오른손 효과:", "potion_attack_mainhand", container);
        SendAttackCheck("§b왼손 효과:", "potion_attack_offhand", container);
    }
    private void SendAttackCheck(String title, String keyName, PersistentDataContainer container){
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
                sb.append(" 레벨: ");
                sb.append(list.get(i)[1]);
                sb.append(" 지속시간: ");
                sb.append(list.get(i)[2]);
                sb.append(" 확률: ");
                sb.append(list.get(i)[3]);
                sender.sendMessage(sb.toString());
                sb.setLength(0);
            }
        }
    }
    private void SetEquipSound(){
        if(CheckArgsLength(3, "messages.do.equip.usage")) return;
        if (IsHandItemAir()) return;
        Sound sound = RegiAccess.getRegistry(RegistryKey.SOUND_EVENT).get(NamespacedKey.minecraft(args[2]));
        if (sound == null){
            sender.sendMessage("§c잘못된 소리가 입력됨");
            return;
        }
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
        NamespacedKey key = NamespacedKey.minecraft(args[1]);
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
    private void AddPotion(){
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
            handItem.setItemMeta(meta);
        }
    }
    private void RemovePotion(){
        if (CheckArgsLength(3, "messages.do.potion.usage")) return;
        if (IsHandItemAir()) return;
        if (meta instanceof PotionMeta potionMeta){
            if (!EffectType.HasCode(args[2])) return;
            PotionEffectType type =  EffectType.GetType(EffectType.GetCode(args[2]));
            potionMeta.removeCustomEffect(type);
            handItem.setItemMeta(meta);
        }
    }
    private void SetConsumable(){
        if (IsHandItemAir()) return;
        if (!handItem.hasData(DataComponentTypes.CONSUMABLE)){
            Consumable.Builder builder = Consumable.consumable();
            handItem.setData(DataComponentTypes.CONSUMABLE, builder);
            sender.sendMessage("아이템이 사용 가능하도록 설정됨");
        }
        else{
            handItem.unsetData(DataComponentTypes.CONSUMABLE);
            sender.sendMessage("아이템이 사용 할 수 없게 설정됨");
        }

    }
    private Consumable ConsumableSettingPlate(int length){
        if (CheckArgsLength(length, "messages.do.consume.usage")) return null;
        if (IsHandItemAir()) return null;
        if (!handItem.hasData(DataComponentTypes.CONSUMABLE)) {
            sendMessage("messages.do.consume.not_valid");
            return null;
        }
        return handItem.getData(DataComponentTypes.CONSUMABLE);

    }
    private void SetConsumeSeconds(){
        Consumable consumable =  ConsumableSettingPlate(3);
        if (consumable == null) return;
        Consumable.Builder builder = consumable.toBuilder();
        Float second = parstFloat(args[2], 0, Float.MAX_VALUE);
        if (second == null) return;
        builder.consumeSeconds(second);
        handItem.setData(DataComponentTypes.CONSUMABLE, builder);
    }
    private void SetConsumeSound(){
        Consumable consumable =  ConsumableSettingPlate(3);
        if (consumable == null) return;
        Consumable.Builder builder = consumable.toBuilder();
        builder.sound(NamespacedKey.minecraft(args[2]));
        handItem.setData(DataComponentTypes.CONSUMABLE, builder);

    }
    private void SetConsumeAnim(){
        Consumable consumable =  ConsumableSettingPlate(3);
        if (consumable == null) return;
        Consumable.Builder builder = consumable.toBuilder();
        ItemUseAnimation animation = UseAnimationType.GetAnimation(args[2]);
        if (animation == null){
            sender.sendMessage("§c잘못된 애니메이션 이름이 입력됨");
            return;
        }
        builder.animation(animation);
        handItem.setData(DataComponentTypes.CONSUMABLE, builder);


    }
    private void SetConsumeParticle(){
        Consumable consumable =  ConsumableSettingPlate(2);
        if (consumable == null) return;
        Consumable.Builder builder = consumable.toBuilder();
        builder.hasConsumeParticles(!consumable.hasConsumeParticles());
        handItem.setData(DataComponentTypes.CONSUMABLE, builder);


    }
    private void SetConsumeSoundEffect(){
        Consumable consumable =  ConsumableSettingPlate(3);
        if (consumable == null) return;
        Consumable.Builder builder = consumable.toBuilder();
        List<ConsumeEffect> effects = new ArrayList<>(consumable.consumeEffects());
        for (ConsumeEffect e : effects){
            if (e instanceof ConsumeEffect.PlaySound ce){
                effects.remove(ce);
                builder.effects(effects);
                break;
            }
        }
        builder.addEffect(ConsumeEffect.playSoundConsumeEffect(NamespacedKey.minecraft(args[2])));
        handItem.setData(DataComponentTypes.CONSUMABLE, builder);
    }
    private void SetConsumeClearAllEffect(){
        Consumable consumable =  ConsumableSettingPlate(2);
        if (consumable == null) return;
        Consumable.Builder builder = consumable.toBuilder();
        List<ConsumeEffect> effects = new ArrayList<>(consumable.consumeEffects());
        for (ConsumeEffect e : effects){
            if (e instanceof ConsumeEffect.PlaySound ce){
                effects.remove(ce);
                builder.effects(effects);
                break;
            }
        }
        builder.addEffect(ConsumeEffect.clearAllStatusEffects());
        handItem.setData(DataComponentTypes.CONSUMABLE, builder);
    }
    private void SetConsumeTelepotationEffectDistance(){
        Consumable consumable =  ConsumableSettingPlate(3);
        if (consumable == null) return;
        Consumable.Builder builder = consumable.toBuilder();
        List<ConsumeEffect> effects = new ArrayList<>(consumable.consumeEffects());
        Float second = parstFloat(args[2], 0, Float.MAX_VALUE);
        if (second == null) return;
        for (ConsumeEffect e : effects){
            if (e instanceof ConsumeEffect.TeleportRandomly ce){
                effects.remove(ce);
                builder.effects(effects);
                break;
            }
        }
        builder.addEffect(ConsumeEffect.teleportRandomlyEffect(second));
        handItem.setData(DataComponentTypes.CONSUMABLE, builder);

    }
    private void AddConsumePotionEffect(){
        Consumable consumable =  ConsumableSettingPlate(6);
        if (consumable == null) return;
        Consumable.Builder builder = consumable.toBuilder();
        List<ConsumeEffect> effects = new ArrayList<>(consumable.consumeEffects());
        if (!EffectType.HasCode(args[3])) {
            sender.sendMessage("§c잘못된 포션 효과가 입력됨");
            return;
        }
        PotionEffectType type =  EffectType.GetType(EffectType.GetCode(args[3]));
        Integer amp = parseInt(args[4], 0, 32767);
        if (amp == null) return;
        Integer dur = parseInt(args[5], 0, Integer.MAX_VALUE);
        if (dur == null) return;
        PotionEffect potion = new PotionEffect(type, dur, amp);
        List<PotionEffect> potionEffects = new ArrayList<>();
        boolean isExist = false;
        float prob = 1;
        for (ConsumeEffect e : effects){
            if (e instanceof ConsumeEffect.ApplyStatusEffects ce){
                potionEffects = new ArrayList<>(ce.effects());
                prob = ce.probability();
                effects.remove(ce);
                builder.effects(effects);
                isExist = true;
                break;
            }
        }
        if (isExist){
            for (PotionEffect p : potionEffects){
                if (p.getType() == type){
                    potionEffects.remove(p);
                    break;
                }
            }
        }
        potionEffects.add(potion);
        builder.addEffect(ConsumeEffect.applyStatusEffects(potionEffects, prob));
        handItem.setData(DataComponentTypes.CONSUMABLE, builder);
        sender.sendMessage("아이템 포션 효과를 수정했습니다.");

    }
    private void RemoveConsumePotionEffect(){
        Consumable consumable =  ConsumableSettingPlate(4);
        if (consumable == null) return;
        Consumable.Builder builder = consumable.toBuilder();
        List<ConsumeEffect> effects = new ArrayList<>(consumable.consumeEffects());
        if (!EffectType.HasCode(args[3])) {
            sender.sendMessage("§c잘못된 포션 효과가 입력됨");
            return;
        }
        PotionEffectType type =  EffectType.GetType(EffectType.GetCode(args[3]));
        List<PotionEffect> potionEffects = new ArrayList<>();
        boolean isExist = false;
        float prob = 1;
        for (ConsumeEffect e : effects){
            if (e instanceof ConsumeEffect.ApplyStatusEffects ce){
                potionEffects = new ArrayList<>(ce.effects());
                prob = ce.probability();
                effects.remove(ce);
                builder.effects(effects);
                isExist = true;
                break;
            }
        }
        if (isExist){
            for (PotionEffect p : potionEffects){
                if (p.getType() == type){
                    potionEffects.remove(p);
                    break;
                }
            }
        }
        else {
            sender.sendMessage("§c제거할 포션 효과가 없음");
            return;
        }
        builder.addEffect(ConsumeEffect.applyStatusEffects(potionEffects, prob));
        handItem.setData(DataComponentTypes.CONSUMABLE, builder);

    }
    private void SetConsumePotionEffectChance(){
        Consumable consumable =  ConsumableSettingPlate(4);
        if (consumable == null) return;
        Consumable.Builder builder = consumable.toBuilder();
        List<ConsumeEffect> effects = new ArrayList<>(consumable.consumeEffects());
        Float prob = parstFloat(args[3], 0, Float.MAX_VALUE);
        if (prob == null) return;
        prob /= 100;
        List<PotionEffect> potionEffects = new ArrayList<>();
        boolean isExist = false;
        for (ConsumeEffect e : effects){
            if (e instanceof ConsumeEffect.ApplyStatusEffects ce){
                potionEffects = new ArrayList<>(ce.effects());
                effects.remove(ce);
                builder.effects(effects);
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            sender.sendMessage("§c아이템에 포션 효과가 존재하지 않음");
            return;
        }
        builder.addEffect(ConsumeEffect.applyStatusEffects(potionEffects, prob));
        handItem.setData(DataComponentTypes.CONSUMABLE, builder);

    }
    private void AddConsumePotionRemoveEffect(){
        Consumable consumable =  ConsumableSettingPlate(4);
        if (consumable == null) return;
        Consumable.Builder builder = consumable.toBuilder();
        List<ConsumeEffect> effects = new ArrayList<>(consumable.consumeEffects());
        if (!EffectType.HasCode(args[3])) {
            sender.sendMessage("§c잘못된 포션 효과가 입력됨");
            return;
        }
        PotionEffectType type =  EffectType.GetType(EffectType.GetCode(args[3]));
        Collection<TypedKey<PotionEffectType>> potionEffects = new ArrayList<>();
        RegistryKeySet<PotionEffectType> potionEffectsKeySet;
        boolean isExist = false;
        for (ConsumeEffect e : effects){
            if (e instanceof ConsumeEffect.RemoveStatusEffects ce){
                potionEffects = new ArrayList<>(ce.removeEffects().values());
                effects.remove(ce);
                builder.effects(effects);
                isExist = true;
                break;
            }
        }
        if (isExist){
            for (TypedKey<PotionEffectType> p : potionEffects){
                if (p.key().toString().equals(type.key().toString())){
                    sender.sendMessage("§c이미 해당 효과가 존재함.");
                    return;
                }
            }
        }
        potionEffects.add(TypedKey.create(RegistryKey.MOB_EFFECT, type.key()));
        potionEffectsKeySet = RegistrySet.keySet(RegistryKey.MOB_EFFECT, potionEffects);
        builder.addEffect(ConsumeEffect.removeEffects(potionEffectsKeySet));
        handItem.setData(DataComponentTypes.CONSUMABLE, builder);

    }
    private void RemoveConsumePotionRemoveEffect(){
        Consumable consumable =  ConsumableSettingPlate(4);
        if (consumable == null) return;
        Consumable.Builder builder = consumable.toBuilder();
        List<ConsumeEffect> effects = new ArrayList<>(consumable.consumeEffects());
        if (!EffectType.HasCode(args[3])) {
            sender.sendMessage("§c잘못된 포션 효과가 입력됨");
            return;
        }
        PotionEffectType type =  EffectType.GetType(EffectType.GetCode(args[3]));
        Collection<TypedKey<PotionEffectType>> potionEffects = new ArrayList<>();
        RegistryKeySet<PotionEffectType> potionEffectsKeySet;
        for (ConsumeEffect e : effects){
            if (e instanceof ConsumeEffect.RemoveStatusEffects ce){
                potionEffects = new ArrayList<>(ce.removeEffects().values());
                effects.remove(ce);
                builder.effects(effects);
                break;
            }
        }
        potionEffects.remove(TypedKey.create(RegistryKey.MOB_EFFECT, type.key()));
        potionEffectsKeySet = RegistrySet.keySet(RegistryKey.MOB_EFFECT, potionEffects);
        builder.addEffect(ConsumeEffect.removeEffects(potionEffectsKeySet));
        handItem.setData(DataComponentTypes.CONSUMABLE, builder);
    }
    private void SetFoodNutrition(){
        if (CheckArgsLength(3, "messages.do.food.usage")) return;
        if (IsHandItemAir()) return;
        FoodComponent food = meta.getFood();
        Integer value = parseInt(args[2], 0, 32767);
        if (value == null) return;
        food.setNutrition(value);
        meta.setFood(food);
        handItem.setItemMeta(meta);
    }
    private void SetFoodSaturation(){
        if (CheckArgsLength(3, "messages.do.food.usage")) return;
        if (IsHandItemAir()) return;
        FoodComponent food = meta.getFood();
        Integer value = parseInt(args[2], 0, 32767);
        if (value == null) return;
        food.setSaturation(value);
        meta.setFood(food);
        handItem.setItemMeta(meta);

    }
    private void SetFoodAlways(){
        if (IsHandItemAir()) return;
        FoodComponent food = meta.getFood();
        food.setCanAlwaysEat(!food.canAlwaysEat());
        meta.setFood(food);
        handItem.setItemMeta(meta);
    }
    private void SetCooldownSeconds(){
        if (CheckArgsLength(3, "messages.do.cooldown.usage")) return;
        if (IsHandItemAir()) return;
        UseCooldownComponent cool = meta.getUseCooldown();
        Float value = parstFloat(args[2], 0, Float.MAX_VALUE);
        if (value == null) return;
        cool.setCooldownSeconds(value);
        meta.setUseCooldown(cool);
        handItem.setItemMeta(meta);
    }
    private void SetCooldownGroup(){
        if (CheckArgsLength(3, "messages.do.cooldown.usage")) return;
        if (IsHandItemAir()) return;
        UseCooldownComponent cool = meta.getUseCooldown();
        cool.setCooldownGroup(NamespacedKey.fromString(args[2], Main.plugin));
        meta.setUseCooldown(cool);
        handItem.setItemMeta(meta);
    }
    private void SetRemainder(){
        if (IsHandItemAir()) return;
        ItemStack offItem = inventory.getItemInOffHand();
        meta.setUseRemainder(offItem);
        handItem.setItemMeta(meta);
    }
    private void SetPrevent(){
        if (CheckArgsLength(2, "messages.do.prevent.usage")) return;
        if (IsHandItemAir()) return;
        NamespacedKey namespacedKey = NamespacedKey.fromString(FunctionalBlockPreventer.keyString, Main.plugin);
        if (namespacedKey == null){
            sender.sendMessage("undefined error");
            return;
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();
        Integer mask = container.get(namespacedKey, PersistentDataType.INTEGER);
        if (mask == null) mask = 0;
        int shift = 0;
        boolean all = false;
        switch (args[1]){
            case "작업대": break;
            case "화로": shift = 1; break;
            case "인첸트": shift = 2; break;
            case "모루": shift = 3; break;
            case "숫돌": shift = 4; break;
            case "석재절단기": shift = 5; break;
            case "훈연기": shift = 6; break;
            case "용광로": shift = 7; break;
            case "대장장이": shift = 8; break;
            case "베틀": shift = 9; break;
            case "제작기": shift = 10; break;
            case "플레이어": shift = 11; break;
            case "전부": all = true; break;
            default: sendMessage("messages.do.prevent.usage"); break;
        }
        if (all){
            int v = 0b111111111111;
            if ((mask & v) == v){
                sender.sendMessage("§b모든 기능블록 사용 가능하게 설정됨");
                mask = 0;
            }
            else{
                mask = v;
                sender.sendMessage("§b모든 기능 블록 사용 불가로 설정됨");
            }
        }
        else{
            mask = mask ^ 1 << shift;
            if ((mask & 1 << shift) != 0){
                sender.sendMessage("§b해당 기능 블록 사용 불가로 설정됨");
            }
            else {
                sender.sendMessage("§b해당 기능 블록 사용 가능하게 설정됨");
            }
        }
        container.set(namespacedKey, PersistentDataType.INTEGER, mask);
        handItem.setItemMeta(meta);
    }


}
