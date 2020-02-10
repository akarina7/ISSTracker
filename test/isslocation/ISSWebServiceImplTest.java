package isslocation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ISSWebServiceImplTest {

  private ISSWebServiceImpl mockIssWebService;
  private ISSWebServiceImpl spyIssWebService;


  @BeforeEach
  public void init() {
    mockIssWebService = mock(ISSWebServiceImpl.class);
    spyIssWebService = spy(ISSWebServiceImpl.class);
  }

  @Test
  public void parseDataAndGetFirstTimeStamp() throws Exception {
    String json = "{  \"message\": \"success\",  \"request\": {\"latitude\": 29.721359,\"longitude\": -95.343003,\"altitude\": \"ALTITUDE\",\"passes\": \"NUMBER_OF_PASSES\",\"datetime\": \"REQUEST_TIMESTAMP\"},\"response\": [{\"risetime\": 2, \"duration\": \"DURATION\"} ]}";

    assertEquals(2L, spyIssWebService.parseData(json));
  }

  @Test
  public void parseDataAndGetSecondTimeStamp() throws Exception {
    String json = "{  \"message\": \"success\",  \"request\": {\"latitude\": 29.721359,\"longitude\": -95.343003,\"altitude\": \"ALTITUDE\",\"passes\": \"NUMBER_OF_PASSES\",\"datetime\": \"REQUEST_TIMESTAMP\"},\"response\": [{\"risetime\": 24, \"duration\": \"DURATION\"} ]}";

    assertEquals(24L, spyIssWebService.parseData(json));
  }

  @Test
  public void parseDataForLatTooLarge() throws Exception {
    String json = "{\"message\": \"failure\", \"reason\": \"Latitude must be number between -90.0 and 90.0\" }";

    var exception = assertThrows(RuntimeException.class, () ->
      spyIssWebService.parseData(json));

    assertEquals("Latitude must be number between -90.0 and 90.0",
      exception.getMessage());
  }

  @Test
  public void parseDataForLonTooLarge() throws Exception {
    String json = "{\"message\": \"failure\", \"reason\": \"Longitue must be number between -180.0 and 180.0\" }";

    var exception = assertThrows(RuntimeException.class, () ->
      spyIssWebService.parseData(json));

    assertEquals("Longitue must be number between -180.0 and 180.0",
      exception.getMessage());
  }

  @Test
  public void parseEmptyData() throws Exception {

    assertThrows(Exception.class, () -> spyIssWebService.parseData(""));
  }

  @Test
  public void ensureFetchIssFlyOverDataIsCalledAndPassesResponseToParseData() throws Exception {
    String json = "{  \"message\": \"success\",  \"request\": {\"latitude\": 29.721359,\"longitude\": -95.343003,\"altitude\": \"ALTITUDE\",\"passes\": \"NUMBER_OF_PASSES\",\"datetime\": \"REQUEST_TIMESTAMP\"},\"response\": [{\"risetime\": 24, \"duration\": \"DURATION\"} ]}";
    String response = "This is some response data! YAY! I LOVE TDD!";
    long timeStamp = 24;

    when(spyIssWebService.parseData(json)).thenReturn(timeStamp);

    when(spyIssWebService.getDataFromURL(
      29.721359, -95.343003)).thenReturn(json);

    spyIssWebService.fetchIssFlyOverData(
      29.721359, -95.343003);

    verify(spyIssWebService).parseData(json);

    verify(spyIssWebService).getDataFromURL(
      29.721359, -95.343003);
  }

  @Test
  public void ensureFetchIssFlyOverDataReturnsTimeStampReturnedByParse() throws Exception {
    String json = "{  \"message\": \"success\",  \"request\": {\"latitude\": 29.721359,\"longitude\": -95.343003,\"altitude\": \"ALTITUDE\",\"passes\": \"NUMBER_OF_PASSES\",\"datetime\": \"REQUEST_TIMESTAMP\"},\"response\": [{\"risetime\": 24, \"duration\": \"DURATION\"} ]}";
    long timeStamp = 24;

    when(spyIssWebService.getDataFromURL(
      29.721359, -95.343003)).thenReturn(json);
    when(spyIssWebService.parseData(json)).thenReturn(timeStamp);

    long flyOverData = spyIssWebService.fetchIssFlyOverData(29.721359, -95.343003);

    assertEquals(flyOverData, timeStamp);
  }

  @Test
  public void ensureFetchIssFlyOverDataReturnsErrorReturnedByParse() throws Exception {
    String json = "{\"message\": \"failure\", \"reason\": \"Longitue must be number between -180.0 and 180.0\" }";
    String message = "Longitue must be number between -180.0 and 180.0";

    ISSWebServiceImpl webService = spy(ISSWebServiceImpl.class);

    when(webService.getDataFromURL(
      29.721359, -95.343003)).thenReturn(json);
    doThrow(new RuntimeException(message)).when(webService).parseData(json);

    try {
      long flyOverData = webService.fetchIssFlyOverData(29.721359, -95.343003);
    } catch (RuntimeException e) {

      assertEquals("Longitue must be number between -180.0 and 180.0", e.getMessage());
    }
  }

  @Test
  public void fetchIssFlyOverDataHandlesExceptionFromGetDataFromURL() throws Exception {

    when(spyIssWebService.getDataFromURL(
      29.721359, -95.343003)).thenThrow(new IOException("Network Error"));

    var exception = assertThrows(RuntimeException.class, () ->
      spyIssWebService.fetchIssFlyOverData(29.721359, -95.343003));

    assertEquals("Network Error", exception.getMessage());
  }

  @Test
  public void ensureFetchIssFlyOverDataReturnsNetworkErrorReturnedByService() throws Exception {
    String message = "Network error.";

    when(spyIssWebService.getDataFromURL(
      29.721359, -95.343003)).thenReturn("");
    doThrow(new RuntimeException(message)).when(spyIssWebService).parseData("");

    try {
      long flyOverData = spyIssWebService.fetchIssFlyOverData(29.721359, -95.343003);
    } catch (RuntimeException e) {

      assertEquals(message, e.getMessage());
    }
  }

  @Test
  public void ensureFetchIssFlyOverDataReturnsSomeTimeStampForLatLon() {

    assertTrue(spyIssWebService.fetchIssFlyOverData(29.721359, -95.343003) > 0);
  }
}
