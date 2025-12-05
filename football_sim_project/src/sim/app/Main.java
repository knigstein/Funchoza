package sim.app;

import sim.engine.MatchEngine;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Добро пожаловать на FUNCHOZA ARENA!!!");
        MatchEngine engine = new MatchEngine(10,30,300);
        engine.setupTeams();
        engine.start();
    }
}