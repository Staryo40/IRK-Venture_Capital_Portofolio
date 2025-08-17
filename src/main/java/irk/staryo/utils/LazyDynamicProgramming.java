package irk.staryo.utils;

import irk.staryo.model.*;

import java.util.*;

public class LazyDynamicProgramming {
    public static PortofolioDpResult execute(FilteredStringbitStartups filteredStartups, int rawIndex){
        assert !filteredStartups.getStringBits().isEmpty() : "Empty string bit after target filtering! No startups that fit the specified criteria";
        Map<String, DiscretePMF> cache = new HashMap<>();

        List<String> sortedStringBit = filteredStartups.getStringBits();
        sortedStringBit.sort(Comparator.comparingInt(StringBitOperation::stringBitOrder));
        LinkedList<String> linkedStringBit = new LinkedList<>(sortedStringBit);
    }

    public static void calculateCombinationConvolution(LinkedList<String> stringBitQueue, Map<Integer, Startup> startupMap, Map<String, DiscretePMF> cache, int rawIndex) throws Exception {
        if (stringBitQueue.isEmpty()){
            return;
        }

        String current = stringBitQueue.poll();
        if (StringBitOperation.stringBitOrder(current) == 2){
            List<Integer> indexList = StringBitOperation.toIndexList(current);
            for (Integer i : indexList){
                Startup su = startupMap.get(i);
                int listN = su.getProceedsScenarioTrend().getOptimistic().size() - 1;
                if (Math.abs(rawIndex) > listN){
                    throw new Exception("Startup " + su.getName() + " does not have P/R/O for " + Math.abs(rawIndex) + " days ago");
                }
                int index = listN + rawIndex;

                ProceedsScenarioTrend suList = su.getProceedsScenarioTrend();
                DiscretePMF pmf = PmfCalculator.pmfFromPert(suList.getPessimistic().get(index), suList.getRealistic().get(index), suList.getOptimistic().get(index));
                String curSB = StringBitOperation.indexToStringbit(i, current.length());
                cache.put(curSB, pmf);
            }

            List<String> currentSub = StringBitOperation.getLowerOrderSubsets(current);
            String first = currentSub.get(0);
            String second = currentSub.get(1);
            DiscretePMF combinedPMF = ConvolutionCalculator.convolvePMFsFFT(cache.get(first), cache.get(second));
            cache.put(current, combinedPMF);
        } else {

        }
    }
}
