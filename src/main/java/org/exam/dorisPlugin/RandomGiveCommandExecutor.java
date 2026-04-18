package org.exam.dorisPlugin;

import io.papermc.paper.entity.PlayerGiveResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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

public class RandomGiveCommandExecutor  implements CommandExecutor {
    private Map<UUID, Long> userCooltimeMap;

    public RandomGiveCommandExecutor() {
        this.userCooltimeMap = new HashMap<>();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("플레이어만 사용 가능합니다.");
            return true;
        }
        Player player = (Player)sender;
        boolean canSend = false;
        int remain = 0;
        if (userCooltimeMap.containsKey(player.getUniqueId())){
            long last = userCooltimeMap.get(player.getUniqueId());
            long current = System.currentTimeMillis();
            int passingSeconds = (int)((current - last) / 1000);
            if (passingSeconds >= 30) canSend = true;
            else remain = 30 - passingSeconds;
        }
        else{
            canSend = true;
        }
        if (args.length < 1) {
            sender.sendMessage("§l사용법: /랜덤추첨 개수");
            sender.sendMessage("§b오피, 자신을 제외한 랜덤 유저에게 손에 든 아이템을 개수만큼 뿌립니다.");
            sender.sendMessage("§b30초의 쿨타임을 가집니다.");
            sender.sendMessage("남은 시간 : " + (canSend ? "§a바로 사용 가능" : remain + "초"));
            return true;
        }
        if (!canSend){
            sender.sendMessage("§c아직 사용할 수 없습니다.");
            sender.sendMessage("§c남은 시간 : " + remain + "초");
            return true;
        }
        Send(player, args);
        return true;
    }
    public void Send(Player player, String[] args){
        Integer amount = PluginUtil.parseInt(args[0], 1, 64);
        if (amount == null) {
            player.sendMessage("§c1 이상 64 이하의 정확한 수량을 입력하세요");
            return;
        }
        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (handItem.getType().isAir()){
            player.sendMessage("§c손에 든 아이템이 없음");
            return;
        }
        int handAmount = handItem.getAmount();
        if (handItem.getAmount() < amount){
            player.sendMessage("§c작성한 수량보다 현재 아이템의 수량이 적습니다.");
            return;
        }
        List<? extends Player> players = Bukkit.getOnlinePlayers().stream()
                .filter(p -> !p.equals(player))
                .filter(p -> !p.isOp()).toList();
        if (players.isEmpty()){
            player.sendMessage("§c대상 플레이어가 없습니다.");
            return;
        }
        userCooltimeMap.put(player.getUniqueId(), System.currentTimeMillis());
        int randomIndex = ThreadLocalRandom.current().nextInt(players.size());
        ItemStack toGive = handItem.clone();
        handItem.setAmount(handAmount - amount);
        toGive.setAmount(amount);
        Player target = players.get(randomIndex);
        Map<Integer, ItemStack> left = target.getInventory().addItem(toGive);
        if (!left.isEmpty()){
            for (ItemStack remain : left.values()) {
                target.getWorld().dropItemNaturally(target.getLocation(), remain);
            }
        }
        Component comp = Component.text("§b"+ player.getName() + "§f님의 랜덤 추첨을 통해 ")
                .append(toGive.displayName().hoverEvent(toGive.asHoverEvent()))
                .append(Component.text(" " + amount+ " 개가 §b" + target.getName() + " §f님에게 전달되었습니다."));
        Bukkit.broadcast(comp);
        if (!left.isEmpty()){
            target.sendMessage("§c인벤토리에 채워지지 않은 아이템이 드롭되었습니다!");
        }
    }
}
