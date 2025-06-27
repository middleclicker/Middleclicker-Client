package dev.cloudmc.feature.mod.impl;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.util.ChatComponentText;

import java.util.HashMap;
import java.util.List;

public class KnownDetectorMod extends Mod {
    private final HashMap<String, String[]> knownList;

    public KnownDetectorMod() {
        super(
                "Known Detector",
                "Detects known players",
                Type.All
        );

        knownList = new HashMap<>();
        knownList.put("Lizishu", new String[]{"Lizishu", "aiterys"});
        knownList.put("Kaoliar", new String[]{"K4slana"});
        knownList.put("SpokenO", new String[]{"SpokenO"});
        knownList.put("LowZTier", new String[]{"DaiChan_Dai"});
        knownList.put("JiuMeng", new String[]{"JIUMENG"});
        knownList.put("Bridge", new String[]{"Br1d3e"});
        knownList.put("Atier", new String[]{"Atier"});
    }

    @SubscribeEvent
    public void onClientConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                checkForKnownPlayers();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        checkForKnownPlayers();
    }

    private void checkForKnownPlayers() {
        Minecraft mc = Cloud.INSTANCE.mc;
        if (mc.thePlayer == null || mc.getNetHandler() == null) return;

        List<GuiPlayerInfo> playerInfoList = mc.getNetHandler().playerInfoList;
        for (GuiPlayerInfo guiPlayerInfo : playerInfoList) {
            String playername = guiPlayerInfo.name;
            for (String knownKey : knownList.keySet()) {
                String[] aliases = knownList.get(knownKey);
                for (String alias : aliases) {
                    if (playername.equalsIgnoreCase(alias)) {
                        mc.thePlayer.addChatMessage(new ChatComponentText("[KnownDetector] Found known player: " + playername + " (aka " + knownKey + ")"));
                    }
                }
            }
        }
    }
}
