import java.io.*;
import java.util.*;

public class LoadData {
    private final ArrayList<Double> td_clean = new ArrayList<>();
    private final ArrayList<Long> td_time = new ArrayList<>();

    private final int dataLen;
    private double max_v, min_v;

    public LoadData(String dataPath, int dataLen) throws FileNotFoundException {
        this.dataLen = dataLen;

        this.loadTimeSeriesData(dataPath);
    }

    public LoadData(String dataPath, int dataLen, double max_v, double min_v) throws FileNotFoundException {
        this.dataLen = dataLen;
        this.max_v = max_v;
        this.min_v = min_v;

        this.loadTimeSeriesDataWithRange(dataPath);
    }

    public void loadTimeSeriesData(String filename) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(filename));

        max_v = Double.MIN_VALUE;
        min_v = Double.MAX_VALUE;

        sc.nextLine();  // skip table header
        for (int k = dataLen; k > 0 && sc.hasNextLine(); --k) {  // the size of td_clean is dataLen
            String[] line_str = sc.nextLine().split(",");
            // td_time
            long t = Long.parseLong(line_str[0]);
            this.td_time.add(t);
            // td_clean
            double v = Double.parseDouble(line_str[1]);
            this.td_clean.add(v);
            // standardize_prepare
            if (v > max_v) max_v = v;
            if (v < min_v) min_v = v;
        }
        // standardize
        for (int i = 0; i < td_clean.size(); ++i)
            td_clean.set(i, (td_clean.get(i) - min_v) / (max_v - min_v));
    }

    public void loadTimeSeriesDataWithRange(String filename) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(filename));

        sc.nextLine();  // skip table header
        System.out.println(filename);
        for (int k = dataLen; k > 0 && sc.hasNextLine(); --k) {  // the size of td_clean is dataLen
            String[] line_str = sc.nextLine().split(",");
            // td_time
            long t = Long.parseLong(line_str[0]);
            this.td_time.add(t);
            // td_clean
            double v = Double.parseDouble(line_str[1]);
            this.td_clean.add(v);
            // standardize_prepare
        }
        // standardize
        for (int i = 0; i < td_clean.size(); ++i)
            td_clean.set(i, (td_clean.get(i) - min_v) / (max_v - min_v));
    }

    public double[] getTd_clean() {
        double[] rtn = new double[td_clean.size()];
        for (int i = 0; i < td_clean.size(); ++i)
            rtn[i] = td_clean.get(i);
        return rtn;
    }

    public long[] getTd_time() {
        long[] rtn = new long[td_time.size()];
        for (int i = 0; i < td_time.size(); ++i)
            rtn[i] = td_time.get(i);
        return rtn;
    }

    public double getMax_v() {
        return max_v;
    }

    public double getMin_v() {
        return min_v;
    }
}
