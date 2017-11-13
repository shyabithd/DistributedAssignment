package distributionassignment.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Created by Samiththa on 4/11/16.
 */
public class TextStore {
    private ArrayList<FileDetail> files;

    

    public TextStore(ArrayList<FileDetail> files) {
        this.files = files;
    }

    public ArrayList<FileDetail> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<FileDetail> files) {
        this.files = files;
    }

    public boolean isContains(String file) {
        boolean iscontain = false;
        for (FileDetail fileDetail : files) {
            if (fileDetail.equals(file.toLowerCase().trim())) {
                iscontain = true;
            }
        }
        return iscontain;
    }

    public ArrayList<FileDetail> returnAllPartialMatches(String file) {
        ArrayList<FileDetail> matches = new ArrayList<>();
        String pattern = "(.*)" + file.toLowerCase() + "(.*)";

        for (FileDetail s : files) {
            System.out.println("" + s.getFileName()+ " file searched: "+ file);
            if (s.getFileName().toLowerCase().matches(pattern)) {
                matches.add(s);
            }
        }
        
        return matches;
    }

    public void printFileList() {
        System.out.println("#####################################################");
        System.out.format("%-30s%-10s%-30s \n", "File", "Rate", "Comments");
        files.stream().forEach((FileDetail file) -> {
            String commentList = "";
            commentList = file.getCommentList().stream().map((comment) -> comment+"\n\t\t\t\t\t").reduce(commentList, String::concat);
            System.out.format("%-30s%-10s%-30s \n", file.getFileName(), String.valueOf(file.getFileRate()), commentList);
        });
        System.out.println("#####################################################");
    }
}
