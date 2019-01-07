package timetracker;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import timetracker.actions.TimerConfig;

public class ActionSettings extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent event){
        Project project = event.getProject();
        String lenStr = Messages.showInputDialog(project, "Enter the session duration in minutes:", "Session length", Messages.getQuestionIcon());
        boolean enterTime = true;
        while(enterTime){
            try {
                if (lenStr == null) {
                    enterTime = false;
                }
                else {
                    int lenInt = Integer.parseInt(lenStr);
                    if (lenInt <= 0) throw new NumberFormatException();
                    enterTime = false;
                    TimerConfig timerConfig = ServiceManager.getService(project, TimerConfig.class);
                    TimerConfig.State newState = timerConfig.getState();
                    newState.sessionLength = lenInt;
                    timerConfig.loadState(newState);
                }
            } catch (NumberFormatException exc){
                lenStr = Messages.showInputDialog(project, "Time entered incorrectly, please try again", "Input error", Messages.getErrorIcon());
            }
        }
    }
}