package com.krask.algorithms;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MinimumDistanceBetweenTwoPoints {


    public static void main(String[] args) {
        solution(coordinates);
    }

    private static int[][] coordinates = new int[][]{
            {1, 2, 4},
            {2, 5, 6},
            {21, 15, 66},
            {3, 4, 6},
            {3, 0, 0},
            {0, 0, 0}
    };

    private static double distance(Point point1, Point point2) {
        return Math.sqrt(
                Math.pow(point1.getX() - point2.getX(), 2) +
                Math.pow(point1.getY() - point2.getY(), 2) +
                Math.pow(point1.getZ() - point2.getZ(), 2)
        );
    }


    public static double solution(int[][] arr) {

        List<Point> points = Arrays.stream(arr)
                .map(point -> new Point(point[0], point[1], point[2]))
                .collect(Collectors.toList());

        List<Point> sortedByXPoints = qSortByX(points);

        double d = findMinDistance(sortedByXPoints);

        System.out.println("Minimum distance: " + d);
        //System.out.println("Minimum distance (bruteForce - for check) : " + bruteForce(sortedByXPoints));

        return d;
    }


    private static double findMinDistance(List<Point> points) {
        int size = points.size();

        //System.out.println(points);

        if (size == 2) {
            return distance(points.get(0), points.get(1));
        } else if (size == 3) {
            return getMin(
                    distance(points.get(0), points.get(1)),
                    distance(points.get(1), points.get(2)),
                    distance(points.get(0), points.get(2))
            );
        }

        List<Point> leftPoints = points.subList(0, size/2);
        List<Point> rightPoints = points.subList(size/2, size);

        double d = Math.min(findMinDistance(leftPoints), findMinDistance(rightPoints));

        return checkDistOnBound(leftPoints, rightPoints, d);
    }

    private static double checkDistOnBound(List<Point> leftPoints, List<Point> rightPoints, double minDist) {

        double middleX = findMiddleX(leftPoints, rightPoints);

        List<Point> leftStrip = leftPoints.stream()
                .filter(point -> point.getX() > middleX - minDist)
                .collect(Collectors.toList());

        // здесь просто обычным перебором меряем растояние между точками из leftStrip и rightStrip
        // наверняка можно лучше!
        return rightPoints.stream()
                .filter(point -> point.getX() < middleX + minDist) // получаем rightStrip
                .map(point -> leftStrip.stream()
                            .map(p -> distance(point, p))
                            .filter(d -> d < minDist)
                            .sorted()
                            .findFirst()
                            .orElse(minDist)
                )
                .sorted()
                .findFirst()
                .orElse(minDist);
    }

    private static double findMiddleX(List<Point> pointList1, List<Point> pointList2) {
        int x1 = pointList1.get(pointList1.size() - 1).getX();
        int x2 = pointList2.get(0).getX();
        return x1 + (x2 - x1)/2;
    }

    private static double getMin(double dist1, double dist2, double dist3) {
        return Math.min(Math.min(dist1, dist2), dist3);
    }

    private static List<Point> qSortByX(List<Point> points) {

        if (points.size() <= 1) {
            return points;
        }

        Integer pivot = points.get(0).getX();

        List<Point> lower = points.stream()
                .filter(point -> point.getX() < pivot)
                .collect(Collectors.toList());

        List<Point> eq = points.stream()
                .filter(point -> point.getX().equals(pivot))
                .collect(Collectors.toList());

        List<Point> bigger = points.stream()
                .filter(point -> point.getX() > pivot)
                .collect(Collectors.toList());

        List<Point> lowerAndEq = Stream
                .concat(qSortByX(lower).stream(), eq.stream())
                .collect(Collectors.toList());

        return Stream.concat(lowerAndEq.stream(), qSortByX(bigger).stream())
                .collect(Collectors.toList());
    }

    // For test
    private static double bruteForce(List<Point> points) {
        return points.stream()
                .map(point -> points.stream()
                        //.peek(p -> System.out.println("point1 = " + point + " point2 = " + p))
                        .map(p -> distance(point, p))
                        //.peek(d -> System.out.println("d = " + d))
                        .filter(d -> d != 0)
                        .sorted()
                        .findFirst()
                        .get()
                ).sorted()
                .findFirst()
                .get();
    }

    @Getter
    @AllArgsConstructor
    public static class Point {
        private Integer x;
        private Integer y;
        private Integer z;

        public String toString() {
            return String.format("[%s, %s, %s]", x, y, z);
        }
    }

}
