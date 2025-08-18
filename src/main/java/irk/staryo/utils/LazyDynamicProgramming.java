package irk.staryo.utils;

import irk.staryo.model.*;

import java.util.*;

public class LazyDynamicProgramming {
    public static PortofolioDpResult execute(FilteredStringbitStartups filteredStartups, int target, int rawIndex) throws Exception {
        assert !filteredStartups.getStringBits().isEmpty() : "Empty string bit after target filtering! No startups that fit the specified criteria";
        Map<String, DiscretePMF> cache = new HashMap<>();

        List<String> sortedStringBit = filteredStartups.getStringBits();
        System.out.println("First list: " + sortedStringBit);
        sortedStringBit.sort(Comparator.comparingInt(StringBitOperation::stringBitOrder));
        LinkedList<String> linkedStringBit = new LinkedList<>(sortedStringBit);

        calculateCombinationConvolution(linkedStringBit, filteredStartups.getStartupMap(), cache, rawIndex);

        Map<String, DiscretePMF> pureMapping = new HashMap<>();
        System.out.println("Second list: " + sortedStringBit);
        for (String s : sortedStringBit){
            if (cache.get(s) == null) System.out.println(s + " is null from cache");
            pureMapping.put(s, cache.get(s));
        }

        Double maxChance = 0.0;
        String maxCombo = "";
        for (Map.Entry<String, DiscretePMF> entry : pureMapping.entrySet()){
            Double curChance = entry.getValue().chanceFromMinimum(target);
            if (curChance > maxChance){
                maxChance = curChance;
                maxCombo = entry.getKey();
            }
        }

        if (maxChance == 0.0){
            return new PortofolioDpResult();
        } else {
            List<Integer> indexList = StringBitOperation.toIndexList(maxCombo);
            List<Startup> startupsResult = new ArrayList<>();
            for (Integer resultI : indexList){
                Startup cur = filteredStartups.getStartupMap().get(resultI);
                startupsResult.add(cur);
            }
            return new PortofolioDpResult(startupsResult, pureMapping.get(maxCombo));
        }
    }

    public static void calculateCombinationConvolution(LinkedList<String> stringBitQueue, Map<Integer, Startup> startupMap, Map<String, DiscretePMF> cache, int rawIndex) throws Exception {
        if (stringBitQueue.isEmpty()){
            return;
        }

        String current = stringBitQueue.poll();
        if (cache.get(current) != null) return;

        if (StringBitOperation.stringBitOrder(current) == 1){
            List<Integer> indexList = StringBitOperation.toIndexList(current);
            int sbIndex = indexList.getFirst();

            Startup su = startupMap.get(sbIndex);
            int listN = su.getProceedsScenarioTrend().getOptimistic().size() - 1;
            if (Math.abs(rawIndex) > listN){
                throw new Exception("Startup " + su.getName() + " does not have P/R/O for " + Math.abs(rawIndex) + " days ago");
            }
            int index = listN + rawIndex;

            ProceedsScenarioTrend suList = su.getProceedsScenarioTrend();
            DiscretePMF pmf = PmfCalculator.pmfFromPert(suList.getPessimistic().get(index), suList.getRealistic().get(index), suList.getOptimistic().get(index));
            cache.put(current, pmf);
        } else {
            List<String> subList = StringBitOperation.getLowerOrderSubsets(current);
            String pivotSub = null;
            for (String curSub : subList){
                if (cache.get(curSub) != null){
                    pivotSub = curSub;
                    break;
                }
            }

            if (pivotSub != null){
                String complement = StringBitOperation.complement(current, pivotSub); // Complement theoretically should be order 1, since pivotSub is current.order - 1

                if (complement.equals(StringBitOperation.emptyStringBitN(current.length()))) {
                    throw new IllegalStateException("Complement was empty for " + current + " with pivot " + pivotSub);
                }

                if (cache.get(complement) == null){
//                    calculateCombinationConvolution(new LinkedList<>(List.of(complement)), startupMap, cache, rawIndex);
                    stringBitQueue.addFirst(complement);
                    calculateCombinationConvolution(stringBitQueue, startupMap, cache, rawIndex); // Ensure its there
                }
                DiscretePMF combinedPMF = ConvolutionCalculator.convolvePMFsFFT(cache.get(pivotSub), cache.get(complement));
                cache.put(current, combinedPMF);
            } else {
                Map<String, Integer> commons = new HashMap<>(); // Common, Frequency
                for (String item : stringBitQueue){
                    if (StringBitOperation.stringBitOrder(current) == StringBitOperation.stringBitOrder(item)){
                        String c = StringBitOperation.findCommonBits(current, item);
                        commons.merge(c, 1, Integer::sum);
                    }
                }

                String bestCommon = null;
                int bestOrder = Integer.MIN_VALUE;
                int bestFreq = Integer.MIN_VALUE;

                for (Map.Entry<String, Integer> entry : commons.entrySet()) {
                    int order = StringBitOperation.stringBitOrder(entry.getKey());
                    int freq = entry.getValue();

                    if (order > bestOrder || (order == bestOrder && freq > bestFreq)) {
                        bestCommon = entry.getKey();
                        bestOrder = order;
                        bestFreq = freq;
                    }
                }

                if (bestCommon == null || bestCommon.equals(StringBitOperation.emptyStringBitN(current.length()))){ // Absolutely No Common
                    String existingSub = findBiggestExistingSub(cache, subList);
                    if (cache.get(existingSub) == null){
//                        calculateCombinationConvolution(new LinkedList<>(List.of(existingSub)), startupMap, cache, rawIndex);
                        stringBitQueue.addFirst(existingSub);
                        calculateCombinationConvolution(stringBitQueue, startupMap, cache, rawIndex); // Ensure its there
                    }

                    String complement = StringBitOperation.complement(current, existingSub);

                    if (complement.equals(StringBitOperation.emptyStringBitN(current.length()))) {
                        throw new IllegalStateException("Complement was empty for " + current + " with pivot " + pivotSub);
                    }

                    if (cache.get(complement) == null){
//                        calculateCombinationConvolution(new LinkedList<>(List.of(complement)), startupMap, cache, rawIndex);
                        stringBitQueue.addFirst(complement);
                        calculateCombinationConvolution(stringBitQueue, startupMap, cache, rawIndex); // Ensure its there
                    }

                    DiscretePMF combinedPMF = ConvolutionCalculator.convolvePMFsFFT(cache.get(existingSub), cache.get(complement));
                    cache.put(current, combinedPMF);
                } else {
                    String complement = StringBitOperation.complement(current, bestCommon);

                    if (complement.equals(StringBitOperation.emptyStringBitN(current.length()))) {
                        throw new IllegalStateException("Complement was empty for " + current + " with pivot " + pivotSub);
                    }

                    if (cache.get(complement) == null){
//                        calculateCombinationConvolution(new LinkedList<>(List.of(complement)), startupMap, cache, rawIndex);
                        stringBitQueue.addFirst(complement);
                        calculateCombinationConvolution(stringBitQueue, startupMap, cache, rawIndex); // Ensure its there
                    }
                    DiscretePMF combinedPMF = ConvolutionCalculator.convolvePMFsFFT(cache.get(bestCommon), cache.get(complement));
                    cache.put(current, combinedPMF);
                }
            }
        }
    }

    public static String findBiggestExistingSub(Map<String, DiscretePMF> cache, List<String> subs){
        int subOrder = StringBitOperation.stringBitOrder(subs.getFirst());
        if (subOrder == 1) {
            for (String curSub : subs) {
                if (cache.get(curSub) != null) {
                    return curSub;
                }
            }
            return subs.getFirst(); // In the case absolutely none is found
        }

        // If not found from subs of sub and not order 1, go deeper
        String bestResult = null;
        for (String sub : subs){
            List<String> sublimenalList = StringBitOperation.getLowerOrderSubsets(sub);
            String curResult = findBiggestExistingSub(cache,sublimenalList);
            int currentOrder = StringBitOperation.stringBitOrder(curResult);
            if (bestResult == null) bestResult = curResult;
            if (currentOrder == subOrder - 1) return curResult; // Return early result if one order down found
            if (currentOrder > StringBitOperation.stringBitOrder(bestResult)) bestResult = curResult;
        }

        return bestResult;
    }
}
