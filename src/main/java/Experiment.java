import Algorithm.*;

import java.util.*;

public class Experiment {
        private static final String dataPath = "./data/grid/grid_value_from2020-11-29to2020-12-06_10543.csv";
//    private static final String dataPath = "./data/power_consumption/Tetuan_city_power_consumption_zone_3_52416.csv";
//    private static final String dataPath = "./data/synthetic/final_1000000.csv";
    private static final int dataLen = 10000;

    public static Analysis srcdRepair(ArrayList<Long> td_time, ArrayList<Double> td_clean, ArrayList<Double> td_dirty, int period, double k, int max_iter) throws Exception {
        System.out.println("\nSRCD");
        SRCD srcd = new SRCD(td_time, td_dirty, period, k, max_iter);
        ArrayList<Double> td_repair = srcd.getTd_repair();
        return new Analysis(td_time, td_clean, td_repair);
    }

    public static Analysis srrdRepair(ArrayList<Long> td_time, ArrayList<Double> td_clean, ArrayList<Double> td_dirty, int period, double k, int max_iter) throws Exception {
        System.out.println("\nSRRD");
        SRRD srrd = new SRRD(td_time, td_dirty, period, k, max_iter);
        ArrayList<Double> td_repair = srrd.getTd_repair();
        return new Analysis(td_time, td_clean, td_repair);
    }

    public static Analysis screenRepair(ArrayList<Long> td_time, ArrayList<Double> td_clean, ArrayList<Double> td_dirty) throws Exception {
        System.out.println("\nSCREEN");
        SCREEN screen = new SCREEN(td_time, td_dirty);
        ArrayList<Double> td_repair = screen.getTd_repair();
        return new Analysis(td_time, td_clean, td_repair);
    }

    public static Analysis lsgreedyRepair(ArrayList<Long> td_time, ArrayList<Double> td_clean, ArrayList<Double> td_dirty) throws Exception {
        System.out.println("\nLsgreedy");
        Lsgreedy lsgreedy = new Lsgreedy(td_time, td_dirty);
        ArrayList<Double> td_repair = lsgreedy.getTd_repair();
        return new Analysis(td_time, td_clean, td_repair);
    }

    public static Analysis ewmaRepair(ArrayList<Long> td_time, ArrayList<Double> td_clean, ArrayList<Double> td_dirty) throws Exception {
        System.out.println("\nEWMA");
        EWMA ewma = new EWMA(td_time, td_dirty);
        ArrayList<Double> td_repair = ewma.getTd_repair();
        return new Analysis(td_time, td_clean, td_repair);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("start load data");
        LoadData loadData = new LoadData(dataPath, dataLen);
        ArrayList<Long> td_time = loadData.getTd_time();
        ArrayList<Double> td_clean = loadData.getTd_clean();
        System.out.println("finish load data");

        // add noise
        System.out.println("start add noise");
        AddNoise addNoise = new AddNoise(td_clean, 2, 1.0, 5, 3);
        ArrayList<Double> td_dirty = addNoise.getTd_dirty();
        System.out.println("end add noise");

//        addNoise.writeRepairResultToFile("./dataTemp/_power_dirty.csv");

        // repair
        Analysis srcd = srcdRepair(td_time, td_clean, td_dirty, 100, 9, 10);
        Analysis srrd = srrdRepair(td_time, td_clean, td_dirty, 100, 10, 10);
        Analysis screen = screenRepair(td_time, td_clean, td_dirty);
        Analysis lsgreedy = lsgreedyRepair(td_time, td_clean, td_dirty);
        Analysis ewma = ewmaRepair(td_time, td_clean, td_dirty);

        //save
//        csr.writeRepairResultToFile("./output/csr_power_repair.csv");
//        isr.writeRepairResultToFile("./output/isr_power_repair.csv");
//        screen.writeRepairResultToFile("./output/screen_power_repair.csv");
//        lsgreedy.writeRepairResultToFile("./output/lsgreedy_power_repair.csv");
//        ewma.writeRepairResultToFile("./output/ewma_power_repair.csv");
//
        System.out.println(
                "\n" +
                        "srcd : " + srcd.getRMSE() + "\n" +
                        "srrd : " + srrd.getRMSE() + "\n" +
                        "screen : " + screen.getRMSE() + "\n" +
                        "lsgreedy : " + lsgreedy.getRMSE() + "\n" +
                        "ewma : " + ewma.getRMSE() + "\n" +
                        ""
        );
    }
}
