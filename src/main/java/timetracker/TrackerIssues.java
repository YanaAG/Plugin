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
import javax.swing.*;

public class TrackerIssues {
    private Vector<Integer> openIssue;
    private Vector<Integer> closeIssue;
    private int delay = 240000;
    private GitHubClient client;
    private IRepositoryIdProvider repoId;
    private IssueService issueService;
    private FileWriter writer;
    private Timer timer;
    private JFrame frame, frameNewIssue;
    private JEditorPane textArea, textAreaNewIssue;
    private JScrollPane scrollPane, scrollPaneNewIssue;


    public TrackerIssues(){
        client = new GitHubClient();
        client.setCredentials("isrpo", "Jjdf34fc");
        repoId = new RepositoryId("isrpo", "Tests");
        issueService = new IssueService(client);
        openIssue = new Vector<>();
        closeIssue = new Vector<>();
        frame = new JFrame("Issues on GitHub");
        frame.setBounds(100, 100, 765, 400);
        textArea = new JEditorPane("text/html", "");
        scrollPane = new JScrollPane();
        scrollPane.setBounds(100, 100, 400, 400);
        frame.getContentPane().add(scrollPane);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        frameNewIssue = new JFrame("Changes in issues");
        frameNewIssue.setBounds(100, 100, 765, 400);
        textAreaNewIssue = new JEditorPane("text/html", "");
        scrollPaneNewIssue = new JScrollPane();
        scrollPaneNewIssue.setBounds(100, 100, 400, 400);
        frameNewIssue.getContentPane().add(scrollPaneNewIssue);
        scrollPaneNewIssue.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
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
        String strOpen = "", strClose = "";
        try {
            for (SearchIssue searchIssue : issueService.searchIssues(repoId, "all", " ")) {
                if (searchIssue.getState().contains("open")){
                    openIssue.add(searchIssue.getNumber());
                    strOpen += " <b>" + searchIssue.getTitle() + " - #" + searchIssue.getNumber() + "</b>" + "<br>" + "<i> State: </i>" + searchIssue.getState() + "<br>"
                            + "<i> Comment: </i>" + searchIssue.getBody() + "<br>" + "<i> Created: </i>" +
                            searchIssue.getCreatedAt() + "<br>" + "<i> Updated: </i>" + searchIssue.getUpdatedAt() + "<br>" +
                            "<i> Url: </i>" + searchIssue.getHtmlUrl() + "<br><br>";
                } else {
                    closeIssue.add(searchIssue.getNumber());
                    strClose += " <b>" + searchIssue.getTitle() + " - #" + searchIssue.getNumber() + "</b>" + "<br>" + "<i> State: </i>" + searchIssue.getState() + "<br>"
                            + "<i> Comment: </i>" + searchIssue.getBody() + "<br>" + "<i> Created: </i>" +
                            searchIssue.getCreatedAt() + "<br>" + "<i> Updated: </i>" + searchIssue.getUpdatedAt() + "<br>" +
                            "<i> Url: </i>" + searchIssue.getHtmlUrl() + "<br><br>";
                }
            }
            textArea.setText(strOpen + strClose);
        } catch (IOException e) {
            e.printStackTrace();
        }
        scrollPane.setViewportView(textArea);
        frame.setVisible(true);
    }

    public void checkIssue(){
        int i = 0;
        String strNewOpen = "", strNewClose = "", res = "";
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
                    strNewOpen += " <b>" + searchIssue.getTitle() + " - #" + searchIssue.getNumber() + "</b>" + "<br>" + "<i> State: </i>" + searchIssue.getState() + "<br>"
                            + "<i> Comment: </i>" + searchIssue.getBody() + "<br>" + "<i> Created: </i>" +
                            searchIssue.getCreatedAt() + "<br>" + "<i> Updated: </i>" + searchIssue.getUpdatedAt() + "<br>" +
                            "<i> Url: </i>" + searchIssue.getHtmlUrl() + "<br><br>";
                    i++;
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
                        strNewClose += " <b>" + searchIssue.getTitle() + " - #" + searchIssue.getNumber() + "</b>" + "<br>" + "<i> State: </i>" + searchIssue.getState() + "<br>"
                                + "<i> Comment: </i>" + searchIssue.getBody() + "<br>" + "<i> Created: </i>" +
                                searchIssue.getCreatedAt() + "<br>" + "<i> Updated: </i>" + searchIssue.getUpdatedAt() + "<br>" +
                                "<i> Url: </i>" + searchIssue.getHtmlUrl() + "<br><br>";
                        i++;
                        //writer.write(searchIssue.getTitle() + " " + searchIssue.getState() + " " + searchIssue.getBody() + "\r\n");
                    }
                }
            }
            res = strNewOpen + strNewClose;
            if (i != 0){
                showNewIssue(res);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stopTracking(){
        timer.stop();
    }

    public void showNewIssue(String str){
        textAreaNewIssue.setText(str);
        scrollPaneNewIssue.setViewportView(textAreaNewIssue);
        frameNewIssue.setVisible(true);
    }
}