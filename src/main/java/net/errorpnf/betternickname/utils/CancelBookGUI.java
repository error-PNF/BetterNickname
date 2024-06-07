package net.errorpnf.betternickname.utils;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import net.errorpnf.betternickname.commands.BetterNickCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CancelBookGUI {
    private static CancelBookGUI INSTANCE = new CancelBookGUI();
    public static CancelBookGUI getInstance() {
        return INSTANCE;
    }

    private CancelBookGUI() {
    }

    @SubscribeEvent
    public void bookGUIOpenEvent(GuiScreenEvent event) {
        if (event.gui instanceof GuiScreenBook) {
            if (BetterNickCommand.shouldCancelBookGui()) {
                // Close the GUI screen
                Minecraft.getMinecraft().displayGuiScreen(null);
                //UChat.chat("Condition met, closing the book GUI.");
                BetterNickCommand.setCancelBookGui(false);
            }
        }
    }
}
