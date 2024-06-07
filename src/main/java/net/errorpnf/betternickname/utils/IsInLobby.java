package net.errorpnf.betternickname.utils;

import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import net.hypixel.modapi.packet.impl.serverbound.ServerboundRegisterPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Collections;

public class IsInLobby {
    private static IsInLobby INSTANCE = new IsInLobby();
    public static IsInLobby getInstance() {
        return INSTANCE;
    }


    private static boolean didJoinWorld = false;
    private static int tick = 0;
    private static boolean inLobby = false;

    public static ClientboundLocationPacket lastLocationPacket;
    private String lastServerName = "";


    public static boolean isInLobby() {
        return inLobby;
    }



    @SubscribeEvent
    public void onWorldJoin(EntityJoinWorldEvent event) {
        if (event.entity == Minecraft.getMinecraft().thePlayer) {
            didJoinWorld = true;
            tick = 0;
        }
    }

    @SubscribeEvent
    public void onTickEvent(TickEvent.ClientTickEvent event) {
        if (HypixelUtils.INSTANCE.isHypixel()) {
            if (lastLocationPacket != null) {
                String server = lastLocationPacket.getServerName().toString();
                if (server.contains("lobby")) {
                    inLobby = true;
                } else {
                    inLobby = false;
                }
            }

            if (didJoinWorld) {
                tick++;
                if (tick > 20) {
                    //System.out.println("Sending location packet...");
                    Runnable location = () -> HypixelModAPI.getInstance().sendPacket(new ServerboundRegisterPacket(
                            HypixelModAPI.getInstance().getRegistry().getEventVersions(Collections.singleton("hyevent:location"))
                    ));
                    location.run(); // Execute the Runnable
                    didJoinWorld = false; // Reset the flag
                    tick = 0; // Reset tick counter
                }
            }
        } else {
            inLobby = false;
        }
    }


    public void setLastLocationPacket(ClientboundLocationPacket packet) {
        this.lastLocationPacket = packet;
    }

}
