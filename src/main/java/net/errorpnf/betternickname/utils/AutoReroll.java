package net.errorpnf.betternickname.utils;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import net.errorpnf.betternickname.commands.BetterNickCommand;
import net.errorpnf.betternickname.config.BetterNickConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoReroll {
    private static AutoReroll INSTANCE = new AutoReroll();
    public static AutoReroll getInstance() {
        return INSTANCE;
    }
    public static boolean autoRerollEnabled = false;
    public static int seconds = 0;
    public static int tick = 0;
    public static String claimedName = null;
    public static boolean hasClaimedName = false;
    private static boolean hasSentIsInLobby = false;

    private AutoReroll() {
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if ((Minecraft.getMinecraft() != null) && (Minecraft.getMinecraft().thePlayer != null) && HypixelUtils.INSTANCE.isHypixel()) {
            if (isAutoRerollEnabled()) {
                if (IsInLobby.isInLobby()) {
                    if (!hasClaimedName && BookParser.getGeneratedNickname() != null && !BookParser.getGeneratedNickname().equals(claimedName)) {

                        if (!BetterNickConfig.matchText.isEmpty() && BookParser.getGeneratedNickname().contains(BetterNickConfig.matchText)) {
                            if (BetterNickConfig.autoClaimName) {
                                Minecraft.getMinecraft().thePlayer.sendChatMessage("/nick actuallyset " + BookParser.getGeneratedNickname());
                                UChat.chat("&e[BetterNick] &aAuto-Reroll match found! Claiming name...");
                                UChat.chat("&e[BetterNick] Your nickname has been set to: &b" + BookParser.getGeneratedNickname());
                                BetterNickCommand.setCancelBookGui(true);
                                toggleAutoReroll();
                                hasSentIsInLobby = false;
                                Minecraft.getMinecraft().thePlayer.playSound("random.levelup", 1f, 2f);
                                claimedName = BookParser.getGeneratedNickname();
                                hasClaimedName = true;
                            } else {
                                UChat.chat("&e[BetterNick] &aAuto-Reroll match found!");
                                UChat.chat("&e[BetterNick] Run &b/betternick claimname &eto claim the name!");
                                toggleAutoReroll();
                                hasSentIsInLobby = false;
                                Minecraft.getMinecraft().thePlayer.playSound("random.levelup", 1f, 2f);
                                claimedName = BookParser.getGeneratedNickname();
                                hasClaimedName = true;
                            }
                        }
                    }
                } else {
                    if (!hasSentIsInLobby) {
                        UChat.chat("&e[BetterNick] &cYou must be in the lobby to do this!");
                        hasSentIsInLobby = true;
                    }
                }



                tick++;
                if (tick > (BetterNickConfig.rerollDelay * 40)) {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/nick help setrandom");
                    BetterNickCommand.setCancelBookGui(true);
                    tick = 0;
                    hasClaimedName = false;
                }
            }
        }
    }

    public static boolean isAutoRerollEnabled() {
        return autoRerollEnabled;
    }

    public static void toggleAutoReroll() {
        if (HypixelUtils.INSTANCE.isHypixel()) {
            if (isAutoRerollEnabled()) {
                UChat.chat("&e[BetterNick] &cDisabled Auto-Reroll.");
            } else {
                UChat.chat("&e[BetterNick] &aEnabled Auto-Reroll.");
            }
            AutoReroll.autoRerollEnabled = !isAutoRerollEnabled();
        }
    }
}
