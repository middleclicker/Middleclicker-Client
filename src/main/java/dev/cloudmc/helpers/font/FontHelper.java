/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc.helpers.font;

import dev.cloudmc.Cloud;

public class FontHelper {

    private String font;

    public GlyphPageFontRenderer size15;
    public GlyphPageFontRenderer size17;
    public GlyphPageFontRenderer size20;
    public GlyphPageFontRenderer size30;
    public GlyphPageFontRenderer size40;

    public void init() {
        font = Cloud.INSTANCE.optionManager.getOptionByName("Font Changer").getCurrentMode();
        size15 = GlyphPageFontRenderer.create(font, 15, true, true, true);
        size17 = GlyphPageFontRenderer.create(font, 17, true, true, true);
        size20 = GlyphPageFontRenderer.create(font, 20, true, true, true);
        size30 = GlyphPageFontRenderer.create(font, 30, true, true, true);
        size40 = GlyphPageFontRenderer.create(font, 40, true, true, true);
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }
}