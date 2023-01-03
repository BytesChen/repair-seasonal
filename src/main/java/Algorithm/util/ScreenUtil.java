package Algorithm.util;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;

public class ScreenUtil {
    private double smin, smax;
    private double w;
    private int n;
    private final long[] time;
    private final double[] original;
    private final double[] repaired;

    public ScreenUtil (long[] time, double[] original) throws Exception {
        this.time = time;
        this.original = original;
        this.n = time.length;
        this.repaired = new double[n];
        setParameters();
    }

    public void setSmin(double smin) {
        this.smin = smin;
    }

    public void setSmax(double smax) {
        this.smax = smax;
    }


    public double[] getRepaired() {
        return repaired;
    }

    public static double median(double[] list) {
        Arrays.sort(list);
        int size = list.length;
        if(size % 2 != 1){
            return (list[size / 2 - 1] + list[size / 2]) / 2;
        }else {
            return list[(size - 1) / 2];
        }
    }

    public static double[] speed(double[] origin, long[] time) {
        int n = origin.length;
        double[] speed = new double[n - 1];
        for (int i = 0; i < n - 1; i++) {
            speed[i] = (origin[i + 1] - origin[i]) / (time[i + 1] - time[i]);
        }
        return speed;
    }

    public static double mad(double[] value) {
        double mid = median(value);
        double[] d = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            d[i] = Math.abs(value[i] - mid);
        }
        return 1.4826 * median(d);
    }

    public static double[] variation(double[] origin) {
        int n = origin.length;
        double[] var = new double[n - 1];
        for (int i = 0; i < n - 1; i++) {
            var[i] = origin[i + 1] - origin[i];
        }
        return var;
    }

    public static double[] variation(long[] origin) {
        int n = origin.length;
        double[] var = new double[n - 1];
        for (int i = 0; i < n - 1; i++) {
            var[i] = origin[i + 1] - origin[i];
        }
        return var;
    }

    private void setParameters() {
        // set the default speed threshold
        double[] speed = speed(original, time);
        double mid = median(speed);
        double sigma = mad(speed);
        smax = mid + 3 * sigma;
        smin = mid - 3 * sigma;
//        System.out.println(smax + ", " + smin);
        // set the default window size
        double[] interval = variation(time);
        w = 5 * median(interval);
    }

    public void repair() {
        // fixed window

        ArrayList<Pair<Long, Double>> ans = new ArrayList<>();
        ans.add(new Pair<>(time[0], original[0]));
        int startIndex = 0;
        for (int i = 1; i < n; i++) {
            ans.add(new Pair<>(time[i], original[i]));
            while (ans.get(startIndex).getKey() + w < ans.get(i).getKey()) {
                // sliding window
                local(ans, startIndex);
                startIndex++;
            }
        }
        while (startIndex < n) {
            local(ans, startIndex);
            startIndex++;
        }
        int k = 0;
        for (Pair<Long, Double> p : ans) {
            this.repaired[k] = p.getValue();
            k++;
        }
    }

    private double getMedian(ArrayList<Pair<Long, Double>> list, int index) {
        int m = 0;
        while (index + m + 1 < list.size()
                && list.get(index + m + 1).getKey() <= list.get(index).getKey() + w) {
            m++;
        }
        double[] x = new double[2 * m + 1];
        x[0] = list.get(index).getValue();
        for (int i = 1; i <= m; i++) {
            x[i] =
                    list.get(index + i).getValue()
                            + smin * (list.get(index).getKey() - list.get(index + i).getKey());
            x[i + m] =
                    list.get(index + i).getValue()
                            + smax * (list.get(index).getKey() - list.get(index + i).getKey());
        }
        Arrays.sort(x);
        return x[m];
    }

    private double getRepairedValue(ArrayList<Pair<Long, Double>> list, int index, double mid) {
        double xmin =
                list.get(index - 1).getValue()
                        + smin * (list.get(index).getKey() - list.get(index - 1).getKey());
        double xmax =
                list.get(index - 1).getValue()
                        + smax * (list.get(index).getKey() - list.get(index - 1).getKey());
        double temp = mid;
        temp = Math.min(xmax, temp);
        temp = Math.max(xmin, temp);
        return temp;
    }

    private void local(ArrayList<Pair<Long, Double>> list, int index) {
        double mid = getMedian(list, index);
        // 计算x_k'
        if (index == 0) {
            list.set(index, new Pair<>(list.get(index).getKey(), mid));
        } else {
            double temp = getRepairedValue(list, index, mid);
            list.set(index, new Pair<>(list.get(index).getKey(), temp));
        }
    }

    private void processNaN() throws Exception {
        int index1 = 0, index2;
        while (index1 < n && Double.isNaN(original[index1])) {
            index1++;
        }
        index2 = index1 + 1;
        while (index2 < n && Double.isNaN(original[index2])) {
            index2++;
        }
        if (index2 >= n) {
            throw new Exception("At least two non-NaN values are needed");
        }
        for (int i = 0; i < index2; i++) {
            original[i] =
                    original[index1]
                            + (original[index2] - original[index1])
                            * (time[i] - time[index1])
                            / (time[index2] - time[index1]);
        }
        for (int i = index2 + 1; i < n; i++) {
            if (!Double.isNaN(original[i])) {
                index1 = index2;
                index2 = i;
                for (int j = index1 + 1; j < index2; j++) {
                    original[j] =
                            original[index1]
                                    + (original[index2] - original[index1])
                                    * (time[j] - time[index1])
                                    / (time[index2] - time[index1]);
                }
            }
        }
        for (int i = index2 + 1; i < n; i++) {
            original[i] =
                    original[index1]
                            + (original[index2] - original[index1])
                            * (time[i] - time[index1])
                            / (time[index2] - time[index1]);
        }
    }
}
