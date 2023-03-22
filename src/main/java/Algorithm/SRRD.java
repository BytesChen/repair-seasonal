package Algorithm;

import java.util.ArrayList;

import Algorithm.util.DualHeap;
import Algorithm.util.Decomposition;

public class SRRD {
    private final long[] td_time;
    private final double[] td_dirty;
    private final double[] td_repair;
    private final int period;
    private final double k;  // k*mad
    private final int max_iter;

    private double mid, mad;
    private double[] seasonal, trend, residual;
    private final DualHeap dh = new DualHeap();
    private final int size;

    public SRRD(long[] td_time, double[] td_dirty, int period, double k, int max_iter) throws Exception {
        this.td_time = td_time;
        this.td_dirty = td_dirty;
        this.td_repair = new double[td_dirty.length];
        this.period = period;
        this.k = k;
        this.max_iter = max_iter;

        this.size = td_dirty.length;

        long startTime = System.currentTimeMillis();
        this.repair();
        long endTime = System.currentTimeMillis();
        System.out.println("SRRD time cost:" + (endTime - startTime) + "ms");
    }

    private void repair() throws Exception {
        System.arraycopy(td_dirty, 0, td_repair, 0, td_dirty.length);

        int h = 0;
        for (; h < max_iter; ++h) {
            Decomposition de = new Decomposition(td_time, td_repair, period, "robust");
            seasonal = de.getSeasonal();
            trend = de.getTrend();
            residual = de.getResidual();

            estimate();

            boolean flag = true;
            for (int i = 0; i < size; ++i) {
                if (sub(residual[i], mid) > k * mad) {
                    flag = false;
                    td_repair[i] = generate(i);
                }
            }
            if (flag) break;
        }
        System.out.println("Stop after " + (h + 1) + " iterations");
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
                dh.insert(residual[j * period + i]);
        if (i < size % period && i + (size / period) * period != pos)
            dh.insert(residual[i + (size / period) * period]);

        rtn = dh.getMedian() + seasonal[pos] + trend[pos];
        dh.clear();
        return rtn;
    }

    private double sub(double a, double b) {
        return a > b ? a - b : b - a;
    }

    public double[] getTd_repair() {
        return td_repair;
    }
}
