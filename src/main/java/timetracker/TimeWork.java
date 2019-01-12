package timetracker;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timetracker.actions.TimerConfig;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class TimeWork implements StatusBarWidget.TextPresentation, ActionListener, StatusBarWidget  {

    private enum TimerPlayState{
        DONE, RUNNING, PAUSED
    }

    private enum TimerPhaseState{
        MAIN, BREAK
    }

    private TimerPlayState playState;
    private TimerPhaseState phaseState;

    private Timer timer = new Timer(1000, this);
    private StatusBar statusBar;
    private long prevTime;
    private int secondsLeft;
    private int currentSessinsMins;
    private TimerConfig timerConfig;
    private TrackerCommitToGit trackerCommitToGit;
    private TrackerIssues trackerIssues;

    private Consumer<MouseEvent> mouseEventConsumer = new Consumer<MouseEvent>() {
        @Override
        public void consume(MouseEvent mouseEvent) {
            long clickTime = getTime();
            if ((clickTime - prevTime) < 200) {
                playState = TimerPlayState.DONE;
                phaseState = TimerPhaseState.MAIN;
            } else {
                if (playState == TimerPlayState.DONE){
                    //set commits before session; show issue before session!
                    trackerIssues.startTracking();
                    trackerCommitToGit.setBeforeSession();
                    currentSessinsMins = timerConfig.getState().sessionLength;
                    secondsLeft = currentSessinsMins * 60;
                    playState = TimerPlayState.RUNNING;
                } else if (playState == TimerPlayState.RUNNING){
                    playState = TimerPlayState.PAUSED;
                } else if (playState == TimerPlayState.PAUSED){
                    playState = TimerPlayState.RUNNING;
                }
                statusBar.updateWidget(ID());
            }
            prevTime = clickTime;
        }
    };

    public TimeWork (Project project){
        prevTime = getTime();
        secondsLeft = -1;
        phaseState = TimerPhaseState.MAIN;
        playState = TimerPlayState.DONE;
        timerConfig = ServiceManager.getService(project, TimerConfig.class);
        trackerCommitToGit = new TrackerCommitToGit(project.getBasePath() + "/.git");
        trackerIssues = new TrackerIssues();
    }

    @Override
    public void actionPerformed(ActionEvent event){
        if (playState == TimerPlayState.RUNNING){
            secondsLeft--;
            if (secondsLeft == 0){
                if (phaseState == TimerPhaseState.MAIN){
                    phaseState = TimerPhaseState.BREAK;
                    secondsLeft = BREAK_MINS * 60;
                    //count commits after session; stop tracking issues
                    trackerCommitToGit.countAfterSession();
                    trackerIssues.stopTracking();
                    popupAlert ("Session completed (" + currentSessinsMins + " mins)");
                } else {
                    phaseState = TimerPhaseState.MAIN;
                    playState = TimerPlayState.DONE;
                    secondsLeft = -1;
                    popupAlert("Break ended. Time to work.");
                }
            }
        }
        statusBar.updateWidget(ID());
    }

    private void popupAlert (String text){
        JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(text, MessageType.INFO, null)
                .setFadeoutTime(7300)
                .createBalloon()
                .show(RelativePoint.getCenterOf(this.statusBar.getComponent()), Balloon.Position.atRight);
    }

    @NotNull
    @Override
    public  String getText(){
        if (playState == TimerPlayState.DONE){
            return "Start Timer";
        } else {
            String str = "";
            if (playState == TimerPlayState.RUNNING){
                str += "|| " + formatSecondsLeft(secondsLeft);
            } else {
                str += "|> " + formatSecondsLeft(secondsLeft);
            }
            return str;
        }
    }

    private String formatSecondsLeft(long seconds){
        long min = seconds / 60;
        long sec = seconds % 60;
        return (min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec;
    }

    @NotNull
    @Override
    public String ID() {
        return "IdeaTimer";
    }

    @NotNull
    @Override
    public StatusBarWidget.WidgetPresentation getPresentation(StatusBarWidget.PlatformType platformType){
        return this;
    }

    @Override
    public void install(StatusBar statusBar){
        this.timer.start();
        this.statusBar = statusBar;
    }

    @Override
    public void dispose(){
        this.timer.stop();
        statusBar = null;
    }

    @Override
    public float getAlignment() {
        return 0.5f;
    }

    @Nullable
    @Override
    public String getTooltipText() {
        return "";
    }

    @Nullable
    @Override
    public Consumer<MouseEvent> getClickConsumer(){
        return mouseEventConsumer;
    }

    private long getTime(){
        return System.currentTimeMillis();
    }

    final private int BREAK_MINS = 5;
}