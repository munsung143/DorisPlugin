package org.exam.dorisPlugin;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;

public class TextFormatBuilder {

    private Component finalComponent;
    private String input;
    private TextColor color = NamedTextColor.WHITE;
    private TextDecoration.State isBold = State.NOT_SET;
    private TextDecoration.State isItalic = State.FALSE;
    private TextDecoration.State isUnder = State.NOT_SET;
    private TextDecoration.State isStrike = State.NOT_SET;
    private TextDecoration.State isObfuscated = State.NOT_SET;

    public TextFormatBuilder(String format){
        finalComponent = Component.empty();
        this.input = format;
    }

    public Component Build(){
        StringBuilder sb = new StringBuilder();
        int len = input.length();
        for (int i = 0; i < len; i++) {
            char c = input.charAt(i);
            if (c == '&' && i < len - 1){
                if (IsFormatingCode(input.charAt(i + 1))) {
                    BuildAndAppendComponent(sb);
                    i++;
                    MatchFormatingCode(input.charAt(i));
                }
                else{
                    sb.append('&');
                }
            }
            else if (c == '#' && i < len - 6){
                boolean isRGB = true;
                StringBuilder rgbb = new StringBuilder();
                for (int j = 1; j <= 6; j++){
                   if (!PluginUtil.IsHex(input.charAt(i + j))){
                       sb.append('#');
                       isRGB = false;
                       break;
                   }
                   else{
                       rgbb.append(input.charAt(i + j));
                   }
                }
                if(!isRGB) continue;
                BuildAndAppendComponent(sb);

                color = PluginUtil.RGBToTextColor(rgbb.toString());
                i = i + 6;
            }
            else{
                sb.append(c);
            }

        }
        BuildAndAppendComponent(sb);
        return finalComponent;
    }
    private boolean IsFormatingCode(char c){
        return PluginUtil.IsHex(c) || (c >= 107 && c <= 111);
    }

    private void BuildAndAppendComponent(StringBuilder sb){
        if (sb.isEmpty()){
            return;
        }
        TextComponent tc = Component.text(sb.toString(), color)
                .decoration(TextDecoration.BOLD, isBold)
                .decoration(TextDecoration.ITALIC, isItalic)
                .decoration(TextDecoration.UNDERLINED, isUnder)
                .decoration(TextDecoration.STRIKETHROUGH, isStrike)
                .decoration(TextDecoration.OBFUSCATED, isObfuscated);
        finalComponent = finalComponent.append(tc); //Component가 불변 객체임에 주의
        sb.setLength(0);
    }

    private void MatchFormatingCode(char c){
        switch (c){
            case '0':
                color = NamedTextColor.BLACK;
                break;
            case '1':
                color = NamedTextColor.DARK_BLUE;
                break;
            case '2':
                color = NamedTextColor.DARK_GREEN;
                break;
            case '3':
                color = NamedTextColor.DARK_AQUA;
                break;
            case '4':
                color = NamedTextColor.DARK_RED;
                break;
            case '5':
                color = NamedTextColor.DARK_PURPLE;
                break;
            case '6':
                color = NamedTextColor.GOLD;
                break;
            case '7':
                color = NamedTextColor.GRAY;
                break;
            case '8':
                color = NamedTextColor.DARK_GRAY;
                break;
            case '9':
                color = NamedTextColor.BLUE;
                break;
            case 'a':
                color = NamedTextColor.GREEN;
                break;
            case 'b':
                color = NamedTextColor.AQUA;
                break;
            case 'c':
                color = NamedTextColor.RED;
                break;
            case 'd':
                color = NamedTextColor.LIGHT_PURPLE;
                break;
            case 'e':
                color = NamedTextColor.YELLOW;
                break;
            case 'f':
                color = NamedTextColor.WHITE;
                break;
            case 'k':
                if (isObfuscated == State.NOT_SET){
                    isObfuscated = State.TRUE;
                }
                else{
                    isObfuscated = State.NOT_SET;
                }
                break;
            case 'l':
                if (isBold == State.NOT_SET){
                    isBold = State.TRUE;
                }
                else{
                    isBold = State.NOT_SET;
                }
                break;
            case 'm':
                if (isStrike == State.NOT_SET){
                    isStrike = State.TRUE;
                }
                else{
                    isStrike = State.NOT_SET;
                }
                break;
            case 'n':
                if (isUnder == State.NOT_SET){
                    isUnder = State.TRUE;
                }
                else{
                    isUnder = State.NOT_SET;
                }
                break;
            case 'o':
                if (isItalic == State.FALSE){
                    isItalic = State.TRUE;
                }
                else{
                    isItalic = State.FALSE;
                }
                break;
        }

    }

}
