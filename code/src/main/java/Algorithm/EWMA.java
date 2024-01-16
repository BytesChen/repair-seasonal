package Algorithm;

public class EWMA {
    private final long[] td_time;
    private final double[] td_dirty;
    private final double[] td_repair;
    private double beta = 0.2;
    private final long cost_time;

    public EWMA(long[] td_time, double[] td_dirty) throws Exception {
        this.td_time = td_time;
        this.td_dirty = td_dirty;
        this.td_repair = new double[td_dirty.length];
        long startTime = System.currentTimeMillis();
        this.repair();
        long endTime = System.currentTimeMillis();
        this.cost_time = endTime - startTime;
//        System.out.println("EWMA time cost:" + cost_time + "ms");
    }

    public double[] getTd_repair() {
        return td_repair;
    }
    public long getCost_time() {return cost_time;}

    private void repair() throws Exception {
        double last_ewma = td_dirty[0];
        td_repair[0] = last_ewma;

        for (int i = 1; i < td_dirty.length; ++i) {  // repair
            double new_value = this.beta * last_ewma + (1 - beta) * td_dirty[i];
            td_repair[i] = new_value;
            last_ewma = new_value;
        }
    }
}
