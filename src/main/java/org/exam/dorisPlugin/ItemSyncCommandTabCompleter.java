package org.exam.dorisPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemSyncCommandTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
        if (!sender.isOp()) return null;
        List<String> tabComplete = null;
        int length = args.length;
        if (length == 1){
             tabComplete =  defaultComp(tabComplete);
        }
        else if (length > 1){
            switch (args[0]){
                case "remove": tabComplete = idTable(tabComplete, args); break;
                case "get": tabComplete = idTable(tabComplete, args); break;
                case "info": tabComplete = idTable(tabComplete, args); break;
                case "create": tabComplete = createTable(tabComplete, args); break;
                case "enchantasync" : tabComplete = enchantAsyncTable(tabComplete, args); break;
                default: tabComplete = new ArrayList<>(); break;
            }
        }
        var filteredComplete = tabComplete.stream().filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()));
        return filteredComplete.collect(Collectors.toList());
    }

    private List<String> defaultComp(List<String> comp){
        comp = new ArrayList<>();
        comp.add("list");
        comp.add("create");
        comp.add("info");
        comp.add("remove");
        comp.add("get");
        comp.add("enchantasync");
        return comp;
    }
    private List<String> idTable(List<String> comp, String[] args){
        comp = new ArrayList<>();
        if (args.length == 2){
            comp.addAll(DataSerializer.itemSyncIdMap.keySet());
        }
        return comp;
    }
    private List<String> createTable(List<String> comp, String[] args){
        comp = new ArrayList<>();
        if (args.length == 3){
            for (var m : Registries.Material){
                comp.add(m.toString().toLowerCase());
            }
        }
        return comp;
    }
    private List<String> enchantAsyncTable(List<String> comp, String[] args){
        comp = new ArrayList<>();
        if (args.length == 2){
            comp.addAll(DataSerializer.itemSyncIdMap.keySet());
        }
        else if (args.length == 3){
            comp.add("true");
            comp.add("false");
        }
        return comp;
    }
}
