package com.ray.neardrop;

public class StateWrapper {
  public State state;

  public StateWrapper() {
    state = State.MAIN_THREAD_IDLE;
  }

  public StateWrapper(State s) {
    state = s;
  }
}
