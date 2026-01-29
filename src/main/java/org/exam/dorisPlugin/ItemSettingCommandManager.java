package org.exam.dorisPlugin;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.data.type.Switch;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.ListPersistentDataTypeProvider;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemSettingCommandManager {

    private String[] args;
    private CommandSender sender;

    private ItemStack handItem;

    private RegistryAccess RegiAccess = RegistryAccess.registryAccess();

    private YamlConfiguration message;

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
    public void Start(){
        if (args.length < 1){
            sendMessage("messages.do.usage");
            return;
        }
        switch (args[0]){
            case "정보":
                sendMessage("messages.do.usage");
                break;
            case "인첸트":
                SetEnchantment();
                break;
            case "이름":
                SetItemName();
                break;
            case "설명":
                SetItemLore();
                break;
            case "속성":
                SetItemAttribute();
                break;
            case "색상":
                SetItemColor();
                break;
            case "스택":
                SetMaxStack();
                break;
            case "착용버프":
                SetPotionPassive();
                break;
            default:
                sendMessage("messages.do.usage");
                break;
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
    public void SetItemName(){
        if (args.length < 2){
            sendMessage("messages.do.name.usage");
            return;
        }
        if (IsHandItemAir()) return;
        ItemMeta meta = handItem.getItemMeta();
        TextFormatBuilder builder = new TextFormatBuilder(CombineRestArgstoString(1));
        Component cmp = builder.Build();
        meta.customName(cmp);
        handItem.setItemMeta(meta);
    }
    public void SetItemLore(){
        if (args.length < 2){
            sendMessage("messages.do.lore.usage");
            return;
        }
        if (args[1].equalsIgnoreCase("추가")){
            AddLore();
        }
        else if (args[1].equalsIgnoreCase("삭제")){
            DeleteLore();
        }
        else if (args[1].equalsIgnoreCase("삽입") || args[1].equalsIgnoreCase("변경")){
            InsertLore();
        }
        else {
            sendMessage("messages.do.lore.usage");
        }
    }

    public void AddLore(){
        if (args.length < 3){
            sendMessage("messages.do.lore.usage");
            return;
        }
        if (IsHandItemAir()) return;
        ItemMeta meta = handItem.getItemMeta();
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
        ItemMeta meta = handItem.getItemMeta();
        List<Component> existLores = meta.lore();
        if (existLores == null){
            sender.sendMessage("설명이 없습니다");
            return;
        }
        int index = -1;
        if (args.length < 3){
            existLores.removeLast();
        }
        else{
            try {
                index = Integer.parseInt(args[2]);
            }
            catch (Exception e){
                sender.sendMessage("숫자를 입력하세요");
                return;
            }
            if (index < 0 || index >= existLores.size()){
                sender.sendMessage("범위 초과");
                return;
            }
            existLores.remove(index);
        }
        meta.lore(existLores);
        handItem.setItemMeta(meta);

    }
    public void InsertLore(){
        if (args.length < 4){
            sendMessage("messages.do.lore.usage");
            return;
        }
        if (IsHandItemAir()) return;
        ItemMeta meta = handItem.getItemMeta();
        List<Component> existLores = meta.lore();
        if (existLores == null){
            sender.sendMessage("설명이 없습니다");
            return;
        }
        int index = -1;
        try {
            index = Integer.parseInt(args[2]);
        }
        catch (Exception e){
            sender.sendMessage("숫자를 입력하세요");
            return;
        }
        if (index < 0 || index >= existLores.size()){
            sender.sendMessage("범위 초과");
            return;
        }
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
        if (args.length < 2){
            sendMessage("messages.do.enchant.usage");
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
        int level = 1;
        if (args.length > 2){
            try {
                level = Integer.parseInt(args[2]);
            }
            catch (Exception e){ }
        }
        handItem.addUnsafeEnchantment(enchant, level);
    }
    public void SetItemAttribute(){
        if (args.length < 2){
            sendMessage("messages.do.attribute.usage");
            return;
        }
        if (args[1].equalsIgnoreCase("목록")){
            sendMessage("messages.do.attribute.list");
            return;
        }
        if (IsHandItemAir()) return;
        ItemMeta meta = handItem.getItemMeta();
        EquipmentSlotGroup slot;
        AttributeModifier.Operation operation;
        double amount = 0;
        if (args[1].equalsIgnoreCase("초기화")){
            meta.setAttributeModifiers(null);
            handItem.setItemMeta(meta);
            return;
        }
        else if (args[1].equalsIgnoreCase("기본데미지") || args[1].equalsIgnoreCase("기본공격속도")){
            SetBaseAttribute();
            return;
        }
        if (args.length < 5){
            sendMessage("messages.do.attribute.usage");
            return;
        }
        if (!AttributeType.ContainsKey(args[1])){
            sender.sendMessage("잘못된 속성");
            return;
        }
        Attribute attribute = RegiAccess.getRegistry(RegistryKey.ATTRIBUTE).get(NamespacedKey.minecraft(AttributeType.GetValue(args[1])));
        if (attribute == null){
            return;
        }
        switch (args[2]){
            case "전체":
                slot = EquipmentSlotGroup.ANY;
                break;
            case "갑옷", "옷":
                slot = EquipmentSlotGroup.ARMOR;
                break;
            case "동물":
                slot = EquipmentSlotGroup.BODY;
                break;
            case "상의","상체":
                slot = EquipmentSlotGroup.CHEST;
                break;
            case "하의","바지":
                slot = EquipmentSlotGroup.LEGS;
                break;
            case "신발","발":
                slot = EquipmentSlotGroup.FEET;
                break;
            case "손":
                slot = EquipmentSlotGroup.HAND;
                break;
            case "머리","투구","모자":
                slot = EquipmentSlotGroup.HEAD;
                break;
            case "오른손":
                slot = EquipmentSlotGroup.MAINHAND;
                break;
            case "왼손":
                slot = EquipmentSlotGroup.OFFHAND;
                break;
            case "안장":
                slot = EquipmentSlotGroup.SADDLE;
                break;
            default:
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

    public void SetBaseAttribute(){
        if (args.length < 3){
            sendMessage("messages.do.attribute.usage");
            return;
        }
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
        ItemMeta meta = handItem.getItemMeta();
        NamespacedKey key = NamespacedKey.fromString(str, null);
        AttributeModifier modifier = new AttributeModifier(key, amount, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);
        meta.removeAttributeModifier(attribute, modifier);
        if (amount > 0 || amount < 0) meta.addAttributeModifier(attribute, modifier);
        handItem.setItemMeta(meta);
    }

    public void SetMaxStack(){
        if (args.length < 2){
            sendMessage("messages.do.stack.usage");
            return;
        }
        int amount = 0;
        try {
            amount = Integer.parseInt(args[1]);
        }
        catch (Exception e){
            return;
        }
        if (amount < 1){
            return;
        }
        ItemMeta meta = handItem.getItemMeta();
        meta.setMaxStackSize(amount);
        handItem.setItemMeta(meta);
    }

    public void SetItemColor(){
        if (args.length < 2){
            sendMessage("messages.do.color.usage");
            return;
        }

        ItemMeta meta = handItem.getItemMeta();
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
    public void SetCode(){
        ItemMeta meta = handItem.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = NamespacedKey.fromString("potion_passive", Main.plugin);
        if (args.length < 2){
            String str = container.get(key, PersistentDataType.STRING);
            if (str != null) sender.sendMessage(str);
            return;
        }
        List<int[]> li = new ArrayList<>();
        li.add(new int[]{5, 7});
        li.add(new int[]{3, 8});
        container.set(key, PersistentDataType.LIST.integerArrays(), li);
        handItem.setItemMeta(meta);
    }
    public void SetPotionPassive(){
        if (args.length < 2){
            sendMessage("messages.do.potion_passive.usage");
            sender.sendMessage(Arrays.toString(EffectType.values()));

            return;
        }
        switch (args[1]){
            case "추가":
                SetPotionPassiveAdd();
                break;
            case "제거":
                SetPotionPassiveRemove();
                break;
            case "확인":
                SetPotionPassiveCheck();
                break;
        }
    }
    public void SetPotionPassiveAdd(){
        if (args.length < 5){
            sendMessage("messages.do.potion_passive.usage");
            return;
        }
        if (IsHandItemAir()) return;
        String key = "";
        int effectCode = 0;
        int level = 0;
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
        try {
            level = Integer.parseInt(args[4]);
        }
        catch (Exception e){
            sender.sendMessage("정확한 레벨 입력");
            return;
        }
        if (level < 0){
            sender.sendMessage("0 이상의 레벨 입력");
            return;
        }

        ItemMeta meta = handItem.getItemMeta();
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
        if (args.length < 4){
            sendMessage("messages.do.potion_passive.usage");
            return;
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

        ItemMeta meta = handItem.getItemMeta();
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
        ItemMeta meta = handItem.getItemMeta();
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


}
