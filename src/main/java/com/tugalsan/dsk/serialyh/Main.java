package com.tugalsan.dsk.serialyh;

import com.tugalsan.api.cast.client.TGS_CastUtils;
import com.tugalsan.api.coronator.client.TGS_Coronator;
import com.tugalsan.api.desktop.server.TS_DesktopFrameUtils;
import com.tugalsan.api.file.properties.server.TS_FilePropertiesUtils;
import com.tugalsan.api.file.server.TS_FileUtils;
import com.tugalsan.api.file.server.TS_FileWatchUtils;
import com.tugalsan.api.file.txt.server.TS_FileTxtUtils;
import com.tugalsan.api.list.client.TGS_ListUtils;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.serialcom.kincony.server.KC868_A32_R1_2.TS_SerialComKinConyKC868_A32_R1_2;
import com.tugalsan.api.thread.server.TS_ThreadRun;
import com.tugalsan.api.thread.server.TS_ThreadSafeLst;
import com.tugalsan.api.thread.server.TS_ThreadWait;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//WHEN RUNNING IN NETBEANS, ALL DEPENDENCIES SHOULD HAVE TARGET FOLDER!
//cd C:\me\codes\com.tugalsan\dsk\com.tugalsan.dsk.serialyh
//java --enable-preview --add-modules jdk.incubator.concurrent -jar target/com.tugalsan.dsk.serialyh-1.0-SNAPSHOT-jar-with-dependencies.jar    
public class Main {
    
    final private static TS_Log d = TS_Log.of(true, Main.class);
    
    public static volatile Mem_Int mem_int_last;
    
    final public static TS_ThreadSafeLst<List<Integer>> mem_int_set_idx_val_or_values16 = new TS_ThreadSafeLst();
    public static volatile GUI gui;
    
    final public static Path fileCmd = Path.of("C", "com.tugalsan.dsk.serialyh", "cmd.txt");
    final public static Path fileRes = Path.of("C", "com.tugalsan.dsk.serialyh", "res.txt");
    
    public static void main(String... s) {
        TS_DesktopFrameUtils.create(() -> gui = new GUI());
        TS_ThreadRun.now(() -> {
            while (true) {
                if (mem_int_set_idx_val_or_values16.isEmpty()) {
                    mem_int_last = Mem_Int.of();
                    if (gui != null) {
                        gui.taReply.setText(mem_int_last.toString());
                        if (mem_int_last.status == Mem_Int.STATUS.OK) {
                            IntStream.range(0, 16).forEachOrdered(i -> {
                                var memVal = mem_int_last.mem_int.get().get(32 * 3 + i);
                                var lstStr = gui.lstTf.get(i);
                                if (Objects.equals(memVal, lstStr)) {
                                    return;
                                }
                                gui.lstTf.get(i).setText(memVal.toString());
                            });
                        }
                    }
                    if (!TS_FileUtils.isExistFile(fileCmd)) {
                        TS_FileTxtUtils.toFile(mem_int_last.lstTI.stream().map(i -> String.valueOf(i)).collect(Collectors.joining("\n")), fileCmd, false);
                        TS_FileTxtUtils.toFile(TS_FileUtils.getTimeLastModified(fileCmd) + "CMD_INIT", fileRes, false);
                    }
                    continue;
                }
                var lst = mem_int_set_idx_val_or_values16.findFirst(val -> true);
                mem_int_set_idx_val_or_values16.removeFirst(lst);
                var result = TGS_Coronator.ofBool()
                        .anoint(val -> null)
                        .anointAndCoronateIf(val -> lst.size() == 16, val -> TS_SerialComKinConyKC868_A32_R1_2.memInt_setAll(lst))
                        .anointAndCoronateIf(val -> lst.size() == 2, val -> TS_SerialComKinConyKC868_A32_R1_2.memInt_setIdx(lst.get(0), lst.get(1)))
                        .coronate();
                if (result == null) {
                    gui.taReply.setText("CMD_ERROR");
                    if (TS_FileUtils.isExistFile(fileCmd)) {
                        TS_FileTxtUtils.toFile(TS_FileUtils.getTimeLastModified(fileCmd) + " CMD_ERROR", fileRes, false);
                    }
                } else if (result) {
                    gui.taReply.setText("Değişiklik başarılı.");
                    if (TS_FileUtils.isExistFile(fileCmd)) {
                        TS_FileTxtUtils.toFile(TS_FileUtils.getTimeLastModified(fileCmd) + " CMD_DONE", fileRes, false);
                    }
                } else {
                    gui.taReply.setText("Değişiklik BAŞARISIZ!");
                    if (TS_FileUtils.isExistFile(fileCmd)) {
                        TS_FileTxtUtils.toFile(TS_FileUtils.getTimeLastModified(fileCmd) + " CMD_FAILED", fileRes, false);
                    }
                }
            }
        });
        TS_FileWatchUtils.file(fileRes, () -> {
            TS_ThreadWait.seconds(null, 1);
            var props = TS_FilePropertiesUtils.createPropertyReader(fileRes);
            if (props.isEmpty()) {
                return;
            }
            List<Integer> bath_timers = TGS_ListUtils.of();
            IntStream.range(0, 16).forEachOrdered(i -> {
                var val = TGS_CastUtils.toInteger(props.get().getProperty("bath_timer_0"));
                if (val == null) {
                    return;
                }
                bath_timers.add(val);
            });
            if (bath_timers.size() != 16) {
                return;
            }
            mem_int_set_idx_val_or_values16.add(bath_timers);
        }, TS_FileWatchUtils.Types.CREATE, TS_FileWatchUtils.Types.MODIFY);
    }
}
