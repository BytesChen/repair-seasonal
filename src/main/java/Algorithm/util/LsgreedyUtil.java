package Algorithm.util;

import java.util.Arrays;
import java.util.PriorityQueue;

public class LsgreedyUtil {
    private double center = 0, sigma;
    private final double eps = 1e-12;
    private int n;
    private final long[] time;
    private final double[] original;
    private double[] repaired;

    public LsgreedyUtil(long[] time, double[] original) throws Exception {
        this.time = time;
        this.original = original;
        this.n = time.length;
        this.repaired = new double[n];
        setParameters();
    }

    public double[] getRepaired() {
        return repaired;
    }

    public static double[] speed(double[] origin, long[] time) {
        int n = origin.length;
        double[] speed = new double[n - 1];
        for (int i = 0; i < n - 1; i++) {
            speed[i] = (origin[i + 1] - origin[i]) / (time[i + 1] - time[i]);
        }
        return speed;
    }

    public static double[] variation(double[] origin) {
        int n = origin.length;
        double[] var = new double[n - 1];
        for (int i = 0; i < n - 1; i++) {
            var[i] = origin[i + 1] - origin[i];
        }
        return var;
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

    public static double mad(double[] value) {
        double mid = median(value);
        double[] d = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            d[i] = Math.abs(value[i] - mid);
        }
        return 1.4826 * median(d);
    }

    private void setParameters() {
        double[] speed = speed(original, time);
        double[] speedchange = variation(speed);
        sigma = mad(speedchange);
    }

    public void repair() {
        repaired = original.clone();
        RepairNode[] table = new RepairNode[n];
        PriorityQueue<RepairNode> heap = new PriorityQueue<>();
        for (int i = 1; i < n - 1; i++) {
            RepairNode node = new RepairNode(i);
            table[i] = node;
            if (Math.abs(node.getU() - center) > 3 * sigma) {
                heap.add(node);
            }
        }
        while (true) {
            RepairNode top = heap.peek();
            if (top == null || Math.abs(top.getU() - center) < Math.max(eps, 3 * sigma)) {
                break;
            } // stop greedy algorithm when the heap is empty or all speed changes locate in center±3sigma
            top.modify();
            for (int i = Math.max(1, top.getIndex() - 1); i <= Math.min(n - 2, top.getIndex() + 1); i++) {
                heap.remove(table[i]);
                RepairNode temp = new RepairNode(i);
                table[i] = temp;
                if (Math.abs(temp.getU() - center) > 3 * sigma) {
                    heap.add(temp);
                }
            }
        }
    }

    class RepairNode implements Comparable<RepairNode> {

        private final int index;
        private final double u; // speed variation

        public RepairNode(int index) {
            this.index = index;
            double v1 = repaired[index + 1] - repaired[index];
            v1 = v1 / (time[index + 1] - time[index]);
            double v2 = repaired[index] - repaired[index - 1];
            v2 = v2 / (time[index] - time[index - 1]);
            this.u = v1 - v2;
        }

        /**
         * modify values of repaired points, to make the difference of its speed variation and center is
         * 1 sigma
         */
        public void modify() {
            double temp;
            if (sigma < eps) {
                temp = Math.abs(u - center);
            } else {
                temp = Math.max(sigma, Math.abs(u - center) / 3);
            }
            temp *=
                    (double) (time[index + 1] - time[index])
                            * (time[index] - time[index - 1])
                            / (time[index + 1] - time[index - 1]);
            if (this.u > center) {
                repaired[index] += temp;
            } else {
                repaired[index] -= temp;
            }
        }

        @Override
        public int compareTo(RepairNode o) {
            double u1 = Math.abs(this.u - center);
            double u2 = Math.abs(o.u - center);
            if (u1 > u2) {
                return -1;
            } else if (u1 == u2) {
                return 0;
            } else {
                return 1;
            }
        }

        public int getIndex() {
            return index;
        }

        public double getU() {
            return u;
        }
    }

    public void setCenter(double center) {
        this.center = center;
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }
}
