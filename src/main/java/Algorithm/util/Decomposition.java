package Algorithm.util;

import java.util.ArrayList;

import Algorithm.util.DualHeap;

public class Decomposition {
    private final ArrayList<Long> td_time;
    private final ArrayList<Double> td;
    private final int period;
    private final String mode;

    private final ArrayList<Double> seasonal = new ArrayList<>();
    private final ArrayList<Double> trend = new ArrayList<>();
    private final ArrayList<Double> residual = new ArrayList<>();

    public Decomposition(ArrayList<Long> td_time, ArrayList<Double> td, int period, String mode) {
        this.td = td;
        this.td_time = td_time;
        this.period = period;
        this.mode = mode;

        this.decompose();
    }

    public Decomposition(ArrayList<Long> td_time, ArrayList<Double> td, int period) {
        this.td = td;
        this.td_time = td_time;
        this.period = period;
        this.mode = "constant";

        this.decompose();
    }

    private void decompose() {
        if (period > td.size()) {  // error
            System.out.println("Error: Period exceed the size of time series!");
            return;
        }
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
        if (mode.equals("constant")) {
            constant_ext();
        } else if (mode.equals("ar")) {
            ar_ext();
        }

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

    private void constant_ext() {
        int interval = period / 2;
        for (int i = interval; i > 0; --i)
            trend.set(i - 1, trend.get(i));
        for (int i = trend.size() - interval - 1; i < trend.size() - 1; ++i)
            trend.set(i + 1, trend.get(i));
    }

    private void ar_ext() {
        // cal \sum x_t * x_{t-1}
        double acf = 0;
        double factor = 0;
        double theta;
        int acf_cnt = 0;
        int interval = period / 2;
        for (int i = interval; i < trend.size() - interval - 1; ++i) {
            double left = trend.get(i), right = trend.get(i + 1);
            acf += left * right;
            factor += left * left;
            acf_cnt += 1;
        }
        acf /= acf_cnt;
        theta = acf / factor;
        assert theta < 1;

        double mean_epsilon = 0;
        double var_epsilon = 0;
        double cnt_epsilon = 0;
        double epsilon;
        for (int i = interval; i < trend.size() - interval - 1; ++i) {
            double left = trend.get(i), right = trend.get(i + 1);
            cnt_epsilon += 1;
            epsilon = right - left * theta;
            mean_epsilon += epsilon;
            var_epsilon += epsilon * epsilon;
        }
        mean_epsilon /= cnt_epsilon;
        var_epsilon /= cnt_epsilon;

        System.out.println(theta);
        System.out.println(mean_epsilon);

        for (int i = interval; i > 0; --i)
            trend.set(i - 1, (trend.get(i) - mean_epsilon) / theta);
        for (int i = trend.size() - interval - 1; i < trend.size() - 1; ++i)
            trend.set(i + 1, theta * trend.get(i) + mean_epsilon);
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

    public static void main(String[] args) {
        ArrayList<Long> td_t = new ArrayList<>();
        ArrayList<Double> td_s = new ArrayList<>();
        td_s.add(5.0);
        td_s.add(3.0);
        td_s.add(7.0);
        td_s.add(8.0);
        td_s.add(9.0);
        td_s.add(6.0);
        td_s.add(10.6);

        Decomposition de = new Decomposition(td_t, td_s, 3);
        for (double d : de.getTrend()) {
            System.out.print(d + " ");
        }
    }
}
