package isslocation;


public class ISSDemo {

  public static void main(String[] args) {

    double lat = Double.parseDouble(args[0]);
    double lon = Double.parseDouble(args[1]);

    ISSPassTime passTime = new ISSPassTime();
    passTime.setService(new ISSWebServiceImpl());
    System.out.printf("The next pass time of the ISS will be %s%n",
      passTime.computeTimeOfFlyOver(lat, lon));
  }
}
