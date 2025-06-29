package dev.cloudmc.gui.hudeditor.impl.impl;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.impl.Hud.ComboCounterRecode;
import dev.cloudmc.gui.Style;
import dev.cloudmc.gui.hudeditor.impl.HudMod;
import dev.cloudmc.helpers.render.GLHelper;
import dev.cloudmc.helpers.render.Helper2D;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ComboCounterRecodeHud extends HudMod {
    public ComboCounterRecodeHud(String name, int x, int y) {
        super(name, x, y);
        setW(80);
        setH(20);
    }

    @Override
    public void renderMod(int mouseX, int mouseY) {
        GLHelper.startScale(getX(), getY(), getSize());
        if (Cloud.INSTANCE.modManager.getMod(getName()).isToggled()) {
            String text = ComboCounterRecode.getCombo() + " Combo";
            if (isModern()) {
                if (isBackground()) {
                    Helper2D.drawRoundedRectangle(getX(), getY(), getW(), getH(), 2, Style.getColor(50).getRGB(), 0);
                }
                Cloud.INSTANCE.fontHelper.size20.drawString(
                        text,
                        getX() + getW() / 2f - Cloud.INSTANCE.fontHelper.size20.getStringWidth(text) / 2f,
                        getY() + 6,
                        getColor()
                );
            } else {
                if (isBackground()) {
                    Helper2D.drawRoundedRectangle(getX(), getY(), getW(), getH(), 2, Style.getColor(50).getRGB(), 0);
                }
                Cloud.INSTANCE.mc.fontRendererObj.drawString(
                        text,
                        getX() + getW() / 2 - Cloud.INSTANCE.fontHelper.size20.getStringWidth(text) / 2,
                        getY() + 6,
                        getColor()
                );
            }
            super.renderMod(mouseX, mouseY);
        }
        GLHelper.endScale();
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent.Pre.Text e) {
        GLHelper.startScale(getX(), getY(), getSize());
        if (Cloud.INSTANCE.modManager.getMod(getName()).isToggled()) {
            String text = ComboCounterRecode.getCombo() + " Combo";
            if (isModern()) {
                if (isBackground()) {
                    Helper2D.drawRoundedRectangle(getX(), getY(), getW(), getH(), 2, Style.getColor(50).getRGB(), 0);
                }
                Cloud.INSTANCE.fontHelper.size20.drawString(
                        text,
                        getX() + getW() / 2f - Cloud.INSTANCE.fontHelper.size20.getStringWidth(text) / 2f,
                        getY() + 6,
                        getColor()
                );
            } else {
                if (isBackground()) {
                    Helper2D.drawRoundedRectangle(getX(), getY(), getW(), getH(), 2, Style.getColor(50).getRGB(), 0);
                }
                Cloud.INSTANCE.mc.fontRendererObj.drawString(
                        text,
                        getX() + getW() / 2 - Cloud.INSTANCE.fontHelper.size20.getStringWidth(text) / 2,
                        getY() + 6,
                        getColor()
                );
            }
        }
        GLHelper.endScale();
    }

    private int getColor() {
        return Cloud.INSTANCE.settingManager.getSettingByModAndName(getName(), "Font Color").getColor().getRGB();
    }

    private boolean isModern() {
        return Cloud.INSTANCE.settingManager.getSettingByModAndName(getName(), "Mode").getCurrentMode().equalsIgnoreCase("Modern");
    }

    private boolean isBackground() {
        return Cloud.INSTANCE.settingManager.getSettingByModAndName(getName(), "Background").isCheckToggled();
    }
}
