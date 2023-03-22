package Algorithm;

import Algorithm.util.Decomposition;

public class SRCD {
    private final long[] td_time;
    private final double[] td_dirty;
    private final double[] td_repair;
    private final int period;
    private final double k;  // k*std
    private final int max_iter;

    private double mean, std;
    private double[] seasonal, trend, residual;
    private final int size;

    public SRCD(long[] td_time, double[] td_dirty, int period, double k, int max_iter) throws Exception {
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
        System.out.println("SRCD time cost:" + (endTime - startTime) + "ms");
    }

    private void repair() throws Exception {
        System.arraycopy(td_dirty, 0, td_repair, 0, td_dirty.length);

        int h = 0;
        for (; h < max_iter; ++h) {
            Decomposition de = new Decomposition(td_time, td_repair, period, "classical");
            seasonal = de.getSeasonal();
            trend = de.getTrend();
            residual = de.getResidual();

            estimate();

            boolean flag = true;
            for (int i = 0; i < size; ++i) {
                if (sub(residual[i], mean) > k * std) {
                    flag = false;
                    td_repair[i] = generate(i);
                }
            }
            if (flag) break;
        }
        System.out.println("Stop after " + (h + 1) + " iterations");
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
            if (j * period + i != pos && !Double.isNaN(residual[j * period + i])) {  // remove anomaly
                sum += residual[j * period + i];
                cnt += 1.0;
            }
        if (i < size % period && i + (size / period) * period != pos && !Double.isNaN(residual[i + (size / period) * period])) {
            sum += residual[i + (size / period) * period];
            cnt += 1.0;
        }

        rtn = sum / cnt + seasonal[pos] + trend[pos];
        return rtn;
    }

    private double sub(double a, double b) {
        return a > b ? a - b : b - a;
    }

    public double[] getTd_repair() {
        return td_repair;
    }
}
