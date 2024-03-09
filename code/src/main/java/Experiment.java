import Algorithm.*;

import java.util.Arrays;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.util.NavigableMap;
import java.util.Objects;

public class Experiment {
    private static final String INPUT_DIR = "";  // need to fill in according to the location of the dataset
    private static final String OUTPUT_DIR = "";
    // dataset
    private static final String[] datasetFileList = {
            "power_5241600.csv",
            "voltage_22825440.csv",
    };
    private static final int[] periodList = {
            144,
            1440,
    };
    private static final int[] dataLenList = {
            52416 * 5,
            228254 * 5,
    };

    // task
    private static final String[] taskList = {
            "scala",
//            "rate",
//            "range",
//            "length",
    };
    private static final double[][] scaleTaskList = {
            {1000000, 4000000},
//            {2.5, 2.5},
//            {1.0, 1.0},
//            {10, 10},
    };

    private static final String[][] ticksTaskList = {
            {"1M,2M,3M,4M,5M", "4M,8M,12M,16M,20M"},
//            {"2,4,6,8,10", "2,4,6,8,10"},
//            {"1,2,3,4,5", "1,2,3,4,5"},
//            {"2,4,6,8,10", "2,4,6,8,10"},
    };

    // parameter
    private static String datasetFile, datasetName, task, x_ticks;
    private static int dataLen, period, max_iter, error_length;
    private static double k, error_range, label_rate, error_rate, scale;
    private static int seed = 666;

    private static void reset(int datasetIdx, int taskIdx) {
        datasetFile = datasetFileList[datasetIdx];
        datasetName = datasetFile.split("_")[0];

        task = taskList[taskIdx];
        x_ticks = ticksTaskList[taskIdx][datasetIdx];
        scale = scaleTaskList[taskIdx][datasetIdx];
        // parameter
        dataLen = dataLenList[datasetIdx];
        // error
        error_rate = 5.0;
        error_range = 2.0;
        error_length = 25;
        // 4seasonal
        period = periodList[datasetIdx];
        max_iter = 5;
        k = 6.;
        // 4imr
        label_rate = 0.5;
    }

    public static Analysis srdRepair(long[] td_time, double[] td_clean, double[] td_dirty, int period, double k, int max_iter, boolean[] td_bool) throws Exception {
        System.out.println("SRD");
        SRD srrd = new SRD(td_time, td_dirty, period, k, max_iter);
        double[] td_repair = srrd.getTd_repair();
        long cost_time = srrd.getCost_time();
        return new Analysis(td_time, td_clean, td_repair, td_bool, cost_time);
    }

    public static Analysis screenRepair(long[] td_time, double[] td_clean, double[] td_dirty, boolean[] td_bool) throws Exception {
        System.out.println("SCREEN");
        SCREEN screen = new SCREEN(td_time, td_dirty);
        double[] td_repair = screen.getTd_repair();
        long cost_time = screen.getCost_time();
        return new Analysis(td_time, td_clean, td_repair, td_bool, cost_time);
    }

    public static Analysis lsgreedyRepair(long[] td_time, double[] td_clean, double[] td_dirty, boolean[] td_bool) throws Exception {
        System.out.println("Lsgreedy");
        Lsgreedy lsgreedy = new Lsgreedy(td_time, td_dirty);
        double[] td_repair = lsgreedy.getTd_repair();
        long cost_time = lsgreedy.getCost_time();
        return new Analysis(td_time, td_clean, td_repair, td_bool, cost_time);
    }

    public static Analysis imrRepair(long[] td_time, double[] td_clean, double[] td_dirty, double[] td_label, boolean[] td_bool) throws Exception {
        System.out.println("IMR");
        IMR imr = new IMR(td_time, td_dirty, td_label, td_bool);
        double[] td_repair = imr.getTd_repair();
        long cost_time = imr.getCost_time();
        return new Analysis(td_time, td_clean, td_repair, td_bool, cost_time);
    }

    public static Analysis ewmaRepair(long[] td_time, double[] td_clean, double[] td_dirty, boolean[] td_bool) throws Exception {
        System.out.println("EWMA");
        EWMA ewma = new EWMA(td_time, td_dirty);
        double[] td_repair = ewma.getTd_repair();
        long cost_time = ewma.getCost_time();
        return new Analysis(td_time, td_clean, td_repair, td_bool, cost_time);
    }

    public static void recordRMSE(String string) throws Exception {
        FileWriter fileWritter = new FileWriter(OUTPUT_DIR + "expRMSE.txt", true);
        BufferedWriter bw = new BufferedWriter(fileWritter);
        bw.write(string);
        bw.close();
    }

    public static void recordTime(String string) throws Exception {
        FileWriter fileWritter = new FileWriter(OUTPUT_DIR + "expTime.txt", true);
        BufferedWriter bw = new BufferedWriter(fileWritter);
        bw.write(string);
        bw.close();
    }

    public static void main_cmp() throws Exception { //synthetic
        for (int datasetIdx = 0; datasetIdx < datasetFileList.length; ++datasetIdx) {
            for (int taskIdx = 0; taskIdx < taskList.length; ++taskIdx) {
                // reset
                reset(datasetIdx, taskIdx);

                System.out.print(datasetName + " " + task + " " + x_ticks + "\n");
                recordRMSE(datasetName + "_rmse " + task + " " + x_ticks + "\n");
                recordTime(datasetName + "_time " + task + " " + x_ticks + "\n");

                for (int base = 1; base <= 5; base++) {
                    switch (task) {
                        case "scala" -> dataLen = base * (int) scale;
                        case "rate" -> error_rate = base * scale;
                        case "range" -> error_range = base * scale;
                        case "length" -> error_length = base * (int) scale;
                    }

                    // start
                    LoadData loadData = new LoadData(INPUT_DIR + datasetFile, dataLen);
                    long[] td_time = loadData.getTd_time();
                    double[] td_clean = loadData.getTd_clean();

                    // add noise
                    AddNoise addNoise = new AddNoise(td_clean, error_rate, error_range, error_length, seed);
                    double[] td_dirty = addNoise.getTd_dirty();

                    // label4imr
                    LabelData labelData = new LabelData(td_clean, td_dirty, label_rate, seed);
                    double[] td_label = labelData.getTd_label();
                    boolean[] td_bool = labelData.getTd_bool();

                    boolean[] default_bool = new boolean[td_bool.length];
                    Arrays.fill(default_bool, false);

                    Analysis analysis;
                    for (int j = 1; j < 6; j++) {
                        switch (j) {
                            case 1 ->
                                    analysis = srdRepair(td_time, td_clean, td_dirty, period, k, max_iter, default_bool);
                            case 2 -> analysis = screenRepair(td_time, td_clean, td_dirty, default_bool);
                            case 3 -> analysis = lsgreedyRepair(td_time, td_clean, td_dirty, default_bool);
                            case 4 -> analysis = imrRepair(td_time, td_clean, td_dirty, td_label, td_bool);
                            default -> analysis = ewmaRepair(td_time, td_clean, td_dirty, default_bool);
                        }
                        recordRMSE(analysis.getRMSE() + ",");
                        recordTime(analysis.getCost_time() + ",");
                        System.gc();
                        Runtime.getRuntime().gc();
                    }
                    recordRMSE("\n");
                    recordTime("\n");
                }
            }
        }
    }

    public static void main_single(String[] args) throws Exception { //k
        int robust_k = 3;
        int period = 12;
        int max_iter = 10;
        int seed = 666;

        double label_rate = 0.3;
        double[] td_clean = {0.6373977808906296, 1.2682098449938954, 1.4816534494876779, 1.662350832095748, 1.7318521450606075, 0.5115599093016385, 0.37636480589573645, -0.4555803576420001, -0.7750904129821361, -0.5376596617520241, -0.6838001429900641, 0.17718029362082788, 0.17553172276693096, 1.1377687244710424, 1.1328629801254417, 1.7047763033844157, 0.870674447059497, 0.6027004648048541, 0.10096784150857682, -0.2746219118875216, -0.8448803714770361, -0.3381377496459961, -0.5565936066057287, -0.32027212768237145, 0.8273534294840243, 1.3902957563637677, 1.3508757189209781, 1.2358099829257498, 1.397294886915466, 0.7963784941695855, 0.8088983755364421, 0.0700281193483478, -0.12085034535862293, -0.5019156674606048, -0.48681747838481393, 0.17617294448852816, 0.33633270661680653, 0.5684015549919433, 1.5714510141759064, 1.1996624124428987, 0.938226739221609, 0.9429702746042874, 0.1398432508200071, 0.25390327125974227, -0.69188000696086, -0.7539278520871808, -0.3554840805514905, 0.32116615480177446, 0.775127391004412, 0.8804545841334374, 1.2240888081956962, 1.014760667118873, 1.1383095721629595, 0.8219381790423066, 0.8130874255151554, 0.2153842781548983, -0.16406330143759118, -0.6177500798870774, -0.15984252820781675, -0.0774795893545398, 0.16541085080327894, 1.3044633003307815, 1.5201263675249808, 1.6114119831079923, 1.0994983094853286, 0.8920296584421894, 0.014972754585266847, -0.2861556176716234, -0.40085951062291175, -0.7342715912251659, -0.40930231138497775, -0.04657145178070171, 0.09443208697740338, 0.9062632350314066, 1.7479875470589556, 1.6720087206294627, 1.520816456867005, 1.3126694817158537, 0.8760339599435029, -0.32068429266250564, -0.21574853634469693, -0.852649215254856, -0.07773356151433142, 0.06767642499896742, 0.8731041624090853, 0.9769692584165923, 0.8991793229010294, 1.6614531364931913, 0.8695833617041182, 0.5457675410690027, 0.9027130074464716, -0.30684567468326207, -0.00391809874879101, -0.5890981005828787, 0.042877748428700846, 0.08922635511965982, 0.6390390459707771, 0.8909194065504902, 1.0187004352408897, 1.1075011150670182, 1.7194985896453014, 1.3290076738650938, 0.36217411853429526, 0.34202597208850893, -0.18762111613987031, -0.281258008358626, -0.2857106525865063, 0.40339464355682453, 0.4332560385400091, 0.5929239090861445, 1.6600580293804503, 1.6880756099131498, 1.138296344977214, 0.6694934326672493, 0.4498616979729019, -0.4627850955713511, -0.4307248270325595, -0.7188189184559031, -0.028297364921567647, 0.33533057309940495};
        double[] td_dirty = {0.6373977808906296, 1.2682098449938954, 1.4816534494876779, 1.662350832095748, 1.7318521450606075, -0.35535741222888273, 0.7085324397033994, -0.4555803576420001, -0.7750904129821361, -0.5376596617520241, -0.6838001429900641, 0.17718029362082788, 0.17553172276693096, 1.1377687244710424, 1.1328629801254417, 1.7047763033844157, 0.870674447059497, 0.6027004648048541, 0.10096784150857682, -0.2746219118875216, -0.8448803714770361, -0.3381377496459961, -0.5565936066057287, -0.32027212768237145, 0.8273534294840243, 1.3902957563637677, 1.3508757189209781, 1.2358099829257498, 1.397294886915466, 0.7963784941695855, 0.4184989314335086, 0.0700281193483478, -0.12085034535862293, -0.5019156674606048, -0.48681747838481393, 0.17617294448852816, 0.33633270661680653, 0.5684015549919433, 1.5714510141759064, 1.1996624124428987, 0.938226739221609, 0.9429702746042874, 0.1398432508200071, 0.25390327125974227, -0.69188000696086, -0.7539278520871808, -0.3554840805514905, 0.32116615480177446, 0.775127391004412, 0.8804545841334374, 1.2240888081956962, 1.014760667118873, 1.1383095721629595, 0.8219381790423066, 0.8130874255151554, 0.2153842781548983, -0.16406330143759118, -0.6177500798870774, -0.15984252820781675, -0.0774795893545398, 0.16541085080327894, 1.3044633003307815, 1.5201263675249808, 1.6114119831079923, 1.0994983094853286, 0.8920296584421894, 0.014972754585266847, -0.2861556176716234, -0.40085951062291175, -0.7342715912251659, -1.2542283217215138, -0.04657145178070171, 0.09443208697740338, 0.9062632350314066, 1.7479875470589556, 1.6720087206294627, 1.520816456867005, 1.3126694817158537, 0.8760339599435029, -0.32068429266250564, -0.21574853634469693, -0.852649215254856, -0.07773356151433142, 0.06767642499896742, 0.8731041624090853, 0.9769692584165923, 0.8991793229010294, 1.6614531364931913, 0.8695833617041182, 0.5457675410690027, 0.9027130074464716, -0.30684567468326207, -0.00391809874879101, -0.5890981005828787, 0.042877748428700846, 0.08922635511965982, 0.6390390459707771, 0.8909194065504902, 1.0187004352408897, 1.1075011150670182, 1.7194985896453014, 1.3290076738650938, 0.36217411853429526, 0.34202597208850893, -0.18762111613987031, -0.281258008358626, -0.2857106525865063, 0.40339464355682453, 0.4332560385400091, 1.2394913238222882, 1.6600580293804503, 1.6880756099131498, 1.138296344977214, 0.6694934326672493, 0.4498616979729019, -0.4627850955713511, -0.4307248270325595, -0.7188189184559031, -0.028297364921567647, 0.33533057309940495};
        long[] td_time = new long[td_clean.length];
        for (int i = 0; i < td_clean.length; ++i)
            td_time[i] = i;


        LabelData labelData = new LabelData(td_clean, td_dirty, label_rate, seed);
        double[] td_label = labelData.getTd_label();
        boolean[] td_bool = labelData.getTd_bool();
        boolean[] default_bool = new boolean[td_bool.length];

        Analysis srd = srdRepair(td_time, td_clean, td_dirty, period, robust_k, max_iter, default_bool);
        Analysis screen = screenRepair(td_time, td_clean, td_dirty, default_bool);
        Analysis lsgreedy = lsgreedyRepair(td_time, td_clean, td_dirty, default_bool);
        Analysis imr = imrRepair(td_time, td_clean, td_dirty, td_label, td_bool);
        Analysis ewma = ewmaRepair(td_time, td_clean, td_dirty, default_bool);

        System.out.println(
                "RMSE:\n" +
                        "srd : " + srd.getRMSE() + "\n" +
                        "screen : " + screen.getRMSE() + "\n" +
                        "lsgreedy : " + lsgreedy.getRMSE() + "\n" +
                        "imr : " + imr.getRMSE() + "\n" +
                        "ewma : " + ewma.getRMSE() + "\n" +
                        ""
        );

        System.out.println(
                "Cost Time:\n" +
                        "srd : " + srd.getCost_time() + "\n" +
                        "screen : " + screen.getCost_time() + "\n" +
                        "lsgreedy : " + lsgreedy.getCost_time() + "\n" +
                        "imr : " + imr.getCost_time() + "\n" +
                        "ewma : " + ewma.getCost_time() + "\n" +
                        ""
        );
    }

    public static void main(String[] args) throws Exception {
        if (INPUT_DIR == "")
            throw new Exception("The location of the dataset needs to be specified.");
        main_cmp();
    }
}
