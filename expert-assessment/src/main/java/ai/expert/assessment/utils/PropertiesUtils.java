package ai.expert.assessment.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.stream.Stream;

/**
 *
 * @author oserret
 */
public class PropertiesUtils {
    
    private static Properties FILE = new Properties();

    public static void crearProperties(String fichProperties) {
        try {
            FILE.load(new FileInputStream(fichProperties));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void crearProperties(Properties fichProperties) {
            FILE = fichProperties;
    }

    public static Properties getProperties() {
        return FILE;
    }

    public static String getProperty(String propertyName) {
        String result;

        result = FILE.getProperty(propertyName);

        if (result == null) {
            result = "";
        }

        return result;
    }

    public static void setProperty(String propertyName, String value) {
        FILE.setProperty(propertyName, value);
    }

    public static Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        try {
            int rest = Integer.parseInt(getProperty("henkel.bot.donwload.date"));
            cal.add(Calendar.DATE, rest);
        } catch (NumberFormatException e) {
            cal.add(Calendar.DATE, -1);
        }

        return cal.getTime();
    }

    public static String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(yesterday());
    }

    public static String getYesterdayDateTimeString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
        return dateFormat.format(yesterday());
    }

    public static String getYesterdayDayString() {
        DateFormat dateFormat = new SimpleDateFormat("dd");
        return dateFormat.format(yesterday());
    }

    public static String getYesterdayMonthString() {
        DateFormat dateFormat = new SimpleDateFormat("MM");
        return dateFormat.format(yesterday());
    }

    public static String getYesterdayYearString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy");
        return dateFormat.format(yesterday());
    }

    public static String changeDateFormat(String dateS, String mask1, String mask2) {
        try {
            DateFormat sourceFormat = new SimpleDateFormat(mask1);
            String dateAsString = dateS;
            Date date = sourceFormat.parse(dateAsString);

            DateFormat dateFormat = new SimpleDateFormat(mask2);
            return dateFormat.format(date);
        } catch (ParseException e) {
            return dateS;
        }
    }

    public static String readLineByLine(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
    
}