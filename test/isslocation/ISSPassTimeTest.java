package isslocation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ISSPassTimeTest {
  private ISSPassTime issPassTime;
  private ISSWebService mockIssWebService;

  @BeforeEach
  public void init() {
    mockIssWebService = mock(ISSWebServiceImpl.class);

    issPassTime = new ISSPassTime();
    issPassTime.setService(mockIssWebService);
  }

  @Test
  public void Canary() {
    assert (true);
  }

  @Test
  public void getTimeStamp() {
    assertAll(
      () -> assertEquals("January 1, 1970, 00:00:01",
        issPassTime.convertTimeStampToUTC(1)),
      () -> assertEquals("January 1, 1970, 00:00:02",
        issPassTime.convertTimeStampToUTC(2)),
      () -> assertEquals("January 1, 1970, 00:01:00",
        issPassTime.convertTimeStampToUTC(60)),
      () -> assertEquals("September 22, 2019, 05:39:09",
        issPassTime.convertTimeStampToUTC(1569130749))
    );
  }

  @Test
  public void getCorrectedDateForHouston() {
    assertAll(
      () -> assertEquals("December 31, 1969, 18:00:01",
        issPassTime.convertTimeStampToTimeAtLatLon(1, 29.721359, -95.343003)),
      () -> assertEquals("December 31, 1969, 18:00:02",
        issPassTime.convertTimeStampToTimeAtLatLon(2, 29.721359, -95.343003)),
      () -> assertEquals("December 31, 1969, 18:01:00",
        issPassTime.convertTimeStampToTimeAtLatLon(
          60, 29.721359, -95.343003)),
      () -> assertEquals("September 22, 2019, 00:39:09",
        issPassTime.convertTimeStampToTimeAtLatLon(
          1569130749, 29.7604, -95.343003))
    );
  }

  @Test
  public void getCorrectedDateForNYC() {
    assertAll(
      () -> assertEquals("December 31, 1969, 19:00:01",
        issPassTime.convertTimeStampToTimeAtLatLon(1, 40.755931, -73.984606)),
      () -> assertEquals("December 31, 1969, 19:00:02",
        issPassTime.convertTimeStampToTimeAtLatLon(2, 40.755931, -73.984606)),
      () -> assertEquals("December 31, 1969, 19:01:00",
        issPassTime.convertTimeStampToTimeAtLatLon(
          60, 40.755931, -73.984606)),
      () -> assertEquals("September 22, 2019, 01:39:09",
        issPassTime.convertTimeStampToTimeAtLatLon(
          1569130749, 40.755931, -73.984606))
    );
  }

  @Test
  public void getCorrectedDateForSingapore() {
    assertAll(
      () -> assertEquals("January 1, 1970, 07:30:01",
        issPassTime.convertTimeStampToTimeAtLatLon(1, 1.356203, 103.828142)),
      () -> assertEquals("January 1, 1970, 07:30:02",
        issPassTime.convertTimeStampToTimeAtLatLon(2, 1.356203, 103.828142)),
      () -> assertEquals("January 1, 1970, 07:31:00",
        issPassTime.convertTimeStampToTimeAtLatLon(60, 1.356203, 103.828142)),
      () -> assertEquals("September 22, 2019, 13:39:09",
        issPassTime.convertTimeStampToTimeAtLatLon(
          1569130749, 1.356203, 103.828142))
    );
  }

  @Test
  public void ensureFetchIssFlyOverDataWasCalled() {
    when(mockIssWebService.fetchIssFlyOverData(
      29.721359, -95.343003)).thenReturn(2L);

    issPassTime.computeTimeOfFlyOver(29.721359, -95.343003);

    verify(mockIssWebService).fetchIssFlyOverData(29.721359, -95.343003);
  }

  @Test
  public void getComputedFlyOverTimeForHouston() {
    when(mockIssWebService.fetchIssFlyOverData(
      29.721359, -95.343003)).thenReturn(2L);

    assertEquals("December 31, 1969, 18:00:02",
      issPassTime.computeTimeOfFlyOver(29.721359, -95.343003));
  }

  @Test
  public void reportsErrorDueToErrorFromWebService() {
    when(mockIssWebService.fetchIssFlyOverData(
      -100, -100))
      .thenThrow(new RuntimeException("Lat out of range."));

    assertEquals("Lat out of range.",
      issPassTime.computeTimeOfFlyOver(-100, -100));
  }

  @Test
  public void reportsNetworkFailureFromWebService() {
    when(mockIssWebService.fetchIssFlyOverData(
      29.721359, -95.343003))
      .thenThrow(new RuntimeException("Network error."));

    assertEquals("Network error.",
      issPassTime.computeTimeOfFlyOver(29.721359, -95.343003));
  }
}