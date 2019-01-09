package timetracker.actions;

import com.intellij.openapi.components.PersistentStateComponent;

public class TimerConfig implements PersistentStateComponent<TimerConfig.State> {
    public static class State{
        public int sessionLength = 1;
    }

    State state = new State();

    public State getState() {
        return state;
    }

    public void loadState(State s) {
        state = s;
    }
}