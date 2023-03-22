import java.util.Random;
import java.util.HashSet;

public class LabelData {
    private final HashSet<Integer> hs = new HashSet<Integer>();
    private final int size;
    private final int seed;
    private final double[] td_clean;
    private final double[] td_dirty;
    private final double[] td_label;
    private final boolean[] td_bool;

    public LabelData(double[] td_clean, double[] td_dirty, double rate, int seed) {
        size = td_clean.length;
        this.td_clean = td_clean;
        this.td_dirty = td_dirty;
        this.td_label = new double[size];
        this.td_bool = new boolean[size];
        this.seed = seed;

        selectM(size, (int) (size * rate));
        labelData();
    }

    private void selectM(int n, int m) {
        Random r = new Random();
        r.setSeed(seed);
        int[] idx = new int[n];
        for (int j = 0; j < n; idx[j] = j, j++) ;

        hs.add(0);
        hs.add(1);
        hs.add(2);
        for (int i = 0, tmp, randomIndex; i < m; i++) {
            randomIndex = n - 1 - r.nextInt(n - i);
            tmp = idx[randomIndex];
            hs.add(tmp);
            idx[randomIndex] = idx[i];
            idx[i] = tmp;
        }
    }

    private void labelData() {
        for (int i = 0; i < size; i++) {
            if (hs.contains(i)) {
                td_bool[i] = true;
                td_label[i] = td_clean[i];
            } else {
                td_bool[i] = false;
                td_label[i] = td_dirty[i];
            }
        }
    }

    public double[] getTd_label() {
        return td_label;
    }

    public boolean[] getTd_bool() {
        return td_bool;
    }

}
