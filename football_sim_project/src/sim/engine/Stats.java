package sim.engine;

public class Stats {
    private final int[] goalsFor = new int[2];
    private int shots = 0;
    private int passes = 0;
    private int possession = 50;

    public void addGoalFor(TeamType t){ 
        if (t==TeamType.SNAILS) goalsFor[0]++; else goalsFor[1]++; 
    }

    public int[] getGoalsFor(){ 
        return goalsFor;
    }

    public int getShots(){ 
        return shots; 
    }

    public int getPasses(){ 
        return passes; 
    }

    public void incrementShots(){ 
        shots++; 
    }

    public void incrementPasses(){ 
        passes++; 
    }

    public int possessionA(){ 
        return possession; 
    }

    public int possessionB(){ 
        return 100-possession; 
    }

    @Override
    public String toString(){
        return "Счёт: " + goalsFor[0] + " - " + goalsFor[1] + " Удары: " + shots + " Передачи: " + passes +
                " Владение: " + possessionA() + "% / " + possessionB() + "%";
    }
}
