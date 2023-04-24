package com.tugalsan.dsk.serialyh;

import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.desktop.server.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.pack.client.*;
import java.util.*;
import java.util.stream.*;
import javax.swing.*;

public class GUI extends javax.swing.JFrame {

    public List<JLabel> lstTf = TGS_ListUtils.of();

    public GUI() {
        setTitle("MESA METAL YÜZEY HAZIRLAMA PROGRAMI");
        TS_DesktopFrameUtils.setThemeDarkLAF(this);
        initComponents();
        var offsetX = 6;
        var offsetY = 120;
        var width = 70;
        var height = 24;
        IntStream.range(0, 16).forEachOrdered(i -> {
            var lblName = new JLabel("Banyo " + i + ":");
            var lblValue = new JLabel();
            var btn = new JButton("Değiştir");
            btn.addActionListener(e -> {
                var oldVal = TGS_CastUtils.toInteger(GUI.this.lstTf.get(i).getText());
                var newVal = TS_DesktopDialogInputNumberUtils.show("Sayı Girin", oldVal);
                if (newVal.isEmpty()) {
                    TS_DesktopDialogInfoUtils.show("HATA", "Bir sayı olmalıydı.");
                    return;
                }
                if (newVal.get() < 0) {
                    TS_DesktopDialogInfoUtils.show("HATA", "0 dan büyük olmalıydı.");
                    return;
                }
                Main.mem_int_set_idx_val.add(new TGS_Pack2(32 * 3 + i, newVal.get()));
            });
            getContentPane().add(lblName, new org.netbeans.lib.awtextra.AbsoluteConstraints(offsetX + (width + 1) * 0, offsetY + height * i, width, height));
            getContentPane().add(lblValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(offsetX + (width + 1) * 1, offsetY + height * i, width, height));
            getContentPane().add(btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(offsetX + (width + 1) * 2, offsetY + height * i, width * 2, height));
            lstTf.add(lblValue);
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

        getContentPane().add(scrollReply, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 6, 834, 108));

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
