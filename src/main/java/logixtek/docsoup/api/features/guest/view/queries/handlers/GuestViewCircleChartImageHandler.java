package logixtek.docsoup.api.features.guest.view.queries.handlers;

import an.awesome.pipelinr.Command;
import com.google.common.io.Files;
import logixtek.docsoup.api.features.guest.view.queries.GuestViewCircleChartImage;
import lombok.AllArgsConstructor;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Component("GuestViewCircleChartImageHandler")
@AllArgsConstructor
public class GuestViewCircleChartImageHandler implements Command.Handler<GuestViewCircleChartImage, ResponseEntity<byte[]>>  {
    private final Logger logger = LoggerFactory.getLogger(GuestViewCircleChartImageHandler.class);

    private final float ONE_HUNDRED_PERCENT = 100.0F;
    @Override
    public ResponseEntity<byte[]> handle(GuestViewCircleChartImage query) {
        var classloader = Thread.currentThread().getContextClassLoader();
        String tempDir = System.getProperty("java.io.tmpdir") + "\\Email\\";
        File parentFile = new File(tempDir);
        File pngFile = new File(parentFile, "circle-chart.png");
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        File svgFile = new File(parentFile, "filename.svg");
        var fileResource = query.getPercent().equals(ONE_HUNDRED_PERCENT) ? "templates/circleChartSuccessTemplate.svg" : "templates/circleChartTemplate.svg";
        try (InputStream is = classloader.getResourceAsStream(fileResource);
             OutputStream outputStream = new FileOutputStream(pngFile)) {

            generateSvgFileByPercent(svgFile, is, query.getPercent());

            convertSvgToPng(svgFile, outputStream);

            byte[] filesBytes = Files.toByteArray(pngFile);

            dispose(pngFile, svgFile, outputStream);

            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);


            return new ResponseEntity<>(filesBytes, headers, HttpStatus.CREATED);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return ResponseEntity.noContent().build();
    }

    private void dispose(File pngFile, File svgFile, OutputStream outputStream) throws IOException {
        outputStream.close();
        pngFile.deleteOnExit();
        svgFile.deleteOnExit();
    }

    private void generateSvgFileByPercent(File svgFile, InputStream is, Float percent) throws IOException {
        var svgString = IOUtils.toString(is, StandardCharsets.UTF_8);
        if(percent < ONE_HUNDRED_PERCENT) {
            final float cir = 75;
            var calculatedPercentValue = (cir -(cir *percent/100));
            svgString = svgString.replaceAll("@calculatedPercentValue", calculatedPercentValue + "");
        }

        try(Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(svgFile)))) {
            writer.write(svgString);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private void convertSvgToPng(File svgFile, OutputStream outputStream) throws TranscoderException {
        TranscoderInput transcoderInput = new TranscoderInput(svgFile.toURI().toString());

        TranscoderOutput transcoderOutput = new TranscoderOutput(outputStream);
        // Convert SVG to PNG and Save to File System
        PNGTranscoder pngTranscoder = new PNGTranscoder();
        pngTranscoder.transcode(transcoderInput, transcoderOutput);
    }
}
