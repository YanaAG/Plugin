package timetracker.actions;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;

@State(
        name = "TimerConfig",
        storages = {
                @Storage(file = "FocusConfig.xml")
        }
)

public class TimerConfig implements PersistentStateComponent<TimerConfig.State> {
    public static class State{
        public int sessionLength = 25;
    }

    State state = new State();

    public State getState() {
        return state;
    }

    public void loadState(State s) {
        state = s;
    }
}