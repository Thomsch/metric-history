package ch.thomsch.util;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

public class DateUtils {

    public static OffsetDateTime offsetDateTimeOf(Date date){
        ZoneId zoneId = ZoneOffset.ofTotalSeconds(date.getTimezoneOffset()*60);
        OffsetDateTime offsetDateTime = OffsetDateTime.ofInstant(date.toInstant(), zoneId);
        return offsetDateTime;
    }



}
