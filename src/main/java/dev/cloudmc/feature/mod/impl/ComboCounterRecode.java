package dev.cloudmc.feature.mod.impl;

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
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ComboCounterRecode extends Mod {
    public ComboCounterRecode() {
        super(
                "ComboCounterRecode",
                "Combo Counter Recode",
                Type.Hud
        );
        String[] mode = {"Modern", "Legacy, Hidden"};
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Mode", this, "Legacy", 0, mode));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Background", this, true));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Font Color", this, new Color(255, 255, 255), new Color(255, 0, 0), 0, new float[]{0, 0}));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Recording Bind", this, Keyboard.KEY_BACKSLASH));
    }

    private final Minecraft mc = Cloud.INSTANCE.mc;
    String currentRecordingFile;
    private static final Timer timer = new Timer();
    private long recordingLength;
    private static List<Pair<Integer, Long>> potentialCombo;
    private static List<List<Pair<Integer, Long>>> recordedCombos;
    private boolean recording;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void onEnable() {
        super.onEnable();
        Cloud.INSTANCE.comboHelper.timer.reset();
        currentRecordingFile = "";
        timer.reset();
        potentialCombo = new ArrayList<>();
        recordedCombos = new ArrayList<>();
        recording = false;
        recordingLength = 0;
    }

    @Override
    public void onDisable() {
        Cloud.INSTANCE.comboHelper.reset();
        scheduler.shutdown();
        super.onDisable();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (Cloud.INSTANCE.comboHelper.timer.getTimePassedMS() >= 5000) {
            Cloud.INSTANCE.comboHelper.reset();
        }
    }

    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent event) {
        if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState()) {
            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                Cloud.INSTANCE.comboHelper.potentialTarget = mc.objectMouseOver.entityHit;
            }
        }
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (Keyboard.isKeyDown(Cloud.INSTANCE.settingManager.getSettingByModAndName(getName(), "Recording Bind").getKey())) {
            recording = !recording;
            if (recording) {
                timer.reset();
                recordingLength = 0;
                potentialCombo.clear();
                recordedCombos.clear();
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
                currentRecordingFile = now.format(formatter); // Predict the filename
            } else {
                recordingLength = timer.getTimePassedMS();
                if (!potentialCombo.isEmpty()) {
                    recordedCombos.add(potentialCombo);
                }

                System.out.println(currentRecordingFile);
                System.out.println(recordedCombos);

                // Process and cut the files
                String baseDirectory = "F:/Video/Recording";
                File recordingFile = new File(baseDirectory + "/" + currentRecordingFile + ".mp4");
                List<Pair<Long, Long>> cutPairs = new ArrayList<>();
                for (List<Pair<Integer, Long>> combos : recordedCombos) {
                    long startMS, endMS;
                    if (combos.get(0).getValue() - 2000 < 0) {
                        startMS = 0;
                    } else {
                        startMS = combos.get(0).getValue() - 2000;
                    }
                    if (combos.get(combos.size()-1).getValue() + 2000 > recordingLength) {
                        endMS = recordingLength;
                    } else {
                        endMS = combos.get(combos.size()-1).getValue() + 2000;
                    }
                    cutPairs.add(new Pair<>(startMS, endMS));
                }

                scheduler.schedule(() -> {
                    if (recordingFile.exists()) {
                        System.out.println("Recording file exists.");
                        cutVideo(recordingFile, cutPairs, baseDirectory);
                    } else {
                        System.out.println("Recording file does not exist.");
                    }
                }, 5, TimeUnit.SECONDS);

                potentialCombo.clear();
                recordedCombos.clear();
                recordingLength = 0;
            }
        }
    }

    public static void dealtHit(int combo) {
        potentialCombo.add(new Pair<>(combo, timer.getTimePassedMS()));
        System.out.println(potentialCombo);
    }

    public static void gotHit() {
        if (potentialCombo.size() >= 4) {
            recordedCombos.add(potentialCombo);
            System.out.println("Added potential combo");
            System.out.println(potentialCombo);
            potentialCombo = new ArrayList<>();
        }
    }

    public static String formatMillisToTime(long millis) {
        long hours = millis / (1000 * 60 * 60);
        long minutes = (millis % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (millis % (1000 * 60)) / 1000;
        long milliseconds = millis % 1000;

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
    }

    public void cutVideo(File recordingFile, List<Pair<Long, Long>> cutPairs, String outputDir) {
        System.out.println("Entered loop");
        if (!recordingFile.exists() || !recordingFile.isFile()) {
            System.out.println("Recording file does not exist or is not a file: " + recordingFile.getAbsolutePath());
            return;
        }

        // Ensure output directory exists
        Path outputDirPath = Paths.get(outputDir);
        try {
            Files.createDirectories(outputDirPath);
        } catch (Exception e) {
            System.err.println("Failed to create output directory: " + e.getMessage());
            return;
        }

        int videoIndex = 0;
        for (Pair<Long, Long> cutPair : cutPairs) {
            String baseName = recordingFile.getName().replaceFirst("\\.[^.]+$", ""); // Removes extension (e.g., "abc.mp4" -> "abc")
            String outputFilePath = outputDirPath.resolve(String.format("%s_%d.mp4", baseName, videoIndex)).toString();
            System.out.println("Processing cut: " + cutPair);

            try {
                // Format times as seconds (FFmpeg accepts integers as seconds)
                String startTime = formatMillisToTime(cutPair.getKey());
                String endTime = formatMillisToTime(cutPair.getValue());

                // Construct FFmpeg command
                ProcessBuilder pb = new ProcessBuilder(
                        "ffmpeg",
                        "-i", recordingFile.getAbsolutePath(),
                        "-ss", startTime,
                        "-to", endTime,
                        "-c", "copy",
                        "-y", // Overwrite output file if it exists
                        outputFilePath
                );

                // Redirect error stream to output stream for simpler handling
                pb.redirectErrorStream(true);

                // Start the process
                Process process = pb.start();

                // Read output stream in a separate thread to prevent blocking
                StringBuilder output = new StringBuilder();
                Thread outputReader = new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            output.append(line).append("\n");
                        }
                    } catch (Exception e) {
                        output.append("Error reading FFmpeg output: ").append(e.getMessage()).append("\n");
                    }
                });
                outputReader.start();

                // Wait for process to complete
                int exitCode = process.waitFor();
                outputReader.join(); // Ensure output reading is complete

                if (exitCode == 0) {
                    System.out.println("Video cut successfully: " + outputFilePath);
                    System.err.println("FFmpeg output: " + output.toString());
                } else {
                    System.err.println("Error cutting video. FFmpeg exit code: " + exitCode);
                    System.err.println("FFmpeg output: " + output.toString());
                }

                videoIndex++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
                System.err.println("Process interrupted: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error processing cut " + cutPair + ": " + e.getMessage());
            }
        }
    }
}
