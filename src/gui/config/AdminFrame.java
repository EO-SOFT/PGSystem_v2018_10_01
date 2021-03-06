/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.config;

import __main__.GlobalMethods;
import __main__.GlobalVars;
import __main__.PropertiesLoader;
import entity.ManufactureUsers;
import gui.packaging.PackagingVars;
import gui.packaging.mode2.gui.PACKAGING_UI0001_Main_Mode2;
import gui.packaging.mode2.state.Mode2_S010_UserCodeScan;
import gui.packaging.mode3.gui.PACKAGING_UI0001_Main_Mode3;
import gui.packaging.mode3.state.Mode3_S010_UserCodeScan;
import gui.packaging.reports.PACKAGING_UI0010_PalletDetails;
import gui.packaging.reports.PACKAGING_UI0011_ProdStatistics;
import gui.packaging.reports.PACKAGING_UI0012_HarnessDetails;
import gui.packaging.reports.PACKAGING_UI0015_DroppedContainer;
import gui.packaging.reports.PACKAGING_UI0016_DroppedHarness;
import gui.packaging.reports.PACKAGING_UI0017_UCS_List;
import gui.packaging.reports.PACKAGING_UI0018_OpenContainer;
import gui.packaging.reports.PACKAGING_UI0019_EfficiencyCalculation;
import gui.packaging.reports.PACKAGING_UI0020_ProdStatisticsByShift;
import gui.packaging.reports.PACKAGING_UI0021_FG_AVAILABLE_STOCK;
import gui.packaging.reports.PACKAGING_UI0022_ClosedContainer;
import gui.packaging_warehouse.PACKAGING_WAREHOUSE_UI0001_MAIN_FORM;
import gui.packaging_warehouse.PACKAGING_WAREHOUSE_UI0001_PasswordRequest;
import gui.packaging_warehouse.PackagingHelper;
import gui.warehouse_dispatch.WAREHOUSE_DISPATCH_UI0002_DISPATCH_SCAN;
import gui.warehouse_dispatch.WAREHOUSE_DISPATCH_UI0003_PasswordRequest;
import gui.warehouse_dispatch.process_reservation.ReservationState;
import gui.warehouse_dispatch.process_reservation.S001_ReservPalletNumberScan;
import gui.warehouse_dispatch.state.S010_DispatchUserCodeScan;
import gui.warehouse_dispatch.state.WarehouseHelper;
import gui.warehouse_fg_reception.WAREHOUSE_FG_UI0001_SCAN;
import gui.warehouse_fg_reception.WAREHOUSE_FG_UI0002_PALLET_LIST;
import helper.Helper;
import java.awt.Frame;
import java.awt.Toolkit;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javassist.bytecode.stackmap.TypeData;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import ui.UILog;
import ui.info.InfoMsg;

/**
 *
 * @author Oussama
 */
public class AdminFrame extends javax.swing.JFrame {

    private ManufactureUsers user;

    public AdminFrame(Frame parent, boolean b, ManufactureUsers user) {
        initComponents();
        this.user = user;
        this.setTitle(GlobalVars.APP_NAME + " " + GlobalVars.APP_VERSION + " by " + GlobalVars.APP_AUTHOR);
        //versionLabel.setText(GlobalVars.APP_NAME + " " + GlobalVars.APP_VERSION);
        //authorLabel.setText(GlobalVars.APP_AUTHOR);

        this.setTitle(GlobalVars.APP_NAME + " " + GlobalVars.APP_VERSION);
        Helper.centerJFrame(this);
        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    /**
     * Creates new form NewMDIApplication
     */
//    public AdminFrame() {
//        initComponents();
//    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        desktopPane = new javax.swing.JDesktopPane();
        menuBar = new javax.swing.JMenuBar();
        MENU_00_MODULES = new javax.swing.JMenu();
        MENU_00_00_PACKAGING = new javax.swing.JMenu();
        MENU_00_00_00_REPORTS = new javax.swing.JMenu();
        menu011_prod_statistics = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        menu013_pallet_list = new javax.swing.JMenuItem();
        menu018_open_pallet_list = new javax.swing.JMenuItem();
        menu018_open_pallet_list1 = new javax.swing.JMenuItem();
        menu010_pallet_details = new javax.swing.JMenuItem();
        menu012_deleted_pallet = new javax.swing.JMenuItem();
        menu012_harness_details = new javax.swing.JMenuItem();
        menu012_deleted_harness = new javax.swing.JMenuItem();
        menu017_ucs = new javax.swing.JMenuItem();
        openMenuItem = new javax.swing.JMenuItem();
        MENU_00_01_FG_RECEPTION = new javax.swing.JMenuItem();
        MENU_00_02_PART_STOCK = new javax.swing.JMenuItem();
        MENU_00_03_PACKAGING_STOCK = new javax.swing.JMenuItem();
        MENU_00_02_DISPATCH = new javax.swing.JMenuItem();
        MENU_01_CONFIGURATION = new javax.swing.JMenu();
        MENU_01_00_CONFIG_UCS = new javax.swing.JMenuItem();
        MENU_01_01_CONFIG_BARCODE = new javax.swing.JMenuItem();
        MENU_01_02_CONFIG_PACK_MASTERDATA = new javax.swing.JMenuItem();
        MENU_01_03_AVANCE = new javax.swing.JMenu();
        MENU_01_03_00_CONFIG_USERS = new javax.swing.JMenuItem();
        MENU_01_03_01_CONFIG_COMPANY = new javax.swing.JMenuItem();
        MENU_01_03_02_CONFIG_WAREHOUSE = new javax.swing.JMenuItem();
        MENU_01_03_03_CONFIG_SEGMENT = new javax.swing.JMenuItem();
        MENU_01_03_04_CONFIG_WOKRPLACE = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        MENU_00_MODULES.setMnemonic('f');
        MENU_00_MODULES.setText("Modules");

        MENU_00_00_PACKAGING.setText("Packaging");

        MENU_00_00_00_REPORTS.setText("Rapports");

        menu011_prod_statistics.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        menu011_prod_statistics.setText("Statistiques production");
        menu011_prod_statistics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu011_prod_statisticsActionPerformed(evt);
            }
        });
        MENU_00_00_00_REPORTS.add(menu011_prod_statistics);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Calcul Efficience");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        MENU_00_00_00_REPORTS.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Production par équipe");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        MENU_00_00_00_REPORTS.add(jMenuItem2);

        menu013_pallet_list.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        menu013_pallet_list.setText("Liste des palettes");
        menu013_pallet_list.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu013_pallet_listActionPerformed(evt);
            }
        });
        MENU_00_00_00_REPORTS.add(menu013_pallet_list);

        menu018_open_pallet_list.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        menu018_open_pallet_list.setText("Palettes ouvertes");
        menu018_open_pallet_list.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu018_open_pallet_listActionPerformed(evt);
            }
        });
        MENU_00_00_00_REPORTS.add(menu018_open_pallet_list);

        menu018_open_pallet_list1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K, java.awt.event.InputEvent.CTRL_MASK));
        menu018_open_pallet_list1.setText("Palettes fermées");
        menu018_open_pallet_list1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu018_open_pallet_list1ActionPerformed(evt);
            }
        });
        MENU_00_00_00_REPORTS.add(menu018_open_pallet_list1);

        menu010_pallet_details.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        menu010_pallet_details.setText("Détails palette");
        menu010_pallet_details.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu010_pallet_detailsActionPerformed(evt);
            }
        });
        MENU_00_00_00_REPORTS.add(menu010_pallet_details);

        menu012_deleted_pallet.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menu012_deleted_pallet.setText("Palettes annulées");
        menu012_deleted_pallet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu012_deleted_palletActionPerformed(evt);
            }
        });
        MENU_00_00_00_REPORTS.add(menu012_deleted_pallet);

        menu012_harness_details.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        menu012_harness_details.setText("Détails faisceau");
        menu012_harness_details.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu012_harness_detailsActionPerformed(evt);
            }
        });
        MENU_00_00_00_REPORTS.add(menu012_harness_details);

        menu012_deleted_harness.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menu012_deleted_harness.setText("Faisceaux annulés");
        menu012_deleted_harness.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu012_deleted_harnessActionPerformed(evt);
            }
        });
        MENU_00_00_00_REPORTS.add(menu012_deleted_harness);

        menu017_ucs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
        menu017_ucs.setText("Standard pack");
        menu017_ucs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu017_ucsActionPerformed(evt);
            }
        });
        MENU_00_00_00_REPORTS.add(menu017_ucs);

        MENU_00_00_PACKAGING.add(MENU_00_00_00_REPORTS);

        openMenuItem.setMnemonic('o');
        openMenuItem.setText("Contenu Contenant");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });
        MENU_00_00_PACKAGING.add(openMenuItem);

        MENU_00_MODULES.add(MENU_00_00_PACKAGING);

        MENU_00_01_FG_RECEPTION.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        MENU_00_01_FG_RECEPTION.setText("Réception F.G");
        MENU_00_01_FG_RECEPTION.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MENU_00_01_FG_RECEPTIONActionPerformed(evt);
            }
        });
        MENU_00_MODULES.add(MENU_00_01_FG_RECEPTION);

        MENU_00_02_PART_STOCK.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        MENU_00_02_PART_STOCK.setText("Stock Produit Fini");
        MENU_00_02_PART_STOCK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MENU_00_02_PART_STOCKActionPerformed(evt);
            }
        });
        MENU_00_MODULES.add(MENU_00_02_PART_STOCK);

        MENU_00_03_PACKAGING_STOCK.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        MENU_00_03_PACKAGING_STOCK.setText("Stock Emballage");
        MENU_00_03_PACKAGING_STOCK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MENU_00_03_PACKAGING_STOCKActionPerformed(evt);
            }
        });
        MENU_00_MODULES.add(MENU_00_03_PACKAGING_STOCK);

        MENU_00_02_DISPATCH.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        MENU_00_02_DISPATCH.setText("Dispatch");
        MENU_00_02_DISPATCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MENU_00_02_DISPATCHActionPerformed(evt);
            }
        });
        MENU_00_MODULES.add(MENU_00_02_DISPATCH);

        menuBar.add(MENU_00_MODULES);

        MENU_01_CONFIGURATION.setText("Configuration");

        MENU_01_00_CONFIG_UCS.setText("Configuration Standard Pack");
        MENU_01_00_CONFIG_UCS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MENU_01_00_CONFIG_UCSActionPerformed(evt);
            }
        });
        MENU_01_CONFIGURATION.add(MENU_01_00_CONFIG_UCS);

        MENU_01_01_CONFIG_BARCODE.setText("Format Code à barre / QR");
        MENU_01_01_CONFIG_BARCODE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MENU_01_01_CONFIG_BARCODEActionPerformed(evt);
            }
        });
        MENU_01_CONFIGURATION.add(MENU_01_01_CONFIG_BARCODE);

        MENU_01_02_CONFIG_PACK_MASTERDATA.setText("Packaging Master Data");
        MENU_01_02_CONFIG_PACK_MASTERDATA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MENU_01_02_CONFIG_PACK_MASTERDATAActionPerformed(evt);
            }
        });
        MENU_01_CONFIGURATION.add(MENU_01_02_CONFIG_PACK_MASTERDATA);

        MENU_01_03_AVANCE.setText("Avancé");

        MENU_01_03_00_CONFIG_USERS.setText("Utilisateurs");
        MENU_01_03_00_CONFIG_USERS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MENU_01_03_00_CONFIG_USERSActionPerformed(evt);
            }
        });
        MENU_01_03_AVANCE.add(MENU_01_03_00_CONFIG_USERS);

        MENU_01_03_01_CONFIG_COMPANY.setText("Société");
        MENU_01_03_01_CONFIG_COMPANY.setEnabled(false);
        MENU_01_03_01_CONFIG_COMPANY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MENU_01_03_01_CONFIG_COMPANYActionPerformed(evt);
            }
        });
        MENU_01_03_AVANCE.add(MENU_01_03_01_CONFIG_COMPANY);

        MENU_01_03_02_CONFIG_WAREHOUSE.setText("Magasins");
        MENU_01_03_02_CONFIG_WAREHOUSE.setEnabled(false);
        MENU_01_03_02_CONFIG_WAREHOUSE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MENU_01_03_02_CONFIG_WAREHOUSEActionPerformed(evt);
            }
        });
        MENU_01_03_AVANCE.add(MENU_01_03_02_CONFIG_WAREHOUSE);

        MENU_01_03_03_CONFIG_SEGMENT.setText("Segment");
        MENU_01_03_03_CONFIG_SEGMENT.setEnabled(false);
        MENU_01_03_03_CONFIG_SEGMENT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MENU_01_03_03_CONFIG_SEGMENTActionPerformed(evt);
            }
        });
        MENU_01_03_AVANCE.add(MENU_01_03_03_CONFIG_SEGMENT);

        MENU_01_03_04_CONFIG_WOKRPLACE.setText("Workplace");
        MENU_01_03_04_CONFIG_WOKRPLACE.setEnabled(false);
        MENU_01_03_04_CONFIG_WOKRPLACE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MENU_01_03_04_CONFIG_WOKRPLACEActionPerformed(evt);
            }
        });
        MENU_01_03_AVANCE.add(MENU_01_03_04_CONFIG_WOKRPLACE);

        MENU_01_CONFIGURATION.add(MENU_01_03_AVANCE);

        menuBar.add(MENU_01_CONFIGURATION);

        jMenu1.setText("?");
        menuBar.add(jMenu1);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(desktopPane, javax.swing.GroupLayout.DEFAULT_SIZE, 885, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(desktopPane, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * To be used because the custom UILog object has not been initialized yet !
     */
    private static final Logger LOGGER = Logger.getLogger(TypeData.ClassName.class.getName());

    // static initializer
    {
        /* Create and display the form */
        //Helper.startSession();
        String feedback = PropertiesLoader.loadConfigProperties();
        LOGGER.log(Level.INFO, feedback);
    }

    /**
     * Creates new form UI0000_ProjectChoice
     */
    /*public AdminFrame() {
    initComponents();
    this.setTitle(GlobalVars.APP_NAME + " " + GlobalVars.APP_VERSION + " by " + GlobalVars.APP_AUTHOR);
    //versionLabel.setText(GlobalVars.APP_NAME + " " + GlobalVars.APP_VERSION);
    //authorLabel.setText(GlobalVars.APP_AUTHOR);
    
    this.setTitle(GlobalVars.APP_NAME + " " + GlobalVars.APP_VERSION);
    Helper.centerJFrame(this);
    this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    
    }*/

    private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed

        if (GlobalVars.APP_PROP.getProperty("PACKAGING_SCAN_MODE").equals("2")) {
            PackagingVars.mode2_context.setState(new Mode2_S010_UserCodeScan());
            PackagingVars.Packaging_Gui_Mode2 = new PACKAGING_UI0001_Main_Mode2(null, this);
            PackagingVars.Packaging_Gui_Mode2.reloadDataTable();
            PackagingVars.Packaging_Gui_Mode2.disableAdminMenus();
        } else if (GlobalVars.APP_PROP.getProperty("PACKAGING_SCAN_MODE").equals("3")) {
            PackagingVars.mode3_context.setState(new Mode3_S010_UserCodeScan());
            PackagingVars.Packaging_Gui_Mode3 = new PACKAGING_UI0001_Main_Mode3(null, this);
            PackagingVars.Packaging_Gui_Mode3.reloadDataTable();
            PackagingVars.Packaging_Gui_Mode3.disableAdminMenus();
        } else {
            UILog.severeDialog(this, "Error in PACKAGING_SCAN_MODE property. Check the config.properties values.", "Properties error");
        }
    }//GEN-LAST:event_openMenuItemActionPerformed

    private void menu011_prod_statisticsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu011_prod_statisticsActionPerformed
        PACKAGING_UI0011_ProdStatistics ui0011 = new PACKAGING_UI0011_ProdStatistics(this, false);
        ui0011.setVisible(true);
    }//GEN-LAST:event_menu011_prod_statisticsActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        PACKAGING_UI0019_EfficiencyCalculation eff_ui = new PACKAGING_UI0019_EfficiencyCalculation(this, false);
        eff_ui.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        PACKAGING_UI0020_ProdStatisticsByShift report = new PACKAGING_UI0020_ProdStatisticsByShift(this, false);
        report.setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void menu013_pallet_listActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu013_pallet_listActionPerformed
        new WAREHOUSE_FG_UI0002_PALLET_LIST(this, true).setVisible(true);
    }//GEN-LAST:event_menu013_pallet_listActionPerformed

    private void menu018_open_pallet_listActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu018_open_pallet_listActionPerformed
        new PACKAGING_UI0018_OpenContainer(this, true).setVisible(true);
    }//GEN-LAST:event_menu018_open_pallet_listActionPerformed

    private void menu018_open_pallet_list1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu018_open_pallet_list1ActionPerformed
        new PACKAGING_UI0022_ClosedContainer(this, true).setVisible(true);
    }//GEN-LAST:event_menu018_open_pallet_list1ActionPerformed

    private void menu010_pallet_detailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu010_pallet_detailsActionPerformed
        try {
            if (PackagingVars.context.getUser().getAccessLevel() == GlobalVars.PROFIL_ADMIN || PackagingVars.context.getUser().getAccessLevel() == GlobalVars.PROFIL_WAREHOUSE_AGENT) {
                new PACKAGING_UI0010_PalletDetails(this, rootPaneCheckingEnabled, true, true, true, true).setVisible(true);
            } else {
                new PACKAGING_UI0010_PalletDetails(this, rootPaneCheckingEnabled, false, false, false, false).setVisible(true);
            }
        } catch (NullPointerException ex) {
            new PACKAGING_UI0010_PalletDetails(this, rootPaneCheckingEnabled, false, false, false, false).setVisible(true);
        }
    }//GEN-LAST:event_menu010_pallet_detailsActionPerformed

    private void menu012_deleted_palletActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu012_deleted_palletActionPerformed
        new PACKAGING_UI0015_DroppedContainer(this, true).setVisible(true);
    }//GEN-LAST:event_menu012_deleted_palletActionPerformed

    private void menu012_harness_detailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu012_harness_detailsActionPerformed
        PACKAGING_UI0012_HarnessDetails harnessDetails;
        if (PackagingVars.context.getUser() != null && PackagingVars.context.getUser().getAccessLevel() == GlobalVars.PROFIL_ADMIN) {
            harnessDetails = new PACKAGING_UI0012_HarnessDetails(this, rootPaneCheckingEnabled, true);
        } else {
            harnessDetails = new PACKAGING_UI0012_HarnessDetails(this, rootPaneCheckingEnabled, false);
        }
        harnessDetails.setVisible(true);
    }//GEN-LAST:event_menu012_harness_detailsActionPerformed

    private void menu012_deleted_harnessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu012_deleted_harnessActionPerformed
        new PACKAGING_UI0016_DroppedHarness(this, true).setVisible(true);
    }//GEN-LAST:event_menu012_deleted_harnessActionPerformed

    private void menu017_ucsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu017_ucsActionPerformed
        new PACKAGING_UI0017_UCS_List(this, true).setVisible(true);
    }//GEN-LAST:event_menu017_ucsActionPerformed

    private void MENU_00_02_PART_STOCKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MENU_00_02_PART_STOCKActionPerformed
        PACKAGING_UI0021_FG_AVAILABLE_STOCK report = new PACKAGING_UI0021_FG_AVAILABLE_STOCK(this, false);
        report.setVisible(true);
    }//GEN-LAST:event_MENU_00_02_PART_STOCKActionPerformed

    private void MENU_00_01_FG_RECEPTIONActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MENU_00_01_FG_RECEPTIONActionPerformed
        //Create and display the dispatch interface
        WarehouseHelper.FinishGoodInput_Gui = new WAREHOUSE_FG_UI0001_SCAN(null, this, this.user);
    }//GEN-LAST:event_MENU_00_01_FG_RECEPTIONActionPerformed

    private void MENU_01_00_CONFIG_UCSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MENU_01_00_CONFIG_UCSActionPerformed
        new CONFIG_UI0001_CONFIG_UCS(this, true).setVisible(true);
    }//GEN-LAST:event_MENU_01_00_CONFIG_UCSActionPerformed

    private void MENU_01_01_CONFIG_BARCODEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MENU_01_01_CONFIG_BARCODEActionPerformed
        new CONFIG_UI0001_CONFIG_BARCODE().setVisible(true);
    }//GEN-LAST:event_MENU_01_01_CONFIG_BARCODEActionPerformed

    private void MENU_01_03_00_CONFIG_USERSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MENU_01_03_00_CONFIG_USERSActionPerformed
        new CONFIG_UI0003_CONFIG_USERS(this, true).setVisible(true);
    }//GEN-LAST:event_MENU_01_03_00_CONFIG_USERSActionPerformed

    private void MENU_00_02_DISPATCHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MENU_00_02_DISPATCHActionPerformed
        Helper.startSession();

        this.user.setLoginTime(new Date());
        WarehouseHelper.warehouse_reserv_context.setUser(this.user);
        WarehouseHelper.warehouse_reserv_context.getUser().update(WarehouseHelper.warehouse_reserv_context.getUser());
        //Go back to step S020
        ReservationState state = new S001_ReservPalletNumberScan();
        WarehouseHelper.warehouse_reserv_context.setState(state);
        String str = String.format(InfoMsg.APP_INFO0003[1],
                this.user.getFirstName() + " " + this.user.getLastName()
                + " / " + this.user.getLogin(), GlobalVars.APP_HOSTNAME,
                GlobalMethods.getStrTimeStamp() + " Dispatch interface : ");

        UILog.info(str);
        //Create and display the dispatch interface
        WarehouseHelper.Dispatch_Gui = new WAREHOUSE_DISPATCH_UI0002_DISPATCH_SCAN(null, this);

        //Set connected user label text
        WarehouseHelper.Dispatch_Gui.setUserLabelText(this.user.getFirstName() + " "
                + this.user.getLastName() + " Connecté à la machine "
                + "[" + GlobalVars.APP_HOSTNAME + "]"
        );

        //Auth réussie, Passage à l'état S02 de lecture des fiches Galia               
        WarehouseHelper.warehouse_reserv_context.setState(new S001_ReservPalletNumberScan());
    }//GEN-LAST:event_MENU_00_02_DISPATCHActionPerformed

    private void MENU_00_03_PACKAGING_STOCKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MENU_00_03_PACKAGING_STOCKActionPerformed
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
        PackagingHelper.Packaging_Main_Gui = new PACKAGING_WAREHOUSE_UI0001_MAIN_FORM(null, this);

        this.dispose();
    }//GEN-LAST:event_MENU_00_03_PACKAGING_STOCKActionPerformed

    private void MENU_01_02_CONFIG_PACK_MASTERDATAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MENU_01_02_CONFIG_PACK_MASTERDATAActionPerformed
        CONFIG_UI0002_CONFIG_PACK_MASTERDATA packaging_config = new CONFIG_UI0002_CONFIG_PACK_MASTERDATA(null, false);
        packaging_config.setVisible(true);
        packaging_config.toFront();
        packaging_config.repaint();
    }//GEN-LAST:event_MENU_01_02_CONFIG_PACK_MASTERDATAActionPerformed

    private void MENU_01_03_04_CONFIG_WOKRPLACEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MENU_01_03_04_CONFIG_WOKRPLACEActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_MENU_01_03_04_CONFIG_WOKRPLACEActionPerformed

    private void MENU_01_03_01_CONFIG_COMPANYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MENU_01_03_01_CONFIG_COMPANYActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_MENU_01_03_01_CONFIG_COMPANYActionPerformed

    private void MENU_01_03_02_CONFIG_WAREHOUSEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MENU_01_03_02_CONFIG_WAREHOUSEActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_MENU_01_03_02_CONFIG_WAREHOUSEActionPerformed

    private void MENU_01_03_03_CONFIG_SEGMENTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MENU_01_03_03_CONFIG_SEGMENTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_MENU_01_03_03_CONFIG_SEGMENTActionPerformed

    /**
     * @param args the command line arguments
     *
     * public static void main(String args[]) { /* Set the Nimbus look and feel
     */
    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
    /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
     */
 /*try {
    for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
    if ("Nimbus".equals(info.getName())) {
    javax.swing.UIManager.setLookAndFeel(info.getClassName());
    break;
    }
    }
    } catch (ClassNotFoundException ex) {
    java.util.logging.Logger.getLogger(AdminFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
    java.util.logging.Logger.getLogger(AdminFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
    java.util.logging.Logger.getLogger(AdminFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
    java.util.logging.Logger.getLogger(AdminFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }*/
    //</editor-fold>
    //</editor-fold>

    /*
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        AdminFrame ui = new AdminFrame();

                        ui.setVisible(true);
                        UILog.createDailyLogFile(GlobalVars.APP_PROP.getProperty("LOG_PATH"));
                        PropertiesLoader.createDailyOutPrintDir(GlobalVars.APP_PROP.getProperty("PRINT_DIR"),
                                GlobalVars.APP_PROP.getProperty("PRINT_PALLET_DIR"),
                                GlobalVars.APP_PROP.getProperty("PRINT_CLOSING_PALLET_DIR"),
                                GlobalVars.APP_PROP.getProperty("PRINT_PICKING_SHEET_DIR"),
                                GlobalVars.APP_PROP.getProperty("PRINT_DISPATCH_SHEET_DIR"));
                    }
                });
            }
        });
    } */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu MENU_00_00_00_REPORTS;
    private javax.swing.JMenu MENU_00_00_PACKAGING;
    private javax.swing.JMenuItem MENU_00_01_FG_RECEPTION;
    private javax.swing.JMenuItem MENU_00_02_DISPATCH;
    private javax.swing.JMenuItem MENU_00_02_PART_STOCK;
    private javax.swing.JMenuItem MENU_00_03_PACKAGING_STOCK;
    private javax.swing.JMenu MENU_00_MODULES;
    private javax.swing.JMenuItem MENU_01_00_CONFIG_UCS;
    private javax.swing.JMenuItem MENU_01_01_CONFIG_BARCODE;
    private javax.swing.JMenuItem MENU_01_02_CONFIG_PACK_MASTERDATA;
    private javax.swing.JMenuItem MENU_01_03_00_CONFIG_USERS;
    private javax.swing.JMenuItem MENU_01_03_01_CONFIG_COMPANY;
    private javax.swing.JMenuItem MENU_01_03_02_CONFIG_WAREHOUSE;
    private javax.swing.JMenuItem MENU_01_03_03_CONFIG_SEGMENT;
    private javax.swing.JMenuItem MENU_01_03_04_CONFIG_WOKRPLACE;
    private javax.swing.JMenu MENU_01_03_AVANCE;
    private javax.swing.JMenu MENU_01_CONFIGURATION;
    private javax.swing.JDesktopPane desktopPane;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem menu010_pallet_details;
    private javax.swing.JMenuItem menu011_prod_statistics;
    private javax.swing.JMenuItem menu012_deleted_harness;
    private javax.swing.JMenuItem menu012_deleted_pallet;
    private javax.swing.JMenuItem menu012_harness_details;
    private javax.swing.JMenuItem menu013_pallet_list;
    private javax.swing.JMenuItem menu017_ucs;
    private javax.swing.JMenuItem menu018_open_pallet_list;
    private javax.swing.JMenuItem menu018_open_pallet_list1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openMenuItem;
    // End of variables declaration//GEN-END:variables

}
