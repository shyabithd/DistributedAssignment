package distributionassignment.support;

import java.util.ArrayList;

/**
 * Created by Samiththa on 4/11/16.
 */
public class FileSearch {

    private Node distributor;

    public FileSearch(Node distributor) {
        this.distributor = distributor;
    }

    public void searchForFiles(String fileName) {
        searchLocalStore(fileName);
        distributor.getNeighbourCommunicationManager().searchFileInNetwork(fileName, distributor);
    }
    
    public void rateFile(String fileName, String fileRate) {
        ArrayList<FileDetail> localStore = searchLocalStore(fileName);
        int rateCount = 1;
        for (FileDetail file : localStore) {
            file.setFileRate(Double.parseDouble(fileRate), file.getFileRateCount());
            rateCount = file.getFileRateCount();
        }
        distributor.getNeighbourCommunicationManager().rateFileInNetwork(fileName.replace(" ", "_"), fileRate, rateCount, distributor);
    }
    public void fileComment(String fileName, String comment) {
        ArrayList<FileDetail> localStore = searchLocalStore(fileName);
        for (FileDetail file : localStore) {
            file.addComment(comment);
        }
        distributor.getNeighbourCommunicationManager().commentFileInNetwork(fileName.replace(" ", "_"), comment.replace(" ", "_"), distributor);
    }
    public ArrayList<FileDetail> searchLocalStore(String fileName) {
        System.out.println("\nSearching local file store ...");
        ArrayList<FileDetail> localStore = distributor.getTextStore().returnAllPartialMatches(fileName);
        System.out.println("Total " + localStore.size() + " records found ");
        if (localStore.size() > 0) {
            String res = "[ ";
            int i = 0;
            for (FileDetail file : localStore) {
                res = res + file.getFileName();
                i++;
                if (i == localStore.size()) {
                    res = res + " ]";
                } else {
                    res = res + ", ";
                }
            }
            System.out.println(res);
        }
        return localStore;
    }

}
