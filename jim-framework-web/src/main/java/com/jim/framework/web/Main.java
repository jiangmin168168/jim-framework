package com.jim.framework.web;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {

        testGraph();
    }

    static void testGraph(){
        Scanner scan = new Scanner(System.in);

        //System.out.println("请输入城市数量以及航班数量");
        String citiesFlightsNumString = scan.nextLine();

        int citiesNum = Integer.valueOf(citiesFlightsNumString.split(" ")[0]);
        int flightsNum = Integer.valueOf(citiesFlightsNumString.split(" ")[1]);
        //System.out.println("获得城市数量:"+citiesNum);
        //System.out.println("获得航班数量:"+flightsNum);

        //初始化城市数据
        List<City> cities = new ArrayList<City>();
        for (int i = 1; i <= citiesNum; i++) {
            int price = i == 1 ? 0 : Integer.MAX_VALUE;
            City a = new City(String.valueOf(i), price);
            cities.add(a);
        }

        //初始化航班数据，从哪到哪多少钱，默认均不可达
        int[][] flights = new int[citiesNum][citiesNum];
        for (int row = 0; row < citiesNum; row++) {
            for (int col = 0; col < citiesNum; col++) {
                flights[row][col] = Integer.MAX_VALUE;
            }
        }
        //根据输入的航班数据进行航班数据更新
        initDemoEdges(flights, citiesNum, flightsNum, scan);

        Graph graph = new Graph(cities, flights);
        graph.search();

        int maxTotalPrice=0;
        for(City city:cities){
            if(city.getMaxTotalPrice()==Integer.MAX_VALUE){
                continue;
            }
            if(city.getMaxTotalPrice()>maxTotalPrice){
                maxTotalPrice=city.getMaxTotalPrice();
            }
        }
        System.out.println(maxTotalPrice);
    }



    /**
     * 根据控制台输出的信息初始化航班数据
     */
    static void initDemoEdges(int[][] flights, int citiesNum, int flightsNum, Scanner scan) {

        for (int i = 0; i < flightsNum; i++) {
            //System.out.println("请输入航班信息");
            String fightInfoString = scan.nextLine();
            //System.out.println("获得航班信息:"+fightInfoString);
            int sourceCity = Integer.valueOf(fightInfoString.split(" ")[0]) - 1;
            int destinationCity = Integer.valueOf(fightInfoString.split(" ")[1]) - 1;
            int price = Integer.valueOf(fightInfoString.split(" ")[2]);
            flights[sourceCity][destinationCity] = price;
            flights[destinationCity][sourceCity] = price;
        }
    }
}

/**
 * 城市对象
 */
class City implements Comparable<City> {

    /**
     * 城市名称
     */
    private String name;

    /**
     * 最大的总价
     */
    private int maxTotalPrice;

    /**
     * 遍历城市时使用
     * 标记此城市是否被访问过
     */
    private boolean isMarked;


    public City(String name) {
        this.name = name;
        this.maxTotalPrice = Integer.MAX_VALUE;
        this.setMarked(false);
    }

    public City(String name, int maxTotalPrice) {
        this.name = name;
        this.maxTotalPrice = maxTotalPrice;
        this.setMarked(false);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxTotalPrice() {
        return maxTotalPrice;
    }

    public void setMaxTotalPrice(int maxTotalPrice) {
        this.maxTotalPrice = maxTotalPrice;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public void setMarked(boolean marked) {
        isMarked = marked;
    }

    @Override
    public int compareTo(City o) {
        return o.maxTotalPrice > maxTotalPrice ? -1 : 1;
    }
}

/**
 * 城市之间航班构成的无向图
 * 如果两个城市间有航班，那么有价格
 * 如果两个城市间没有航班，将价格设置成整数的最大值，用于表示无航班
 */
class Graph {

    private List<City> cities;

    private int[][] flights;

    /**
     * 未访问过的城市
     * 初始时是全部的城市
     */
    private Queue<City> unVisited;

    public Graph(List<City> cities, int[][] flights) {
        this.cities = cities;
        this.flights = flights;
        unVisited = new PriorityQueue<City>();

        for (City v : cities) {
            unVisited.add(v);
        }
    }

    private void updateDistance(City city, City neighbor) {
        int distance = getDistance(city, neighbor) + city.getMaxTotalPrice();
        if (distance < neighbor.getMaxTotalPrice()) {
            neighbor.setMaxTotalPrice(distance);
        }
    }

    private int getDistance(City source, City destination) {
        int sourceIndex = cities.indexOf(source);
        int destIndex = cities.indexOf(destination);
        return flights[sourceIndex][destIndex];
    }

    private List<City> getNeighbors(City v) {
        List<City> neighbors = new ArrayList<City>();
        int position = cities.indexOf(v);
        City neighbor = null;
        int distance;
        for (int i = 0; i < cities.size(); i++) {
            if (i == position) {
                continue;
            }
            distance = flights[position][i];
            if (distance < Integer.MAX_VALUE) {
                neighbor = getFlight(i);
                if (!neighbor.isMarked()) {
                    neighbors.add(neighbor);
                }
            }
        }
        return neighbors;
    }

    private City getFlight(int index) {
        return cities.get(index);
    }

    public void search() {
        while (!unVisited.isEmpty()) {
            City city = unVisited.element();
            city.setMarked(true);
            List<City> neighbors = getNeighbors(city);
            for (City neighbor : neighbors) {
                //从相邻的城市中选取最便宜的
                updateDistance(city, neighbor);
            }
            unVisited.poll();
        }
        //System.out.println("search end");
    }
}




