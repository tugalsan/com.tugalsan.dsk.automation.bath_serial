package com.tugalsan.dsk.automation.bath_serial;

import com.tugalsan.api.list.client.TGS_ListUtils;
import com.tugalsan.api.serialcom.kincony.server.KC868_A32_R1_2.TS_SerialComKinConyKC868_A32_R1_2;
import com.tugalsan.api.time.client.TGS_Time;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Mem_Int {

    public static enum STATUS {
        OK, ERROR_SIZE, ERROR_EMPTY
    }

    private Mem_Int(TGS_UnionExcuse<List<Integer>> mem_int, TGS_UnionExcuse<Integer> mode) {
        this.mem_int = mem_int;
        this.mode = mode;
        if (!mem_int.isPresent()) {
            status = STATUS.ERROR_EMPTY;
            return;
        }
        if (mem_int.value().size() != 32 * 3 + 16) {
            status = STATUS.ERROR_EMPTY;
            return;
        }
        IntStream.range(32 * 0, 32 * 1).forEachOrdered(i -> lstDI.add(mem_int.value().get(i).equals(1)));
        IntStream.range(32 * 1, 32 * 2).forEachOrdered(i -> lstDO.add(mem_int.value().get(i).equals(1)));
        IntStream.range(32 * 2, 32 * 3).forEachOrdered(i -> lstOS.add(mem_int.value().get(i).equals(1)));
        IntStream.range(32 * 3, 32 * 3 + 16).forEachOrdered(i -> lstTI.add(mem_int.value().get(i)));

        status = STATUS.OK;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        if (null == status) {
            sb
                    .append(time.toString_timeOnly())
                    .append(" UNKNOWN_STATUS");
        } else switch (status) {
            case ERROR_EMPTY -> sb
                        .append(time.toString_timeOnly())
                        .append(" ERROR_EMPTY");
            case ERROR_SIZE -> sb
                        .append(time.toString_timeOnly())
                        .append(" ERROR_SIZE [").append(mem_int.value().size()).append("] -> ")
                        .append(mem_int);
            case OK -> sb
                        .append(time.toString_timeOnly())
                        .append(" lstDI: ")
                        .append(lstDI.stream().map(b -> b ? 1 : 0).map(i -> String.valueOf(i)).collect(Collectors.joining(", ")))
                        .append("\n")
                        .append(time.toString_timeOnly())
                        .append(" lstDO: ")
                        .append(lstDO.stream().map(b -> b ? 1 : 0).map(i -> String.valueOf(i)).collect(Collectors.joining(", ")))
                        .append("\n")
                        .append(time.toString_timeOnly())
                        .append(" lstOS: ")
                        .append(lstOS.stream().map(b -> b ? 1 : 0).map(i -> String.valueOf(i)).collect(Collectors.joining(", ")))
                        .append("\n")
                        .append(time.toString_timeOnly())
                        .append(" lstTI: ")
                        .append(lstTI.stream().map(i -> String.valueOf(i)).collect(Collectors.joining(", ")));
            default -> sb
                        .append(time.toString_timeOnly())
                        .append(" UNKNOWN_STATUS");
        }
        return sb.toString();
    }

    public static Mem_Int of() {
        var mem_int = TS_SerialComKinConyKC868_A32_R1_2.memInt_getAll(Main.killTrigger, Main.COMX);
        var mode = TS_SerialComKinConyKC868_A32_R1_2.mode_getIdx(Main.killTrigger, Main.COMX);
        return new Mem_Int(mem_int, mode);
    }
    public TGS_UnionExcuse<List<Integer>> mem_int;
    public TGS_UnionExcuse<Integer> mode;
    public STATUS status;
    public List<Boolean> lstDI = TGS_ListUtils.of();
    public List<Boolean> lstDO = TGS_ListUtils.of();
    public List<Boolean> lstOS = TGS_ListUtils.of();
    public List<Integer> lstTI = TGS_ListUtils.of();
    public TGS_Time time = TGS_Time.of();
}
