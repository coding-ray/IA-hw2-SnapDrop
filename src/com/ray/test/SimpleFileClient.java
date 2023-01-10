package com.ray.test;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SimpleFileClient {

  public static final int SOCKET_PORT = 5501;
  public static final String SERVER = "127.0.0.1";
  public static final String FILE_TO_RECEIVED = "file-rec.txt";

  public static final int FILE_SIZE = 10 * 1024;

  public static void main(String[] args) throws IOException {
    int bytesRead;
    int current = 0;
    try (FileOutputStream fos = new FileOutputStream(FILE_TO_RECEIVED);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        Socket sock = new Socket(SERVER, SOCKET_PORT);) {
      System.out.println("Connecting...");

      // receive file
      byte[] mybytearray = new byte[10 * 1024];
      InputStream is = sock.getInputStream();
      bytesRead = is.read(mybytearray, 0, mybytearray.length);

      current = bytesRead;

      do {
        bytesRead = is.read(mybytearray, current, (mybytearray.length - current));
        if (bytesRead >= 0)
          current += bytesRead;
      } while (bytesRead > -1);

      bos.write(mybytearray, 0, current);
      bos.flush();
      System.out.println("File " + FILE_TO_RECEIVED
          + " downloaded (" + current + " bytes read)");
    }
  }
}