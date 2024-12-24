package com.tugalsan.dsk.automation.bath_serial;

import com.tugalsan.api.cast.client.TGS_CastUtils;
import com.tugalsan.api.desktop.server.TS_DesktopDialogInfoUtils;
import com.tugalsan.api.desktop.server.TS_DesktopDialogInputListUtils;
import com.tugalsan.api.desktop.server.TS_DesktopMainUtils;
import com.tugalsan.api.file.properties.server.TS_FilePropertiesUtils;
import com.tugalsan.api.file.server.TS_FileUtils;
import com.tugalsan.api.file.server.TS_FileWatchUtils;
import com.tugalsan.api.file.txt.server.TS_FileTxtUtils;
import com.tugalsan.api.function.client.TGS_FuncEffectivelyFinal;
import com.tugalsan.api.list.client.TGS_ListUtils;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.os.server.TS_OsPlatformUtils;
import com.tugalsan.api.serialcom.kincony.server.KC868_A32_R1_2.TS_SerialComKinConyKC868_A32_R1_2;
import com.tugalsan.api.stream.client.TGS_StreamUtils;
import com.tugalsan.api.thread.server.async.TS_ThreadAsync;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncLst;
import com.tugalsan.api.thread.server.TS_ThreadWait;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.IntStream;

//WHEN RUNNING IN NETBEANS, ALL DEPENDENCIES SHOULD HAVE TARGET FOLDER!
//cd C:\me\codes\com.tugalsan\dsk\com.tugalsan.dsk.automation.bath_serial
//java --enable-preview --add-modules jdk.incubator.vector -jar target/com.tugalsan.dsk.automation.bath_serial-jar-with-dependencies.jar COM3
public class Main {

    final private static TS_Log d = TS_Log.of(true, Main.class);

    public static volatile Mem_Int mem_int_last;

    final public static TS_ThreadSyncLst<List<Integer>> cmdValues16 = TS_ThreadSyncLst.ofSlowRead();
    public static volatile int mode = 0;
    public static volatile int modeRequested = 1;
    public static volatile GUI gui;

    public static Path fileCmd;
    /*C:\com.tugalsan.dsk.automation.bath_serial\cmd.txt
    bath_timer_0=0
    bath_timer_1=0
    bath_timer_2=1800
    bath_timer_3=190
    bath_timer_4=190
    bath_timer_5=190
    bath_timer_6=15
    bath_timer_7=190
    bath_timer_8=180
    bath_timer_9=90
    bath_timer_10=190
    bath_timer_11=120
    bath_timer_12=1800
    bath_timer_13=0
    bath_timer_14=0
    bath_timer_15=0
     */
    public static Path fileRes;
    /*C:\com.tugalsan.dsk.automation.bath_serial\res.txt
    02.05.2023 12:01:04 CMD_DONE
     */
    final public static String PROPS_PARAM_PREFIXS = "bath_timer_";
    public static String COMX;

    public static TS_ThreadSyncTrigger killTrigger = TS_ThreadSyncTrigger.of();

    public static void main(String... s) {
        //FOLDERS
        var fileFolderName = "com.tugalsan.dsk.automation.bath_serial";
        var fileFolder = TS_OsPlatformUtils.isWindows()
                ? Path.of("C:").resolve(fileFolderName)
                : Path.of("/home/ku_yhk/" + fileFolderName);
        fileCmd = fileFolder.resolve("cmd.txt");
        fileRes = fileFolder.resolve("res.txt");
        //PREPARE INFO
        var sb = new StringBuilder()
                .append("USAGE: java --enable-preview --add-modules jdk.incubator.vector \\")
                .append("\n       -jar target/com.tugalsan.dsk.automation.bath_serial-1.0-SNAPSHOT-jar-with-dependencies.jar COMX");
        var portNamesFull = TS_SerialComKinConyKC868_A32_R1_2.listPortNamesFull();
        var portNames = TS_SerialComKinConyKC868_A32_R1_2.listPortNames();
        if (portNames.isEmpty()) {
            sb.append("\nERROR: NO PORT DETECTED!");
        } else {
            sb.append("\nPARAM OPTIONS:");
            IntStream.range(0, Math.min(portNames.size(), portNamesFull.size())).forEachOrdered(i -> {
                sb.append("\n ").append(portNames.get(i)).append(": ").append(portNamesFull.get(i));
            });
        }
        //EXIT IF PORT LIST EMPTY
        if (portNames.isEmpty()) {
            TS_DesktopDialogInfoUtils.show("HOW TO USE (WARNING: CLI PORT-NAME NOT PRESENTED)", sb.toString());
            TS_ThreadWait.seconds(null, null, 5);
            System.exit(0);
        }
        //IF PORT NOT GIVEN, ASK
        if (s.length == 0) {
            var portNameIdx = TS_DesktopDialogInputListUtils.show(null, "HOW TO USE (WARNING: PORT-NAME NOT GIVEN)", sb.toString(), 0, portNames).orElse(null);
            if (portNameIdx == null) {
                d.cr("main", "Exit by cancel.");
                System.exit(0);
            }
            COMX = portNames.get(portNameIdx);
        }
        //IF PORT GIVEN ON CLI, CHECK
        if (s.length != 0) {
            COMX = portNames.stream().filter(pn -> Objects.equals(s[0], pn)).findAny().orElse(null);
            if (COMX == null) {
                TS_DesktopDialogInfoUtils.show("HOW TO USE (WARNING: CLI PORT-NAME WRONG)", sb.toString());
                TS_ThreadWait.seconds(null, null, 5);
                System.exit(0);
            }
        }
        //PRINT DECIDED PORT
        System.out.println("Selected Port: [" + COMX + "]");
        //SHOW GUI
        TS_DesktopMainUtils.setThemeAndinvokeLaterAndFixTheme(() -> gui = new GUI());
        //DO STH I DONT REMEMBER 
        TS_ThreadAsync.now(Main.killTrigger, kt -> {
            while (true) {
                //IF cmdValues16 IS EMPTY, FIND A WAY TO FILL IT UP
                if (cmdValues16.isEmpty()) {
                    mem_int_last = Mem_Int.of();
                    //IF GUI AVAILABLE UPDATE RENDERED ITEMS
                    if (gui != null) {
                        gui.taReply.setText(mem_int_last.toString());
                        gui.taReply.append("\nmode:" + TGS_FuncEffectivelyFinal.ofStr()
                                .anoint(val -> "Okunuyor")
                                .anointIf(val -> mem_int_last.mode.isPresent() && mem_int_last.mode.value() == 0, val -> "DÜĞME TEST")
                                .anointIf(val -> mem_int_last.mode.isPresent() && mem_int_last.mode.value() == 1, val -> "YH PROGRAMI")
                                .coronate()
                        );
                        if (mem_int_last.status == Mem_Int.STATUS.OK) {
                            IntStream.range(0, 16).forEachOrdered(i -> {//BUTTON UPDATE
                                var memVal = mem_int_last.mem_int.value().get(32 * 3 + i);
                                var lstStr = gui.lstValues.get(i);
                                if (Objects.equals(memVal, lstStr)) {
                                    return;
                                }
                                gui.lstValues.get(i).setText(memVal.toString());
                            });
                        }
                    }
                    //IF fileCmd NOT EXISTS FILL IT
                    if (!TS_FileUtils.isExistFile(fileCmd)) {
                        var sj = new StringJoiner("\n");
                        IntStream.range(0, 16).forEachOrdered(i -> {
                            if (mem_int_last.lstTI.size() > i) {//NEEDED
                                sj.add(PROPS_PARAM_PREFIXS + i + "=" + mem_int_last.lstTI.get(i));
                            }
                        });
                        TS_FileTxtUtils.toFile(sj.toString(), fileCmd, false);
                    }
                    //IF fileRes NOT EXISTS FILL IT
                    if (!TS_FileUtils.isExistFile(fileRes)) {
                        TS_FileTxtUtils.toFile(TS_FileUtils.getTimeLastModified(fileCmd) + " CMD_INIT", fileRes, false);
                    }
                    //CHANGE MODE TO PROGRAM IF NOT SET BEFORE
                    if (mem_int_last.mode.orElse(e -> 0) == 0) {
                        var result = TS_SerialComKinConyKC868_A32_R1_2.mode_setIdx(killTrigger, COMX, modeRequested);
                        d.ce("mode_setIdx", modeRequested, result);
                    }
                    continue;
                }
                //IF cmdValues16 IS NOT EMPTY, FETCH FIRST, SET MEM
                var lst = cmdValues16.removeAndPopFirst(val -> true);
                d.ce("set_lst", lst);
                var lstIdx = TGS_StreamUtils.toLst(IntStream.range(0, lst.size()).filter(i -> lst.get(i) != 0));
                d.ce("set_osc", lstIdx);
                if (TS_SerialComKinConyKC868_A32_R1_2.memInt_setAll(Main.killTrigger, COMX, lst).isVoid() && TS_SerialComKinConyKC868_A32_R1_2.digitalOut_oscilateAll(Main.killTrigger, COMX, lstIdx).isVoid()) {
                    gui.taReply.setText("Değişiklik başarılı.");
                    TS_FileTxtUtils.toFile(TS_FileUtils.getTimeLastModified(fileRes) + " CMD_DONE", fileRes, false);
                } else {
                    gui.taReply.setText("Değişiklik BAŞARISIZ!");
                    TS_FileTxtUtils.toFile(TS_FileUtils.getTimeLastModified(fileRes) + " CMD_FAILED", fileRes, false);
                }
            }
        });

        //FILL cmdValues16 with file watcher on MODIFY
        TS_FileWatchUtils.file(Main.killTrigger, fileCmd, () -> {
            d.cr("watcher", "detected");
            TS_ThreadWait.seconds("wait.watch", Main.killTrigger, 1);
            var props = TS_FilePropertiesUtils.createPropertyReader(fileCmd);
            if (!props.isPresent()) {
                d.cr("watcher", "props.isEmpty()");
                return;
            }
            List<Integer> bath_timers = TGS_ListUtils.of();
            IntStream.range(0, 16).forEachOrdered(i -> {
                var val = TGS_CastUtils.toInteger(props.value().getProperty(PROPS_PARAM_PREFIXS + i));
                if (val == null) {
                    d.cr("watcher", "param_null", PROPS_PARAM_PREFIXS + i);
                    return;
                }
                bath_timers.add(val);
            });
            if (bath_timers.size() != 16) {
                d.cr("watcher", "bath_timers.size() != 16", bath_timers.size());
                Main.gui.taReply.setText("ERROR reading values");
                return;
            }
            cmdValues16.add(bath_timers);
            d.cr("watcher", "cmd_added", bath_timers);
        }, 60, TS_FileWatchUtils.Triggers.MODIFY);
    }
}
