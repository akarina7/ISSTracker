package isslocation;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URL;

import static java.util.stream.Collectors.joining;

public class ISSWebServiceImpl implements ISSWebService {
  @Override
  public long fetchIssFlyOverData(double lat, double lon) {
    try {
      return parseData(getDataFromURL(lat, lon));
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public String getDataFromURL(double lat, double lon) throws Exception {
    String uri = "http://api.open-notify.org/iss-pass.json?lat=%.6f&lon=%.6f";
    String uriParam = String.format(uri, lat, lon);

    return new java.util.Scanner(new URL(uriParam).openStream())
      .tokens().collect(joining(""));
  }

  public long parseData(String json) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    JsonNode node = mapper.readTree(json);
    String message = node.get("message").asText();
    if ("failure".equalsIgnoreCase(message)) {
      String reason = node.get("reason").asText();
      throw new RuntimeException(reason);
    }
    String riseTime = node.get("response").get(0).get("risetime").asText();
    return Long.parseLong(riseTime);
  }
}
