package timetracker;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;

public class TimerStatusBar implements ProjectComponent {
    private Project project;

    public TimerStatusBar(Project p){
        project = p;
    }

    @Override
    public void projectOpened() {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
        if (statusBar != null)
            statusBar.addWidget(new TimeWork(project), "before ReadOnlyAttribute");
    }

    @Override
    public void projectClosed() {

    }
}