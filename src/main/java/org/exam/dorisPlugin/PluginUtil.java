package org.exam.dorisPlugin;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;

public class PluginUtil {

    public static boolean IsRGB(String str){
        if (str.length() != 6) return false;
        for (int i = 0; i < str.length(); i++){
            if (!IsHex(str.charAt(i))) return false;
        }
        return true;
    }

    public static TextColor RGBToTextColor(String str){
        int r = HexToDecimal(str.charAt(0)) * 16 + HexToDecimal(str.charAt(1));
        int g = HexToDecimal(str.charAt(2)) * 16 + HexToDecimal(str.charAt(3));
        int b = HexToDecimal(str.charAt(4)) * 16 + HexToDecimal(str.charAt(5));
        return TextColor.color(r, g, b);
    }
    public static Color RGBToColor(String str){
        int r = HexToDecimal(str.charAt(0)) * 16 + HexToDecimal(str.charAt(1));
        int g = HexToDecimal(str.charAt(2)) * 16 + HexToDecimal(str.charAt(3));
        int b = HexToDecimal(str.charAt(4)) * 16 + HexToDecimal(str.charAt(5));
        return Color.fromRGB(r, g, b);
    }

    public static boolean IsNum(char c){
        return (c >= 48 && c <= 57);
    }
    public static boolean IsAlp(char c){
        return (c >= 97 && c <= 102);
    }
    public static boolean IsBigAlp(char c){
        return (c >= 65 && c <= 70);
    }
    public static boolean IsHex(char c){
        return IsNum(c) || IsAlp(c) || IsBigAlp(c);
    }
    public static int HexToDecimal(char c){
        int val = 0;
        if (IsNum(c)){
            val = c - 48;
        }
        else if (IsAlp(c)){
            val = c - 87;
        }
        else if (IsBigAlp(c)){
            val = c - 55;
        }
        return val;
    }

    public static String CombineRestArgstoString(String[] args, int startAt){
        StringBuilder inputBuilder = new StringBuilder();
        for(int i = startAt; i < args.length; i++){
            inputBuilder.append(args[i]);
            if(i < args.length - 1){
                inputBuilder.append(' ');
            }
        }
        return inputBuilder.toString();
    }
    public static Integer parseInt(String arg, int min, int max) {
        try {
            int value = Integer.parseInt(arg);
            return value >= min && value <= max ? value : null;
        } catch (Exception e) {
            return null;
        }
    }
    public static Float parseFloat(String arg, float min, float max){
        try {
            float value = Float.parseFloat(arg);
            return value >= min && value <= max ? value : null;
        } catch (Exception e) {
            return null;
        }
    }
    public static Boolean parseBool(String arg){
        try {
            return Boolean.parseBoolean(arg);
        } catch (Exception e) {
            return null;
        }
    }
}
