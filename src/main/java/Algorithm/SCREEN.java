package Algorithm;

import Algorithm.util.ScreenUtil;

import java.util.ArrayList;

public class SCREEN {
    private final ArrayList<Long> td_time;
    private final ArrayList<Double> td;
    private final ArrayList<Double> td_repair = new ArrayList<>();
//    private double minSpeeds;
//    private double maxSpeeds;

    public SCREEN(ArrayList<Long> td_time, ArrayList<Double> td) throws Exception {
        this.td_time = td_time;
        this.td = td;
        this.fillNullValue();
        long startTime = System.currentTimeMillis();    //获取开始时间
        this.repair();
        long endTime = System.currentTimeMillis();    //获取结束时间
        System.out.println("Time cost:" + (endTime - startTime) + "ms");    //输出程序运行时间
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

    public void fillNullValue() {
        double temp = this.td.get(0);
        for (int i = 0; i < this.td.size(); i++) {
            if (Double.isNaN(td.get(i))) {
                td.set(i, temp);
            } else {
                temp = td.get(i);
            }
        }
    }

    public void repair() throws Exception {
        long[] times = this.arrayListToListLong(this.td_time);
        double[] temp_dirty = this.arrayListToListDouble(this.td);
        double[] temp_repair;

        ScreenUtil screenUtil = new ScreenUtil(times, temp_dirty);
        screenUtil.repair();
        temp_repair = screenUtil.getRepaired();

        // listToArrayList
        for (int i = 0; i < this.td.size(); i++) {
            this.td_repair.add(temp_repair[i]);
        }
    }

    public double[] arrayListToListDouble(ArrayList<Double> arrayList) {
        double[] doubles = new double[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            doubles[i] = arrayList.get(i);
        }
        return doubles;
    }

    public long[] arrayListToListLong(ArrayList<Long> arrayList) {
        long[] longs = new long[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            longs[i] = arrayList.get(i);
        }
        return longs;
    }
}
