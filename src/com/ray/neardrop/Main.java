package com.ray.neardrop;

import java.util.List;
import java.util.ArrayList;

public class Main {
  private static Main mainThread = new Main();
  private static List<Server> serverObjects = new ArrayList<>();
  private static List<Client> clientObjects = new ArrayList<>();
  private static List<Thread> serverThreads = new ArrayList<>();
  private static List<Thread> clientThreads = new ArrayList<>();
  private static List<String> serverCandidates = new ArrayList<>();
  private static List<String> clientCandidates = new ArrayList<>();
  private static StateWrapper stateWrapper = new StateWrapper(State.MAIN_THREAD_IDLE);

  public static void main(String[] args) throws Exception {
    Runtime.getRuntime().addShutdownHook(new ShutdownHook());
    setUpAndStartThreads();
    // while (!serverThreads.isEmpty() || !clientThreads.isEmpty()) {
    // waitForEvents();
    // System.out.println(stateWrapper.state);
    // if (stateWrapper.state == State.MAIN_THREAD_IDLE) {
    // } else if (stateWrapper.state == State.NEW_SERVER_REQUEST) {
    // for (String c : serverCandidates) {
    // System.out.println(c);
    // }
    // serverCandidates.clear();
    // stateWrapper.state = State.MAIN_THREAD_IDLE;
    // } else if (stateWrapper.state == State.NEW_CLIENT_REQUEST) {
    // for (String c : clientCandidates) {
    // System.out.println(c);
    // }
    // clientCandidates.clear();
    // stateWrapper.state = State.MAIN_THREAD_IDLE;
    // }
    // }

  }

  private static void setUpAndStartThreads() {
    serverObjects.add(new Server(mainThread, stateWrapper, clientCandidates));
    clientObjects.add(new Client(mainThread, stateWrapper, serverCandidates));
    serverThreads.add(new Thread(serverObjects.get(0), "Server 1"));
    clientThreads.add(new Thread(clientObjects.get(0), "Client 1"));
    serverThreads.get(0).start();
    Utils.sleep(100);
    clientThreads.get(0).start();
  }

  private static void waitForEvents() {
    /*
     * Credit: Java Thread notify() Method with Examples - Javatpoint
     * https://www.javatpoint.com/java-thread-notify-method
     */
    while (serverCandidates.isEmpty() && clientCandidates.isEmpty())
      synchronized (mainThread) {
        try {
          mainThread.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
          Thread.currentThread().interrupt();
        }
      }
  }
}
