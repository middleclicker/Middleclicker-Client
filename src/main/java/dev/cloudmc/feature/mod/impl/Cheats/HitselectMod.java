package dev.cloudmc.feature.mod.impl.Cheats;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import dev.cloudmc.helpers.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

import java.util.Random;

public class HitselectMod extends Mod {
    public HitselectMod() {
        super(
                "Hitselect",
                "Helps you hitselect",
                Type.Cheats
        );
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Fake Swing", this, true));
        lastHitTime = 0;
        timer = new Timer();
        hitselecting = false;
        target = null;
    }

    private Minecraft mc = Cloud.INSTANCE.mc;

    private static long lastHitTime;
    private Timer timer;
    private boolean hitselecting;
    private static Entity target;

    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent event) {
        if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState()) {
            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                target = mc.objectMouseOver.entityHit;
                // If the player is able to swing,
                // And the player is below the other player,
                // And the player is falling down -> We will hit the ground first and therefore accelerate before the other player -> Good time for hitselect.
                if (System.currentTimeMillis() - lastHitTime < 500 && mc.thePlayer.motionY < 0 && mc.thePlayer.posY < target.posX) {
                    if (!hitselecting) {
                        hitselecting = true;
                        timer.reset();
                    } else {
                        if (timer.getTimePassedMS() >= getRandomNumber()) { // Randomized duration
                            hitselecting = false;
                        } else {
                            mc.objectMouseOver = null;
                            if (!Cloud.INSTANCE.settingManager.getSettingByModAndName(getName(), "Fake Swing").isCheckToggled()) {
                                // Cancel swing animation. Doesn't work.
                                mc.thePlayer.swingProgress = 0;
                                mc.thePlayer.isSwingInProgress = false;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void gotHit() {

    }

    public static void dealtHit(int entityID) {
        if (target != null && target.getEntityId() == entityID) {
            lastHitTime = System.currentTimeMillis();
        }
    }

    public static int getRandomNumber() {
        Random random = new Random();
        return random.nextInt(31) + 40; // Generates a number between 40 and 70 inclusive
    }
}
