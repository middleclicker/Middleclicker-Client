package dev.cloudmc.feature.mod.impl;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.EntityLivingBase;

import java.lang.reflect.Field;

public class NoJumpDelay extends Mod {
    private Field jumpTicksField;

    public NoJumpDelay() {
        super(
                "NoJumpDelay",
                "Removes the vanilla jump delay",
                Type.Cheats
        );

        // Initialize reflection to access jumpTicks field
        try {
            jumpTicksField = EntityLivingBase.class.getDeclaredField("jumpTicks"); // MCP name
            jumpTicksField.setAccessible(true); // Bypass private access
        } catch (NoSuchFieldException e) {
            try {
                // Fallback to obfuscated name for 1.7.10 (field_70773_bE in MCP mappings)
                jumpTicksField = EntityLivingBase.class.getDeclaredField("field_70773_bE");
                jumpTicksField.setAccessible(true);
            } catch (NoSuchFieldException ex) {
                ex.printStackTrace();
                // Optionally disable mod if field is not found
                System.out.println("NoJumpDelay: Could not find jumpTicks field!");
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Ensure this runs only for the client player and in the END phase
        if (event.phase == TickEvent.Phase.END && event.player instanceof EntityClientPlayerMP) {
            if (Cloud.INSTANCE.mc.thePlayer != null && jumpTicksField != null) {
                try {
                    // Set jumpTicks to 0 using reflection
                    jumpTicksField.setInt(Cloud.INSTANCE.mc.thePlayer, 0);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    System.out.println("NoJumpDelay: Failed to set jumpTicks!");
                }
            }
        }
    }
}
