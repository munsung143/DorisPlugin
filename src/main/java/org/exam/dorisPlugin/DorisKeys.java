package org.exam.dorisPlugin;

import org.bukkit.NamespacedKey;

public final class DorisKeys {

    public static NamespacedKey random = NamespacedKey.fromString("random", Main.plugin);
    public static NamespacedKey prevent = NamespacedKey.fromString("prevent", Main.plugin);
    public static NamespacedKey randomDamage = NamespacedKey.fromString("random_damage", Main.plugin);

    public static NamespacedKey sync = NamespacedKey.fromString("sync", Main.plugin);
    public static NamespacedKey sync_version = NamespacedKey.fromString("sync:version");
    public static NamespacedKey sync_code = NamespacedKey.fromString("sync:code");

    public static NamespacedKey potion_use = NamespacedKey.fromString("potion_use", Main.plugin);
    public static NamespacedKey potion_use_code = NamespacedKey.fromString("potion_use:code");
    public static NamespacedKey potion_use_list = NamespacedKey.fromString("potion_use:list");
    public static NamespacedKey potion_use_dur = NamespacedKey.fromString("potion_use:durability");
    public static NamespacedKey potion_use_sneak = NamespacedKey.fromString("potion_use:sneak");

    public static NamespacedKey potion_passive_armor = NamespacedKey.fromString("potion_passive_armor", Main.plugin);
    public static NamespacedKey potion_passive_mainhand = NamespacedKey.fromString("potion_passive_mainhand", Main.plugin);
    public static NamespacedKey potion_passive_offhand = NamespacedKey.fromString("potion_passive_offhand", Main.plugin);

    public static NamespacedKey place_prevention = NamespacedKey.fromString("place_prevention", Main.plugin);


}
