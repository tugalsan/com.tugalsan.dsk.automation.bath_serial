package com.tugalsan.dsk.serialyh;

import com.tugalsan.api.cast.client.TGS_CastUtils;
import com.tugalsan.api.coronator.client.TGS_Coronator;
import com.tugalsan.api.desktop.server.TS_DesktopDialogInfoUtils;
import com.tugalsan.api.desktop.server.TS_DesktopMainUtils;
import com.tugalsan.api.file.properties.server.TS_FilePropertiesUtils;
import com.tugalsan.api.file.server.TS_FileUtils;
import com.tugalsan.api.file.server.TS_FileWatchUtils;
import com.tugalsan.api.file.txt.server.TS_FileTxtUtils;
import com.tugalsan.api.list.client.TGS_ListUtils;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.serialcom.kincony.server.KC868_A32_R1_2.TS_SerialComKinConyKC868_A32_R1_2;
import com.tugalsan.api.stream.client.TGS_StreamUtils;
import com.tugalsan.api.thread.server.async.TS_ThreadAsync;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncLst;
import com.tugalsan.api.thread.server.TS_ThreadWait;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.stream.IntStream;
import javax.swing.JLabel;

//WHEN RUNNING IN NETBEANS, ALL DEPENDENCIES SHOULD HAVE TARGET FOLDER!
//cd C:\me\codes\com.tugalsan\dsk\com.tugalsan.dsk.serialyh
//java --enable-preview --add-modules jdk.incubator.concurrent -jar target/com.tugalsan.dsk.serialyh-1.0-SNAPSHOT-jar-with-dependencies.jar USB-SERIAL CH340 (COM3)    
public class Main {

    final private static TS_Log d = TS_Log.of(true, Main.class);

    public static volatile Mem_Int mem_int_last;

    final public static TS_ThreadSyncLst<List<Integer>> cmdValues16 = new TS_ThreadSyncLst();
    public static volatile int mode = 0;
    public static volatile int modeRequested = 1;
    public static volatile GUI gui;

    final public static Path fileCmd = Paths.get("C:", "com.tugalsan.dsk.serialyh", "cmd.txt");
    final public static Path fileRes = Paths.get("C:", "com.tugalsan.dsk.serialyh", "res.txt");
    final public static String propsParamPrefix = "bath_timer_";
    public static String COMX;

    public static TS_ThreadSyncTrigger killTrigger = TS_ThreadSyncTrigger.of();

    public static void main(String... s) {
        List<String> portNames = TS_SerialComKinConyKC868_A32_R1_2.portNames();
        if (s.length == 0) {
            StringBuilder sb = new StringBuilder()
                    .append("USAGE: java --enable-preview --add-modules jdk.incubator.concurrent \\")
                    .append("\n       -jar target/com.tugalsan.dsk.serialyh-1.0-SNAPSHOT-jar-with-dependencies.jar COMX");
            if (portNames.isEmpty()) {
                sb.append("\nERROR: NO PORT DETECTED!");
            } else {
                sb.append("\nPARAM OPTIONS:");
                portNames.forEach(p -> {
                    sb.append("\n ").append(p);
                });
            }
            TS_DesktopDialogInfoUtils.show("HOW TO USE", sb.toString());
            TS_ThreadWait.of("wait.main", Main.killTrigger, Duration.ofSeconds(10));
            System.exit(0);
            return;
        }
        COMX = s[0];
        System.out.println("comX: [" + COMX + "]");
        TS_DesktopMainUtils.setThemeAndinvokeLaterAndFixTheme(() -> gui = new GUI());
        TS_ThreadAsync.now(Main.killTrigger, kt -> {
            while (true) {
                if (cmdValues16.isEmpty()) {
                    mem_int_last = Mem_Int.of();
                    if (gui != null) {
                        gui.taReply.setText(mem_int_last.toString());
                        gui.taReply.append("\nmode:" + TGS_Coronator.ofStr()
                                .anoint(val -> "Okunuyor")
                                .anointIf(val -> mem_int_last.mode.isPresent() && mem_int_last.mode.get() == 0, val -> "DÜĞME TEST")
                                .anointIf(val -> mem_int_last.mode.isPresent() && mem_int_last.mode.get() == 1, val -> "YH PROGRAMI")
                                .coronate()
                        );
                        if (mem_int_last.status == Mem_Int.STATUS.OK) {
                            IntStream.range(0, 16).forEachOrdered(i -> {//BUTTON UPDATE
                                Integer memVal = mem_int_last.mem_int.get().get(32 * 3 + i);
                                JLabel lstStr = gui.lstValues.get(i);
                                if (Objects.equals(memVal, lstStr)) {
                                    return;
                                }
                                gui.lstValues.get(i).setText(memVal.toString());
                            });
                        }
                    }
                    if (!TS_FileUtils.isExistFile(fileCmd)) {
                        StringJoiner sj = new StringJoiner("\n");
                        IntStream.range(0, 16).forEachOrdered(i -> {
                            sj.add(propsParamPrefix + i + "=" + mem_int_last.lstTI.get(i));
                        });
                        TS_FileTxtUtils.toFile(sj.toString(), fileCmd, false);
                    }
                    if (!TS_FileUtils.isExistFile(fileRes)) {
                        TS_FileTxtUtils.toFile(TS_FileUtils.getTimeLastModified(fileCmd) + " CMD_INIT", fileRes, false);
                    }
                    if (mem_int_last.mode.orElse(0) == 0) {
                        boolean result = TS_SerialComKinConyKC868_A32_R1_2.mode_setIdx(killTrigger, COMX, modeRequested);
                        d.ce("mode_setIdx", modeRequested, result);
                    }
                    continue;
                }
                List<Integer> lst = cmdValues16.popFirst(val -> true);
                d.ce("set_lst", lst);
                List<Integer> lstIdx = TGS_StreamUtils.toLst(IntStream.range(0, lst.size()).filter(i -> lst.get(i) != 0));
                d.ce("set_osc", lstIdx);
                if (TS_SerialComKinConyKC868_A32_R1_2.memInt_setAll(Main.killTrigger, COMX, lst) && TS_SerialComKinConyKC868_A32_R1_2.digitalOut_oscilateAll(Main.killTrigger, COMX, lstIdx)) {
                    gui.taReply.setText("Değişiklik başarılı.");
                    TS_FileTxtUtils.toFile(TS_FileUtils.getTimeLastModified(fileRes) + " CMD_DONE", fileRes, false);
                } else {
                    gui.taReply.setText("Değişiklik BAŞARISIZ!");
                    TS_FileTxtUtils.toFile(TS_FileUtils.getTimeLastModified(fileRes) + " CMD_FAILED", fileRes, false);
                }
            }
        });
        TS_FileWatchUtils.file(Main.killTrigger, fileCmd, () -> {
            d.cr("watcher", "detected");
            TS_ThreadWait.seconds("wait.watch", Main.killTrigger, 1);
            Optional<Properties> props = TS_FilePropertiesUtils.createPropertyReader(fileCmd);
            if (!props.isPresent()) {
                d.cr("watcher", "props.isEmpty()");
                return;
            }
            List<Integer> bath_timers = TGS_ListUtils.of();
            IntStream.range(0, 16).forEachOrdered(i -> {
                Integer val = TGS_CastUtils.toInteger(props.get().getProperty(propsParamPrefix + i));
                if (val == null) {
                    d.cr("watcher", "param_null", propsParamPrefix + i);
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
        }, TS_FileWatchUtils.Triggers.MODIFY);
    }
}
