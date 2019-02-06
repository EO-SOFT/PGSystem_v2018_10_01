/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package print;

import __main__.GlobalMethods;
import __main__.GlobalVars;
import com.itextpdf.barcodes.Barcode128;
import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.color.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import entity.BaseContainer;
import entity.BaseHarness;
import java.awt.Desktop;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import ui.UILog;

public class PrintHarnessLabel_A5 {

    public static String DEST;
    public BaseHarness bh;
    public BaseContainer bc;

    //    public static final String DEST = "./simple_table13_" + (new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")).format(new Date()).toString() + ".pdf";
    public String[][] DATA = null;

    public static String getDEST() {
        return DEST;
    }

    public static void setDEST(String DEST) {
        PrintHarnessLabel_A5.DEST = DEST;
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

    public PrintHarnessLabel_A5(BaseHarness bh, BaseContainer bc) {
        PrintHarnessLabel_A5.DEST = (String.format("." + File.separator
                + GlobalVars.APP_PROP.getProperty("PRINT_DIRNAME")
                + File.separator
                + GlobalMethods.getStrTimeStamp("yyyy_MM_dd")
                + File.separator
                + GlobalVars.APP_PROP.getProperty("PRINT_PALLET_DIRNAME")
                + File.separator + "PartLabel_A5_"
                + bc.getHarnessPart() + "_"
                + GlobalMethods.getStrTimeStamp("yyyy_MM_dd_HH_mm_ss") + ".pdf"));
        this.bh = bh;
        this.bc = bc;
        prepareLabelData(this.bh, this.bc);
    }
    
    public final void prepareLabelData(BaseHarness bh, BaseContainer bc) {
        this.DATA = new String[][]{
            {"Supplier Address",  GlobalVars.COMPANY_INFO.getName(), GlobalVars.COMPANY_INFO.getAddress1()},
            {"Customer Part no. ("+GlobalVars.HARN_PART_PREFIX+")", bh.getHarnessPart(), GlobalVars.HARN_PART_PREFIX + bh.getHarnessPart()},
            {"Index", bc.getHarnessIndex(), ""},
            {"Quantity ("+GlobalVars.QUANTITY_PREFIX+")", "1", GlobalVars.QUANTITY_PREFIX+"1"},
            {"Supplier Part no. (" + GlobalVars.SUPPLIER_PART_PREFIX + ")", bc.getSupplierPartNumber(), GlobalVars.SUPPLIER_PART_PREFIX + bc.getSupplierPartNumber()},
            {"FIFO Date. (" + GlobalVars.FIFO_DATE_PREFIX + ")", GlobalMethods.convertDateToStringFormat(new Date(), "yy.MM.dd"), GlobalVars.FIFO_DATE_PREFIX + GlobalMethods.convertDateToStringFormat(new Date(), "yy.MM.dd")},
            {"QRCode ID", bh.getCounter(), ""},
            {"Pallet Num", bh.getPalletNumber(), bh.getPalletNumber()},
        
        };
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
        cell.setPaddingTop(2);
        cell.setPaddingRight(2);
        cell.setPaddingBottom(2);
        cell.setPaddingLeft(2);

        return cell;
    }

    public static Cell createQRcode(String text, PdfDocument pdfDoc) {
        BarcodeQRCode b = new BarcodeQRCode(text);
        //BarcodeQRCode barcodeQRCode = new BarcodeQRCode(text);//text, 1000, 1000, null);
        return new Cell().add(new Image(b.createFormXObject(ColorConstants.BLACK, pdfDoc)));
    }

    public String createPdfFile() throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(PrintHarnessLabel_A5.DEST));
        try (Document doc = new Document(pdfDoc, PageSize.A5.rotate())) {
            doc.setMargins(10, 10, 10, 10);
            
            Table table = new Table(4);
            table.setTextAlignment(TextAlignment.LEFT);
            
            //-------- LINE 1 ---------------------------------
            //Supplier Address Data
            table.addCell(new Cell(0, 4).setBorder(Border.NO_BORDER)
                    .add(h.h1((String) DATA[0][0]))
                    .add(h.h((String) DATA[0][1], true, 28))
                    .add(h.h((String) DATA[0][2], true, 24))
            );
            
            //-------- LINE 2 ---------------------------------
            //Customer Part no. (P) Barcode + Data
            table.addCell(new Cell(0, 2).setBorder(Border.NO_BORDER)
                    .add(h.h3((String) DATA[1][0]))
                    .add(createBarcode((String) DATA[1][2], pdfDoc, false, 1, 30))
                    .add(h.h((String) DATA[1][1], true, 36)
                    ));
            //Index
            table.addCell(new Cell().setBorder(Border.NO_BORDER)
                    .add(
                            h.h3((String) DATA[2][0]))
                    .add(
                            h.h((String) DATA[2][1], true, 28))
                    .add(
                            (String) DATA[2][2]
                    ));
            //Quantity
            table.addCell(new Cell().setBorder(Border.NO_BORDER)
                    .add(
                            h.h3((String) DATA[3][0]))
                    .add(
                            createBarcode((String) DATA[3][2], pdfDoc, false, 1, 30))
                    .add(
                            h.h((String) DATA[3][1], true, 32)
                    ));
            
            //-------- LINE 3 ---------------------------------
            //Supplier Part no
            table.addCell(new Cell(0, 2).setBorder(Border.NO_BORDER)
                    .add(h.h3((String) DATA[4][0]))
                    .add(createBarcode((String) DATA[4][2], pdfDoc, false, 1, 30))
                    .add(h.h((String) DATA[4][1], true, 24)
                    ));
            
            //FIFO Date
            table.addCell(new Cell(0, 2).setBorder(Border.NO_BORDER)
                    .add(
                            h.h3((String) DATA[5][0]))
                    .add(
                            createBarcode((String) DATA[5][2], pdfDoc, false, 1, 30))
                    .add(
                            h.h((String) DATA[5][1], true, 24)
                    ));
            
            //QR
            table.addCell(new Cell(0, 2).setBorder(Border.NO_BORDER)
                    .add(
                            h.h3((String) DATA[6][0]))
                    .add(
                            createQRcode((String) DATA[6][1], pdfDoc))
                    .add(
                             ""
                    ));                        
            
            //Pallet Number
            table.addCell(new Cell(0, 2).setBorder(Border.NO_BORDER)
                    .add(
                            h.h3((String) DATA[7][0]))
                    .add(
                            createBarcode((String) DATA[7][1], pdfDoc, false, 1, 24))
                    .add(
                            h.h((String) DATA[7][1], true, 24)
                    ));
            
            doc.add(table);
            
            return DEST;
        }
    }
    
    public boolean sentToDefaultDesktopPrinter(String filePath) {
        try {
            UILog.info(String.format("Sending [%s] to the default printer...", filePath));
            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
            }else{
                UILog.severe("Desktop object not supported !");
            }
            //System.out.println("Default printer "+desktop.toString());
                    
            desktop.print(new File(filePath));

            UILog.info(String.format("File [%s] has been printed.", filePath));
            return true;
        } catch (IOException ioe) {
            UILog.severe("Problem in sentToDefaultDesktopPrinter");
            return false;
        }
    }



}
