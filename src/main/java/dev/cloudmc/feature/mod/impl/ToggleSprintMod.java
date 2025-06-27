/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */
package dev.cloudmc.feature.mod.impl;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class ToggleSprintMod extends Mod {

    private static boolean toggled = false;

    public ToggleSprintMod() {
        super(
                "ToggleSprint",
                "Allows you to toggle the Sprint button instead of holding it.",
                Type.Mechanic
        );
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Keybinding", this, Keyboard.KEY_LCONTROL));

        String[] mode = {"Modern", "Legacy", "Hidden"};
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Mode", this, "Hidden", 0, mode));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Background", this, false));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Font Color", this, new Color(255, 255, 255), new Color(255, 0, 0), 0, new float[]{0, 0}));
    }

    public static boolean isSprinting() {
        return toggled;
    }

    @Override
    public void onDisable(){
        super.onDisable();
        KeyBinding.setKeyBindState(Cloud.INSTANCE.mc.gameSettings.keyBindSprint.getKeyCode(), false);
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent e) {
        KeyBinding.setKeyBindState(Cloud.INSTANCE.mc.gameSettings.keyBindSprint.getKeyCode(), toggled);
    }

    @SubscribeEvent
    public void key(InputEvent.KeyInputEvent e) {
        if(Keyboard.isKeyDown(getKey())){
            toggled = !toggled;
        }
    }

    private int getKey(){
        return Cloud.INSTANCE.settingManager.getSettingByModAndName(getName(), "Keybinding").getKey();
    }
}
