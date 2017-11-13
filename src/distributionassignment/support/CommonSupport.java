package distributionassignment.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by vidudaya on 4/11/16.
 */
public class CommonSupport {

    private final String BLANK = " ";

    public String getFormattedNumber(int number, int pad) {
        String formatted = String.format("%0" + pad + "d", number);
        return formatted;
    }

    public String getUniqueId() {
        String chars[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
                , "a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
        String id = "";
        Random rand = new Random();
        for (int i = 0; i < 8; ++i) {
            id = id.concat(chars[rand.nextInt(20)]);
        }
        return id;
    }

    public ArrayList<FileDetail> getRandomFileList() throws IOException {
        ArrayList<FileDetail> list = new ArrayList<FileDetail>();
        InputStream is = null;
        BufferedReader br = null;
        try {
            System.out.println("Path: "+ this.getClass().getResource("/filenames").getPath());
            is = this.getClass().getResource("/filenames").openStream();
            br = new BufferedReader(new InputStreamReader(is));
            int i = 0;
            Random rand = new Random();
            while (br.ready() && i < 5) {
                String temp = br.readLine();
                if (rand.nextInt(10) % 3 == 0) {
                    continue;
                }
                if ((rand.nextInt(10) + list.size()) % 2 == 0) {
                    FileDetail fileDetail = new FileDetail(temp);
                    list.add(fileDetail);
                    System.out.println("file : " + temp);
                    ++i;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                br.close();
            }
            if (is != null) {
                is.close();
            }
        }
        return list;
    }

    public String generateMessageToSend(String... args) {
        String messageToSend = "";
        String message = "";

        for (String value : args) {
            message = message.concat(BLANK).concat(value);
        }
        message = message.trim();
        int length = message.length() + 5;
        messageToSend = getFormattedNumber(length, 4).concat(BLANK).concat(message);

        return messageToSend;
    }

    public String getCombinedStringOfList(ArrayList<FileDetail> list) {
        String messageToSend = "";
        String stringList = "";
        for (FileDetail value : list) {
            stringList = value.getFileName().trim().replaceAll(" ", "_"); // for make a single continues string
            messageToSend = messageToSend.concat(BLANK).concat(stringList);
        }

        return messageToSend.trim();
    }
}
