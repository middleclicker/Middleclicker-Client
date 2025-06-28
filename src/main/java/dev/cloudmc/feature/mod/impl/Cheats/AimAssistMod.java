package dev.cloudmc.feature.mod.impl.Cheats;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

import java.util.List;
import java.util.Objects;

public class AimAssistMod extends Mod {
    public AimAssistMod() {
        super(
                "AimAssist",
                "Assists your aim",
                Type.Cheats
        );

        Cloud.INSTANCE.settingManager.addSetting(new Setting("Mode", this, "Linear", 0, new String[]{"Linear", "Exponential", "Lockon"}));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Speed", this, 10, 1));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Range", this, 7, 4));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("MinFOV", this, 360, 10));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("MaxFOV", this, 360, 80));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Target", this, "Distance", 0, new String[]{"Distance", "Health"}));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Sprinting", this, false));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Mouse Pressed", this, true));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Breaking Blocks", this, true));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Holding Weapon", this, true));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Ignore Invis", this, true));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Raytracing", this, true));
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent e) {
        if (Cloud.INSTANCE.mc.thePlayer == null || Cloud.INSTANCE.mc.theWorld == null || Cloud.INSTANCE.mc.currentScreen != null)
            return;

        if (Cloud.INSTANCE.settingManager.getSettingByModAndName("AimAssist", "Breaking Blocks").isCheckToggled()) {
            MovingObjectPosition objectMouseOver = Cloud.INSTANCE.mc.objectMouseOver;
            if (objectMouseOver != null && objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                Block b = Cloud.INSTANCE.mc.theWorld.getBlock(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
                if (b != Blocks.air && !(b instanceof BlockLiquid) && b instanceof Block) {
                    return;
                }
            }
        }

        if (Cloud.INSTANCE.settingManager.getSettingByModAndName("AimAssist", "Holding Weapon").isCheckToggled() && !isPlayerHoldingWeapon()) {
            return;
        }

        if (Cloud.INSTANCE.settingManager.getSettingByModAndName("AimAssist", "Mouse Pressed").isCheckToggled() && !Mouse.isButtonDown(0)) {
            return;
        }

        if (Cloud.INSTANCE.settingManager.getSettingByModAndName("AimAssist", "Sprinting").isCheckToggled() && !Cloud.INSTANCE.mc.thePlayer.isSprinting()) {
            return;
        }

        int minFov = (int) Math.floor(Cloud.INSTANCE.settingManager.getSettingByModAndName("AimAssist", "MinFOV").getCurrentNumber());
        int maxFov = (int) Math.floor(Cloud.INSTANCE.settingManager.getSettingByModAndName("AimAssist", "MaxFOV").getCurrentNumber());
        List<EntityPlayer> entityPlayers = Cloud.INSTANCE.mc.theWorld.playerEntities;
        entityPlayers.remove(Cloud.INSTANCE.mc.thePlayer);
        EntityPlayer target = null;
        for (EntityPlayer entityPlayer : entityPlayers) {
            float yawToEntity = getYawToEntity(entityPlayer);
            if (Math.abs(yawToEntity) <= minFov/2D || Math.abs(yawToEntity) >= maxFov/2D) continue;
            if (Cloud.INSTANCE.mc.thePlayer.getDistanceToEntity(entityPlayer) > Cloud.INSTANCE.settingManager.getSettingByModAndName("AimAssist", "Range").getCurrentNumber()) continue;
            if (Cloud.INSTANCE.settingManager.getSettingByModAndName("AimAssist", "Ignore Invis").isCheckToggled() && entityPlayer.isInvisible()) continue;
            if (Cloud.INSTANCE.settingManager.getSettingByModAndName("AimAssist", "Raytracing").isCheckToggled() && !Cloud.INSTANCE.mc.thePlayer.canEntityBeSeen(entityPlayer)) continue;

            String targetMode = Cloud.INSTANCE.settingManager.getSettingByModAndName("AimAssist", "Target").getCurrentMode();
            if (Objects.equals(targetMode, "Distance")) {
                if (target == null || Cloud.INSTANCE.mc.thePlayer.getDistanceToEntity(entityPlayer) < Cloud.INSTANCE.mc.thePlayer.getDistanceToEntity(target)) {
                    target = entityPlayer;
                }
            } else if (Objects.equals(targetMode, "Health")) {
                if (target == null || entityPlayer.getHealth() < target.getHealth()) {
                    target = entityPlayer;
                }
            }
        }

        if (target == null) return;

        String aimMode = Cloud.INSTANCE.settingManager.getSettingByModAndName("AimAssist", "Mode").getCurrentMode();

        if (Objects.equals(aimMode, "Lockon")) {
            double deltaX = (target.posX + target.prevPosX) / 2.0 - Cloud.INSTANCE.mc.thePlayer.posX;
            double deltaZ = (target.posZ + target.prevPosZ) / 2.0 - Cloud.INSTANCE.mc.thePlayer.posZ;

            if (deltaX == 0.0 && deltaZ == 0.0) {
                return;
            }

            float yawToEntity = (float) (Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0);

            // Normalize yaw to [-180, 180]
            yawToEntity = ((yawToEntity % 360.0f) + 540.0f) % 360.0f - 180.0f;

            Cloud.INSTANCE.mc.thePlayer.rotationYaw = yawToEntity;
        } else if (Objects.equals(aimMode, "Linear")) {
            float yawToEntity = getYawToEntity(target);
            float speed = Cloud.INSTANCE.settingManager.getSettingByModAndName("AimAssist", "Speed").getCurrentNumber() / 60;
            if (yawToEntity < 0) {
                speed = -speed;
            }
            Cloud.INSTANCE.mc.thePlayer.rotationYaw += speed;
        } else if (Objects.equals(aimMode, "Exponential")) {
            float yawToEntity = getYawToEntity(target);
            float speed = Cloud.INSTANCE.settingManager.getSettingByModAndName("AimAssist", "Speed").getCurrentNumber();
            float absYaw = Math.abs(yawToEntity);
            float adjustedSpeed = (float) (speed * Math.pow(absYaw, 2));
            float minSpeed = 0.1f;
            float maxSpeed = 50.0f;
            adjustedSpeed = Math.min(Math.max(adjustedSpeed, minSpeed), maxSpeed);
            if (yawToEntity < 0) {
                adjustedSpeed = -adjustedSpeed;
            }
            Cloud.INSTANCE.mc.thePlayer.rotationYaw += adjustedSpeed;
        }
    }

    public static float getYawToEntity(Entity ent) {
        // Get player and entity positions
        double deltaX = ent.posX - Cloud.INSTANCE.mc.thePlayer.posX;
        double deltaZ = ent.posZ - Cloud.INSTANCE.mc.thePlayer.posZ;

        // Calculate the angle to the entity using atan2
        double yawToEntity = Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0; // Adjust for Minecraft's coordinate system

        // Get the player's current yaw
        float playerYaw = Cloud.INSTANCE.mc.thePlayer.rotationYaw;

        // Normalize player yaw to [0, 360]
        playerYaw = playerYaw % 360.0f;
        if (playerYaw < 0) playerYaw += 360.0f;

        // Normalize yaw to entity to [0, 360]
        yawToEntity = yawToEntity % 360.0;
        if (yawToEntity < 0) yawToEntity += 360.0;

        // Calculate the shortest yaw difference
        float yawDifference = (float) (yawToEntity - playerYaw);

        // Normalize to [-180, 180]
        if (yawDifference > 180.0f) yawDifference -= 360.0f;
        if (yawDifference < -180.0f) yawDifference += 360.0f;

        return yawDifference;
    }

    public static boolean isPlayerHoldingWeapon() {
        if (Cloud.INSTANCE.mc.thePlayer.getCurrentEquippedItem() == null) {
            return false;
        } else {
            Item item = Cloud.INSTANCE.mc.thePlayer.getCurrentEquippedItem().getItem();
            return item instanceof ItemSword || item instanceof ItemAxe;
        }
    }
}
