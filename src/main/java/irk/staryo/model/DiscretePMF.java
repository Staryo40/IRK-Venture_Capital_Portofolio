package irk.staryo.model;

import java.util.List;

public class DiscretePMF {
    public final int min;
    public final int max;
    public final List<Double> p;  // probabilities for values [min..max]

    public DiscretePMF(int min, List<Double> p) {
        this.min = min;
        this.p = p;
        this.max = min + p.size() - 1;
    }

    @Override
    public String toString() {
        String result = "DiscretePMF{\n";
        for (int i = min; i <= max; i++){
            result += "   " + i + " = " + p.get(i-min);
                result += "\n";
        }
        result += "}";
        return result;
    }
}

