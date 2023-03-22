package Algorithm.util;

import java.util.Objects;

public class Decomposition {
    private final long[] td_time;
    private final double[] td;
    private final int period;

    private final double[] seasonal;
    private final double[] trend;
    private final double[] residual;

    public Decomposition(long[] td_time, double[] td, int period, String method) throws Exception {
        this.td = td;
        this.td_time = td_time;
        this.period = period;

        this.seasonal = new double[td.length];
        this.trend = new double[td.length];
        this.residual = new double[td.length];

        if (Objects.equals(method, "classical"))
            this.classical_decompose();
        else if (Objects.equals(method, "robust"))
            this.robust_decompose();
        else {
            throw new Exception("Error: Method should be \"classical\" or \"robust\".");
        }
    }

    private void classical_decompose() throws Exception {
        if (period > td.length)
            throw new Exception("Error: Period exceed the size of time series!");

        // constant
        int interval = period / 2;
        int size = td.length;
        // structure
        double[] de_trend = new double[size];

        // step 1: trend
        for (int i = 0; i < interval; ++i) trend[i] = 0.0;  // head null

        double ma = 0.0;
        if (period % 2 == 1) {
            // initial
            for (int i = 0; i < period; ++i) ma += td[i];
            trend[interval] = ma / period;
            // moving median
            for (int i = period; i < size; ++i) {
                ma += td[i];
                ma -= td[i - period];
                trend[i - interval] = ma / period;
            }
        } else {
            // initial
            double temp = (td[0] + td[period]) / 2.0;
            ma += temp;
            for (int i = 1; i < period; ++i) ma += td[i];
            trend[interval] = ma / period;
            ma -= temp;
            // moving median
            for (int i = period; i < size - 1; ++i) {
                ma += td[i];
                ma -= td[i - period + 1];
                temp = (td[i - period + 1] + td[i + 1]) / 2.0;
                ma += temp;
                trend[i - interval + 1] = ma / period;
                ma -= temp;
            }
        }
        ma = 0.0;

        for (int i = td.length - 1; i > td.length - 1 - interval; --i) trend[i] = 0.0;  // tail null

        // step 2: de-trend
        for (int i = 0; i < size; ++i)
            de_trend[i] = td[i] - trend[i];

        // step 3: seasonal
        double cycle_cnt = 0.0;
        for (int i = 0; i < period; ++i) {
            // in each cycle
            for (int j = 0; j < size / period; ++j)
                if (!Double.isNaN(de_trend[j * period + i])) {
                    cycle_cnt += 1.0;
                    ma += de_trend[j * period + i];
                }
            if (i < size % period && !Double.isNaN(de_trend[i + (size / period) * period])) {
                cycle_cnt += 1.0;
                ma += de_trend[i + (size / period) * period];
            }
            seasonal[i] = ma / cycle_cnt;
            cycle_cnt = 0.0;
            ma = 0.0;
        }

        // de-mean
        for (int i = 0; i < period; ++i)
            ma += seasonal[i];
        double mean_s = ma / period;
        for (int i = 0; i < period; ++i)
            seasonal[i] = seasonal[i] - mean_s;

        // extend
        for (int i = period; i < size; ++i)
            seasonal[i] = seasonal[i % period];

        // step 3: residual
        for (int i = 0; i < size; ++i)
            residual[i] = de_trend[i] - seasonal[i];
    }

    private void robust_decompose() throws Exception {
        if (period > td.length)
            throw new Exception("Error: Period exceed the size of time series!");

        // constant
        int interval = period / 2;
        int size = td.length;
        // structure
        double[] de_trend = new double[size];
        DualHeap dh = new DualHeap();

        // step 1: trend
        for (int i = 0; i < interval; ++i) trend[i] = 0.0;  // head null

        if (period % 2 == 1) {
            // initial
            for (int i = 0; i < period; ++i) dh.insert(td[i]);
            trend[interval] = dh.getMedian();
            // moving median
            for (int i = period; i < size; ++i) {
                dh.insert(td[i]);
                dh.erase(td[i - period]);
                trend[i - interval] = dh.getMedian();
            }
        } else {
            // initial
            double temp = (td[0] + td[period]) / 2.0;
            dh.insert(temp);
            for (int i = 1; i < period; ++i) dh.insert(td[i]);
            trend[interval] = dh.getMedian();
            dh.erase(temp);
            // moving median
            for (int i = period; i < size - 1; ++i) {
                dh.insert(td[i]);
                dh.erase(td[i - period + 1]);
                temp = (td[i - period + 1] + td[i + 1]) / 2.0;
                dh.insert(temp);
                trend[i - interval + 1] = dh.getMedian();
                dh.erase(temp);
            }
        }
        dh.clear();

        for (int i = td.length - 1; i > td.length - 1 - interval; --i) trend[i] = 0.0;  // tail null

        // trend extension
//        constant_ext();
        ar_ext();

        // step 2: de-trend
        for (int i = 0; i < size; ++i)
            de_trend[i] = td[i] - trend[i];

        // step 3: seasonal
        for (int i = 0; i < period; ++i) {
            // in each cycle
            for (int j = 0; j < size / period; ++j)
                dh.insert(de_trend[j * period + i]);
            if (i < size % period)
                dh.insert(de_trend[i + (size / period) * period]);

            seasonal[i] = dh.getMedian();
            dh.clear();
        }

        // de-median
        for (int i = 0; i < period; ++i)
            dh.insert(seasonal[i]);
        double median_s = dh.getMedian();
        for (int i = 0; i < period; ++i)
            seasonal[i] = seasonal[i] - median_s;
        dh.clear();

        // extend
        for (int i = period; i < size; ++i)
            seasonal[i] = seasonal[i % period];

        // step 3: residual
        for (int i = 0; i < size; ++i)
            residual[i] = de_trend[i] - seasonal[i];
    }

//    private void constant_ext() {
//        int interval = period / 2;
//        for (int i = interval; i > 0; --i)
//            trend[i - 1] = trend[i];
//        for (int i = trend.length - interval - 1; i < trend.length - 1; ++i)
//            trend[i + 1] = trend[i];
//    }

    private void ar_ext() {
        int interval = period / 2;
        int end = trend.length - interval - 1;

        double a = 0.0, b = 0.0, d = trend.length - 2 * interval - 1, tmp;
        for (int i = interval; i < end; ++i) {
            b -= trend[i];
            a += trend[i] * trend[i];
        }
        tmp = a * d - b * b;
        a /= tmp;
        b /= tmp;
        d /= tmp;

        double sigma = 0.0, a1 = 0.0;
        for (int i = interval; i < end; ++i) {
            sigma += (a + b * trend[i]) * trend[i + 1];
            a1 += (b + d * trend[i]) * trend[i + 1];
        }

        // extend
        for (int i = interval; i > 0; --i)
            trend[i - 1] = (trend[i] - sigma) / a1;
        for (int i = trend.length - interval - 1; i < trend.length - 1; ++i)
            trend[i + 1] = a1 * trend[i] + sigma;
    }

    public double[] getSeasonal() {
        return seasonal;
    }

    public double[] getTrend() {
        return trend;
    }

    public double[] getResidual() {
        return residual;
    }
}
