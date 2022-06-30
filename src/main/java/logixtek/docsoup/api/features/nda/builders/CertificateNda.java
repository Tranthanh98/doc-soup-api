package logixtek.docsoup.api.features.nda.builders;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CertificateNda {
    private String documentName;
    private String documentId;
    private String originalChecksum;
    private String signedChecksum;
    private String signature;
    private String pageCount;
    private BufferedImage image;
    private String[][] contentHistory;

    private final Color textColor = new Color(17, 17, 17);
    private final int fontSize = 8;

    public CertificateNda documentName(String documentName) {
        this.documentName = documentName;
        return this;
    }

    public CertificateNda documentId(String documentId) {
        this.documentId = documentId;
        return this;
    }

    public CertificateNda originalChecksum(String originalChecksum) {
        this.originalChecksum = originalChecksum;
        return this;
    }

    public CertificateNda signedChecksum(String signedChecksum) {
        this.signedChecksum = signedChecksum;
        return this;
    }

    public CertificateNda signature(String signature) {
        this.signature = signature;
        return this;
    }

    public CertificateNda pageCount(int pageCount) {
        this.pageCount = Integer.toString(pageCount);
        return this;
    }

    public CertificateNda thumbnail(BufferedImage image) {
        this.image = image;
        return this;
    }

    public CertificateNda contentHistory(String[][] contentHistory) {
        this.contentHistory = contentHistory;
        return this;
    }

    public ByteArrayOutputStream build() {

        var classloader = Thread.currentThread().getContextClassLoader();

        try (PDDocument document = Loader.loadPDF(classloader.getResourceAsStream("NDA_Certificate.pdf"));
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = document.getPage(0);
            PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
            //document Name

            PDFont mediumText = PDTrueTypeFont.load(document, classloader.getResourceAsStream(
                    "Roboto-Medium.ttf"), WinAnsiEncoding.INSTANCE);

            PDFont regularText = PDTrueTypeFont.load(document, classloader.getResourceAsStream(
                    "Roboto-Regular.ttf"), WinAnsiEncoding.INSTANCE);

            contentStream.beginText();
            contentStream.setFont(mediumText, fontSize);
            contentStream.setNonStrokingColor(textColor);
            contentStream.newLineAtOffset(44, 742);
            contentStream.showText(this.documentName);
            contentStream.endText();

            //document id
            contentStream.beginText();
            contentStream.setFont(mediumText, fontSize);
            contentStream.setNonStrokingColor(textColor);
            contentStream.newLineAtOffset(44, 703);
            contentStream.showText(this.documentId);
            contentStream.endText();

            // originalChecksum
            contentStream.beginText();
            contentStream.setFont(mediumText, fontSize);
            contentStream.setNonStrokingColor(textColor);
            contentStream.newLineAtOffset(44, 653);
            contentStream.showText(this.originalChecksum);
            contentStream.endText();

            // signedChecksum
            contentStream.beginText();
            contentStream.setFont(mediumText, fontSize);
            contentStream.setNonStrokingColor(textColor);
            contentStream.newLineAtOffset(44, 610);
            contentStream.showText(this.signedChecksum);
            contentStream.endText();

            // signature
            PDFont signatureFont = PDTrueTypeFont.load(document, classloader.getResourceAsStream(
                    "DancingScript-font.ttf"), WinAnsiEncoding.INSTANCE);

            contentStream.beginText();
            contentStream.setFont(signatureFont, 17);
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.newLineAtOffset(48, 555);
            contentStream.showText(this.signature);
            contentStream.endText();

            // page count
            contentStream.beginText();
            contentStream.setFont(mediumText, 10);
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.newLineAtOffset(440, 560);
            contentStream.showText(this.pageCount);
            contentStream.endText();

            //create thumbnail
            if (this.image != null) {
                contentStream.addRect(433, 607, 131, 165);
                contentStream.setNonStrokingColor(new Color(247, 247, 247));
                contentStream.fill();

                PDImageXObject pdImage = LosslessFactory.createFromImage(document, this.image);
                contentStream.drawImage(pdImage, 433, 607);
            }

            drawTable(document, page, contentStream, mediumText, regularText, 450, 35, this.contentHistory);

            contentStream.close();

            document.save(out);

            return out;

        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void drawTable(PDDocument document,
                            PDPage page,
                           PDPageContentStream contentStream,
                           PDFont mediumText,
                           PDFont regularText,
                           float y, float margin,
                           String[][] content) throws IOException {
        final int rows = content.length;
        final int cols = content[0].length;
        final float rowHeight = 60f;
        final float tableWidth = page.getMediaBox().getWidth() - margin - margin;
        final float colWidth = tableWidth / (float) cols;

        //draw the rows
        float nexty = y;
        for (int i = 0; i <= rows; i++) {
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, nexty);
            nexty -= rowHeight;
            contentStream.endText();
        }

        //draw the columns
        float nextx = margin;
        for (int i = 0; i <= cols; i++) {
            contentStream.beginText();
            contentStream.newLineAtOffset(nextx, y);
            contentStream.endText();
        }

        float textx = margin;
        float texty = y - 30;

        contentStream.beginText();
        contentStream.newLineAtOffset(textx, y);
        contentStream.setFont(regularText, 12);
        contentStream.setNonStrokingColor(textColor);
        contentStream.showText("Timestamp");
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(textx + colWidth - 27, y);
        contentStream.setNonStrokingColor(textColor);
        contentStream.setFont(regularText, 12);
        contentStream.showText("Audit Event");
        contentStream.endText();

        //now add the text
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

        int lastItemRender = 0;

        for (int i = 0; i < content.length; i++) {
            for (int j = 0; j < content[i].length - 1; j++) {
                String text = content[i][j];

                float heightOffset = drawString(contentStream, mediumText, text, textx, texty, (int) colWidth * 2, textColor);

                if (j != 0 && i != content.length - 1) {
                    String userAgent = content[i][j + 1];
                    drawString(contentStream, mediumText, userAgent, textx, texty - heightOffset - 13, (int) colWidth * 2, Color.gray);
                }

                textx += colWidth - 25;
            }
            texty -= rowHeight;
            textx = margin;

            if (texty < 120 && i < content.length - 1) {
                lastItemRender = i;
                contentStream.addRect(30, 72, 535, (float)2);
                contentStream.setNonStrokingColor(Color.WHITE);
                contentStream.fill();
                break;
            }
        }

        if(lastItemRender != 0){
            PDPage newPage = new PDPage(PDRectangle.A4);

            PDPageContentStream newContentStream = new PDPageContentStream(document, newPage);

            newContentStream.beginText();
            newContentStream.newLineAtOffset(30, 780);
            newContentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            newContentStream.setNonStrokingColor(textColor);
            newContentStream.showText("History");
            newContentStream.endText();

            newContentStream.addRect(30, 765, 534, (float)0.5);
            newContentStream.setNonStrokingColor(Color.BLACK);
            newContentStream.fill();

            int nextItem = lastItemRender + 1;

            String [][] contentSplit = new String[content.length - nextItem][3];

            for(int i = 0; i < content.length - nextItem; i++){
                contentSplit[i] = content[nextItem + i];
            }

            document.addPage(newPage);

            drawTable(document, newPage, newContentStream, mediumText, regularText, 740, margin, contentSplit);
        }

        contentStream.close();
    }

    private int[] possibleWrapPoints(String text) {
        String[] split = text.split("(?<=\\W)");
        int[] ret = new int[split.length];
        ret[0] = split[0].length();
        for (int i = 1; i < split.length; i++)
            ret[i] = ret[i - 1] + split[i].length();
        return ret;
    }

    private float drawString(PDPageContentStream content, PDFont font, String text, float x, float y, int paragraphWidth, Color textColor) throws IOException {

        int start = 0;
        int end = 0;
        int height = 0;

        for (int i : possibleWrapPoints(text)) {
            float width = font.getStringWidth(text.substring(start, i)) / 1000 * fontSize;
            if (start < end && width > paragraphWidth) {
                // Draw partial text and increase height
                content.beginText();
                content.setNonStrokingColor(textColor);
                content.newLineAtOffset(x, y - height);
                content.setFont(font, 8);
                content.showText(text.substring(start, end));
                content.endText();
                height += 10;
                start = end;
            }
            end = i;
        }

        content.beginText();
        content.setNonStrokingColor(textColor);
        content.setFont(font, 8);
        content.newLineAtOffset(x, y - height);
        content.showText(text.substring(start));
        content.endText();

        return height;
    }
}
