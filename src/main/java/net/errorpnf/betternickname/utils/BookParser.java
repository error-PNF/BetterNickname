package net.errorpnf.betternickname.utils;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.*;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class BookParser {
    private static BookParser INSTANCE = new BookParser();
    public static String generatedNickname = null;
    public static String currentRank = null;

    public static BookParser getInstance() {
        return INSTANCE;
    }

    private BookParser() {
    }

    @SubscribeEvent
    public void onNetwork2(FMLNetworkEvent event) {
        if (event instanceof FMLNetworkEvent.ClientConnectedToServerEvent && !((FMLNetworkEvent.ClientConnectedToServerEvent) event).isLocal) {
            event.manager.channel().pipeline().addAfter("encoder", "packet_logger_handler", new SimpleChannelInboundHandler<Packet>() {
                @Override
                protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
                    //System.out.println("Received packet: " + msg.getClass().getSimpleName());

                    if (msg instanceof S2FPacketSetSlot) {
                        String parsedPacket = parsePacket(msg);
                        if (parsedPacket.contains("We've generated a random username for you:")) {
                            if (parsedPacket.contains("/nick actuallyset ") && !parsedPacket.contains("Click here to reuse")) {
                                // Find the position of the command pattern
                                int commandIndex = parsedPacket.indexOf("/nick actuallyset ");
                                if (commandIndex != -1) {
                                    generatedNickname = getString(parsedPacket, commandIndex);

                                    ChatComponentText message = new ChatComponentText("§e[BetterNick] Generated nickname: §b" + generatedNickname);

                                    ChatComponentText hoverText = new ChatComponentText(
                                            "§eTo claim a generated nickname, either run the\n"
                                                    + "§ecommand §b/betternick claimname§e, or §bclick this message§e."
                                                    + "\n\n§cWARNING: When claiming a name, you are only able to claim"
                                                    + "\n§cthe most recently generated name. Once you have"
                                                    + "\n§crerolled your nickname, all previous names will be lost.");
                                    message.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
                                    message.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nick actuallyset " + BookParser.getGeneratedNickname()));

                                    UChat.chat(message);
                                }
                            }
                        }
                    }

                    ctx.fireChannelRead(msg);
                }

                @NotNull
                private String getString(String parsedPacket, int commandIndex) {
                    String randomString = parsedPacket.substring(commandIndex + "/nick actuallyset ".length());

                    // Cut off the string before " respawn"
                    int respawnIndex = randomString.indexOf(" respawn");
                    if (respawnIndex != -1) {
                        randomString = randomString.substring(0, respawnIndex);
                    }
                    return randomString;
                }
            });
        }
    }

    private String parsePacket(Packet<?> packet) {
        if (packet instanceof S2FPacketSetSlot) {
            S2FPacketSetSlot setSlotPacket = (S2FPacketSetSlot) packet;
            if (setSlotPacket.func_149174_e() != null) {
                if (Minecraft.getMinecraft().thePlayer != null && setSlotPacket.func_149174_e().getItem() instanceof ItemEditableBook) {
                    ItemEditableBook bookItem = (ItemEditableBook) setSlotPacket.func_149174_e().getItem();
                    NBTTagCompound bookData = setSlotPacket.func_149174_e().getTagCompound();
                    if (bookData != null) {
                        NBTTagList pages = bookData.getTagList("pages", 8); // Tag type 8 is for strings
                        //System.out.println("Number of pages: " + pages.tagCount());
                        StringBuilder bookContent = new StringBuilder();
                        for (int i = 0; i < pages.tagCount(); i++) {
                            NBTTagString page = (NBTTagString) pages.get(i);
                            bookContent.append(page.getString()).append("\n");
                        }
                        //System.out.println("Contents of the book:");
                        String bookContentToString = bookContent.toString();
                        //System.out.println(bookContentToString);
                        return bookContentToString;
                    }
                }
                return "Window ID: " + setSlotPacket.func_149175_c() + ", Slot: " + setSlotPacket.func_149173_d()  + ", Item: " + setSlotPacket.func_149174_e().getItem();
            }
        }
        return packet.toString();
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        // remove hypixel's chat messages
        if (event.message.getFormattedText().contains("§eGenerating a unique random name. Please wait...")) {
            event.setCanceled(true);
        } else if (event.message.getFormattedText().contains("§eProcessing request. Please wait...")) {
            event.setCanceled(true);
        } else if (event.message.getFormattedText().contains("§cThat's not a random nickname we gave you!")) {
            event.setCanceled(true);
        }

        String message = event.message.getFormattedText();

        if (message.startsWith("§r§aSet your nick rank to")) {
            if (message.contains("§r§7DEFAULT")) {
                currentRank = "§r§7";
                event.setCanceled(true);
            } else if (message.contains("§r§aVIP")) {
                if (message.contains("§6+")) {
                    currentRank = "§r§a[VIP§6+§a] ";
                    event.setCanceled(true);
                } else {
                    currentRank = "§r§a[VIP] ";
                    event.setCanceled(true);
                }
            } else if (message.contains("§r§bMVP")) {
                if (message.contains("§c+")) {
                    currentRank = "§r§b[MVP§c+§b] ";
                    event.setCanceled(true);
                } else {
                    currentRank = "§r§b[MVP] ";
                    event.setCanceled(true);
                }
            }
        }
    }

    public static String getGeneratedNickname() {
        return generatedNickname;
    }

    // this is debug and shouldn't really be used
    public static void setGeneratedNickname(String generatedNickname) {
        BookParser.generatedNickname = generatedNickname;
    }

    public static String getCurrentRank() {
        return currentRank;
    }

    public static void setCurrentRank(String currentRank) {
        BookParser.currentRank = currentRank;
    }
}
