package com.ray.neardrop;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
  public static final int DEFAULT_PORT = 5487;
  public static final int BACKUP_PORT = 9453;

  private Main mainThread;
  private StateWrapper stateWrapper;
  private List<String> clientCandidates;
  private ServerSocket serverSocket;
  private List<Socket> clientSockets = new ArrayList<>();

  public Server(Main m, StateWrapper s, List<String> can) {
    mainThread = m;
    stateWrapper = s;
    clientCandidates = can;
  }

  public void run() {
    try {
      hostServer();
      String message = receiveMessage();
      waitUntilEvents();
      // requestNewClient("192.168.1.1");

    } catch (IOException e) {
      e.printStackTrace();
    }

    // End of the thread
    try {
      serverSocket.close();
      for (Socket s : clientSockets) {
        s.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void hostServer() throws IOException {
    serverSocket = new ServerSocket(DEFAULT_PORT);
  }

  private void waitUntilEvents() {

  }

  private String receiveMessage() throws IOException {
    /*
     * Credit: Example of sending a String through a Socket in Java. · GitHub
     * https://gist.github.com/chatton/8955d2f96f58f6082bde14e7c33f69a6
     */
    System.out.println("ServerSocket awaiting connections...");
    clientSockets.add(serverSocket.accept()); // blocking call
    Socket clientSocket = clientSockets.get(clientSockets.size() - 1);
    DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
    return dataInputStream.readUTF();
  }

  // private void receiveFile(String outFilename) throws FileNotFoundException,
  // IOException {
  // int bytesRead;
  // int current = 0;
  // try (FileOutputStream fos = new FileOutputStream(outFilename);
  // BufferedOutputStream bos = new BufferedOutputStream(fos);) {
  // System.out.println("Connecting...");

  // // receive file
  // byte[] mybytearray = new byte[10 * 1024];
  // InputStream is = serverSocket.getInputStream();
  // bytesRead = is.read(mybytearray, 0, mybytearray.length);

  // current = bytesRead;

  // do {
  // bytesRead = is.read(mybytearray, current, (mybytearray.length - current));
  // if (bytesRead >= 0)
  // current += bytesRead;
  // } while (bytesRead > -1);

  // bos.write(mybytearray, 0, current);
  // bos.flush();
  // System.out.println("File " + outFilename
  // + " downloaded (" + current + " bytes read)");
  // }
  // }

  public void requestNewClient(String clientIP) {
    synchronized (mainThread) {
      stateWrapper.state = State.NEW_CLIENT_REQUEST;
      clientCandidates.add(clientIP);
      mainThread.notify();
    }
  }
}