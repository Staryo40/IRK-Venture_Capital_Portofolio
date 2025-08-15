package irk.staryo.utils;

public class PertCalculator {
    public static double mean(int pessimistic, int realistic, int optimistic){
        double add = (double) (pessimistic + 4 * realistic + optimistic);
        return add / 6;
    }

    public static double standardDeviation(int pessimistic, int optimistic){
        double add = (double) (optimistic - pessimistic);
        return add / 6;
    }
}
