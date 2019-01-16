package timetracker;

import org.apache.commons.lang3.tuple.Triple;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;

public class TrackerCommitToGit {
    private String wayToProject;
    private int beforeSession;
    private FileWriter writer;
    private FileRepositoryBuilder fileRepositoryBuilder = new FileRepositoryBuilder();
    private Repository repository;
    private Git git;
    private Vector<Triple<String, String, String>> allCommits = new Vector<>();
    private JFrame frame;
    private JEditorPane textArea;
    private JScrollPane scrollPane;



    public TrackerCommitToGit(String way){
        wayToProject = way;
        fileRepositoryBuilder.setMustExist(true);
        fileRepositoryBuilder.setGitDir(new File(wayToProject));
        try {
            repository = fileRepositoryBuilder.build();
            git = new Git(repository);
        } catch (IOException e) {
            e.printStackTrace();
        }
        frame = new JFrame("Commits");
        frame.setBounds(100, 100, 765, 400);
        textArea = new JEditorPane("text/html", "");
        scrollPane = new JScrollPane();
        scrollPane.setBounds(100, 100, 400, 400);
        frame.getContentPane().add(scrollPane);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    }

    public int countCommitToGit(){
        int count = 0;
        try {
            Iterable<RevCommit> log = git.log().call();
            for (RevCommit commit : log){
                count++;
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        git.close();
        return (count);
    }

    public void setBeforeSession(){
        beforeSession = countCommitToGit();
        allCommits.clear();
        commitBeforeSession();
    }

    public void countAfterSession(){
        int afterSession = countCommitToGit(), diff;
        String str = "";
        if (afterSession != beforeSession) {
            diff = afterSession - beforeSession;
            try {
                writer = new FileWriter("D:/way.txt", true);
                writer.write("After session: \r\n");
                Iterable<RevCommit> log = git.log().call();
                for (RevCommit commit : log){
                    if (diff != 0){
                        str += "<i> Commit: </i>" + "<b>" + commit.getName() + "</b>" + "<br>" + "<i> Commit message: </i>" + commit.getFullMessage() + "<br>"
                                + "<i> Commit time: </i>" + new Date(commit.getCommitTime() * 1000L) + "<br><br>";
                        writer.write("Commit: " + commit.getName() + "\r\n");
                        writer.write("Commit message: " + commit.getFullMessage() + "\r\n");
                        writer.write("Commit time: " + new Date(commit.getCommitTime() * 1000L)  + "\r\n" + "\r\n");
                        diff--;
                    }
                }
                textArea.setText(str);
                writer.close();
                scrollPane.setViewportView(textArea);
                frame.setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoHeadException e) {
                e.printStackTrace();
            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        }
    }

    public void commitBeforeSession(){
        Integer time;
        String name, message;
        try {
            Iterable<RevCommit> log = git.log().call();
            for (RevCommit commit : log){
                name = commit.getName();
                message = commit.getFullMessage();
                time = commit.getCommitTime();
                allCommits.add(Triple.of(name, message, time.toString()));
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        git.close();
    }
}