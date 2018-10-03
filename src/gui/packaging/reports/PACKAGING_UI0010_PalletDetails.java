/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.packaging.reports;

import __main__.GlobalVars;
import com.itextpdf.text.DocumentException;
import helper.Helper;
import helper.HQLHelper;
import entity.BaseContainer;
import entity.BaseHarness;
import entity.HisOpenPalPrint;
import entity.LoadPlan;
import entity.LoadPlanLine;
import gui.packaging.PackagingVars;
import gui.packaging.mode1.state.Mode1_S050_ClosingPallet;
import gui.packaging.mode3.state.Mode3_S040_ClosingPallet;
import gui.warehouse_fg_reception.WAREHOUSE_FG_UI0002_PALLET_LIST;
import helper.PrinterHelper;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Query;
import gui.packaging.mode2.state.Mode2_S040_ClosingPallet;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import ui.UILog;

/**
 *
 * @author user
 */
public final class PACKAGING_UI0010_PalletDetails extends javax.swing.JFrame {

    @SuppressWarnings("UseOfObsoleteCollectionType")
    Vector<String> searchResult_table_header = new Vector<>();
    @SuppressWarnings("UseOfObsoleteCollectionType")
    Vector searchResult_table_data = new Vector();
    private BaseContainer bc = new BaseContainer();
    private boolean dropAccess = false;
    private boolean printOpenSheet = false;
    private boolean printCloseSheet = false;
    private String palletNumber = "";
    private String dispatchLabelNo = "";

    /**
     * Creates new form UI0010_PalletDetails
     *
     * @param parent
     * @param modal
     */
    public PACKAGING_UI0010_PalletDetails(java.awt.Frame parent, boolean modal) {
        //super(parent, modal);        
        initComponents();
        initGui();
    }

    /**
     * Creates new form UI0010_PalletDetails
     *
     * @param parent
     * @param modal
     * @param palletNumber : Requested container number to be displayed
     * @param dispatchLabel
     * @param searchMode
     */
    public PACKAGING_UI0010_PalletDetails(java.awt.Frame parent, boolean modal, String palletNumber, String dispatchLabel, int searchMode) {
        this.palletNumber = palletNumber;
        this.dispatchLabelNo = dispatchLabel;
        initComponents();
        initGui();
        this.searchForPallet(palletNumber, dispatchLabel, searchMode);
        this.palletNum_txtbox.setText(palletNumber);

    }

    /**
     * Creates new form UI0010_PalletDetails
     *
     * @param parent
     * @param modal
     * @param drop : Show drop button in the form
     * @param printOpenSheet
     * @param printCloseSheet
     * @param emptyAccess
     */
    public PACKAGING_UI0010_PalletDetails(java.awt.Frame parent, boolean modal, boolean drop, boolean printOpenSheet,
            boolean printCloseSheet, boolean emptyAccess) {
        //super(parent, modal);
        initComponents();

        if (drop == false) {
            dropButton.setVisible(false);
        }
        if (printOpenSheet == false) {
            printOpenSheetButton.setVisible(false);
        }
        if (printCloseSheet == false) {
            printCloseSheetButton.setVisible(false);
        }

        initGui();
    }

    /**
     * Creates new form UI0010_PalletDetails
     *
     * @param parent
     * @param modal
     * @param palletNumber : Requested container number to be displayed
     * @param dispatchLabelNo
     * @param searchMode
     * @param drop : Show drop button in the form
     * @param printOpenSheet : Show print open sheet button in the form
     * @param printCloseSheet : Show print close sheet button in the form
     *
     */
    public PACKAGING_UI0010_PalletDetails(java.awt.Frame parent, boolean modal,
            String palletNumber, String dispatchLabelNo, int searchMode, boolean drop, boolean printOpenSheet,
            boolean printCloseSheet) {
        this.palletNumber = palletNumber;
        this.dispatchLabelNo = dispatchLabelNo;
        initComponents();

        this.setDropAccess(drop);
        this.setPrintOpenSheet(printOpenSheet);
        this.setPrintCloseSheet(printCloseSheet);

        initGui();
        this.searchForPallet(palletNumber, dispatchLabelNo, searchMode);
        this.palletNum_txtbox.setText(palletNumber);

        System.out.println("state_txtbox.getText() " + state_txtbox.getText());
        //Avtiver/Desactiver le bouton "Continuer fermeture..."
        if (state_txtbox.getText().equals(GlobalVars.PALLET_WAITING) && PackagingVars.context.getUser() != null) {
            continue_btn.setEnabled(true);
        } else {
            continue_btn.setEnabled(false);
        }

        //Activer ou désactiver le bouton print closing palette si Open
        if (state_txtbox.getText().equals(GlobalVars.PALLET_OPEN) && PackagingVars.context.getUser() != null) {
            printCloseSheetButton.setEnabled(false);
        } else {
            printCloseSheetButton.setEnabled(true);
        }

    }

    private void initGui() {
        //Center the this dialog in the screen
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        //Disable continue button
        continue_btn.setEnabled(false);

        //Desable table edition
        disableEditingTable();

        //Load table header
        load_container_table_header();

        this.setTitle(this.getTitle() + " " + this.palletNumber);

    }

    public void disableEditingTable() {
        for (int c = 0; c < searchResult_table.getColumnCount(); c++) {
            Class<?> col_class = searchResult_table.getColumnClass(c);
            searchResult_table.setDefaultEditor(col_class, null);        // remove editor            
        }
    }

    public BaseContainer getBaseContainer() {
        return bc;
    }

    public void setBaseContainer(BaseContainer bc) {
        this.bc = bc;
    }

    public boolean isDropAccess() {
        return dropAccess;
    }

    public void setDropAccess(boolean dropAccess) {
        this.dropAccess = dropAccess;
        dropButton.setVisible(dropAccess);
    }

    public boolean isPrintOpenSheet() {
        return printOpenSheet;
    }

    public void setPrintOpenSheet(boolean printOpenSheet) {
        this.printOpenSheet = printOpenSheet;
        printOpenSheetButton.setVisible(printOpenSheet);
    }

    public boolean isPrintCloseSheet() {
        return printCloseSheet;
    }

    public void setPrintCloseSheet(boolean printCloseSheet) {
        this.printCloseSheet = printCloseSheet;
        printCloseSheetButton.setVisible(printCloseSheet);
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
        palletNum_txtbox = new javax.swing.JTextField();
        search_btn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        msg_lbl = new javax.swing.JLabel();
        table_scroll = new javax.swing.JScrollPane();
        searchResult_table = new javax.swing.JTable();
        palletNumber_txtbox = new javax.swing.JTextField();
        history_btn = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        continue_btn = new javax.swing.JButton();
        clear_btn = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        details_pallet_1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        palletId_txtbox = new javax.swing.JTextField();
        harnessPart_txtbox = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        index_txtbox = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        supplierPartNumber_txtbox = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        workstation_txtbox = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        qtyExptected_txtbox = new javax.swing.JTextField();
        qtyRead_txtbox = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        packType_txtbox = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        startTime_txtbox = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        completeTime_txtbox = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        workingTime_txtbox = new javax.swing.JTextField();
        user_txtbox = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        create_user_txtbox = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        state_txtbox = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        comment_txt = new javax.swing.JTextArea();
        login_lbl3 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        special_order_txtbox = new javax.swing.JTextField();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        project_txtbox = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        destination_txtbox = new javax.swing.JTextField();
        details_chargement_2 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        net_weight_txt = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        gross_weight_txt = new javax.swing.JTextField();
        volume_txt = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        eng_change_date_txt = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        eng_change_txt = new javax.swing.JTextArea();
        jLabel37 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        article_desc_txt = new javax.swing.JTextArea();
        jLabel38 = new javax.swing.JLabel();
        details_chargement = new javax.swing.JPanel();
        loadPlanId_txtbox = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        planCreateTime_txtbox = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        planDispatchTime_txtbox = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        planDestination_txtbox = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        planStatus_txtbox = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        planCreateUser_txtbox = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        lineCreateUser_txtbox = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        lineCreateTime_txtbox = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        pile_txtbox = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        position_txtbox = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        dispatchLabelNo_txtbox = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        stdTime_txtbox = new javax.swing.JTextField();
        total_stdTime_txtbox = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        dispatch_label_no_txtbox = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        pack_list_btn = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        printOpenSheetButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        printCloseSheetButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        dropButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Détails palette");
        setBackground(new java.awt.Color(51, 51, 51));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));

        palletNum_txtbox.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        palletNum_txtbox.setForeground(new java.awt.Color(0, 0, 153));
        palletNum_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                palletNum_txtboxActionPerformed(evt);
            }
        });
        palletNum_txtbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                palletNum_txtboxKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                palletNum_txtboxKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                palletNum_txtboxKeyTyped(evt);
            }
        });

        search_btn.setBackground(new java.awt.Color(0, 102, 102));
        search_btn.setForeground(new java.awt.Color(255, 255, 255));
        search_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/edit-find.png"))); // NOI18N
        search_btn.setText("Recherche");
        search_btn.setToolTipText("Search");
        search_btn.setBorderPainted(false);
        search_btn.setMaximumSize(new java.awt.Dimension(24, 24));
        search_btn.setMinimumSize(new java.awt.Dimension(24, 24));
        search_btn.setOpaque(false);
        search_btn.setPreferredSize(new java.awt.Dimension(24, 24));
        search_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search_btnActionPerformed(evt);
            }
        });

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Pack Number");

        msg_lbl.setForeground(new java.awt.Color(255, 255, 51));

        searchResult_table.setAutoCreateRowSorter(true);
        searchResult_table.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        searchResult_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Create Time", "Create User", "Harness Part ", "Counter", "Pallet Number"
            }
        ));
        table_scroll.setViewportView(searchResult_table);

        palletNumber_txtbox.setEditable(false);
        palletNumber_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        palletNumber_txtbox.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        palletNumber_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                palletNumber_txtboxActionPerformed(evt);
            }
        });

        history_btn.setBackground(new java.awt.Color(0, 102, 102));
        history_btn.setForeground(new java.awt.Color(255, 255, 255));
        history_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/time-icon.png"))); // NOI18N
        history_btn.setText("Historique");
        history_btn.setToolTipText("Search");
        history_btn.setBorderPainted(false);
        history_btn.setMaximumSize(new java.awt.Dimension(24, 24));
        history_btn.setMinimumSize(new java.awt.Dimension(24, 24));
        history_btn.setOpaque(false);
        history_btn.setPreferredSize(new java.awt.Dimension(24, 24));
        history_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                history_btnActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Détails palette");

        continue_btn.setBackground(java.awt.Color.cyan);
        continue_btn.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        continue_btn.setText("Continuer fermeture...");
        continue_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                continue_btnActionPerformed(evt);
            }
        });

        clear_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/edit-clear.png"))); // NOI18N
        clear_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clear_btnActionPerformed(evt);
            }
        });

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);

        details_pallet_1.setBackground(new java.awt.Color(51, 51, 51));

        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("ID");

        palletId_txtbox.setEditable(false);
        palletId_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        palletId_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                palletId_txtboxActionPerformed(evt);
            }
        });

        harnessPart_txtbox.setEditable(false);
        harnessPart_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        harnessPart_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                harnessPart_txtboxActionPerformed(evt);
            }
        });

        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Harness Part");

        index_txtbox.setEditable(false);
        index_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        index_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                index_txtboxActionPerformed(evt);
            }
        });

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Index");

        supplierPartNumber_txtbox.setEditable(false);
        supplierPartNumber_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        supplierPartNumber_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supplierPartNumber_txtboxActionPerformed(evt);
            }
        });

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Supplier Part Number");

        workstation_txtbox.setEditable(false);
        workstation_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        workstation_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                workstation_txtboxActionPerformed(evt);
            }
        });

        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Workstation");

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Quantity Expected");

        qtyExptected_txtbox.setEditable(false);
        qtyExptected_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        qtyExptected_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qtyExptected_txtboxActionPerformed(evt);
            }
        });

        qtyRead_txtbox.setEditable(false);
        qtyRead_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        qtyRead_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qtyRead_txtboxActionPerformed(evt);
            }
        });

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Quantity Read");

        packType_txtbox.setEditable(false);
        packType_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        packType_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                packType_txtboxActionPerformed(evt);
            }
        });

        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Pack Type");

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Start Time");

        startTime_txtbox.setEditable(false);
        startTime_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        startTime_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startTime_txtboxActionPerformed(evt);
            }
        });

        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Complete Time");

        completeTime_txtbox.setEditable(false);
        completeTime_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        completeTime_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                completeTime_txtboxActionPerformed(evt);
            }
        });

        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Working time (min)");

        workingTime_txtbox.setEditable(false);
        workingTime_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        workingTime_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                workingTime_txtboxActionPerformed(evt);
            }
        });

        user_txtbox.setEditable(false);
        user_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        user_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                user_txtboxActionPerformed(evt);
            }
        });

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Create Login");

        create_user_txtbox.setEditable(false);
        create_user_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        create_user_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                create_user_txtboxActionPerformed(evt);
            }
        });

        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("State");

        state_txtbox.setEditable(false);
        state_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        state_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                state_txtboxActionPerformed(evt);
            }
        });

        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setText("Create User");

        comment_txt.setColumns(10);
        comment_txt.setRows(3);
        comment_txt.setToolTipText("");
        comment_txt.setEnabled(false);
        jScrollPane1.setViewportView(comment_txt);

        login_lbl3.setForeground(new java.awt.Color(255, 255, 255));
        login_lbl3.setText("Special Order Comment");

        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setText("Special");

        special_order_txtbox.setEditable(false);
        special_order_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        special_order_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                special_order_txtboxActionPerformed(evt);
            }
        });

        project_txtbox.setEditable(false);
        project_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        project_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                project_txtboxActionPerformed(evt);
            }
        });

        jLabel39.setForeground(new java.awt.Color(255, 255, 255));
        jLabel39.setText("Project");

        jLabel40.setForeground(new java.awt.Color(255, 255, 255));
        jLabel40.setText("Destination");

        destination_txtbox.setEditable(false);
        destination_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        destination_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                destination_txtboxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout details_pallet_1Layout = new javax.swing.GroupLayout(details_pallet_1);
        details_pallet_1.setLayout(details_pallet_1Layout);
        details_pallet_1Layout.setHorizontalGroup(
            details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(details_pallet_1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(details_pallet_1Layout.createSequentialGroup()
                        .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(qtyExptected_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(qtyRead_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(details_pallet_1Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(packType_txtbox)))
                    .addGroup(details_pallet_1Layout.createSequentialGroup()
                        .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(palletId_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(harnessPart_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(index_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(details_pallet_1Layout.createSequentialGroup()
                                .addComponent(jLabel40)
                                .addGap(24, 24, 24))
                            .addComponent(destination_txtbox)))
                    .addGroup(details_pallet_1Layout.createSequentialGroup()
                        .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6)
                            .addComponent(startTime_txtbox, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                            .addComponent(jLabel16)
                            .addComponent(workstation_txtbox))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(completeTime_txtbox, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel2)
                            .addComponent(user_txtbox))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(details_pallet_1Layout.createSequentialGroup()
                                .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel27))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(workingTime_txtbox)
                            .addComponent(create_user_txtbox))))
                .addGap(18, 18, 18)
                .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(details_pallet_1Layout.createSequentialGroup()
                        .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(supplierPartNumber_txtbox)
                            .addComponent(jLabel29)
                            .addComponent(special_order_txtbox))
                        .addGap(18, 18, 18)
                        .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(state_txtbox, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                            .addComponent(jLabel14)
                            .addComponent(jLabel39)
                            .addComponent(project_txtbox))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE))
                    .addComponent(login_lbl3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(details_pallet_1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(filler1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        details_pallet_1Layout.setVerticalGroup(
            details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(details_pallet_1Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, details_pallet_1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(supplierPartNumber_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, details_pallet_1Layout.createSequentialGroup()
                        .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(index_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(harnessPart_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, details_pallet_1Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(palletId_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, details_pallet_1Layout.createSequentialGroup()
                        .addComponent(jLabel39)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(project_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(destination_txtbox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, details_pallet_1Layout.createSequentialGroup()
                        .addComponent(jLabel40)
                        .addGap(30, 30, 30)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(details_pallet_1Layout.createSequentialGroup()
                        .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(details_pallet_1Layout.createSequentialGroup()
                                .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(qtyRead_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(qtyExptected_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, details_pallet_1Layout.createSequentialGroup()
                                .addComponent(jLabel29)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(special_order_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, details_pallet_1Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(state_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(details_pallet_1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(details_pallet_1Layout.createSequentialGroup()
                                            .addComponent(jLabel6)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(startTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(details_pallet_1Layout.createSequentialGroup()
                                            .addComponent(jLabel7)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(completeTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(details_pallet_1Layout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(workingTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel27)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel16))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(details_pallet_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(user_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(workstation_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(create_user_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(details_pallet_1Layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(login_lbl3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(details_pallet_1Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(packType_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("Détails palette 1/2", details_pallet_1);

        details_chargement_2.setBackground(new java.awt.Color(51, 51, 51));

        jLabel33.setForeground(new java.awt.Color(255, 255, 255));
        jLabel33.setText("Net weight");

        net_weight_txt.setEditable(false);
        net_weight_txt.setBackground(new java.awt.Color(255, 255, 255));
        net_weight_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                net_weight_txtActionPerformed(evt);
            }
        });

        jLabel34.setForeground(new java.awt.Color(255, 255, 255));
        jLabel34.setText("Gross weight");

        gross_weight_txt.setEditable(false);
        gross_weight_txt.setBackground(new java.awt.Color(255, 255, 255));
        gross_weight_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gross_weight_txtActionPerformed(evt);
            }
        });

        volume_txt.setEditable(false);
        volume_txt.setBackground(new java.awt.Color(255, 255, 255));
        volume_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                volume_txtActionPerformed(evt);
            }
        });

        jLabel35.setForeground(new java.awt.Color(255, 255, 255));
        jLabel35.setText("Volume");

        eng_change_date_txt.setEditable(false);
        eng_change_date_txt.setBackground(new java.awt.Color(255, 255, 255));
        eng_change_date_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eng_change_date_txtActionPerformed(evt);
            }
        });

        jLabel36.setForeground(new java.awt.Color(255, 255, 255));
        jLabel36.setText("Engineering change date");

        eng_change_txt.setEditable(false);
        eng_change_txt.setColumns(20);
        eng_change_txt.setRows(5);
        jScrollPane2.setViewportView(eng_change_txt);

        jLabel37.setForeground(new java.awt.Color(255, 255, 255));
        jLabel37.setText("Engineering change");

        article_desc_txt.setEditable(false);
        article_desc_txt.setColumns(20);
        article_desc_txt.setRows(5);
        jScrollPane3.setViewportView(article_desc_txt);

        jLabel38.setForeground(new java.awt.Color(255, 255, 255));
        jLabel38.setText("Article description");

        javax.swing.GroupLayout details_chargement_2Layout = new javax.swing.GroupLayout(details_chargement_2);
        details_chargement_2.setLayout(details_chargement_2Layout);
        details_chargement_2Layout.setHorizontalGroup(
            details_chargement_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(details_chargement_2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(details_chargement_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(details_chargement_2Layout.createSequentialGroup()
                        .addGroup(details_chargement_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(net_weight_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel33))
                        .addGap(18, 18, 18)
                        .addGroup(details_chargement_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(eng_change_date_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel36)))
                    .addGroup(details_chargement_2Layout.createSequentialGroup()
                        .addGroup(details_chargement_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel34)
                            .addComponent(gross_weight_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(volume_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel35))
                        .addGap(18, 18, 18)
                        .addGroup(details_chargement_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel37)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(details_chargement_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel38)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(125, Short.MAX_VALUE))
        );
        details_chargement_2Layout.setVerticalGroup(
            details_chargement_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(details_chargement_2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(details_chargement_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(details_chargement_2Layout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(net_weight_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(details_chargement_2Layout.createSequentialGroup()
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(eng_change_date_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(details_chargement_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(jLabel37)
                    .addComponent(jLabel38))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(details_chargement_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, details_chargement_2Layout.createSequentialGroup()
                        .addComponent(gross_weight_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(volume_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(99, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Détails pallete 2/2", details_chargement_2);

        details_chargement.setBackground(new java.awt.Color(51, 51, 51));

        loadPlanId_txtbox.setEditable(false);
        loadPlanId_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        loadPlanId_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadPlanId_txtboxActionPerformed(evt);
            }
        });

        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("ID plan de chargement");

        planCreateTime_txtbox.setEditable(false);
        planCreateTime_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        planCreateTime_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                planCreateTime_txtboxActionPerformed(evt);
            }
        });

        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Date création du plan");

        planDispatchTime_txtbox.setEditable(false);
        planDispatchTime_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        planDispatchTime_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                planDispatchTime_txtboxActionPerformed(evt);
            }
        });

        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText(" Date Dispatch");

        planDestination_txtbox.setEditable(false);
        planDestination_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        planDestination_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                planDestination_txtboxActionPerformed(evt);
            }
        });

        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("Destination");

        planStatus_txtbox.setEditable(false);
        planStatus_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        planStatus_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                planStatus_txtboxActionPerformed(evt);
            }
        });

        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("Status");

        planCreateUser_txtbox.setEditable(false);
        planCreateUser_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        planCreateUser_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                planCreateUser_txtboxActionPerformed(evt);
            }
        });

        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("Utilisateur");

        lineCreateUser_txtbox.setEditable(false);
        lineCreateUser_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        lineCreateUser_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lineCreateUser_txtboxActionPerformed(evt);
            }
        });

        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("Utilisateur");

        lineCreateTime_txtbox.setEditable(false);
        lineCreateTime_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        lineCreateTime_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lineCreateTime_txtboxActionPerformed(evt);
            }
        });

        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setText("Date création de ligne");

        pile_txtbox.setEditable(false);
        pile_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        pile_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pile_txtboxActionPerformed(evt);
            }
        });

        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Pile Num");

        position_txtbox.setEditable(false);
        position_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        position_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                position_txtboxActionPerformed(evt);
            }
        });

        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setText("Position");

        dispatchLabelNo_txtbox.setEditable(false);
        dispatchLabelNo_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        dispatchLabelNo_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispatchLabelNo_txtboxActionPerformed(evt);
            }
        });

        jLabel32.setForeground(new java.awt.Color(255, 255, 255));
        jLabel32.setText("Etiquette Dispatch");

        javax.swing.GroupLayout details_chargementLayout = new javax.swing.GroupLayout(details_chargement);
        details_chargement.setLayout(details_chargementLayout);
        details_chargementLayout.setHorizontalGroup(
            details_chargementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(details_chargementLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(details_chargementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(loadPlanId_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(planStatus_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(jLabel19)
                    .addComponent(pile_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(details_chargementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(planCreateUser_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(planCreateTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(jLabel28)
                    .addComponent(position_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(details_chargementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lineCreateTime_txtbox)
                    .addComponent(planDestination_txtbox)
                    .addComponent(jLabel22)
                    .addComponent(jLabel21)
                    .addComponent(planDispatchTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(details_chargementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel32)
                    .addGroup(details_chargementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(dispatchLabelNo_txtbox)
                        .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lineCreateUser_txtbox, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)))
                .addContainerGap(188, Short.MAX_VALUE))
        );
        details_chargementLayout.setVerticalGroup(
            details_chargementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(details_chargementLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(details_chargementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, details_chargementLayout.createSequentialGroup()
                        .addGroup(details_chargementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(details_chargementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(loadPlanId_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(planCreateTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(details_chargementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel24)
                            .addComponent(jLabel23))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(details_chargementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(planCreateUser_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(planStatus_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(details_chargementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(details_chargementLayout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addComponent(jLabel19))
                            .addGroup(details_chargementLayout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(jLabel28))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, details_chargementLayout.createSequentialGroup()
                        .addGroup(details_chargementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(details_chargementLayout.createSequentialGroup()
                                .addComponent(jLabel32)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dispatchLabelNo_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(details_chargementLayout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(planDispatchTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(planDestination_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(28, 28, 28)
                        .addGroup(details_chargementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(details_chargementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(pile_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(position_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lineCreateTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lineCreateUser_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(59, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Détails chargement", details_chargement);

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Liste des faisceaux");

        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setText("Standard Time");

        stdTime_txtbox.setEditable(false);
        stdTime_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        stdTime_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stdTime_txtboxActionPerformed(evt);
            }
        });

        total_stdTime_txtbox.setEditable(false);
        total_stdTime_txtbox.setBackground(new java.awt.Color(255, 255, 255));
        total_stdTime_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                total_stdTime_txtboxActionPerformed(evt);
            }
        });

        jLabel31.setForeground(new java.awt.Color(255, 255, 255));
        jLabel31.setText("Total Standard Time");

        dispatch_label_no_txtbox.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        dispatch_label_no_txtbox.setForeground(new java.awt.Color(0, 0, 153));
        dispatch_label_no_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispatch_label_no_txtboxActionPerformed(evt);
            }
        });
        dispatch_label_no_txtbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                dispatch_label_no_txtboxKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                dispatch_label_no_txtboxKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                dispatch_label_no_txtboxKeyTyped(evt);
            }
        });

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Dispatch Label No");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(table_scroll)
            .addComponent(jSeparator4)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(msg_lbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(palletNum_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(dispatch_label_no_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(clear_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(search_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(history_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(continue_btn))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel30)
                                .addGap(18, 18, 18)
                                .addComponent(stdTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel31)
                                .addGap(18, 18, 18)
                                .addComponent(total_stdTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(palletNumber_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel15)
                    .addComponent(palletNumber_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel5))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(palletNum_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dispatch_label_no_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(continue_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(search_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(clear_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(history_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5)
                .addComponent(msg_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel31)
                        .addComponent(total_stdTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel17)
                        .addComponent(jLabel30)
                        .addComponent(stdTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(table_scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jToolBar1.setBackground(new java.awt.Color(0, 51, 51));
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        pack_list_btn.setBackground(new java.awt.Color(0, 51, 51));
        pack_list_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/list.png"))); // NOI18N
        pack_list_btn.setText("Liste palettes");
        pack_list_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pack_list_btnActionPerformed(evt);
            }
        });
        jToolBar1.add(pack_list_btn);

        jSeparator5.setOpaque(true);
        jToolBar1.add(jSeparator5);

        printOpenSheetButton.setBackground(java.awt.SystemColor.desktop);
        printOpenSheetButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/document-print.png"))); // NOI18N
        printOpenSheetButton.setToolTipText("Print Opening Sheet");
        printOpenSheetButton.setBorderPainted(false);
        printOpenSheetButton.setFocusable(false);
        printOpenSheetButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        printOpenSheetButton.setMaximumSize(new java.awt.Dimension(24, 24));
        printOpenSheetButton.setMinimumSize(new java.awt.Dimension(24, 24));
        printOpenSheetButton.setOpaque(false);
        printOpenSheetButton.setPreferredSize(new java.awt.Dimension(24, 24));
        printOpenSheetButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        printOpenSheetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printOpenSheetButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(printOpenSheetButton);
        jToolBar1.add(jSeparator1);

        printCloseSheetButton.setBackground(java.awt.SystemColor.desktop);
        printCloseSheetButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/document-print.png"))); // NOI18N
        printCloseSheetButton.setToolTipText("Print Closing Sheet");
        printCloseSheetButton.setBorderPainted(false);
        printCloseSheetButton.setFocusable(false);
        printCloseSheetButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        printCloseSheetButton.setMaximumSize(new java.awt.Dimension(24, 24));
        printCloseSheetButton.setMinimumSize(new java.awt.Dimension(24, 24));
        printCloseSheetButton.setOpaque(false);
        printCloseSheetButton.setPreferredSize(new java.awt.Dimension(24, 24));
        printCloseSheetButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        printCloseSheetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printCloseSheetButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(printCloseSheetButton);

        jSeparator2.setOpaque(true);
        jToolBar1.add(jSeparator2);

        dropButton.setBackground(java.awt.SystemColor.desktop);
        dropButton.setForeground(new java.awt.Color(255, 255, 255));
        dropButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/edit-delete.png"))); // NOI18N
        dropButton.setToolTipText("Drop Pallet");
        dropButton.setBorderPainted(false);
        dropButton.setMaximumSize(new java.awt.Dimension(24, 24));
        dropButton.setMinimumSize(new java.awt.Dimension(24, 24));
        dropButton.setOpaque(false);
        dropButton.setPreferredSize(new java.awt.Dimension(24, 24));
        dropButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dropButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(dropButton);

        jSeparator3.setOpaque(true);
        jToolBar1.add(jSeparator3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void clearSearchBox() {
        //Vider le champs de text scan
        palletNum_txtbox.setText("");
        palletNum_txtbox.requestFocusInWindow();
    }

    public void load_container_table_header() {
        this.reset_container_table_content();
        searchResult_table_header.add("ID");
        searchResult_table_header.add("Create Time");
        searchResult_table_header.add("Create User");
        searchResult_table_header.add("Harness Part");
        searchResult_table_header.add("Counter");
        searchResult_table_header.add("Pallet Number");

        searchResult_table.setModel(new DefaultTableModel(searchResult_table_data, searchResult_table_header));
    }

    @SuppressWarnings("UseOfObsoleteCollectionType")
    public void reset_container_table_content() {
        searchResult_table_data = new Vector();
        DefaultTableModel dataModel = new DefaultTableModel(searchResult_table_data, searchResult_table_header);
        searchResult_table.setModel(dataModel);
    }

    public void reload_container_table_data(List resultList) {
        this.reset_container_table_content();

        Set<BaseHarness> harnessList = new HashSet<>(0);
        for (Object o : resultList) {
            BaseHarness base_harness = (BaseHarness) o;
            harnessList.add(base_harness);
            @SuppressWarnings("UseOfObsoleteCollectionType")
            Vector<Object> oneRow = new Vector<>();

            oneRow.add(base_harness.getId());
            oneRow.add(base_harness.getCreateTimeString("dd/MM/yy HH:mm"));
            oneRow.add(base_harness.getUser() + " / " + base_harness.getCreateUser());
            oneRow.add(base_harness.getHarnessPart());
            oneRow.add(base_harness.getCounter());
            oneRow.add(base_harness.getPalletNumber());

            searchResult_table_data.add(oneRow);
        }

        //Charger la liste des fx (Lazy mode) !
        this.bc.setHarnessList(harnessList);

        searchResult_table.setModel(new DefaultTableModel(searchResult_table_data, searchResult_table_header));
        searchResult_table.setAutoCreateRowSorter(true);
    }

    private void reload_load_plan_data(List result) {
        this.clearLoadPlanFields();

        System.out.println("reload_load_plan_data result" + result.size());

        LoadPlanLine l = (LoadPlanLine) result.get(0);
        pile_txtbox.setText("" + l.getPileNum());
        position_txtbox.setText("" + l.getId());
        lineCreateUser_txtbox.setText(l.getUser());
        lineCreateTime_txtbox.setText("" + l.getCreateTime());
        dispatchLabelNo_txtbox.setText(l.getDispatchLabelNo());
        //Get Laod Plan Data
        Helper.startSession();
        Query query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_BY_ID);
        query.setParameter("id", l.getLoadPlanId());
        Helper.sess.getTransaction().commit();
        List planList = query.list();
        LoadPlan plan = (LoadPlan) planList.get(0);
        loadPlanId_txtbox.setText("" + plan.getId());
        planCreateTime_txtbox.setText("" + plan.getCreateTime());
        planCreateUser_txtbox.setText(plan.getUser());
        planDispatchTime_txtbox.setText("" + plan.getDeliveryTime());
        planDestination_txtbox.setText("" + l.getDestinationWh());
        planStatus_txtbox.setText(plan.getPlanState());

    }

    private void clearLoadPlanFields() {
        loadPlanId_txtbox.setText("");
        pile_txtbox.setText("");
        planCreateTime_txtbox.setText("");
        position_txtbox.setText("");
        planDispatchTime_txtbox.setText("");
        planDestination_txtbox.setText("");
        planStatus_txtbox.setText("");
        planCreateUser_txtbox.setText("");
        lineCreateUser_txtbox.setText("");
        lineCreateTime_txtbox.setText("");
    }

    /**
     *
     * @param palletNumber
     * @param mode 1 = search by production pallet num, 2 by fors serial number,
     * 3 both
     */
    private void searchForPallet(String palletNumber, String dispatchLabelNo, int mode) {
        msg_lbl.setText("");

        this.clearContainerFieldsValues();
        this.clearLoadPlanFields();
        this.reset_container_table_content();

        if (!palletNumber.trim().equals("") && palletNumber.startsWith(GlobalVars.CLOSING_PALLET_PREFIX)) {
            palletNumber = palletNumber.substring(2);
        }

        System.out.println("palletNumber" + palletNumber);
        //################# Container Data ####################
        //Start transaction                
        Query query = null;
        Helper.startSession();
        switch (mode) {
            case 1:
                //Search by production serial
                System.out.println("search mode 1 " + palletNumber);
                query = Helper.sess.createQuery(HQLHelper.GET_CONTAINER_BY_NUMBER);
                query.setParameter("palletNumber", palletNumber.trim());
                Helper.sess.getTransaction().commit();
                break;
            case 2:
                //Search by fors serial
                System.out.println("search mode 2 " + dispatchLabelNo);
                query = Helper.sess.createQuery(HQLHelper.GET_CONTAINER_BY_FORS_SERIAL);
                query.setParameter("dispatchLabelNo", "%" + dispatchLabelNo.trim() + "%");
                Helper.sess.getTransaction().commit();
                break;
            case 3:
                System.out.println("search mode 3 " + palletNumber + " " + dispatchLabelNo);
                query = Helper.sess.createQuery(HQLHelper.GET_CONTAINER_BY_NUMBER_AND_FORS_SERIAL);
                query.setParameter("palletNumber", palletNumber.trim());
                query.setParameter("dispatchLabelNo", "%" + dispatchLabelNo.trim() + "%");
                Helper.sess.getTransaction().commit();
                break;
            default:
                break;
        }
        @SuppressWarnings("null")
        List result = query.list();
        if (result.isEmpty()) {
            msg_lbl.setText("Num. palette introuvable !");
            this.reset_container_table_content();
            //Show / Hide tools buttons
        } else {
            msg_lbl.setText("");
            System.out.println("Result found " + result.size());
            this.setBaseContainer((BaseContainer) result.get(0));
            this.setContainerFieldsValues(this.bc);
            //################# Harness Data ####################
            Helper.startSession();
            query = Helper.sess.createQuery(HQLHelper.GET_HP_BY_PALLET_NUMBER);
            query.setParameter("palletNumber", this.bc.getPalletNumber());
            Helper.sess.getTransaction().commit();
            result = query.list();

            //reload table data                
            this.reload_container_table_data(result);

            //################# LoadPlan line data ####################
            Helper.startSession();
            query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_LINE_BY_PAL_NUM);
            query.setParameter("palletNumber", this.bc.getPalletNumber());
            Helper.sess.getTransaction().commit();
            result = query.list();
            //Reload LoadPlan line details
            if (!result.isEmpty()) {
                this.reload_load_plan_data(result);
            }
        }

//          else {
//            msg_lbl.setText("Num. palette introuvable !");
//            clearSearchBox();
//        } 
    }

    private void setContainerFieldsValues(BaseContainer bc) {
        palletNumber_txtbox.setText(String.valueOf(bc.getPalletNumber()));
        palletId_txtbox.setText(String.valueOf(bc.getId()));
        user_txtbox.setText(bc.getUser());
        create_user_txtbox.setText(bc.getCreateUser());
        harnessPart_txtbox.setText(bc.getHarnessPart());
        index_txtbox.setText(bc.getHarnessIndex());
        supplierPartNumber_txtbox.setText(bc.getSupplierPartNumber());
        workstation_txtbox.setText(bc.getPackWorkstation());
        qtyExptected_txtbox.setText(String.valueOf(bc.getQtyExpected()));
        qtyRead_txtbox.setText(String.valueOf(bc.getQtyRead()));
        packType_txtbox.setText(bc.getPackType());
        stdTime_txtbox.setText(bc.getStdTime() + "");
        total_stdTime_txtbox.setText("" + (bc.getStdTime() * bc.getQtyRead()));
        if (bc.getSpecial_order() != null && bc.getSpecial_order() == 1) {
            special_order_txtbox.setText("SPECIAL");
        } else {
            special_order_txtbox.setText("");
        }
        state_txtbox.setText(bc.getContainerState());
        startTime_txtbox.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(bc.getStartTime()));
        if (bc.getClosedTime() != null) {
            completeTime_txtbox.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(bc.getClosedTime()));
        }
        workingTime_txtbox.setText(String.valueOf(bc.getWorkTime()));
        comment_txt.setText(bc.getComment());
        project_txtbox.setText(bc.getProject());
        destination_txtbox.setText(bc.getDestination());
        net_weight_txt.setText(bc.getNetWeight() + "");
        gross_weight_txt.setText(bc.getGrossWeight() + "");
        article_desc_txt.setText(bc.getArticleDesc());
        eng_change_date_txt.setText(bc.getEngChangeDate() + "");
        eng_change_txt.setText(bc.getEngChange());
        volume_txt.setText(bc.getVolume() + "");

    }

    public void clearContainerFieldsValues() {
        palletNumber_txtbox.setText("");
        palletId_txtbox.setText("");
        user_txtbox.setText("");
        create_user_txtbox.setText("");
        harnessPart_txtbox.setText("");
        index_txtbox.setText("");
        supplierPartNumber_txtbox.setText("");
        workstation_txtbox.setText("");
        qtyExptected_txtbox.setText("");
        qtyRead_txtbox.setText("");
        packType_txtbox.setText("");
        state_txtbox.setText("");
        startTime_txtbox.setText("");
        completeTime_txtbox.setText("");
        workingTime_txtbox.setText("");
        stdTime_txtbox.setText("");
        total_stdTime_txtbox.setText("");
        volume_txt.setText("");
        gross_weight_txt.setText("");
        net_weight_txt.setText("");
        article_desc_txt.setText("");
        eng_change_date_txt.setText("");
        eng_change_txt.setText("");
        destination_txtbox.setText("");
        project_txtbox.setText("");
    }

    public void setOkText(String newTxt) {
        msg_lbl.setText(newTxt);
    }

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.dropButton.setVisible(false);
            this.printOpenSheetButton.setVisible(false);
            this.printCloseSheetButton.setVisible(false);

            this.dispose();
        }
    }//GEN-LAST:event_formKeyPressed

    private void dropButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dropButtonActionPerformed
        if (!palletId_txtbox.getText().equals("")) {
            PACKAGING_UI9001_DropContainerConfirmation dropConfirm = new PACKAGING_UI9001_DropContainerConfirmation(this, false, this.bc, Integer.valueOf(GlobalVars.APP_PROP.getProperty("PACKAGING_SCAN_MODE")));
            dropConfirm.setVisible(true);
        } else {
            msg_lbl.setText("Please specify existing pallet number !");
        }
    }//GEN-LAST:event_dropButtonActionPerformed

    private void printOpenSheetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printOpenSheetButtonActionPerformed
        msg_lbl.setText("");
        if (!palletNum_txtbox.getText().isEmpty()) {
            Query query = Helper.sess.createQuery(HQLHelper.GET_OPEN_SHEET);
            query.setParameter("id", Integer.valueOf(palletNumber_txtbox.getText()));
            Helper.sess.beginTransaction();
            Helper.sess.getTransaction().commit();
            List result = query.list();
            if (result.size() > 0) {
                HisOpenPalPrint pallet = (HisOpenPalPrint) result.get(0);
                pallet.setWriteTime(new Date());
                pallet.setWriteId(PackagingVars.context.getUser().getId());

                PrinterHelper.saveAndReprintOpenSheet(pallet);
            } else {
                msg_lbl.setText("Num. Palette introuvable dans les palettes ouvertes !");
            }
        } else {
            msg_lbl.setText("Num. Palette introuvable dans les palettes ouvertes !");
        }
    }//GEN-LAST:event_printOpenSheetButtonActionPerformed

    private void printCloseSheetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printCloseSheetButtonActionPerformed
        msg_lbl.setText("");
        if (!palletNum_txtbox.getText().isEmpty()) {
            Query query = Helper.sess.createQuery(HQLHelper.GET_CONTAINER_BY_NUMBER);
            query.setParameter("palletNumber", palletNumber_txtbox.getText());
            Helper.sess.beginTransaction();
            Helper.sess.getTransaction().commit();
            List result = query.list();
            if (result.size() > 0) {
                BaseContainer b = (BaseContainer) result.get(0);
                b.setFifoTime(new Date());
                b.setWriteId(PackagingVars.context.getUser().getId());
                try {
                    switch (PackagingVars.PROJECT.getPackagingMode()) {
                        case "1":
                            PrinterHelper.saveAndPrintClosingSheetMode1(PackagingVars.mode1_context, b, true);
                            break;
                        case "2":
                            PrinterHelper.saveAndPrintClosingSheetMode2(PackagingVars.mode2_context, b, true);
                            break;
                        case "3":
                            PrinterHelper.saveAndPrintClosingSheetMode3(PackagingVars.mode3_context, b, true);
                            break;
                        default:
                            break;
                    }
                } catch (IOException | DocumentException ex) {
                    UILog.severe(ex.toString());
                } catch (Exception ex) {
                    Logger.getLogger(PACKAGING_UI0010_PalletDetails.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                msg_lbl.setText("Num. Palette introuvable dans les palettes fermées !");
            }
        } else {
            msg_lbl.setText("Num. Palette introuvable dans les palettes fermées !");
        }
    }//GEN-LAST:event_printCloseSheetButtonActionPerformed

    private void pack_list_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pack_list_btnActionPerformed
        new WAREHOUSE_FG_UI0002_PALLET_LIST(null, true).setVisible(true);
    }//GEN-LAST:event_pack_list_btnActionPerformed

    private void pile_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pile_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pile_txtboxActionPerformed

    private void lineCreateTime_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineCreateTime_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lineCreateTime_txtboxActionPerformed

    private void lineCreateUser_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineCreateUser_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lineCreateUser_txtboxActionPerformed

    private void planCreateUser_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_planCreateUser_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_planCreateUser_txtboxActionPerformed

    private void planStatus_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_planStatus_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_planStatus_txtboxActionPerformed

    private void planDestination_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_planDestination_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_planDestination_txtboxActionPerformed

    private void planDispatchTime_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_planDispatchTime_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_planDispatchTime_txtboxActionPerformed

    private void planCreateTime_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_planCreateTime_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_planCreateTime_txtboxActionPerformed

    private void loadPlanId_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadPlanId_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_loadPlanId_txtboxActionPerformed

    private void create_user_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_create_user_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_create_user_txtboxActionPerformed

    private void user_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_user_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_user_txtboxActionPerformed

    private void workingTime_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workingTime_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_workingTime_txtboxActionPerformed

    private void completeTime_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_completeTime_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_completeTime_txtboxActionPerformed

    private void startTime_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startTime_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_startTime_txtboxActionPerformed

    private void state_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_state_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_state_txtboxActionPerformed

    private void packType_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_packType_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_packType_txtboxActionPerformed

    private void qtyRead_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_qtyRead_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_qtyRead_txtboxActionPerformed

    private void qtyExptected_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_qtyExptected_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_qtyExptected_txtboxActionPerformed

    private void workstation_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workstation_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_workstation_txtboxActionPerformed

    private void supplierPartNumber_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supplierPartNumber_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_supplierPartNumber_txtboxActionPerformed

    private void index_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_index_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_index_txtboxActionPerformed

    private void harnessPart_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_harnessPart_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_harnessPart_txtboxActionPerformed

    private void palletId_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_palletId_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_palletId_txtboxActionPerformed

    private void clear_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clear_btnActionPerformed

        palletNum_txtbox.setText("");
    }//GEN-LAST:event_clear_btnActionPerformed

    private void continue_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_continue_btnActionPerformed
        BaseContainer b = new BaseContainer().getBaseContainer(palletNumber_txtbox.getText());

        switch (GlobalVars.APP_PROP.getProperty("PACKAGING_SCAN_MODE")) {
            case "1":
                PackagingVars.mode1_context.getBaseContainerTmp().setPalletNumber(b.getPalletNumber());
                //Set requested closing pallet number in the main gui
                PackagingVars.Packaging_Gui_Mode1.setFeedbackTextarea("N° " + GlobalVars.CLOSING_PALLET_PREFIX + b.getPalletNumber());
                //############# PASSE TO S050 STATE ###############
                PackagingVars.Packaging_Gui_Mode1.state = new Mode1_S050_ClosingPallet();
                this.dispose();
                break;
            case "2":
                PackagingVars.mode2_context.getBaseContainerTmp().setPalletNumber(b.getPalletNumber());
                //Set requested closing pallet number in the main gui
                PackagingVars.Packaging_Gui_Mode2.setFeedbackTextarea("N° " + GlobalVars.CLOSING_PALLET_PREFIX + b.getPalletNumber());
                //############# PASSE TO S050 STATE ###############
                PackagingVars.Packaging_Gui_Mode2.state = new Mode2_S040_ClosingPallet();
                this.dispose();
                break;
            case "3":
                PackagingVars.mode3_context.getBaseContainerTmp().setPalletNumber(b.getPalletNumber());
                //Set requested closing pallet number in the main gui
                PackagingVars.Packaging_Gui_Mode3.setFeedbackTextarea("N° " + GlobalVars.CLOSING_PALLET_PREFIX + b.getPalletNumber());
                //############# PASSE TO S050 STATE ###############
                PackagingVars.Packaging_Gui_Mode3.state = new Mode3_S040_ClosingPallet();
                this.dispose();
                break;
            default:
                break;
        }


    }//GEN-LAST:event_continue_btnActionPerformed

    private void history_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_history_btnActionPerformed
        new PACKAGING_UI0014_PalletHistory(null, true, palletNumber_txtbox.getText()).setVisible(true);
    }//GEN-LAST:event_history_btnActionPerformed

    private void palletNumber_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_palletNumber_txtboxActionPerformed

    }//GEN-LAST:event_palletNumber_txtboxActionPerformed

    private void search_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_search_btnActionPerformed
        String dispatch_no = "";
        if (!dispatch_label_no_txtbox.getText().isEmpty()) {
            dispatch_no = (dispatch_label_no_txtbox.getText().startsWith(GlobalVars.APP_PROP.getProperty("DISPATCH_SERIAL_NO_PREFIX"))) ? dispatch_label_no_txtbox.getText().substring(1) : dispatch_label_no_txtbox.getText();
        }
        System.out.println("production_num " + palletNum_txtbox.getText());
        System.out.println("dispatch_label_no_txtbox " + dispatch_label_no_txtbox.getText());
        if (dispatch_label_no_txtbox.getText().isEmpty() && !palletNum_txtbox.getText().isEmpty()) {
            this.searchForPallet(palletNum_txtbox.getText(), "", 1);
        } else if (palletNum_txtbox.getText().isEmpty() && !dispatch_no.isEmpty()) {
            this.searchForPallet("", dispatch_no, 2);
        } else {
            this.searchForPallet(palletNum_txtbox.getText(), dispatch_no, 3);
        }
        this.setTitle(palletNum_txtbox.getText());
    }//GEN-LAST:event_search_btnActionPerformed

    private void palletNum_txtboxKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_palletNum_txtboxKeyTyped
        if (!palletNumber_txtbox.getText().isEmpty()) {
            history_btn.setEnabled(true);
        } else {
            history_btn.setEnabled(false);
            msg_lbl.setText("");
        }
    }//GEN-LAST:event_palletNum_txtboxKeyTyped

    private void palletNum_txtboxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_palletNum_txtboxKeyReleased
        if (!palletNumber_txtbox.getText().isEmpty()) {
            history_btn.setEnabled(true);
        } else {
            history_btn.setEnabled(false);
            msg_lbl.setText("");
        }
    }//GEN-LAST:event_palletNum_txtboxKeyReleased

    private void palletNum_txtboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_palletNum_txtboxKeyPressed
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                if (palletNum_txtbox.getText().startsWith(GlobalVars.CLOSING_PALLET_PREFIX)) {
                    this.searchForPallet(palletNum_txtbox.getText().substring(GlobalVars.CLOSING_PALLET_PREFIX.length()), "", 1);
                } else {
                    this.searchForPallet(palletNum_txtbox.getText(), "", 1);
                }   break;
            case KeyEvent.VK_ESCAPE:
                this.dispose();
                break;
            case KeyEvent.VK_CLEAR:
                this.palletNum_txtbox.setText("");
                this.reset_container_table_content();
                break;
            default:
                if (!palletNumber_txtbox.getText().isEmpty()) {
                    history_btn.setEnabled(true);
                    msg_lbl.setText("");
                } else {
                    history_btn.setEnabled(false);
                }   break;
        }
    }//GEN-LAST:event_palletNum_txtboxKeyPressed

    private void palletNum_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_palletNum_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_palletNum_txtboxActionPerformed

    private void position_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_position_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_position_txtboxActionPerformed

    private void special_order_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_special_order_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_special_order_txtboxActionPerformed

    private void stdTime_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stdTime_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stdTime_txtboxActionPerformed

    private void total_stdTime_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_total_stdTime_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_total_stdTime_txtboxActionPerformed

    private void dispatchLabelNo_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dispatchLabelNo_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dispatchLabelNo_txtboxActionPerformed

    private void dispatch_label_no_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dispatch_label_no_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dispatch_label_no_txtboxActionPerformed

    private void dispatch_label_no_txtboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dispatch_label_no_txtboxKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_dispatch_label_no_txtboxKeyPressed

    private void dispatch_label_no_txtboxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dispatch_label_no_txtboxKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_dispatch_label_no_txtboxKeyReleased

    private void dispatch_label_no_txtboxKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dispatch_label_no_txtboxKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_dispatch_label_no_txtboxKeyTyped

    private void net_weight_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_net_weight_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_net_weight_txtActionPerformed

    private void gross_weight_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gross_weight_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_gross_weight_txtActionPerformed

    private void volume_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_volume_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_volume_txtActionPerformed

    private void eng_change_date_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eng_change_date_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_eng_change_date_txtActionPerformed

    private void project_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_project_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_project_txtboxActionPerformed

    private void destination_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_destination_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_destination_txtboxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea article_desc_txt;
    private javax.swing.JButton clear_btn;
    private javax.swing.JTextArea comment_txt;
    private javax.swing.JTextField completeTime_txtbox;
    private javax.swing.JButton continue_btn;
    private javax.swing.JTextField create_user_txtbox;
    private javax.swing.JTextField destination_txtbox;
    private javax.swing.JPanel details_chargement;
    private javax.swing.JPanel details_chargement_2;
    private javax.swing.JPanel details_pallet_1;
    private javax.swing.JTextField dispatchLabelNo_txtbox;
    private javax.swing.JTextField dispatch_label_no_txtbox;
    private javax.swing.JButton dropButton;
    private javax.swing.JTextField eng_change_date_txt;
    private javax.swing.JTextArea eng_change_txt;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JTextField gross_weight_txt;
    private javax.swing.JTextField harnessPart_txtbox;
    private javax.swing.JButton history_btn;
    private javax.swing.JTextField index_txtbox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTextField lineCreateTime_txtbox;
    private javax.swing.JTextField lineCreateUser_txtbox;
    private javax.swing.JTextField loadPlanId_txtbox;
    private javax.swing.JLabel login_lbl3;
    private javax.swing.JLabel msg_lbl;
    private javax.swing.JTextField net_weight_txt;
    private javax.swing.JTextField packType_txtbox;
    private javax.swing.JButton pack_list_btn;
    private javax.swing.JTextField palletId_txtbox;
    private javax.swing.JTextField palletNum_txtbox;
    private javax.swing.JTextField palletNumber_txtbox;
    private javax.swing.JTextField pile_txtbox;
    private javax.swing.JTextField planCreateTime_txtbox;
    private javax.swing.JTextField planCreateUser_txtbox;
    private javax.swing.JTextField planDestination_txtbox;
    private javax.swing.JTextField planDispatchTime_txtbox;
    private javax.swing.JTextField planStatus_txtbox;
    private javax.swing.JTextField position_txtbox;
    private javax.swing.JButton printCloseSheetButton;
    private javax.swing.JButton printOpenSheetButton;
    private javax.swing.JTextField project_txtbox;
    private javax.swing.JTextField qtyExptected_txtbox;
    private javax.swing.JTextField qtyRead_txtbox;
    private javax.swing.JTable searchResult_table;
    private javax.swing.JButton search_btn;
    private javax.swing.JTextField special_order_txtbox;
    private javax.swing.JTextField startTime_txtbox;
    private javax.swing.JTextField state_txtbox;
    private javax.swing.JTextField stdTime_txtbox;
    private javax.swing.JTextField supplierPartNumber_txtbox;
    private javax.swing.JScrollPane table_scroll;
    private javax.swing.JTextField total_stdTime_txtbox;
    private javax.swing.JTextField user_txtbox;
    private javax.swing.JTextField volume_txt;
    private javax.swing.JTextField workingTime_txtbox;
    private javax.swing.JTextField workstation_txtbox;
    // End of variables declaration//GEN-END:variables

}
