/*
 * Copyright (c) 2022 DupliCAT
 * GNU Lesser General Public License v3.0
 */

package dev.cloudmc.feature.mod;

import dev.cloudmc.feature.mod.impl.Cheats.*;
import dev.cloudmc.feature.mod.impl.Hud.*;
import dev.cloudmc.feature.mod.impl.Mechanic.*;
import dev.cloudmc.feature.mod.impl.Tweaks.*;
import dev.cloudmc.feature.mod.impl.Visual.*;
import dev.cloudmc.feature.mod.impl.crosshair.CrosshairMod;

import java.util.ArrayList;

public class ModManager {

    public ArrayList<Mod> mods = new ArrayList<>();

    public ModManager() {
        init();
    }

    /**
     * Initializes all mods
     */

    public void init() {
        addMod(new ToggleSprintMod());
        addMod(new ToggleSneakMod());
        addMod(new FpsMod());
        addMod(new KeystrokesMod());
        addMod(new ArmorMod());
        addMod(new FullbrightMod());
        addMod(new CoordinatesMod());
        addMod(new ServerAddressMod());
        addMod(new PingMod());
        addMod(new CpsMod());
        addMod(new PotionMod());
        addMod(new TimeMod());
        addMod(new SpeedIndicatorMod());
        addMod(new FreelookMod());
        addMod(new CrosshairMod());
        addMod(new MotionblurMod());
        addMod(new GuiTweaksMod());
        addMod(new BlockOverlayMod());
        addMod(new BlockInfoMod());
        addMod(new ReachDisplayMod());
        addMod(new ComboCounterRecode());
        addMod(new ZoomMod());
        addMod(new DayCounterMod());
        addMod(new NoHurtCamMod());
        addMod(new ScrollTooltipsMod());
        addMod(new ParticleMultiplierMod());
        addMod(new NickHiderMod());
        addMod(new ScoreboardMod());
        addMod(new BossbarMod());
        addMod(new DirectionMod());
        addMod(new HitColorMod());
        addMod(new TimeChangerMod());
        addMod(new NameTagMod());
        addMod(new NoJumpDelay());
        addMod(new AimAssistMod());
        addMod(new FakePlayerMod());
        addMod(new ModernKeyHandlingMod());
        addMod(new HitboxesMod());
        addMod(new ChatTweaksMod());
        addMod(new HitselectMod());
        addMod(new FakelagMod());
    }

    /**
     * @return Returns an Arraylist of all mods
     */

    public ArrayList<Mod> getMods() {
        return mods;
    }

    /**
     * Returns a given mod using its name
     * @param name The name of the mod
     * @return The returned mod
     */

    public Mod getMod(String name) {
        for (Mod m : mods) {
            if (m.getName().equals(name)) {
                return m;
            }
        }

        return null;
    }

    /**
     * Adds a mod to the list
     * @param mod The mod which should be added
     */

    public void addMod(Mod mod) {
        mods.add(mod);
    }
}
