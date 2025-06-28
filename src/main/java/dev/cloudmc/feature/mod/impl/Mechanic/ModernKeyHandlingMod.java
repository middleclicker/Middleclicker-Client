package dev.cloudmc.feature.mod.impl.Mechanic;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class ModernKeyHandlingMod extends Mod {

    private static final KeyBinding[] MOVEMENT_KEYS = new KeyBinding[]{
            Cloud.INSTANCE.mc.gameSettings.keyBindForward,
            Cloud.INSTANCE.mc.gameSettings.keyBindBack,
            Cloud.INSTANCE.mc.gameSettings.keyBindLeft,
            Cloud.INSTANCE.mc.gameSettings.keyBindRight,
            Cloud.INSTANCE.mc.gameSettings.keyBindJump,
            Cloud.INSTANCE.mc.gameSettings.keyBindSneak,
            Cloud.INSTANCE.mc.gameSettings.keyBindSprint
    };
    private boolean wasInInventory;

    public ModernKeyHandlingMod() {
        super(
                "Modern Key Handling",
                "1.12.2 Key handling",
                Type.Mechanic
        );
        wasInInventory = false;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (Cloud.INSTANCE.mc.thePlayer == null || Cloud.INSTANCE.mc.theWorld == null) return;
        if (event.phase == TickEvent.Phase.END) {
            if (Cloud.INSTANCE.mc.currentScreen instanceof GuiScreen) {
                wasInInventory = true;
            } else if (Cloud.INSTANCE.mc.currentScreen == null && wasInInventory) {
                for (KeyBinding key : MOVEMENT_KEYS) {
                    if (Keyboard.isKeyDown(key.getKeyCode())) {
                        KeyBinding.setKeyBindState(key.getKeyCode(), true);
                    }
                }
                wasInInventory = false;
            }
        }
    }
}
