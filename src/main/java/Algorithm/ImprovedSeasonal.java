package Algorithm;

import java.util.ArrayList;

import Algorithm.util.DualHeap;
import Algorithm.util.Decomposition;

public class ImprovedSeasonal {
    private final ArrayList<Long> td_time;
    private final ArrayList<Double> td_dirty;
    private final ArrayList<Double> td_repair = new ArrayList<>();
    private final int period;
    private final double k;  // k*mad
    private final int max_iter;

    private double mid, mad;
    private ArrayList<Double> seasonal, trend, residual;
    private final DualHeap dh = new DualHeap();
    private final int size;

    public ImprovedSeasonal(ArrayList<Long> td_time, ArrayList<Double> td_dirty, int period, double k, int max_iter) throws Exception {
        this.td_time = td_time;
        this.td_dirty = td_dirty;
        this.period = period;
        this.k = k;
        this.max_iter = max_iter;

        this.size = td_dirty.size();

        long startTime = System.currentTimeMillis();
        this.repair();
        long endTime = System.currentTimeMillis();
        System.out.println("ImprovedSeasonal time cost:" + (endTime - startTime) + "ms");
    }

    private void repair() throws Exception {
        td_repair.addAll(td_dirty);

        for (int h = 0; h < max_iter; ++h) {
            Decomposition de = new Decomposition(td_time, td_repair, period, "improved", "constant");
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
}
