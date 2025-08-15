package irk.staryo.model;

import java.util.List;

public class ProceedsScenarioTrend {
    private List<Integer> pessimistic;
    private List<Integer> realistic;
    private List<Integer> optimistic;

    public ProceedsScenarioTrend(List<Integer> pessimistic, List<Integer> realistic, List<Integer> optimistic) {
        this.pessimistic = pessimistic;
        this.realistic = realistic;
        this.optimistic = optimistic;
    }

    @Override
    public String toString() {
        return "ProceedsScenarioTrend{" +
                "pessimistic=" + pessimistic +
                ", realistic=" + realistic +
                ", optimistic=" + optimistic +
                '}';
    }
}
