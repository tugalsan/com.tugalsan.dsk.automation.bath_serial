package com.tugalsan.dsk.serialyh;

import com.tugalsan.api.list.client.TGS_ListUtils;
import com.tugalsan.api.serialcom.kincony.server.KC868_A32_R1_2.TS_SerialComKinConyKC868_A32_R1_2;
import com.tugalsan.api.time.client.TGS_Time;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Mem_Int {

    public static enum STATUS {
        OK, ERROR_SIZE, ERROR_EMPTY
    }

    private Mem_Int(Optional<List<Integer>> mem_int) {
        this.mem_int = mem_int;
        if (mem_int.isEmpty()) {
            status = STATUS.ERROR_EMPTY;
            return;
        }
        if (mem_int.get().size() != 32 * 3 + 16) {
            status = STATUS.ERROR_EMPTY;
            return;
        }
        IntStream.range(32 * 0, 32 * 1).forEachOrdered(i -> lstDI.add(mem_int.get().get(i).equals(1)));
        IntStream.range(32 * 1, 32 * 2).forEachOrdered(i -> lstDO.add(mem_int.get().get(i).equals(1)));
        IntStream.range(32 * 2, 32 * 3).forEachOrdered(i -> lstOS.add(mem_int.get().get(i).equals(1)));
        IntStream.range(32 * 3, 32 * 3 + 16).forEachOrdered(i -> lstTI.add(mem_int.get().get(i)));

        status = STATUS.OK;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        switch (status) {
            case ERROR_EMPTY ->
                sb
                        .append(time.toString_timeOnly())
                        .append(" ERROR_EMPTY");
            case ERROR_SIZE ->
                sb
                        .append(time.toString_timeOnly())
                        .append(" ERROR_SIZE [").append(mem_int.get().size()).append("] -> ")
                        .append(mem_int);
            case OK ->
                sb
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
            default ->
                sb
                        .append(time.toString_timeOnly())
                        .append(" UNKNOWN_STATUS");
        }
        return sb.toString();
    }

    public static Mem_Int of() {
        var mem_int = TS_SerialComKinConyKC868_A32_R1_2.memInt_getAll(Main.COMX);
        return new Mem_Int(mem_int);
    }
    public Optional<List<Integer>> mem_int;
    public STATUS status;
    public List<Boolean> lstDI = TGS_ListUtils.of();
    public List<Boolean> lstDO = TGS_ListUtils.of();
    public List<Boolean> lstOS = TGS_ListUtils.of();
    public List<Integer> lstTI = TGS_ListUtils.of();
    public TGS_Time time = TGS_Time.of();
}
