package org.exam.dorisPlugin;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.components.EquippableComponent;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.exam.dorisPlugin.enums.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemSetter {

    private ItemStack targetItem;
    private ItemStack defaultItem;
    private ItemSyncData syncData;
    private ItemSetResult result;

    public ItemSetter(ItemStack item) {
        this.targetItem = null;
        defaultItem = null;
        result = new ItemSetResult(false, "§c아이템 세팅이 정상적으로 처리되지 않음", false);
        syncData = null;

        SetTargetItem(item);
    }

    public boolean isNull() {
        return targetItem == null;
    }

    public void SetTargetItem(ItemStack item) {
        if (item == null || item.isEmpty() || item.getType().isAir()) {
            return;
        }
        defaultItem = item;
        PersistentDataContainer cont = item.getItemMeta().getPersistentDataContainer().get(DorisKeys.sync, PersistentDataType.TAG_CONTAINER);
        if (cont == null) {
            this.targetItem = item;
        } else {
            String code = cont.get(DorisKeys.sync_code, PersistentDataType.STRING);
            syncData = DataSerializer.itemSyncMap.get(code);
            this.targetItem = syncData.item;
            result.sync = true;
        }
    }

    private void updateSyncVersion() {
        if (result.sync) {
            syncData.version++;
            DataSerializer.itemSyncSerialize();
            DataSerializer.SaveItemSyncData();
        }
    }

    private int ParseInt(String arg, int min, int max, int def) {
        return PluginUtil.parseInt2(arg, min, max, def);
    }

    private double ParseDouble(String arg, double min, double max, double def) {
        return PluginUtil.parseDouble2(arg, min, max, def);
    }

    private float ParseFloat(String arg, float min, float max, float def) {
        return PluginUtil.parseFloat2(arg, min, max, def);
    }

    private boolean ParseBool(String arg, boolean def) {
        return PluginUtil.parseBool2(arg, def);
    }

    public ItemSetResult SetItemName(String name) {
        if (name == null) return result.setFail("§c아이템 이름이 입력되지 않음");
        TextFormatBuilder builder = new TextFormatBuilder(name);
        Component cmp = builder.Build();
        targetItem.editMeta(meta -> meta.customName(cmp));
        updateSyncVersion();
        return result.setSuccess("§a아이템에 이름이 적용됨");
    }

    public ItemSetResult AddLore(String lore) {
        if (lore == null) return result.setFail("§c추가할 설명이 입력되지 않음");
        TextFormatBuilder builder = new TextFormatBuilder(lore);
        Component cmp = builder.Build();
        targetItem.editMeta(meta -> {
            List<Component> existLore = meta.hasLore() ? meta.lore() : new ArrayList<>();
            existLore.addLast(cmp);
            meta.lore(existLore);
        });
        updateSyncVersion();
        return result.setSuccess("§a아이템에 새로운 설명이 추가됨");
    }

    public ItemSetResult DeleteLore(String indexArg) {
        targetItem.editMeta(meta -> {
            List<Component> existLore = meta.lore();
            if (existLore == null) {
                result.setFail("§c아이템에 설명이 없음");
                return;
            }
            int i = PluginUtil.parseInt2(indexArg, 0, existLore.size() - 1, existLore.size() - 1);
            existLore.remove(i);
            meta.lore(existLore);

            result.setSuccess(new StringBuilder("§a아이템의 ").append(i).append("번째 줄 설명 제거").toString());
        });
        updateSyncVersion();
        return result;
    }

    public ItemSetResult InsertLore(String indexArg, String lore) {
        TextFormatBuilder builder = new TextFormatBuilder(lore);
        Component cmp = builder.Build();
        targetItem.editMeta(meta -> {
            List<Component> existLore = meta.lore();
            if (existLore == null) {
                result.setFail("§c아이템에 설명이 없음");
                return;
            }

            int i = PluginUtil.parseInt2(indexArg, 0, existLore.size() - 1, existLore.size() - 1);
            existLore.add(i, cmp);
            meta.lore(existLore);

            result.setSuccess(new StringBuilder("§a아이템의 ").append(i).append("번째 줄에 설명 삽입됨").toString());
        });
        updateSyncVersion();
        return result;
    }

    public ItemSetResult ChangeLore(String indexArg, String lore) {
        TextFormatBuilder builder = new TextFormatBuilder(lore);
        Component cmp = builder.Build();

        targetItem.editMeta(meta -> {
            List<Component> existLore = meta.lore();
            if (existLore == null) {
                result.setFail("§c아이템에 설명이 없음");
                return;
            }

            int i = PluginUtil.parseInt2(indexArg, 0, existLore.size() - 1, existLore.size() - 1);
            existLore.add(i, cmp);
            existLore.remove(i + 1);
            meta.lore(existLore);

            result.setSuccess(new StringBuilder("§a아이템의 ").append(i).append("번째 줄 설명이 변경됨").toString());
        });

        updateSyncVersion();
        return result;
    }

    public ItemSetResult SetEnchantment(String type, String levelArg) {
        if (!EnchantType.ContainsKey(type)) {
            return result.setFail(new StringBuilder("§c존재하지 않는 인챈트 타입: ").append(type).toString());
        }

        Enchantment enchant = Registries.Enchantment.get(NamespacedKey.minecraft(EnchantType.GetValue(type)));
        int level = PluginUtil.parseInt2(levelArg, 0, 32767, 1);

        if (syncData.asyncEnchant){
            targetItem = defaultItem;
            result.sync = false;
        }
        targetItem.addUnsafeEnchantment(enchant, level);

        updateSyncVersion();
        return result.setSuccess(new StringBuilder("§a아이템에 ")
                .append(type).append(" ").append(level).append("레벨 인챈트 적용됨").toString());
    }

    public ItemSetResult SetAttribute(String typeArg, String slotArg, String operArg, String amountArg) {

        if (!AttributeType.ContainsKey(typeArg)) {
            return result.setFail("§c존재하지 않는 속성 타입입니다.");
        }

        Attribute attribute = Registries.Attribute.get(NamespacedKey.minecraft(AttributeType.GetValue(typeArg)));
        EquipmentSlotGroup slot = SlotType.GetSlotGroup(slotArg);
        if (slot == null) {
            return result.setFail("§c존재하지 않는 슬롯 타입입니다.");
        }

        AttributeModifier.Operation operation;
        switch (operArg) {
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
                return result.setFail("§c잘못된 연산 방식입니다. (더하기, 곱하기, 누적곱하기)");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(slot.toString());
        sb.append('.');
        sb.append(operation.name().toLowerCase());
        NamespacedKey key = NamespacedKey.fromString(sb.toString(), Main.plugin);

        double amount = PluginUtil.parseDouble2(amountArg, Double.MIN_VALUE, Double.MAX_VALUE, 1);
        double targetAmount = (operation != AttributeModifier.Operation.ADD_NUMBER) ? amount / 100 : amount;

        targetItem.editMeta(meta -> {
            AttributeModifier modifier = new AttributeModifier(key, targetAmount, operation, slot);
            meta.removeAttributeModifier(attribute, modifier);

            if (targetAmount != 0) {
                meta.addAttributeModifier(attribute, modifier);
                result.setSuccess(new StringBuilder("§a속성 적용됨: ")
                        .append(typeArg).append(" (").append(amountArg).append(")").toString());
            } else {
                result.setSuccess(new StringBuilder("§a속성 제거됨: ").append(typeArg).toString());
            }
        });

        updateSyncVersion();
        return result;
    }

    public ItemSetResult ClearAttribute() {

        targetItem.editMeta(meta -> {
            meta.setAttributeModifiers(null);
            result.setSuccess("§a아이템의 모든 속성이 초기화됨");
        });

        updateSyncVersion();
        return result;
    }

    public ItemSetResult SetBaseAttribute(String typeArg, String amountArg) {
        double amount = PluginUtil.parseDouble2(amountArg, Double.MIN_VALUE, Double.MAX_VALUE, 1);

        targetItem.editMeta(meta -> {
            Attribute attribute = Attribute.ATTACK_DAMAGE;
            String str = "base_attack_damage";

            if (typeArg.equalsIgnoreCase("기본공격속도")) {
                attribute = Attribute.ATTACK_SPEED;
                str = "base_attack_speed";
            }

            NamespacedKey key = NamespacedKey.fromString(str, null);
            AttributeModifier modifier = new AttributeModifier(key, amount, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND);

            meta.removeAttributeModifier(attribute, modifier);

            if (amount != 0) {
                meta.addAttributeModifier(attribute, modifier);
                result.setSuccess(new StringBuilder("§a아이템의 ").append(typeArg).append("이(가) ").append(amount).append("(으)로 설정됨").toString());
            } else {
                result.setSuccess(new StringBuilder("§a아이템의 ").append(typeArg).append(" 속성이 제거됨").toString());
            }
        });

        updateSyncVersion();
        return result;
    }

    public ItemSetResult SetMaxStack(String amountArg) {
        int amount = PluginUtil.parseInt2(amountArg, 1, 64, 1);

        targetItem.editMeta(meta -> {
            meta.setMaxStackSize(amount);
            result.setSuccess(new StringBuilder("§a아이템의 최대 중첩 개수가 ").append(amount).append("개로 설정됨").toString());
        });

        updateSyncVersion();
        return result;
    }

    public ItemSetResult SetItemColor(String colorArg) {

        targetItem.editMeta(meta -> {
            Color color = null;
            if (!colorArg.equalsIgnoreCase("초기화")) {
                if (!PluginUtil.IsRGB(colorArg)) {
                    result.setFail("§c잘못된 RGB 형식입니다. (예: 255,255,255)");
                    return;
                }
                color = PluginUtil.RGBToColor(colorArg);
            }

            if (meta instanceof LeatherArmorMeta leatherMeta) {
                leatherMeta.setColor(color);
                result.setSuccess(color == null ? "§a가죽 갑옷 색상이 초기화됨" : "§a가죽 갑옷 색상이 적용됨");
            } else if (meta instanceof PotionMeta potionMeta) {
                potionMeta.setColor(color);
                result.setSuccess(color == null ? "§a포션 색상이 초기화됨" : "§a포션 색상이 적용됨");
            } else {
                result.setFail("§c색상을 변경할 수 없는 아이템입니다.");
            }
        });

        // result의 마지막 상태가 성공일 때만 updateSyncVersion 호출
        if (result.success) {
            updateSyncVersion();
        }

        return result;
    }

    public ItemSetResult AddPotionPassive(String slotArg, String typeArg, String levelArg) {
        String key = "potion_attack_mainhand";
        switch (slotArg) {
            case "갑옷":
                key = "potion_attack_armor";
                break;
            case "왼손":
                key = "potion_attack_offhand";
                break;
            default:
                break;
        }

        NamespacedKey namespacedKey = NamespacedKey.fromString(key, Main.plugin);

        if (!EffectType.HasCode(typeArg)) {
            return result.setFail(new StringBuilder("§c존재하지 않는 포션 효과입니다: ").append(typeArg).toString());
        }

        int effectCode = EffectType.GetCode(typeArg);
        int level = PluginUtil.parseInt2(levelArg, 0, 32767, 0);

        targetItem.editMeta(meta -> {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            List<int[]> list = container.get(namespacedKey, PersistentDataType.LIST.integerArrays());

            if (list == null) {
                list = new ArrayList<>();
            } else {
                list = new ArrayList<>(list);
            }

            int[] pair = (new int[]{effectCode, level});
            int i = 0;
            for (i = 0; i < list.size(); i++) {
                if (list.get(i)[0] == effectCode) {
                    list.get(i)[1] = level;
                    break;
                }
            }

            if (i == list.size()) {
                list.add(pair);
            }

            container.set(namespacedKey, PersistentDataType.LIST.integerArrays(), list);

            result.setSuccess(new StringBuilder("§a아이템 [")
                    .append(slotArg).append("] 슬롯에 ")
                    .append(typeArg).append(" ").append(level + 1).append("레벨 효과가 적용됨").toString());
        });

        updateSyncVersion();
        return result;
    }

    public ItemSetResult RemovePotionPassive(String slotArg, String typeArg) {
        String key = "potion_attack_mainhand";
        switch (slotArg) {
            case "갑옷":
                key = "potion_attack_armor";
                break;
            case "왼손":
                key = "potion_attack_offhand";
                break;
            default:
                break;
        }

        NamespacedKey namespacedKey = NamespacedKey.fromString(key, Main.plugin);

        if (!EffectType.HasCode(typeArg)) {
            return result.setFail(new StringBuilder("§c존재하지 않는 포션 효과입니다: ").append(typeArg).toString());
        }

        int effectCode = EffectType.GetCode(typeArg);

        targetItem.editMeta(meta -> {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            List<int[]> list = container.get(namespacedKey, PersistentDataType.LIST.integerArrays());

            if (list == null) {
                result.setFail("§c해당 슬롯에 설정된 효과가 없습니다.");
                return;
            }

            int i = 0;
            boolean found = false;
            for (i = 0; i < list.size(); i++) {
                if (list.get(i)[0] == effectCode) {
                    list.remove(i);
                    found = true;
                    break;
                }
            }

            if (!found) {
                result.setFail(new StringBuilder("§c제거할 효과(").append(typeArg).append(")를 찾을 수 없습니다.").toString());
                return;
            }

            if (list.isEmpty()) {
                container.remove(namespacedKey);
            } else {
                container.set(namespacedKey, PersistentDataType.LIST.integerArrays(), list);
            }

            result.setSuccess(new StringBuilder("§a아이템 [")
                    .append(slotArg).append("] 슬롯에서 ")
                    .append(typeArg).append(" 효과를 제거함").toString());
        });

        if (result.success) updateSyncVersion();
        return result;
    }

    public ItemSetResult AddPotionAttack(String slotArg, String typeArg, String levelArg, String durationArg, String chanceArg) {
        String key = "potion_attack_mainhand";
        switch (slotArg) {
            case "갑옷":
                key = "potion_attack_armor";
                break;
            case "왼손":
                key = "potion_attack_offhand";
                break;
            default:
                break;
        }

        NamespacedKey namespacedKey = NamespacedKey.fromString(key, Main.plugin);

        if (!EffectType.HasCode(typeArg)) {
            return result.setFail(new StringBuilder("§c존재하지 않는 포션 효과입니다: ").append(typeArg).toString());
        }

        int effectCode = EffectType.GetCode(typeArg);
        int level = PluginUtil.parseInt2(levelArg, 0, 32767, 0);
        int duration = PluginUtil.parseInt2(durationArg, 1, Integer.MAX_VALUE, 100);
        int chance = PluginUtil.parseInt2(chanceArg, 1, 1000, 100);

        targetItem.editMeta(meta -> {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            List<int[]> list = container.get(namespacedKey, PersistentDataType.LIST.integerArrays());

            if (list == null) {
                list = new ArrayList<>();
            } else {
                list = new ArrayList<>(list);
            }

            int[] pair = (new int[]{effectCode, level, duration, chance});
            int i = 0;
            for (i = 0; i < list.size(); i++) {
                if (list.get(i)[0] == effectCode) {
                    list.get(i)[1] = level;
                    list.get(i)[2] = duration;
                    list.get(i)[3] = chance;
                    break;
                }
            }

            if (i == list.size()) {
                list.add(pair);
            }

            container.set(namespacedKey, PersistentDataType.LIST.integerArrays(), list);

            result.setSuccess(new StringBuilder("§a아이템 [")
                    .append(slotArg).append("] 슬롯에 ")
                    .append(typeArg).append(" 공격 효과가 적용됨 (")
                    .append(chance / 10.0).append("% 확률)").toString());
        });

        updateSyncVersion();
        return result;
    }

    public ItemSetResult RemovePotionAttack(String slotArg, String typeArg) {
        String key = "potion_attack_mainhand";
        switch (slotArg) {
            case "갑옷":
                key = "potion_attack_armor";
                break;
            case "왼손":
                key = "potion_attack_offhand";
                break;
            default:
                break;
        }

        NamespacedKey namespacedKey = NamespacedKey.fromString(key, Main.plugin);

        if (!EffectType.HasCode(typeArg)) {
            return result.setFail(new StringBuilder("§c존재하지 않는 포션 효과입니다: ").append(typeArg).toString());
        }

        int effectCode = EffectType.GetCode(typeArg);

        targetItem.editMeta(meta -> {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            List<int[]> list = container.get(namespacedKey, PersistentDataType.LIST.integerArrays());

            if (list == null) {
                result.setFail("§c해당 슬롯에 설정된 공격 효과가 없습니다.");
                return;
            }

            int i = 0;
            boolean found = false;
            for (i = 0; i < list.size(); i++) {
                if (list.get(i)[0] == effectCode) {
                    list.remove(i);
                    found = true;
                    break;
                }
            }

            if (!found) {
                result.setFail(new StringBuilder("§c제거할 공격 효과(").append(typeArg).append(")를 찾을 수 없습니다.").toString());
                return;
            }

            if (list.isEmpty()) {
                container.remove(namespacedKey);
            } else {
                container.set(namespacedKey, PersistentDataType.LIST.integerArrays(), list);
            }

            result.setSuccess(new StringBuilder("§a아이템 [")
                    .append(slotArg).append("] 슬롯에서 ")
                    .append(typeArg).append(" 공격 효과를 제거함").toString());
        });

        if (result.success) updateSyncVersion();
        return result;
    }

    public ItemSetResult SetEquipSlot(String slotArg) {
        EquipmentSlot slot = SlotType.GetSlot(slotArg);

        if (slot == null) {
            return result.setFail("§c존재하지 않는 슬롯 타입입니다.");
        }

        targetItem.editMeta(meta -> {
            EquippableComponent ec = meta.getEquippable();
            ec.setSlot(slot);
            meta.setEquippable(ec);
            result.setSuccess(new StringBuilder("§a아이템 착용 슬롯이 ").append(slotArg).append("(으)로 설정됨").toString());
        });

        updateSyncVersion();
        return result;
    }

    public ItemSetResult SetEquipSound(String soundArg) {
        Sound sound = Registries.SoundEvent.get(NamespacedKey.minecraft(soundArg));

        if (sound == null) {
            return result.setFail(new StringBuilder("§c존재하지 않는 사운드입니다: ").append(soundArg).toString());
        }

        targetItem.editMeta(meta -> {
            EquippableComponent ec = meta.getEquippable();
            ec.setEquipSound(sound);
            meta.setEquippable(ec);
            result.setSuccess(new StringBuilder("§a아이템 착용 사운드가 ").append(soundArg).append("(으)로 설정됨").toString());
        });

        updateSyncVersion();
        return result;
    }

    public ItemSetResult SetEquipModel(String modelArg) {
        targetItem.editMeta(meta -> {
            EquippableComponent ec = meta.getEquippable();
            ec.setModel(NamespacedKey.minecraft(modelArg));
            meta.setEquippable(ec);
            result.setSuccess(new StringBuilder("§a아이템 착용 모델이 ").append(modelArg).append("(으)로 설정됨").toString());
        });

        updateSyncVersion();
        return result;
    }

    public ItemSetResult SetModel(String modelArg) {
        targetItem.editMeta(meta -> {
            meta.setItemModel(NamespacedKey.minecraft(modelArg));
            result.setSuccess(new StringBuilder("§a아이템 모델이 ").append(modelArg).append("(으)로 설정됨").toString());
        });

        updateSyncVersion();
        return result;
    }

    public ItemSetResult SetUnbreakable() {
        targetItem.editMeta(meta -> {
            boolean currentState = meta.isUnbreakable();
            meta.setUnbreakable(!currentState);
            result.setSuccess(!currentState ? "§a아이템이 이제 파괴되지 않음" : "§a아이템이 이제 파괴될 수 있음");
        });

        updateSyncVersion();
        return result;
    }

    public ItemSetResult SetMaxDurability(String durabilityArg) {
        int amount = PluginUtil.parseInt2(durabilityArg, 0, Integer.MAX_VALUE, 10);

        targetItem.editMeta(meta -> {
            if (meta instanceof Damageable damageable) {
                damageable.setMaxDamage(amount);
                result.setSuccess(new StringBuilder("§a아이템의 최대 내구도 수치가 ").append(amount).append("(으)로 설정됨").toString());
            } else {
                result.setFail("§c내구도 설정 실패 (내구도가 없는 아이템)");
            }
        });

        if (result.success) updateSyncVersion();
        return result;
    }

    public ItemSetResult SetCurrentDurability(String durabilityArg) {
        int amount = PluginUtil.parseInt2(durabilityArg, 0, Integer.MAX_VALUE, 10);

        // 동기화 방지
        targetItem = defaultItem;
        result.sync = false;

        targetItem.editMeta(meta -> {
            if (meta instanceof Damageable damageable) {
                damageable.setDamage(amount);
                result.setSuccess(new StringBuilder("§a아이템의 데미지 수치가 ").append(amount).append("(으)로 설정됨").toString());
            } else {
                result.setFail("§c내구도 설정 실패 (내구도가 없는 아이템)");
            }
        });

        if (result.success) updateSyncVersion();
        return result;
    }

    public ItemSetResult AddPotion(String typeArg, String levelArg, String durationArg) {
        if (!EffectType.HasCode(typeArg)) {
            return result.setFail(new StringBuilder("§c존재하지 않는 포션 효과입니다: ").append(typeArg).toString());
        }

        PotionEffectType type = EffectType.GetType(EffectType.GetCode(typeArg));
        int level = PluginUtil.parseInt2(levelArg, 0, 32767, 0);
        int duration = PluginUtil.parseInt2(durationArg, 1, Integer.MAX_VALUE, 100);

        targetItem.editMeta(meta -> {
            if (meta instanceof PotionMeta potionMeta) {
                PotionEffect effect = new PotionEffect(type, duration, level);
                potionMeta.addCustomEffect(effect, true);
                result.setSuccess(new StringBuilder("§a포션에 ")
                        .append(typeArg).append(" ").append(level + 1).append("레벨 효과가 추가됨").toString());
            } else {
                result.setFail("§c포션 효과 추가 실패 (포션 아이템이 아님)");
            }
        });

        if (result.success) updateSyncVersion();
        return result;
    }

    public ItemSetResult RemovePotion(String typeArg) {
        if (!EffectType.HasCode(typeArg)) {
            return result.setFail(new StringBuilder("§c존재하지 않는 포션 효과입니다: ").append(typeArg).toString());
        }

        PotionEffectType type = EffectType.GetType(EffectType.GetCode(typeArg));

        targetItem.editMeta(meta -> {
            if (meta instanceof PotionMeta potionMeta) {
                if (potionMeta.hasCustomEffect(type)) {
                    potionMeta.removeCustomEffect(type);
                    result.setSuccess(new StringBuilder("§a포션에서 ").append(typeArg).append(" 효과를 제거함").toString());
                } else {
                    result.setFail("§c해당 포션에 제거할 효과가 없습니다.");
                }
            } else {
                result.setFail("§c포션 효과 제거 실패 (포션 아이템이 아님)");
            }
        });

        if (result.success) updateSyncVersion();
        return result;
    }

    public ItemSetResult SetConsumable() {
        if (!targetItem.hasData(DataComponentTypes.CONSUMABLE)) {
            Consumable.Builder builder = Consumable.consumable();
            targetItem.setData(DataComponentTypes.CONSUMABLE, builder);
            result.setSuccess("§a아이템이 이제 소모 가능함");
        } else {
            targetItem.unsetData(DataComponentTypes.CONSUMABLE);
            result.setSuccess("§a아이템의 소모 기능이 제거됨");
        }

        updateSyncVersion();
        return result;
    }

    public Consumable ConsumableSettingPlate() {
        if (!targetItem.hasData(DataComponentTypes.CONSUMABLE)) {
            return null;
        }
        return targetItem.getData(DataComponentTypes.CONSUMABLE);

    }

    public ItemSetResult SetConsumeSeconds(String secondArg) {
        Consumable consumable = ConsumableSettingPlate();
        if (consumable == null) {
            return result.setFail("§c소모 시간 설정 실패 (소모성 컴포넌트 없음)");
        }

        Consumable.Builder builder = consumable.toBuilder();
        float second = PluginUtil.parseFloat2(secondArg, 0, Float.MAX_VALUE, 3);
        builder.consumeSeconds(second);
        targetItem.setData(DataComponentTypes.CONSUMABLE, builder);

        result.setSuccess(new StringBuilder("§a아이템 소모 시간이 ").append(second).append("초로 설정됨").toString());
        updateSyncVersion();
        return result;
    }

    public ItemSetResult SetConsumeSound(String soundArg) {
        Consumable consumable = ConsumableSettingPlate();
        if (consumable == null) {
            return result.setFail("§c소모 사운드 설정 실패 (소모성 컴포넌트 없음)");
        }

        Consumable.Builder builder = consumable.toBuilder();
        builder.sound(NamespacedKey.minecraft(soundArg));
        targetItem.setData(DataComponentTypes.CONSUMABLE, builder);

        result.setSuccess(new StringBuilder("§a아이템 소모 사운드가 ").append(soundArg).append("(으)로 설정됨").toString());
        updateSyncVersion();
        return result;
    }

    public ItemSetResult SetConsumeAnim(String animArg) {
        Consumable consumable = ConsumableSettingPlate();
        if (consumable == null) {
            return result.setFail("§c소모 애니메이션 설정 실패 (소모성 컴포넌트 없음)");
        }

        ItemUseAnimation animation = UseAnimationType.GetAnimation(animArg);
        if (animation == null) {
            return result.setFail(new StringBuilder("§c존재하지 않는 애니메이션 타입: ").append(animArg).toString());
        }

        Consumable.Builder builder = consumable.toBuilder();
        builder.animation(animation);
        targetItem.setData(DataComponentTypes.CONSUMABLE, builder);

        result.setSuccess(new StringBuilder("§a아이템 소모 애니메이션이 ").append(animArg).append("(으)로 설정됨").toString());
        updateSyncVersion();
        return result;
    }

    public ItemSetResult SetConsumeParticle() {
        Consumable consumable = ConsumableSettingPlate();
        if (consumable == null) {
            return result.setFail("§c소모 파티클 설정 실패 (소모성 컴포넌트 없음)");
        }

        Consumable.Builder builder = consumable.toBuilder();
        boolean newState = !consumable.hasConsumeParticles();
        builder.hasConsumeParticles(newState);
        targetItem.setData(DataComponentTypes.CONSUMABLE, builder);

        result.setSuccess(newState ? "§a아이템 소모 시 파티클이 표시됨" : "§a아이템 소모 시 파티클이 표시되지 않음");
        updateSyncVersion();
        return result;
    }

    public ItemSetResult SetConsumeSoundEffect(String soundStr) {
        Consumable consumable = ConsumableSettingPlate();
        if (consumable == null) {
            return result.setFail("§c소모 효과 설정 실패 (소모성 컴포넌트 없음)");
        }

        Consumable.Builder builder = consumable.toBuilder();
        List<ConsumeEffect> effects = new ArrayList<>(consumable.consumeEffects());

        for (ConsumeEffect e : effects) {
            if (e instanceof ConsumeEffect.PlaySound ce) {
                effects.remove(ce);
                builder.effects(effects);
                break;
            }
        }

        builder.addEffect(ConsumeEffect.playSoundConsumeEffect(NamespacedKey.minecraft(soundStr)));
        targetItem.setData(DataComponentTypes.CONSUMABLE, builder);

        result.setSuccess(new StringBuilder("§a아이템 소모 효과 사운드가 ").append(soundStr).append("(으)로 추가됨").toString());
        updateSyncVersion();
        return result;
    }

    public ItemSetResult SetConsumeClearAllEffect() {
        Consumable consumable = ConsumableSettingPlate();
        if (consumable == null) {
            return result.setFail("§c효과 제거 설정 실패 (소모성 컴포넌트 없음)");
        }

        Consumable.Builder builder = consumable.toBuilder();
        List<ConsumeEffect> effects = new ArrayList<>(consumable.consumeEffects());

        for (ConsumeEffect e : effects) {
            if (e instanceof ConsumeEffect.PlaySound ce) {
                effects.remove(ce);
                builder.effects(effects);
                break;
            }
        }

        builder.addEffect(ConsumeEffect.clearAllStatusEffects());
        targetItem.setData(DataComponentTypes.CONSUMABLE, builder);

        result.setSuccess("§a아이템 소모 시 모든 상태 효과를 제거하도록 설정됨");
        updateSyncVersion();
        return result;
    }

    public ItemSetResult SetConsumeTelepotationEffectDistance(String distanceArg) {
        Consumable consumable = ConsumableSettingPlate();
        if (consumable == null) {
            return result.setFail("§c텔레포트 효과 설정 실패 (소모성 컴포넌트 없음)");
        }

        Consumable.Builder builder = consumable.toBuilder();
        List<ConsumeEffect> effects = new ArrayList<>(consumable.consumeEffects());
        float distance = PluginUtil.parseFloat2(distanceArg, 0, Float.MAX_VALUE, 0);

        for (ConsumeEffect e : effects) {
            if (e instanceof ConsumeEffect.TeleportRandomly ce) {
                effects.remove(ce);
                builder.effects(effects);
                break;
            }
        }

        builder.addEffect(ConsumeEffect.teleportRandomlyEffect(distance));
        targetItem.setData(DataComponentTypes.CONSUMABLE, builder);

        result.setSuccess(new StringBuilder("§a아이템 소모 시 랜덤 텔레포트 거리: ").append(distance).append(" 블록으로 설정됨").toString());
        updateSyncVersion();
        return result;
    }

    public ItemSetResult AddConsumePotionEffect(String typeArg, String levelArg, String durationArg) {
        Consumable consumable = ConsumableSettingPlate();
        if (consumable == null) {
            return result.setFail("§c소모 포션 효과 추가 실패 (소모성 컴포넌트 없음)");
        }

        if (!EffectType.HasCode(typeArg)) {
            return result.setFail(new StringBuilder("§c존재하지 않는 포션 효과입니다: ").append(typeArg).toString());
        }

        Consumable.Builder builder = consumable.toBuilder();
        List<ConsumeEffect> effects = new ArrayList<>(consumable.consumeEffects());

        PotionEffectType type = EffectType.GetType(EffectType.GetCode(typeArg));
        int level = PluginUtil.parseInt2(levelArg, 0, 32767, 0);
        int duration = PluginUtil.parseInt2(durationArg, 1, Integer.MAX_VALUE, 100);
        PotionEffect newPotion = new PotionEffect(type, duration, level);

        List<PotionEffect> potionEffects = new ArrayList<>();
        float prob = 1.0f;

        // 기존 ApplyStatusEffects 추출 및 제거
        for (int i = 0; i < effects.size(); i++) {
            if (effects.get(i) instanceof ConsumeEffect.ApplyStatusEffects ce) {
                potionEffects = new ArrayList<>(ce.effects());
                prob = ce.probability();
                effects.remove(i);
                break;
            }
        }

        // 동일한 타입의 기존 효과 제거 (업데이트 처리)
        potionEffects.removeIf(p -> p.getType().equals(type));

        // 새 효과 추가
        potionEffects.add(newPotion);

        // 빌더에 반영
        builder.effects(effects); // 기존 효과들(다른 타입) 유지
        builder.addEffect(ConsumeEffect.applyStatusEffects(potionEffects, prob));
        targetItem.setData(DataComponentTypes.CONSUMABLE, builder);

        result.setSuccess(new StringBuilder("§a아이템 소모 효과에 ")
                .append(typeArg).append(" ").append(level + 1).append("레벨이 추가됨").toString());

        updateSyncVersion();
        return result;
    }

    public ItemSetResult RemoveConsumePotionEffect(String typeArg) {
        Consumable consumable = ConsumableSettingPlate();
        if (consumable == null) {
            return result.setFail("§c소모 포션 효과 제거 실패 (소모성 컴포넌트 없음)");
        }

        if (!EffectType.HasCode(typeArg)) {
            return result.setFail(new StringBuilder("§c존재하지 않는 포션 효과입니다: ").append(typeArg).toString());
        }

        Consumable.Builder builder = consumable.toBuilder();
        List<ConsumeEffect> effects = new ArrayList<>(consumable.consumeEffects());
        PotionEffectType type = EffectType.GetType(EffectType.GetCode(typeArg));

        List<PotionEffect> potionEffects = new ArrayList<>();
        float prob = 1.0f;
        boolean foundCategory = false;

        // 기존 ApplyStatusEffects 추출 및 리스트에서 제거
        for (int i = 0; i < effects.size(); i++) {
            if (effects.get(i) instanceof ConsumeEffect.ApplyStatusEffects ce) {
                potionEffects = new ArrayList<>(ce.effects());
                prob = ce.probability();
                effects.remove(i);
                foundCategory = true;
                break;
            }
        }

        if (!foundCategory) {
            return result.setFail("§c아이템 소모 효과에 등록된 포션 효과가 없습니다.");
        }

        // 특정 포션 타입 제거
        boolean removed = potionEffects.removeIf(p -> p.getType().equals(type));

        if (!removed) {
            return result.setFail(new StringBuilder("§c제거할 효과(").append(typeArg).append(")를 찾을 수 없습니다.").toString());
        }

        // 남은 효과들 유지 (다른 타입의 ConsumeEffect들)
        builder.effects(effects);

        // 남은 포션 효과가 있다면 다시 추가
        if (!potionEffects.isEmpty()) {
            builder.addEffect(ConsumeEffect.applyStatusEffects(potionEffects, prob));
        }

        targetItem.setData(DataComponentTypes.CONSUMABLE, builder);

        result.setSuccess(new StringBuilder("§a아이템 소모 효과에서 ").append(typeArg).append(" 효과를 제거함").toString());

        updateSyncVersion();
        return result;
    }

    public ItemSetResult SetConsumePotionEffectChance(String chanceArg) {
        Consumable consumable = ConsumableSettingPlate();
        if (consumable == null) {
            return result.setFail("§c포션 효과 확률 설정 실패 (소모성 컴포넌트 없음)");
        }

        Consumable.Builder builder = consumable.toBuilder();
        List<ConsumeEffect> effects = new ArrayList<>(consumable.consumeEffects());

        float prob = PluginUtil.parseFloat2(chanceArg, 0, Float.MAX_VALUE, 100);
        float targetProb = prob / 100f;

        List<PotionEffect> potionEffects = new ArrayList<>();
        boolean isExist = false;

        // 기존 ApplyStatusEffects 추출 및 제거
        for (int i = 0; i < effects.size(); i++) {
            if (effects.get(i) instanceof ConsumeEffect.ApplyStatusEffects ce) {
                potionEffects = new ArrayList<>(ce.effects());
                effects.remove(i);
                isExist = true;
                break;
            }
        }

        if (!isExist) {
            return result.setFail("§c설정된 포션 효과가 없어 확률을 변경할 수 없습니다.");
        }

        // 변경된 확률로 다시 추가
        builder.effects(effects);
        builder.addEffect(ConsumeEffect.applyStatusEffects(potionEffects, targetProb));
        targetItem.setData(DataComponentTypes.CONSUMABLE, builder);

        result.setSuccess(new StringBuilder("§a소모 포션 효과 발생 확률이 ")
                .append(prob).append("%로 설정됨").toString());

        updateSyncVersion();
        return result;
    }

    public ItemSetResult AddConsumePotionRemoveEffect(String typeArg) {
        Consumable consumable = ConsumableSettingPlate();
        if (consumable == null) {
            return result.setFail("§c소모 시 효과 제거 설정 실패 (소모성 컴포넌트 없음)");
        }

        if (!EffectType.HasCode(typeArg)) {
            return result.setFail(new StringBuilder("§c존재하지 않는 포션 효과입니다: ").append(typeArg).toString());
        }

        Consumable.Builder builder = consumable.toBuilder();
        List<ConsumeEffect> effects = new ArrayList<>(consumable.consumeEffects());
        PotionEffectType type = EffectType.GetType(EffectType.GetCode(typeArg));

        Collection<TypedKey<PotionEffectType>> potionEffects = new ArrayList<>();
        boolean isExist = false;

        // 기존 RemoveStatusEffects 추출 및 리스트에서 제거
        for (int i = 0; i < effects.size(); i++) {
            if (effects.get(i) instanceof ConsumeEffect.RemoveStatusEffects ce) {
                potionEffects = new ArrayList<>(ce.removeEffects().values());
                effects.remove(i);
                isExist = true;
                break;
            }
        }

        // 중복 체크
        if (isExist) {
            for (TypedKey<PotionEffectType> p : potionEffects) {
                if (p.key().toString().equals(type.key().toString())) {
                    return result.setFail(new StringBuilder("§c이미 제거 목록에 등록된 효과입니다: ").append(typeArg).toString());
                }
            }
        }

        // 새로운 제거 효과 추가
        potionEffects.add(TypedKey.create(RegistryKey.MOB_EFFECT, type.key()));
        RegistryKeySet<PotionEffectType> potionEffectsKeySet = RegistrySet.keySet(RegistryKey.MOB_EFFECT, potionEffects);

        // 기존 다른 종류의 효과들과 함께 다시 빌드
        builder.effects(effects);
        builder.addEffect(ConsumeEffect.removeEffects(potionEffectsKeySet));
        targetItem.setData(DataComponentTypes.CONSUMABLE, builder);

        result.setSuccess(new StringBuilder("§a아이템 소모 시 제거될 효과에 ")
                .append(typeArg).append(" 효과가 추가됨").toString());

        updateSyncVersion();
        return result;
    }

    public ItemSetResult RemoveConsumePotionRemoveEffect(String typeArg) {
        Consumable consumable = ConsumableSettingPlate();
        if (consumable == null) {
            return result.setFail("§c효과 제거 목록 수정 실패 (소모성 컴포넌트 없음)");
        }

        if (!EffectType.HasCode(typeArg)) {
            return result.setFail(new StringBuilder("§c존재하지 않는 포션 효과입니다: ").append(typeArg).toString());
        }

        Consumable.Builder builder = consumable.toBuilder();
        List<ConsumeEffect> effects = new ArrayList<>(consumable.consumeEffects());
        PotionEffectType type = EffectType.GetType(EffectType.GetCode(typeArg));

        Collection<TypedKey<PotionEffectType>> potionEffects = new ArrayList<>();
        boolean foundCategory = false;

        // 기존 RemoveStatusEffects 추출 및 리스트에서 일시 제거
        for (int i = 0; i < effects.size(); i++) {
            if (effects.get(i) instanceof ConsumeEffect.RemoveStatusEffects ce) {
                potionEffects = new ArrayList<>(ce.removeEffects().values());
                effects.remove(i);
                foundCategory = true;
                break;
            }
        }

        if (!foundCategory) {
            return result.setFail("§c설정된 효과 제거 목록이 없습니다.");
        }

        // 특정 효과 키 생성 및 제거 시도
        TypedKey<PotionEffectType> targetKey = TypedKey.create(RegistryKey.MOB_EFFECT, type.key());
        boolean removed = potionEffects.removeIf(p -> p.key().toString().equals(targetKey.key().toString()));

        if (!removed) {
            return result.setFail(new StringBuilder("§c제거 목록에서 해당 효과(").append(typeArg).append(")를 찾을 수 없습니다.").toString());
        }

        // 다른 종류의 ConsumeEffect 유지
        builder.effects(effects);

        // 남은 제거 대상 효과가 있다면 다시 추가
        if (!potionEffects.isEmpty()) {
            RegistryKeySet<PotionEffectType> potionEffectsKeySet = RegistrySet.keySet(RegistryKey.MOB_EFFECT, potionEffects);
            builder.addEffect(ConsumeEffect.removeEffects(potionEffectsKeySet));
        }

        targetItem.setData(DataComponentTypes.CONSUMABLE, builder);

        result.setSuccess(new StringBuilder("§a아이템 소모 시 제거될 효과 목록에서 ")
                .append(typeArg).append(" 효과를 삭제함").toString());

        updateSyncVersion();
        return result;
    }

    public ItemSetResult SetFoodNutrition(String valueArg) {
        int value = PluginUtil.parseInt2(valueArg, 0, 32767, 1);

        targetItem.editMeta(meta -> {
            FoodComponent food = meta.getFood();
            food.setNutrition(value);
            meta.setFood(food);
            result.setSuccess(new StringBuilder("§a아이템의 허기 회복량이 ").append(value).append("(으)로 설정됨").toString());
        });

        updateSyncVersion();
        return result;
    }

    public ItemSetResult SetFoodSaturation(String valueArg) {
        float value = PluginUtil.parseFloat2(valueArg, 0, 32767, 1);

        targetItem.editMeta(meta -> {
            FoodComponent food = meta.getFood();
            food.setSaturation(value);
            meta.setFood(food);
            result.setSuccess(new StringBuilder("§a아이템의 포만감 회복량이 ").append(value).append("(으)로 설정됨").toString());
        });

        updateSyncVersion();
        return result;
    }

    public ItemSetResult SetFoodAlways() {
        targetItem.editMeta(meta -> {
            FoodComponent food = meta.getFood();
            boolean newState = !food.canAlwaysEat();
            food.setCanAlwaysEat(newState);
            meta.setFood(food);
            result.setSuccess(newState ? "§a이제 배가 불러도 이 아이템을 먹을 수 있음" : "§a이제 배가 부르면 이 아이템을 먹을 수 없음");
        });

        updateSyncVersion();
        return result;
    }

    public ItemSetResult SetCooldownSeconds(String coolArg) {
        float value = PluginUtil.parseFloat2(coolArg, 0, Float.MAX_VALUE, 0);

        targetItem.editMeta(meta -> {
            UseCooldownComponent cool = meta.getUseCooldown();
            cool.setCooldownSeconds(value);
            meta.setUseCooldown(cool);
            result.setSuccess(new StringBuilder("§a아이템 재사용 대기시간이 ").append(value).append("초로 설정됨").toString());
        });

        updateSyncVersion();
        return result;
    }

    public ItemSetResult SetCooldownGroup(String group) {
        targetItem.editMeta(meta -> {
            UseCooldownComponent cool = meta.getUseCooldown();
            NamespacedKey key = NamespacedKey.fromString(group, Main.plugin);
            cool.setCooldownGroup(key);
            meta.setUseCooldown(cool);
            result.setSuccess(new StringBuilder("§a아이템 쿨다운 그룹이 '").append(group).append("'(으)로 설정됨").toString());
        });

        updateSyncVersion();
        return result;
    }

    public ItemSetResult SetRemainder(ItemStack remainder) {
        targetItem.editMeta(meta -> {
            meta.setUseRemainder(remainder);
            String itemName = (remainder == null) ? "없음" : remainder.getType().toString();
            result.setSuccess(new StringBuilder("§a아이템 사용 후 남을 아이템이 ").append(itemName).append("(으)로 설정됨").toString());
        });

        updateSyncVersion();
        return result;
    }

    public ItemSetResult SetPrevent(String typeArg) {
        targetItem.editMeta(meta -> {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            Integer mask = container.get(DorisKeys.prevent, PersistentDataType.INTEGER);
            if (mask == null) mask = 0;

            Integer shift = FunctionalBlockType.name2Mask(typeArg);
            if (shift == null) {
                result.setFail(new StringBuilder("§c존재하지 않는 방지 타입입니다: ").append(typeArg).toString());
                return;
            }

            int newMask;
            String status;

            if (shift == -1) {
                if ((mask & shift) == shift) {
                    newMask = 0;
                    status = "모든 상호작용 방지 해제";
                } else {
                    newMask = shift;
                    status = "모든 상호작용 방지 설정";
                }
            } else {
                newMask = mask ^ shift;
                boolean isSet = (newMask & shift) != 0;
                status = new StringBuilder(typeArg)
                        .append(" 방지 ")
                        .append(isSet ? "설정" : "해제")
                        .toString();
            }

            container.set(DorisKeys.prevent, PersistentDataType.INTEGER, newMask);
            result.setSuccess(new StringBuilder("§a아이템의 ").append(status).append(" 완료").toString());
        });

        if (result.success) updateSyncVersion();
        return result;
    }

    public ItemSetResult ApplyRandom(String randomKey){
        ItemMeta meta = targetItem.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(DorisKeys.random, PersistentDataType.STRING, randomKey);
        targetItem.setItemMeta(meta);
        updateSyncVersion();
        return result.setSuccess("§아이템에 랜덤 테이블이 적용 됨.");
    }
}
