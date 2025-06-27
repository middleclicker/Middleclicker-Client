/*
 * Copyright (c) 2022 Mojang &
 * slightly edited by DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc.gui.titlescreen;

import dev.cloudmc.Cloud;
import dev.cloudmc.helpers.render.Helper2D;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import java.awt.*;

public class Panorama extends GuiScreen {

    public static int panoramaTimer = 0;

    private final DynamicTexture viewportTexture = new DynamicTexture(256, 256);

    private static final ResourceLocation catBackground = new ResourceLocation(Cloud.modID, "panorama/background1.png");
    private static final ResourceLocation girlBackground = new ResourceLocation(Cloud.modID, "panorama/background2.jpeg");

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {
        panoramaTimer++;
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        // Bind and draw the cat_background.jpg image
        this.mc.getTextureManager().bindTexture(girlBackground);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
        tessellator.addVertexWithUV(0, height, this.zLevel, 0, 1);
        tessellator.addVertexWithUV(width, height, this.zLevel, 1, 1);
        tessellator.addVertexWithUV(width, 0, this.zLevel, 1, 0);
        tessellator.addVertexWithUV(0, 0, this.zLevel, 0, 0);
        tessellator.draw();
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        Helper2D.drawRectangle(0, 0, width, height, new Color(0, 0, 0, 25).getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}