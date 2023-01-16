package Algorithm;

import Algorithm.util.ScreenUtil;

import java.util.ArrayList;

public class SCREEN {
    private final ArrayList<Long> td_time;
    private final ArrayList<Double> td_dirty;
    private final ArrayList<Double> td_repair = new ArrayList<>();
//    private double minSpeeds;
//    private double maxSpeeds;

    public SCREEN(ArrayList<Long> td_time, ArrayList<Double> td_dirty) throws Exception {
        this.td_time = td_time;
        this.td_dirty = td_dirty;
        long startTime = System.currentTimeMillis();
        this.repair();
        long endTime = System.currentTimeMillis();
        System.out.println("Screen time cost:" + (endTime - startTime) + "ms");
    }

//    public SCREEN(ArrayList<Long> td_time, ArrayList<Double> td, double minSpeed, double maxSpeed) {
//        this.td_time = td_time;
//        this.td = td;
//        this.minSpeeds = minSpeed;
//        this.maxSpeeds = maxSpeed;
//    }

    public ArrayList<Double> getTd_repair() {
        return td_repair;
    }

    private void repair() throws Exception {
        long[] times = this.arrayListToListLong(this.td_time);
        double[] temp_dirty = this.arrayListToListDouble(this.td_dirty);
        double[] temp_repair;

        ScreenUtil screenUtil = new ScreenUtil(times, temp_dirty);
        screenUtil.repair();
        temp_repair = screenUtil.getRepaired();

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
