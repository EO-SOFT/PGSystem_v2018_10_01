/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SimpleTable {

    public static final String DEST = "results/tables/simple_table.pdf";

    public static void main(String[] args) throws IOException,
            DocumentException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new SimpleTable().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(dest));
        document.open();
        PdfPTable table = new PdfPTable(8);
        for (int aw = 0; aw < 16; aw++) {
            // the cell object
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
            PdfPCell cell = new PdfPCell();
            cell.setImage(createBarcode(writer, "P388-4016", 6f, 0.7f, BaseColor.BLACK, BaseColor.BLACK, true));
            
            table.addCell(cell);
        }
        document.add(table);
        document.close();
    }

    public Image createBarcode(PdfWriter writer, String code, float barcodeHeight, float minBarWidth, BaseColor barColor, BaseColor textColor, Boolean showText) throws DocumentException, IOException {
        Barcode128 code128 = new Barcode128();
        code128.setCodeType(0);
        code128.setGenerateChecksum(true);
        code128.setBarHeight(barcodeHeight);
        code128.setX(minBarWidth);
        code128.setCode(code);

        if (showText == false) {
            code128.setFont(null);
        }
        //Add Barcode to PDF document
        Image barcode_img = Image.getInstance(code128.createImageWithBarcode(writer.getDirectContent(), barColor, textColor));

        return barcode_img;
    }

}
