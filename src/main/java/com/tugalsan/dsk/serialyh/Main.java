package com.tugalsan.dsk.serialyh;

import com.tugalsan.api.desktop.server.TS_DesktopFrameUtils;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.pack.client.TGS_Pack2;
import com.tugalsan.api.serialcom.kincony.server.KC868_A32_R1_2.TS_SerialComKinConyKC868_A32_R1_2;
import com.tugalsan.api.thread.server.TS_ThreadRun;
import com.tugalsan.api.thread.server.TS_ThreadSafeLst;
import java.util.Objects;
import java.util.stream.IntStream;

//WHEN RUNNING IN NETBEANS, ALL DEPENDENCIES SHOULD HAVE TARGET FOLDER!
//cd C:\me\codes\com.tugalsan\dsk\com.tugalsan.dsk.serialyh
//java --enable-preview --add-modules jdk.incubator.concurrent -jar target/com.tugalsan.dsk.serialyh-1.0-SNAPSHOT-jar-with-dependencies.jar    
public class Main {

    private static TS_Log d = TS_Log.of(true, Main.class);

    public static volatile Mem_Int mem_int_last;

    public static TS_ThreadSafeLst<TGS_Pack2<Integer, Integer>> mem_int_set_idx_val = new TS_ThreadSafeLst();
    public static volatile GUI gui;

    public static void main(String... s) {
        TS_DesktopFrameUtils.create(() -> gui = new GUI());
        TS_ThreadRun.now(() -> {
            while (true) {
                if (mem_int_set_idx_val.isEmpty()) {
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
                    continue;
                }
                var cmd = mem_int_set_idx_val.findFirst(val -> true);
                mem_int_set_idx_val.removeFirst(cmd);
                var result = TS_SerialComKinConyKC868_A32_R1_2.memInt_setIdx(cmd.value0, cmd.value1);
                if (result) {
                    gui.taReply.setText("Değişiklik başarılı.");
                } else {
                    gui.taReply.setText("Değişiklik BAŞARISIZ!");
                }
            }
        });
    }
}
