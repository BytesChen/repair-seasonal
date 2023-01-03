import java.io.*;
import java.util.*;

public class LoadData {
    private final ArrayList<Double> td_clean = new ArrayList<>();
    private final ArrayList<Long> td_time = new ArrayList<>();

    private final int dataLen;

    public LoadData(String dataPath, int dataLen) throws FileNotFoundException {
        this.dataLen = dataLen;

        this.loadTimeSeriesData(dataPath);
    }

    public void loadTimeSeriesData(String filename) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(filename));
        sc.nextLine();  // skip table header
        for (int k = dataLen; k > 0 && sc.hasNextLine(); --k) {  // the size of td_clean is dataLen
            String[] line_str = sc.nextLine().split(",");
            // td_time
            long t = Long.parseLong(line_str[0]);
            this.td_time.add(t);
            // td_clean
            double v = Double.parseDouble(line_str[1]);
            this.td_clean.add(v);
        }
    }

    public ArrayList<Double> getTd_clean() {
        return td_clean;
    }

    public ArrayList<Long> getTd_time() {
        return td_time;
    }
}
