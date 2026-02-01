package org.exam.dorisPlugin;

import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum UseAnimationType {
    블록(ItemUseAnimation.BLOCK),
    활(ItemUseAnimation.BOW),
    브러시(ItemUseAnimation.BRUSH),
    번들(ItemUseAnimation.BUNDLE),
    석궁(ItemUseAnimation.CROSSBOW),
    마시기(ItemUseAnimation.DRINK),
    먹기(ItemUseAnimation.EAT),
    없음(ItemUseAnimation.NONE),
    창(ItemUseAnimation.SPEAR),
    망원경(ItemUseAnimation.SPYGLASS),
    염소뿔(ItemUseAnimation.TOOT_HORN),
    삼지창(ItemUseAnimation.TRIDENT);
    private ItemUseAnimation animation;
    private UseAnimationType(ItemUseAnimation animation){
        this.animation = animation;
    }
    private static final Map<String, ItemUseAnimation> To_Anim = new HashMap<>();
    static {
        for (UseAnimationType t : values()) {
            To_Anim.put(t.name(), t.animation);
        }
    }
    public static ItemUseAnimation GetAnimation(String str){
        return To_Anim.get(str);
    }
}
