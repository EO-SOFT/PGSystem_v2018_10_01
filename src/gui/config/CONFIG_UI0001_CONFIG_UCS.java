/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.config;

import entity.ConfigProject;
import entity.ConfigSegment;
import entity.ConfigUcs;
import entity.ConfigWarehouse;
import entity.ConfigWorkplace;
import entity.PackagingMaster;
import gui.packaging.PackagingVars;
import helper.ComboItem;
import helper.HQLHelper;
import helper.Helper;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import ui.UILog;
import ui.error.ErrorMsg;

/**
 *
 * @author Administrator
 */
public class CONFIG_UI0001_CONFIG_UCS extends javax.swing.JFrame {

    /**
     * Les méthodes JTable qauxi sauxivent doivent être dans auxne class
     * interface initGauxi() initContainerTableDoauxbleClick load_table_header
     * reset_table_content disableEditingTable refresh
     *
     * Les 4 champs qauxi sauxivent doivent être dans auxne class interface
     */
    Vector<String> ucs_table_header = new Vector<String>();

    List<String> table_header = Arrays.asList(
            "#",
            "CPN",
            "LPN",
            "Order No",
            "Harness Type",
            "Harness Index",
            "Pack type",
            "Pack size",
            "Additional barecode",
            "Std Time",
            "Workstation",
            "Segment",
            "Workplace",
            "Active",
            "Lifes",
            "Special Order",
            "Price"
    );

    Vector ucs_table_data = new Vector();
    public List<Object[]> resultList;
    ConfigUcs aux;

    public CONFIG_UI0001_CONFIG_UCS(java.awt.Frame parent, boolean modal) {
        //super(parent, modal);
        initComponents();
        initGui();
        refresh();
    }

    private void initGui() {
        //Center the this dialog in the screen
        Helper.centerJFrame(this);

        //Desable table edition
        disableEditingTable();
        //Load table header
        load_table_header();

        //Load pack master data
        initPackMasterBox();

        //
        //initSegmentFilter();
        //
        initProjectFilter();
        //
        //Helper.loadProjectsInJbox(harnessType_filter);

        //Support double click on rows in container jtable to display history
        this.initContainerTableDoubleClick();
    }

    private void initPackMasterBox() {
        List result = new PackagingMaster().selectAllMasterPack();
        //Map project data in the list
        for (Object o : result) {
            PackagingMaster pc = (PackagingMaster) o;
            pack_type_filter.addItem(new ComboItem(pc.getPackMaster(), pc.getPackMaster()));
        }

    }

    private void initProjectFilter() {
        List result = new ConfigProject().selectCustomers();
        if (result.isEmpty()) {
            UILog.severeDialog(this, ErrorMsg.APP_ERR0035);
            UILog.severe(ErrorMsg.APP_ERR0035[1]);
        } else { //Map project data in the list
            project_filter.removeAllItems();
            for (Object o : result) {
                project_filter.addItem(new ComboItem(o.toString(), o.toString()));
            }
        }
    }

    private void initHarnessTypeByProject(String project) {
        List result = new ConfigProject().selectHarnessTypeByProject(project);
        if (result.isEmpty()) {
            UILog.severeDialog(this, ErrorMsg.APP_ERR0035);
            UILog.severe(ErrorMsg.APP_ERR0035[1]);
        } else { //Map project data in the list
            harnessType_filter.removeAllItems();
            for (Object o : result) {
                harnessType_filter.addItem(new ComboItem(o.toString(), o.toString()));
            }
        }
    }

    private void setWorkplaceBySegment(String segment) {
        if (segment != null && !segment.isEmpty() && segment != "null") {
            System.out.println("setWorkplaceBySegment " + segment);
            List result = new ConfigWorkplace().selectBySegment(segment);
            if (result.isEmpty()) {
                UILog.severeDialog(this, ErrorMsg.APP_ERR0038);
                UILog.severe(ErrorMsg.APP_ERR0038[1]);
            } else { //Map project data in the list
                workplace_filter.removeAllItems();
                for (Object o : result) {
                    ConfigWorkplace cp = (ConfigWorkplace) o;
                    workplace_filter.addItem(new ComboItem(cp.getWorkplace(), cp.getWorkplace()));
                }
            }
        }
    }
    private void setPacakgingWarehouseByProject(String project) {
        List result = new ConfigWarehouse().selectByProjectAndType(project, "PACKAGING");
        if (result.isEmpty()) {
            UILog.severeDialog(this, ErrorMsg.APP_ERR0042);
            UILog.severe(ErrorMsg.APP_ERR0042[1]);
        } else { //Map project data in the list
            packaging_wh_box.removeAllItems();
            for (Object o : result) {
                ConfigWarehouse cp = (ConfigWarehouse) o;
                packaging_wh_box.addItem(new ComboItem(cp.getWarehouse(), cp.getWarehouse()));
            }
        }
    }
    
    private void setWarehouseByProject(String project) {
        List result = new ConfigWarehouse().selectByProjectAndType(project, "FINISH_GOODS");
        if (result.isEmpty()) {
            UILog.severeDialog(this, ErrorMsg.APP_ERR0036);
            UILog.severe(ErrorMsg.APP_ERR0036[1]);
        } else { //Map project data in the list
            warehouse_filter.removeAllItems();
            for (Object o : result) {
                ConfigWarehouse cp = (ConfigWarehouse) o;
                warehouse_filter.addItem(new ComboItem(cp.getWarehouse(), cp.getWarehouse()));
            }
        }
    }

    private boolean setSegmentByProject(String project) {
        List result = new ConfigSegment().selectBySegment(project);
        if (result.isEmpty()) {
            UILog.severeDialog(this, ErrorMsg.APP_ERR0037);
            UILog.severe(ErrorMsg.APP_ERR0037[1]);
            return false;
        } else { //Map project data in the list
            segment_filter.removeAllItems();
            for (Object o : result) {
                ConfigSegment cp = (ConfigSegment) o;
                segment_filter.addItem(new ComboItem(cp.getSegment(), cp.getSegment()));
            }
            segment_filter.setSelectedIndex(0);
            this.setWorkplaceBySegment(String.valueOf(segment_filter.getSelectedItem()));
            return true;
        }
    }

    private void load_table_header() {
        this.reset_table_content();

        for (Iterator<String> it = table_header.iterator(); it.hasNext();) {
            ucs_table_header.add(it.next());
        }

        ucs_table.setModel(new DefaultTableModel(ucs_table_data, ucs_table_header));
    }

    private void initContainerTableDoubleClick() {
        this.ucs_table.addMouseListener(
                new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Helper.startSession();
                    Query query = Helper.sess.createQuery(HQLHelper.GET_UCS_BY_ID);
                    query.setParameter("id", Integer.valueOf(ucs_table.getValueAt(ucs_table.getSelectedRow(), 0).toString()));
                    Helper.sess.getTransaction().commit();
                    aux = (ConfigUcs) query.list().get(0);
                    id_lbl.setText(aux.getId().toString());
                    create_time_txt.setText(aux.getCreateTime().toString());
                    write_time_txt.setText(aux.getWriteTime().toString());
                    cpn_txtbox.setText(aux.getHarnessPart());
                    lpn_txtbox.setText(aux.getSupplierPartNumber());
                    index_txtbox.setText(aux.getHarnessIndex());
                    engChange_textArea.setText(aux.getEngChange());
                    engChangeDatePicker.setDate(aux.getEngChangeDate());
                    articleDesc_textArea.setText(aux.getArticleDesc());
                    netWeight_txtbox.setText(aux.getNetWeight() + "");
                    grossWeight_txtbox.setText(aux.getGrossWeight() + "");
                    volume_txtbox.setText(aux.getVolume() + "");
                    pack_size_txtbox.setText(aux.getPackSize() + "");
                    assy_txtbox.setText(aux.getAssyWorkstationName());
                    barcodes_nbre_txtbox.setText(aux.getAdditionalBarcode() + "");
                    lifes_txtbox.setText(aux.getLifes() + "");
                    order_no_txt.setText(aux.getOrderNo());
                    comment_txt.setText(aux.getComment());
                    destination_txtbox.setText(aux.getDestination());
                    try {
                        stdTime_txtbox.setText(aux.getStdTime() + "");
                    } catch (Exception ex) {
                        stdTime_txtbox.setText("0.00");
                    }
                    try {
                        price_txtbox.setText(aux.getPrice() + "");
                    } catch (Exception ex) {
                        price_txtbox.setText("0.00");
                    }
                    for (int i = 0; i < pack_type_filter.getItemCount(); i++) {
                        if (pack_type_filter.getItemAt(i).toString().equals(aux.getPackType())) {
                            pack_type_filter.setSelectedIndex(i);
                            break;
                        }
                    }
                    for (int i = 0; i < project_filter.getItemCount(); i++) {
                        if (project_filter.getItemAt(i).toString().equals(aux.getProject())) {
                            project_filter.setSelectedIndex(i);
                            break;
                        }
                    }
                    for (int i = 0; i < warehouse_filter.getItemCount(); i++) {
                        if (warehouse_filter.getItemAt(i).toString().equals(aux.getWarehouse())) {
                            warehouse_filter.setSelectedIndex(i);
                            break;
                        }
                    }
                    for (int i = 0; i < segment_filter.getItemCount(); i++) {
                        if (segment_filter.getItemAt(i).toString().equals(aux.getSegment())) {
                            segment_filter.setSelectedIndex(i);
                            break;
                        }
                    }
                    for (int i = 0; i < workplace_filter.getItemCount(); i++) {
                        if (workplace_filter.getItemAt(i).toString().equals(aux.getWorkplace())) {
                            workplace_filter.setSelectedIndex(i);
                            break;
                        }
                    }
                    for (int i = 0; i < harnessType_filter.getItemCount(); i++) {
                        if (harnessType_filter.getItemAt(i).toString().equals(aux.getHarnessType())) {
                            harnessType_filter.setSelectedIndex(i);
                            break;
                        }
                    }
                    for (int i = 0; i < packaging_wh_box.getItemCount(); i++) {
                        if (packaging_wh_box.getItemAt(i).toString().equals(aux.getPackaging_warehouse())) {
                            packaging_wh_box.setSelectedIndex(i);
                            break;
                        }
                    }

                    if (String.valueOf(aux.getActive()).equals("1")) {
                        active_combobox.setSelectedIndex(0);
                    } else {
                        active_combobox.setSelectedIndex(1);
                    }

                    if (aux.getSpecialOrder() == 1) {
                        special_order_check.setSelected(true);
                    } else {
                        special_order_check.setSelected(false);
                    }

                    try {
                        if (aux.isLabelPerPiece()) {
                            label_per_piece_checkbox.setSelected(true);
                        } else {
                            label_per_piece_checkbox.setSelected(false);
                        }
                    } catch (NullPointerException ex) {
                        label_per_piece_checkbox.setSelected(false);
                    }

                    delete_btn.setEnabled(true);
                    duplicate_btn.setEnabled(true);
                }
            }
        }
        );
    }

    private void reset_table_content() {

        ucs_table_data = new Vector();
        DefaultTableModel dataModel = new DefaultTableModel(ucs_table_data, ucs_table_header);
        ucs_table.setModel(dataModel);
    }

    public void disableEditingTable() {
        for (int c = 0; c < ucs_table.getColumnCount(); c++) {
            Class<?> col_class = ucs_table.getColumnClass(c);
            ucs_table.setDefaultEditor(col_class, null);        // remove editor            
        }
    }

    /**
     * @param ucs_table_data
     * @param ucs_table_header
     * @param ucs_table
     * @todo : reload_table_data a mettre dans une classe interface
     * @param resultList
     */
    public void reload_table_data(List<Object[]> resultList, Vector ucs_table_data, Vector<String> ucs_table_header, JTable ucs_table) {
        this.reset_table_content();
        for (Object[] line : resultList) {
            @SuppressWarnings("UseOfObsoleteCollectionType")
            Vector<Object> oneRow = new Vector<Object>();
            for (Object cell : line) {
                oneRow.add(String.valueOf(cell));
            }
            ucs_table_data.add(oneRow);
        }

        ucs_table.setModel(new DefaultTableModel(ucs_table_data, ucs_table_header));
        ucs_table.setAutoCreateRowSorter(true);
    }

    private void clearFields() {
        id_lbl.setText("");
        create_time_txt.setText("");
        write_time_txt.setText("");
        cpn_txtbox.setText("");
        lpn_txtbox.setText("");
        index_txtbox.setText("");
        active_combobox.setSelectedIndex(0);
        assy_txtbox.setText("");
        lifes_txtbox.setText("-1");
        barcodes_nbre_txtbox.setText("0");
        pack_size_txtbox.setText("");
        stdTime_txtbox.setText("0.00");
        price_txtbox.setText("0.00");
        order_no_txt.setText("");
        delete_btn.setEnabled(false);
        duplicate_btn.setEnabled(false);
        msg_lbl.setText("");
        comment_txt.setText("");
        destination_txtbox.setText("");
        special_order_check.setSelected(false);
        articleDesc_textArea.setText("");
        engChange_textArea.setText("");
        engChangeDatePicker.setDate(null);
        netWeight_txtbox.setText("0.00");
        grossWeight_txtbox.setText("0.00");
        volume_txtbox.setText("0.00");
        label_per_piece_checkbox.setSelected(false);
        aux = null;
    }

    private void clearSearchFields() {
        cpn_txtbox_search.setText("");
        pack_type_txtbox_search.setText("");
        segment_txtbox_search.setText("");
    }

    private void refresh() {
        Helper.startSession();
        String query_str = " SELECT "
                + " u.id AS id, "
                + " u.harness_part AS cpn, "
                + " u.supplier_part_number AS lpn, "
                + " u.order_no AS order_no, "
                + " u.harness_type AS harness_type, "
                + " u.harness_index AS harness_index, "
                + " u.pack_type AS pack_type, "
                + " u.pack_size AS pack_size, "
                + " u.additional_barcode AS barecodes, "
                + " u.std_time AS stdTime, "
                + " u.assy_workstation AS assy_workstation, "
                + " u.segment AS segment, "
                + " u.workplace AS workplace, "
                + " u.active AS active, "
                + " u.lifes AS lifes, "
                + " u.special_order AS special_order,"
                + " u.price "
                + " FROM Config_Ucs u WHERE 1=1 ";

        if (!cpn_txtbox_search.getText().trim().equals("")) {
            query_str += " AND harness_part LIKE '%" + cpn_txtbox_search.getText().trim() + "%'";
        }
        if (!pack_type_txtbox_search.getText().trim().equals("")) {
            query_str += " AND pack_type LIKE '%" + pack_type_txtbox_search.getText().trim() + "%'";
        }
        if (!segment_txtbox_search.getText().trim().equals("")) {
            query_str += " AND segment LIKE '%" + segment_txtbox_search.getText().trim() + "%'";
        }
        if (!supplier_pn_txtbox_search.getText().trim().equals("")) {
            query_str += " AND supplier_part_number LIKE '%" + supplier_pn_txtbox_search.getText().trim() + "%'";
        }
        query_str += " ORDER BY id DESC ";
        SQLQuery query = Helper.sess.createSQLQuery(query_str);
        resultList = query.list();

        Helper.sess.getTransaction().commit();

        this.reload_table_data(resultList, ucs_table_data, ucs_table_header, ucs_table);

        this.disableEditingTable();
    }

    /**
     * This method is called from within the constrauxctor to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        save_btn = new javax.swing.JButton();
        cancel_btn = new javax.swing.JButton();
        delete_btn = new javax.swing.JButton();
        duplicate_btn = new javax.swing.JButton();
        msg_lbl = new javax.swing.JLabel();
        user_list_panel = new javax.swing.JPanel();
        user_table_scroll = new javax.swing.JScrollPane();
        ucs_table = new javax.swing.JTable();
        cpn_txtbox_search = new javax.swing.JTextField();
        fname_lbl_search = new javax.swing.JLabel();
        lname_lbl_search = new javax.swing.JLabel();
        pack_type_txtbox_search = new javax.swing.JTextField();
        llogin_lbl_search = new javax.swing.JLabel();
        segment_txtbox_search = new javax.swing.JTextField();
        filter_btn = new javax.swing.JButton();
        clear_search_btn = new javax.swing.JButton();
        supplier_pn_txtbox_search = new javax.swing.JTextField();
        llogin_lbl_search1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        project_filter = new javax.swing.JComboBox();
        login_lbl3 = new javax.swing.JLabel();
        login_lbl5 = new javax.swing.JLabel();
        warehouse_filter = new javax.swing.JComboBox();
        login_lbl6 = new javax.swing.JLabel();
        destination_txtbox = new javax.swing.JTextField();
        login_lbl = new javax.swing.JLabel();
        segment_filter = new javax.swing.JComboBox();
        pwd_lbl = new javax.swing.JLabel();
        workplace_filter = new javax.swing.JComboBox();
        pwd_lbl7 = new javax.swing.JLabel();
        harnessType_filter = new javax.swing.JComboBox();
        cpn_txtbox = new javax.swing.JTextField();
        fname_lbl = new javax.swing.JLabel();
        lname_lbl = new javax.swing.JLabel();
        lpn_txtbox = new javax.swing.JTextField();
        pwd_lbl1 = new javax.swing.JLabel();
        index_txtbox = new javax.swing.JTextField();
        active_combobox = new javax.swing.JComboBox();
        pwd_lbl2 = new javax.swing.JLabel();
        login_lbl1 = new javax.swing.JLabel();
        assy_txtbox = new javax.swing.JTextField();
        order_no_txt = new javax.swing.JTextField();
        login_lbl4 = new javax.swing.JLabel();
        pwd_lbl6 = new javax.swing.JLabel();
        stdTime_txtbox = new javax.swing.JTextField();
        lname_lbl1 = new javax.swing.JLabel();
        create_time_txt = new javax.swing.JTextField();
        lname_lbl2 = new javax.swing.JLabel();
        write_time_txt = new javax.swing.JTextField();
        packaging_wh_box = new javax.swing.JComboBox();
        fname_lbl14 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        pwd_lbl3 = new javax.swing.JLabel();
        pack_type_filter = new javax.swing.JComboBox();
        login_lbl2 = new javax.swing.JLabel();
        barcodes_nbre_txtbox = new javax.swing.JTextField();
        label_per_piece_checkbox = new javax.swing.JCheckBox();
        pwd_lbl4 = new javax.swing.JLabel();
        pack_size_txtbox = new javax.swing.JTextField();
        pwd_lbl5 = new javax.swing.JLabel();
        lifes_txtbox = new javax.swing.JTextField();
        special_order_check = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        comment_txt = new javax.swing.JTextArea();
        print_2nd_closing_label_checkbox = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        pwd_lbl9 = new javax.swing.JLabel();
        netWeight_txtbox = new javax.swing.JTextField();
        pwd_lbl10 = new javax.swing.JLabel();
        grossWeight_txtbox = new javax.swing.JTextField();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 100), new java.awt.Dimension(0, 100), new java.awt.Dimension(32767, 100));
        pwd_lbl12 = new javax.swing.JLabel();
        volume_txtbox = new javax.swing.JTextField();
        pwd_lbl8 = new javax.swing.JLabel();
        price_txtbox = new javax.swing.JTextField();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 100), new java.awt.Dimension(0, 100), new java.awt.Dimension(32767, 100));
        pwd_lbl13 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        engChange_textArea = new javax.swing.JTextArea();
        pwd_lbl14 = new javax.swing.JLabel();
        engChangeDatePicker = new org.jdesktop.swingx.JXDatePicker();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 100), new java.awt.Dimension(0, 100), new java.awt.Dimension(32767, 100));
        pwd_lbl11 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        articleDesc_textArea = new javax.swing.JTextArea();
        fname_lbl1 = new javax.swing.JLabel();
        id_lbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Standard Pack Master Data");
        setName("Configuration packaging par référence"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Standard Pack Master Data", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 1, 14))); // NOI18N
        jPanel1.setToolTipText("Standard Pack Master Data");
        jPanel1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel1.setName("Standard Pack Master Data"); // NOI18N

        save_btn.setText("Save");
        save_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_btnActionPerformed(evt);
            }
        });

        cancel_btn.setText("Clear");
        cancel_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_btnActionPerformed(evt);
            }
        });

        delete_btn.setText("Delete");
        delete_btn.setEnabled(false);
        delete_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_btnActionPerformed(evt);
            }
        });

        duplicate_btn.setText("Dupliquer");
        duplicate_btn.setEnabled(false);
        duplicate_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                duplicate_btnActionPerformed(evt);
            }
        });

        msg_lbl.setBackground(new java.awt.Color(255, 255, 255));
        msg_lbl.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        msg_lbl.setForeground(new java.awt.Color(0, 0, 255));

        user_list_panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "UCS list", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 1, 14))); // NOI18N
        user_list_panel.setToolTipText("");

        ucs_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        user_table_scroll.setViewportView(ucs_table);

        cpn_txtbox_search.setName("fname_txtbox"); // NOI18N
        cpn_txtbox_search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cpn_txtbox_searchKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                cpn_txtbox_searchKeyTyped(evt);
            }
        });

        fname_lbl_search.setText("CPN");

        lname_lbl_search.setText("Pack Type");

        pack_type_txtbox_search.setName("fname_txtbox"); // NOI18N
        pack_type_txtbox_search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pack_type_txtbox_searchKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                pack_type_txtbox_searchKeyTyped(evt);
            }
        });

        llogin_lbl_search.setText("Segment");

        segment_txtbox_search.setName("fname_txtbox"); // NOI18N
        segment_txtbox_search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                segment_txtbox_searchKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                segment_txtbox_searchKeyTyped(evt);
            }
        });

        filter_btn.setText("Filter");
        filter_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filter_btnActionPerformed(evt);
            }
        });

        clear_search_btn.setText("Clear filters");
        clear_search_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clear_search_btnActionPerformed(evt);
            }
        });

        supplier_pn_txtbox_search.setName("fname_txtbox"); // NOI18N
        supplier_pn_txtbox_search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                supplier_pn_txtbox_searchKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                supplier_pn_txtbox_searchKeyTyped(evt);
            }
        });

        llogin_lbl_search1.setText("LEONI PN");

        javax.swing.GroupLayout user_list_panelLayout = new javax.swing.GroupLayout(user_list_panel);
        user_list_panel.setLayout(user_list_panelLayout);
        user_list_panelLayout.setHorizontalGroup(
            user_list_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(user_list_panelLayout.createSequentialGroup()
                .addGroup(user_list_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(user_table_scroll)
                    .addGroup(user_list_panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(fname_lbl_search)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(user_list_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(user_list_panelLayout.createSequentialGroup()
                                .addComponent(filter_btn)
                                .addGap(18, 18, 18)
                                .addComponent(clear_search_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(user_list_panelLayout.createSequentialGroup()
                                .addComponent(cpn_txtbox_search, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lname_lbl_search)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pack_type_txtbox_search, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(llogin_lbl_search)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(segment_txtbox_search, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(llogin_lbl_search1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(supplier_pn_txtbox_search, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        user_list_panelLayout.setVerticalGroup(
            user_list_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, user_list_panelLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(user_list_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(user_list_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(llogin_lbl_search1)
                        .addComponent(supplier_pn_txtbox_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(user_list_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cpn_txtbox_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(fname_lbl_search)
                        .addComponent(lname_lbl_search)
                        .addComponent(pack_type_txtbox_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(llogin_lbl_search)
                        .addComponent(segment_txtbox_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(user_list_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filter_btn)
                    .addComponent(clear_search_btn))
                .addGap(18, 18, 18)
                .addComponent(user_table_scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE))
        );

        project_filter.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                project_filterItemStateChanged(evt);
            }
        });
        project_filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                project_filterActionPerformed(evt);
            }
        });

        login_lbl3.setText("Project *");

        login_lbl5.setText("F.G Warehouse");

        warehouse_filter.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                warehouse_filterItemStateChanged(evt);
            }
        });
        warehouse_filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                warehouse_filterActionPerformed(evt);
            }
        });

        login_lbl6.setText("Final Destination");

        destination_txtbox.setName("fname_txtbox"); // NOI18N

        login_lbl.setText("Segment *");

        segment_filter.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                segment_filterItemStateChanged(evt);
            }
        });
        segment_filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                segment_filterActionPerformed(evt);
            }
        });

        pwd_lbl.setText("Workplace *");

        workplace_filter.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                workplace_filterItemStateChanged(evt);
            }
        });
        workplace_filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                workplace_filterActionPerformed(evt);
            }
        });

        pwd_lbl7.setText("Harness Type *");

        cpn_txtbox.setName("cpn_txtbox"); // NOI18N

        fname_lbl.setText("CPN *");

        lname_lbl.setText("LPN *");

        lpn_txtbox.setName("fname_txtbox"); // NOI18N

        pwd_lbl1.setText("Index *");

        index_txtbox.setName("fname_txtbox"); // NOI18N

        active_combobox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "0" }));
        active_combobox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                active_comboboxActionPerformed(evt);
            }
        });

        pwd_lbl2.setText("Active *");

        login_lbl1.setText("Assembly Workstation");

        assy_txtbox.setName("fname_txtbox"); // NOI18N

        order_no_txt.setName("fname_txtbox"); // NOI18N

        login_lbl4.setText("Order No *");

        pwd_lbl6.setText("Std Time *");

        stdTime_txtbox.setText("0.00");
        stdTime_txtbox.setName("fname_txtbox"); // NOI18N
        stdTime_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stdTime_txtboxActionPerformed(evt);
            }
        });

        lname_lbl1.setText("Creation Date");

        create_time_txt.setEditable(false);
        create_time_txt.setName("fname_txtbox"); // NOI18N

        lname_lbl2.setText("Write Date");

        write_time_txt.setEditable(false);
        write_time_txt.setName("fname_txtbox"); // NOI18N

        packaging_wh_box.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        fname_lbl14.setText("Packaging Wh");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(login_lbl6)
                            .addComponent(login_lbl3)
                            .addComponent(login_lbl5)
                            .addComponent(fname_lbl14)))
                    .addComponent(lname_lbl1)
                    .addComponent(login_lbl1))
                .addGap(36, 36, 36)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(packaging_wh_box, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(create_time_txt, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(warehouse_filter, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(destination_txtbox, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(project_filter, javax.swing.GroupLayout.Alignment.LEADING, 0, 176, Short.MAX_VALUE))
                    .addComponent(assy_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lname_lbl2)
                    .addComponent(pwd_lbl6)
                    .addComponent(pwd_lbl7)
                    .addComponent(pwd_lbl)
                    .addComponent(login_lbl)
                    .addComponent(pwd_lbl2))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(active_combobox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(stdTime_txtbox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                    .addComponent(harnessType_filter, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(segment_filter, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(workplace_filter, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(write_time_txt))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fname_lbl)
                            .addComponent(lname_lbl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cpn_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lpn_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(login_lbl4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(order_no_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(pwd_lbl1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(index_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(446, 446, 446))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(active_combobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pwd_lbl2))
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(project_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(login_lbl3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(warehouse_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(login_lbl5)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(segment_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(login_lbl)
                            .addComponent(fname_lbl)
                            .addComponent(cpn_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(workplace_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pwd_lbl)
                            .addComponent(lname_lbl)
                            .addComponent(lpn_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(pwd_lbl7)
                            .addComponent(harnessType_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(index_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(pwd_lbl1)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(packaging_wh_box, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fname_lbl14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(login_lbl6)
                            .addComponent(destination_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(pwd_lbl6)
                        .addComponent(stdTime_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(order_no_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(login_lbl4))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(assy_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(login_lbl1)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(create_time_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lname_lbl1)
                        .addComponent(lname_lbl2))
                    .addComponent(write_time_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(45, 45, 45))
        );

        jTabbedPane1.addTab("General", jPanel2);

        pwd_lbl3.setText("Pack Type *");

        login_lbl2.setText("Barcodes Nbre *");

        barcodes_nbre_txtbox.setText("0");
        barcodes_nbre_txtbox.setName("fname_txtbox"); // NOI18N

        label_per_piece_checkbox.setText("Print label for each piece ?");
        label_per_piece_checkbox.setToolTipText("Print an A5 label for each scanned piece.\nThis label is different from open and closing sheet labels, If set to true, it will be printed once the user scan the QR code of a harness.");
        label_per_piece_checkbox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                label_per_piece_checkboxStateChanged(evt);
            }
        });

        pwd_lbl4.setText("Std Pack Qty  *");

        pack_size_txtbox.setText("1");
        pack_size_txtbox.setName("fname_txtbox"); // NOI18N

        pwd_lbl5.setText("Nbre Packs *");

        lifes_txtbox.setText("-1");
        lifes_txtbox.setName("fname_txtbox"); // NOI18N

        special_order_check.setText("Special Order");
        special_order_check.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                special_order_checkStateChanged(evt);
            }
        });

        comment_txt.setColumns(10);
        comment_txt.setRows(2);
        comment_txt.setText("Comment for this order...");
        comment_txt.setToolTipText("Special order comment or short description");
        comment_txt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                comment_txtFocusGained(evt);
            }
        });
        jScrollPane1.setViewportView(comment_txt);

        print_2nd_closing_label_checkbox.setText("Print 2nd Closing Sheet ?");
        print_2nd_closing_label_checkbox.setToolTipText("Print an A5 label for each scanned piece.\nThis label is different from open and closing sheet labels, If set to true, it will be printed once the user scan the QR code of a harness.");
        print_2nd_closing_label_checkbox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                print_2nd_closing_label_checkboxStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addComponent(special_order_check)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 751, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pwd_lbl3, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(login_lbl2))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(pack_type_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(barcodes_nbre_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(pwd_lbl4)
                                .addGap(26, 26, 26)
                                .addComponent(pack_size_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(label_per_piece_checkbox))
                        .addGap(33, 33, 33)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(pwd_lbl5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lifes_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(print_2nd_closing_label_checkbox))
                        .addGap(221, 221, 221)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pwd_lbl3)
                    .addComponent(pack_type_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pwd_lbl4)
                    .addComponent(pack_size_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pwd_lbl5)
                    .addComponent(lifes_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(login_lbl2)
                    .addComponent(barcodes_nbre_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_per_piece_checkbox)
                    .addComponent(print_2nd_closing_label_checkbox))
                .addGap(26, 26, 26)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(special_order_check)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(138, Short.MAX_VALUE))
        );

        jScrollPane4.setViewportView(jPanel4);
        jPanel4.getAccessibleContext().setAccessibleParent(jTabbedPane1);

        jTabbedPane1.addTab("Packaging", jScrollPane4);

        pwd_lbl9.setText("Net Weight (kg)");

        netWeight_txtbox.setText("0.00");
        netWeight_txtbox.setName("fname_txtbox"); // NOI18N
        netWeight_txtbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                netWeight_txtboxActionPerformed(evt);
            }
        });

        pwd_lbl10.setText("Gross Weight (kg)");

        grossWeight_txtbox.setText("0.00");
        grossWeight_txtbox.setName("fname_txtbox"); // NOI18N

        filler1.setBackground(new java.awt.Color(255, 255, 255));
        filler1.setOpaque(true);

        pwd_lbl12.setText("Volume (m3)");

        volume_txtbox.setText("0.00");
        volume_txtbox.setName("fname_txtbox"); // NOI18N

        pwd_lbl8.setText("Price *");

        price_txtbox.setText("0.00");
        price_txtbox.setName("fname_txtbox"); // NOI18N

        filler2.setBackground(new java.awt.Color(255, 255, 255));
        filler2.setOpaque(true);

        pwd_lbl13.setText("Engineering Change");

        engChange_textArea.setColumns(20);
        engChange_textArea.setRows(5);
        engChange_textArea.setText("-");
        jScrollPane3.setViewportView(engChange_textArea);

        pwd_lbl14.setText("Change Date");

        filler3.setBackground(new java.awt.Color(255, 255, 255));
        filler3.setOpaque(true);

        pwd_lbl11.setText("Article Description");

        articleDesc_textArea.setColumns(20);
        articleDesc_textArea.setRows(5);
        articleDesc_textArea.setText("-");
        jScrollPane2.setViewportView(articleDesc_textArea);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pwd_lbl10)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(pwd_lbl9))
                            .addComponent(pwd_lbl12, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(pwd_lbl8, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(23, 23, 23))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(pwd_lbl11)
                        .addGap(18, 18, 18)))
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, 1, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(netWeight_txtbox)
                    .addComponent(grossWeight_txtbox)
                    .addComponent(volume_txtbox)
                    .addComponent(price_txtbox)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(pwd_lbl14)
                        .addGap(56, 56, 56))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(pwd_lbl13)
                        .addGap(18, 18, 18)))
                .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(engChangeDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(83, 83, 83))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(filler2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filler1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(pwd_lbl9)
                            .addComponent(netWeight_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(pwd_lbl10)
                            .addComponent(grossWeight_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pwd_lbl12)
                            .addComponent(volume_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(pwd_lbl8))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(price_txtbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pwd_lbl11)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pwd_lbl14)
                    .addComponent(engChangeDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pwd_lbl13)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(filler3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Enigeering settings", jPanel3);

        fname_lbl1.setText("ID");

        id_lbl.setBackground(new java.awt.Color(153, 204, 255));
        id_lbl.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        id_lbl.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        id_lbl.setRequestFocusEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(user_list_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(save_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(duplicate_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancel_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(57, 57, 57)
                        .addComponent(delete_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(1126, Short.MAX_VALUE))
                    .addComponent(msg_lbl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 951, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(fname_lbl1)
                                .addGap(18, 18, 18)
                                .addComponent(id_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(604, 604, 604)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(msg_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(fname_lbl1)
                    .addComponent(id_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cancel_btn)
                        .addComponent(delete_btn))
                    .addComponent(duplicate_btn)
                    .addComponent(save_btn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(user_list_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 219, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void netWeight_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_netWeight_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_netWeight_txtboxActionPerformed

    private void label_per_piece_checkboxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_label_per_piece_checkboxStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_label_per_piece_checkboxStateChanged

    private void special_order_checkStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_special_order_checkStateChanged

    }//GEN-LAST:event_special_order_checkStateChanged

    private void supplier_pn_txtbox_searchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_supplier_pn_txtbox_searchKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_supplier_pn_txtbox_searchKeyTyped

    private void supplier_pn_txtbox_searchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_supplier_pn_txtbox_searchKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_supplier_pn_txtbox_searchKeyPressed

    private void clear_search_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clear_search_btnActionPerformed
        clearSearchFields();
    }//GEN-LAST:event_clear_search_btnActionPerformed

    private void filter_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filter_btnActionPerformed
        refresh();
    }//GEN-LAST:event_filter_btnActionPerformed

    private void segment_txtbox_searchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_segment_txtbox_searchKeyTyped
        refresh();
    }//GEN-LAST:event_segment_txtbox_searchKeyTyped

    private void segment_txtbox_searchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_segment_txtbox_searchKeyPressed

    }//GEN-LAST:event_segment_txtbox_searchKeyPressed

    private void pack_type_txtbox_searchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pack_type_txtbox_searchKeyTyped
        refresh();
    }//GEN-LAST:event_pack_type_txtbox_searchKeyTyped

    private void pack_type_txtbox_searchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pack_type_txtbox_searchKeyPressed

    }//GEN-LAST:event_pack_type_txtbox_searchKeyPressed

    private void cpn_txtbox_searchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cpn_txtbox_searchKeyTyped
        refresh();
    }//GEN-LAST:event_cpn_txtbox_searchKeyTyped

    private void cpn_txtbox_searchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cpn_txtbox_searchKeyPressed

    }//GEN-LAST:event_cpn_txtbox_searchKeyPressed

    private void duplicate_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_duplicate_btnActionPerformed
        id_lbl.setText("");
        aux.setHarnessPart(cpn_txtbox.getText());
        aux.setSupplierPartNumber(lpn_txtbox.getText());
        aux.setHarnessIndex(index_txtbox.getText());
        aux.setStdTime(Double.valueOf(stdTime_txtbox.getText()));
        aux.setPrice(Double.valueOf(price_txtbox.getText()));
        aux.setPackSize(Integer.valueOf(pack_size_txtbox.getText()));
        aux.setComment(comment_txt.getText());
        aux.setOrderNo(order_no_txt.getText());
        assy_txtbox.setText(aux.getAssyWorkstationName());
        barcodes_nbre_txtbox.setText(aux.getAdditionalBarcode() + "");
        lifes_txtbox.setText(aux.getLifes() + "");
        aux.setProject(project_filter.getSelectedItem().toString());
        aux.setWarehouse(warehouse_filter.getSelectedItem().toString());
        aux.setPackaging_warehouse(packaging_wh_box.getSelectedItem().toString());
        aux.setArticleDesc(articleDesc_textArea.getText());
        aux.setEngChange(engChange_textArea.getText());
        aux.setEngChangeDate(engChangeDatePicker.getDate());
        aux.setNetWeight(Double.valueOf(netWeight_txtbox.getText().trim()));
        aux.setGrossWeight(Double.valueOf(grossWeight_txtbox.getText().trim()));
        aux.setVolume(Double.valueOf(volume_txtbox.getText().trim()));
        for (int i = 0; i < pack_type_filter.getItemCount(); i++) {
            if (pack_type_filter.getItemAt(i).toString().equals(aux.getPackType())) {
                pack_type_filter.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < segment_filter.getItemCount(); i++) {
            if (segment_filter.getItemAt(i).toString().equals(aux.getSegment())) {
                segment_filter.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < workplace_filter.getItemCount(); i++) {
            if (workplace_filter.getItemAt(i).toString().equals(aux.getWorkplace())) {
                workplace_filter.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < harnessType_filter.getItemCount(); i++) {
            if (harnessType_filter.getItemAt(i).toString().equals(aux.getHarnessType())) {
                harnessType_filter.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < packaging_wh_box.getItemCount(); i++) {
            if (packaging_wh_box.getItemAt(i).toString().equals(aux.getPackaging_warehouse())) {
                packaging_wh_box.setSelectedIndex(i);
                break;
            }
        }

        if (aux.getSpecialOrder() == 1) {
            special_order_check.setSelected(true);
        } else {
            special_order_check.setSelected(false);
        }
        try {
            if (aux.isLabelPerPiece()) {
                label_per_piece_checkbox.setSelected(true);
            } else {
                label_per_piece_checkbox.setSelected(false);
            }
        } catch (NullPointerException ex) {
            label_per_piece_checkbox.setSelected(false);
        }

        if (String.valueOf(aux.getActive()).equals("1")) {
            active_combobox.setSelectedIndex(0);
        } else {
            active_combobox.setSelectedIndex(1);
        }
        this.aux = null;
        msg_lbl.setText("Element dupliqué !");
    }//GEN-LAST:event_duplicate_btnActionPerformed

    private void delete_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_btnActionPerformed
        int confirmed = JOptionPane.showConfirmDialog(this,
                String.format("Confirmez-vous la suppression de cet élement [%s] ?",
                        this.aux.getId()),
                "Suppression UCS",
                JOptionPane.WARNING_MESSAGE);

        if (confirmed == 0) {
            aux.delete(aux);
            clearFields();
            msg_lbl.setText("Elément supprimé !");
            refresh();
        }
    }//GEN-LAST:event_delete_btnActionPerformed

    private void cancel_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_btnActionPerformed
        clearFields();
    }//GEN-LAST:event_cancel_btnActionPerformed

    private void save_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_btnActionPerformed

        if (id_lbl.getText().isEmpty()) { // ID Label is empty, then is a new Object
            ConfigUcs mu = new ConfigUcs();
            boolean err = false;

            if (cpn_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid CPN.", "CPN Error");
                err = true;
                cpn_txtbox.requestFocus();
                cpn_txtbox.setBackground(Color.red);
            } else {
                mu.setHarnessPart(cpn_txtbox.getText().trim());
                cpn_txtbox.setBackground(Color.white);
            }
            if (lpn_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid LPN.", "LPN Error");
                err = true;
                lpn_txtbox.requestFocus();
                lpn_txtbox.setBackground(Color.red);
            } else {
                mu.setSupplierPartNumber(lpn_txtbox.getText().trim());
                lpn_txtbox.setBackground(Color.white);
            }

            if (index_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid index.", "Index Error");
                err = true;
                index_txtbox.requestFocus();
                index_txtbox.setBackground(Color.red);
            } else {
                mu.setHarnessIndex(index_txtbox.getText().trim());
                index_txtbox.setBackground(Color.white);
            }

            if (pack_type_filter.getSelectedItem().toString().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid pack type.", "Pack type Error");
                err = true;
                pack_type_filter.requestFocus();
                pack_type_filter.setBackground(Color.red);
            } else {
                mu.setPackType(pack_type_filter.getSelectedItem().toString());
                pack_type_filter.setBackground(Color.white);
            }
            if (pack_size_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid pack size.", "Pack size Error");
                err = true;
                pack_size_txtbox.requestFocus();
                pack_size_txtbox.setBackground(Color.red);
            } else {
                try {
                    mu.setPackSize(Integer.valueOf(pack_size_txtbox.getText().trim()));
                    pack_size_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for pack size.", "Number format error.");
                    err = true;
                    pack_size_txtbox.requestFocus();
                    pack_size_txtbox.setBackground(Color.red);
                }
            }
            if (segment_filter.getSelectedItem().toString().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid segment.", "Segment Error");
                err = true;
                segment_filter.requestFocus();
                segment_filter.setBackground(Color.red);
            } else {
                mu.setSegment(segment_filter.getSelectedItem().toString());
                segment_filter.setBackground(Color.white);
            }
            if (workplace_filter.getSelectedItem().toString().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid workplace.", "Workplace Error");
                err = true;
                workplace_filter.requestFocus();
                workplace_filter.setBackground(Color.red);
            } else {
                mu.setWorkplace(workplace_filter.getSelectedItem().toString());
                workplace_filter.setBackground(Color.white);
            }
            if (harnessType_filter.getSelectedItem().toString().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid harness type.", "Harness type Error");
                err = true;
                harnessType_filter.requestFocus();
                harnessType_filter.setBackground(Color.red);
            } else {
                mu.setHarnessType(harnessType_filter.getSelectedItem().toString());
                harnessType_filter.setBackground(Color.white);
            }
            if (lifes_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Number of pack cannot be null.", "Number of packs Error");
                err = true;
                lifes_txtbox.requestFocus();
                lifes_txtbox.setBackground(Color.red);

            } else {
                try {
                    mu.setLifes(Integer.valueOf(lifes_txtbox.getText().trim()));
                    lifes_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for number of packs.", "Number format error.");
                    err = true;
                    lifes_txtbox.requestFocus();
                    lifes_txtbox.setBackground(Color.red);
                }
            }
            if (stdTime_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Standard time cannot be null.", "Standard time Error");
                err = true;
                stdTime_txtbox.requestFocus();
                stdTime_txtbox.setBackground(Color.red);
            } else {
                try {
                    mu.setStdTime(Double.valueOf(stdTime_txtbox.getText().trim()));
                    stdTime_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for standard time.", "Number format error.");
                    err = true;
                    stdTime_txtbox.requestFocus();
                    stdTime_txtbox.setBackground(Color.red);
                }
            }
            if (price_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Price cannot be null.", "Price Error");
                err = true;
                price_txtbox.requestFocus();
                price_txtbox.setBackground(Color.red);
            } else {
                try {
                    mu.setPrice(Double.valueOf(price_txtbox.getText().trim()));
                    price_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for price.", "Number format error.");
                    err = true;
                    price_txtbox.requestFocus();
                    price_txtbox.setBackground(Color.red);
                }
            }
            if (netWeight_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Net weight cannot be null.", "Net weight Error");
                err = true;
                netWeight_txtbox.requestFocus();
                netWeight_txtbox.setBackground(Color.red);
            } else {
                try {
                    mu.setNetWeight(Double.valueOf(netWeight_txtbox.getText().trim()));
                    netWeight_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for net weight.", "Number format error.");
                    err = true;
                    netWeight_txtbox.requestFocus();
                    netWeight_txtbox.setBackground(Color.red);
                }
            }
            if (barcodes_nbre_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Additional barcodes number cannot be null.", "Barecode number Error");
                err = true;
                barcodes_nbre_txtbox.requestFocus();
                barcodes_nbre_txtbox.setBackground(Color.red);
            } else {
                try {
                    mu.setAdditionalBarcode(Integer.valueOf(barcodes_nbre_txtbox.getText().trim()));
                    barcodes_nbre_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for additional barecodes number.", "Number format error.");
                    err = true;
                    barcodes_nbre_txtbox.requestFocus();
                    barcodes_nbre_txtbox.setBackground(Color.red);
                }
            }

            try {
                if (engChangeDatePicker.getDate().toString().isEmpty()) {
                    UILog.severeDialog(this, "Empty date error for engineering change date.", "Date format error.");
                    err = true;
                    engChangeDatePicker.requestFocus();
                    engChangeDatePicker.setBackground(Color.red);
                } else {
                    mu.setEngChangeDate(engChangeDatePicker.getDate());
                    engChangeDatePicker.setBackground(Color.white);
                }
            } catch (NullPointerException | NumberFormatException ex) {
                UILog.severeDialog(this, "Date format error for engineering change date.", "Date format error.");
                err = true;
                engChangeDatePicker.requestFocus();
                engChangeDatePicker.setBackground(Color.red);
            }
            mu.setCreateId(PackagingVars.context.getUser().getId());
            mu.setWriteId(PackagingVars.context.getUser().getId());
            mu.setCreateTime(new Date());
            mu.setWriteTime(new Date());
            mu.setActive(Integer.valueOf(active_combobox.getSelectedItem().toString()));
            mu.setProject(project_filter.getSelectedItem().toString());
            mu.setWarehouse(warehouse_filter.getSelectedItem().toString());
            mu.setPackaging_warehouse(packaging_wh_box.getSelectedItem().toString());
            mu.setDestination(destination_txtbox.getText());
            mu.setArticleDesc((articleDesc_textArea.getText().length() > 25) ? articleDesc_textArea.getText().substring(0, 25) : articleDesc_textArea.getText());
            mu.setEngChange((engChange_textArea.getText().length() > 25) ? engChange_textArea.getText().substring(0, 25) : engChange_textArea.getText());
            mu.setGrossWeight(Double.valueOf(grossWeight_txtbox.getText().trim()));
            mu.setVolume(Double.valueOf(volume_txtbox.getText().trim()));

            if (assy_txtbox.getText().isEmpty()) {
                mu.setAssyWorkstationName("-");
            } else {
                mu.setAssyWorkstationName(assy_txtbox.getText().trim());
            }
            mu.setComment(comment_txt.getText());
            if (order_no_txt.getText().isEmpty()) {
                mu.setOrderNo("-");
            } else {
                mu.setOrderNo(order_no_txt.getText());
            }
            if (special_order_check.isSelected()) {
                mu.setSpecialOrder(1);
            } else {
                mu.setSpecialOrder(0);
            }
            if (label_per_piece_checkbox.isSelected()) {
                mu.setLabelPerPiece(true);
            } else {
                mu.setLabelPerPiece(false);
            }
            System.out.println("FORM validation : " + err);
            System.out.println("New object " + mu.toString());
            if (!err) {
                int newId = mu.create(mu);
                String[] msg = {"Nouveau élément "+newId+" enregistré !"};
                clearFields();
                msg_lbl.setText(msg[0]);
                UILog.infoDialog(this, msg);
                refresh();
            }
        } else { // ID Label is filed, then is an update

            boolean err = false;

            if (cpn_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid CPN.", "CPN Error");
                err = true;
                cpn_txtbox.requestFocus();
                cpn_txtbox.setBackground(Color.red);
            } else {
                aux.setHarnessPart(cpn_txtbox.getText().trim());
                cpn_txtbox.setBackground(Color.white);
            }
            if (lpn_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid LPN.", "LPN Error");
                err = true;
                lpn_txtbox.requestFocus();
                lpn_txtbox.setBackground(Color.red);
            } else {
                aux.setSupplierPartNumber(lpn_txtbox.getText().trim());
                lpn_txtbox.setBackground(Color.white);
            }

            if (index_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid index.", "Index Error");
                err = true;
                index_txtbox.requestFocus();
                index_txtbox.setBackground(Color.red);
            } else {
                aux.setHarnessIndex(index_txtbox.getText().trim());
                index_txtbox.setBackground(Color.white);
            }

            if (pack_type_filter.getSelectedItem().toString().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid pack type.", "Pack type Error");
                err = true;
                pack_type_filter.requestFocus();
                pack_type_filter.setBackground(Color.red);
            } else {
                aux.setPackType(pack_type_filter.getSelectedItem().toString());
                pack_type_filter.setBackground(Color.white);
            }
            if (pack_size_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid pack size.", "Pack size Error");
                err = true;
                pack_size_txtbox.requestFocus();
                pack_size_txtbox.setBackground(Color.red);
            } else {
                try {
                    aux.setPackSize(Integer.valueOf(pack_size_txtbox.getText().trim()));
                    pack_size_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for pack size.", "Number format error.");
                    err = true;
                    pack_size_txtbox.requestFocus();
                    pack_size_txtbox.setBackground(Color.red);
                }
            }
            if (segment_filter.getSelectedItem().toString().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid segment.", "Segment Error");
                err = true;
                segment_filter.requestFocus();
                segment_filter.setBackground(Color.red);
            } else {
                aux.setSegment(segment_filter.getSelectedItem().toString());
                segment_filter.setBackground(Color.white);
            }
            if (workplace_filter.getSelectedItem().toString().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid workplace.", "Workplace Error");
                err = true;
                workplace_filter.requestFocus();
                workplace_filter.setBackground(Color.red);
            } else {
                aux.setWorkplace(workplace_filter.getSelectedItem().toString());
                workplace_filter.setBackground(Color.white);
            }
            if (harnessType_filter.getSelectedItem().toString().isEmpty()) {
                UILog.severeDialog(this, "Empty or invalid harness type.", "Harness type Error");
                err = true;
                harnessType_filter.requestFocus();
                harnessType_filter.setBackground(Color.red);
            } else {
                aux.setHarnessType(harnessType_filter.getSelectedItem().toString());
                harnessType_filter.setBackground(Color.white);
            }
            if (lifes_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Number of pack cannot be null.", "Number of packs Error");
                err = true;
                lifes_txtbox.requestFocus();
                lifes_txtbox.setBackground(Color.red);

            } else {
                try {
                    aux.setLifes(Integer.valueOf(lifes_txtbox.getText().trim()));
                    lifes_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for number of packs.", "Number format error.");
                    err = true;
                    lifes_txtbox.requestFocus();
                    lifes_txtbox.setBackground(Color.red);
                }
            }
            if (stdTime_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Standard time cannot be null.", "Standard time Error");
                err = true;
                stdTime_txtbox.requestFocus();
                stdTime_txtbox.setBackground(Color.red);
            } else {
                try {
                    aux.setStdTime(Double.valueOf(stdTime_txtbox.getText().trim()));
                    stdTime_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for standard time.", "Number format error.");
                    err = true;
                    stdTime_txtbox.requestFocus();
                    stdTime_txtbox.setBackground(Color.red);
                }
            }
            if (price_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Price cannot be null.", "Price Error");
                err = true;
                price_txtbox.requestFocus();
                price_txtbox.setBackground(Color.red);
            } else {
                try {
                    aux.setPrice(Double.valueOf(price_txtbox.getText().trim()));
                    price_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for price.", "Number format error.");
                    err = true;
                    price_txtbox.requestFocus();
                    price_txtbox.setBackground(Color.red);
                }
            }
            if (netWeight_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Net weight cannot be null.", "Net weight Error");
                err = true;
                netWeight_txtbox.requestFocus();
                netWeight_txtbox.setBackground(Color.red);
            } else {
                try {
                    aux.setNetWeight(Double.valueOf(netWeight_txtbox.getText().trim()));
                    netWeight_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for net weight.", "Number format error.");
                    err = true;
                    netWeight_txtbox.requestFocus();
                    netWeight_txtbox.setBackground(Color.red);
                }
            }
            if (barcodes_nbre_txtbox.getText().trim().isEmpty()) {
                UILog.severeDialog(this, "Additional barcodes number cannot be null.", "Barecode number Error");
                err = true;
                barcodes_nbre_txtbox.requestFocus();
                barcodes_nbre_txtbox.setBackground(Color.red);
            } else {
                try {
                    aux.setAdditionalBarcode(Integer.valueOf(barcodes_nbre_txtbox.getText().trim()));
                    barcodes_nbre_txtbox.setBackground(Color.white);
                } catch (java.lang.NumberFormatException ex) {
                    UILog.severeDialog(this, "Number format error for additional barecodes number.", "Number format error.");
                    err = true;
                    barcodes_nbre_txtbox.requestFocus();
                    barcodes_nbre_txtbox.setBackground(Color.red);
                }
            }

            try {
                if (engChangeDatePicker.getDate().toString().isEmpty()) {
                    UILog.severeDialog(this, "Empty date error for engineering change date.", "Date format error.");
                    err = true;
                    engChangeDatePicker.requestFocus();
                    engChangeDatePicker.setBackground(Color.red);
                } else {
                    aux.setEngChangeDate(engChangeDatePicker.getDate());
                    engChangeDatePicker.setBackground(Color.white);
                }
            } catch (NullPointerException | NumberFormatException ex) {
                UILog.severeDialog(this, "Date format error for engineering change date.", "Date format error.");
                err = true;
                engChangeDatePicker.requestFocus();
                engChangeDatePicker.setBackground(Color.red);
            }
            aux.setWriteId(PackagingVars.context.getUser().getId());
            aux.setWriteTime(new Date());
            aux.setActive(Integer.valueOf(active_combobox.getSelectedItem().toString()));
            aux.setProject(project_filter.getSelectedItem().toString());
            aux.setWarehouse(warehouse_filter.getSelectedItem().toString());
            aux.setPackaging_warehouse(packaging_wh_box.getSelectedItem().toString());            
            aux.setDestination(destination_txtbox.getText());
            aux.setArticleDesc(articleDesc_textArea.getText());
            aux.setEngChange(engChange_textArea.getText());
            aux.setGrossWeight(Double.valueOf(grossWeight_txtbox.getText().trim()));
            aux.setVolume(Double.valueOf(volume_txtbox.getText().trim()));

            if (assy_txtbox.getText().isEmpty()) {
                aux.setAssyWorkstationName("-");
            } else {
                aux.setAssyWorkstationName(assy_txtbox.getText().trim());
            }
            aux.setComment(comment_txt.getText());
            if (order_no_txt.getText().isEmpty()) {
                aux.setOrderNo("-");
            } else {
                aux.setOrderNo(order_no_txt.getText());
            }
            if (special_order_check.isSelected()) {
                aux.setSpecialOrder(1);
            } else {
                aux.setSpecialOrder(0);
            }
            if (label_per_piece_checkbox.isSelected()) {
                aux.setLabelPerPiece(true);
            } else {
                aux.setLabelPerPiece(false);
            }
            System.out.println("FORM validation : " + err);
            System.out.println("Object updated" + aux.toString());
            if (!err) {
                aux.update(aux);
                clearFields();
                String[] msg = {"Changements enregistrés"};                                
                msg_lbl.setText(msg[0]);
                UILog.infoDialog(this, msg);
                refresh();
            }

        }
    }//GEN-LAST:event_save_btnActionPerformed

    private void stdTime_txtboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stdTime_txtboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stdTime_txtboxActionPerformed

    private void active_comboboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_active_comboboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_active_comboboxActionPerformed

    private void workplace_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workplace_filterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_workplace_filterActionPerformed

    private void workplace_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_workplace_filterItemStateChanged

    }//GEN-LAST:event_workplace_filterItemStateChanged

    private void segment_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_segment_filterActionPerformed
        System.out.println("Selected Segment " + String.valueOf(segment_filter.getSelectedItem()));
        if ("ALL".equals(String.valueOf(segment_filter.getSelectedItem()).trim())) {
            this.workplace_filter.setSelectedIndex(0);
            this.workplace_filter.setEnabled(false);
        } else {
            this.workplace_filter.removeAllItems();
            this.workplace_filter.setEnabled(true);
            this.setWorkplaceBySegment(String.valueOf(segment_filter.getSelectedItem()));
        }

        refresh();
    }//GEN-LAST:event_segment_filterActionPerformed

    private void segment_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_segment_filterItemStateChanged

    }//GEN-LAST:event_segment_filterItemStateChanged

    private void warehouse_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_warehouse_filterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_warehouse_filterActionPerformed

    private void warehouse_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_warehouse_filterItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_warehouse_filterItemStateChanged

    private void project_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_project_filterActionPerformed
        System.out.println("Selected Project " + String.valueOf(project_filter.getSelectedItem()));
        if ("ALL".equals(String.valueOf(segment_filter.getSelectedItem()).trim())) {
            this.segment_filter.setSelectedIndex(0);
            this.segment_filter.setEnabled(false);
        } else {
            this.setWarehouseByProject(String.valueOf(project_filter.getSelectedItem()));
            this.setPacakgingWarehouseByProject(String.valueOf(project_filter.getSelectedItem()));
            if (this.setSegmentByProject(String.valueOf(project_filter.getSelectedItem()))) {
                this.initHarnessTypeByProject(String.valueOf(project_filter.getSelectedItem()));
            }
        }
        
        System.out.println("Set the packaging warehouse " + String.valueOf(project_filter.getSelectedItem()));
        
    }//GEN-LAST:event_project_filterActionPerformed
  
    
    private void project_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_project_filterItemStateChanged

    }//GEN-LAST:event_project_filterItemStateChanged

    private void comment_txtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_comment_txtFocusGained
        comment_txt.setText("");
    }//GEN-LAST:event_comment_txtFocusGained

    private void print_2nd_closing_label_checkboxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_print_2nd_closing_label_checkboxStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_print_2nd_closing_label_checkboxStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox active_combobox;
    private javax.swing.JTextArea articleDesc_textArea;
    private javax.swing.JTextField assy_txtbox;
    private javax.swing.JTextField barcodes_nbre_txtbox;
    private javax.swing.JButton cancel_btn;
    private javax.swing.JButton clear_search_btn;
    private javax.swing.JTextArea comment_txt;
    private javax.swing.JTextField cpn_txtbox;
    private javax.swing.JTextField cpn_txtbox_search;
    private javax.swing.JTextField create_time_txt;
    private javax.swing.JButton delete_btn;
    private javax.swing.JTextField destination_txtbox;
    private javax.swing.JButton duplicate_btn;
    private org.jdesktop.swingx.JXDatePicker engChangeDatePicker;
    private javax.swing.JTextArea engChange_textArea;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JButton filter_btn;
    private javax.swing.JLabel fname_lbl;
    private javax.swing.JLabel fname_lbl1;
    private javax.swing.JLabel fname_lbl14;
    private javax.swing.JLabel fname_lbl_search;
    private javax.swing.JTextField grossWeight_txtbox;
    private javax.swing.JComboBox harnessType_filter;
    private javax.swing.JLabel id_lbl;
    private javax.swing.JTextField index_txtbox;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JCheckBox label_per_piece_checkbox;
    private javax.swing.JTextField lifes_txtbox;
    private javax.swing.JLabel llogin_lbl_search;
    private javax.swing.JLabel llogin_lbl_search1;
    private javax.swing.JLabel lname_lbl;
    private javax.swing.JLabel lname_lbl1;
    private javax.swing.JLabel lname_lbl2;
    private javax.swing.JLabel lname_lbl_search;
    private javax.swing.JLabel login_lbl;
    private javax.swing.JLabel login_lbl1;
    private javax.swing.JLabel login_lbl2;
    private javax.swing.JLabel login_lbl3;
    private javax.swing.JLabel login_lbl4;
    private javax.swing.JLabel login_lbl5;
    private javax.swing.JLabel login_lbl6;
    private javax.swing.JTextField lpn_txtbox;
    private javax.swing.JLabel msg_lbl;
    private javax.swing.JTextField netWeight_txtbox;
    private javax.swing.JTextField order_no_txt;
    private javax.swing.JTextField pack_size_txtbox;
    private javax.swing.JComboBox pack_type_filter;
    private javax.swing.JTextField pack_type_txtbox_search;
    private javax.swing.JComboBox packaging_wh_box;
    private javax.swing.JTextField price_txtbox;
    private javax.swing.JCheckBox print_2nd_closing_label_checkbox;
    private javax.swing.JComboBox project_filter;
    private javax.swing.JLabel pwd_lbl;
    private javax.swing.JLabel pwd_lbl1;
    private javax.swing.JLabel pwd_lbl10;
    private javax.swing.JLabel pwd_lbl11;
    private javax.swing.JLabel pwd_lbl12;
    private javax.swing.JLabel pwd_lbl13;
    private javax.swing.JLabel pwd_lbl14;
    private javax.swing.JLabel pwd_lbl2;
    private javax.swing.JLabel pwd_lbl3;
    private javax.swing.JLabel pwd_lbl4;
    private javax.swing.JLabel pwd_lbl5;
    private javax.swing.JLabel pwd_lbl6;
    private javax.swing.JLabel pwd_lbl7;
    private javax.swing.JLabel pwd_lbl8;
    private javax.swing.JLabel pwd_lbl9;
    private javax.swing.JButton save_btn;
    private javax.swing.JComboBox segment_filter;
    private javax.swing.JTextField segment_txtbox_search;
    private javax.swing.JCheckBox special_order_check;
    private javax.swing.JTextField stdTime_txtbox;
    private javax.swing.JTextField supplier_pn_txtbox_search;
    private javax.swing.JTable ucs_table;
    private javax.swing.JPanel user_list_panel;
    private javax.swing.JScrollPane user_table_scroll;
    private javax.swing.JTextField volume_txtbox;
    private javax.swing.JComboBox warehouse_filter;
    private javax.swing.JComboBox workplace_filter;
    private javax.swing.JTextField write_time_txt;
    // End of variables declaration//GEN-END:variables

}
