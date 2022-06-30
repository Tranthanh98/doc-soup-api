package logixtek.docsoup.api.infrastructure.helper;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;


public class ContentHelper
{
    public static Blob convertFileToBlob(MultipartFile file) throws IOException, SQLException {
        var fileBytes = file.getBytes();

        try {
            return new javax.sql.rowset.serial.SerialBlob(fileBytes);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public static  byte[] convertBlobToByte(Blob blob) throws SQLException {

        var length = (int) blob.length();

        var byteData = blob.getBytes(1, length);

        blob.free();

        return byteData;
    }
}
