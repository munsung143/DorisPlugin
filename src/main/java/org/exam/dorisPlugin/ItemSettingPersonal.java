package org.exam.dorisPlugin;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.exam.dorisPlugin.enums.EffectType;

import java.util.Arrays;
import java.util.List;

public class ItemSettingPersonal {

    Player sender;
    String[] args;
    ItemSetter set;
    ItemStack handItem;



    public ItemSettingPersonal(Player sender, String[] args){
        this.sender = sender;
        this.args = args;
    }
    private boolean CheckArgsLength(int index, String message){
        if (args.length > index){
            return true;
        }
        yamlMessage(message);
        return false;
    }
    private boolean CheckValidity(int index, String message){
        if (args.length > index){
            if (handItem.getType().isAir()){
                sender.sendMessage("아이템을 들어주세요");
                return false;
            }
            return true;
        }
        else{
            yamlMessage(message);
            return false;
        }
    }
    private void yamlMessage(String message){
        Main.sendMessage(message, sender);
    }
    private void handleResult(ItemSetResult result){
        sender.sendMessage(result.message);
        if (result.success && result.sync){
            sender.sendMessage("아이템 동기화 완료");
        }
    }
    public void Execute(){
        String message = "messages.do.usage";
        if (!CheckArgsLength(0, message)) return;
        handItem = sender.getInventory().getItemInMainHand();
        set = new ItemSetter(handItem);

        switch (args[0]){
            case "인첸트": if (CheckValidity(2, "messages.do.enchant.usage")){
                handleResult(set.SetEnchantment(args[1], args[2]));
            } break;
            case "이름": if (CheckValidity(1, "messages.do.name.usage")){
                String arg = PluginUtil.CombineRestArgstoString(args, 1);
                handleResult(set.SetItemName(arg));
            } break;
            case "설명": SetItemLore(1); break;
            case "속성": SetItemAttribute(1); break;
            case "색상": if (CheckValidity(1, "messages.do.color.usage")){
                handleResult(set.SetItemColor(args[1]));
            } break;
            case "스택": if (CheckValidity(1, "messages.do.stack.usage")){
                handleResult(set.SetMaxStack(args[1]));
            } break;
            case "착용버프": SetPotionPassive(1); break;
            case "공격버프": SetPotionAttack(1); break;
            case "사용버프": SetPotionUse(1); break;
            case "착용": SetEquip(1);break;
            case "모델": if (CheckValidity(1, "messages.do.model.usage")){
                handleResult(set.SetModel(args[1]));
            } break;
            case "내구도": SetDurability(1); break;
            case "포션": SetPotion(1); break;
            case "음식": SetFood(1); break;
            case "소비": SetConsume(1); break;
            case "쿨타임": SetCooldown(1); break;
            case "잔여물": handleResult(set.SetRemainder(((Player)sender).getInventory().getItemInOffHand())); break;
            case "방지" : if (CheckValidity(1, "messages.do.prevent.usage")){
                handleResult(set.SetPrevent(args[1]));
            } break;
            case "랜덤데미지" : if (CheckValidity(1, "messages.do.random_damage.usage")){
                handleResult(set.SetRandomDamage(args[1]));
            } break;
            case "숨김" : if (CheckValidity(1, "messages.do.hide.usage")){
                handleResult(set.SetItemFlag(args[1]));
            } break;
            case "인첸트빛" : handleResult(set.SetEnchantGlint()); break;
            case "설치방지" : handleResult(set.SetPreventPlace()); break;
            default: yamlMessage(message); break;
        }
    }
    private void SetItemLore(int req){
        String message = "messages.do.lore.usage";
        if (!CheckArgsLength(req, message)) return;
        switch (args[req]){
            case "추가": if (CheckValidity(req + 1, "messages.do.lore.usage")){
                String arg = PluginUtil.CombineRestArgstoString(args, req + 1);
                handleResult(set.AddLore(arg));
            } break;
            case "제거","삭제": if (CheckValidity(req + 1, "messages.do.lore.usage")){
                handleResult(set.DeleteLore(args[req + 1]));
            } break;
            case "삽입" : if (CheckValidity(req + 2, "messages.do.lore.usage")){
                String arg = PluginUtil.CombineRestArgstoString(args, req + 2);
                handleResult(set.InsertLore(args[req + 1], arg));
            } break;
            case "변경" : if (CheckValidity(req + 2, "messages.do.lore.usage")){
                String arg = PluginUtil.CombineRestArgstoString(args, req + 2);
                handleResult(set.ChangeLore(args[req + 1], arg));
            } break;
            default: yamlMessage(message); break;
        }
    }
    public void SetItemAttribute(int req){
        String message = "messages.do.attribute.usage";
        if (!CheckArgsLength(req, message)) return;
        switch (args[req]){ // args[1]에서 args[req]로 수정하여 인덱스 일관성 유지
            case "목록": yamlMessage("messages.do.attribute.list"); break;
            case "기본데미지","기본공격속도": if (CheckValidity(req + 1, "messages.do.attribute.usage")){
                handleResult(set.SetBaseAttribute(args[req], args[req + 1]));
            } break;
            case "초기화": if (CheckValidity(req, message)){
                handleResult(set.ClearAttribute());
            } break;
            case "추가": if (CheckValidity(req + 4, "messages.do.attribute.usage")){
                handleResult(set.SetAttribute(args[req + 1], args[req + 2], args[req + 3], args[req + 4]));
            } break;
            default: yamlMessage(message); break;
        }
    }

    public void SetPotionPassive(int req){
        String message = "messages.do.potion_passive.usage";
        if (!CheckArgsLength(req, message)) {
            sender.sendMessage(Arrays.toString(EffectType.values())); return;
        }
        switch (args[req]){ // args[1]에서 args[req]로 수정하여 인덱스 일관성 유지
            case "추가": if (CheckValidity(req + 3, "messages.do.potion_passive.usage")){
                handleResult(set.AddPotionPassive(args[req + 1], args[req + 2], args[req + 3]));
            } break;
            case "제거": if (CheckValidity(req + 2, "messages.do.potion_passive.usage")){
                handleResult(set.RemovePotionPassive(args[req + 1], args[req + 2]));
            } break;
            case "확인": PotionPassiveCheck(); break;
            default: yamlMessage(message); break;
        }
    }

    public void PotionPassiveCheck(){
        if (!CheckValidity(0, "")) return;
        PersistentDataContainer container = handItem.getItemMeta().getPersistentDataContainer();
        PassiveCheck("§b착용 효과:", "potion_passive_armor", container);
        PassiveCheck("§b오른손 효과:", "potion_passive_mainhand", container);
        PassiveCheck("§b왼손 효과:", "potion_passive_offhand", container);
    }
    private void PassiveCheck(String title, String keyName, PersistentDataContainer container){
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
    public void SetPotionAttack(int req){
        String message = "messages.do.potion_attack.usage";
        if (!CheckArgsLength(req, message)) {
            sender.sendMessage(Arrays.toString(EffectType.values())); return;
        }
        switch (args[req]){
            case "추가": if (CheckValidity(req + 5, "messages.do.potion_attack.usage")){
                handleResult(set.AddPotionAttack(args[req + 1], args[req + 2], args[req + 3], args[req + 4], args[req + 5]));
            } break;
            case "제거": if (CheckValidity(req + 2, "messages.do.potion_attack.usage")){
                handleResult(set.RemovePotionAttack(args[req + 1], args[req + 2]));
            } break;
            case "확인":  PotionAttackCheck(); break;
            default: yamlMessage(message); break;
        }
    }
    public void PotionAttackCheck(){
        if (!CheckValidity(0, "")) return;
        PersistentDataContainer container = handItem.getItemMeta().getPersistentDataContainer();
        AttackCheck("§b착용 효과:", "potion_attack_armor", container);
        AttackCheck("§b오른손 효과:", "potion_attack_mainhand", container);
        AttackCheck("§b왼손 효과:", "potion_attack_offhand", container);
    }
    private void AttackCheck(String title, String keyName, PersistentDataContainer container){
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
    public void SetPotionUse(int req){
        String message = "messages.do.potion_use.usage";
        if (!CheckArgsLength(req, message)) {
            sender.sendMessage(Arrays.toString(EffectType.values())); return;
        }
        switch (args[req]){
            case "추가": if (CheckValidity(req + 6, "messages.do.potion_use.usage")){
                handleResult(set.AddPotionUse(args[req + 1], args[req + 2], args[req + 3], args[req + 4], args[req + 5], args[req + 6]));
            } break;
            case "제거": if (CheckValidity(req + 1, "messages.do.potion_use.usage")){
                handleResult(set.RemovePotionUse(args[req + 1]));
            } break;
            case "내구도": if (CheckValidity(req + 1, "messages.do.potion_use.usage")){
                handleResult(set.SetPotionUseDurability(args[req + 1]));
            } break;
            case "확인": PotionUseCheck(); break;
            default: yamlMessage(message); break;
        }
    }
    public void PotionUseCheck(){
        if (!CheckValidity(0, "")) return;
        PersistentDataContainer loot = handItem.getItemMeta().getPersistentDataContainer().get(DorisKeys.potion_use, PersistentDataType.TAG_CONTAINER);
        if (loot == null){
            sender.sendMessage("없음");
        }
        else{
            var list = loot.get(DorisKeys.potion_use_list, PersistentDataType.LIST.integerArrays());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < list.size(); i++){
                sb.append(EffectType.GetName(list.get(i)[0]));
                sb.append(" 레벨: ");
                sb.append(list.get(i)[1]);
                sb.append(" 지속시간: ");
                sb.append(list.get(i)[2]);
                sb.append(" 확률: ");
                sb.append(list.get(i)[3]);
                sb.append(" 쿨타임: ");
                sb.append(list.get(i)[4]);
                sender.sendMessage(sb.toString());
                sb.setLength(0);
            }
        }
    }
    private void UseCheck(String title, String keyName, PersistentDataContainer container){
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

    private void SetEquip(int req){
        String message = "messages.do.equip.usage";
        if (!CheckArgsLength(req, message)) return;
        switch (args[req]){
            case "부위": if (CheckValidity(req + 1, message)){
                handleResult(set.SetEquipSlot(args[req + 1]));
            } break;
            case "소리": if (CheckValidity(req + 1, message)){
                handleResult(set.SetEquipSound(args[req + 1]));
            } break;
            case "모델": if (CheckValidity(req + 1, message)){
                handleResult(set.SetEquipModel(args[req + 1]));
            } break;
            default: yamlMessage(message); break;
        }
    }

    private void SetDurability(int req){
        String message = "messages.do.durability.usage";
        if (!CheckArgsLength(req, message)) return;
        switch (args[req]){
            case "무한": if (CheckValidity(req, message)){
                handleResult(set.SetUnbreakable());
            } break;
            case "최대": if (CheckValidity(req + 1, message)){
                handleResult(set.SetMaxDurability(args[req + 1]));
            } break;
            case "감소": if (CheckValidity(req + 1, message)){
                handleResult(set.SetCurrentDurability(args[req + 1]));
            } break;
            default: yamlMessage(message); break;
        }
    }

    private void SetPotion(int req){
        String message = "messages.do.potion.usage";
        if (!CheckArgsLength(req, message)) return;
        switch (args[req]){
            case "추가": if (CheckValidity(req + 3, message)){
                handleResult(set.AddPotion(args[req + 1], args[req + 2], args[req + 3]));
            } break;
            case "제거": if (CheckValidity(req + 1, message)){
                handleResult(set.RemovePotion(args[req + 1]));
            } break;
            default: yamlMessage(message); break;
        }
    }

    private void SetFood(int req){
        String message = "messages.do.food.usage";
        if (!CheckArgsLength(req, message)) return;
        switch (args[req]){
            case "회복량": if (CheckValidity(req + 1, message)){
                handleResult(set.SetFoodNutrition(args[req + 1]));
            } break;
            case "포만도": if (CheckValidity(req + 1, message)){
                handleResult(set.SetFoodSaturation(args[req + 1]));
            } break;
            case "항상": if (CheckValidity(req, message)){
                handleResult(set.SetFoodAlways());
            } break;
            default: yamlMessage(message); break;
        }
    }
    private void SetConsume(int req) {
        String message = "messages.do.consume.usage";
        if (!CheckArgsLength(req, message)) return;
        switch (args[req]) {
            case "설정": if (CheckValidity(req, message)) {
                handleResult(set.SetConsumable());
            } break;
            case "시간": if (CheckValidity(req + 1, message)) {
                handleResult(set.SetConsumeSeconds(args[req + 1]));
            } break;
            case "소리": if (CheckValidity(req + 1, message)) {
                handleResult(set.SetConsumeSound(args[req + 1]));
            } break;
            case "애니메이션": if (CheckValidity(req + 1, message)) {
                handleResult(set.SetConsumeAnim(args[req + 1]));
            } break;
            case "파티클": if (CheckValidity(req, message)) {
                handleResult(set.SetConsumeParticle());
            } break;
            case "포션": SetConsumePotionEffect(req + 1); break;
            case "포션해제": SetConsumePotionRemoveEffect(req + 1); break;
            case "소리효과": if (CheckValidity(req + 1, message)) {
                handleResult(set.SetConsumeSoundEffect(args[req + 1]));
            } break;
            case "이동거리": if (CheckValidity(req + 1, message)) {
                handleResult(set.SetConsumeTelepotationEffectDistance(args[req + 1]));
            } break;
            case "모든포션해제": handleResult(set.SetConsumeClearAllEffect()); break;
            default: yamlMessage(message); break;
        }
    }

    private void SetCooldown(int req) {
        String message = "messages.do.cooldown.usage";
        if (!CheckArgsLength(req, message)) return;
        switch (args[req]) {
            case "설정": if (CheckValidity(req + 1, message)) {
                handleResult(set.SetCooldownSeconds(args[req + 1]));
            } break;
            case "그룹": if (CheckValidity(req + 1, message)) {
                handleResult(set.SetCooldownGroup(args[req + 1]));
            } break;
            default: yamlMessage(message); break;
        }
    }

    private void SetConsumePotionEffect(int req) {
        String message = "messages.do.consume.usage";
        if (!CheckArgsLength(req, message)) return;
        switch (args[req]) { // args[2]에서 args[req]로 수정
            case "추가": if (CheckValidity(req + 3, message)) {
                handleResult(set.AddConsumePotionEffect(args[req + 1], args[req + 2], args[req + 3]));
            } break;
            case "제거": if (CheckValidity(req + 1, message)) {
                handleResult(set.RemoveConsumePotionEffect(args[req + 1]));
            } break;
            case "확률": if (CheckValidity(req + 1, message)) {
                handleResult(set.SetConsumePotionEffectChance(args[req + 1]));
            } break;
            default: yamlMessage(message); break;
        }
    }

    private void SetConsumePotionRemoveEffect(int req) {
        String message = "messages.do.consume.usage";
        if (!CheckArgsLength(req, message)) return;
        switch (args[req]) { // args[2]에서 args[req]로 수정
            case "추가": if (CheckValidity(req + 1, message)) {
                handleResult(set.AddConsumePotionRemoveEffect(args[req + 1]));
            } break;
            case "제거": if (CheckValidity(req + 1, message)) {
                handleResult(set.RemoveConsumePotionRemoveEffect(args[req + 1]));
            } break;
            default: yamlMessage(message); break;
        }
    }
}
