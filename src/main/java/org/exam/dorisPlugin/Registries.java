package org.exam.dorisPlugin;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionEffectType;

public final class Registries {
    public final static Registry<EntityType> EntityType = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENTITY_TYPE);
    public final static Registry<PotionEffectType> MobEffect = RegistryAccess.registryAccess().getRegistry(RegistryKey.MOB_EFFECT);
    public final static Registry<Attribute> Attribute = RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE);
    public final static Registry<Enchantment> Enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
    public final static Registry<Sound> SoundEvent = RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT);
    public final static Registry<Material> Material = Registry.MATERIAL;
}
