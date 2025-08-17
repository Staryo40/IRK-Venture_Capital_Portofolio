package irk.staryo.utils;

import irk.staryo.model.DiscretePMF;
import irk.staryo.model.Startup;
import irk.staryo.model.StartupPMF;

import java.util.*;

public class StartupCombination {
    public static boolean checkValidStartupDate(List<Startup> startups, Integer dateIndex){
        for (Startup s : startups) {
            int curDateIndex = s.getProceedsScenarioTrend().getOptimistic().size() - 1;
            if (Math.abs(dateIndex) > curDateIndex){
                return false;
            }
        }

        return true;
    }

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

            current.remove(current.size() - 1);
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

    public static List<String> generateCombinationBitmasks(List<Startup> startups, int costLimit) {
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

        return result;
    }


}
