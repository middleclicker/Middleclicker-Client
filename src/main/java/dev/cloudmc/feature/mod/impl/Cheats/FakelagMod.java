package dev.cloudmc.feature.mod.impl.Cheats;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import net.minecraft.client.Minecraft;

public class FakelagMod extends Mod {

    private static final Minecraft mc = Cloud.INSTANCE.mc;

    public FakelagMod() {
        super(
                "Fakelag",
                "Changes your latency",
                Type.Cheats
        );
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Inbound Delay", this, 100, 50));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Outbound Delay", this, 100, 50));
    }

    public int getInboundDelay() {
        return (int) Math.floor(Cloud.INSTANCE.settingManager.getSettingByModAndName("Fakelag", "Inbound Delay").getCurrentNumber());
    }

    public int getOutboundDelay() {
        return (int) Math.floor(Cloud.INSTANCE.settingManager.getSettingByModAndName("Fakelag", "Outbound Delay").getCurrentNumber());
    }
}