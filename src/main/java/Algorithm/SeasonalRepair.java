package Algorithm;

import java.util.ArrayList;

import Algorithm.util.DualHeap;
import Algorithm.util.Decomposition;

public class SeasonalRepair {
    private final ArrayList<Long> td_time;
    private final ArrayList<Double> td_dirty;
    private final ArrayList<Double> td_repair;
    private final int period;
    private final double k = 10.0;  // k*mad
    private final int max_iter = 10;

    private double mid, mad;
    private ArrayList<Double> seasonal, trend, residual;
    private final DualHeap dh = new DualHeap();
    private final int size;

    public SeasonalRepair(ArrayList<Long> td_time, ArrayList<Double> td_dirty, int period) throws Exception {
        this.td_time = td_time;
        this.td_dirty = td_dirty;
        this.td_repair = td_dirty;
        this.period = period;

        this.size = td_dirty.size();

        long startTime = System.currentTimeMillis();
        this.repair();
        long endTime = System.currentTimeMillis();
        System.out.println("Time cost:" + (endTime - startTime) + "ms");
    }

    private void repair() throws Exception {
        for (int h = 0; h < max_iter; ++h) {
            Decomposition de = new Decomposition(td_time, td_repair, period, "constant");
            seasonal = de.getSeasonal();
            trend = de.getTrend();
            residual = de.getResidual();

            estimate();

            boolean flag = true;
            for (int i = 0; i < size; ++i) {
                if (sub(residual.get(i), mid) > k * mad) {
                    flag = false;
                    td_repair.set(i, generate(i));
                }
            }
            if (flag) break;
        }
    }

    private void estimate() throws Exception {
        // mid
        for (double d : residual)
            dh.insert(d);
        this.mid = dh.getMedian();
        dh.clear();
        //mad
        for (double d : residual)
            dh.insert(sub(d, mid));
        this.mad = dh.getMedian();
        dh.clear();
    }

    private double generate(int pos) throws Exception {
        // in each cycle
        int i = pos % period;
        double rtn;
        for (int j = 0; j < size / period; ++j)
            if (j * period + i != pos)  // remove anomaly
                dh.insert(residual.get(j * period + i));
        if (i < size % period && i + (size / period) * period != pos)
            dh.insert(residual.get(i + (size / period) * period));

        rtn = dh.getMedian() + seasonal.get(pos) + trend.get(pos);
        dh.clear();
        return rtn;
    }

    private double sub(double a, double b) {
        return a > b ? a - b : b - a;
    }

    public ArrayList<Double> getTd_repair() {
        return td_repair;
    }

    public static void main(String[] args) {
//        double a = 144.0, b = 23.0;
//        System.out.println(a > b ? a - b : b - a);
    }
}
