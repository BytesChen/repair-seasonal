package Algorithm;

import Algorithm.util.ScreenUtil;

import java.util.ArrayList;

public class EWMA {
    private final ArrayList<Long> td_time;
    private final ArrayList<Double> td_dirty;
    private final ArrayList<Double> td_repair = new ArrayList<>();
    private double beta = 0.2;

    public EWMA(ArrayList<Long> td_time, ArrayList<Double> td_dirty) throws Exception {
        this.td_time = td_time;
        this.td_dirty = td_dirty;
        long startTime = System.currentTimeMillis();    //获取开始时间
        this.repair();
        long endTime = System.currentTimeMillis();    //获取结束时间
        System.out.println("EWMA time cost:" + (endTime - startTime) + "ms");
    }

    public ArrayList<Double> getTd_repair() {
        return td_repair;
    }

    private void repair() throws Exception {
        double last_ewma = td_dirty.get(0);
        td_repair.add(last_ewma);

        for (int i = 1; i < td_dirty.size(); ++i) {  // repair
            double new_value = this.beta * last_ewma + (1 - beta) * td_dirty.get(i);
            td_repair.add(new_value);
            last_ewma = new_value;
        }
    }
}
