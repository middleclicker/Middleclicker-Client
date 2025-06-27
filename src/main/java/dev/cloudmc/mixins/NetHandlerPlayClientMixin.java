package dev.cloudmc.mixins;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.impl.HitselectMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public class NetHandlerPlayClientMixin {
    @Inject(method = "handleEntityStatus", at = @At("RETURN"))
    public void handleEntityStatus(S19PacketEntityStatus packetIn, CallbackInfo callback) {
        Entity entity = packetIn.func_149161_a(Minecraft.getMinecraft().theWorld);
        int entityID = entity.getEntityId();
        byte packetType = packetIn.func_149160_c();
        if (packetType == 2) {
            if (entityID == Minecraft.getMinecraft().thePlayer.getEntityId()) {
                Cloud.INSTANCE.comboHelper.gotHit();
                HitselectMod.gotHit();
            } else {
                Cloud.INSTANCE.comboHelper.dealtHit(entityID);
                HitselectMod.dealtHit(entityID);
            }
        }
    }
}
