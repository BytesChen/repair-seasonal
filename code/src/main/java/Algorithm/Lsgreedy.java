package Algorithm;

import Algorithm.util.LsgreedyUtil;

public class Lsgreedy {
    private final long[] td_time;
    private final double[] td_dirty;
    private double[] td_repair;
    private final long cost_time;

    public Lsgreedy(long[] td_time, double[] td_dirty) throws Exception {
        this.td_time = td_time;
        this.td_dirty = td_dirty;
        long startTime = System.currentTimeMillis();
        this.repair();
        long endTime = System.currentTimeMillis();
        this.cost_time = endTime - startTime;
//        System.out.println("Lsgreedy time cost:" + cost_time + "ms");
    }

    public double[] getTd_repair() {
        return td_repair;
    }
    public long getCost_time() {return cost_time;}

    private void repair() throws Exception {
        LsgreedyUtil lsgreedyUtil = new LsgreedyUtil(td_time, td_dirty);
        lsgreedyUtil.repair();
        td_repair = lsgreedyUtil.getRepaired();
    }
}
