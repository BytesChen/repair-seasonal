package Algorithm;

import Algorithm.util.LsgreedyUtil;

public class Lsgreedy {
    private final long[] td_time;
    private final double[] td_dirty;
    private double[] td_repair;

    public Lsgreedy(long[] td_time, double[] td_dirty) throws Exception {
        this.td_time = td_time;
        this.td_dirty = td_dirty;
        long startTime = System.currentTimeMillis();
        this.repair();
        long endTime = System.currentTimeMillis();
        System.out.println("Lsgreedy time cost:" + (endTime - startTime) + "ms");
    }

    public double[] getTd_repair() {
        return td_repair;
    }

    private void repair() throws Exception {
        LsgreedyUtil lsgreedyUtil = new LsgreedyUtil(td_time, td_dirty);
        lsgreedyUtil.repair();
        td_repair = lsgreedyUtil.getRepaired();
    }
}
