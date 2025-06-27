package dev.cloudmc.feature.mod.impl;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FakelagMod extends Mod {

    private final Queue<PacketEntry> inboundQueue = new ConcurrentLinkedQueue<>();
    private final Queue<PacketEntry> outboundQueue = new ConcurrentLinkedQueue<>();

    private final Minecraft mc = Minecraft.getMinecraft();

    public FakelagMod() {
        super(
                "Fakelag",
                "Changes your latency",
                Type.Cheats
        );
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Inbound Delay", this, 100, 50));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Outbound Delay", this, 100, 50));
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        inboundQueue.clear();
        outboundQueue.clear();
    }

    @SubscribeEvent
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
        inboundQueue.add(new PacketEntry(event.packet, (long) (System.currentTimeMillis() + Cloud.INSTANCE.settingManager.getSettingByModAndName("Fakelag", "Inbound Delay").getCurrentNumber())));
        event.setCanceled(true); // Cancel original packet to delay it
    }

    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
        outboundQueue.add(new PacketEntry(event.packet, (long) (System.currentTimeMillis() + Cloud.INSTANCE.settingManager.getSettingByModAndName("Fakelag", "Outbound Delay").getCurrentNumber())));
        event.setCanceled(true); // Cancel original packet to delay it
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!isToggled()) return;

        long currentTime = System.currentTimeMillis();

        // Process inbound queue
        Iterator<PacketEntry> inboundIterator = inboundQueue.iterator();
        while (inboundIterator.hasNext()) {
            PacketEntry entry = inboundIterator.next();
            if (currentTime >= entry.sendTime) {
                entry.packet.processPacket(mc.getNetHandler());
                inboundIterator.remove();
            }
        }

        // Process outbound queue
        Iterator<PacketEntry> outboundIterator = outboundQueue.iterator();
        while (outboundIterator.hasNext()) {
            PacketEntry entry = outboundIterator.next();
            if (currentTime >= entry.sendTime) {
                mc.getNetHandler().addToSendQueue(entry.packet);
                outboundIterator.remove();
            }
        }
    }

    private static class PacketEntry {
        public final Packet packet;
        public final long sendTime;

        public PacketEntry(Packet packet, long sendTime) {
            this.packet = packet;
            this.sendTime = sendTime;
        }
    }

}