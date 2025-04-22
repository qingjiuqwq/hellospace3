/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ChatUtils {

    public static void message(final String message) {
        Wrapper.mc().gui.getChat().addMessage(Component.nullToEmpty(ChatFormatting.RED + "[" + ChatFormatting.GOLD + "Space" + ChatFormatting.RED + "]" + ChatFormatting.WHITE + " " + message));
    }

    public static void warning(final String message) {
        message(ChatFormatting.RED + "[" + ChatFormatting.GOLD + ChatFormatting.BOLD + "WARNING" + ChatFormatting.RED + "]" + ChatFormatting.WHITE + " " + message);
    }

    public static void error(final String message) {
        message(ChatFormatting.RED + "[" + ChatFormatting.DARK_RED + ChatFormatting.BOLD + "ERROR" + ChatFormatting.RED + "]" + ChatFormatting.WHITE + " " + message);
    }

    public static void success(final String message) {
        message(ChatFormatting.GREEN + "[" + ChatFormatting.DARK_GREEN + ChatFormatting.BOLD + "SUCCESS" + ChatFormatting.GREEN + "]" + ChatFormatting.WHITE + " " + message);
    }

}
