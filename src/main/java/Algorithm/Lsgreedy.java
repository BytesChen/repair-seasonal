package Algorithm;

import Algorithm.util.LsgreedyUtil;
import Algorithm.util.ScreenUtil;

import java.util.ArrayList;

public class Lsgreedy {
    private final ArrayList<Long> td_time;
    private final ArrayList<Double> td_dirty;
    private final ArrayList<Double> td_repair = new ArrayList<>();

    public Lsgreedy(ArrayList<Long> td_time, ArrayList<Double> td_dirty) throws Exception {
        this.td_time = td_time;
        this.td_dirty = td_dirty;
        long startTime = System.currentTimeMillis();    //获取开始时间
        this.repair();
        long endTime = System.currentTimeMillis();    //获取结束时间
        System.out.println("Lsgreedy time cost:" + (endTime - startTime) + "ms");
    }

    public ArrayList<Double> getTd_repair() {
        return td_repair;
    }

    private void repair() throws Exception {
        long[] times = this.arrayListToListLong(this.td_time);
        double[] temp_dirty = this.arrayListToListDouble(this.td_dirty);
        double[] temp_repair;

        LsgreedyUtil lsgreedyUtil = new LsgreedyUtil(times, temp_dirty);
        lsgreedyUtil.repair();
        temp_repair = lsgreedyUtil.getRepaired();

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
