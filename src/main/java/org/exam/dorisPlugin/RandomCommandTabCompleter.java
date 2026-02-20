package org.exam.dorisPlugin;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.inventory.ItemType;
import org.exam.dorisPlugin.enums.AttributeType;
import org.exam.dorisPlugin.enums.EffectType;
import org.exam.dorisPlugin.enums.EnchantType;
import org.exam.dorisPlugin.enums.FunctionalBlockType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RandomCommandTabCompleter implements TabCompleter {

    private Map<String , RandomTable> randomTableMap;
    private List<String> tabComplete;
    private int length;
    private String[] compArgs;

    public RandomCommandTabCompleter(Map<String, RandomTable> random){
        this.randomTableMap = random;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
        if (!sender.isOp()) return null;
        tabComplete = new ArrayList<>();
        length = args.length;
        compArgs = args;
        if (length == 1){
            defaultComp();
        }
        else if (length > 1){
            switch (args[0]){
                case "remove": removeTable(); break;
                case "apply": applyTable(); break;
                case "group": groupComp(); break;
                default: break;
            }
        }
        var filteredComplete = tabComplete.stream().filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()));
        return filteredComplete.collect(Collectors.toList());
    }

    private void defaultComp(){
        tabComplete.add("list");
        tabComplete.add("add");
        tabComplete.add("apply");
        tabComplete.add("remove");
        tabComplete.add("group");
    }
    private void removeTable(){
        if (length == 2){
            tabComplete.addAll(randomTableMap.keySet());
        }
    }
    private void applyTable(){
        if (length == 2){
            tabComplete.addAll(randomTableMap.keySet());
        }
    }
    private void groupComp(){
        if (length == 2) {
            tabComplete.addAll(randomTableMap.keySet());
        }
        else if (length > 2){
            if (length == 3){
                tabComplete.add("add");
                tabComplete.add("item");
                tabComplete.add("remove");
                tabComplete.add("weight");
                tabComplete.add("message");
            }
            else if (length > 3){
                switch (compArgs[2]){
                    case "item": groupIndex(); break;
                    case "remove": groupIndex(); break;
                    case "weight": groupIndex(); break;
                    case "message": groupIndex(); break;
                    default: break;
                }
            }
        }
    }
    private void groupIndex(){
        if (length == 4){
            if (!randomTableMap.containsKey(compArgs[1])) return;
            int i = 0;
            for (RandomGroup g : randomTableMap.get(compArgs[1]).groups){
                tabComplete.add(String.valueOf(i));
                i++;
            }

        }
    }
}
