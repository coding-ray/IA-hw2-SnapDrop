package com.ray.neardrop;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

public class Utils {
  private static final Pattern DOT_PATTERN = Pattern.compile("\\.");
  private static final Pattern LAN_IP_PATTERN = Pattern.compile("192\\.168");

  public static String[] getMyIPInLAN() throws SocketException {
    Enumeration<NetworkInterface> networkInterface = NetworkInterface.getNetworkInterfaces();
    while (networkInterface.hasMoreElements()) {
      NetworkInterface ni = networkInterface.nextElement();
      List<InterfaceAddress> iaList = ni.getInterfaceAddresses();
      for (InterfaceAddress ia : iaList) {
        String ipString = ia.getAddress().getHostAddress();
        boolean isInLAN = LAN_IP_PATTERN.matcher(ipString).find();
        if (isInLAN) {
          String numberOfSubnetMaskBits = Short.toString(ia.getNetworkPrefixLength());
          return new String[] { ipString, numberOfSubnetMaskBits };
        }
      }
    }
    return new String[0];
  }

  public static List<String> getOtherHostsInLAN(int timeout) throws InvalidParameterException, IOException {
    String[] ipAndSubnet = new String[0];
    try {
      ipAndSubnet = getMyIPInLAN();
    } catch (SocketException e) {
      e.printStackTrace();
      return Collections.emptyList();
    }

    if (ipAndSubnet.length != 2) {
      throw new IndexOutOfBoundsException("Error in getting the IP of this device in LAN.");
    }

    String myAddr = ipAndSubnet[0];
    int numberOfSubnetMaskBits = Integer.parseInt(ipAndSubnet[1]);
    return getHostsInLAN(myAddr, numberOfSubnetMaskBits, timeout, true);
  }

  public static List<String> getHostsInLAN(
      String myAddr,
      int numberOfSubnetMaskBits /* only 24 is supported now */,
      int timeout,
      boolean toIgnoreMyself) throws InvalidParameterException, IOException {
    /*
     * Credit:
     * How to find all IP in a network using JAVA – Quora.
     * https://www.quora.com/How-do-I-find-all-IP-in-a-network-using-JAVA
     */

    String subnet = getSubnet(myAddr, numberOfSubnetMaskBits);
    checkSubnetMaskBits(subnet, numberOfSubnetMaskBits);
    int addrCount = (1 << (32 - numberOfSubnetMaskBits)) - 1;
    List<String> result = new ArrayList<>();
    for (int i = 1; i < addrCount; i++) {
      String hostIP = subnet + "." + i;
      if (!(toIgnoreMyself && hostIP.equals(myAddr)) &&
          InetAddress.getByName(hostIP).isReachable(timeout))
        result.add(hostIP);
    }

    return result;
  }

  public static String getSubnet(String ip, int numberOfSubnetMaskBits /* only 24 is supported now */) {
    /*
     * Credit: networking - How to get subnet mask of local system using java? -
     * Stack Overflow.
     * https://stackoverflow.com/a/1221581
     */

    String[] bytes = DOT_PATTERN.split(ip);
    if (numberOfSubnetMaskBits != 24) {
      return "";
    }

    StringBuilder result = new StringBuilder();
    result.append(bytes[0]);
    result.append(".");
    result.append(bytes[1]);
    result.append(".");
    result.append(bytes[2]);

    return result.toString();
  }

  public static void checkSubnetMaskBits(String subnet, int n) throws InvalidParameterException {
    if (n > 32 || n < 0) {
      throw new InvalidParameterException("\"" + n + "\"" +
          " as the number of the subnet-mask bits indicate that the input subnet \"" + subnet + "\" is not IPv4.");
    }

    if (n != 24) {
      throw new InvalidParameterException(
          "Currently, only 24 as the number of the subnet-mast bits is allowed. But received \"" + n + "\"");
    }
  }

  public static void sleep(long milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException e) {
      e.printStackTrace();
      Thread.currentThread().interrupt();
    }
  }
}
