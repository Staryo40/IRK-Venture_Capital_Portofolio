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

    public List<Integer> getOptimistic() {
        return optimistic;
    }

    public void setOptimistic(List<Integer> optimistic) {
        this.optimistic = optimistic;
    }

    public List<Integer> getPessimistic() {
        return pessimistic;
    }

    public void setPessimistic(List<Integer> pessimistic) {
        this.pessimistic = pessimistic;
    }

    public List<Integer> getRealistic() {
        return realistic;
    }

    public void setRealistic(List<Integer> realistic) {
        this.realistic = realistic;
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
