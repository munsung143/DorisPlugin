package org.exam.dorisPlugin;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class DamageTestCommandExecutor implements CommandExecutor {
    public static Map<UUID, Integer> testTypeMap;

    public DamageTestCommandExecutor() {
        this.testTypeMap = new HashMap<>();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p && p.isOp())) {
            sender.sendMessage("오피만 사용 가능합니다.");
            return true;
        }
        UUID uuid = p.getUniqueId();
        Integer val = testTypeMap.get(p.getUniqueId());
        if (val == null || val == 0){
            testTypeMap.put(uuid ,1);
            p.sendMessage("§b딜 테스트 활성화");
        }
        else if (val == 1){
            testTypeMap.put(uuid ,2);
            p.sendMessage("§b짧은 딜 테스트 활성화");
        }
        else if (val == 2){
            testTypeMap.put(uuid ,0);
            p.sendMessage("§b딜 테스트 비활성화");
        }
        return true;
    }
}
