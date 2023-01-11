import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Random;

public class AddNoise {
    private final ArrayList<Double> td_clean;
    private final ArrayList<Double> td_dirty = new ArrayList<>();

    private final double td_range;  // time series data range

    private final int err_rate;  // error rate rate/1000
    private final double err_range;  // error range

    public AddNoise(ArrayList<Double> td_clean, int rate, double range) {
        this.td_clean = td_clean;

        this.err_rate = rate;
        this.err_range = range;

        this.td_range = calRange();

        this.addNoise();
//        this.addNoiseTemp();
    }

    private double calRange() {  // data range
        double v_min = Double.MAX_VALUE, v_max = Double.MIN_VALUE;
        for (Double value : this.td_clean) {
            if (value < v_min) v_min = value;
            if (value > v_max) v_max = value;
        }
        return v_max - v_min;
    }

    private void addNoise() {
        Random random = new Random(666);

        for (Double value : this.td_clean) {
            int thr = random.nextInt(1000);
            if (thr < this.err_rate) {
                double new_value = value + random.nextGaussian() * this.td_range * this.err_range;
                BigDecimal b = new BigDecimal(new_value);  // 精确的小数位保留2位四舍五入处理
                this.td_dirty.add(b.setScale(5, RoundingMode.HALF_UP).doubleValue());
            } else {
                this.td_dirty.add(value);
            }
        }
    }

    private void addNoiseSeg() {
        Random random = new Random(666);

        for (Double value : this.td_clean) {
            Random r = new Random();
            int i1 = r.nextInt(1000);
            if (i1 < this.err_rate) {
                double new_value = value + random.nextGaussian() * this.td_range * this.err_range;
                BigDecimal b = new BigDecimal(new_value);  // 精确的小数位保留2位四舍五入处理
                this.td_dirty.add(b.setScale(5, RoundingMode.HALF_UP).doubleValue());
            } else {
                this.td_dirty.add(value);
            }
        }
    }

    private void addNoiseTemp() {
        this.td_dirty.addAll(this.td_clean);

        td_dirty.set(750, td_dirty.get(750) + 20000.0);
    }

    public ArrayList<Double> getTd_dirty() {
        return td_dirty;
    }

    public void writeRepairResultToFile(String targetFileName) {
        File writeFile = new File(targetFileName);
        try {
            BufferedWriter writeText = new BufferedWriter(new FileWriter(writeFile));
            writeText.write("timestamp,value");
            for (int j = 0; j < this.td_dirty.size(); j++) {
                writeText.newLine();    //换行
                double val = this.td_dirty.get(j);
                writeText.write(j + "," + val);
            }
            writeText.flush();
            writeText.close();
        } catch (IOException e) {
            System.out.println("Error");
        }
    }

}
