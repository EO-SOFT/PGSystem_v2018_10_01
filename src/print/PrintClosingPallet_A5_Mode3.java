/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package print;

import __main__.GlobalMethods;
import __main__.GlobalVars;
import com.itextpdf.barcodes.Barcode128;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.color.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.text.DocumentException;
import entity.BaseContainer;
import java.awt.Desktop;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import ui.UILog;

public final class PrintClosingPallet_A5_Mode3 {

    public static String DEST;
    public BaseContainer bc;

    //    public static final String DEST = "./simple_table13_" + (new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")).format(new Date()).toString() + ".pdf";
    public String[][] DATA = null;

    public void prepareLabelData(BaseContainer bc) {
        this.DATA = new String[][]{
            /*0*/{"Customer Part no. (" + GlobalVars.HARN_PART_PREFIX + ")", bc.getHarnessPart(), GlobalVars.HARN_PART_PREFIX + bc.getHarnessPart()},
            /*1*/ {"Supplier Address", GlobalVars.COMPANY_INFO.getName(), GlobalVars.COMPANY_INFO.getAddress1()},
            /*2*/ {"Supplier Part no. (" + GlobalVars.SUPPLIER_PART_PREFIX + ")", bc.getSupplierPartNumber(), GlobalVars.SUPPLIER_PART_PREFIX + bc.getSupplierPartNumber()},
            /*3*/ {"Article desc.", (bc.getArticleDesc().length() > 22) ? bc.getArticleDesc().substring(0, 22) : bc.getArticleDesc(), ""},
            /*4*/ {"Quantity (" + GlobalVars.QUANTITY_PREFIX + ")", bc.getQtyExpected().toString(), GlobalVars.QUANTITY_PREFIX + bc.getQtyExpected().toString()},
            /*5*/ {"Index", bc.getHarnessIndex(), ""},
            /*6*/ {"Pack workstation", bc.getPackWorkstation(), ""},
            /*7*/ {"Pack operator", bc.getUser(), ""},
            /*8*/ {"Container No. (" + GlobalVars.CLOSING_PALLET_PREFIX + ")", bc.getPalletNumber(), GlobalVars.CLOSING_PALLET_PREFIX + bc.getPalletNumber()},
            /*9*/ {"Gross Weight (" + GlobalVars.WEIGHT_PREFIX + ")", bc.getGrossWeight() + "", GlobalVars.WEIGHT_PREFIX + bc.getGrossWeight() + ""},
            /*10*/ {"FIFO Date. (" + GlobalVars.FIFO_DATE_PREFIX + ")", GlobalMethods.convertDateToStringFormat(new Date(), "yy.MM.dd"), GlobalVars.FIFO_DATE_PREFIX + GlobalMethods.convertDateToStringFormat(new Date(), "yy.MM.dd")},
            /*11*/ {"Eng. Change", (bc.getEngChange().length() > 22) ? bc.getEngChange().substring(0, 22) : bc.getEngChange(), ""},
            /*12*/ {"Eng. Change Date", GlobalMethods.convertDateToStringFormat(bc.getEngChangeDate(), "yy.MM.dd"), ""},
            /*13*/ {"Pack Type", bc.getPackType(), ""},
            /*14*/ {"Project", bc.getProject(), ""},
            /*15*/ {"Warehouse (" + GlobalVars.WAREHOUSE_PREFIX + ")", bc.getWarehouse(), GlobalVars.WAREHOUSE_PREFIX + bc.getWarehouse()}
        };
    }

    public PrintClosingPallet_A5_Mode3(BaseContainer bc) {
        PrintClosingPallet_A5_Mode3.DEST = (String.format("." + File.separator
                + GlobalVars.APP_PROP.getProperty("PRINT_DIRNAME")
                + File.separator
                + GlobalMethods.getStrTimeStamp("yyyy_MM_dd")
                + File.separator
                + GlobalVars.APP_PROP.getProperty("PRINT_CLOSING_PALLET_DIRNAME")
                + File.separator + "PrintClosingPallet_A5_"
                + bc.getHarnessPart() + "_"
                + GlobalMethods.getStrTimeStamp("yyyy_MM_dd_HH_mm_ss") + ".pdf"));
        this.bc = bc;
        prepareLabelData(this.bc);
    }

    public static class h {

        public static Paragraph h(String text, boolean bold, int size) throws IOException {
            return format(text, bold, size);
        }

        public static Paragraph h1Bold(String text) throws IOException {
            return format(text, true, 18);
        }

        public static Paragraph h1(String text) throws IOException {
            return format(text, false, 18);
        }

        public static Paragraph h2Bold(String text) throws IOException {
            return format(text, true, 16);
        }

        public static Paragraph h2(String text) throws IOException {
            return format(text, false, 16);
        }

        public static Paragraph h3Bold(String text) throws IOException {
            return format(text, true, 10);
        }

        public static Paragraph h3(String text) throws IOException {
            return format(text, false, 10);
        }

        public static Paragraph format(String text, boolean bold, int size) throws IOException {
            Paragraph paragraph = new Paragraph(text);
            if (bold) {
                paragraph.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD));
            } else {
                paragraph.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA));
            }
            paragraph.setFontSize(size);
            return paragraph;
        }
    }

    public static Cell createBarcode(String code, PdfDocument pdfDoc, boolean displayText, float barWidth, float barHeight) {
        Barcode128 code128 = new Barcode128(pdfDoc);
        code128.setCode(code);
        code128.setCodeType(Barcode128.CODE128);
        if (!displayText) {
            code128.setFont(null);
        }
        if (barWidth != 0) {
            code128.setX(barWidth);
        }
        if (barHeight != 0) {
            code128.setBarHeight(barHeight);
        }
        Cell cell = new Cell().add(new Image(code128.createFormXObject(ColorConstants.BLACK, ColorConstants.BLACK, pdfDoc)));
        cell.setPaddingTop(0);
        cell.setPaddingRight(0);
        cell.setPaddingBottom(0);
        cell.setPaddingLeft(0);

        return cell;
    }

    public String createPdfTemplate3() throws Exception, DocumentException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(PrintClosingPallet_A5_Mode3.DEST));
        Document doc = new Document(pdfDoc, PageSize.A5.rotate());
        doc.setMargins(5, 0, 5, 10);

        Table table = new Table(4);
        table.setTextAlignment(TextAlignment.LEFT);
        table.setWidthPercent(100f);

        //-------- LINE 1 ---------------------------------
        //Project
        table.addCell(new Cell()//.setBorder(Border.NO_BORDER)
                .add(
                        h.h3((String) DATA[14][0]))
                .add(
                        "" //createBarcode((String) DATA[12][2], pdfDoc, false, 1, 20))
                )
                .add(
                        h.h((String) DATA[14][1], true, 32)
                ).setMarginBottom(1).setMarginTop(0));        

        //Supplier Address Data
        table.addCell(new Cell(0, 3)//.setBorder(Border.NO_BORDER)
                .add(h.h3((String) DATA[1][0]))
                .add(h.h2Bold((String) DATA[1][1]))
                .add((String) DATA[1][2]).setMarginBottom(0));

        //-------- LINE 2 ---------------------------------
        //Supplier Part no
        table.addCell(new Cell()//.setBorder(Border.NO_BORDER)
                .add(
                        h.h3((String) DATA[2][0]))
                .add(
                        createBarcode((String) DATA[2][2], pdfDoc, false, 1, 20))
                .add(
                        h.h2Bold((String) DATA[2][1])
                ).setMarginBottom(1).setPaddingLeft(10).setPaddingRight(10));
        //Pack Type
        table.addCell(new Cell()//.setBorder(Border.NO_BORDER)
                .add(
                        h.h3((String) DATA[13][0]))
                .add(
                        h.h(((String) DATA[13][1]), true, 26))
                .add(
                        (String) DATA[13][2]
                ).setMarginBottom(1));
        //Article description
        table.addCell(new Cell(0, 2)//.setBorder(Border.NO_BORDER)
                .add(
                        h.h3((String) DATA[3][0]))
                .add(
                        h.h3Bold(((String) DATA[3][1])))
                .add(
                        (String) DATA[3][2]
                ).setMarginBottom(1));

        //-------- LINE 3 ---------------------------------
        //Quantity
        table.addCell(new Cell()//.setBorder(Border.NO_BORDER)
                .add(
                        h.h3((String) DATA[4][0]))
                .add(
                        createBarcode((String) DATA[4][2], pdfDoc, false, 1, 25))
                .add(
                        h.h((String) DATA[4][1], true, 32)
                ).setMarginBottom(1).setMarginTop(0).setPaddingLeft(15));
        //Index
        table.addCell(new Cell()//.setBorder(Border.NO_BORDER)
                .add(
                        h.h3((String) DATA[5][0]))
                .add(
                        (String) DATA[5][2])
                .add(
                        h.h((String) DATA[5][1], true, 32)
                ).setMarginBottom(1).setMarginTop(0));
        //Pack workstation
        table.addCell(new Cell()//.setBorder(Border.NO_BORDER)
                .add(
                        h.h3((String) DATA[6][0]))
                .add(
                        (String) DATA[6][2])
                .add(
                        h.h2Bold((String) DATA[6][1])
                ).setMarginBottom(1).setMarginTop(0));
        //Pack Operator
        table.addCell(new Cell()//.setBorder(Border.NO_BORDER)
                .add(
                        h.h3((String) DATA[7][0]))
                .add(
                        (String) DATA[7][2])
                .add(
                        h.h2Bold((String) DATA[7][1])
                ).setMarginBottom(1).setMarginTop(0));
        //-------- LINE 4 ---------------------------------
        
        //Warehouse
        table.addCell(new Cell(0, 2)//.setBorder(Border.NO_BORDER)
                .add(
                        h.h3((String) DATA[15][0]))
                .add(
                        createBarcode((String) DATA[15][2], pdfDoc, false, 1, 20))
                .add(
                        h.h((String) DATA[15][1], true, 32)
                ).setMarginBottom(1).setMarginTop(0).setPaddingLeft(15));
        //Gross weight
//        table.addCell(new Cell()//.setBorder(Border.NO_BORDER)
//                .add(
//                        h.h3((String) DATA[9][0]))
//                .add(
//                        createBarcode((String) DATA[9][2], pdfDoc, false, 1, 20))
//                .add(
//                        h.h2Bold((String) DATA[9][1])
//                ).setMarginBottom(1).setMarginTop(0));
        //FIFO Date
        table.addCell(new Cell(0, 2)//.setBorder(Border.NO_BORDER)
                .add(
                        h.h3((String) DATA[10][0]))
                .add(
                        createBarcode((String) DATA[10][2], pdfDoc, false, 1, 20))
                .add(
                        h.h2Bold((String) DATA[10][1])
                ).setMarginBottom(1).setMarginTop(0));
        //-------- LINE 5 ---------------------------------
        //Customer Part no. (P) Barcode + Data
        table.addCell(new Cell(0, 2)//.setBorder(Border.NO_BORDER)
                .add(h.h3((String) DATA[0][0]))
                .add(createBarcode((String) DATA[0][2], pdfDoc, false, 1, 20))
                .add(h.h((String) DATA[0][1], true, 36)
                ).setMarginBottom(0).setPaddingLeft(10));
        //Container no.
        table.addCell(new Cell(0, 2)//.setBorder(Border.NO_BORDER)
                .add(
                        h.h3((String) DATA[8][0]))
                .add(
                        createBarcode((String) DATA[8][2], pdfDoc, false, 1, 20))
                .add(
                        h.h((String) DATA[8][1], true, 32)
                ).setMarginBottom(1).setMarginTop(0).setPaddingLeft(15));
        //Eng.Change
//        table.addCell(new Cell(0, 2)//.setBorder(Border.NO_BORDER)
//                .add(
//                        h.h3((String) DATA[11][0]))
//                .add(
//                        ""//createBarcode((String) DATA[11][2], pdfDoc, false, 1, 20)
//                )
//                .add(
//                        h.h2Bold((String) DATA[11][1])
//                ).setMarginBottom(1).setMarginTop(0));

        //Eng.Change Date
//        table.addCell(new Cell()//.setBorder(Border.NO_BORDER)
//                .add(
//                        h.h3((String) DATA[12][0]))
//                .add(
//                        "" //createBarcode((String) DATA[12][2], pdfDoc, false, 1, 20))
//                )
//                .add(
//                        h.h2Bold((String) DATA[12][1])
//                ).setMarginBottom(1).setMarginTop(0));

        doc.add(table);
        doc.close();
        return DEST;
    }

    public boolean sentToDefaultDesktopPrinter(String filePath) {
        try {
            UILog.info(String.format("Sending [%s] to the default printer...", filePath));
            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
            }
            desktop.print(new File(filePath));
            UILog.info("File [%s] has been printed.");
            return true;
        } catch (IOException ioe) {
            return false;
        }
    }
//    public static void main(String[] args) throws Exception {
//        File file = new File(DEST);
//        file.getParentFile().mkdirs();
//        //new PrintClosingPallet_A5_Mode3().createPdfTemplate3(DEST);
//    }

    public static String getDEST() {
        return DEST;
    }

    public static void setDEST(String DEST) {
        PrintClosingPallet_A5_Mode3.DEST = DEST;
    }

}
