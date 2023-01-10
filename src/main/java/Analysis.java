import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Analysis {
    private final ArrayList<Double> td_clean;
    private final ArrayList<Double> td_repair;

    private double MAE;
    private double RMSE;

    public Analysis(ArrayList<Long> td_time, ArrayList<Double> td_clean, ArrayList<Double> td_repair) {
        this.td_clean = td_clean;
        this.td_repair = td_repair;

        this.analysis();
    }

    public void analysis() {
        int dataLen = td_clean.size();
        for (int i = 0; i < dataLen; i++) {
            this.MAE += Math.abs(td_clean.get(i) - td_repair.get(i));
            this.RMSE += Math.pow((td_clean.get(i) - td_repair.get(i)), 2);
        }

        this.MAE = this.MAE / dataLen;
        this.RMSE = Math.sqrt(this.RMSE / dataLen);
    }

    public String getMAE() {
        return String.format("%.3f", this.MAE);
    }

    public String getRMSE() {
        return String.format("%.3f", this.RMSE);
    }

    public void writeRepairResultToFile(String targetFileName) {
        File writeFile = new File(targetFileName);
        try {
            BufferedWriter writeText = new BufferedWriter(new FileWriter(writeFile));
            writeText.write("timestamp,value");
            for (int j = 0; j < this.td_repair.size(); j++) {
                writeText.newLine();    //换行
                double val = this.td_repair.get(j);
                writeText.write(j + "," + val);
            }
            writeText.flush();
            writeText.close();
        } catch (IOException e) {
            System.out.println("Error");
        }
    }
}
