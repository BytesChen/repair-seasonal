package Algorithm;

import java.util.ArrayList;

public class SeasonalRepair {

    private final ArrayList<Long> td_time;
    private final ArrayList<Double> td_dirty;
    private final ArrayList<Double> td_repair = new ArrayList<>();

    public SeasonalRepair(ArrayList<Long> td_time, ArrayList<Double> td_dirty) throws Exception {
        this.td_time = td_time;
        this.td_dirty = td_dirty;

        long startTime = System.currentTimeMillis();
        this.repair();
        long endTime = System.currentTimeMillis();
        System.out.println("Time cost:" + (endTime - startTime) + "ms");
    }

    public void repair() throws Exception {
        int dataLen = td_time.size();

        for (int i = 0; i < dataLen; ++i) {
            td_repair.add(td_dirty.get(i));
        }
    }

    public ArrayList<Double> getTd_repair() {
        return td_repair;
    }
}
