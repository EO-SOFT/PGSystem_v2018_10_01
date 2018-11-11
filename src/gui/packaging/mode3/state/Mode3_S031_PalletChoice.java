/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.packaging.mode3.state;

import __main__.GlobalMethods;
import __main__.GlobalVars;
import com.itextpdf.text.DocumentException;
import gui.packaging.Mode3_Context;
import helper.Helper;
import helper.PrinterHelper;
import entity.BaseContainer;
import entity.BaseContainerTmp;
import entity.BaseHarnessAdditionalBarecode;
import entity.BaseHarness;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import gui.packaging.PackagingVars;
import gui.packaging.mode3.gui.PACKAGING_UI9000_ChoosePackType_Mode3;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Query;
import ui.UILog;
import ui.error.ErrorMsg;

/**
 *
 * @author ezou1001
 */
public class Mode3_S031_PalletChoice implements Mode3_State {

    private final ImageIcon imgIcon = new ImageIcon(GlobalVars.APP_PROP.getProperty("IMG_PATH") + "S040_PaletChoice.jpg");

    public Mode3_S031_PalletChoice() {
        PackagingVars.Packaging_Gui_Mode3.setIconLabel(this.imgIcon);
        //Reload container table content
        PackagingVars.Packaging_Gui_Mode3.reloadDataTable();
    }

    @Override
    public void doAction(Mode3_Context context) {
        JTextField scan_txtbox = PackagingVars.Packaging_Gui_Mode3.getScanTxt();
        String barcode = scan_txtbox.getText().trim();
        System.out.println("GlobalVars.APP_HOSTNAME " + GlobalVars.APP_HOSTNAME);

        //Textbox is not empty
        if (!barcode.isEmpty()) {
            BaseContainer bc = new BaseContainer().getBaseContainer(barcode);
            //######################### OPEN NEW PALLET ########################
            //Is it a new pallet ?
            if (barcode.equals(GlobalVars.OPEN_PALLET_KEYWORD)) {//NEWP barcode
                choosePack(scan_txtbox, context);
            } //####################################################
            else if (bc != null && !bc.getPackWorkstation().equals(GlobalVars.APP_HOSTNAME)) {

                UILog.severe(ErrorMsg.APP_ERR0014[0], bc.getPackWorkstation(), GlobalVars.APP_HOSTNAME, bc.getPackWorkstation());
                UILog.severeDialog(null, ErrorMsg.APP_ERR0014, bc.getPackWorkstation(), GlobalVars.APP_HOSTNAME, bc.getPackWorkstation());
                PackagingVars.Packaging_Gui_Mode3.getFeedbackTextarea().setText(UILog.severe(ErrorMsg.APP_ERR0014[0], bc.getPackWorkstation(), GlobalVars.APP_HOSTNAME, bc.getPackWorkstation()));

            } //# 1- If container exist 
            //# 2- Mode3_State is Open
            //# 3- Max Quantity not reached
            //# 4- Container Harness Part = Mode3_Context Harness Part  
            //# 5- Container Harness Type = Mode3_Context Harness Type
            else if (bc != null
                    && bc.getContainerState().equals(GlobalVars.PALLET_OPEN)
                    && bc.getQtyRead() < bc.getQtyExpected()
                    && (bc.getHarnessPart().equals(PackagingVars.mode3_context.getBaseContainerTmp().getHarnessPart().substring(1))
                    || bc.getHarnessPart().equals(PackagingVars.mode3_context.getBaseContainerTmp().getHarnessPart()))
                    && bc.getHarnessType().equals(PackagingVars.mode3_context.getBaseContainerTmp().getHarnessType())) {
                try {
                    Helper.sess.beginTransaction();
                    Helper.sess.persist(bc);
                    bc.setWriteId(PackagingVars.context.getUser().getId());
                    bc.setFifoTime(GlobalMethods.getTimeStamp(null));
                    bc.setHarnessType(context.getBaseContainerTmp().getHarnessType());

                    //#################### SET HARNESS DATA  #######################
                    //- Set harness data from current mode3_context.
                    BaseHarness bh = new BaseHarness().setDefautlVals();
                    bh = saveBaseHarness(bc, bh, barcode, context);
                    System.out.println("Save Base Harness bh with tmp values " + bh.toString());
                    //##############################################################

                    //############### SET & SAVE ALL ENGINE LABELS DATA #################
                    //Si ce part number contient des code à barre pour sachet
                    if (PackagingVars.mode3_context.getBaseHarnessAdditionalBarecodeTmp().getLabelCode().length != 0) {
                        savePlaticBagCodes(bh, PackagingVars.mode3_context.getBaseHarnessAdditionalBarecodeTmp().getLabelCode());
                    }
                    //##############################################################

                    //############## ADD THE HARNESS TO THE CONTAINER ##############
                    //Insert the harness into the container
                    bc.getHarnessList().add(bh);

                    int newQty = bc.getQtyRead() + 1;
                    //Incrémenter la taille du contenaire
                    bc.setQtyRead(bc.getQtyRead() + 1);
                    bc.update(bc);
                    clearScanBox(scan_txtbox);

                    UILog.info("Deletion  counters result %d ", deletePieceFromDropTable(bh));

                    bh.create(bh);
                    
                    //##############################################################
                    //Print Harness Label if this option is set to true
                    //######################### Print Harness Label ################
                    if (bc.isLabelPerPiece()) {
                        PrinterHelper.PrintPieceLabel(bc, bh);
                    }

                    //##############################################################
                    //- Set harness data from drop table
                    //- Remouve it from drop table (use a flag var to drop it in the end
                    // of this condition)
                    //- Create a history mouvement line in base_harness history
                    //##############################################################
                    //############## Check if pallet should be closed ##############
                    //############## UCS Contains just 1 harness ###################
                    if (bc.getQtyExpected() == newQty || bc.getQtyExpected() == 1) {
                        UILog.info("Quantité terminée %s", bc.toString());
                        try {
                            PrinterHelper.saveAndPrintClosingSheetMode3(PackagingVars.mode3_context, bc, false);
                        } catch (IOException | DocumentException ex) {
                            UILog.severe(ex.toString());
                        } catch (Exception ex) {
                            Logger.getLogger(Mode3_S031_PalletChoice.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        //Helper.startSession();
                        bc.setContainerState(GlobalVars.PALLET_WAITING);
                        bc.setContainerStateCode(GlobalVars.PALLET_WAITING_CODE);
                        bc.update(bc);
                        PackagingVars.mode3_context.getBaseContainerTmp().setPalletNumber(bc.getPalletNumber());
                        //Set requested closing pallet number in the main gui
                        PackagingVars.Packaging_Gui_Mode3.setFeedbackTextarea("Scanner le code de fermeture palette N°\n "
                                + GlobalVars.CLOSING_PALLET_PREFIX + bc.getPalletNumber());
                        context.setState(new Mode3_S040_ClosingPallet());

                    } else { //QtyExpected not reached yet ! Pallet will still open.

                        //Clear session vals in mode3_context
                        clearContextSessionVals();
                        // Change go back to state HarnessPartScan
                        context.setState(new Mode3_S020_HarnessPartScan());
                    }
                } //############################### INVALID PALLET CODE #############
                catch (Exception ex) {
                    Logger.getLogger(Mode3_S031_PalletChoice.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                UILog.severe(ErrorMsg.APP_ERR0015[0], barcode, GlobalVars.OPEN_PALLET_KEYWORD);
                UILog.severeDialog(null, ErrorMsg.APP_ERR0015, barcode, GlobalVars.OPEN_PALLET_KEYWORD);
                PackagingVars.Packaging_Gui_Mode3.getFeedbackTextarea().setText(UILog.severe(ErrorMsg.APP_ERR0015[0], barcode, GlobalVars.OPEN_PALLET_KEYWORD));
                //Vide le scan box
                clearScanBox(scan_txtbox);
                //Retourner l'état actuel
                context.setState(this);
            }
        } //############################### INVALID PALLET CODE #############
        else {
            UILog.severe(ErrorMsg.APP_ERR0015[0], barcode, GlobalVars.OPEN_PALLET_KEYWORD);
            UILog.severeDialog(null, ErrorMsg.APP_ERR0015, barcode, GlobalVars.OPEN_PALLET_KEYWORD);
            PackagingVars.Packaging_Gui_Mode3.getFeedbackTextarea().setText(UILog.severe(ErrorMsg.APP_ERR0015[0], barcode, GlobalVars.OPEN_PALLET_KEYWORD));
            //Vide le scan box
            clearScanBox(scan_txtbox);
            //Retourner l'état actuel
            context.setState(this);
        }
    }

    @Override
    public String toString() {
        return "State S04 : S040_PaletChoice";
    }

    @Override
    public ImageIcon getImg() {
        return this.imgIcon;
    }

    @Override
    public void clearScanBox(JTextField scan_txtbox) {
        //Vider le champs de text scan
        scan_txtbox.setText("");
        scan_txtbox.requestFocusInWindow();
        PackagingVars.Packaging_Gui_Mode3.setScanTxt(scan_txtbox);
    }

    @Override
    public void clearContextSessionVals() {
        PackagingVars.mode3_context.setBaseContainerTmp(new BaseContainerTmp());
        PackagingVars.mode3_context.setLabelCount(0);
        GlobalVars.PLASTICBAG_BARCODE_PATTERN_LIST = new String[0][];
    }

    /**
     * Load choosing pack pop up
     *
     * @param scan_txtbox
     * @param context
     */
    private void choosePack(JTextField scan_txtbox, Mode3_Context context) {
        //Vide le scan box
        clearScanBox(scan_txtbox);
        String hp;
        hp
                = (PackagingVars.mode3_context.getBaseContainerTmp().getHarnessPart()
                        .startsWith(GlobalVars.HARN_PART_PREFIX))
                ? context.getBaseContainerTmp().getHarnessPart().substring(1)
                : context.getBaseContainerTmp().getHarnessPart();

        Object o = new PACKAGING_UI9000_ChoosePackType_Mode3(null, true, hp);

    }

    /**
     * Delete the given piece from drop table
     *
     * @param bh
     * @return
     */
    private int deletePieceFromDropTable(BaseHarness bh) {
        //####### CHECK IF THE HARNESS EXISTS IN THE DROP TABLE ########
        //Yes                
        Query query = Helper.sess.createQuery("DELETE DropBaseHarness WHERE counter = :COUNTER");
        query.setParameter("COUNTER", bh.getCounter());

        return query.executeUpdate();

    }

    private void savePlaticBagCodes(BaseHarness bh, String[] labels) {
        for (String labelCode : PackagingVars.mode3_context.getBaseHarnessAdditionalBarecodeTmp().getLabelCode()) {
            BaseHarnessAdditionalBarecode bel = new BaseHarnessAdditionalBarecode();
            bel.setDefautlVals();
            bel.setLabelCode(labelCode);
            bel.setHarness(bh);
            bel.create(bel);
        }
    }

    /**
     * Save a piece in container and return BaseHarness object
     *
     * @param bc
     * @param bh
     * @param barcode
     * @param context
     * @return
     */
    private BaseHarness saveBaseHarness(BaseContainer bc, BaseHarness bh, String barcode, Mode3_Context context) {
        System.out.println("Save Base Harness bh param " + bh.toString());
        System.out.println("context.getBaseContainerTmp().getHarnessPart()" + context.getBaseContainerTmp().getHarnessPart());
        bh.setHarnessPart(context.getBaseContainerTmp().getHarnessPart());
        bh.setCounter(context.getBaseContainerTmp().getHernessCounter());
        bh.setPalletNumber(barcode);
        bh.setHarnessType(context.getBaseContainerTmp().getHarnessType());
        bh.setStdTime(bc.getStdTime());
        bh.setPackWorkstation(GlobalVars.APP_HOSTNAME);
        bh.setSegment(bc.getSegment());
        bh.setWorkplace(bc.getWorkplace());
        bh.setContainer(bc);
        bh.setProject(bc.getProject());

        return bh;
    }

}
