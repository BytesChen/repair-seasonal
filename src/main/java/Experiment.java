import Algorithm.*;

import java.util.Arrays;

public class Experiment {
    private static final String dataPath = "./data/grid/grid_value_from2020-11-29to2020-12-06_10543.csv";
    //    private static final String dataPath = "./data/power_consumption/Tetuan_city_power_consumption_zone_3_52416.csv";
    //    private static final String dataPath = "./data/synthetic/final_1000000.csv";
//    private static final String dataPath = "./data/temp_grid.csv";
    private static final String saveName = "grid";
    private static final int dataLen = 50000;

    public static Analysis srcdRepair(long[] td_time, double[] td_clean, double[] td_dirty, int period, double k, int max_iter, boolean[] td_bool) throws Exception {
        System.out.println("\nSRCD");
        SRCD srcd = new SRCD(td_time, td_dirty, period, k, max_iter);
        double[] td_repair = srcd.getTd_repair();
        return new Analysis(td_time, td_clean, td_repair, td_bool);
    }

    public static Analysis srrdRepair(long[] td_time, double[] td_clean, double[] td_dirty, int period, double k, int max_iter, boolean[] td_bool) throws Exception {
        System.out.println("\nSRRD");
        SRRD srrd = new SRRD(td_time, td_dirty, period, k, max_iter);
        double[] td_repair = srrd.getTd_repair();
        return new Analysis(td_time, td_clean, td_repair, td_bool);
    }

    public static Analysis screenRepair(long[] td_time, double[] td_clean, double[] td_dirty, boolean[] td_bool) throws Exception {
        System.out.println("\nSCREEN");
        SCREEN screen = new SCREEN(td_time, td_dirty);
        double[] td_repair = screen.getTd_repair();
        return new Analysis(td_time, td_clean, td_repair, td_bool);
    }

    public static Analysis lsgreedyRepair(long[] td_time, double[] td_clean, double[] td_dirty, boolean[] td_bool) throws Exception {
        System.out.println("\nLsgreedy");
        Lsgreedy lsgreedy = new Lsgreedy(td_time, td_dirty);
        double[] td_repair = lsgreedy.getTd_repair();
        return new Analysis(td_time, td_clean, td_repair, td_bool);
    }

    public static Analysis imrRepair(long[] td_time, double[] td_clean, double[] td_dirty, double[] td_label, boolean[] td_bool) throws Exception {
        System.out.println("\nIMR");
        IMR imr = new IMR(td_time, td_dirty, td_label, td_bool);
        double[] td_repair = imr.getTd_repair();
        return new Analysis(td_time, td_clean, td_repair, td_bool);
    }

    public static Analysis ewmaRepair(long[] td_time, double[] td_clean, double[] td_dirty, boolean[] td_bool) throws Exception {
        System.out.println("\nEWMA");
        EWMA ewma = new EWMA(td_time, td_dirty);
        double[] td_repair = ewma.getTd_repair();
        return new Analysis(td_time, td_clean, td_repair, td_bool);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("start load data");
        LoadData loadData = new LoadData(dataPath, dataLen);
        long[] td_time = loadData.getTd_time();
        double[] td_clean = loadData.getTd_clean();
        // for imr
        System.out.println("finish load data");

        // add noise
        System.out.println("start add noise");
        AddNoise addNoise = new AddNoise(td_clean, 2, 1.0, 5, 3);
        double[] td_dirty = addNoise.getTd_dirty();
        System.out.println("end add noise");
//        System.arraycopy(td_clean,0,td_dirty,0,td_clean.length);

//        addNoise.writeRepairResultToFile("./output/" + saveName + "_dirty.csv");

        // label4imr
        System.out.println("start label data");
        LabelData labelData = new LabelData(td_clean, td_dirty, 0.5, 3);
        double[] td_label = labelData.getTd_label();
        boolean[] td_bool = labelData.getTd_bool();

        boolean[] default_bool = new boolean[td_bool.length];
        Arrays.fill(default_bool, false);
        System.out.println("end label data");

        // repair
        Analysis srcd = srcdRepair(td_time, td_clean, td_dirty, 144, 9, 10, default_bool);
        Analysis srrd = srrdRepair(td_time, td_clean, td_dirty, 144, 25, 10, default_bool);
        Analysis screen = screenRepair(td_time, td_clean, td_dirty, default_bool);
        Analysis lsgreedy = lsgreedyRepair(td_time, td_clean, td_dirty, default_bool);
        Analysis imr = imrRepair(td_time, td_clean, td_dirty, td_label, td_bool);
        Analysis ewma = ewmaRepair(td_time, td_clean, td_dirty, default_bool);

        //save
//        srcd.writeRepairResultToFile("./output/srcd_" + saveName + "_repair.csv");
//        srrd.writeRepairResultToFile("./output/srrd_" + saveName + "_repair.csv");
//        screen.writeRepairResultToFile("./output/screen_" + saveName + "repair.csv");
//        lsgreedy.writeRepairResultToFile("./output/lsgreedy_" + saveName + "_repair.csv");
//        imr.writeRepairResultToFile("./output/imr_" + saveName + "_repair.csv");
//        ewma.writeRepairResultToFile("./output/ewma_" + saveName + "_repair.csv");

        System.out.println(
                "\n" +
                        "srcd : " + srcd.getRMSE() + "\n" +
                        "srrd : " + srrd.getRMSE() + "\n" +
                        "screen : " + screen.getRMSE() + "\n" +
                        "lsgreedy : " + lsgreedy.getRMSE() + "\n" +
                        "imr : " + imr.getRMSE() + "\n" +
                        "ewma : " + ewma.getRMSE() + "\n" +
                        ""
        );
    }
}
