/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc.gui.titlescreen;

import dev.cloudmc.Cloud;
import dev.cloudmc.gui.titlescreen.buttons.IconButton;
import dev.cloudmc.gui.titlescreen.buttons.TextButton;
import dev.cloudmc.helpers.font.GlyphPageFontRenderer;
import dev.cloudmc.helpers.render.Helper2D;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiSelectWorld;

import java.io.IOException;
import java.util.ArrayList;

public class TitleScreen extends Panorama {

    private final ArrayList<TextButton> textButtons = new ArrayList<>();
    private final ArrayList<IconButton> iconButtons = new ArrayList<>();
    private final ArrayList<IconButton> bottomIconButtons = new ArrayList<>();

    public TitleScreen() {
        textButtons.add(new TextButton("Singleplayer", width / 2 - 75, height / 2));
        textButtons.add(new TextButton("Multiplayer", width / 2 - 75, height / 2 + 25));
        bottomIconButtons.add(new IconButton("settings.png", 0, height - 35));
        bottomIconButtons.add(new IconButton("languages.png", 0, height - 35));
        iconButtons.add(new IconButton("cross.png", width - 25, 5));
    }

    /**
     * Renders button text and logos on the screen
     *
     * @param mouseX       The current X position of the mouse
     * @param mouseY       The current Y position of the mouse
     * @param partialTicks The partial ticks used for rendering
     */

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        int y = 0;
        for (TextButton textButton : textButtons) {
            textButton.renderButton(width / 2 - 75, height / 2 + y * 25, mouseX, mouseY, 0x80ffffff);
            y++;
        }

        int x = 0;
        int baseX = width/2 - (20 * bottomIconButtons.size() + 3 * (bottomIconButtons.size()-1))/2;
        for (IconButton iconButton : bottomIconButtons) {
            iconButton.renderButton(baseX + x*(20+3), height-35, mouseX, mouseY, 0x80ffffff, 10, 10);
            x++;
        }

        for (IconButton iconButton : iconButtons) {
            if (iconButton.getIcon().equals("cross.png")) {
                iconButton.renderButton(width - 25, 5, mouseX, mouseY, 0x80ffffff);
            }
        }

        drawLogo();
        drawCopyright();
    }

    /**
     * Is called when any mouse button is pressed. Adds functionality to every button on screen
     *
     * @param mouseX      The current X position of the mouse
     * @param mouseY      The current Y position of the mouse
     * @param mouseButton The current mouse button which is pressed
     */

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (TextButton textButton : textButtons) {
            if (textButton.isHovered(mouseX, mouseY)) {
                switch (textButton.getText()) {
                    case "Singleplayer":
                        mc.displayGuiScreen(new GuiSelectWorld(this));
                        break;
                    case "Multiplayer":
                        mc.displayGuiScreen(new GuiMultiplayer(this));
                        break;
                }
            }
        }

        for (IconButton iconButton : bottomIconButtons) {
            if (iconButton.isHovered(mouseX, mouseY)) {
                switch (iconButton.getIcon()) {
                    case "settings.png":
                        mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                        break;
                    case "languages.png":
                        mc.displayGuiScreen(new GuiLanguage(this, mc.gameSettings, mc.getLanguageManager()));
                        break;
                }
            }
        }

        for (IconButton iconButton : iconButtons) {
            if (iconButton.isHovered(mouseX, mouseY)) {
                if (iconButton.getIcon().equals("cross.png")) {
                    mc.shutdown();
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Draws the main "Cloud" Text and the Logo in the middle
     */

    private void drawLogo() {
        GlyphPageFontRenderer fontRenderer = Cloud.INSTANCE.fontHelper.size40;
        fontRenderer.drawString(Cloud.modName, width / 2f - fontRenderer.getStringWidth(Cloud.modName) / 2f, height / 2f - 27.5f, -1);
        Helper2D.drawPicture(width / 2 - 30, height / 2 - 78, 60, 60, 0x40ffffff, "cloudlogo.png");
    }

    /**
     * Draws the "Cloud Client" Text and Mojang Copyright Notice on the bottom
     */

    private void drawCopyright() {
        GlyphPageFontRenderer fontRenderer = Cloud.INSTANCE.fontHelper.size17;
        String copyright = "Copyright Mojang Studios. Do not distribute!";
        String text = Cloud.modName + " Client " + Cloud.modVersion;
        fontRenderer.drawString(copyright, width - fontRenderer.getStringWidth(copyright)-5, height - fontRenderer.getFontHeight()-5, 0x50ffffff);
        fontRenderer.drawString(text, 4+5, height - fontRenderer.getFontHeight()-5, 0x50ffffff);
    }
}