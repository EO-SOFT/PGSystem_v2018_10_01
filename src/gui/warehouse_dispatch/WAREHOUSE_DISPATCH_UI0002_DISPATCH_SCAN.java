/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.warehouse_dispatch;

import __main__.GlobalMethods;
import __main__.GlobalVars;
import entity.BaseContainer;
import entity.HisLogin;
import entity.LoadPlan;
import entity.LoadPlanDestinationRel;
import entity.LoadPlanLine;
import entity.LoadPlanLinePackaging;
import entity.ManufactureUsers;
import entity.PackagingStockMovement;
import gui.packaging.PackagingVars;
import gui.packaging.reports.PACKAGING_UI0010_PalletDetails;
import gui.warehouse_dispatch.process_control_labels.ControlState;
import gui.warehouse_dispatch.process_control_labels.S001_PalletNumberScan;
import gui.warehouse_dispatch.process_reservation.ReservationState;
import helper.JDialogExcelFileChooser;
import gui.warehouse_dispatch.state.WarehouseHelper;
import helper.HQLHelper;
import helper.Helper;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import static java.awt.Event.DELETE;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.hibernate.Query;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.SQLQuery;
import org.hibernate.type.StandardBasicTypes;
import ui.UILog;
import ui.error.ErrorMsg;
import gui.warehouse_dispatch.process_reservation.S001_ReservPalletNumberScan;
import helper.JTableHelper;
import java.awt.GridLayout;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author user
 */
public final class WAREHOUSE_DISPATCH_UI0002_DISPATCH_SCAN extends javax.swing.JFrame implements ActionListener,
        PropertyChangeListener {

    ReservationState state = WarehouseHelper.warehouse_reserv_context.getState();
    @SuppressWarnings("UseOfObsoleteCollectionType")
    Vector<String> load_plan_lines_table_header = new Vector<String>();
    Vector<String> load_plan_table_header = new Vector<String>();
    Vector load_plan_lines_table_data = new Vector();
    Vector load_plan_table_data = new Vector();
    Vector total_per_dest_table_data = new Vector();
    Vector total_per_dest_table_data_header = new Vector<String>();
    
    //Used in tab 3
    Vector total_packages_data = new Vector();
    Vector<String> total_packages_header = new Vector<String>() {};
    
    @SuppressWarnings("UseOfObsoleteCollectionType")
    Vector load_plan_lines_data = new Vector();
    @SuppressWarnings("UseOfObsoleteCollectionType")
    Vector load_plan_data = new Vector();
    String initTextValue = "...................";
    int DESTINATION_COMLUMN = 7;
    int LINE_ID_COMLUMN = 8;
    int PALLET_NUM_COLUMN = 1;
    static int destIndex = 0;
    static String selectedDestination = "";
    JRadioButton[] radioButtonList;
    private ReleasingJob task;

    private ManufactureUsers u = new ManufactureUsers();

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getSelectedDestination() {
        return selectedDestination;
    }

    class ReleasingJob extends SwingWorker<Void, Void> {

        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            Random random = new Random();

            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            //close_plan_menu.setEnabled(true);
            setCursor(null); //turn off the wait cursor
            JOptionPane.showMessageDialog(null, "Plan released !\n");

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Creates new form NewJFrame
     */
    public WAREHOUSE_DISPATCH_UI0002_DISPATCH_SCAN() {
        initComponents();
    }

    public WAREHOUSE_DISPATCH_UI0002_DISPATCH_SCAN(Object[] context, JFrame parent) {

        initComponents();
        //Initialiser les valeurs globales de test (Pattern Liste,...)
        Helper.startSession();

        initGui();

    }

    /**
     * Charge les destinations finales du plan sous forme de radioButton
     */
    private boolean loadDestinationsRadioGroup(int loadPlanId) {
        System.out.println("Start loadDestinationsRadioGroup ");
        Helper.startSession();
        Query query = Helper.sess.createQuery(HQLHelper.GET_FINAL_DESTINATIONS_OF_PLAN);
        query.setParameter("loadPlanId", loadPlanId);
        Helper.sess.getTransaction().commit();
        List result = query.list();
        if (result.isEmpty()) {
            UILog.info(ErrorMsg.APP_ERR0025[0]);
            UILog.infoDialog(null, ErrorMsg.APP_ERR0025);
            return false;
        } else {
            //Remouve all items from jpanel_destinations
            destIndex = 0;
            jpanel_destinations.removeAll();
            jpanel_destinations.setLayout(new GridLayout(0, 6));
            ButtonGroup group = new ButtonGroup();
            radioButtonList = new JRadioButton[result.size()];
            System.out.println("radioButtonList " + radioButtonList.length);
            System.out.println(radioButtonList.toString());

            //Map destinations data in the list
            for (Object o : result) {
                System.out.println("Rendering radioButton " + destIndex);
                LoadPlanDestinationRel lp = (LoadPlanDestinationRel) o;
                radioButtonList[destIndex] = new JRadioButton(lp.getDestination(), false);

                if (destIndex == 0) {
                    radioButtonList[destIndex].setSelected(true);
                    selectedDestination = radioButtonList[destIndex].getText();
                }

                radioButtonList[destIndex].addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent evt) {
                        JRadioButton button = (JRadioButton) evt.getSource();
                        String command = button.getActionCommand();
                        if (null != button && button.isSelected()) {
                            // do something with the button
                            selectedDestination = button.getText();
                            destination_label_help.setText(selectedDestination);
                        }
                        filterPlanLines(false);
                    }
                });
                System.out.println("Add " + radioButtonList[destIndex] + " to group.");
                group.add(radioButtonList[destIndex]);
                jpanel_destinations.add(radioButtonList[destIndex]);
                jpanel_destinations.revalidate();
                jpanel_destinations.repaint();
                destIndex++;
            }
            System.out.println("End loadDestinationsRadioGroup");
            return true;
        }
    }

    private void initPlanTableDoubleClick() {

        this.load_plan_lines_table.addMouseListener(
                new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {

                            new PACKAGING_UI0010_PalletDetails(null,
                                    rootPaneCheckingEnabled,
                                    String.valueOf(
                                            load_plan_lines_table.getValueAt(
                                                    load_plan_lines_table.getSelectedRow(),
                                                    PALLET_NUM_COLUMN)), "", 1, true, true, true
                            ).setVisible(true);

                        }
                    }
                }
        );

        this.load_plan_table.addMouseListener(
                new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            loadPlanDataInGui();
                        }
                    }

                    public void loadPlanDataInGui() {
                        String id = String.valueOf(load_plan_table.getValueAt(load_plan_table.getSelectedRow(), 1));
                        Helper.startSession();
                        Query query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_BY_ID);
                        query.setParameter("id", Integer.valueOf(id));

                        Helper.sess.getTransaction().commit();
                        List result = query.list();
                        LoadPlan plan = (LoadPlan) result.get(0);
                        WarehouseHelper.temp_load_plan = plan;

                        //Load destinations of the plan
                        //if (loadDestinations(Integer.valueOf(id))) {
                        if (loadDestinationsRadioGroup(Integer.valueOf(id))) {
                            loadPlanDataToLabels(plan, radioButtonList[0].getText());
                            reloadPlanLinesData(Integer.valueOf(id), radioButtonList[0].getText());

                            plan_id_filter.setText(id);
                            //Disable delete button if the plan is CLOSED
                            if (WarehouseHelper.LOAD_PLAN_STATE_CLOSED.equals(plan.getPlanState())) {
                                delete_plan_submenu.setEnabled(false);
                                close_plan_menu.setEnabled(false);
                                export_plan_menu.setEnabled(true);
                                edit_plan_menu.setEnabled(false);
                                control_dispatch_menu.setEnabled(false);
                                set_packaging_pile_btn.setEnabled(true);
                                piles_box.setEnabled(true);
                                scan_txt.setEnabled(false);
                                txt_filter_part.setEnabled(true);
                                radio_btn_20.setEnabled(false);
                                radio_btn_40.setEnabled(false);
                            } else { // The plan still Open
                                export_plan_menu.setEnabled(true);
                                edit_plan_menu.setEnabled(true);
                                control_dispatch_menu.setEnabled(true);
                                set_packaging_pile_btn.setEnabled(true);
                                piles_box.setEnabled(true);
                                scan_txt.setEnabled(true);
                                radio_btn_20.setEnabled(true);
                                radio_btn_40.setEnabled(true);
                                txt_filter_part.setEnabled(true);

                                if (WarehouseHelper.warehouse_reserv_context.getUser().getAccessLevel() == GlobalVars.PROFIL_WAREHOUSE_AGENT) {
                                    delete_plan_menu.setEnabled(false);
                                    close_plan_menu.setEnabled(false);
                                }
                                if (WarehouseHelper.warehouse_reserv_context.getUser().getAccessLevel() == GlobalVars.PROFIL_ADMIN) {
                                    delete_plan_menu.setEnabled(true);
                                    close_plan_menu.setEnabled(true);
                                }

                            }
                        }

                        filterPlanLines(false);
                        current_plan_jpanel.setSelectedIndex(1);
                    }
                }
        );
    }

    public JTable getLoadPlan_lines_table() {
        return load_plan_lines_table;
    }

    public void setDispatch_lines_table(JTable load_plan_lines_table) {
        this.load_plan_lines_table = load_plan_lines_table;
    }

    /**
     * Calculate how many packaging item is consumed by this plan for each
     * destination. It calculate the full explosion of pallets/box types to
     * single item + the external packaging items.
     *
     * @param planId
     */
    public void reloadTruckTotals(int planId) {

        Helper.startSession();
        String query_str = String.format(
                HQLHelper.GET_TOTAL_TRUCK_VALUES,
                planId);
        SQLQuery query = Helper.sess.createSQLQuery(query_str);

        query.addScalar("total_net_weight", StandardBasicTypes.DOUBLE);
        query.addScalar("total_gross_weight", StandardBasicTypes.DOUBLE);
        query.addScalar("total_volume", StandardBasicTypes.DOUBLE);
        query.addScalar("total_value", StandardBasicTypes.DOUBLE);
        query.addScalar("total_std_time", StandardBasicTypes.DOUBLE);

        List<Object[]> result = query.list();
        Helper.sess.getTransaction().commit();
        //Reset table content
        //Populate jtable rows
        for (Object[] o : result) {

            txt_total_net_weight.setText(String.format("%1$,.2f", o[0]));
            txt_gross_weight.setText(String.format("%1$,.2f", o[1]));
            txt_total_volume.setText(String.format("%1$,.2f", o[2]));
            txt_total_value.setText(String.format("%1$,.2f", o[3]));
            txt_total_hours.setText(String.format("%1$,.2f", o[4]));
        }
    }

    /**
     * Calculate the total per part number and per destination.
     *
     * @param planId
     */
    public void reloadTotalPerPNTab2(int planId) {
        /*
         Helper.startSession();
         String query_str = String.format(
         HQLHelper.GET_LOAD_PLAN_EXT_PACKAGING_AND_CONTAINER,
         planId, planId);
         SQLQuery query = Helper.sess.createSQLQuery(query_str);

         query.addScalar("destination", StandardBasicTypes.STRING);
         query.addScalar("pack_item", StandardBasicTypes.STRING);
         query.addScalar("quantity", StandardBasicTypes.DOUBLE);

         List<Object[]> result = query.list();
         Helper.sess.getTransaction().commit();
         //Reset table content
         Vector total_packages_data = new Vector();
         Vector<String> total_packages_header = new Vector<String>() {
         };
         total_packages_header.add("Destination");
         total_packages_header.add("Pack Item");
         total_packages_header.add("Quantity");
         DefaultTableModel dataModel = new DefaultTableModel(total_packages_data, total_packages_header);
         jtable_total_packages.setModel(dataModel);

         //Populate jtable rows
         for (Object[] o : result) {
         Vector<Object> oneRow = new Vector<Object>();
         oneRow.add(o[0]);
         oneRow.add(o[1]);
         System.out.println("o[2].toString() "+o[2].toString());
         oneRow.add(String.valueOf(String.format("%1$,2f", Double.valueOf(o[2].toString()))));
         total_packages_data.add(oneRow);
         }
         jtable_total_packages.setModel(new DefaultTableModel(total_packages_data, total_packages_header));
         setTotalPackagingTableRowsStyle();
         */
    }

    /**
     * Calculate how many packaging item is consumed by this plan for each
     * destination. It calculate the full explosion of pallets/box types to
     * single item + the external packaging items.
     *
     * @param planId
     */
    public void reloadPackagingContainerTab3(int planId) {
        System.out.println("reloadPackagingContainerTab3 ");
        
        Helper.startSession();
        String query_str = String.format(
                HQLHelper.GET_LOAD_PLAN_EXT_PACKAGING_AND_CONTAINER,
                planId, planId);
        SQLQuery query = Helper.sess.createSQLQuery(query_str);

        query.addScalar("destination", StandardBasicTypes.STRING);
        query.addScalar("pack_item", StandardBasicTypes.STRING);
        query.addScalar("quantity", StandardBasicTypes.DOUBLE);

        List<Object[]> result = query.list();
        Helper.sess.getTransaction().commit();
        
        System.out.println("result lines "+result.size());
        
        //Reset table content
        total_packages_data = new Vector();
        total_packages_header = new Vector<String>() {};
        total_packages_header.add("Destination");
        total_packages_header.add("Pack Item");
        total_packages_header.add("Quantity");
        
        jtable_total_packages.setModel(new DefaultTableModel(total_packages_data, total_packages_header));

        //Populate jtable rows
        for (Object[] o : result) {
            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add((String) o[0]);
            oneRow.add((String) o[1]);
            oneRow.add(String.valueOf(String.format("%1$,.2f", Double.valueOf(o[2].toString()))));
            System.out.println("one row "+oneRow.toString());
            total_packages_data.add(oneRow);
        }
        
        System.out.println("reloadPackagingContainerTab3 query Total packaging \n\n"+query_str);
        jtable_total_packages.setModel(new DefaultTableModel(total_packages_data, total_packages_header));
        setTotalPackagingTableRowsStyle();

    }

    /**
     * Load data in the UI from the given object
     *
     * @param p
     */
    public void loadPlanDataToLabels(LoadPlan p, String defaultDest) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        plan_id_filter.setText("" + p.getId());
        this.plan_num_label.setText("" + p.getId());
        this.create_user_label.setText(p.getUser());
        this.create_time_label.setText(sdf1.format(p.getCreateTime()));
        if (p.getDeliveryTime() != null) {
            this.dispatch_date_label.setText(sdf2.format(p.getDeliveryTime()));
        }
        if (p.getEndTime() != null) {
            this.release_date_label.setText(sdf1.format(p.getEndTime()));
        }
        this.project_label.setText(p.getProject());
        this.state_label.setText(p.getPlanState());
        this.destination_label_help.setText("");
        this.truck_no_txt1.setText(p.getTruckNo());
        //Select the last pile of the plan
        Helper.startSession();
        String query_str = String.format(HQLHelper.GET_PILES_OF_PLAN, Integer.valueOf(p.getId()));
        SQLQuery query = Helper.sess.createSQLQuery(query_str);

        query.addScalar("pile_num", StandardBasicTypes.INTEGER);
        List<Object[]> resultList = query.list();
        Helper.sess.getTransaction().commit();
        Integer[] arg = (Integer[]) resultList.toArray(new Integer[resultList.size()]);
        if (!resultList.isEmpty()) {
            for (int i = 1; i < arg.length; i++) {
                if (Integer.valueOf(piles_box.getItemAt(i).toString().trim()) == arg[i]) {
                    piles_box.setSelectedIndex(i);
                    pile_label_help.setText(arg[i].toString());
                }
            }
        } else {
            piles_box.setSelectedIndex(1);
            pile_label_help.setText(piles_box.getSelectedItem().toString());
        }
        this.destination_label_help.setText(defaultDest);

    }

    public void cleanDataLabels() {
        this.truck_no_txt1.setText("#");
        this.plan_num_label.setText("#");
        this.create_user_label.setText("-----");
        this.create_time_label.setText("--/--/---- --:--");
        this.dispatch_date_label.setText("--/--/----");
        this.release_date_label.setText("--/--/---- --:--");
        this.project_label.setText("-----");
        this.state_label.setText("-----");
        this.destination_label_help.setText("#");
        this.pile_label_help.setText("0");
    }

    public void setDestinationHelpLabel(String dest) {
        destination_label_help.setText(dest);
    }

    public void setTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        create_time_label.setText(sdf.format(date));
    }

    public void setPlanNumLabel(String text) {
        plan_num_label.setText(text);
    }

    public String getSelectedPileNum() {
        return String.valueOf(piles_box.getSelectedItem());
    }

    public String getPlanNum() {
        return plan_num_label.getText();
    }

    public String getPlanDispatchTime() {
        return dispatch_date_label.getText();
    }

    private void initGui() {

        this.scan_txt.setEnabled(false);
        this.txt_filter_part.setEnabled(false);

        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);

        //Group radio buttons
        ButtonGroup group = new ButtonGroup();
        group.add(radio_btn_20);
        group.add(radio_btn_40);

        //Center the this dialog in the screen
        Helper.centerJFrame(this);

        //Desable load plans lines table edition
        disableLoadPlanLinesEditingTable();

        //Desable load plans table edition
        disableLoadPlanEditingTable();

        //Init JTable Key Listener
        initJTableKeyListener();

        //Load table header
        load_line_table_header();

        //Load table header
        load_plan_table_header();

        //Set the rendering of plans table in Tab1
        // setLoadPlanTableRowsStyle();
        //Initialize double clique on table row
        initPlanTableDoubleClick();

        ///Charger les plan de la base
        reloadPlansData();

        //
        //this.loadDestinations(0);
        //Disable destinations Jbox
        //destinations_box.setEnabled(false);
        piles_box.setEnabled(false);

        export_plan_menu.setEnabled(false);
        delete_plan_menu.setEnabled(false);
        close_plan_menu.setEnabled(false);
        edit_plan_menu.setEnabled(false);
        control_dispatch_menu.setEnabled(false);
        set_packaging_pile_btn.setEnabled(false);

        //Clean values form fields
        cleanDataLabels();

        //Show the jframe
        this.setVisible(true);

    }

    private void loadPiles() {
        piles_box.removeAllItems();
        piles_box.addItem("*");
        int len = 32;
        if (radio_btn_40.isSelected()) {
            len = 64;
        }
        for (int i = 1; i <= len; i++) {
            piles_box.addItem(i);
        }
    }

    public void initJTableKeyListener() {
        int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
        InputMap inputMap = this.load_plan_lines_table.getInputMap(condition);
        ActionMap actionMap = this.load_plan_lines_table.getActionMap();

        // DELETE is a String constant that for me was defined as "Delete"
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), DELETE);

        actionMap.put(DELETE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!state_label.getText().equals(WarehouseHelper.LOAD_PLAN_STATE_CLOSED)) {
                    int confirmed = JOptionPane.showConfirmDialog(null,
                            "Voulez-vous supprimer la ligne ?", "Suppression !",
                            JOptionPane.YES_NO_OPTION);
                    if (confirmed == 0) {

                        Integer id = (Integer) load_plan_lines_table.getValueAt(load_plan_lines_table.getSelectedRow(), LINE_ID_COMLUMN);
                        //Delete line from the database
                        Helper.startSession();
                        Query query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_LINE_BY_ID);
                        query.setParameter("id", id);

                        Helper.sess.getTransaction().commit();
                        List result = query.list();
                        LoadPlanLine line = (LoadPlanLine) result.get(0);

                        line.delete(line);
                        filterPlanLines(false);
                    }
                }
            }

        });

    }

    /**
     * Desactive l'édition du jTable load plan lines
     */
    public void disableLoadPlanLinesEditingTable() {
        for (int c = 0; c < load_plan_lines_table.getColumnCount(); c++) {

            Class<?> col_class = load_plan_lines_table.getColumnClass(c);
            load_plan_lines_table.setDefaultEditor(col_class, null);        // remove editor                       
        }
        JTableHelper.sizeColumnsToFit(load_plan_lines_table);
    }

    /**
     * Desactive l'édition du jTable load plan
     */
    public void disableLoadPlanEditingTable() {
        for (int c = 0; c < load_plan_table.getColumnCount(); c++) {
            Class<?> col_class = load_plan_table.getColumnClass(c);
            load_plan_table.setDefaultEditor(col_class, null);        // remove editor                 
        }
        load_plan_table.setAutoCreateRowSorter(true);
        JTableHelper.sizeColumnsToFit(load_plan_table);
    }

    /**
     * Charge les entête du jTable load plan lines
     */
    public void load_line_table_header() {
        this.reset_load_plan_lines_table_content();

        load_plan_lines_table_header.add("POSITION NUM");
        load_plan_lines_table_header.add("PALLET NUM");
        load_plan_lines_table_header.add("CUSTOMER PN");
        load_plan_lines_table_header.add("INTERNAL PN");
        load_plan_lines_table_header.add("PACK TYPE");
        load_plan_lines_table_header.add("PACK SIZE");
        load_plan_lines_table_header.add("DISPATCH LABEL NO");
        load_plan_lines_table_header.add("DESTINATION");
        load_plan_lines_table_header.add("N° LINE");
        load_plan_lines_table_header.add("SEGMENT");
        load_plan_lines_table_header.add("FIFO TIME");

        load_plan_lines_table.setModel(new DefaultTableModel(load_plan_lines_data, load_plan_lines_table_header));
    }

    /**
     * Charge les entête du jTable load plan
     */
    public void load_plan_table_header() {
        this.reset_load_plan_table_content();
        load_plan_table_header.add("TRUCK NO");
        load_plan_table_header.add("N° PLAN");
        load_plan_table_header.add("CREATE DATE");
        load_plan_table_header.add("DELIV DATE");
        load_plan_table_header.add("USER");
        load_plan_table_header.add("PROJECT");
        load_plan_table_header.add("STATE");

        load_plan_table.setModel(new DefaultTableModel(load_plan_data, load_plan_table_header));
    }

    /**
     *
     * @param planId
     * @param destinationWh
     * @param harnessPart
     * @param pileNum
     * @param lineState 0 All lines, 1 Controlled lines, 2 Not controlled lines
     * @param palletNumber
     */
    //completer la méthode pour filtrer sur les lignes
    public void filterPlanLines(int planId, String destinationWh, String harnessPart, int pileNum, int lineState, String palletNumber, String dispatchNumber) {

        Helper.startSession();
        String GET_LOAD_PLAN_LINE_BY_PLAN_ID_AND_DEST_AND_PN_AND_PILE = "FROM LoadPlanLine lpl WHERE "
                + "lpl.loadPlanId = :loadPlanId "
                + "AND lpl.destinationWh LIKE :destinationWh "
                + "AND lpl.harnessPart LIKE :harnessPart ";
        if (pileNum != 0) {
            GET_LOAD_PLAN_LINE_BY_PLAN_ID_AND_DEST_AND_PN_AND_PILE += "AND lpl.pileNum = :pileNum ";
        }
        if (lineState == 1) {
            GET_LOAD_PLAN_LINE_BY_PLAN_ID_AND_DEST_AND_PN_AND_PILE += "AND lpl.dispatchLabelNo != '' ";
        }
        if (lineState == 2) {
            GET_LOAD_PLAN_LINE_BY_PLAN_ID_AND_DEST_AND_PN_AND_PILE += "AND lpl.dispatchLabelNo = '' ";
        }

        if (palletNumber.length() != 0) {
            GET_LOAD_PLAN_LINE_BY_PLAN_ID_AND_DEST_AND_PN_AND_PILE += "AND lpl.palletNumber LIKE :palletNumber ";
        }
        if (dispatchNumber.length() != 0) {
            GET_LOAD_PLAN_LINE_BY_PLAN_ID_AND_DEST_AND_PN_AND_PILE += "AND lpl.dispatchLabelNo LIKE :dispatchLabelNo ";
        }
        GET_LOAD_PLAN_LINE_BY_PLAN_ID_AND_DEST_AND_PN_AND_PILE += "ORDER BY "
                + "destinationWh ASC, "
                + "pileNum DESC,"
                + "id ASC";

        Query query = Helper.sess.createQuery(GET_LOAD_PLAN_LINE_BY_PLAN_ID_AND_DEST_AND_PN_AND_PILE).setCacheable(false);
        query.setParameter("loadPlanId", planId);
        query.setParameter("destinationWh", "%" + destinationWh + "%");
        query.setParameter("harnessPart", "%" + harnessPart + "%");
        if (palletNumber.length() != 0) {
            query.setParameter("palletNumber", "%" + palletNumber + "%");
        }
        if (dispatchNumber.length() != 0) {
            query.setParameter("dispatchLabelNo", "%" + dispatchNumber + "%");
        }

        if (pileNum != 0) {
            query.setParameter("pileNum", pileNum);
        }

        Helper.sess.getTransaction().commit();
        List result = query.list();
        txt_nbreLigne.setText(result.size() + "");
        this.reload_load_plan_lines_table_data(result);

        this.reloadPackagingContainerTab3(planId);

        this.reloadTruckTotals(planId);
    }

    /**
     * Charge les données du jTable load plan lines
     *
     * @param planId
     * @param destinationWh
     */
    public void reloadPlanLinesData(int planId, String destinationWh) {
        System.out.println("reloadPlanLinesData destination WH" + destinationWh);
        Helper.startSession();
        Query query = null;
        if (destinationWh != null && !destinationWh.isEmpty()) {
            query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_LINE_BY_PLAN_ID_AND_WH);
            query.setParameter("loadPlanId", planId);
            query.setParameter("destinationWh", destinationWh);
        } else {
            query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_LINE_BY_PLAN_ID);
            query.setParameter("loadPlanId", planId);
        }

        Helper.sess.getTransaction().commit();
        List result = query.list();
        this.reload_load_plan_lines_table_data(result);
    }

    /**
     * Charge les données du jTable load plan lines
     */
    public void reloadPlansData() {
        Helper.startSession();
        Query query;
        if (!lp_filter_val.getText().isEmpty()) {
            query = Helper.sess.createQuery(HQLHelper.GET_LOAD_ALL_PLANS_BY_FILTER);
            int id = 0;
            try {
                id = Integer.valueOf(lp_filter_val.getText());
            } catch (Exception ex) {
                id = 0;
            }
            query.setInteger("id", id);
            query.setString("truckNo", "%" + lp_filter_val.getText() + "%");
            query.setString("user", "%" + lp_filter_val.getText() + "%");
        } else {
            query = Helper.sess.createQuery(HQLHelper.GET_LOAD_ALL_PLANS);
        }
        Helper.sess.getTransaction().commit();
        List result = query.list();
        this.reload_load_plan_table_data(result);

    }

    /**
     *
     * @param table : JTable element en entrée
     * @return Les donées du JTable sous forme de table 2 dimensions
     */
    public Object[][] getTableData(JTable table) {
        TableModel dtm = table.getModel();
        int nRow = dtm.getRowCount(), nCol = dtm.getColumnCount();
        Object[][] tableData = new Object[nRow][nCol];
        for (int i = 0; i < nRow; i++) {
            for (int j = 0; j < nCol; j++) {
                tableData[i][j] = dtm.getValueAt(i, j);
            }
        }
        return tableData;
    }

    public void reset_load_plan_table_content() {
        load_plan_table_data = new Vector();
        DefaultTableModel dataModel = new DefaultTableModel(load_plan_table_data, load_plan_table_header);
        load_plan_table.setModel(dataModel);
    }

    public void reset_load_plan_lines_table_content() {
        load_plan_lines_table_data = new Vector();
        DefaultTableModel dataModel = new DefaultTableModel(load_plan_lines_table_data, load_plan_lines_table_header);
        load_plan_lines_table.setModel(dataModel);
    }

    /**
     * Mapping des donées dans le JTable load plan lines
     *
     * @param resultList
     */
    public void reload_load_plan_lines_table_data(List resultList) {
        this.reset_load_plan_lines_table_content();

        txt_totalQty.setText("0.0");
        for (Object o : resultList) {
            LoadPlanLine lpl = (LoadPlanLine) o;
            Vector<Object> oneRow = new Vector<Object>();

            oneRow.add(String.format("%02d", lpl.getPileNum()));
            oneRow.add(lpl.getPalletNumber());
            oneRow.add(lpl.getHarnessPart());
            oneRow.add(lpl.getSupplierPart());
            oneRow.add(lpl.getPackType());
            oneRow.add(lpl.getQty());
            oneRow.add(lpl.getDispatchLabelNo());
            oneRow.add(lpl.getDestinationWh());
            oneRow.add(lpl.getId());
            oneRow.add(lpl.getHarnessType());
            oneRow.add(lpl.getCreateTime());

            //Set total qty label
            txt_totalQty.setText("" + (Float.valueOf(txt_totalQty.getText()) + lpl.getQty()));

            load_plan_lines_table_data.add(oneRow);

        }
        load_plan_lines_table.setModel(new DefaultTableModel(load_plan_lines_table_data, load_plan_lines_table_header));

        //Initialize default style for table container
        setLoadPlanLinesTableRowsStyle();
    }

    /**
     * Mapping des donées dans le JTable load plan lines
     *
     * @param resultList
     */
    public void reload_load_plan_table_data(List resultList) {
        this.reset_load_plan_table_content();

        for (Object o : resultList) {
            LoadPlan lp = (LoadPlan) o;
            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add(lp.getTruckNo());
            oneRow.add(lp.getId());
            oneRow.add(GlobalMethods.convertToStandardDate(lp.getCreateTime()));
            oneRow.add(GlobalMethods.convertToStandardDate(lp.getDeliveryTime()));
            oneRow.add(lp.getUser());
            oneRow.add(lp.getProject());
            oneRow.add(lp.getPlanState());

            load_plan_table_data.add(oneRow);

        }
        load_plan_table.setModel(new DefaultTableModel(load_plan_table_data, load_plan_table_header));

        //Initialize default style for table container
        setLoadPlanTableRowsStyle();
    }

    public void tableAddRow(Component oneRow) {
        load_plan_lines_table.add(oneRow);
    }

    /**
     * Réinitialise le style de la table load plan lines
     */
    public void setLoadPlanLinesTableRowsStyle() {
        //Initialize default style for table container

        //#######################
        load_plan_lines_table.setFont(new Font(String.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_FONT")), Font.PLAIN, 12));
        load_plan_lines_table.setRowHeight(Integer.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_ROW_HEIGHT")));
        load_plan_lines_table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus, int row, int col) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                String dispatchLabelNo = (String) table.getModel().getValueAt(row, 6);
                //############### DISPATCH LABEL CONTROLLED ?
                if (isSelected) {
                    setBackground(new Color(51, 204, 255));
                    setForeground(Color.BLACK);
                } else if (!"".equals(dispatchLabelNo)) {
                    setBackground(new Color(146, 255, 167));
                    setForeground(Color.BLACK);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }
                setHorizontalAlignment(JLabel.LEFT);
                return this;
            }
        });
        //#######################
        this.disableLoadPlanLinesEditingTable();
    }

    /**
     * Réinitialise le style de la table load plan lines
     */
    public void setTotalPackagingTableRowsStyle() {
        //Initialize default style for table container

        //#######################
        jtable_total_packages.setFont(new Font(String.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_FONT")), Font.PLAIN, 12));
        jtable_total_packages.setRowHeight(Integer.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_ROW_HEIGHT")));
        jtable_total_packages.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus, int row, int col) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                if (isSelected) {
                    setBackground(new Color(51, 204, 255));
                    setForeground(Color.BLACK);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }
                setHorizontalAlignment(JLabel.LEFT);
                return this;
            }
        });
        //#######################
        JTableHelper.sizeColumnsToFit(jtable_total_packages);
    }

    /**
     * Réinitialise le style de la table load plan
     */
    public void setLoadPlanTableRowsStyle() {
        //Initialize default style for table container

        //#######################        
        load_plan_table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus, int row, int col) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                if (isSelected) {
                    setBackground(new Color(51, 204, 255));
                    setForeground(Color.BLACK);
                }

                setBackground(Color.WHITE);
                setForeground(Color.BLACK);

                setHorizontalAlignment(JLabel.LEFT);

                return this;
            }
        });
        //#######################
        this.disableLoadPlanEditingTable();
    }

    /**
     *
     * @param msg : String to be displayed
     * @param type : 1 for OK , -1 for error, 0 to clean the label
     */
    public void setMessageLabel(String msg, int type) {

        switch (type) {
            case -1:
                message_label.setBackground(Color.red);
                message_label.setForeground(Color.white);
                message_label.setText(msg);
                break;
            case 1:
                message_label.setBackground(Color.green);
                message_label.setForeground(Color.black);
                message_label.setText(msg);
                break;
            case 0:
                message_label.setBackground(Color.WHITE);
                message_label.setText("");
                break;
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        connectedUserName_label = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        scan_txt = new javax.swing.JTextField();
        radio_btn_20 = new javax.swing.JRadioButton();
        radio_btn_40 = new javax.swing.JRadioButton();
        time_label5 = new javax.swing.JLabel();
        pile_label_help = new javax.swing.JLabel();
        destination_label_help = new javax.swing.JLabel();
        message_label = new javax.swing.JTextField();
        jSplitPane2 = new javax.swing.JSplitPane();
        current_plan_jpanel = new javax.swing.JTabbedPane();
        all_plans_jpanel = new javax.swing.JPanel();
        all_plans_scroll_panel = new javax.swing.JScrollPane();
        load_plan_table = new javax.swing.JTable();
        new_plan_btn = new javax.swing.JButton();
        refresh_btn = new javax.swing.JButton();
        lp_filter_val = new javax.swing.JTextField();
        plan_id_filter = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txt_filter_part = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txt_filter_pal_number = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txt_filter_dispatchl_number = new javax.swing.JTextField();
        piles_box = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        controlled_combobox = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jpanel_destinations = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        set_packaging_pile_btn = new javax.swing.JButton();
        btn_filter_ok = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txt_totalQty = new javax.swing.JLabel();
        txt_nbreLigne = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        load_plan_lines_table = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        total_per_pn_table = new javax.swing.JTable();
        tab2_destination = new javax.swing.JTextField();
        tab2_cpn = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        tab2_packtype = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        tab2_refresh = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        tab2_txt_totalQty = new javax.swing.JLabel();
        tab2_txt_nbreLigne = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txt_total_hours = new javax.swing.JTextField();
        txt_total_value = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txt_total_volume = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txt_gross_weight = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txt_total_net_weight = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        tab3_refresh = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jtable_total_packages = new javax.swing.JTable();
        details_jpanel = new javax.swing.JPanel();
        time_label8 = new javax.swing.JLabel();
        create_time_label = new javax.swing.JLabel();
        time_label1 = new javax.swing.JLabel();
        time_label2 = new javax.swing.JLabel();
        plan_num_label = new javax.swing.JLabel();
        create_user_label = new javax.swing.JLabel();
        time_label3 = new javax.swing.JLabel();
        time_label4 = new javax.swing.JLabel();
        dispatch_date_label = new javax.swing.JLabel();
        state_label = new javax.swing.JLabel();
        time_label6 = new javax.swing.JLabel();
        time_label7 = new javax.swing.JLabel();
        release_date_label = new javax.swing.JLabel();
        time_label9 = new javax.swing.JLabel();
        project_label = new javax.swing.JLabel();
        truck_no_txt1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        new_plan_menu = new javax.swing.JMenu();
        export_plan_menu = new javax.swing.JMenu();
        edit_plan_menu = new javax.swing.JMenu();
        pallet_details_menu = new javax.swing.JMenu();
        plans_list_meni = new javax.swing.JMenu();
        control_dispatch_menu = new javax.swing.JMenu();
        delete_plan_menu = new javax.swing.JMenu();
        delete_plan_submenu = new javax.swing.JMenuItem();
        close_plan_menu = new javax.swing.JMenu();
        close_plan_submenu = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Dispatch Module");
        setBackground(new java.awt.Color(194, 227, 254));
        setFocusTraversalPolicyProvider(true);
        setName("dispatch_module"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                formKeyTyped(evt);
            }
        });

        connectedUserName_label.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        connectedUserName_label.setForeground(new java.awt.Color(0, 51, 204));
        connectedUserName_label.setText(" ");

        jLabel15.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabel15.setText("Session : ");

        scan_txt.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        scan_txt.setForeground(new java.awt.Color(0, 0, 153));
        scan_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scan_txtActionPerformed(evt);
            }
        });
        scan_txt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                scan_txtKeyPressed(evt);
            }
        });

        radio_btn_20.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        radio_btn_20.setSelected(true);
        radio_btn_20.setText("Truck 20\" (32 Positions)");
        radio_btn_20.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radio_btn_20ItemStateChanged(evt);
            }
        });

        radio_btn_40.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        radio_btn_40.setText("Truck 40\" (64 Position)");
        radio_btn_40.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radio_btn_40ItemStateChanged(evt);
            }
        });

        time_label5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        time_label5.setText("Destination");

        pile_label_help.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        pile_label_help.setForeground(new java.awt.Color(204, 0, 0));
        pile_label_help.setText("0");

        destination_label_help.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        destination_label_help.setForeground(new java.awt.Color(204, 0, 0));
        destination_label_help.setText("#");

        message_label.setEditable(false);
        message_label.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        message_label.setForeground(new java.awt.Color(255, 51, 51));
        message_label.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                message_labelActionPerformed(evt);
            }
        });

        current_plan_jpanel.setPreferredSize(new java.awt.Dimension(800, 1543));
        current_plan_jpanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                current_plan_jpanelMouseClicked(evt);
            }
        });

        all_plans_jpanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        all_plans_jpanel.setPreferredSize(new java.awt.Dimension(300, 1543));

        load_plan_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Truck No", "N° Plan", "Create date", "Dispatch date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        load_plan_table.setCellSelectionEnabled(true);
        load_plan_table.setMinimumSize(new java.awt.Dimension(10, 10));
        load_plan_table.setName(""); // NOI18N
        all_plans_scroll_panel.setViewportView(load_plan_table);

        new_plan_btn.setBackground(new java.awt.Color(153, 204, 255));
        new_plan_btn.setText("New...");
        new_plan_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new_plan_btnActionPerformed(evt);
            }
        });

        refresh_btn.setBackground(new java.awt.Color(153, 204, 255));
        refresh_btn.setText("Refresh");
        refresh_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refresh_btnActionPerformed(evt);
            }
        });

        lp_filter_val.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lp_filter_valActionPerformed(evt);
            }
        });
        lp_filter_val.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                lp_filter_valKeyTyped(evt);
            }
        });

        plan_id_filter.setBackground(new java.awt.Color(153, 255, 255));
        plan_id_filter.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        plan_id_filter.setMinimumSize(new java.awt.Dimension(14, 24));
        plan_id_filter.setPreferredSize(new java.awt.Dimension(14, 24));
        plan_id_filter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                plan_id_filterKeyPressed(evt);
            }
        });

        jLabel5.setText("Plan ID");

        javax.swing.GroupLayout all_plans_jpanelLayout = new javax.swing.GroupLayout(all_plans_jpanel);
        all_plans_jpanel.setLayout(all_plans_jpanelLayout);
        all_plans_jpanelLayout.setHorizontalGroup(
            all_plans_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(all_plans_jpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(all_plans_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(all_plans_scroll_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 717, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(all_plans_jpanelLayout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(plan_id_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45)
                        .addComponent(lp_filter_val, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(refresh_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(new_plan_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(612, Short.MAX_VALUE))
        );
        all_plans_jpanelLayout.setVerticalGroup(
            all_plans_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(all_plans_jpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(all_plans_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(all_plans_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(new_plan_btn)
                        .addComponent(refresh_btn)
                        .addComponent(lp_filter_val, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5))
                    .addComponent(plan_id_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(all_plans_scroll_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 752, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(837, Short.MAX_VALUE))
        );

        current_plan_jpanel.addTab("All plans", all_plans_jpanel);

        jLabel1.setText("CPN");

        txt_filter_part.setBackground(new java.awt.Color(204, 255, 204));
        txt_filter_part.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        txt_filter_part.setToolTipText("Part Number");
        txt_filter_part.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_filter_partActionPerformed(evt);
            }
        });
        txt_filter_part.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_filter_partKeyTyped(evt);
            }
        });

        jLabel4.setText("Prod Pallet No.");

        txt_filter_pal_number.setBackground(new java.awt.Color(204, 255, 204));
        txt_filter_pal_number.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        txt_filter_pal_number.setToolTipText("Part Number");
        txt_filter_pal_number.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_filter_pal_numberKeyTyped(evt);
            }
        });

        jLabel8.setText("Dispatch Pallet No.");

        txt_filter_dispatchl_number.setBackground(new java.awt.Color(204, 255, 204));
        txt_filter_dispatchl_number.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        txt_filter_dispatchl_number.setToolTipText("Part Number");
        txt_filter_dispatchl_number.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_filter_dispatchl_numberKeyTyped(evt);
            }
        });

        piles_box.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        piles_box.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "*", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32" }));
        piles_box.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                piles_boxItemStateChanged(evt);
            }
        });

        jLabel2.setText("Position");

        controlled_combobox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ALL", "Controlled", "Not controlled" }));
        controlled_combobox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                controlled_comboboxItemStateChanged(evt);
            }
        });

        jLabel9.setText("Labels Control");

        jpanel_destinations.setBackground(new java.awt.Color(204, 204, 255));

        javax.swing.GroupLayout jpanel_destinationsLayout = new javax.swing.GroupLayout(jpanel_destinations);
        jpanel_destinations.setLayout(jpanel_destinationsLayout);
        jpanel_destinationsLayout.setHorizontalGroup(
            jpanel_destinationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 207, Short.MAX_VALUE)
        );
        jpanel_destinationsLayout.setVerticalGroup(
            jpanel_destinationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 26, Short.MAX_VALUE)
        );

        jLabel3.setText("Destination");

        set_packaging_pile_btn.setBackground(new java.awt.Color(153, 204, 255));
        set_packaging_pile_btn.setText("External Packaging");
        set_packaging_pile_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                set_packaging_pile_btnActionPerformed(evt);
            }
        });

        btn_filter_ok.setBackground(new java.awt.Color(153, 204, 255));
        btn_filter_ok.setText("Refresh");
        btn_filter_ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_filter_okActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setText("Lines");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setText("Total Qty");

        txt_totalQty.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txt_totalQty.setText("0");

        txt_nbreLigne.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txt_nbreLigne.setText("0");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(txt_filter_part, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(txt_filter_pal_number, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(txt_filter_dispatchl_number, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(piles_box, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(controlled_combobox, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jpanel_destinations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btn_filter_ok)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(set_packaging_pile_btn)
                        .addGap(319, 319, 319)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_nbreLigne, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(106, 106, 106)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(txt_totalQty, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel1))
                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel9)
                        .addComponent(jLabel2)
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(controlled_combobox)
                    .addComponent(jpanel_destinations, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(piles_box)
                    .addComponent(txt_filter_dispatchl_number)
                    .addComponent(txt_filter_pal_number)
                    .addComponent(txt_filter_part))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(set_packaging_pile_btn)
                            .addComponent(btn_filter_ok))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_nbreLigne)
                            .addComponent(jLabel6)
                            .addComponent(txt_totalQty)
                            .addComponent(jLabel7)))))
        );

        load_plan_lines_table.setAutoCreateRowSorter(true);
        load_plan_lines_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PILE NUM", "PALLET NUM", "CPN", "INTERNAL PN", "PACK TYPE", "PACK SIZE", "DESTINATION", "LINE ID", "FAMILY", "FIFO"
            }
        ));
        jScrollPane3.setViewportView(load_plan_lines_table);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1204, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 599, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(925, Short.MAX_VALUE))
        );

        current_plan_jpanel.addTab("Plan detail 1/3", jPanel3);

        jPanel1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jPanel1KeyTyped(evt);
            }
        });

        total_per_pn_table.setAutoCreateRowSorter(true);
        total_per_pn_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "DESTINATION", "CPN", "PACK TYPE", "TOTAL QTY", "TOTAL PACK"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        total_per_pn_table.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(total_per_pn_table);

        tab2_destination.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        tab2_destination.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tab2_destinationKeyTyped(evt);
            }
        });

        tab2_cpn.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        tab2_cpn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tab2_cpnKeyTyped(evt);
            }
        });

        jLabel17.setText("Destination");

        jLabel18.setText("CPN");

        tab2_packtype.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        tab2_packtype.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tab2_packtypeKeyTyped(evt);
            }
        });

        jLabel19.setText("Pack Type");

        tab2_refresh.setText("Actualiser");
        tab2_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tab2_refreshActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel20.setText("Total Qty");

        tab2_txt_totalQty.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        tab2_txt_totalQty.setText("0");

        tab2_txt_nbreLigne.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        tab2_txt_nbreLigne.setText("0");

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel21.setText("Total Packages ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 741, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17)
                            .addComponent(tab2_destination, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18)
                            .addComponent(tab2_cpn, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tab2_packtype, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(tab2_refresh))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(445, 445, 445)
                        .addComponent(jLabel20)
                        .addGap(18, 18, 18)
                        .addComponent(tab2_txt_totalQty, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tab2_txt_nbreLigne, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(590, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(tab2_refresh)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(1, 1, 1)
                            .addComponent(tab2_packtype, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                            .addGap(2, 2, 2)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel18)
                                .addComponent(jLabel19)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tab2_cpn, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                            .addComponent(tab2_destination))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(tab2_txt_nbreLigne)
                    .addComponent(jLabel21)
                    .addComponent(tab2_txt_totalQty)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 537, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1012, Short.MAX_VALUE))
        );

        current_plan_jpanel.addTab("Plan detail 2/3", jPanel1);

        jPanel2.setAutoscrolls(true);

        jLabel10.setText("Total hours");

        txt_total_hours.setEditable(false);

        txt_total_value.setEditable(false);

        jLabel11.setText("Total value");

        txt_total_volume.setEditable(false);

        jLabel12.setText("Total volume");

        jLabel13.setText("Total gross weight");

        txt_gross_weight.setEditable(false);

        jLabel14.setText("Total net weight");

        txt_total_net_weight.setEditable(false);

        jLabel16.setText("Total packaging ");

        tab3_refresh.setText("Actualiser");
        tab3_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tab3_refreshActionPerformed(evt);
            }
        });

        jtable_total_packages.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ));
        jScrollPane4.setViewportView(jtable_total_packages);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(59, 59, 59)
                                .addComponent(txt_total_hours, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txt_total_volume, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(jLabel11))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txt_total_value, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(jLabel14))
                            .addComponent(txt_gross_weight, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tab3_refresh)
                            .addComponent(txt_total_net_weight, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel16)
                    .addComponent(jScrollPane4))
                .addContainerGap(628, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_total_net_weight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(txt_total_value, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(txt_total_hours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(txt_gross_weight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13)
                        .addComponent(txt_total_volume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12))
                    .addComponent(tab3_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel16)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 542, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(987, Short.MAX_VALUE))
        );

        current_plan_jpanel.addTab("Plan detail 3/3", jPanel2);

        jSplitPane2.setRightComponent(current_plan_jpanel);

        time_label8.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        time_label8.setText("Truck Num");

        create_time_label.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        create_time_label.setText("--/--/---- --:--");

        time_label1.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        time_label1.setText("Creation date");

        time_label2.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        time_label2.setText("Loading Plan No :");

        plan_num_label.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        plan_num_label.setText("#");

        create_user_label.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        create_user_label.setText("-----");

        time_label3.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        time_label3.setText("Create user :");

        time_label4.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        time_label4.setText("Dispatch Date :");

        dispatch_date_label.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        dispatch_date_label.setText("--/--/----");

        state_label.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        state_label.setText("-----");

        time_label6.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        time_label6.setText("Status :");

        time_label7.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        time_label7.setText("Release Date :");

        release_date_label.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        release_date_label.setText("--/--/----");

        time_label9.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        time_label9.setText("Project :");

        project_label.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        project_label.setText("-----");

        truck_no_txt1.setEditable(false);
        truck_no_txt1.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        truck_no_txt1.setText("0");
        truck_no_txt1.setToolTipText("");
        truck_no_txt1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                truck_no_txt1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout details_jpanelLayout = new javax.swing.GroupLayout(details_jpanel);
        details_jpanel.setLayout(details_jpanelLayout);
        details_jpanelLayout.setHorizontalGroup(
            details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(details_jpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(details_jpanelLayout.createSequentialGroup()
                        .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(time_label4)
                            .addComponent(time_label3)
                            .addComponent(time_label6)
                            .addComponent(time_label7)
                            .addComponent(time_label2)
                            .addComponent(time_label1)
                            .addComponent(time_label8))
                        .addGap(18, 18, 18)
                        .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(details_jpanelLayout.createSequentialGroup()
                                .addComponent(truck_no_txt1)
                                .addGap(11, 11, 11))
                            .addComponent(dispatch_date_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(create_user_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(release_date_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(details_jpanelLayout.createSequentialGroup()
                                .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(state_label, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(create_time_label, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(plan_num_label))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(details_jpanelLayout.createSequentialGroup()
                        .addComponent(time_label9)
                        .addGap(18, 18, 18)
                        .addComponent(project_label, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        details_jpanelLayout.setVerticalGroup(
            details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(details_jpanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(time_label8)
                    .addComponent(truck_no_txt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(time_label2)
                    .addComponent(plan_num_label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(time_label1)
                    .addComponent(create_time_label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(time_label3)
                    .addComponent(create_user_label, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(time_label4)
                    .addComponent(dispatch_date_label))
                .addGap(18, 18, 18)
                .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(project_label)
                    .addComponent(time_label9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(state_label)
                    .addComponent(time_label6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(details_jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(time_label7)
                    .addComponent(release_date_label))
                .addContainerGap(1444, Short.MAX_VALUE))
        );

        jSplitPane2.setLeftComponent(details_jpanel);

        jButton1.setText("jButton1");

        new_plan_menu.setMnemonic(KeyEvent.VK_F5);
        new_plan_menu.setText("New Plan");
        new_plan_menu.addMenuKeyListener(new javax.swing.event.MenuKeyListener() {
            public void menuKeyPressed(javax.swing.event.MenuKeyEvent evt) {
            }
            public void menuKeyReleased(javax.swing.event.MenuKeyEvent evt) {
            }
            public void menuKeyTyped(javax.swing.event.MenuKeyEvent evt) {
                new_plan_menuMenuKeyTyped(evt);
            }
        });
        new_plan_menu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new_plan_menuMouseClicked(evt);
            }
        });
        new_plan_menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new_plan_menuActionPerformed(evt);
            }
        });
        jMenuBar1.add(new_plan_menu);

        export_plan_menu.setMnemonic(KeyEvent.VK_F5);
        export_plan_menu.setText("Export to Excel");
        export_plan_menu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                export_plan_menuMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                export_plan_menuMouseEntered(evt);
            }
        });
        export_plan_menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                export_plan_menuActionPerformed(evt);
            }
        });
        export_plan_menu.addMenuKeyListener(new javax.swing.event.MenuKeyListener() {
            public void menuKeyPressed(javax.swing.event.MenuKeyEvent evt) {
            }
            public void menuKeyReleased(javax.swing.event.MenuKeyEvent evt) {
            }
            public void menuKeyTyped(javax.swing.event.MenuKeyEvent evt) {
                export_plan_menuMenuKeyTyped(evt);
            }
        });
        jMenuBar1.add(export_plan_menu);

        edit_plan_menu.setText("Edit plan");
        edit_plan_menu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                edit_plan_menuMouseClicked(evt);
            }
        });
        jMenuBar1.add(edit_plan_menu);

        pallet_details_menu.setMnemonic(KeyEvent.VK_F7);
        pallet_details_menu.setText("Pallet details");
        pallet_details_menu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pallet_details_menuMouseClicked(evt);
            }
        });
        pallet_details_menu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                pallet_details_menuKeyTyped(evt);
            }
        });
        jMenuBar1.add(pallet_details_menu);

        plans_list_meni.setMnemonic(KeyEvent.VK_F8);
        plans_list_meni.setText("Plans list");
        plans_list_meni.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                plans_list_meniMouseClicked(evt);
            }
        });
        plans_list_meni.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                plans_list_meniKeyTyped(evt);
            }
        });
        jMenuBar1.add(plans_list_meni);

        control_dispatch_menu.setMnemonic(KeyEvent.VK_F9);
        control_dispatch_menu.setText("Dispatch Labels Control");
        control_dispatch_menu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                control_dispatch_menuMouseClicked(evt);
            }
        });
        jMenuBar1.add(control_dispatch_menu);

        delete_plan_menu.setBackground(new java.awt.Color(255, 102, 102));
        delete_plan_menu.setText("Delete plan");

        delete_plan_submenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        delete_plan_submenu.setBackground(new java.awt.Color(255, 102, 102));
        delete_plan_submenu.setText("Delete plan");
        delete_plan_submenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_plan_submenuActionPerformed(evt);
            }
        });
        delete_plan_menu.add(delete_plan_submenu);

        jMenuBar1.add(delete_plan_menu);

        close_plan_menu.setBackground(new java.awt.Color(255, 102, 102));
        close_plan_menu.setText("Close Plan");

        close_plan_submenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K, java.awt.event.InputEvent.CTRL_MASK));
        close_plan_submenu.setBackground(new java.awt.Color(255, 102, 102));
        close_plan_submenu.setText("Close Plan");
        close_plan_submenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close_plan_submenuActionPerformed(evt);
            }
        });
        close_plan_menu.add(close_plan_submenu);

        jMenuBar1.add(close_plan_menu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1600, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(442, 442, 442)
                                .addComponent(time_label5)
                                .addGap(58, 58, 58)
                                .addComponent(destination_label_help, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(pile_label_help, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(scan_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(radio_btn_20)
                                        .addGap(11, 11, 11)
                                        .addComponent(radio_btn_40)))
                                .addGap(86, 86, 86)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(connectedUserName_label, javax.swing.GroupLayout.PREFERRED_SIZE, 463, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(message_label, javax.swing.GroupLayout.PREFERRED_SIZE, 1539, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(destination_label_help)
                            .addComponent(time_label5, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pile_label_help))
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(connectedUserName_label, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(message_label, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(scan_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(radio_btn_20)
                            .addComponent(radio_btn_40))))
                .addGap(70, 70, 70)
                .addComponent(jSplitPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1673, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getAccessibleContext().setAccessibleDescription("Dispatch Module");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void scan_txtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_scan_txtKeyPressed
        // User has pressed Carriage return button
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            state.doAction(WarehouseHelper.warehouse_reserv_context);
            state = WarehouseHelper.warehouse_reserv_context.getState();

        } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            int confirmed = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to logoff ?", "Logoff confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (confirmed == 0) {
                logout();

            }
        }
    }//GEN-LAST:event_scan_txtKeyPressed

    //########################################################################
    //################ Reset GUI Component to ReservationState S01 ######################
    //########################################################################
    public void logout() {

        if (WarehouseHelper.warehouse_reserv_context.getUser().getId() != null) {
            //Save authentication line in HisLogin table
            /*
             HisLogin his_login = new HisLogin(
             WarehouseHelper.warehouse_reserv_context.getUser().getId(),
             WarehouseHelper.warehouse_reserv_context.getUser().getId(),
             String.format(Helper.INFO0012_LOGOUT_SUCCESS,
             WarehouseHelper.warehouse_reserv_context.getUser().getFirstName()
             + " " + WarehouseHelper.warehouse_reserv_context.getUser().getLastName()
             + " / " + WarehouseHelper.warehouse_reserv_context.getUser().getLogin(),
             GlobalVars.APP_HOSTNAME, GlobalMethods.getStrTimeStamp()));
             his_login.setCreateId(WarehouseHelper.warehouse_reserv_context.getUser().getId());
             his_login.setWriteId(WarehouseHelper.warehouse_reserv_context.getUser().getId());
            
            
             String str = String.format(Helper.INFO0012_LOGOUT_SUCCESS,
             WarehouseHelper.warehouse_reserv_context.getUser().getFirstName() + " " + WarehouseHelper.warehouse_reserv_context.getUser().getLastName()
             + " / " + PackagingVars.context.getUser().getLogin(), GlobalVars.APP_HOSTNAME,
             GlobalMethods.getStrTimeStamp());
             his_login.setMessage(str);

             str = "";
             his_login.create(his_login);
             */
            //Reset the state
            state = new S001_ReservPalletNumberScan();

            this.clearContextSessionVals();

            connectedUserName_label.setText("");

            this.dispose();
        }

    }

    //########################################################################
    //########################## USER LABEL METHODS ##########################
    //########################################################################
    public void setUserLabelText(String newText) {
        connectedUserName_label.setText(newText);
    }

    public JTextField getScanTxt() {
        return this.scan_txt;
    }

    public void setScanTxt(JTextField setScanTxt) {
        this.scan_txt = setScanTxt;
    }

    public void clearContextSessionVals() {
        //Pas besoin de réinitialiser le uid        
        PackagingVars.context.setUser(new ManufactureUsers());
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        int confirmed = JOptionPane.showConfirmDialog(null,
                "On quittant le programme vous perdez toutes vos données actuelles. Voulez-vous quitter ?", "Exit Program Message Box",
                JOptionPane.YES_NO_OPTION);
        if (confirmed == 0) {
            //clearGui();
            logout();

        } else {
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);//no
        }
    }//GEN-LAST:event_formWindowClosing

    private void radio_btn_40ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radio_btn_40ItemStateChanged
        loadPiles();
    }//GEN-LAST:event_radio_btn_40ItemStateChanged

    private void radio_btn_20ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radio_btn_20ItemStateChanged
        loadPiles();
    }//GEN-LAST:event_radio_btn_20ItemStateChanged

    private void formKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyTyped

    }//GEN-LAST:event_formKeyTyped


    private void scan_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scan_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_scan_txtActionPerformed

    public boolean loadPlanDataInGui(int id) {
        try {

            Helper.startSession();
            Query query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_BY_ID);
            query.setParameter("id", id);

            Helper.sess.getTransaction().commit();
            List result = query.list();
            LoadPlan plan = (LoadPlan) result.get(0);
            WarehouseHelper.temp_load_plan = plan;

            //Load destinations of the plan
            //if (loadDestinations(Integer.valueOf(id))) {
            if (loadDestinationsRadioGroup(Integer.valueOf(id))) {
                System.out.println("selectedDestination " + selectedDestination);
                loadPlanDataToLabels(plan, selectedDestination);
                reloadPlanLinesData(Integer.valueOf(id), selectedDestination);

                //Disable delete button if the plan is CLOSED
                if (WarehouseHelper.LOAD_PLAN_STATE_CLOSED.equals(plan.getPlanState())) {
                    delete_plan_menu.setEnabled(false);
                    close_plan_menu.setEnabled(false);
                    export_plan_menu.setEnabled(true);
                    edit_plan_menu.setEnabled(false);
                    set_packaging_pile_btn.setEnabled(true);
                    control_dispatch_menu.setEnabled(false);
                    piles_box.setEnabled(true);
                    scan_txt.setEnabled(false);
                    txt_filter_part.setEnabled(true);
                    radio_btn_20.setEnabled(false);
                    radio_btn_40.setEnabled(false);

                } else { // The plan still Open
                    if (WarehouseHelper.warehouse_reserv_context.getUser().getAccessLevel() == GlobalVars.PROFIL_WAREHOUSE_AGENT) {
                        delete_plan_menu.setEnabled(false);
                        close_plan_menu.setEnabled(false);
                    }
                    if (WarehouseHelper.warehouse_reserv_context.getUser().getAccessLevel() == GlobalVars.PROFIL_ADMIN) {
                        delete_plan_menu.setEnabled(true);
                        close_plan_menu.setEnabled(true);
                    }
                    export_plan_menu.setEnabled(true);
                    edit_plan_menu.setEnabled(true);
                    control_dispatch_menu.setEnabled(true);
                    set_packaging_pile_btn.setEnabled(true);
                    piles_box.setEnabled(true);
                    scan_txt.setEnabled(true);
                    txt_filter_part.setEnabled(true);
                    radio_btn_20.setEnabled(true);
                    radio_btn_40.setEnabled(true);
                }
            }

            filterPlanLines(false);
            filterPlanLines(false);
            return true;
        } catch (Exception e) {
            return false;

        }
    }

    private void export_plan_menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_export_plan_menuActionPerformed

    }//GEN-LAST:event_export_plan_menuActionPerformed

    private void export_plan_menuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_export_plan_menuMouseClicked
        exportPlanDetails();
    }//GEN-LAST:event_export_plan_menuMouseClicked

    private void edit_plan_menuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_edit_plan_menuMouseClicked
        new WAREHOUSE_DISPATCH_UI0005_EDIT_PLAN(this, true, WarehouseHelper.temp_load_plan);
    }//GEN-LAST:event_edit_plan_menuMouseClicked

    private void pallet_details_menuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pallet_details_menuMouseClicked
        new PACKAGING_UI0010_PalletDetails(this, rootPaneCheckingEnabled, true, true, true, true).setVisible(true);
    }//GEN-LAST:event_pallet_details_menuMouseClicked

    private void pallet_details_menuKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pallet_details_menuKeyTyped
        new PACKAGING_UI0010_PalletDetails(this, rootPaneCheckingEnabled, true, true, true, true).setVisible(true);
    }//GEN-LAST:event_pallet_details_menuKeyTyped

    private void plans_list_meniMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_plans_list_meniMouseClicked
        new WAREHOUSE_DISPATCH_UI0006_LIST(this, true).setVisible(true);
    }//GEN-LAST:event_plans_list_meniMouseClicked

    private void plans_list_meniKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_plans_list_meniKeyTyped
        new WAREHOUSE_DISPATCH_UI0006_LIST(this, true).setVisible(true);
    }//GEN-LAST:event_plans_list_meniKeyTyped

    private void export_plan_menuMenuKeyTyped(javax.swing.event.MenuKeyEvent evt) {//GEN-FIRST:event_export_plan_menuMenuKeyTyped
        //exportPlanDetails();
    }//GEN-LAST:event_export_plan_menuMenuKeyTyped

    private void control_dispatch_menuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_control_dispatch_menuMouseClicked

        WarehouseHelper.Label_Control_Gui = new WAREHOUSE_DISPATCH_UI0009_LABELS_CONTROL();
        ControlState _state_ = new S001_PalletNumberScan();
        WarehouseHelper.warehouse_control_context.setState(_state_);
        WarehouseHelper.Label_Control_Gui.setState(_state_);
        WarehouseHelper.warehouse_control_context.setUser(WarehouseHelper.warehouse_reserv_context.getUser());
        WarehouseHelper.Label_Control_Gui.setVisible(true);
        System.out.println("Intializing WarehouseHelper.Label_Control_Gui " + WarehouseHelper.Label_Control_Gui.toString());

    }//GEN-LAST:event_control_dispatch_menuMouseClicked

    /*    */
    private void delete_plan_submenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_plan_submenuActionPerformed
        int confirmed = JOptionPane.showConfirmDialog(null,
                "Voulez-vous supprimer le plan de chargement sélectionné ?", "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION);
        if (confirmed == 0) {
            Helper.startSession();
            Integer id = Integer.valueOf(plan_num_label.getText());
            Query query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_LINE_BY_PLAN_ID)
                    .setParameter("loadPlanId", id);
            Helper.sess.getTransaction().commit();
            List result = query.list();
            int lines = result.size();
            System.out.println("Le plan " + id + " contient " + lines + " Lines");

            if (lines == 0) {
                Helper.startSession();
                query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_BY_ID);
                query.setParameter("id", id);
                Helper.sess.getTransaction().commit();
                result = query.list();
                LoadPlan plan = (LoadPlan) result.get(0);

                //############ REMOVE THE PLAN LINES ###############
                query = Helper.sess.createQuery(HQLHelper.DEL_LOAD_PLAN_LINE_BY_PLAN_ID);
                query.setParameter("load_plan_id", plan.getId());
                query.executeUpdate();

                plan.delete(plan);

                //Reload Load Plan list
                this.reloadPlansData();

                //Reset Load Plan Lines table
                this.reset_load_plan_lines_table_content();

                clearGui();
                //Go back to step S020
                state = new S001_ReservPalletNumberScan();
                WarehouseHelper.warehouse_reserv_context.setState(state);
            } else {
                UILog.severeDialog(null, ErrorMsg.APP_ERR0023);
                UILog.severe("Total lines = " + lines);
            }

        }
    }//GEN-LAST:event_delete_plan_submenuActionPerformed

    private void close_plan_submenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_plan_submenuActionPerformed
        System.out.println("WarehouseHelper.temp_load_plan.getTruckNo() " + WarehouseHelper.temp_load_plan.getTruckNo());

        if (WarehouseHelper.temp_load_plan.getTruckNo() == null || WarehouseHelper.temp_load_plan.getTruckNo().length() == 0) {
            UILog.severe(ErrorMsg.APP_ERR0032[0], plan_num_label.getText());
            UILog.severeDialog(null, ErrorMsg.APP_ERR0032, plan_num_label.getText());
        } else {

            int confirmed = JOptionPane.showConfirmDialog(null,
                    "Confirmez-vous la fin du chargement N° " + plan_num_label.getText() + " ?", "Fin du chargement",
                    JOptionPane.YES_NO_OPTION);
            if (confirmed == 0) {
                Helper.startSession();
                Query query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_LINE_BY_PLAN_ID);
                query.setParameter("loadPlanId", Integer.valueOf(plan_num_label.getText()));
                Helper.sess.getTransaction().commit();
                List result = query.list();
                if (!result.isEmpty()) {
                    //Initialize progress property.

                    close_plan_menu.setEnabled(false);
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    for (Object obj : result) {
                        LoadPlanLine line = (LoadPlanLine) obj;
                        
                        BaseContainer bc = new BaseContainer().getBaseContainer(line.getPalletNumber());
                        bc.setContainerState(GlobalVars.PALLET_DISPATCHED);
                        bc.setContainerStateCode(GlobalVars.PALLET_DISPATCHED_CODE);
                        bc.setDispatchTime(new Date());
                        bc.setFifoTime(new Date());
                        bc.setDestination(line.getDestinationWh());
                        bc.update(bc);

                        if (GlobalVars.APP_PROP.getProperty("BOOK_PACKAGING") == null || "".equals(GlobalVars.APP_PROP.getProperty("BOOK_PACKAGING").toString())) {
                            JOptionPane.showMessageDialog(null, "Propriété BOOK_PACKAGING non spécifiée dans le "
                                    + "fichier des propriétées !", "Erreur propriétés", JOptionPane.ERROR_MESSAGE);
                        } else if (bc.getWarehouse() == null || "".equals(bc.getWarehouse().toString())) {
                            JOptionPane.showMessageDialog(null, "Finish good warehouse non spécifié pour cette pallete " + bc.getPalletNumber(), "Erreur propriétés", JOptionPane.ERROR_MESSAGE);
                        } else if ("1".equals(GlobalVars.APP_PROP.getProperty("BOOK_PACKAGING").toString())) {
                            //Book packaging items                    
                            PackagingStockMovement pm = new PackagingStockMovement();
                            pm.bookMasterPack(
                                    WarehouseHelper.warehouse_reserv_context.getUser().getFirstName() + " " + WarehouseHelper.warehouse_reserv_context.getUser().getLastName(),
                                    bc.getPackType(),
                                    1,
                                    "OUT",
                                    bc.getWarehouse(),
                                    line.getDestinationWh(),
                                    "Dispatch in plan " + plan_num_label.getText(),
                                    line.getPalletNumber()
                            );
                        }
                    }
                    //Loop on packaging supplementaire et déduire les quantitées.
                    if ("1".equals(GlobalVars.APP_PROP.getProperty("BOOK_PACKAGING").toString())) {
                        Helper.startSession();
                        query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_PACKAGING_BY_PLAN_ID);
                        query.setParameter("loadPlanId", Integer.valueOf(plan_num_label.getText()));
                        Helper.sess.getTransaction().commit();
                        result = query.list();
                        for (Object obj : result) {
                            LoadPlanLinePackaging line = (LoadPlanLinePackaging) obj;
                            PackagingStockMovement transaction
                                    = new PackagingStockMovement(
                                            line.getPackItem(),
                                            "",
                                            plan_num_label.getText(),
                                            WarehouseHelper.warehouse_reserv_context.getUser().getFirstName() + " "
                                            + WarehouseHelper.warehouse_reserv_context.getUser().getLastName(),
                                            new Date(),
                                            GlobalVars.APP_PROP.getProperty("WH_PACKAGING"),
                                            -Float.valueOf(line.getQty()),
                                            "Packaging Supplementaire. " + line.getComment());
                            transaction.create(transaction);
                        }
                    }
                    Helper.startSession();
                    WarehouseHelper.temp_load_plan.setPlanState(WarehouseHelper.LOAD_PLAN_STATE_CLOSED);
                    WarehouseHelper.temp_load_plan.setEndTime(new Date());
                    WarehouseHelper.temp_load_plan.update(WarehouseHelper.temp_load_plan);

                    clearGui();

                    //Refresh Data
                    reloadPlansData();

                    //Go back to step S020
                    state = new S001_ReservPalletNumberScan();
                    WarehouseHelper.warehouse_reserv_context.setState(state);

                    Toolkit.getDefaultToolkit().beep();
                    setCursor(null);
                    JOptionPane.showMessageDialog(null, "Consignement closed !\n");
                    //UILog.infoDialog(null, new String["Plan released !\n");                
                } else {
                    UILog.severe(ErrorMsg.APP_ERR0030[0], plan_num_label.getText());
                    UILog.severeDialog(null, ErrorMsg.APP_ERR0030, plan_num_label.getText());

                }

            }

        }
    }//GEN-LAST:event_close_plan_submenuActionPerformed

    private void new_plan_menuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_new_plan_menuMouseClicked
        new WAREHOUSE_DISPATCH_UI0004_NEW_PLAN(this, true);
    }//GEN-LAST:event_new_plan_menuMouseClicked

    private void new_plan_menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_new_plan_menuActionPerformed
        new WAREHOUSE_DISPATCH_UI0004_NEW_PLAN(this, true);
    }//GEN-LAST:event_new_plan_menuActionPerformed

    private void new_plan_menuMenuKeyTyped(javax.swing.event.MenuKeyEvent evt) {//GEN-FIRST:event_new_plan_menuMenuKeyTyped
        new WAREHOUSE_DISPATCH_UI0004_NEW_PLAN(this, true);
    }//GEN-LAST:event_new_plan_menuMenuKeyTyped

    private void message_labelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_message_labelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_message_labelActionPerformed

    private void btn_filter_okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_filter_okActionPerformed
        filterPlanLines(false);
    }//GEN-LAST:event_btn_filter_okActionPerformed

    private void set_packaging_pile_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_set_packaging_pile_btnActionPerformed
        new WAREHOUSE_DISPATCH_UI0008_SET_PACKAGING_OF_PILE(this, true, WarehouseHelper.temp_load_plan, selectedDestination);
    }//GEN-LAST:event_set_packaging_pile_btnActionPerformed

    private void controlled_comboboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_controlled_comboboxItemStateChanged
        filterPlanLines(false);
    }//GEN-LAST:event_controlled_comboboxItemStateChanged

    private void piles_boxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_piles_boxItemStateChanged
        //Set the values of destination and pile labels help
        try {
            destination_label_help.setText(selectedDestination);
            pile_label_help.setText(piles_box.getSelectedItem().toString());
            filterPlanLines(false);
        } catch (Exception e) {
        }
    }//GEN-LAST:event_piles_boxItemStateChanged

    private void txt_filter_dispatchl_numberKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_filter_dispatchl_numberKeyTyped
        filterPlanLines(false);
    }//GEN-LAST:event_txt_filter_dispatchl_numberKeyTyped

    private void txt_filter_pal_numberKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_filter_pal_numberKeyTyped
        filterPlanLines(false);
    }//GEN-LAST:event_txt_filter_pal_numberKeyTyped

    private void txt_filter_partKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_filter_partKeyTyped
        filterPlanLines(false);
    }//GEN-LAST:event_txt_filter_partKeyTyped

    private void txt_filter_partActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_filter_partActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_filter_partActionPerformed

    private void plan_id_filterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_plan_id_filterKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                if (loadPlanDataInGui(Integer.valueOf(plan_id_filter.getText()))) {
                    current_plan_jpanel.setSelectedIndex(1);
                } else {
                    JOptionPane.showOptionDialog(null, "Plan " + plan_id_filter.getText() + " introuvable !", "Plan introuvable  !", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{}, null);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showOptionDialog(null, "Plan " + plan_id_filter.getText() + " introuvable !", "Plan introuvable  !", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{}, null);
            }
        }
    }//GEN-LAST:event_plan_id_filterKeyPressed

    private void lp_filter_valKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lp_filter_valKeyTyped
        reloadPlansData();
    }//GEN-LAST:event_lp_filter_valKeyTyped

    private void lp_filter_valActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lp_filter_valActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lp_filter_valActionPerformed

    private void refresh_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refresh_btnActionPerformed
        reloadPlansData();
    }//GEN-LAST:event_refresh_btnActionPerformed

    private void new_plan_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_new_plan_btnActionPerformed
        new WAREHOUSE_DISPATCH_UI0004_NEW_PLAN(this, true);
    }//GEN-LAST:event_new_plan_btnActionPerformed

    private void truck_no_txt1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_truck_no_txt1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_truck_no_txt1ActionPerformed

    /**
     *
     * @param destination
     * @param pn
     * @param pack_type
     */
    public void total_per_part_and_destination(String destination, String pn, String pack_type) {
        
        
        tab2_txt_nbreLigne.setText("0");
        tab2_txt_totalQty.setText("0");
        total_per_dest_table_data = new Vector();
        total_per_dest_table_data_header = new Vector();
        //Init table header
        total_per_dest_table_data_header.add("DESTINATION");
        total_per_dest_table_data_header.add("CPN");
        total_per_dest_table_data_header.add("SPN");
        total_per_dest_table_data_header.add("PACK TYPE");
        total_per_dest_table_data_header.add("UCS");
        total_per_dest_table_data_header.add("TOTAL QTY");
        total_per_dest_table_data_header.add("TOTAL PACKS");

        DefaultTableModel dataModel = new DefaultTableModel(total_per_dest_table_data, total_per_dest_table_data_header);
        total_per_pn_table.setModel(dataModel);

        Helper.startSession();

        String query_str = "SELECT\n"
                + "line.destination_wh AS destination_wh,\n"
                + "line.harness_part AS harness_part,\n"
                + "line.supplier_part AS supplier_part,\n"
                + "line.pack_type AS pack_type,\n"
                + "line.qty AS qty,\n"
                + "SUM(line.qty) AS total_qty,    \n"
                + "COUNT(*) AS total_pack\n"
                + "FROM load_plan_line line\n"
                + "WHERE load_plan_id = '"+plan_num_label.getText()+"' \n";
        if(destination != null && !destination.isEmpty()){
            query_str += " AND destination_wh LIKE '%"+destination.toUpperCase()+"%'";
        }
        if(pn != null && !pn.isEmpty()){
            query_str += " AND harness_part LIKE '%"+pn+"%'";
        }
        if(pack_type != null && !pack_type.isEmpty()){
            query_str += " AND pack_type LIKE '%"+pack_type.toUpperCase()+"%'";
        }
        query_str += " GROUP BY destination_wh, harness_part, pack_type, qty, supplier_part\n"
                + "ORDER BY destination_wh ASC, pack_type DESC;";

//        SQLQuery query = Helper.sess.createSQLQuery(
//                String.format(query_str, plan_num_label.getText()));
        SQLQuery query = Helper.sess.createSQLQuery(query_str);
        
        System.out.println("Query "+query_str);
        
        query.addScalar("destination_wh", StandardBasicTypes.STRING)
                .addScalar("harness_part", StandardBasicTypes.STRING)
                .addScalar("supplier_part", StandardBasicTypes.STRING)
                .addScalar("pack_type", StandardBasicTypes.STRING)
                .addScalar("qty", StandardBasicTypes.DOUBLE)
                .addScalar("total_qty", StandardBasicTypes.DOUBLE)
                .addScalar("total_pack", StandardBasicTypes.INTEGER);

        List<Object[]> result = query.list();
        Helper.sess.getTransaction().commit();
        float total_packs = 0;
        float total_qty = 0;
        
        for (Object[] obj : result) {
            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add((String) obj[0]);
            oneRow.add((String) obj[1]);
            oneRow.add((String) obj[2]);
            oneRow.add((String) obj[3]);
            oneRow.add(String.format("%1$,.2f", obj[4]));
            oneRow.add(String.format("%1$,.2f", obj[5]));
            oneRow.add(String.format("%d", obj[6]));
            
            System.out.println("one row "+oneRow.toString());
            
            total_per_dest_table_data.add(oneRow);
            
            total_qty += Float.valueOf(obj[5].toString());
            total_packs += Float.valueOf(String.format("%d", obj[6]));
        }
        tab2_txt_nbreLigne.setText(total_packs+"");
        tab2_txt_totalQty.setText(total_qty+"");
        total_per_pn_table.setModel(new DefaultTableModel(total_per_dest_table_data, total_per_dest_table_data_header));

    }

    private void tab2_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tab2_refreshActionPerformed
        total_per_part_and_destination(tab2_destination.getText().trim(), tab2_cpn.getText().trim(), tab2_packtype.getText().trim());
    }//GEN-LAST:event_tab2_refreshActionPerformed

    private void export_plan_menuMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_export_plan_menuMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_export_plan_menuMouseEntered

    private void current_plan_jpanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_current_plan_jpanelMouseClicked
        
    }//GEN-LAST:event_current_plan_jpanelMouseClicked

    private void tab2_destinationKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tab2_destinationKeyTyped
        total_per_part_and_destination(tab2_destination.getText().trim(), tab2_cpn.getText().trim(), tab2_packtype.getText().trim());
    }//GEN-LAST:event_tab2_destinationKeyTyped

    private void tab2_cpnKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tab2_cpnKeyTyped
        total_per_part_and_destination(tab2_destination.getText().trim(), tab2_cpn.getText().trim(), tab2_packtype.getText().trim());

    }//GEN-LAST:event_tab2_cpnKeyTyped

    private void jPanel1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanel1KeyTyped
        total_per_part_and_destination(tab2_destination.getText().trim(), tab2_cpn.getText().trim(), tab2_packtype.getText().trim());
    }//GEN-LAST:event_jPanel1KeyTyped

    private void tab2_packtypeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tab2_packtypeKeyTyped
        total_per_part_and_destination(tab2_destination.getText().trim(), tab2_cpn.getText().trim(), tab2_packtype.getText().trim());
    }//GEN-LAST:event_tab2_packtypeKeyTyped

    private void tab3_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tab3_refreshActionPerformed
        this.reloadPackagingContainerTab3(Integer.valueOf(plan_num_label.getText()));
    }//GEN-LAST:event_tab3_refreshActionPerformed

    private void clearGui() {

        this.cleanDataLabels();

        //Clear mode2_context temp vars
        WarehouseHelper.warehouse_reserv_context.clearAllVars();

        //Clear lines from Jtable
        reset_load_plan_lines_table_content();

        //Disable delete button
        delete_plan_menu.setEnabled(false);

        //Disable End load button
        close_plan_menu.setEnabled(false);

        //Disable Export Excel button
        export_plan_menu.setEnabled(false);

        //Disable Edit plan button
        edit_plan_menu.setEnabled(false);

        control_dispatch_menu.setEnabled(false);

//        destinations_box.setEnabled(false);
        piles_box.setEnabled(false);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel all_plans_jpanel;
    private javax.swing.JScrollPane all_plans_scroll_panel;
    private javax.swing.JButton btn_filter_ok;
    private javax.swing.JMenu close_plan_menu;
    private javax.swing.JMenuItem close_plan_submenu;
    private javax.swing.JLabel connectedUserName_label;
    private javax.swing.JMenu control_dispatch_menu;
    private javax.swing.JComboBox controlled_combobox;
    private javax.swing.JLabel create_time_label;
    private javax.swing.JLabel create_user_label;
    private javax.swing.JTabbedPane current_plan_jpanel;
    private javax.swing.JMenu delete_plan_menu;
    private javax.swing.JMenuItem delete_plan_submenu;
    private javax.swing.JLabel destination_label_help;
    private javax.swing.JPanel details_jpanel;
    private javax.swing.JLabel dispatch_date_label;
    private javax.swing.JMenu edit_plan_menu;
    private javax.swing.JMenu export_plan_menu;
    private javax.swing.JButton jButton1;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JPanel jpanel_destinations;
    private javax.swing.JTable jtable_total_packages;
    private javax.swing.JTable load_plan_lines_table;
    private javax.swing.JTable load_plan_table;
    private javax.swing.JTextField lp_filter_val;
    private javax.swing.JTextField message_label;
    private javax.swing.JButton new_plan_btn;
    private javax.swing.JMenu new_plan_menu;
    private javax.swing.JMenu pallet_details_menu;
    private javax.swing.JLabel pile_label_help;
    private javax.swing.JComboBox piles_box;
    private javax.swing.JFormattedTextField plan_id_filter;
    private javax.swing.JLabel plan_num_label;
    private javax.swing.JMenu plans_list_meni;
    private javax.swing.JLabel project_label;
    private javax.swing.JRadioButton radio_btn_20;
    private javax.swing.JRadioButton radio_btn_40;
    private javax.swing.JButton refresh_btn;
    private javax.swing.JLabel release_date_label;
    private javax.swing.JTextField scan_txt;
    private javax.swing.JButton set_packaging_pile_btn;
    private javax.swing.JLabel state_label;
    private javax.swing.JTextField tab2_cpn;
    private javax.swing.JTextField tab2_destination;
    private javax.swing.JTextField tab2_packtype;
    private javax.swing.JButton tab2_refresh;
    private javax.swing.JLabel tab2_txt_nbreLigne;
    private javax.swing.JLabel tab2_txt_totalQty;
    private javax.swing.JButton tab3_refresh;
    private javax.swing.JLabel time_label1;
    private javax.swing.JLabel time_label2;
    private javax.swing.JLabel time_label3;
    private javax.swing.JLabel time_label4;
    private javax.swing.JLabel time_label5;
    private javax.swing.JLabel time_label6;
    private javax.swing.JLabel time_label7;
    private javax.swing.JLabel time_label8;
    private javax.swing.JLabel time_label9;
    private javax.swing.JTable total_per_pn_table;
    private javax.swing.JTextField truck_no_txt1;
    private javax.swing.JTextField txt_filter_dispatchl_number;
    private javax.swing.JTextField txt_filter_pal_number;
    private javax.swing.JTextField txt_filter_part;
    private javax.swing.JTextField txt_gross_weight;
    private javax.swing.JLabel txt_nbreLigne;
    private javax.swing.JLabel txt_totalQty;
    private javax.swing.JTextField txt_total_hours;
    private javax.swing.JTextField txt_total_net_weight;
    private javax.swing.JTextField txt_total_value;
    private javax.swing.JTextField txt_total_volume;
    // End of variables declaration//GEN-END:variables

    public void loadPlanDataInGui() {
        String id = plan_num_label.getText();
        Helper.startSession();
        Query query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_BY_ID);
        query.setParameter("id", Integer.valueOf(id));

        Helper.sess.getTransaction().commit();
        List result = query.list();
        LoadPlan plan = (LoadPlan) result.get(0);
        WarehouseHelper.temp_load_plan = plan;
        loadPlanDataToLabels(plan, "");
        reloadPlanLinesData(Integer.valueOf(id), null);
        //loadDestinations(Integer.valueOf(id));
        loadDestinationsRadioGroup(Integer.valueOf(id));
        //Disable delete button if the plan is CLOSED
        if (WarehouseHelper.LOAD_PLAN_STATE_CLOSED.equals(plan.getPlanState())) {
            delete_plan_menu.setEnabled(false);
            close_plan_menu.setEnabled(false);
            export_plan_menu.setEnabled(true);
            edit_plan_menu.setEnabled(false);
            control_dispatch_menu.setEnabled(false);
            piles_box.setEnabled(false);
            set_packaging_pile_btn.setEnabled(false);
        } else {

            if (WarehouseHelper.warehouse_reserv_context.getUser().getAccessLevel() == GlobalVars.PROFIL_WAREHOUSE_AGENT) {
                delete_plan_menu.setEnabled(false);
                close_plan_menu.setEnabled(false);
            }
            if (WarehouseHelper.warehouse_reserv_context.getUser().getAccessLevel() == GlobalVars.PROFIL_ADMIN) {
                delete_plan_menu.setEnabled(true);
                close_plan_menu.setEnabled(true);
            }
            control_dispatch_menu.setEnabled(true);
            export_plan_menu.setEnabled(true);
            edit_plan_menu.setEnabled(true);
            piles_box.setEnabled(true);
            set_packaging_pile_btn.setEnabled(true);
            scan_txt.setEnabled(true);
            radio_btn_20.setEnabled(true);
            radio_btn_40.setEnabled(true);
        }
    }

    /**
     * Filter the plan lines according to the give values
     *
     * @param pass
     */
    public void filterPlanLines(boolean pass) {

        if (!"*".equals(piles_box.getSelectedItem().toString())) {
            try {
                int pile = Integer.parseInt(piles_box.getSelectedItem().toString());
                filterPlanLines(
                        Integer.valueOf(plan_num_label.getText()),
                        selectedDestination,
                        txt_filter_part.getText().trim(),
                        pile,
                        controlled_combobox.getSelectedIndex(),
                        txt_filter_pal_number.getText().trim(),
                        txt_filter_dispatchl_number.getText().trim());

            } catch (NumberFormatException e) {
                filterPlanLines(
                        Integer.valueOf(plan_num_label.getText()),
                        selectedDestination,
                        txt_filter_part.getText().trim(),
                        0,
                        controlled_combobox.getSelectedIndex(),
                        txt_filter_pal_number.getText().trim(),
                        txt_filter_dispatchl_number.getText().trim());
            }
        } else {
            if (!plan_num_label.getText().equals("#")) {
                filterPlanLines(
                        Integer.valueOf(plan_num_label.getText()),
                        selectedDestination,
                        txt_filter_part.getText().trim(), 0, controlled_combobox.getSelectedIndex(),
                        txt_filter_pal_number.getText().trim(),
                        txt_filter_dispatchl_number.getText().trim());
//                filterPlanLines(
//                        Integer.valueOf(plan_num_label.getText()),
//                        selectedDestination,
//                        txt_filter_part.getText(), 0, controlled_combobox.getSelectedIndex(),
//                        txt_filter_pal_number.getText().trim(),
//                        txt_filter_dispatchl_number.getText().trim());
            }
        }
    }

    private void exportPlanDetails() {
        if (plan_num_label.getText().equals("#")) {
            UILog.severeDialog(this, ErrorMsg.APP_ERR0031);
            return;
        }

        //Create the excel workbook
        Workbook wb = new HSSFWorkbook();
        Sheet sheet1 = wb.createSheet("PILES_DETAILS");
        Sheet sheet2 = wb.createSheet("PILES_GROUPED");
        Sheet sheet4 = wb.createSheet("TOTAL_PACKAGING");
        Sheet sheet5 = wb.createSheet("TOTAL_STOCK_PER_DESTINATION");
        CreationHelper createHelper = wb.getCreationHelper();

        //######################################################################
        //##################### SHEET 1 : PILES DETAILS ########################
        //Initialiser les entête du fichier
        // Create a row and put some cells in it. Rows are 0 based.
        Row row = sheet1.createRow((short) 0);
        // Create a cell and put a value in it.    
        //N° LINE
        row.createCell(0).setCellValue("PILE NUM");
        row.createCell(1).setCellValue("PALLET NUM");
        row.createCell(2).setCellValue("CUSTOMER PN");
        row.createCell(3).setCellValue("INTERNAL PN");
        row.createCell(4).setCellValue("PACK TYPE");
        row.createCell(5).setCellValue("QTY");
        row.createCell(6).setCellValue("ORDER NUM");
        row.createCell(7).setCellValue("TYPE");
        row.createCell(8).setCellValue("DISPATCH LABEL NO");
        row.createCell(9).setCellValue("DESTINATION");
        row.createCell(10).setCellValue("N° LINE");
        //Load lines of the actual loading plan
        Helper.startSession();
        Query query = Helper.sess.createQuery(HQLHelper.GET_LOAD_PLAN_LINE_BY_PLAN_ID_ASC);
        query.setParameter("loadPlanId", Integer.valueOf(plan_num_label.getText()));

        Helper.sess.getTransaction().commit();
        List result = query.list();

        short sheetPointer = 1;

        for (Object o : result) {
            LoadPlanLine lpl = (LoadPlanLine) o;
            row = sheet1.createRow(sheetPointer);
            row.createCell(0).setCellValue(lpl.getPileNum());
            row.createCell(1).setCellValue(Integer.valueOf(lpl.getPalletNumber()));
            try {
                row.createCell(2).setCellValue(Integer.valueOf(lpl.getHarnessPart()));
            } catch (NumberFormatException e) {
                row.createCell(2).setCellValue(lpl.getHarnessPart());
            }
            row.createCell(3).setCellValue(lpl.getSupplierPart());
            row.createCell(4).setCellValue(lpl.getPackType());
            row.createCell(5).setCellValue(lpl.getQty());
            row.createCell(6).setCellValue(lpl.getOrderNum());
            row.createCell(7).setCellValue(lpl.getHarnessType());
            row.createCell(8).setCellValue((lpl.getDispatchLabelNo().startsWith(GlobalVars.DISPATCH_SERIAL_NO_PREFIX)) ? lpl.getDispatchLabelNo().substring(0) : lpl.getDispatchLabelNo());
            row.createCell(9).setCellValue(lpl.getDestinationWh());
            row.createCell(10).setCellValue(lpl.getId());
            sheetPointer++;
        }

        //######################################################################
        //##################### SHEET 2 : PILES SUMMARY ########################
        short sheet2Pointer = 0;

        Row row2 = sheet2.createRow(sheet2Pointer);
        sheet2Pointer++;
        // Create a cell and put a value in it.    

        row2.createCell(0).setCellValue("TYPE");
        row2.createCell(1).setCellValue("PILE NUM");
        row2.createCell(2).setCellValue("CUSTOMER PN");
        row2.createCell(3).setCellValue("INDEX");
        row2.createCell(4).setCellValue("LEONI PN");
        row2.createCell(5).setCellValue("TOTAL QTY");
        row2.createCell(6).setCellValue("UCS QTY");
        row2.createCell(7).setCellValue("PACK TYPE");
        row2.createCell(8).setCellValue("NBRE PACK");
        row2.createCell(9).setCellValue("ORDER NUM");
        row2.createCell(10).setCellValue("DESTINATION");

        Helper.startSession();

        SQLQuery query2 = Helper.sess.createSQLQuery(String.format(HQLHelper.GET_LOAD_PLAN_LINE_GROUPED_BY_PILES, plan_num_label.getText()));

        query2.addScalar("harness_type", StandardBasicTypes.STRING) //0
                .addScalar("pile_num", StandardBasicTypes.INTEGER) //1
                .addScalar("harness_part", StandardBasicTypes.STRING)//2
                .addScalar("harness_index", StandardBasicTypes.STRING)//3
                .addScalar("supplier_part", StandardBasicTypes.STRING)//4
                .addScalar("total_qty", StandardBasicTypes.INTEGER)//5
                .addScalar("qty", StandardBasicTypes.INTEGER)//6
                .addScalar("pack_type", StandardBasicTypes.STRING)//7
                .addScalar("nbre_pack", StandardBasicTypes.INTEGER)//8
                .addScalar("order_num", StandardBasicTypes.STRING)//9
                .addScalar("destination_wh", StandardBasicTypes.STRING);//10

        List<Object[]> result2 = query2.list();
        Helper.sess.getTransaction().commit();
        for (Object[] obj : result2) {
            row2 = sheet2.createRow(sheet2Pointer);

            row2.createCell(0).setCellValue((String) obj[0]);
            row2.createCell(1).setCellValue((Integer) obj[1]);
            row2.createCell(2).setCellValue((String) obj[2]);
            row2.createCell(3).setCellValue((String) obj[3]);
            row2.createCell(4).setCellValue((String) obj[4]);
            row2.createCell(5).setCellValue((Integer) obj[5]);
            row2.createCell(6).setCellValue((Integer) obj[6]);
            row2.createCell(7).setCellValue((String) obj[7]);
            row2.createCell(8).setCellValue((Integer) obj[8]);
            try {
                row2.createCell(9).setCellValue(Integer.valueOf(obj[9].toString()));
            } catch (Exception e) {
                row2.createCell(9).setCellValue("");
            }
            row2.createCell(10).setCellValue((String) obj[10]);
            sheet2Pointer++;
        }

        //######################################################################
        //##################### SHEET 4 : PACKAGIN MOUVEMENTS ########################
        short sheet4Pointer = 0;

        Row row4 = sheet4.createRow(sheet4Pointer);
        sheet4Pointer++;
        // Create a cell and put a value in it.    

        row4.createCell(0).setCellValue("DESTINATION");
        row4.createCell(1).setCellValue("PACK INTEM");
        row4.createCell(2).setCellValue("QTY");

        Helper.startSession();
        String query_str = String.format(
                HQLHelper.GET_LOAD_PLAN_EXT_PACKAGING_AND_CONTAINER,
                Integer.valueOf(this.plan_num_label.getText()), Integer.valueOf(this.plan_num_label.getText()));
        SQLQuery query4 = Helper.sess.createSQLQuery(query_str);

        query4.addScalar("destination", StandardBasicTypes.STRING);
        query4.addScalar("pack_item", StandardBasicTypes.STRING);
        query4.addScalar("quantity", StandardBasicTypes.DOUBLE);

        List<Object[]> result4 = query4.list();
        Helper.sess.getTransaction().commit();

        for (Object[] obj : result4) {
            row4 = sheet4.createRow(sheet4Pointer);
            row4.createCell(0).setCellValue((String) obj[0]);
            row4.createCell(1).setCellValue((String) obj[1]);
            row4.createCell(2).setCellValue((Double) obj[2]);
            sheet4Pointer++;
        }
        /*
         ####################SHEET 5 : LABEST MASK ########################*/
        short sheet5Pointer = 0;

        Row row5 = sheet5.createRow(sheet5Pointer);
        sheet5Pointer++;
        // Create a cell and put a value in it.    

        row5.createCell(0).setCellValue("TYPE");
        row5.createCell(1).setCellValue("CPN");
        row5.createCell(2).setCellValue("INDEX");
        row5.createCell(3).setCellValue("LPN");
        row5.createCell(4).setCellValue("TOTAL QTY");
        row5.createCell(7).setCellValue("ORDER NUM");
        row5.createCell(8).setCellValue("DESTINATION");

        Helper.startSession();

        SQLQuery query5 = Helper.sess.createSQLQuery(String.format(HQLHelper.GET_LOAD_PLAN_STOCK_PER_FDP, plan_num_label.getText()));
        query5.addScalar("harness_type", StandardBasicTypes.STRING)
                .addScalar("harness_part", StandardBasicTypes.STRING)
                .addScalar("harness_index", StandardBasicTypes.STRING)
                .addScalar("supplier_part", StandardBasicTypes.STRING)
                .addScalar("total_qty", StandardBasicTypes.INTEGER)
                .addScalar("order_num", StandardBasicTypes.STRING)
                .addScalar("destination_wh", StandardBasicTypes.STRING);

        List<Object[]> result5 = query5.list();
        Helper.sess.getTransaction().commit();
        for (Object[] obj : result5) {
            row5 = sheet5.createRow(sheet5Pointer);
            row5.createCell(0).setCellValue((String) obj[0]);
            row5.createCell(1).setCellValue((String) obj[1]);
            row5.createCell(2).setCellValue((String) obj[2]);
            row5.createCell(3).setCellValue((String) obj[3]);
            row5.createCell(4).setCellValue((Integer) obj[4]);
            try {
                row5.createCell(5).setCellValue((Integer) obj[5]);
            } catch (Exception e) {
                row5.createCell(5).setCellValue("");
            }
            row5.createCell(6).setCellValue((String) obj[6]);
            sheet5Pointer++;
        }

        //Past the workbook to the file chooser
        new JDialogExcelFileChooser(this, true, wb).setVisible(true);
    }
}
