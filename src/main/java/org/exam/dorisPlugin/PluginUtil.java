package org.exam.dorisPlugin;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;

import java.util.concurrent.ThreadLocalRandom;

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
    public static int parseInt2(String arg, int min, int max, int def) {
        try {
            return Math.max(min, Math.min(Integer.parseInt(arg), max));
        } catch (Exception e) {
            return def;
        }
    }
    public static short parseShort2(String arg, short min, short max, short def) {
        return (short)parseInt2(arg, (int)min, (int)max, (int)def);
    }
    public static float parseFloat2(String arg, float min, float max, float def) {
        try {
            return Math.max(min, Math.min(Float.parseFloat(arg), max));
        } catch (Exception e) {
            return def;
        }
    }
    public static double parseDouble2(String arg, double min, double max, double def) {
        try {
            return Math.max(min, Math.min(Double.parseDouble(arg), max));
        } catch (Exception e) {
            return def;
        }
    }
    public static boolean parseBool2(String arg, boolean def) {
        try {
            return Boolean.parseBoolean(arg);
        } catch (Exception e) {
            return def;
        }
    }
    public static Short parseShort(String arg, short min, short max) {
        try {
            short value = Short.parseShort(arg);
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

    private static String randomList = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static String GetRandomID(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++){
            sb.append(randomList.charAt(ThreadLocalRandom.current().nextInt(randomList.length())));
        }
        return sb.toString();
    }
}
