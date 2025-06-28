package dev.cloudmc.feature.mod.impl.Hud;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import dev.cloudmc.helpers.Pair;
import dev.cloudmc.helpers.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ComboCounterRecode extends Mod {

    private final Minecraft mc;

    private static int combo;
    private static Entity potentialTarget;
    private static Timer comboResetTimer;

    private static List<List<Pair<Integer, Long>>> recordedCombos;
    private static List<Pair<Integer, Long>> currentCombo;
    private static Timer recordingTimer;
    private boolean recording;
    private File recordingDirectory, recordingFile;
    private long recordingLength;

    private final ScheduledExecutorService scheduler;

    public ComboCounterRecode() {
        super(
                "ComboCounterRecode",
                "Combo Counter Recode",
                Type.Hud
        );

        this.mc = Cloud.INSTANCE.mc;

        combo = 0;
        potentialTarget = null;
        comboResetTimer = new Timer();

        recordedCombos = new ArrayList<>();
        currentCombo = new ArrayList<>();
        recordingTimer = new Timer();
        this.recording = false;
        this.recordingDirectory = new File("F:/Video/Recording");
        this.recordingFile = null;
        this.recordingLength = 0L;

        this.scheduler = Executors.newScheduledThreadPool(1);

        Cloud.INSTANCE.settingManager.addSetting(new Setting("Mode", this, "Legacy", 0, new String[]{"Modern", "Legacy, Hidden"}));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Background", this, true));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Font Color", this, new Color(255, 255, 255), new Color(255, 0, 0), 0, new float[]{0, 0}));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Reset Time", this, 5, 10));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Only Players", this, false));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Record Combos", this, true));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Recording Bind", this, Keyboard.KEY_BACKSLASH));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Combo Threshold", this, 6, 4));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Chat Output", this, true));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Cut Video", this, true));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Process Delay", this, 5, 3));
    }

    public static void dealtHit(int damagedEntityID) {
        if (potentialTarget != null && damagedEntityID == potentialTarget.getEntityId()) {
            combo++;
            comboResetTimer.reset();
            currentCombo.add(new Pair<>(combo, recordingTimer.getTimePassedMS()));
        }
    }

    public static void gotHit() {
        combo = 0;
        comboResetTimer.reset();
        if (currentCombo != null && currentCombo.size() >= getComboThreshold()) {
            recordedCombos.add(currentCombo);
            currentCombo = new ArrayList<>(); // Re-initialize currentCombo to prevent java linking BS
        }
    }

    public static void reset() { // Purely for code readability
        gotHit();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (nullCheck()) return;
        if (comboResetTimer.passedS(getResetTime())) {
            reset();
        }
    }

    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent event) {
        if (nullCheck()) return;
        if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState()) {
            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                Entity entity = mc.objectMouseOver.entityHit;
                if (!isOnlyPlayers() || (isOnlyPlayers() && entity instanceof EntityPlayer)) {
                    potentialTarget = entity;
                }
            }
        }
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (nullCheck()) return;
        if (isRecordCombos() && Keyboard.isKeyDown(getRecordingBind())) {
            this.recording = !this.recording;
            if (recording) {
                currentCombo = new ArrayList<>();
                recordedCombos = new ArrayList<>();
                recordingTimer.reset();
                this.recordingLength = 0L;
                this.recordingFile = new File(this.recordingDirectory, generateOBSFilename());
            } else {
                this.recordingLength = recordingTimer.getTimePassedMS();

                if (currentCombo != null && !currentCombo.isEmpty() && currentCombo.size() >= getComboThreshold()) {
                    recordedCombos.add(currentCombo);
                }

                List<Pair<String, String>> cutPairs = generateCutPairs();

                if (isChatOutput()) {
                    mc.thePlayer.addChatMessage(new ChatComponentText("Recorded Combos: Hours:Minutes:Seconds:MS"));
                    for (Pair<String, String> cutPair : cutPairs) {
                        mc.thePlayer.addChatMessage(new ChatComponentText(cutPair.getKey() + " | " + cutPair.getValue()));
                    }
                }

                if (isCutVideo()) {
                    this.scheduler.schedule(() -> cutVideo(cutPairs), getProcessDelay(), TimeUnit.SECONDS);
                }
            }
        }
    }

    public void cutVideo(List<Pair<String, String>> cutPairs) {
        if (!this.recordingFile.exists() || !this.recordingFile.isFile()) {
            System.out.println("Recording file does not exist or is not a file: " + recordingFile.getAbsolutePath());
            return;
        }

        for (int videoIndex = 0; videoIndex < cutPairs.size(); videoIndex++) {
            Pair<String, String> cutPair = cutPairs.get(videoIndex);
            String originalFilename = this.recordingFile.getName().replaceFirst("\\.[^.]+$", "");
            String currentFilename = String.format("%s_%d.mp4", originalFilename, videoIndex);
            String outputFilePath = Paths.get(this.recordingDirectory.getAbsolutePath(), currentFilename).toAbsolutePath().toString();

            String startTime = cutPair.getKey();
            String endTime = cutPair.getValue();

            try {
                ProcessBuilder pb = new ProcessBuilder(
                        "ffmpeg",
                        "-i", recordingFile.getAbsolutePath(),
                        "-ss", startTime,
                        "-to", endTime,
                        "-c", "copy",
                        "-y", // Overwrite output file if it exists
                        outputFilePath
                );
                pb.redirectErrorStream(true); // Redirect error stream to output stream for simpler handling
                Process process = pb.start();

                StringBuilder consoleOutput = new StringBuilder();
                Thread outputReader = new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            consoleOutput.append(line).append("\n");
                        }
                    } catch (Exception e) {
                        consoleOutput.append("Error reading FFmpeg output: ").append(e.getMessage()).append("\n");
                    }
                });
                outputReader.start();

                // Wait for process to complete
                int exitCode = process.waitFor();
                outputReader.join(); // Ensure output reading is complete

                if (exitCode == 0) {
                    System.out.println("Video cut successfully: " + outputFilePath);
                    System.err.println("FFmpeg output: " + consoleOutput);
                } else {
                    System.err.println("Error cutting video. FFmpeg exit code: " + exitCode);
                    System.err.println("FFmpeg output: " + consoleOutput);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<Pair<String, String>> generateCutPairs() {
        List<Pair<String, String>> cutPairs = new ArrayList<>();
        for (List<Pair<Integer, Long>> combos : recordedCombos) {
            if (combos.isEmpty()) continue;
            int comboLength = combos.size();
            Pair<Integer, Long> firstHit = combos.get(0);
            Pair<Integer, Long> lastHit = combos.get(comboLength - 1);

            long startMS = Math.max(firstHit.getValue() - 2000L, 0L);
            long endMS = Math.min(lastHit.getValue() + 2000L, this.recordingLength);

            cutPairs.add(new Pair<>(formatMillisToTime(startMS), formatMillisToTime(endMS)));
        }
        return cutPairs;
    }

    public static String formatMillisToTime(long millis) {
        long hours = millis / (1000 * 60 * 60);
        long minutes = (millis % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (millis % (1000 * 60)) / 1000;
        long milliseconds = millis % 1000;

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
    }

    private String generateOBSFilename() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
        return now.format(formatter) + ".mp4"; // Predict the filename
    }

    private static int getComboThreshold() {
        return (int) Math.floor(Cloud.INSTANCE.settingManager.getSettingByModAndName("ComboCounterRecode", "Combo Threshold").getCurrentNumber());
    }

    private static float getResetTime() {
        return Cloud.INSTANCE.settingManager.getSettingByModAndName("ComboCounterRecode", "Combo Threshold").getCurrentNumber();
    }

    private static long getProcessDelay() {
        return (long) Cloud.INSTANCE.settingManager.getSettingByModAndName("ComboCounterRecode", "Process Delay").getCurrentNumber();
    }

    private static boolean isRecordCombos() {
        return Cloud.INSTANCE.settingManager.getSettingByModAndName("ComboCounterRecode", "Record Combos").isCheckToggled();
    }

    private static boolean isOnlyPlayers() {
        return Cloud.INSTANCE.settingManager.getSettingByModAndName("ComboCounterRecode", "Only Players").isCheckToggled();
    }

    private static boolean isChatOutput() {
        return Cloud.INSTANCE.settingManager.getSettingByModAndName("ComboCounterRecode", "Chat Output").isCheckToggled();
    }

    private static boolean isCutVideo() {
        return Cloud.INSTANCE.settingManager.getSettingByModAndName("ComboCounterRecode", "Cut Video").isCheckToggled();
    }

    private static int getRecordingBind() {
        return Cloud.INSTANCE.settingManager.getSettingByModAndName("ComboCounterRecode", "Recording Bind").getKey();
    }

    // Returns true if the current environment is invalid.
    private static boolean nullCheck() {
        return Cloud.INSTANCE.mc.thePlayer == null && Cloud.INSTANCE.mc.theWorld == null;
    }

    public static int getCombo() {
        return combo;
    }
}
