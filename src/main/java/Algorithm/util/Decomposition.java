package Algorithm.util;

import java.util.ArrayList;
import java.util.Objects;

public class Decomposition {
    private final ArrayList<Long> td_time;
    private final ArrayList<Double> td;
    private final int period;

    private final ArrayList<Double> seasonal = new ArrayList<>();
    private final ArrayList<Double> trend = new ArrayList<>();
    private final ArrayList<Double> residual = new ArrayList<>();

    public Decomposition(ArrayList<Long> td_time, ArrayList<Double> td, int period, String method) throws Exception {
        this.td = td;
        this.td_time = td_time;
        this.period = period;

        if (Objects.equals(method, "classical"))
            this.classical_decompose();
        else if (Objects.equals(method, "robust"))
            this.robust_decompose();
        else {
            throw new Exception("Error: Method should be \"classical\" or \"robust\".");
        }
    }

    private void classical_decompose() throws Exception {
        if (period > td.size())
            throw new Exception("Error: Period exceed the size of time series!");

        // structure
        ArrayList<Double> de_trend = new ArrayList<>();
        // constant
        int interval = period / 2;
        int size = td.size();

        // step 1: trend
        for (int i = 0; i < interval; ++i) trend.add(Double.NaN);  // head null

        double ma = 0.0;
        if (period % 2 == 1) {
            // initial
            for (int i = 0; i < period; ++i) ma += td.get(i);
            trend.add(ma / period);
            // moving median
            for (int i = period; i < size; ++i) {
                ma += td.get(i);
                ma -= td.get(i - period);
                trend.add(ma / period);
            }
        } else {
            // initial
            double temp = (td.get(0) + td.get(period)) / 2.0;
            ma += temp;
            for (int i = 1; i < period; ++i) ma += td.get(i);
            trend.add(ma / period);
            ma -= temp;
            // moving median
            for (int i = period; i < size - 1; ++i) {
                ma += td.get(i);
                ma -= td.get(i - period + 1);
                temp = (td.get(i - period + 1) + td.get(i + 1)) / 2.0;
                ma += temp;
                trend.add(ma / period);
                ma -= temp;
            }
        }
        ma = 0.0;

        for (int i = 0; i < interval; ++i) trend.add(Double.NaN);  // tail null

        // step 2: de-trend
        for (int i = 0; i < size; ++i)
            de_trend.add(td.get(i) - trend.get(i));

        // step 3: seasonal
        double cycle_cnt = 0.0;
        for (int i = 0; i < period; ++i) {
            // in each cycle
            for (int j = 0; j < size / period; ++j)
                if (!Double.isNaN(de_trend.get(j * period + i))) {
                    cycle_cnt += 1.0;
                    ma += de_trend.get(j * period + i);
                }
            if (i < size % period && !Double.isNaN(de_trend.get(i + (size / period) * period))) {
                cycle_cnt += 1.0;
                ma += de_trend.get(i + (size / period) * period);
            }
            seasonal.add(ma / cycle_cnt);
            cycle_cnt = 0.0;
            ma = 0.0;
        }

        // de-mean
        for (int i = 0; i < period; ++i)
            ma += seasonal.get(i);
        double mean_s = ma / period;
        for (int i = 0; i < period; ++i)
            seasonal.set(i, seasonal.get(i) - mean_s);

        // extend
        for (int i = period; i < size; ++i)
            seasonal.add(seasonal.get(i % period));

        // step 3: residual
        for (int i = 0; i < size; ++i)
            residual.add(de_trend.get(i) - seasonal.get(i));
    }

    private void robust_decompose() throws Exception {
        if (period > td.size())
            throw new Exception("Error: Period exceed the size of time series!");

        // structure
        ArrayList<Double> de_trend = new ArrayList<>();
        DualHeap dh = new DualHeap();
        // constant
        int interval = period / 2;
        int size = td.size();

        // step 1: trend
        for (int i = 0; i < interval; ++i) trend.add(0.0);  // head null

        if (period % 2 == 1) {
            // initial
            for (int i = 0; i < period; ++i) dh.insert(td.get(i));
            trend.add(dh.getMedian());
            // moving median
            for (int i = period; i < size; ++i) {
                dh.insert(td.get(i));
                dh.erase(td.get(i - period));
                trend.add(dh.getMedian());
            }
        } else {
            // initial
            double temp = (td.get(0) + td.get(period)) / 2.0;
            dh.insert(temp);
            for (int i = 1; i < period; ++i) dh.insert(td.get(i));
            trend.add(dh.getMedian());
            dh.erase(temp);
            // moving median
            for (int i = period; i < size - 1; ++i) {
                dh.insert(td.get(i));
                dh.erase(td.get(i - period + 1));
                temp = (td.get(i - period + 1) + td.get(i + 1)) / 2.0;
                dh.insert(temp);
                trend.add(dh.getMedian());
                dh.erase(temp);
            }
        }
        dh.clear();

        for (int i = 0; i < interval; ++i) trend.add(0.0);  // tail null

        // trend extension
//        constant_ext();
        ar_ext();

        // step 2: de-trend
        for (int i = 0; i < size; ++i)
            de_trend.add(td.get(i) - trend.get(i));

        // step 3: seasonal
        for (int i = 0; i < period; ++i) {
            // in each cycle
            for (int j = 0; j < size / period; ++j)
                dh.insert(de_trend.get(j * period + i));
            if (i < size % period)
                dh.insert(de_trend.get(i + (size / period) * period));

            seasonal.add(dh.getMedian());
            dh.clear();
        }

        // de-median
        for (int i = 0; i < period; ++i)
            dh.insert(seasonal.get(i));
        double median_s = dh.getMedian();
        for (int i = 0; i < period; ++i)
            seasonal.set(i, seasonal.get(i) - median_s);
        dh.clear();

        // extend
        for (int i = period; i < size; ++i)
            seasonal.add(seasonal.get(i % period));

        // step 3: residual
        for (int i = 0; i < size; ++i)
            residual.add(de_trend.get(i) - seasonal.get(i));
    }

//    private void constant_ext() {
//        int interval = period / 2;
//        for (int i = interval; i > 0; --i)
//            trend.set(i - 1, trend.get(i));
//        for (int i = trend.size() - interval - 1; i < trend.size() - 1; ++i)
//            trend.set(i + 1, trend.get(i));
//    }

    private void ar_ext() {
        int interval = period / 2;
        int end = trend.size() - interval - 1;

        double a = 0.0, b = 0.0, d = trend.size() - 2 * interval - 1, tmp;
        for (int i = interval; i < end; ++i) {
            b -= trend.get(i);
            a += trend.get(i) * trend.get(i);
        }
        tmp = a * d - b * b;
        a /= tmp;
        b /= tmp;
        d /= tmp;

        double sigma = 0.0, a1 = 0.0;
        for (int i = interval; i < end; ++i) {
            sigma += (a + b * trend.get(i)) * trend.get(i + 1);
            a1 += (b + d * trend.get(i)) * trend.get(i + 1);
        }

        // extend
        for (int i = interval; i > 0; --i)
            trend.set(i - 1, (trend.get(i) - sigma) / a1);
        for (int i = trend.size() - interval - 1; i < trend.size() - 1; ++i)
            trend.set(i + 1, a1 * trend.get(i) + sigma);
    }

    public ArrayList<Double> getSeasonal() {
        return seasonal;
    }

    public ArrayList<Double> getTrend() {
        return trend;
    }

    public ArrayList<Double> getResidual() {
        return residual;
    }
}
