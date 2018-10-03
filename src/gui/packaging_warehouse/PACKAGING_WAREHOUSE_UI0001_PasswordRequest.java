/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.packaging_warehouse;

import __main__.GlobalMethods;
import __main__.GlobalVars;
import __main__.StartFrame;
import entity.ManufactureUsers;
import gui.warehouse_dispatch.state.WarehouseHelper;
import helper.HQLHelper;
import helper.Helper;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.hibernate.Query;
import ui.UILog;
import ui.info.InfoMsg;

/**
 *
 * @author user
 */
public class PACKAGING_WAREHOUSE_UI0001_PasswordRequest extends javax.swing.JDialog {

    private ManufactureUsers user;

    //Object to preserve the main frame as parent
    private StartFrame main_gui;

    /**
     * Creates new form UI0010_PalletDetails
     *
     * @param parent
     * @param modal
     */
    public PACKAGING_WAREHOUSE_UI0001_PasswordRequest(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        Helper.centerJDialog(this);
        this.setResizable(false);
        this.user = null;
        this.main_gui = (StartFrame) parent;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        ok_btn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        error_lbl = new javax.swing.JLabel();
        admin_password_txtbox = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();
        admin_login_txtbox = new javax.swing.JTextField();
        cancel_btn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Password");
        setType(java.awt.Window.Type.UTILITY);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        ok_btn.setText("OK");
        ok_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ok_btnActionPerformed(evt);
            }
        });

        jLabel1.setText("Password :");

        error_lbl.setForeground(new java.awt.Color(255, 0, 0));

        admin_password_txtbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                admin_password_txtboxKeyPressed(evt);
            }
        });

        jLabel2.setText("Login :");

        cancel_btn.setText("Cancel");
        cancel_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(ok_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cancel_btn))
                    .addComponent(admin_password_txtbox)
                    .addComponent(admin_login_txtbox))
                .addGap(105, 105, 105)
                .addComponent(error_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(error_lbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(admin_login_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(admin_password_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ok_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancel_btn))
                .addGap(22, 22, 22))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void clearPasswordBox() {
        //Vider le champs de text scan
        admin_password_txtbox.setText("");
    }

    private boolean checkLoginAndPass() {
        Helper.startSession();
        Helper.sess.beginTransaction();
        System.out.println("Login to check " + admin_login_txtbox.getText());
        System.out.println("Password " + String.valueOf(admin_password_txtbox.getPassword()));
        Query query = Helper.sess.createQuery(HQLHelper.CHECK_LOGIN_PASS);
        query.setParameter("login", admin_login_txtbox.getText());
        query.setParameter("password", String.valueOf(admin_password_txtbox.getPassword()));

        Helper.sess.getTransaction().commit();
        List result = query.list();

        System.out.println("Resultat du check " + result.size());
        if (result.isEmpty()) {
            return false;
        }

        this.user = (ManufactureUsers) result.get(0);
        return true;
    }

    private void ok_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ok_btnActionPerformed
        if (checkLoginAndPass()) {
            connect();
        } else {
            JOptionPane.showMessageDialog(null, Helper.ERR0001_LOGIN_FAILED, "Login Error", JOptionPane.ERROR_MESSAGE);
            admin_password_txtbox.setText("");
        }
    }//GEN-LAST:event_ok_btnActionPerformed

    private void connect() {
        Helper.startSession();

        this.user.setLoginTime(new Date());
        WarehouseHelper.warehouse_reserv_context.setUser(this.user);
        WarehouseHelper.warehouse_reserv_context.getUser().update(WarehouseHelper.warehouse_reserv_context.getUser());
        //Go back to step S020
        try {
            GlobalVars.APP_HOSTNAME = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            Logger.getLogger(PACKAGING_WAREHOUSE_UI0001_PasswordRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
//        String str = String.format(Helper.INFO0001_LOGIN_SUCCESS,
//                this.user.getFirstName() + " " + this.user.getLastName()
//                + " / " + this.user.getLogin(), GlobalVars.APP_HOSTNAME,
//                GlobalMethods.getStrTimeStamp() + " Packaging warehouse interface : ");
//        UILog.info(str);
        
        String str = String.format(InfoMsg.APP_INFO0003[1],
                this.user.getFirstName() + " " + this.user.getLastName()
                + " / " + this.user.getLogin(), GlobalVars.APP_HOSTNAME,
                GlobalMethods.getStrTimeStamp() + " Packaging warehouse interface : ");
        
        UILog.info(str); 

        //Save authentication line in HisLogin table
//        HisLogin his_login = new HisLogin(
//                this.user.getId(), this.user.getId(),
//                String.format(Helper.INFO0001_LOGIN_SUCCESS,
//                        this.user.getFirstName() + " " + this.user.getLastName() + " / " + this.user.getLogin(),
//                        GlobalVars.APP_HOSTNAME, GlobalMethods.getStrTimeStamp()));
//        his_login.setCreateId(this.user.getId());
//        his_login.setWriteId(this.user.getId());
//        his_login.setMessage(str);
//        his_login.create(his_login);

        PackagingHelper.user = this.user;
        //Create and display the packaing main form
        PackagingHelper.Packaging_Main_Gui = new PACKAGING_WAREHOUSE_UI0001_MAIN_FORM(null, this.main_gui);

        this.dispose();

    }

    private void admin_password_txtboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_admin_password_txtboxKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (checkLoginAndPass()) {
                connect();
            } else {
                JOptionPane.showMessageDialog(null, Helper.ERR0001_LOGIN_FAILED, "Login Error", JOptionPane.ERROR_MESSAGE);
                admin_password_txtbox.setText("");
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            WarehouseHelper.Dispatch_Gui.logout();
            this.dispose();
        }
    }//GEN-LAST:event_admin_password_txtboxKeyPressed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        //if (WarehouseHelper.warehouse_reserv_context.getUser() == null) {
        // WarehouseHelper.warehouse_reserv_context.setState(new S010_DispatchUserCodeScan());
        //}


    }//GEN-LAST:event_formWindowClosing

    private void cancel_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_btnActionPerformed
        this.dispose();
    }//GEN-LAST:event_cancel_btnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField admin_login_txtbox;
    private javax.swing.JPasswordField admin_password_txtbox;
    private javax.swing.JButton cancel_btn;
    private javax.swing.JLabel error_lbl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton ok_btn;
    // End of variables declaration//GEN-END:variables
}
