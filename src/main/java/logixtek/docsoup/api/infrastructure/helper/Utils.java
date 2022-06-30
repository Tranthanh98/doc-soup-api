package logixtek.docsoup.api.infrastructure.helper;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Strings;
import com.itextpdf.html2pdf.HtmlConverter;
import lombok.SneakyThrows;
import org.h2.util.DateTimeUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Utils {

    private Utils()
    {

    }

    @SneakyThrows
    public static <T> T getJsonValue(String json, String key, Class<T> classType) {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        var subscriptionNode = objectMapper.readTree(json);
        var childNode = subscriptionNode.get(key);
        if(!childNode.getNodeType().equals(JsonNodeType.OBJECT)) {
            var result = (T) objectMapper.convertValue(childNode.asText(), classType);

            return result;
        }

        String childNodeString = objectMapper.writeValueAsString(childNode);
        if(classType.equals(String.class)) {
            return (T)childNodeString;
        }

        var result = (T) objectMapper.convertValue(childNodeString, classType);

        return result;
    }

    @SneakyThrows
    public static <T> T getJsonValue(String json, String key, JavaType type) {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        var subscriptionNode = objectMapper.readTree(json);
        var childNode = subscriptionNode.get(key);

        String childNodeString = objectMapper.writeValueAsString(childNode);

        var result = (T) objectMapper.readValue(childNodeString, type);

        return result;
    }


    public static String covertMillisecondToMinutesFormat(Long millis) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    public static Long betweenDates(Instant firstDate, Instant secondDate)
    {
        return ChronoUnit.DAYS.between(firstDate, secondDate);
    }

    // format lifespan: 1y2M3w4d5H6m7s8S
    public static String calculateLifeSpan(Instant startDate, Instant endDate) {
        var result = new StringBuilder();
        var years = ChronoUnit.YEARS.between(startDate.atZone(ZoneId.systemDefault()), endDate.atZone(ZoneId.systemDefault()));
        result.append(years).append("y");

        var temp = startDate.atZone(ZoneId.systemDefault()).plus(years, ChronoUnit.YEARS);
        var months = ChronoUnit.MONTHS.between(temp, endDate.atZone(ZoneId.systemDefault()));
        result.append(months).append("M");

        temp = temp.plus(months, ChronoUnit.MONTHS);
        var days = ChronoUnit.DAYS.between(temp, endDate.atZone(ZoneId.systemDefault()));
        result.append(days).append("d");

        temp = temp.plus(days, ChronoUnit.DAYS);
        var hours = ChronoUnit.HOURS.between(temp, endDate.atZone(ZoneId.systemDefault()));
        result.append(hours).append("H");

        temp = temp.plus(hours, ChronoUnit.HOURS);
        var minutes = ChronoUnit.MINUTES.between(temp, endDate.atZone(ZoneId.systemDefault()));
        result.append(minutes).append("m");

        temp = temp.plus(minutes, ChronoUnit.MINUTES);
        var seconds = ChronoUnit.SECONDS.between(temp, endDate.atZone(ZoneId.systemDefault()));
        result.append(seconds).append("s");

        temp = temp.plus(seconds, ChronoUnit.SECONDS);
        var milliSeconds = ChronoUnit.MILLIS.between(temp, endDate.atZone(ZoneId.systemDefault()));
        result.append(milliSeconds).append("S");

        return result.toString();
    }

    public static  <T> Stream<T> collectionToStream(Collection<T> collection) {
        return Optional.ofNullable(collection)
                .map(Collection::stream)
                .orElseGet(Stream::empty);
    }

    public static Long convertSizeUnitToByteSize(long size, String unit){

        if(size < 0) {
            return 0L;
        }

        int factor = 0;
        if (unit.equals("GB")) {
            factor = 1073741824;
        }
        else if (unit.equals("MB")) {
            factor = 1048576;
        }
        else if (unit.equals("KB")) {
            factor = 1024;
        }

        return size * factor;
    }

    public static byte[] generatePdfFromHtml(String html, String name) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(html, buffer);
        byte[] pdfAsBytes = buffer.toByteArray();
        try (FileOutputStream fos = new FileOutputStream(name)) {
            fos.write(pdfAsBytes);
        }

        return pdfAsBytes;
    }

    public static String getFirstCharacterOfContact(String osNameOrEmail) {
        var firstCharacterOfContact = Character.toString(osNameOrEmail.charAt(0));
        if(Strings.isNullOrEmpty(osNameOrEmail)) {
            var characters = osNameOrEmail.split(" ");
            firstCharacterOfContact =  Character.toString(osNameOrEmail.charAt(0));
            if(characters.length > 1) {
                firstCharacterOfContact = firstCharacterOfContact + characters[1];
            }
        }

        return firstCharacterOfContact;
    }

    public static int getDayNumberOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static String getDayStringOfWeek(Integer dateNumber) {
        switch (dateNumber) {
            case Calendar.MONDAY: return "Mon";
            case Calendar.TUESDAY: return "Tue";
            case Calendar.WEDNESDAY: return "Wed";
            case Calendar.THURSDAY: return "Thu";
            case Calendar.FRIDAY: return "Fri";
            case Calendar.SATURDAY: return "Sat";
            case Calendar.SUNDAY: return "Sun";
            default: return null;
        }
    }

    public static boolean isNumeric(String string) {
        // Checks if the provided string
        // is a numeric by applying a regular
        // expression on it.
        String regex = "[0-9]+[\\.]?[0-9]*";
        return Pattern.matches(regex, string);
    }
}
