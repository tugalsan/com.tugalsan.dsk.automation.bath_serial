package com.tugalsan.dsk.automation.bath_serial;

import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.desktop.server.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.stream.client.TGS_StreamUtils;
import java.util.*;
import java.util.stream.*;
import javax.swing.*;
import org.netbeans.lib.awtextra.AbsoluteConstraints;

public class GUI extends javax.swing.JFrame {

    public List<JLabel> lstValues = TGS_ListUtils.of();
    private List<JButton> lstBtns = TGS_ListUtils.of();

    private Optional<List<Integer>> getLblValues() {
        var result = TGS_StreamUtils.toLst(
                lstValues.stream()
                        .map(lbl -> TGS_CastUtils.toInteger(lbl.getText()).orElse(null))
                        .filter(val -> val != null)
        );
        return result.size() == 16 ? Optional.of(result) : Optional.empty();
    }

    public static List<String> lblNames_KüçükFosfat = TGS_ListUtils.of(
            "-", //0
            "YÜ - Yükleme", //1
            "YD - YağAlma", //2
            "DU - Durulama1", //3
            "DU - Durulama2", //4
            "DU - Durulama3", //5
            "PA - PasAlma", //6
            "DU - Durulama(P.A)", //7
            "FA - Aktivasyon", //8
            "FF - T.Ç.Fosfat", //9
            "DU - Durulama(T.Ç.F)", //10
            "FV - Pasivasyon", //11
            "KU - Kurutma", //12
            "BO - Boşaltma", //13
            "-", //14
            "-" //15
    );
    public static List<String> lblNames_KüçükKromat = TGS_ListUtils.of(
            "-", //0
            "YÜ - Yükleme", //1
            "Sıcak boya Sökme", //2
            "YG - YağAlma", //3
            "DU - Durulama1", //4
            "DU - Durulama2", //5
            "AA - Aşındırma (Al)", //6
            "DU - Durulama (Aşın.)", //7
            "DU - Durulama (Y.Kr.)", //8
            "KY - Kromat (Yeşil)", //9
            "KU - Kurutma", //10
            "BO - Boşaltma", //11
            "-", //12
            "-", //13
            "-", //14
            "-" //15
    );

    public GUI() {
        setTitle("MESA METAL YÜZEY HAZIRLAMA PROGRAMI");
        initComponents();
        var offsetX = 6;
        var offsetY = 120;
        var widthName = 150;
        var widthValue = 75;
        var height = 24;
        var gap = 2;
        IntStream.range(0, 16).forEachOrdered(i -> {
            lstValues.add(new JLabel());
            lstBtns.add(new JButton(lblNames_KüçükFosfat.get(i) + ":"));
            getContentPane().add(lstBtns.get(i), new AbsoluteConstraints(offsetX, offsetY + height * i, widthName, height));
            getContentPane().add(lstValues.get(i), new AbsoluteConstraints(offsetX + widthName + gap, offsetY + gap + height * i, widthValue, height));
        });
        IntStream.range(0, 16).forEachOrdered(i -> {
            lstBtns.get(i).addActionListener(e -> {
                var oldVal = TGS_CastUtils.toInteger(this.lstValues.get(i).getText()).orElse(null);
                var newVal = TS_DesktopDialogInputNumberUtils.show("Sayı Girin", oldVal);
                if (!newVal.isPresent()) {
                    TS_DesktopDialogInfoUtils.show("HATA", "Bir sayı olmalıydı.");
                    return;
                }
                if (newVal.value() < 0) {
                    TS_DesktopDialogInfoUtils.show("HATA", "0 dan büyük olmalıydı.");
                    return;
                }
                var lblValues = getLblValues();
                if (lblValues.isPresent()) {
                    lblValues.get().set(i, newVal.value());
                    Main.cmdValues16.add(lblValues.get());
                }
            });
        });
        setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollReply = new javax.swing.JScrollPane();
        taReply = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        taReply.setColumns(20);
        taReply.setRows(5);
        scrollReply.setViewportView(taReply);

        getContentPane().add(scrollReply, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 6, 790, 108));

        setSize(new java.awt.Dimension(814, 557));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new GUI().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollReply;
    public javax.swing.JTextArea taReply;
    // End of variables declaration//GEN-END:variables
}
