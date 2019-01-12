package timetracker;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.SearchIssue;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import javax.swing.Timer;

public class TrackerIssues {
    private Vector<Integer> openIssue;
    private Vector<Integer> closeIssue;
    private int delay = 240000;
    private GitHubClient client;
    private IRepositoryIdProvider repoId;
    private IssueService issueService;
    private FileWriter writer;
    private Timer timer;

    public TrackerIssues(){
        client = new GitHubClient();
        client.setCredentials("isrpo", "Jjdf34fc");
        repoId = new RepositoryId("isrpo", "Tests");
        issueService = new IssueService(client);
        openIssue = new Vector<>();
        closeIssue = new Vector<>();
    }

    public void startTracking(){
        openIssue.clear();
        closeIssue.clear();
        issueOnGitHub();
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkIssue();
            }
        };
        timer = new Timer(delay, actionListener);
        timer.setRepeats(true);
        timer.start();
    }

    public void issueOnGitHub(){
        try {
            for (SearchIssue searchIssue : issueService.searchIssues(repoId, "all", " ")) {
                if (searchIssue.getState().contains("open")){
                    openIssue.add(searchIssue.getNumber());
                } else {
                    closeIssue.add(searchIssue.getNumber());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkIssue(){
        try {
            writer = new FileWriter("D:/issue.txt", true);
            for (SearchIssue searchIssue : issueService.searchIssues(repoId, "all", " ")){
                if ((searchIssue.getState().contains("open")) & !(openIssue.contains(searchIssue.getNumber()))) {
                    //add to vector openIssue
                    if (closeIssue.contains(searchIssue.getNumber())){
                        writer.write("Change on open - " + searchIssue.getNumber() + "\r\n");
                        closeIssue.removeElement(searchIssue.getNumber());
                    } else {
                        writer.write("New open issue - " + searchIssue.getNumber() + "\r\n");
                    }
                    openIssue.add(searchIssue.getNumber());
                    //writer.write(searchIssue.getTitle() + " " + searchIssue.getState() + " " + searchIssue.getBody() + "\r\n");
                } else {
                    if ((searchIssue.getState().contains("closed")) & !(closeIssue.contains(searchIssue.getNumber()))) {
                        //add to vector closeIssue
                        if (openIssue.contains(searchIssue.getNumber())) {
                            writer.write("Change on close - " + searchIssue.getNumber() + "\r\n");
                            openIssue.removeElement(searchIssue.getNumber());
                        } else {
                            writer.write("New close issue - " + searchIssue.getNumber() + "\r\n");
                        }
                        closeIssue.add(searchIssue.getNumber());
                        //writer.write(searchIssue.getTitle() + " " + searchIssue.getState() + " " + searchIssue.getBody() + "\r\n");
                    }
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopTracking(){
        timer.stop();
    }
}