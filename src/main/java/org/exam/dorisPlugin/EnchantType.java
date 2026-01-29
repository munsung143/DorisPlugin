package org.exam.dorisPlugin;

import java.util.HashMap;
import java.util.Map;

public enum EnchantType {
    친수성("aqua_affinity"),
    살충("bane_of_arthropods"),
    귀속저주("binding_curse"),
    폭발보호("blast_protection"),
    격파("breach"),
    집전("channeling"),
    육중("density"),
    물갈퀴("depth_strider"),
    효율("efficiency"),
            가벼운착지("feather_falling"),
            발화("fire_aspect"),
            화염보호("fire_protection"),
            화염("flame"),
            행운("fortune"),
            차가운걸음("frost_walker"),
            찌르기("impaling"),
            무한("infinity"),
            밀치기("knockback"),
            약탈("looting"),
            충성("loyalty"),
            바다의행운("luck_of_the_sea"),
            돌진("lunge"),
            미끼("lure"),
            수선("mending"),
            다중발사("multishot"),
            관통("piercing"),
            힘("power"),
            발사체보호("projectile_protection"),
            보호("protection"),
            밀어내기("punch"),
            빠른장전("quick_charge"),
            호흡("respiration"),
            급류("riptide"),
            날카로움("sharpness"),
            섬세한손길("silk_touch"),
            강타("smite"),
            영혼가속("soul_speed"),
    휩쓸기("sweeping_edge"),
    신속한잠행("swift_sneak"),
            가시("thorns"),
            내구성("unbreaking"),
            소실("vanishing_curse"),
            돌풍("wind_burst");

    private String key;
    private static final Map<String, String> BY_KEY = new HashMap<>();
    static {
        for (EnchantType s : values()) {
            BY_KEY.put(s.name(), s.key);
        }
    }
    private EnchantType(String key){
        this.key = key;
    }
    public String GetKey(){
        return key;
    }
    public static boolean ContainsKey(String str){
        return BY_KEY.containsKey(str);
    }
    public static String GetValue(String str){
        return BY_KEY.get(str);
    }
}
