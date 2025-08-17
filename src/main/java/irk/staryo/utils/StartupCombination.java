package irk.staryo.utils;

import irk.staryo.model.FilteredStringbitStartups;
import irk.staryo.model.Startup;

import java.util.*;

public class StartupCombination {
    public static Map<Startup, Integer> generateStartupIndex(List<Startup> startups) {
        Map<Startup, Integer> startupIndex = new HashMap<>();
        Integer index = 0;
        for (Startup s : startups) {
            startupIndex.put(s, index);
            index++;
        }

        return startupIndex;
    }

    public static List<List<Startup>> generateCombinations(List<Startup> startups, int costLimit) {
        List<List<Startup>> result = new ArrayList<>();
        generateCombinationsHelper(startups, 0, new ArrayList<>(), 0, new HashSet<>(), costLimit, result);
        return result;
    }

    private static void generateCombinationsHelper(List<Startup> startups, int index, List<Startup> current, int currentCost, Set<String> usedSectors, int costLimit, List<List<Startup>> result) {
        for (int i = index; i < startups.size(); i++) {
            Startup s = startups.get(i);
            if (currentCost + s.getTicketSize() > costLimit) continue;
            if (usedSectors.contains(s.getSector())) continue;

            current.add(s);
            usedSectors.add(s.getSector());

            generateCombinationsHelper(startups, i + 1, current, currentCost + s.getTicketSize(), usedSectors, costLimit, result);

            current.removeLast();
            usedSectors.remove(s.getSector());
        }

        if (!current.isEmpty()) {
            boolean canExtendGlobally = false;
            for (Startup s : startups) {
                if (usedSectors.contains(s.getSector())) continue;
                if (currentCost + s.getTicketSize() <= costLimit) {
                    canExtendGlobally = true;
                    break;
                }
            }
            if (!canExtendGlobally) {
                result.add(new ArrayList<>(current));
            }
        }
    }

    // Also filters via sector and ticket size limit
    public static FilteredStringbitStartups generateCombinationBitmasks(List<Startup> startups, int costLimit) {
        assert !startups.isEmpty() : "Startup list cannot be empty to produce sector/ticket size combinations";
        List<List<Startup>> validCombinations = generateCombinations(startups, costLimit);
        Map<Startup, Integer> startupIndex = generateStartupIndex(startups);

        int bitMaskLength = startups.size();
        List<String> result = new ArrayList<>();

        for (List<Startup> combination : validCombinations) {
            StringBuilder mask = new StringBuilder("0".repeat(bitMaskLength));
            for (Startup s : combination) {
                int idx = startupIndex.get(s);
                mask.setCharAt(idx, '1');
            }
            result.add(mask.toString());
        }

        return new FilteredStringbitStartups(result, MapUtils.reverseMap(startupIndex));
    }

    // Target is a negative integer expressing the index of the day before 4 August 2025
    public static FilteredStringbitStartups filterTargetBased(List<Startup> startups, int costLimit, int target) throws Exception {
        assert !startups.isEmpty() : "Startup list cannot be empty to produce target combinations";
        FilteredStringbitStartups sectorCostFiltered = generateCombinationBitmasks(startups, costLimit);
        List<String> stringBitList = sectorCostFiltered.getStringBits();
        Map<Integer, Startup> startupMapping = sectorCostFiltered.getStartupMap();

        for (String s : stringBitList) {
            List<Integer> indexList = StringBitOperation.toIndexList(s);
            int cumulativeMax = 0;
            for (Integer i : indexList){
                Startup current = startupMapping.get(i);
                int listN = current.getProceedsScenarioTrend().getOptimistic().size() - 1;
                if (Math.abs(target) > listN){
                    throw new Exception("Startup " + current.getName() + " does not have P/R/O for " + Math.abs(target) + " days ago");
                }
                int index = listN + target;
                cumulativeMax += current.getProceedsScenarioTrend().getOptimistic().get(index);
            }

            if (cumulativeMax < target){
                stringBitList.remove(s);
            }
        }

        return new FilteredStringbitStartups(stringBitList, startupMapping);
    }
}
