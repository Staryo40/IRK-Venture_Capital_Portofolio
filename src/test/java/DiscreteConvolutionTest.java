import irk.staryo.model.DiscretePMF;
import irk.staryo.model.ProceedsScenarioTrend;
import irk.staryo.ui.deal_flow.DealFlow;
import irk.staryo.utils.ConvolutionCalculator;
import irk.staryo.utils.DatabaseLoader;
import irk.staryo.utils.PmfCalculator;
import irk.staryo.utils.Repository;
import org.junit.jupiter.api.Test;

public class DiscreteConvolutionTest {
    @Test
    void testAdd() {
        DatabaseLoader.load();
        DealFlow.startups = Repository.getInstance().getSortedStartups();

        ProceedsScenarioTrend su1 = Repository.getInstance().getStartupList().getFirst().getProceedsScenarioTrend();
        ProceedsScenarioTrend su2 = Repository.getInstance().getStartupList().get(1).getProceedsScenarioTrend();

//        System.out.println("First PST: " + su1);
//        System.out.println("Second PST: " + su2);
        DiscretePMF dp1 = PmfCalculator.pmfFromPert(su1.getPessimistic().getFirst(), su1.getRealistic().getFirst(), su1.getOptimistic().getFirst());
        DiscretePMF dp2 = PmfCalculator.pmfFromPert(su2.getPessimistic().getFirst(), su2.getRealistic().getFirst(), su2.getOptimistic().getFirst());
//        DiscretePMF dp1 = PmfCalculator.pmfFromPert(0, 50, 300);
//        DiscretePMF dp2 = PmfCalculator.pmfFromPert(2, 60, 200);
        System.out.println("First: " + dp1);
        System.out.println("Second: " + dp2);

        long startTime = System.nanoTime();
        DiscretePMF resFFT = ConvolutionCalculator.convolvePMFsFFT(dp1, dp2);
        long stopTime = System.nanoTime();
        System.out.println("FFT Convolution Time: " + (stopTime - startTime));
        System.out.println("FFT Convolution: " + resFFT);


        startTime = System.nanoTime();
        DiscretePMF resNaive = ConvolutionCalculator.convolveNaive(dp1, dp2);
        stopTime = System.nanoTime();
        System.out.println("Naive Convolution Time: " + (stopTime - startTime));
        System.out.println("Naive Convolution: " + resNaive);
    }
}
