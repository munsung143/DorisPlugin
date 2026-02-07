package org.exam.dorisPlugin;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.view.builder.InventoryViewBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RandomTeleport implements Listener {
    private static final Map<UUID, Boolean> playerSneakState = new HashMap<>();
    private static final Component title = new TextFormatBuilder("&2메뉴").Build();

    @EventHandler
    public void OnSneak(PlayerToggleSneakEvent event){
        playerSneakState.put(event.getPlayer().getUniqueId(), event.isSneaking());
    }
    @EventHandler
    public void OnSwap(PlayerSwapHandItemsEvent event){
        Player player = event.getPlayer();
        if (!player.getWorld().getName().equals("spawn")) return;
        Boolean b = playerSneakState.get(player.getUniqueId());
        if (b != null && b){
            Inventory menu =  Bukkit.createInventory(new MenuInventoryHolder(), 27, title);
            ItemStack item = new ItemStack(Material.GRASS_BLOCK, 1);
            ItemMeta meta = item.getItemMeta();
            meta.customName(new TextFormatBuilder("&a야생 이동").Build());
            item.setItemMeta(meta);
            menu.setItem(13, item);
            player.openInventory(menu);
        }
    }

    @EventHandler
    public void OnExit(PlayerQuitEvent event){
        playerSneakState.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void OnClick(InventoryClickEvent event){
        InventoryHolder holder =  event.getView().getTopInventory().getHolder();
        if (holder instanceof MenuInventoryHolder){
            event.setCancelled(true);
            if (event.getSlot() == 13){
                event.getView().close();
                TeleportPlayer(event.getView().getPlayer());
            }
        }
    }

    private void TeleportPlayer(HumanEntity player){
        World world = Bukkit.getWorld("world");
        int x = (int)((Math.random() - 0.5) * 10000);
        int z = (int)((Math.random() - 0.5) * 10000);
        int y = 62; // 바다 높이
        if (world.getBlockAt(x, y, z).getType() == Material.AIR){
            for (y = y; y > -64; y--){
                if (world.getBlockAt(x, y, z).getType() != Material.AIR){
                    break;
                }
            }
        }
        else {
            for (y = y; y < 200; y++){
                if (world.getBlockAt(x, y, z).getType() == Material.AIR){
                    break;
                }
            }
        }
        Location loc = new Location(world, x, y, z);
        player.teleport(loc);
    }

}
