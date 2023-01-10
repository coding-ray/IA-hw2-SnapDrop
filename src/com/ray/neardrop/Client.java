package com.ray.neardrop;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.NotYetConnectedException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Client implements Runnable {
  private Main mainThread;
  private StateWrapper stateWrapper;
  private List<String> serverCandidates;

  private Socket socket;
  private Socket clientSocket;

  public Client(Main m, StateWrapper s, List<String> can) {
    mainThread = m;
    stateWrapper = s;
    serverCandidates = can;
  }

  public void run() {
    // List the (IPv4) IP addresses of all the other devices
    // in the same local area network (LAN)
    List<String> hosts = new ArrayList<>();

    try {
      hosts = Utils.getOtherHostsInLAN(20);
    } catch (InvalidParameterException | IOException e) {
      e.printStackTrace();
      hosts = Collections.emptyList();
    }

    hosts.removeIf(host -> !hasServer(host));
    if (hosts.isEmpty())
      hostServerAndWait();
    else {
      System.out.println("All the available servers are listed as follows.");
      int index = 0;
      for (String host : hosts) {
        System.out.println(index + ") " + host);
        index++;
      }
      System.out.println(
          "Please choose one of them as your peer.\n" +
              "Otherwise, please enter single \"s\" to host a server on this device");
      Scanner in = new Scanner(System.in);
      String input = in.nextLine();
      if (input.equals("s"))
        hostServerAndWait();
      try {
        int option = Integer.parseInt(input);
        if (option >= 0 && option < index) {
          requestNewServer(hosts.get(option));
          sendMessage("Test");
        } else
          throw new IndexOutOfBoundsException("Index must in the range of [0," + index + "].");
      } catch (NumberFormatException | IndexOutOfBoundsException | IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static boolean hasServer(String host) {
    try (Socket s = new Socket(host, Server.DEFAULT_PORT);) {
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  private static void hostServerAndWait() {
    // todo
  }

  private void requestNewServer(String serverIP) {
    try {
      socket = new Socket(serverIP, Server.DEFAULT_PORT);
    } catch (IOException e) {
      e.printStackTrace();
    }
    // synchronized (mainThread) {
    // stateWrapper.state = State.NEW_CLIENT_REQUEST;
    // serverCandidates.add(serverIP);
    // mainThread.notify();
    // }
  }

  private void sendMessage(String message) throws IOException {
    /*
     * Credit: Example of sending a String through a Socket in Java. · GitHub
     * https://gist.github.com/chatton/8955d2f96f58f6082bde14e7c33f69a6
     */

    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

    System.out.println("Sending string to the ServerSocket");

    dataOutputStream.writeUTF("Hello from the other side!");
    dataOutputStream.flush();
    dataOutputStream.close();
  }
}
