package com.paddy.leetcode;

import java.util.*;
import java.util.stream.Collectors;

public class PeopleWithSecrets {

    public List<Integer> findAllPeople(int n, int[][] meetings, int firstPerson) {

        var result = new HashSet<Integer>();

        Map<Integer, List<Integer>> adjMap = new HashMap<>();

        Map<Integer, List<int[]>> timeMeetingMap = new LinkedHashMap<>();

        List<Integer> initialList = new ArrayList<>();
        initialList.add(firstPerson);
        adjMap.put(0, initialList);

        List<Integer> revInitialList = new ArrayList<>();
        revInitialList.add(0);
        adjMap.put(firstPerson, revInitialList);

        result.add(0);
        result.add(firstPerson);

        Arrays.sort(meetings, Comparator.comparingInt(a -> a[2]));
        for (int i = 0; i < meetings.length; i++) {
            List<int[]> list = timeMeetingMap.getOrDefault(meetings[i][2], new ArrayList<>());
            list.add(meetings[i]);
            timeMeetingMap.put(meetings[i][2], list);
        }


        for (Map.Entry<Integer, List<int[]>> timeMeetingEntry: timeMeetingMap.entrySet()) {
            Map<Integer, List<Integer>> tempAdjMap = new HashMap<>();
            Set<Integer> visited = new HashSet<>();
            List<int[]> list = timeMeetingEntry.getValue();
            for (int[] meeting: list) {
                List<Integer> adjList = tempAdjMap.getOrDefault(meeting[0], new ArrayList<>());
                adjList.add(meeting[1]);
                tempAdjMap.put(meeting[0], adjList);

                List<Integer> revAdjList = tempAdjMap.getOrDefault(meeting[1], new ArrayList<>());
                revAdjList.add(meeting[0]);
                tempAdjMap.put(meeting[1], revAdjList);
            }

            for (int[] meeting : list) {
                if (result.contains(meeting[0])) {

                    helper(tempAdjMap, visited, meeting[0], result);
                } else if (result.contains(meeting[1])) {

                    helper(tempAdjMap, visited, meeting[1], result);
                }
            }


        }

        return result.stream().collect(Collectors.toList());

    }

    private void helper(Map<Integer, List<Integer>> adjMap, Set<Integer> visited, int node, Set<Integer> result) {
        if (visited.contains(node)) {
            return;
        }

        visited.add(node);
        result.add(node);
        List<Integer> adjNodes = adjMap.getOrDefault(node, new ArrayList<>());
        for (Integer adjNode : adjNodes) {
            helper(adjMap,  visited, adjNode, result);
        }
    }

    public static void main(String[] args) {
        PeopleWithSecrets peopleWithSecrets = new PeopleWithSecrets();

        System.out.println( peopleWithSecrets.findAllPeople(5, new int[][] {{1,2,5}, {2, 3 ,8}, {1, 5, 10}}, 1));
    }
}
