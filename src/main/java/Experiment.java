import Algorithm.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import Algorithm.util.DualHeap;

public class Experiment {
    private static final String dataPath = "data/cloudwise/dianwang_value_from2020-11-29to2020-12-06_10543.csv";
    private static final int dataLen = 10000;  // 时间序列大小

    public static Analysis seasonalRepair(ArrayList<Long> td_time, ArrayList<Double> td_clean, ArrayList<Double> td_dirty, int period) throws Exception {
        System.out.println("\nSeasonalRepair");
        SeasonalRepair seasonalRepair = new SeasonalRepair(td_time, td_dirty, period);
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
//        System.out.println("start load data");
//        LoadData loadData = new LoadData(dataPath, dataLen);
//        ArrayList<Long> td_time = loadData.getTd_time();
//        ArrayList<Double> td_clean = loadData.getTd_clean();
//        System.out.println("finish load data\n\n");

        // add noise
//        System.out.println("start add noise");
//        AddNoise addNoise = new AddNoise(td_clean, 1, 1.0);
//        ArrayList<Double> td_dirty = addNoise.getTd_dirty();
//        System.out.println("end add noise");

        DualHeap dualHeap = new DualHeap();
        for (int i = 1; i < 5; ++i) dualHeap.insert(i);

        dualHeap.erase(1);

        System.out.println(dualHeap.getMedian());

        dualHeap.clear();

        for (int i = 1; i < 5; ++i) dualHeap.insert(i);

        System.out.println(dualHeap.getMedian());

        // repair
//        Analysis sr = seasonalRepair(td_time, td_clean, td_dirty,144);
//        Analysis screen = screenRepair(td_time, td_clean, td_dirty);

//        System.out.println(
//                sr.getRMSE() + "\n" +
//                screen.getRMSE()
//        );
    }
}
