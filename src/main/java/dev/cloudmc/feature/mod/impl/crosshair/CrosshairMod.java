/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc.feature.mod.impl.crosshair;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import dev.cloudmc.helpers.ResolutionHelper;
import dev.cloudmc.helpers.render.Helper2D;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.awt.*;

public class CrosshairMod extends Mod {

    public static final LayoutManager layoutManager = new LayoutManager();

    public CrosshairMod() {
        super(
                "Crosshair",
                "Makes Crosshair customizable.",
                Type.Hud
        );

        Cloud.INSTANCE.settingManager.addSetting(new Setting("Color", this, new Color(255, 255, 255), new Color(255, 0, 0), 0, new float[]{0, 0}));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Cells", this, layoutManager.getLayout(0)));
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post e) {
        if (e.type == RenderGameOverlayEvent.ElementType.TEXT) {
            for (int row = 0; row < 11; row++) {
                for (int col = 0; col < 11; col++) {
                    if (getCells()[row][col] && isToggled()) {
                        Helper2D.drawRectangle(
                                ResolutionHelper.getWidth() / 2 - 5 + col,
                                ResolutionHelper.getHeight() / 2 - 5 + row,
                                1, 1, color()
                        );
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Pre e) {
        if (e.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            if (!e.isCanceled() && e.isCancelable()) {
                e.setCanceled(true);
            }
        }
    }

    private int color() {
        return Cloud.INSTANCE.settingManager.getSettingByModAndName(getName(), "Color").getColor().getRGB();
    }

    private boolean[][] getCells() {
        return Cloud.INSTANCE.settingManager.getSettingByModAndName(getName(), "Cells").getCells();
    }
}
