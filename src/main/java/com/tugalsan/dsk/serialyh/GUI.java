package com.tugalsan.dsk.serialyh;

import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.desktop.server.*;
import com.tugalsan.api.list.client.*;
import java.util.*;
import java.util.stream.*;
import javax.swing.*;
import org.netbeans.lib.awtextra.AbsoluteConstraints;

public class GUI extends javax.swing.JFrame {

    public List<JLabel> lstTf = TGS_ListUtils.of();

    public static List<String> lblNames_KüçükFosfat = List.of(
            "-", //0
            "Yükleme", //1
            "YağAlma", //2
            "Durulama1", //3
            "Durulama2", //4
            "Durulama3", //5
            "PasAlma", //6
            "Durulama(P.A)", //7
            "Altivasyon", //8
            "T.Ç.Fosfat", //9
            "Durulama(T.Ç.F)", //10
            "Pasivasyon", //11
            "Kurutma", //12
            "Boşaltma", //13
            "-", //14
            "-" //15
    );
    public static List<String> lblNames_KüçükKromat = List.of(
            "-", //0
            "Yükleme", //1
            "Sıcak boya Sökme", //2
            "YağAlma", //3
            "Durulama1", //4
            "Durulama2", //5
            "Aşındırma (Al)", //6
            "Durulama (Aşın.)", //7
            "Durulama (Y.Kr.)", //8
            "Kromat (Yeşil)", //9
            "Kurutma", //10
            "Boşaltma", //11
            "-", //12
            "-", //13
            "-", //14
            "-" //15
    );

    public GUI() {
        setTitle("MESA METAL YÜZEY HAZIRLAMA PROGRAMI");
        TS_DesktopFrameUtils.setThemeDarkLAF(this);
        initComponents();
        var offsetX = 6;
        var offsetY = 120;
        var width = 100;
        var height = 24;
        IntStream.range(0, 16).forEachOrdered(i -> {
            var lblName = new JLabel(lblNames_KüçükFosfat.get(i) + ":");
            var lblValue = new JLabel();
            var btn = new JButton("Değiştir");
            btn.addActionListener(e -> {
                var oldVal = TGS_CastUtils.toInteger(lstTf.get(i).getText());
                var newVal = TS_DesktopDialogInputNumberUtils.show("Sayı Girin", oldVal);
                if (newVal.isEmpty()) {
                    TS_DesktopDialogInfoUtils.show("HATA", "Bir sayı olmalıydı.");
                    return;
                }
                if (newVal.get() < 0) {
                    TS_DesktopDialogInfoUtils.show("HATA", "0 dan büyük olmalıydı.");
                    return;
                }
                Main.mem_int_set_idx_val_or_values16.add(List.of(i, newVal.get()));
            });
            getContentPane().add(btn, new AbsoluteConstraints(offsetX + (width + 1) * 0, offsetY + height * i, width, height));
            getContentPane().add(lblName, new AbsoluteConstraints(offsetX + (width + 1) * 1, offsetY + height * i, width, height));
            getContentPane().add(lblValue, new AbsoluteConstraints(offsetX + (width + 1) * 2, offsetY + height * i, width, height));
            lstTf.add(lblValue);
        });

        setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollReply = new javax.swing.JScrollPane();
        taReply = new javax.swing.JTextArea();
        btnTestAll5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        taReply.setColumns(20);
        taReply.setRows(5);
        scrollReply.setViewportView(taReply);

        getContentPane().add(scrollReply, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 6, 790, 108));

        btnTestAll5.setText("test all 5");
        btnTestAll5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTestAll5ActionPerformed(evt);
            }
        });
        getContentPane().add(btnTestAll5, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 130, 470, -1));

        setSize(new java.awt.Dimension(814, 557));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnTestAll5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTestAll5ActionPerformed
        Main.mem_int_set_idx_val_or_values16.add(TGS_ListUtils.toList(6, 16));
    }//GEN-LAST:event_btnTestAll5ActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new GUI().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnTestAll5;
    private javax.swing.JScrollPane scrollReply;
    public javax.swing.JTextArea taReply;
    // End of variables declaration//GEN-END:variables
}
