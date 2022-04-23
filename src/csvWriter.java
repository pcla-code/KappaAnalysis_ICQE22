import java.io.*;
import java.time.LocalDateTime;

public class csvWriter {

    StringBuilder sb;
    FileWriter fout;

    public csvWriter(String[] features) throws IOException {
        fout = new FileWriter(LocalDateTime.now().toString().split("\\.")[0] + ".csv", true);

        for (String feature : features) {
            fout.append(feature);
            fout.append(',');
        }
        fout.append('\n');

        System.out.println("Created CSV File!");

    }

    public void addValues(int[] values) {
        for (int value : values) {
            sb.append(value);
            sb.append(",");
        }

        sb.append('\n');
    }

    public void create() throws IOException {
        fout.write(sb.toString());
        System.out.println("Created!");

        fout.close();
    }
}
