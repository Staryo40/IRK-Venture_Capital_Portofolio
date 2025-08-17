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

    public Double chanceFromMinimum(int target){
        if (target > max) return 0.0;
        int start = Math.max(min, target);
        int startId = start - min;

        Double cumulativeChance = 0.0;
        for (int i = startId; i <= p.size()-1; i++){
            cumulativeChance += p.get(i);
        }
        return cumulativeChance;
    }

    public Double expectedGain(){
        int current = min;
        Double total = 0.0;
        for (int i = 0; i < p.size(); i++){
            total += current * p.get(i);
            current++;
        }
        return total;
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

