package dev.cloudmc.feature.mod.impl.Cheats;

import com.mojang.authlib.GameProfile;
import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;

import java.util.UUID;

public class FakePlayerMod extends Mod {
    public FakePlayerMod() {
        super(
                "FakePlayer",
                "Spawns a fake player for testing",
                Type.Cheats
        );
    }

    private EntityOtherPlayerMP fake_player;

    @Override
    public void onEnable() {
        super.onEnable();
        Minecraft mc = Cloud.INSTANCE.mc;
        fake_player = new EntityOtherPlayerMP(mc.theWorld, new GameProfile(UUID.fromString("90676414-9e9d-49b0-b109-dc7cbd53f0ab"), "Fake Player"));
        fake_player.setLocationAndAngles(mc.thePlayer.posX, mc.thePlayer.posY - mc.thePlayer.height, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        fake_player.rotationYawHead = mc.thePlayer.rotationYawHead;
        mc.theWorld.addEntityToWorld(-100, fake_player);
        fake_player.onLivingUpdate();
    }

    @Override
    public void onDisable() {
        try {
            Cloud.INSTANCE.mc.theWorld.removeEntityFromWorld(-100);
        } catch (Exception ignored) {}
        super.onDisable();
    }
}
