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
                case "모델": modelComp(); break;
                case "포션": potionComp(); break;
                case "방지" : preventComp(); break;
                case "숨김" : itemFlagComp(); break;

                case "음식" : foodComp(); break;
                case "착용버프" : potionPassiveComp(); break;
                case "공격버프" : potionAttackComp(); break;
                case "사용버프" : potionUseComp(); break;
                case "소비" : consumeComp(); break;
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
        tabComplete.add("사용버프");
        tabComplete.add("착용");
        tabComplete.add("모델");
        tabComplete.add("내구도");
        tabComplete.add("포션");
        tabComplete.add("음식");
        tabComplete.add("소비");
        tabComplete.add("쿨타임");
        tabComplete.add("잔여물");
        tabComplete.add("방지");
        tabComplete.add("랜덤데미지");
        tabComplete.add("숨김");
        tabComplete.add("인첸트빛");
        tabComplete.add("설치방지");
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
    private void consumeComp(){
        if (length == 2){
            tabComplete.add("설정");
            tabComplete.add("시간");
            tabComplete.add("소리");
            tabComplete.add("애니메이션");
            tabComplete.add("파티클");
            tabComplete.add("포션");
            tabComplete.add("포션해제");
            tabComplete.add("소리효과");
            tabComplete.add("이동거리");
            tabComplete.add("모든포션해제");
        }
        else if (length >= 3 && compArgs[1].equals("포션")){
            if (length == 3){
                tabComplete.add("추가");
                tabComplete.add("제거");
                tabComplete.add("확률");
            }
        }
        else if (length >= 3 && compArgs[1].equals("포션해제")){
            if (length == 3){
                tabComplete.add("추가");
                tabComplete.add("제거");
            }
        }
    }
    private void foodComp(){
        if (length == 2){
            tabComplete.add("회복량");
            tabComplete.add("포만도");
            tabComplete.add("항상");
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
    private void potionPassiveComp(){
        if (length == 2){
            tabComplete.add("추가");
            tabComplete.add("제거");
            tabComplete.add("확인");
        }
        else if (length >= 3 && compArgs[1].equals("추가")){
            if (length == 3){
                tabComplete.add("갑옷");
                tabComplete.add("오른손");
                tabComplete.add("왼손");
            }
            else if (length == 4) {
                for (EffectType e : EffectType.values()){
                    tabComplete.add(e.toString());
                }
            }
            else if (length == 5){
                tabComplete.add("<레벨>");
            }
        }
        else if (length >= 3 && compArgs[1].equals("제거")){
            if (length == 3){
                tabComplete.add("갑옷");
                tabComplete.add("오른손");
                tabComplete.add("왼손");
            }
            else if (length == 4) {
                for (EffectType e : EffectType.values()){
                    tabComplete.add(e.toString());
                }
            }
        }
    }
    private void potionAttackComp(){
        if (length == 2){
            tabComplete.add("추가");
            tabComplete.add("제거");
            tabComplete.add("확인");
        }
        else if (length >= 3 && compArgs[1].equals("추가")){
            if (length == 3){
                tabComplete.add("갑옷");
                tabComplete.add("오른손");
                tabComplete.add("왼손");
            }
            else if (length == 4) {
                for (EffectType e : EffectType.values()){
                    tabComplete.add(e.toString());
                }
            }
            else if (length == 5){
                tabComplete.add("<레벨>");
            }
            else if (length == 6){
                tabComplete.add("<지속시간>");
            }
            else if (length == 7){
                tabComplete.add("<확률>");
            }
        }
        else if (length >= 3 && compArgs[1].equals("제거")){
            if (length == 3){
                tabComplete.add("갑옷");
                tabComplete.add("오른손");
                tabComplete.add("왼손");
            }
            else if (length == 4) {
                for (EffectType e : EffectType.values()){
                    tabComplete.add(e.toString());
                }
            }
        }
    }
    private void potionUseComp(){
        if (length == 2){
            tabComplete.add("추가");
            tabComplete.add("제거");
            tabComplete.add("내구도");
            tabComplete.add("확인");
        }
        else if (length >= 3 && compArgs[1].equals("추가")){
            if (length == 3){
                for (EffectType e : EffectType.values()){
                    tabComplete.add(e.toString());
                }
            }
            else if (length == 4){
                tabComplete.add("<레벨>");
            }
            else if (length == 5){
                tabComplete.add("<지속시간>");
            }
            else if (length == 6){
                tabComplete.add("<확률>");
            }
            else if (length == 7){
                tabComplete.add("<쿨타임>");
            }
            else if (length == 8){
                tabComplete.add("0");
                tabComplete.add("1");
            }
        }
        else if (length >= 3 && compArgs[1].equals("제거")){
            if (length == 3){
                for (EffectType e : EffectType.values()){
                    tabComplete.add(e.toString());
                }
            }
        }
        else if (length >= 3 && compArgs[1].equals("내구도")){
            if (length == 3){
                tabComplete.add("<내구도>");
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
    private void itemFlagComp(){
        if (length == 2){
            for (ItemFlagType e : ItemFlagType.values()){
                tabComplete.add(e.toString());
            }
        }
    }
}
