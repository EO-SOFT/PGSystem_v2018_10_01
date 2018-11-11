/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.packaging.reports;

import __main__.GlobalVars;
import entity.ConfigProject;
import entity.ConfigSegment;
import entity.ConfigWorkplace;
import helper.ComboItem;
import helper.Helper;
import helper.JDialogExcelFileChooser;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.type.StandardBasicTypes;
import ui.UILog;
import ui.error.ErrorMsg;

/**
 *
 * @author Administrator
 */
public class PACKAGING_UI0011_ProdStatistics extends javax.swing.JFrame {

    Vector<String> declared_result_table_header = new Vector<String>();
    Vector declared_result_table_data = new Vector();

    private List<Object[]> declaredResultList;

    List<Object> projects = new ArrayList<Object>();
    List<Object> segments = new ArrayList<Object>();
    List<Object> workplaces = new ArrayList<Object>();

    SimpleDateFormat timeDf = new SimpleDateFormat("HH:mm");
    SimpleDateFormat dateDf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dateTimeDf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    String startTimeStr = "";
    String endTimeStr = "";
    String startDateStr = null;
    String endDateStr = null;
    String harness_part = "";

    ButtonGroup radioGroup = new ButtonGroup();

    /**
     * Creates new form UI0011_ProdStatistics_
     */
    public PACKAGING_UI0011_ProdStatistics(java.awt.Frame parent, boolean modal) {
        //super(parent, modal);
        initComponents();
        initTimeSpinners();
        initProjectFilter();
        //initSegmentFilter();
        this.workplace_filter.setEnabled(false);
        radioGroup.add(radio_all_harness);
        radioGroup.add(radio_filled_ucs);
        //initWorkplaceFilter();
        this.reset_tables_content();
        this.refresh();
        //Helper.centerJDialog(this);
        Helper.centerJFrame(this);
    }

    private void initProjectFilter() {
        List result = new ConfigProject().selectCustomers();
        if (result.isEmpty()) {
            UILog.severeDialog(this, ErrorMsg.APP_ERR0035);
            UILog.severe(ErrorMsg.APP_ERR0035[1]);
        } else { //Map project data in the list
            project_filter.removeAllItems();
            project_filter.addItem(new ComboItem("ALL", "ALL"));
            for (Object o : result) {
                project_filter.addItem(new ComboItem(o.toString(), o.toString()));
            }
        }
    }

//    private void initSegmentFilter() {
//        List result = new ConfigSegment().select();
//        if (result.isEmpty()) {
//            JOptionPane.showMessageDialog(null, Helper.ERR0026_NO_SEGMENT_FOUND, "Configuration error !", ERROR_MESSAGE);
//            System.err.println(Helper.ERR0026_NO_SEGMENT_FOUND);
//        } else { //Map project data in the list
//            for (Object o : result) {
//                ConfigSegment cp = (ConfigSegment) o;
//                segment_filter.addItem(new ComboItem(cp.getSegment(), cp.getSegment()));
//            }
//        }
//    }
//
//    private void initWorkplaceFilter() {
//        List result = new ConfigWorkplace().select();
//        if (result.isEmpty()) {
//            JOptionPane.showMessageDialog(null, Helper.ERR0027_NO_WORKPLACE_FOUND, "Configuration error !", ERROR_MESSAGE);
//            System.err.println(Helper.ERR0027_NO_WORKPLACE_FOUND);
//        } else { //Map project data in the list
//            for (Object o : result) {
//                ConfigWorkplace cp = (ConfigWorkplace) o;
//                workplace_filter.addItem(new ComboItem(cp.getWorkplace(), cp.getWorkplace()));
//            }
//        }
//    }
    private void setWorkplaceBySegment(String segment) {
        System.out.println("setWorkplaceBySegment segment = " + segment);
        List result = new ConfigWorkplace().selectBySegment(segment);
        if (result.isEmpty()) {
            JOptionPane.showMessageDialog(null, Helper.ERR0027_NO_WORKPLACE_FOUND + " for " + segment, "Configuration error !", ERROR_MESSAGE);
            System.err.println(Helper.ERR0027_NO_WORKPLACE_FOUND + " for " + segment);
        } else { //Map project data in the list
            for (Object o : result) {
                ConfigWorkplace cp = (ConfigWorkplace) o;
                workplace_filter.addItem(new ComboItem(cp.getWorkplace(), cp.getWorkplace()));
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
            segment_filter.addItem(new ComboItem("ALL", "ALL"));
            for (Object o : result) {
                ConfigSegment cp = (ConfigSegment) o;
                segment_filter.addItem(new ComboItem(cp.getSegment(), cp.getSegment()));
            }
            segment_filter.setSelectedIndex(0);
            //this.setWorkplaceBySegment(String.valueOf(segment_filter.getSelectedItem()));
            return true;
        }
    }

    private void initTimeSpinners() {

        String startTime = GlobalVars.APP_PROP.getProperty("START_TIME");
        String endTime = GlobalVars.APP_PROP.getProperty("END_TIME");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");

        //################# Start Time Spinner ####################
        startTimeSpinner.setModel(new SpinnerDateModel());
        JSpinner.DateEditor startTimeEditor = new JSpinner.DateEditor(startTimeSpinner, "HH:mm");
        startTimeSpinner.setEditor(startTimeEditor);
        try {
            startTimeSpinner.setValue(timeFormat.parse(startTime));
        } catch (ParseException ex) {
            Logger.getLogger(PACKAGING_UI0011_ProdStatistics.class.getName()).log(Level.SEVERE, null, ex);
        }

        //################# End Time Spinner ######################
        endTimeSpinner.setModel(new SpinnerDateModel());
        JSpinner.DateEditor endTimeEditor = new JSpinner.DateEditor(endTimeSpinner, "HH:mm");
        endTimeSpinner.setEditor(endTimeEditor);
        try {
            endTimeSpinner.setValue(timeFormat.parse(endTime));
        } catch (ParseException ex) {
            Logger.getLogger(PACKAGING_UI0011_ProdStatistics.class.getName()).log(Level.SEVERE, null, ex);
        }

        startDatePicker.setDate(new Date());
        endDatePicker.setDate(new Date());

    }

    public void reset_tables_content() {
        //############ Reset declared table result
        this.load_declared_result_table_header();
        declared_result_table_data = new Vector();
        DefaultTableModel declaredDataModel = new DefaultTableModel(declared_result_table_data, declared_result_table_header);
        declared_result_table.setModel(declaredDataModel);

    }

    /**
     *
     */
    public void load_declared_result_table_header() {
        declared_result_table_header.clear();
        declared_result_table_header.add("Part number");
        declared_result_table_header.add("Produced qty");
        declared_result_table_header.add("Segment");
        declared_result_table_header.add("Workplace");
        declared_result_table_header.add("Std Time (Hours)");
        declared_result_table_header.add("Produced hours");
        declared_result_table.setModel(new DefaultTableModel(declared_result_table_data, declared_result_table_header));
        declared_result_table.setAutoCreateRowSorter(true);
    }

    public void disableEditingTables() {
        for (int c = 0; c < declared_result_table.getColumnCount(); c++) {
            // remove editor   
            Class<?> col_class1 = declared_result_table.getColumnClass(c);
            declared_result_table.setDefaultEditor(col_class1, null);
        }
    }

    @SuppressWarnings("empty-statement")
    public void reload_declared_result_table_data(List<Object[]> resultList) {

        int total_produced = 0;
        Double produced_hours = 0.00;

        for (Object[] obj : resultList) {
            Vector<Object> oneRow = new Vector<Object>();
            if (String.valueOf(obj[0]).startsWith("P")) {
                oneRow.add(String.valueOf(obj[0]).substring(1)); //harness_part
            } else {
                oneRow.add(String.valueOf(obj[0])); //harness_part
            }
            oneRow.add(String.valueOf(String.format("%d", obj[1]))); //produced_qty;
            oneRow.add(String.valueOf(obj[2])); //segment
            oneRow.add(String.valueOf(obj[3])); //workplace
            oneRow.add(String.valueOf(String.format("%1$,.2f", obj[4]))); //std_time            
            oneRow.add(String.valueOf(String.format("%1$,.2f", obj[5]))); //produced_hours
            total_produced = total_produced + Integer.valueOf(String.valueOf(obj[1]));
            produced_hours = produced_hours + Double.valueOf(String.valueOf(obj[5]));
            declared_result_table_data.add(oneRow);
        }
        declared_result_table.setModel(new DefaultTableModel(declared_result_table_data, declared_result_table_header));
        declared_result_table.setFont(new Font(String.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_FONT")), Font.BOLD, Integer.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_FONTSIZE"))));
        declared_result_table.setRowHeight(Integer.valueOf(GlobalVars.APP_PROP.getProperty("JTABLE_ROW_HEIGHT")));

        //Set declared qty labels values
        this.total_declared_lbl.setText(String.valueOf(total_produced));

        //Set declared hours labels values
        this.total_produced_hours_lbl.setText(String.format("%1$,.2f", produced_hours));

    }

    private boolean checkValidFields() {
        if (startTimeSpinner.getValue() != ""
                && endTimeSpinner.getValue() != ""
                && startDatePicker.getDate() != null
                && endDatePicker.getDate() != null) {
            return true;
        } else {
            return false;
        }
    }

    private void refresh() {
        //System.out.println("Execute refresh()");
        if (checkValidFields()) {
            segments.clear();
            workplaces.clear();
            projects.clear();
            startTimeStr = timeDf.format(startTimeSpinner.getValue());
            endTimeStr = timeDf.format(endTimeSpinner.getValue());
            startDateStr = dateDf.format(startDatePicker.getDate()) + " " + startTimeStr;
            endDateStr = dateDf.format(endDatePicker.getDate()) + " " + endTimeStr;
            harness_part = "%" + harness_part_txt.getText() + "%";

            try {
                Date startDate = dateTimeDf.parse(startDateStr);
                Date endDate = dateTimeDf.parse(endDateStr);
                System.out.println(startDate.before(endDate));
            } catch (Exception ex) {
                Logger.getLogger(PACKAGING_UI0011_ProdStatistics.class.getName()).log(Level.SEVERE, null, ex);
            }
//            System.out.println("startDate " + startDateStr);
//            System.out.println("endDate " + endDateStr);

            //Populate the segments Array with data
            if (String.valueOf(project_filter.getSelectedItem()).equals("ALL") || String.valueOf(project_filter.getSelectedItem()).equals("null")) {
                List result = new ConfigProject().selectCustomers();
                if (result.isEmpty()) {
                    UILog.severeDialog(this, ErrorMsg.APP_ERR0035);
                    UILog.severe(ErrorMsg.APP_ERR0035[1]);
                } else { //Map project data in the list
                    for (Object o : result) {
                        projects.add(o.toString());
                    }
                }
            } else {
                projects.add(String.valueOf(project_filter.getSelectedItem()));
            }
            if (String.valueOf(segment_filter.getSelectedItem()).equals("ALL") || String.valueOf(segment_filter.getSelectedItem()).equals("null")) {
                List result = new ConfigSegment().select();
                if (result.isEmpty()) {
                    JOptionPane.showMessageDialog(null, Helper.ERR0026_NO_SEGMENT_FOUND, "Configuration error !", ERROR_MESSAGE);
                    System.err.println(Helper.ERR0026_NO_SEGMENT_FOUND);
                } else { //Map project data in the list
                    for (Object o : result) {
                        ConfigSegment cs = (ConfigSegment) o;
                        segments.add(String.valueOf(cs.getSegment()));
                    }
                }
            } else {
                segments.add(String.valueOf(segment_filter.getSelectedItem()));
                //Populate the workplaces Array with data
                if (String.valueOf(workplace_filter.getSelectedItem()).equals("ALL")) {
                    List result = new ConfigWorkplace().selectBySegment(String.valueOf(segment_filter.getSelectedItem()));
                    if (result.isEmpty()) {
                        JOptionPane.showMessageDialog(null, Helper.ERR0027_NO_WORKPLACE_FOUND, "Configuration error !", ERROR_MESSAGE);
                        System.err.println(Helper.ERR0027_NO_WORKPLACE_FOUND);
                    } else { //Map project data in the list
                        for (Object o : result) {
                            ConfigWorkplace cw = (ConfigWorkplace) o;
                            workplaces.add(String.valueOf(cw.getWorkplace()));
                        }
                    }
                } else {
                    workplaces.add(String.valueOf(workplace_filter.getSelectedItem()));
                }
            }

            try {
                //Clear all tables
                this.reset_tables_content();
                String query_str = "";
                //################# Declared Harness Data ####################        
                Helper.startSession();
                SQLQuery query;
                if (radio_filled_ucs.isSelected()) { // UCS Complet
                    //Request 1
                    query_str = "(SELECT bc.harness_part AS harness_part,"
                            + " SUM(bc.qty_read) AS produced_qty,"
                            + " bc.segment AS segment,"
                            + " bc.workplace AS workplace,"
                            + " bc.std_time AS std_time,"
                            + " bc.std_time*SUM(bc.qty_read) AS produced_hours"
                            + " FROM base_container bc "
                            + " WHERE "
                            + " (bc.stored_time BETWEEN '%s' AND '%s')"
                            + " AND bc.harness_part like '%s' ";
                    if (!segments.isEmpty()) {
                        query_str += " AND bc.segment IN (:segments) ";
                    }
                    if (!workplaces.isEmpty()) {
                        query_str += " AND bc.workplace IN (:workplaces) ";
                    }
                    if (!projects.isEmpty()) {
                        query_str += " AND bc.project IN (:projects) ";
                    }

                    query_str = String.format(query_str, startDateStr, endDateStr, harness_part);
                    query_str += "GROUP BY bc.harness_part, bc.segment, bc.workplace, bc.std_time "
                            + "ORDER BY bc.harness_part ASC, bc.segment ASC, bc.workplace ASC)";

                    //Select only harness parts with UCS completed.                                
                    query = Helper.sess.createSQLQuery(query_str);

                    query.addScalar("harness_part", StandardBasicTypes.STRING)
                            .addScalar("produced_qty", StandardBasicTypes.INTEGER)
                            .addScalar("segment", StandardBasicTypes.STRING)
                            .addScalar("workplace", StandardBasicTypes.STRING)
                            .addScalar("std_time", StandardBasicTypes.DOUBLE)
                            .addScalar("produced_hours", StandardBasicTypes.DOUBLE);
                    if (!projects.isEmpty()) {
                        query.setParameterList("projects", projects);
                    }
                    if (!segments.isEmpty()) {
                        query.setParameterList("segments", segments);
                    }
                    if (!workplaces.isEmpty()) {
                        query.setParameterList("workplaces", workplaces);
                    }

                } else { // Fx Scannés
                    //Request 2
                    query_str = " SELECT  "
                            + " bh.harness_part AS harness_part,"
                            + " COUNT(bh.harness_part) AS produced_qty,"
                            + " bh.segment AS segment,"
                            + " bh.workplace AS workplace,"
                            + " bh.std_time AS std_time,"
                            + " SUM(bh.std_time) AS produced_hours "
                            + " FROM base_harness bh, base_container bc "
                            + " WHERE bc.id = bh.container_id "
                            + " AND bh.create_time BETWEEN '%s' AND '%s'"
                            + " AND bc.harness_part like '%s' ";

                    if (!segments.isEmpty()) {
                        query_str += " AND bc.segment IN (:segments) ";
                    }
                    if (!workplaces.isEmpty()) {
                        query_str += " AND bc.workplace IN (:workplaces) ";
                    }
                    if (!projects.isEmpty()) {
                        query_str += " AND bc.project IN (:projects) ";
                    }

                    query_str = String.format(query_str, startDateStr, endDateStr, harness_part);
                    query_str += " GROUP BY bh.segment, bh.workplace, bh.harness_part, bh.std_time  "
                            + " ORDER BY bh.segment ASC,bh.workplace ASC, bh.harness_part ASC";

                    //Select only harness parts with UCS completed.                                
                    query = Helper.sess.createSQLQuery(query_str);

                    query.addScalar("harness_part", StandardBasicTypes.STRING)
                            .addScalar("produced_qty", StandardBasicTypes.INTEGER)
                            .addScalar("segment", StandardBasicTypes.STRING)
                            .addScalar("workplace", StandardBasicTypes.STRING)
                            .addScalar("std_time", StandardBasicTypes.DOUBLE)
                            .addScalar("produced_hours", StandardBasicTypes.DOUBLE);

                    if (!projects.isEmpty()) {
                        query.setParameterList("projects", projects);
                    }
                    if (!segments.isEmpty()) {
                        query.setParameterList("segments", segments);
                    }
                    if (!workplaces.isEmpty()) {
                        query.setParameterList("workplaces", workplaces);
                    }

                }

                this.declaredResultList = query.list();

                Helper.sess.getTransaction().commit();

                this.reload_declared_result_table_data(declaredResultList);

                this.disableEditingTables();

            } catch (HibernateException e) {
                if (Helper.sess.getTransaction() != null) {
                    Helper.sess.getTransaction().rollback();
                }
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Empty field", "Empty field Error", JOptionPane.ERROR_MESSAGE);
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

        north_panel = new javax.swing.JPanel();
        startDatePicker = new org.jdesktop.swingx.JXDatePicker();
        endDatePicker = new org.jdesktop.swingx.JXDatePicker();
        refresh_btn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        startTimeSpinner = new javax.swing.JSpinner();
        endTimeSpinner = new javax.swing.JSpinner();
        result_table_scroll = new javax.swing.JScrollPane();
        declared_result_table = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        export_btn = new javax.swing.JButton();
        radio_filled_ucs = new javax.swing.JRadioButton();
        radio_all_harness = new javax.swing.JRadioButton();
        workplace_filter = new javax.swing.JComboBox();
        segment_filter = new javax.swing.JComboBox();
        jLabel20 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        total_produced_hours_lbl = new javax.swing.JTextField();
        total_declared_lbl = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        total_dropped_hours_lbl = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        total_dropped_lbl = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        harness_part_txt = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        project_filter = new javax.swing.JComboBox();
        jLabel23 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Production statistics");
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        north_panel.setBackground(new java.awt.Color(51, 51, 51));
        north_panel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                north_panelKeyPressed(evt);
            }
        });

        refresh_btn.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        refresh_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gui/refresh.png"))); // NOI18N
        refresh_btn.setText("Refresh");
        refresh_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refresh_btnActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Du");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Au");

        startTimeSpinner.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        endTimeSpinner.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        declared_result_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        result_table_scroll.setViewportView(declared_result_table);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Quantités déclarées");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Déclaration fin de ligne");

        export_btn.setText("Exporter en Excel...");
        export_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                export_btnActionPerformed(evt);
            }
        });

        radio_filled_ucs.setForeground(new java.awt.Color(255, 255, 255));
        radio_filled_ucs.setText("Total palettes STORED");
        radio_filled_ucs.setToolTipText("Calcul la quantité des palettes avec UCS Complet.");
        radio_filled_ucs.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radio_filled_ucsItemStateChanged(evt);
            }
        });

        radio_all_harness.setForeground(new java.awt.Color(255, 255, 255));
        radio_all_harness.setSelected(true);
        radio_all_harness.setText("Total par pièces scannées");
        radio_all_harness.setToolTipText("<html>Calcul le total des faisceaux scannés au niveau fin de ligne.</html>");
        radio_all_harness.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radio_all_harnessItemStateChanged(evt);
            }
        });

        workplace_filter.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
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

        segment_filter.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
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

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Segment");

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Σ Quantités produites");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Σ Heures produites");

        total_produced_hours_lbl.setEditable(false);
        total_produced_hours_lbl.setBackground(new java.awt.Color(153, 255, 255));
        total_produced_hours_lbl.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        total_produced_hours_lbl.setText("0");

        total_declared_lbl.setEditable(false);
        total_declared_lbl.setBackground(new java.awt.Color(153, 255, 255));
        total_declared_lbl.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        total_declared_lbl.setText("0");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Σ Heures annulées");

        total_dropped_hours_lbl.setEditable(false);
        total_dropped_hours_lbl.setBackground(new java.awt.Color(255, 255, 102));
        total_dropped_hours_lbl.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        total_dropped_hours_lbl.setText("0");

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Σ Quantités annulées");

        total_dropped_lbl.setEditable(false);
        total_dropped_lbl.setBackground(new java.awt.Color(255, 255, 102));
        total_dropped_lbl.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        total_dropped_lbl.setText("0");

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("Workplace");

        harness_part_txt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                harness_part_txtKeyPressed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Part number");

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

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("Project");

        javax.swing.GroupLayout north_panelLayout = new javax.swing.GroupLayout(north_panel);
        north_panel.setLayout(north_panelLayout);
        north_panelLayout.setHorizontalGroup(
            north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(result_table_scroll)
            .addGroup(north_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(north_panelLayout.createSequentialGroup()
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(north_panelLayout.createSequentialGroup()
                                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(project_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel23))
                                .addGap(18, 18, 18)
                                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel20)
                                    .addComponent(segment_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(workplace_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel22))
                                .addGap(18, 18, 18)
                                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(harness_part_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(north_panelLayout.createSequentialGroup()
                                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(startDatePicker, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                                    .addComponent(endDatePicker, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(endTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(startTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(radio_all_harness, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(radio_filled_ucs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(11, 11, 11)
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(total_dropped_lbl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                            .addComponent(total_produced_hours_lbl, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(total_declared_lbl, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(total_dropped_hours_lbl))
                        .addGap(26, 26, 26))
                    .addGroup(north_panelLayout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(621, 621, 621))
                    .addGroup(north_panelLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(export_btn)
                        .addGap(13, 13, 13)
                        .addComponent(refresh_btn)
                        .addGap(0, 647, Short.MAX_VALUE))))
        );
        north_panelLayout.setVerticalGroup(
            north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(north_panelLayout.createSequentialGroup()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(north_panelLayout.createSequentialGroup()
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(north_panelLayout.createSequentialGroup()
                                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel22))
                                .addGap(1, 1, 1)
                                .addComponent(harness_part_txt, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(north_panelLayout.createSequentialGroup()
                                .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel20)
                                    .addComponent(jLabel23))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(segment_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(workplace_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(project_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(north_panelLayout.createSequentialGroup()
                                    .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(startDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                        .addComponent(endDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2)
                                        .addComponent(endTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addComponent(startTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, north_panelLayout.createSequentialGroup()
                                .addComponent(radio_all_harness)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(radio_filled_ucs)
                                .addGap(5, 5, 5)))
                        .addGap(18, 18, 18)
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(refresh_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(export_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addGap(16, 16, 16)
                        .addComponent(result_table_scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(north_panelLayout.createSequentialGroup()
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(total_declared_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(total_produced_hours_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel21)
                            .addComponent(total_dropped_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(north_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(total_dropped_hours_lbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16))))
                .addGap(215, 215, 215))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(north_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(north_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 669, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void refresh_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refresh_btnActionPerformed

        refresh();

    }//GEN-LAST:event_refresh_btnActionPerformed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.dispose();
        }
    }//GEN-LAST:event_formKeyPressed

    private void north_panelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_north_panelKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.dispose();
        }
    }//GEN-LAST:event_north_panelKeyPressed

    private void export_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_export_btnActionPerformed

        startTimeStr = timeDf.format(startTimeSpinner.getValue());
        endTimeStr = timeDf.format(endTimeSpinner.getValue());
        startDateStr = dateDf.format(startDatePicker.getDate()) + " " + startTimeStr;
        endDateStr = dateDf.format(endDatePicker.getDate()) + " " + endTimeStr;

        //Create the excel workbook
        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet("PROD_STATISTICS");
        CreationHelper createHelper = wb.getCreationHelper();
        int total_produced = 0;
        double total_produced_hours = 0.00;

        //######################################################################
        //##################### SHEET 1 : PILES DETAILS ########################
        //Initialiser les entête du fichier
        // Create a row and put some cells in it. Rows are 0 based.
        Row row = sheet.createRow((short) 0);

        row.createCell(0).setCellValue("PART NUMBER");
        row.createCell(1).setCellValue("PRODUCED QTY");
        row.createCell(2).setCellValue("SEGMENT");
        row.createCell(3).setCellValue("WORKPLACE");
        row.createCell(4).setCellValue("STD TIME");
        row.createCell(5).setCellValue("PRODUCED HOURS");

        short sheetPointer = 1;

        for (Object[] obj : this.declaredResultList) {
            row = sheet.createRow(sheetPointer);
            row.createCell(0).setCellValue(String.valueOf(obj[0])); //PN
            row.createCell(1).setCellValue(Double.valueOf(obj[1].toString())); //QTY
            if (String.valueOf(obj[2].toString()).startsWith("P")) {
                row.createCell(2).setCellValue(String.valueOf(obj[2]).substring(1));//SEGMENT
            } else {
                row.createCell(2).setCellValue(String.valueOf(obj[2]));//SEGMENT
            }
            row.createCell(3).setCellValue(obj[3].toString());//WORKPLACE
            row.createCell(4).setCellValue(Double.valueOf(obj[4].toString()));//STD TIME
            row.createCell(5).setCellValue(Double.valueOf(obj[5].toString()));//PRODUCED HOURS

            total_produced = total_produced + Integer.valueOf(String.valueOf(obj[1]));
            total_produced_hours = total_produced_hours + Double.valueOf(String.valueOf(obj[5]));

            sheetPointer++;
        }

        //Total produced line
        row = sheet.createRow(sheetPointer++);
        row.createCell(0).setCellValue("TOTAL PRODUCED QTY :");
        row.createCell(1).setCellValue(total_produced);

        //Total produced hours
        row = sheet.createRow(sheetPointer++);
        row.createCell(0).setCellValue("TOTAL PRODUCED HOURS :");
        row.createCell(1).setCellValue(Double.valueOf(total_produced_hours));

        //Start date
        row = sheet.createRow(sheetPointer++);
        row.createCell(0).setCellValue("FROM : ");
        row.createCell(1).setCellValue(String.valueOf(startDateStr));

        //End date
        row = sheet.createRow(sheetPointer++);
        row.createCell(0).setCellValue("TO : ");
        row.createCell(1).setCellValue(String.valueOf(endDateStr));

        //Past the workbook to the file chooser
        new JDialogExcelFileChooser((Frame) super.getParent(), true, wb).setVisible(true);
    }//GEN-LAST:event_export_btnActionPerformed

    private void workplace_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_workplace_filterItemStateChanged
        //refresh();
    }//GEN-LAST:event_workplace_filterItemStateChanged

    private void workplace_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workplace_filterActionPerformed
//        System.out.println("workplace_filterActionPerformed segment value "+segment_filter.getSelectedItem().toString());
//        if (!segment_filter.getSelectedItem().toString().equals("ALL")) {
//            refresh();
//        }
    }//GEN-LAST:event_workplace_filterActionPerformed

    private void segment_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_segment_filterItemStateChanged

    }//GEN-LAST:event_segment_filterItemStateChanged

    private void segment_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_segment_filterActionPerformed
        String segment = String.valueOf(segment_filter.getSelectedItem()).trim();
        this.workplace_filter.removeAllItems();
        this.workplace_filter.addItem(new ComboItem("ALL", "ALL"));
        if ("ALL".equals(segment) || segment.equals("null")) {
            this.workplace_filter.setSelectedIndex(0);
            this.workplace_filter.setEnabled(false);

        } else {
            this.setWorkplaceBySegment(segment);
            this.workplace_filter.setEnabled(true);
        }
//        refresh();

    }//GEN-LAST:event_segment_filterActionPerformed

    private void radio_all_harnessItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radio_all_harnessItemStateChanged
        refresh();
    }//GEN-LAST:event_radio_all_harnessItemStateChanged

    private void radio_filled_ucsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radio_filled_ucsItemStateChanged
        refresh();
    }//GEN-LAST:event_radio_filled_ucsItemStateChanged

    private void harness_part_txtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_harness_part_txtKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh();
        }
    }//GEN-LAST:event_harness_part_txtKeyPressed

    private void project_filterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_project_filterItemStateChanged

    }//GEN-LAST:event_project_filterItemStateChanged


    private void project_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_project_filterActionPerformed
        String project = String.valueOf(project_filter.getSelectedItem()).trim();
        System.out.println("Selected Project " + project);
        if ("ALL".equals(project)) {
            segment_filter.removeAllItems();
            segment_filter.addItem(new ComboItem("ALL", "ALL"));
            this.segment_filter.setSelectedIndex(0);
            this.segment_filter.setEnabled(false);
        } else {
            this.setSegmentByProject(project);
            this.segment_filter.setEnabled(true);
            //refresh();
        }
    }//GEN-LAST:event_project_filterActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable declared_result_table;
    private org.jdesktop.swingx.JXDatePicker endDatePicker;
    private javax.swing.JSpinner endTimeSpinner;
    private javax.swing.JButton export_btn;
    private javax.swing.JTextField harness_part_txt;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel north_panel;
    private javax.swing.JComboBox project_filter;
    private javax.swing.JRadioButton radio_all_harness;
    private javax.swing.JRadioButton radio_filled_ucs;
    private javax.swing.JButton refresh_btn;
    private javax.swing.JScrollPane result_table_scroll;
    private javax.swing.JComboBox segment_filter;
    private org.jdesktop.swingx.JXDatePicker startDatePicker;
    private javax.swing.JSpinner startTimeSpinner;
    private javax.swing.JTextField total_declared_lbl;
    private javax.swing.JTextField total_dropped_hours_lbl;
    private javax.swing.JTextField total_dropped_lbl;
    private javax.swing.JTextField total_produced_hours_lbl;
    private javax.swing.JComboBox workplace_filter;
    // End of variables declaration//GEN-END:variables
}
