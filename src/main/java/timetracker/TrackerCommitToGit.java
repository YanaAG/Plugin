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
    private FileWriter writer;

    public TrackerCommitToGit(String way){
        wayToProject = way;
        try {
            writer = new FileWriter("D:/way.txt", true);
            writer.write(wayToProject + "\r\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void countCommitToGit(){
        FileRepositoryBuilder fileRepositoryBuilder = new FileRepositoryBuilder();
        try{
            writer = new FileWriter("D:/way.txt", true);
            fileRepositoryBuilder.setMustExist(true);
            fileRepositoryBuilder.setGitDir(new File(wayToProject));
            Repository repository = fileRepositoryBuilder.build();
            Git git = new Git(repository);
            Iterable<RevCommit> log = git.log().call();
            for (RevCommit commit : log){
                writer.write("Commit: " + commit.getName() + "\r\n");
                String logMessage = commit.getFullMessage();
                writer.write("Message commit: " + logMessage + "\r\n" + "\r\n");
            }
            git.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoHeadException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }
}