package logixtek.docsoup.api.infrastructure.helper;

import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.constants.RoleDefinition;
import logixtek.docsoup.api.infrastructure.models.CompanyUser;
import logixtek.docsoup.api.infrastructure.models.Viewer;
import org.apache.logging.log4j.util.Strings;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

public class Helper {
    public static List<String> convertToCSVRow(Viewer viewer) {
        var duration = viewer.getDuration() != null ?  Utils.covertMillisecondToMinutesFormat(viewer.getDuration()) : Strings.EMPTY;
        var localDate = Timestamp.valueOf(viewer.getViewedAt().atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
        var date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(localDate);

        return  Arrays.asList(
                String.valueOf(viewer.getSender()),
                viewer.getContactId() == null && viewer.getIsPreview() ? viewer.getLinkCreatorEmail() : viewer.getEmail(),
                viewer.getDevice(),
                viewer.getLocationName(),
                String.valueOf(viewer.getViewedRate()),
                duration,
                date);
    }

    public static List<String> convertToCSVRow(CompanyUser companyUser) {
        return  Arrays.asList(
                String.valueOf(companyUser.getFullName()),
                companyUser.getEmail(),
                getCompanyUserStates(companyUser.getMember(), companyUser.getRole()),
                getCompanyUserStatus(companyUser.getStatus(), companyUser.getUserId())
        );
    }

    public static List<String> genCsvViewerHeader() {
        return Arrays.asList("Link name", "Email", "Device", "Location", "Viewed", "Duration", "Date");
    }

    public static List<String> genCsvCompanyUserHeader() {
        return Arrays.asList("Name", "Email", "States", "Status");
    }

    private static String getCompanyUserStates(Integer memberType, String role) {
        if(memberType.equals(CompanyUserConstant.OWNER_TYPE)) {
            return CompanyUserConstant.OWNER;
        }

        return role.equals(RoleDefinition.C_ADMIN) ? CompanyUserConstant.ADMIN : CompanyUserConstant.MEMBER;

    }

    private static String getCompanyUserStatus(Integer memberType, String userId) {
        if(com.google.common.base.Strings.isNullOrEmpty(userId)) {
            return CompanyUserConstant.INVITED_STRING;
        }

        switch (memberType) {
            case CompanyUserConstant.ACTIVE_STATUS:
                return CompanyUserConstant.ACTIVE_STRING;
            case CompanyUserConstant.DE_ACTIVE_STATUS:
                return CompanyUserConstant.INACTIVE_STRING;
            case CompanyUserConstant.SUSPENDED_STATUS:
                return CompanyUserConstant.SUSPEND_STRING;
            default:
                return null;
        }
    }

    public static String checksumFile(byte[] byteArray) throws NoSuchAlgorithmException, IOException {

        MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");

        var in = new ByteArrayInputStream(byteArray);

        return getFileChecksum(shaDigest, in);


    }

    private static String getFileChecksum(MessageDigest digest, InputStream fis) throws IOException {

        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };

        fis.close();

        byte[] bytes = digest.digest();

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}
