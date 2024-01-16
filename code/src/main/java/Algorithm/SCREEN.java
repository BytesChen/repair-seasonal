package Algorithm;

import Algorithm.util.ScreenUtil;

public class SCREEN {
    private final long[] td_time;
    private final double[] td_dirty;
    private double[] td_repair;
    //    private double minSpeeds;
//    private double maxSpeeds;
    private final long cost_time;

    public SCREEN(long[] td_time, double[] td_dirty) throws Exception {
        this.td_time = td_time;
        this.td_dirty = td_dirty;

        long startTime = System.currentTimeMillis();
        this.repair();
        long endTime = System.currentTimeMillis();
        this.cost_time = endTime - startTime;
//        System.out.println("Screen time cost:" + cost_time + "ms");
    }

    public double[] getTd_repair() {
        return td_repair;
    }
    public long getCost_time() {return cost_time;}

    private void repair() throws Exception {
        ScreenUtil screenUtil = new ScreenUtil(td_time, td_dirty);
        screenUtil.repair();
        td_repair = screenUtil.getRepaired();
    }
}
