package org.exam.dorisPlugin;

import java.util.HashMap;
import java.util.Map;

public enum AttributeType {
    방어력("armor"),
            방어강도("armor_toughness"),
            데미지("attack_damage"),
            넉백("attack_knockback"),
            공격속도("attack_speed"),
            파괴속도("block_break_speed"),
            블록범위("block_interaction_range"),
            발화시간("burning_time"),
            카메라거리("camera_distance"),
            개체범위("entity_interaction_range"),
            폭발넉백저항("explosion_knockback_resistance"),
            낙뎀증가율("fail_damage_multiplier"),
            비행속도("flying_speed"),
            추적거리("follow_range"),
            중력("gravity"),
            점프력("jump_strength"),
            넉백저항("knockback_resistance"),
            행운("luck"),
            최대흡수("max_absortion"),
            최대체력("max_health"),
            채광효율("mining_efficiency"),
            이동효율("movement_efficiency"),
            이동속도("movement_speed"),
            낙뎀무효높이("safe_fall_distance"),
            크기("scale"),
            웅크림속도("sneaking_speed"),
            소환("spawn_reinforcements"),
            걸음높이("step_height"),
            수중채광("submerged_mining_speed"),
            휩쓸기피해("sweeping_damage_ratio"),
            유혹거리("tempt_range"),
            수중이동효율("water_movement_efficiency"),
            위치수신거리("waypoint_receive_range"),
            위치송신거리("waypoint_transmit_range");

    private String key;
    private static final Map<String, String> BY_KEY = new HashMap<>();
    static {
        for (AttributeType s : values()) {
            BY_KEY.put(s.name(), s.key);
        }
    }
    private AttributeType(String key){
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
