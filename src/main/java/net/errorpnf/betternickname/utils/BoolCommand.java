package net.errorpnf.betternickname.utils;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class BoolCommand extends CommandBase {

    public static boolean isShouldEnable() {
        return shouldEnable;
    }

    public static void setShouldEnable(boolean shouldEnable) {
        BoolCommand.shouldEnable = shouldEnable;
    }

    public static boolean shouldEnable = false;

    @Override
    public String getCommandName() {
        return "bool";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "shit do";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        shouldEnable = !shouldEnable;
        UChat.chat("bool is now" + shouldEnable);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
