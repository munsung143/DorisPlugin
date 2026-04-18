package org.exam.dorisPlugin;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.exam.dorisPlugin.enums.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemSettingCommandExecutor implements TabCompleter, CommandExecutor {

    private String[] compArgs;
    private List<String> tabComplete;
    private int length;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!sender.isOp()) return false;
        if (sender instanceof Player player){
            ItemSettingPersonal p = new ItemSettingPersonal(player, args);
            p.Execute();
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
        if (!sender.isOp()) return null;
        tabComplete = new ArrayList<>();
        length = args.length;
        compArgs = args;
        if (length == 1){
            defaultComp();
        }
        else if (length >= 2){
            switch (args[0]){
                case "인첸트": enchantComp(); break;
                case "설명": loreComp(); break;
                case "속성": attributeComp(); break;
                //case "착용버프": potionPassiveComp(); break;
                //case "공격버프": potionAttackComp(); break;
                //case "착용": equipComp();break;
                case "모델": modelComp(); break;
                //case "내구도": durabilityComp(); break;
                case "포션": potionComp(); break;
                //case "음식": foodComp(); break;
                //case "소비": consumeComp(); break;
                //case "쿨타임": cooldownComp(); break;
                case "방지" : preventComp(); break;
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
        if (length == 2){
            tabComplete.add("추가");
            tabComplete.add("삭제");
            tabComplete.add("제거");
            tabComplete.add("삽입");
            tabComplete.add("변경");
        }
    }
    private void attributeComp(){
        if (length == 2){
            tabComplete.add("추가");
            tabComplete.add("기본데미지");
            tabComplete.add("기본공격속도");
            tabComplete.add("초기화");
            tabComplete.add("목록");
        }
        else if (length >= 3 && compArgs[1].equals("추가")){
            if (length == 3){
                for (AttributeType a : AttributeType.values()){
                    tabComplete.add(a.toString());
                }
            }
            else if (length == 4) {
                for (SlotType a : SlotType.values()) {
                    tabComplete.add(a.toString());
                }
            }
            else if (length == 5){
                tabComplete.add("더하기");
                tabComplete.add("곱하기");
                tabComplete.add("누적곱하기");
            }

        }
    }
    private void potionComp(){
        if (length == 2) {
            tabComplete.add("추가");
            tabComplete.add("제거");
        }
        else if (length == 3){
            for (EffectType e : EffectType.values()){
                tabComplete.add(e.toString());
            }
        }
    }
    private void preventComp(){
        if (length == 2){
            for (FunctionalBlockType e : FunctionalBlockType.values()){
                tabComplete.add(e.toString());
            }
        }
    }
}
