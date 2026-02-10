package org.exam.dorisPlugin;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemSettingCommandTabCompleter implements TabCompleter, CommandExecutor {

    private ItemSettingCommandManager manager;
    private String[] args;
    private CommandSender sender;
    private List<String> tabComplete;
    private int length;

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
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!sender.isOp()) return false;
        this.args = args;
        this.sender = sender;
        if (CheckArgsLength(1, "messages.do.usage")) return true;
        manager = new ItemSettingCommandManager(sender, args);
        switch (args[0]){
            case "인첸트": manager.SetEnchantment(); break;
            case "이름": manager.SetItemName(); break;
            case "설명": SetItemLore(); break;
            case "속성": SetItemAttribute(); break;
            case "색상": manager.SetItemColor(); break;
            case "스택": manager.SetMaxStack(); break;
            case "착용버프": SetPotionPassive(); break;
            case "공격버프": SetPotionAttack(); break;
            case "착용": SetEquip();break;
            case "모델": manager.SetModel(); break;
            case "내구도": SetDurability(); break;
            case "포션": SetPotion(); break;
            case "음식": SetFood(); break;
            case "소비": SetConsume(); break;
            case "쿨타임": SetCooldown(); break;
            case "잔여물": manager.SetRemainder(); break;
            case "방지" : manager.SetPrevent(); break;
            default: sendMessage("messages.do.usage"); break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){

        tabComplete = new ArrayList<>();
        length = args.length;
        if (length <= 1){
            defaultComp();
        }
        else if (length <= 2){
            switch (args[0]){
                case "인첸트": enchantComp(); break;
                case "설명": loreComp(); break;
                //case "속성": attributeComp(); break;
                //case "착용버프": potionPassiveComp(); break;
                //case "공격버프": potionAttackComp(); break;
                //case "착용": equipComp();break;
                case "모델": modelComp(); break;
                //case "내구도": durabilityComp(); break;
                //case "포션": potionComp(); break;
                //case "음식": foodComp(); break;
                //case "소비": consumeComp(); break;
                //case "쿨타임": cooldownComp(); break;
                //case "방지" : preventComp(); break;
                default: break;
            }
        }
        var filteredComplete = tabComplete.stream().filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()));
        return filteredComplete.collect(Collectors.toList());
    }

    private void defaultComp(){
        tabComplete.add("인첸트");
        tabComplete.add("이름");
        tabComplete.add("설명");
        tabComplete.add("속성");
        tabComplete.add("색상");
        tabComplete.add("스택");
        tabComplete.add("착용버프");
        tabComplete.add("공격버프");
        tabComplete.add("착용");
        tabComplete.add("모델");
        tabComplete.add("내구도");
        tabComplete.add("포션");
        tabComplete.add("음식");
        tabComplete.add("소비");
        tabComplete.add("쿨타임");
        tabComplete.add("잔여물");
        tabComplete.add("방지");
    }
    private void enchantComp(){
        for (EnchantType e : EnchantType.values()){
            tabComplete.add(e.toString());
        }
    }
    private void modelComp(){
        for (ItemType k : RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM)){
            tabComplete.add(k.getKey().getKey());
        }
    }
    private void loreComp(){
        tabComplete.add("추가");
        tabComplete.add("삭제");
        tabComplete.add("삽입");
        tabComplete.add("변경");
    }
    private void SetItemLore(){
        if (CheckArgsLength(2, "messages.do.lore.usage")) return;
        switch (args[1]){
            case "추가": manager.AddLore(); break;
            case "삭제": manager.DeleteLore(); break;
            case "삽입", "변경": manager.InsertLore(); break;
            default: sendMessage("messages.do.lore.usage"); break;
        }
    }
    public void SetItemAttribute(){
        if (CheckArgsLength(2, "messages.do.attribute.usage")) return;
        switch (args[1]){
            case "목록": sendMessage("messages.do.attribute.list"); break;
            case "기본데미지","기본공격속도": manager.SetBaseAttribute(); break;
            case "초기화": manager.ClearAttribute(); break;
            case "추가": manager.SetAttribute(); break;
            default: sendMessage("messages.do.attribute.usage"); break;
        }
    }
    public void SetPotionPassive(){
        if (CheckArgsLength(2, "messages.do.potion_passive.usage")) {
            sender.sendMessage(Arrays.toString(EffectType.values())); return;
        }
        switch (args[1]){
            case "추가": manager.SetPotionPassiveAdd(); break;
            case "제거": manager.SetPotionPassiveRemove(); break;
            case "확인": manager.SetPotionPassiveCheck(); break;
            default: sendMessage("messages.do.potion_passive_usage"); break;
        }
    }
    public void SetPotionAttack(){
        if (CheckArgsLength(2, "messages.do.potion_attack.usage")) {
            sender.sendMessage(Arrays.toString(EffectType.values())); return;
        }
        switch (args[1]){
            case "추가": manager.SetPotionAttackAdd(); break;
            case "제거": manager.SetPotionAttackRemove(); break;
            case "확인": manager.SetPotionAttackCheck(); break;
            default: sendMessage("messages.do.potion_attack_usage"); break;
        }
    }
    private void SetEquip(){
        if (CheckArgsLength(2, "messages.do.equip.usage")) return;
        switch (args[1]){
            case "부위": manager.SetEquipSlot(); break;
            case "소리": manager.SetEquipSound(); break;
            case "모델": manager.SetEquipModel(); break;
            default: sendMessage("messages.do.equip.usage");
        }
    }
    private void SetDurability(){
        if (CheckArgsLength(2, "messages.do.durability.usage")) return;
        switch (args[1]){
            case "무한": manager.SetUnbreakable(); break;
            case "최대": manager.SetMaxDurability(); break;
            case "감소": manager.SetCurrentDurability(); break;
            default: sendMessage("messages.do.durability.usage"); break;
        }
    }
    private void SetPotion(){
        if (CheckArgsLength(2, "messages.do.potion.usage")) return;
        switch (args[1]){
            case "제거": manager.RemovePotion(); break;
            default: manager.AddPotion(); break;
        }
    }
    private void SetFood(){
        if (CheckArgsLength(2, "messages.do.food.usage")) return;
        switch (args[1]){
            case "회복량": manager.SetFoodNutrition(); break;
            case "포만도": manager.SetFoodSaturation(); break;
            case "항상": manager.SetFoodAlways(); break;
            default: sendMessage("messages.do.food.usage"); break;
        }
    }
    private void SetConsume(){
        if (CheckArgsLength(2, "messages.do.consume.usage")) return;
        switch (args[1]){
            case "설정": manager.SetConsumable(); break;
            case "시간": manager.SetConsumeSeconds(); break;
            case "소리": manager.SetConsumeSound(); break;
            case "애니메이션": manager.SetConsumeAnim(); break;
            case "파티클": manager.SetConsumeParticle(); break;
            case "포션": SetConsumePotionEffect(); break;
            case "포션해제": SetConsumePotionRemoveEffect(); break;
            case "소리효과": manager.SetConsumeSoundEffect(); break;
            case "이동거리": manager.SetConsumeTelepotationEffectDistance(); break;
            case "모든포션해제": manager.SetConsumeClearAllEffect(); break;
            default: sendMessage("messages.do.consume.usage"); break;
        }
    }
    private void SetCooldown(){
        if (CheckArgsLength(2, "messages.do.cooldown.usage")) return;
        switch (args[1]){
            case "설정": manager.SetCooldownSeconds(); break;
            case "그룹": manager.SetCooldownGroup(); break;
            default: sendMessage("messages.do.cooldown.usage"); break;
        }

    }
    private void SetConsumePotionEffect(){
        if (CheckArgsLength(3, "messages.do.consume.usage")) return;
        switch (args[2]){
            case "추가": manager.AddConsumePotionEffect(); break;
            case "제거": manager.RemoveConsumePotionEffect(); break;
            case "확률": manager.SetConsumePotionEffectChance(); break;
            default: sendMessage("messages.do.consume.usage"); break;
        }
    }
    private void SetConsumePotionRemoveEffect(){
        if (CheckArgsLength(3, "messages.do.consume.usage")) return;
        switch (args[2]){
            case "추가": manager.AddConsumePotionRemoveEffect(); break;
            case "제거": manager.RemoveConsumePotionRemoveEffect(); break;
            default: sendMessage("messages.do.consume.usage"); break;
        }
    }
}
