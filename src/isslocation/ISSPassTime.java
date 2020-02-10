package isslocation;

import net.iakovlev.timeshape.TimeZoneEngine;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

public class ISSPassTime {
  ISSWebService issWebService;
  public String dateFormatString = "MMMM d, yyyy, HH:mm:ss";

  public void setService(ISSWebService WebService) {
    issWebService = WebService;
  }

  public String convertTimeStampToUTC(long timeStamp) {
    LocalDateTime dateTime =
      LocalDateTime.ofEpochSecond(timeStamp, 0, ZoneOffset.UTC);

    DateTimeFormatter dateFormat =
      DateTimeFormatter.ofPattern(dateFormatString, Locale.ENGLISH);
    String formattedDate = dateTime.format(dateFormat);

    return formattedDate;
  }

  public ISSPassTime() {
  }

  public String convertTimeStampToTimeAtLatLon(
    long timeStamp, double lat, double lon) {
    TimeZoneEngine engine = TimeZoneEngine.initialize();
    Optional<ZoneId> region = engine.query(lat, lon);
    String region_str = region.map(ZoneId::toString).orElse("(empty)");

    ZoneId timeZone = ZoneId.of(region_str);
    LocalDateTime parseTime =
      LocalDateTime.parse(convertTimeStampToUTC(timeStamp),
        DateTimeFormatter.ofPattern(dateFormatString));
    ZonedDateTime utcTime = parseTime.atZone(ZoneOffset.UTC);
    ZonedDateTime givenLocationDateTime =
      utcTime.withZoneSameInstant(timeZone);

    String convertedDate =
      DateTimeFormatter.ofPattern(dateFormatString)
        .format(givenLocationDateTime);

    return convertedDate;
  }

  public String computeTimeOfFlyOver(double lat, double lon) {
    try {
      return convertTimeStampToTimeAtLatLon(
        issWebService.fetchIssFlyOverData(lat, lon), lat, lon);
    } catch (Exception e) {
      return e.getMessage();
    }
  }
}
