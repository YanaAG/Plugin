package timetracker;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TrackerCommitToGit {
    private String wayToProject;
    private int beforeSession;
    private FileWriter writer;
    private FileRepositoryBuilder fileRepositoryBuilder = new FileRepositoryBuilder();
    private Repository repository;
    private Git git;

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
        try {
            writer = new FileWriter("D:/way.txt", true);
            writer.write("Before session " + beforeSession + "\r\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void countAfterSession(){
        int afterSession = countCommitToGit();
        try {
            writer = new FileWriter("D:/way.txt", true);
            writer.write("After session " + afterSession + "\r\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}