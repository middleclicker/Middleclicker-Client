package dev.cloudmc.feature.mod.impl.Tweaks;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

public class ChatTweaksMod extends Mod {

    private Minecraft mc;
    private float prevChatScale;

    public ChatTweaksMod() {
        super(
                "Chat Tweaks",
                "Tweaks and adds features to the chat",
                Type.Tweaks
        );

        mc = Cloud.INSTANCE.mc;
        prevChatScale = mc.gameSettings.chatScale;
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Toggle Bind", this, Keyboard.KEY_R));
    }

    @SubscribeEvent
    public void key(InputEvent.KeyInputEvent e) {
        if (Keyboard.isKeyDown(Cloud.INSTANCE.settingManager.getSettingByModAndName("Chat Tweaks", "Toggle Bind").getKey())) {
            if (isChatVisible()) {
                prevChatScale = mc.gameSettings.chatScale;
                mc.gameSettings.chatScale = 0f;
            } else {
                mc.gameSettings.chatScale = prevChatScale;
            }
        }
    }

    private boolean isChatVisible() {
        return mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN && mc.gameSettings.chatScale != 0 && mc.gameSettings.chatOpacity != 0;
    }
}
