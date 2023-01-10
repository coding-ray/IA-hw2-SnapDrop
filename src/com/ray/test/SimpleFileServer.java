package com.ray.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleFileServer {

  public static final int SOCKET_PORT = 5501;
  public static final String FILE_TO_SEND = "file.txt";

  public static void main(String[] args) throws IOException {
    OutputStream os = null;
    Socket sock = null;
    try (
        ServerSocket servsock = new ServerSocket(SOCKET_PORT);) {
      while (true) {
        System.out.println("Waiting...");
        File myFile = new File(FILE_TO_SEND);
        try (
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);) {
          sock = servsock.accept();
          System.out.println("Accepted connection : " + sock);
          // send file
          byte[] mybytearray = new byte[(int) myFile.length()];
          bis.read(mybytearray, 0, mybytearray.length);
          os = sock.getOutputStream();
          System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
          os.write(mybytearray, 0, mybytearray.length);
          os.flush();
          System.out.println("Done.");
          break;
        } catch (IOException ex) {
          System.out.println(ex.getMessage() + ": An Inbound Connection Was Not Resolved");
        }
      }
    } catch (IOException ex) {
      System.out.println(ex.getMessage());
    }
  }
}