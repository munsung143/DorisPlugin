package org.exam.dorisPlugin;

import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EffectType {
    흡수(0, PotionEffectType.ABSORPTION),
    흉조(1, PotionEffectType.BAD_OMEN),
    실명(2, PotionEffectType.BLINDNESS),
    앵무조개의숨결(3, PotionEffectType.BREATH_OF_THE_NAUTILUS),
    전달체의힘(4, PotionEffectType.CONDUIT_POWER),
    어둠(5, PotionEffectType.DARKNESS),
    돌고래의은혜(6, PotionEffectType.DOLPHINS_GRACE),
    화염저항(7, PotionEffectType.FIRE_RESISTANCE),
    발광(8, PotionEffectType.GLOWING),
    성급함(9, PotionEffectType.HASTE),
    생명력강화(10, PotionEffectType.HEALTH_BOOST),
    마을의영웅(11, PotionEffectType.HERO_OF_THE_VILLAGE),
    허기(12, PotionEffectType.HUNGER),
    감염(13, PotionEffectType.INFESTED),
    즉시피해(14, PotionEffectType.INSTANT_DAMAGE),
    즉시회복(15, PotionEffectType.INSTANT_HEALTH),
    투명(16, PotionEffectType.INVISIBILITY),
    점프강화(17, PotionEffectType.JUMP_BOOST),
    공중부양(18, PotionEffectType.LEVITATION),
    행운(19, PotionEffectType.LUCK),
    채굴피로(20, PotionEffectType.MINING_FATIGUE),
    멀미(21, PotionEffectType.NAUSEA),
    야간투시(22, PotionEffectType.NIGHT_VISION),
    점액질(23, PotionEffectType.OOZING),
    독(24, PotionEffectType.POISON),
    약탈흉조(25, PotionEffectType.RAID_OMEN),
    재생(26, PotionEffectType.REGENERATION),
    저항(27, PotionEffectType.RESISTANCE),
    포화(28, PotionEffectType.SATURATION),
    느린낙하(29, PotionEffectType.SLOW_FALLING),
    구속(30, PotionEffectType.SLOWNESS),
    신속(31, PotionEffectType.SPEED),
    힘(32, PotionEffectType.STRENGTH),
    시험흉조(33, PotionEffectType.TRIAL_OMEN),
    불운(34, PotionEffectType.UNLUCK),
    수중호흡(35, PotionEffectType.WATER_BREATHING),
    나약함(36, PotionEffectType.WEAKNESS),
    직조(37, PotionEffectType.WEAVING),
    바람충전(38, PotionEffectType.WIND_CHARGED),
    위더(39, PotionEffectType.WITHER);



    private int code;
    private PotionEffectType type;
    private static final Map<String, Integer> To_Code = new HashMap<>();
    private static final List<PotionEffectType> To_Type = new ArrayList<>();
    private static final List<String> To_Name = new ArrayList<>();
    static {
        for (EffectType s : values()) {
            To_Code.put(s.name(), s.code);
            To_Type.add(s.type);
            To_Name.add(s.name());
        }
    }
    private EffectType(int code, PotionEffectType type){
        this.code = code;
        this.type = type;
    }
    public static int GetCode(String name){
        return To_Code.get(name);
    }
    public static boolean HasCode(String name){
        return To_Code.containsKey(name);
    }
    public static PotionEffectType GetType(int code){
        return To_Type.get(code);
    }
    public static String GetName(int code){
        return To_Name.get(code);
    }
}
