package com.tugalsan.dsk.serialyh;

import com.tugalsan.api.desktop.server.TS_DesktopFrameUtils;
import com.tugalsan.api.serialcom.kincony.server.KC868_A32_R1_2.*;

//WHEN RUNNING IN NETBEANS, ALL DEPENDENCIES SHOULD HAVE TARGET FOLDER!
//cd C:\me\codes\com.tugalsan\dsk\com.tugalsan.dsk.serialyh
//java --enable-preview --add-modules jdk.incubator.concurrent -jar target/com.tugalsan.dsk.serialyh-1.0-SNAPSHOT-jar-with-dependencies.jar    
public class Main {

    public static void main(String... s) {
        TS_DesktopFrameUtils.create(() -> new GUI());
//        TS_SerialComKinConyKC868_A32_R1_2_Test.main(s);
    }
}
