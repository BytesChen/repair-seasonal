package Algorithm;

import java.util.ArrayList;

import Algorithm.util.Decomposition;

public class ClassicalSeasonal {
    private final ArrayList<Long> td_time;
    private final ArrayList<Double> td_dirty;
    private final ArrayList<Double> td_repair = new ArrayList<>();
    private final int period;
    private final double k;  // k*std
    private final int max_iter;

    private double mean, std;
    private ArrayList<Double> seasonal, trend, residual;
    private final int size;

    public ClassicalSeasonal(ArrayList<Long> td_time, ArrayList<Double> td_dirty, int period, double k, int max_iter) throws Exception {
        this.td_time = td_time;
        this.td_dirty = td_dirty;
        this.period = period;
        this.k = k;
        this.max_iter = max_iter;

        this.size = td_dirty.size();

        long startTime = System.currentTimeMillis();
        this.repair();
        long endTime = System.currentTimeMillis();
        System.out.println("ClassicalSeasonal time cost:" + (endTime - startTime) + "ms");
    }

    private void repair() throws Exception {
        td_repair.addAll(td_dirty);

        for (int h = 0; h < max_iter; ++h) {
            Decomposition de = new Decomposition(td_time, td_repair, period, "classical");
            seasonal = de.getSeasonal();
            trend = de.getTrend();
            residual = de.getResidual();

            estimate();

            boolean flag = true;
            for (int i = 0; i < size; ++i) {
                if (sub(residual.get(i), mean) > k * std) {
                    flag = false;
                    td_repair.set(i, generate(i));
                }
            }
            if (flag) break;
        }
    }

    private void estimate() throws Exception {
        mean = 0.0;
        double cnt = 0.0;
        // mean
        for (double d : residual) {
            if (!Double.isNaN(d)) {
                cnt += 1;
                mean += d;
            }
        }
        mean /= cnt;

        std = 0.0;
        // std
        for (double d : residual) {
            if (!Double.isNaN(d)) {
                std += (d - mean) * (d - mean);
            }
        }
        std /= cnt;
        std = Math.sqrt(std);
    }

    private double generate(int pos) throws Exception {
        // in each cycle
        int i = pos % period;
        double sum = 0.0, cnt = 0.0, rtn;
        for (int j = 0; j < size / period; ++j)
            if (j * period + i != pos && !Double.isNaN(residual.get(j * period + i))) {  // remove anomaly
                sum += residual.get(j * period + i);
                cnt += 1.0;
            }
        if (i < size % period && i + (size / period) * period != pos && !Double.isNaN(residual.get(i + (size / period) * period))) {
            sum += residual.get(i + (size / period) * period);
            cnt += 1.0;
        }

        rtn = sum / cnt + seasonal.get(pos) + trend.get(pos);
        return rtn;
    }

    private double sub(double a, double b) {
        return a > b ? a - b : b - a;
    }

    public ArrayList<Double> getTd_repair() {
        return td_repair;
    }
}
