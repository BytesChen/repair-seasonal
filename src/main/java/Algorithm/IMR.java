package Algorithm;

import Algorithm.util.IMRUtil;

import java.util.ArrayList;

public class IMR {
    private final ArrayList<Long> td_time;
    private final ArrayList<Double> td_dirty;
    private final ArrayList<Double> td_repair = new ArrayList<>();
    private final double[] td_label;
    private final boolean[] td_bool;

    public IMR(ArrayList<Long> td_time, ArrayList<Double> td_dirty, double[] td_label, boolean[] td_bool) {
        this.td_time = td_time;
        this.td_dirty = td_dirty;
        this.td_label = td_label;
        this.td_bool = td_bool;

        long startTime = System.currentTimeMillis();
        this.repair();
        long endTime = System.currentTimeMillis();
        System.out.println("IMR time cost:" + (endTime - startTime) + "ms");
    }

    public ArrayList<Double> getTd_repair() {
        return td_repair;
    }

    private void repair() {
        long[] time = this.arrayListToListLong(this.td_time);
        double[] temp_dirty = this.arrayListToListDouble(this.td_dirty);
        double[] temp_repair;

        IMRUtil imrUtil = new IMRUtil(time, temp_dirty, td_label, td_bool, 3, 1.5, 10000);
        imrUtil.repair();
        temp_repair = imrUtil.getRepaired();

        // listToArrayList
        for (int i = 0; i < this.td_dirty.size(); i++) {
            this.td_repair.add(temp_repair[i]);
        }
    }

    private double[] arrayListToListDouble(ArrayList<Double> arrayList) {
        double[] doubles = new double[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            doubles[i] = arrayList.get(i);
        }
        return doubles;
    }

    private long[] arrayListToListLong(ArrayList<Long> arrayList) {
        long[] longs = new long[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            longs[i] = arrayList.get(i);
        }
        return longs;
    }
}
