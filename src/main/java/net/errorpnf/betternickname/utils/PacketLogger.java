package net.errorpnf.betternickname.utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.commons.io.FileUtils;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class PacketLogger {

    public static KeyBinding toggleSneakKey = new KeyBinding("key.sneakmod.toggle", Keyboard.KEY_P, "key.categories.sneakmod");

    private static boolean isSneaking = false;
    private static final String PACKET_LOG_DIR = "C:\\Users\\benst\\Downloads\\packets";

    private static PacketLogger INSTANCE = new PacketLogger();

    public static PacketLogger getInstance() {
        return INSTANCE;
    }

    private PacketLogger() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (toggleSneakKey.isPressed()) {
            //System.out.println("sneak is pressed");
            isSneaking = !isSneaking;
        }
    }

    /*@SubscribeEvent
    public void onClientConnect(FMLNetworkEvent event) {
            if (event instanceof FMLNetworkEvent.ClientConnectedToServerEvent && !((FMLNetworkEvent.ClientConnectedToServerEvent) event).isLocal) {
                event.manager.channel().pipeline().addAfter("encoder", "packet_logger_handler", new SimpleChannelInboundHandler<Packet>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
                        String packetData = parsePacket(msg);
                        if (packetData != null) {
                            System.out.println("packet saved");
                            logPacket(packetData);
                        }
                        ctx.fireChannelRead(msg);
                    }
                });
            }
    }*/

    private void logPacket(String packetData) {
        File logDir = new File(PACKET_LOG_DIR);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        long currentTimeMillis = System.currentTimeMillis();
        String logFileName = "packet_" + currentTimeMillis + ".txt";
        File logFile = new File(logDir, logFileName);
        try {
            FileUtils.writeStringToFile(logFile, packetData, StandardCharsets.UTF_8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String parsePacket(Packet<?> packet) {
        if (packet instanceof S2FPacketSetSlot) {
            S2FPacketSetSlot setSlotPacket = (S2FPacketSetSlot) packet;
            if (setSlotPacket.func_149174_e() != null) {
                if (Minecraft.getMinecraft().thePlayer != null && setSlotPacket.func_149174_e().getItem() instanceof ItemEditableBook) {
                    NBTTagCompound bookData = setSlotPacket.func_149174_e().getTagCompound();
                    if (bookData != null) {
                        NBTTagList pages = bookData.getTagList("pages", 8); // Tag type 8 is for strings
                        StringBuilder bookContent = new StringBuilder();
                        for (int i = 0; i < pages.tagCount(); i++) {
                            NBTTagString page = (NBTTagString) pages.get(i);
                            bookContent.append(page.getString()).append("\n");
                        }
                        return bookContent.toString();
                    }
                }
                return "Window ID: " + setSlotPacket.func_149175_c() + ", Slot: " + setSlotPacket.func_149173_d() + ", Item: " + setSlotPacket.func_149174_e().getDisplayName();
            }

        } else if (packet instanceof S0EPacketSpawnObject) {
            S0EPacketSpawnObject parsePacket = (S0EPacketSpawnObject) packet;
            return "Spawn Object Packet: ID = " + parsePacket.getEntityID() + ", X: " + parsePacket.getX() + ", Y: " + parsePacket.getY() + ", Z: " + parsePacket.getZ() + ", Type: " + parsePacket.getType() + ", Confuse: " + parsePacket.func_149009_m();

        } else if (packet instanceof S2CPacketSpawnGlobalEntity) {
            S2CPacketSpawnGlobalEntity parsePacket = (S2CPacketSpawnGlobalEntity) packet;
            return "Entity ID: " + parsePacket.func_149052_c() + ", X: " + parsePacket.func_149051_d() + ", Y: " + parsePacket.func_149050_e() + ", Z: " + parsePacket.func_149049_f() + ", Type: " + parsePacket.func_149053_g();

        } else if (packet instanceof S3FPacketCustomPayload) {
            S3FPacketCustomPayload customPayloadPacket = (S3FPacketCustomPayload) packet;
            return parseCustomPayload(customPayloadPacket);

        } else if (packet instanceof S23PacketBlockChange) {
            S23PacketBlockChange parsePacket = (S23PacketBlockChange) packet;
            return "Block State: " + parsePacket.getBlockState() + ", Block Position:" + parsePacket.getBlockPosition();

        /*} else if (packet instanceof S29PacketSoundEffect) {
            S29PacketSoundEffect parsePacket = (S29PacketSoundEffect) packet;
            return "Sound Name: " + parsePacket.getSoundName() + ", X: " + parsePacket.getX() + ", Y: " + parsePacket.getY() + ", Z: " + parsePacket.getZ() + ", Volume: " + parsePacket.getVolume() + ", Pitch: " + parsePacket.getPitch();
        }*/
        } else {
            return null;
        }
        return packet.toString();
    }

    private String parseCustomPayload(S3FPacketCustomPayload packet) {
        String channelName = packet.getChannelName();
        PacketBuffer data = packet.getBufferData();
        ByteBuf byteBuf = data.copy();
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        String dataString = new String(bytes, StandardCharsets.UTF_8);

        if (channelName.equals("badlion:timers")) return null;
        else return "Custom Payload Packet: Channel = " + channelName + ", Data = " + dataString;
    }


    private Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onClientTick(EntityItemPickupEvent event) {
        System.out.println("did shit");
        if (mc.theWorld != null && mc.thePlayer != null) {
            EntityItem entityItem = event.item;
            ItemStack itemStack = entityItem.getEntityItem();

            StringBuilder sb = new StringBuilder();

            sb.append("Entity Position: " + entityItem.getPosition())
                    .append("\nAge: " + entityItem.getAge())
                    .append("\nThrower: " + entityItem.getThrower())
                    .append("\nOwner: " + entityItem.getOwner())
                    .append("\nHover Start: " + entityItem.hoverStart)
                    .append("\nUUID: " + entityItem.getUniqueID())

                    .append("\n\nItem:" + itemStack.getItem())
                    .append("\nDisplay Name: " + itemStack.getDisplayName())
                    .append("\nStack Size: " + itemStack.getMaxStackSize())
                    .append("\nUnlocalized Name: " + itemStack.getUnlocalizedName())
                    .append("\nEnchantment Tag List: " + itemStack.getEnchantmentTagList())
                    .append("\nItem Damage: " + itemStack.getItemDamage());

            //System.out.println("Saved Item: " + itemStack.getDisplayName());
            logPacket(sb.toString());
        }
    }
}