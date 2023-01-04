import Algorithm.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Experiment {
    private static final String dataPath = "data/cloudwise/dianwang_value_from2020-11-29to2020-12-06_10543.csv";
    private static final int dataLen = 10000;  // 时间序列大小

    public static Analysis seasonalRepair(ArrayList<Long> td_time, ArrayList<Double> td_clean, ArrayList<Double> td_dirty) throws Exception {
        System.out.println("\nSeasonalRepair");
        SeasonalRepair seasonalRepair = new SeasonalRepair(td_time, td_dirty);
        ArrayList<Double> td_repair = seasonalRepair.getTd_repair();
        return new Analysis(td_clean, td_repair);
    }

    public static Analysis screenRepair(ArrayList<Long> td_time, ArrayList<Double> td_clean, ArrayList<Double> td_dirty) throws Exception {
        System.out.println("\nSCREEN");
        SCREEN screen = new SCREEN(td_time, td_dirty);
        ArrayList<Double> td_repair = screen.getTd_repair();
        return new Analysis(td_clean, td_repair);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("start load data");
        LoadData loadData = new LoadData(dataPath, dataLen);
        ArrayList<Long> td_time = loadData.getTd_time();
        ArrayList<Double> td_clean = loadData.getTd_clean();
        System.out.println("finish load data\n\n");

        // add noise
        System.out.println("start add noise");
        AddNoise addNoise = new AddNoise(td_clean, 1, 1.0);
        ArrayList<Double> td_dirty = addNoise.getTd_dirty();
        System.out.println("end add noise");

        // repair
//        Analysis sr = seasonalRepair(td_time, td_clean, td_dirty);
        Analysis screen = screenRepair(td_time, td_clean, td_dirty);

        System.out.println(
//                sr.getRMSE() + "\n" +
                screen.getRMSE()
        );
    }
}
