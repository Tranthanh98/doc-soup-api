package logixtek.docsoup.api.features.nda.builders;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.Overlay;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;

import java.awt.*;
import java.io.*;
import java.util.UUID;

public class SignedNda {
    private String fullName;
    private String date;
    private String email;
    private byte[] originNda;
    private String documentCertificateId;

    private Color textColor = new Color(17, 17, 17);
    private int fontSize = 8;

    public SignedNda fullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public SignedNda date(String date) {
        this.date = date;
        return this;
    }

    public SignedNda email(String email) {
        this.email = email;
        return this;
    }

    public SignedNda originNda(byte[] originNda) {
        this.originNda = originNda;
        return this;
    }

    public SignedNda documentCertificateId(String documentCertificateId){
        this.documentCertificateId =documentCertificateId;
        return this;
    }

    public ByteArrayOutputStream build() {

        float marginLeft = 60;

        var classloader = Thread.currentThread().getContextClassLoader();

        try(PDDocument originDoc = Loader.loadPDF(this.originNda);
            PDDocument document = Loader.loadPDF(classloader.getResourceAsStream("NDA_Signed.pdf"));
            ByteArrayOutputStream out = new ByteArrayOutputStream()){

            if(originDoc.isEncrypted()){
                originDoc.setAllSecurityToBeRemoved(true);
            }

            PDFont font = PDTrueTypeFont.load(document, classloader.getResourceAsStream(
                    "Roboto-Medium.ttf"), WinAnsiEncoding.INSTANCE);

            PDFont regularText = PDType1Font.HELVETICA;

            for (int i = 0; i < originDoc.getNumberOfPages(); i++) {
                PDPage page = originDoc.getPage(i);
                PDPageContentStream contentStream =
                        new PDPageContentStream(originDoc, page, PDPageContentStream.AppendMode.APPEND, true, true);

                var height = page.getMediaBox().getHeight();

                contentStream.beginText();
                contentStream.setFont(regularText, 8);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.newLineAtOffset( 30, height - 28 );
                contentStream.showText("ID: " +this.documentCertificateId);
                contentStream.endText();

                contentStream.close();
            }


            PDPage page = document.getPage(0);

            PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);

            try{
                // documentId
                contentStream.addRect(marginLeft - 30, 807, 300, 20);
                contentStream.setNonStrokingColor(Color.WHITE);
                contentStream.fill();

                contentStream.beginText();
                contentStream.setFont(regularText, 8);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.newLineAtOffset( marginLeft - 28, 807 );
                contentStream.showText("ID: " +this.documentCertificateId);
                contentStream.endText();

                // Name
                contentStream.beginText();
                contentStream.setFont(font, fontSize);
                contentStream.setNonStrokingColor(textColor);
                contentStream.newLineAtOffset( marginLeft, 658 );
                contentStream.showText(this.fullName);
                contentStream.endText();

                // date
                contentStream.beginText();
                contentStream.setFont(font, fontSize);
                contentStream.setNonStrokingColor(textColor);
                contentStream.newLineAtOffset( marginLeft, 615 );
                contentStream.showText(this.date);
                contentStream.endText();

                // email
                contentStream.beginText();
                contentStream.setFont(font, fontSize);
                contentStream.setNonStrokingColor(textColor);
                contentStream.newLineAtOffset( marginLeft, 569 );
                contentStream.showText(this.email);
                contentStream.endText();

                // signature
                PDFont signatureFont = PDTrueTypeFont.load(document, classloader.getResourceAsStream(
                        "DancingScript-font.ttf"), WinAnsiEncoding.INSTANCE);

                contentStream.beginText();
                contentStream.setFont(signatureFont, 17);
                contentStream.setNonStrokingColor(textColor);
                contentStream.newLineAtOffset( marginLeft, 515 );
                contentStream.showText(this.fullName);
                contentStream.endText();
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }


            contentStream.close();

            originDoc.addPage(page);

            originDoc.save(out);

            return out;
        }
        catch (IOException e){
            System.out.println(e.getMessage());
            return null;
        }

    }
}
