package Algorithm;

import Algorithm.util.IMRUtil;

public class IMR {
    private final long[] td_time;
    private final double[] td_dirty;
    private double[] td_repair;
    private final double[] td_label;
    private final boolean[] td_bool;
    private final long cost_time;

    public IMR(long[] td_time, double[] td_dirty, double[] td_label, boolean[] td_bool) {
        this.td_time = td_time;
        this.td_dirty = td_dirty;
        this.td_label = td_label;
        this.td_bool = td_bool;

        long startTime = System.currentTimeMillis();
        this.repair();
        long endTime = System.currentTimeMillis();
        this.cost_time = endTime - startTime;
//        System.out.println("IMR time cost:" + cost_time + "ms");
    }

    public double[] getTd_repair() {
        return td_repair;
    }

    public long getCost_time() {
        return cost_time;
    }

    private void repair() {
        IMRUtil imrUtil = new IMRUtil(td_time, td_dirty, td_label, td_bool, 1, 0.3, 1000);
        imrUtil.repair();
        td_repair = imrUtil.getRepaired();
    }
}
