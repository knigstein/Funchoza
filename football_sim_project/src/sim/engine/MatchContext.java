package sim.engine;

import sim.model.Ball;
import sim.model.Player;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MatchContext {
    private final int width, height;
    private final Random random = new Random();
    private Team teamA, teamB;
    private Ball ball;
    private int timeSeconds = 0;
    private final List<EventRecord> events = new ArrayList<>();
    private final Stats stats = new Stats();

    public MatchContext(int width, int height) {
        this.width = width; this.height = height; 
    }

    public Random getRandom() { 
        return random; 
    }

    public int getWidth() { 
        return width; 
    }
    
    public int getHeight() { 
        return height; 
    }

    public Team getTeamA() { 
        return teamA; 
    }

    public Team getTeamB() { 
        return teamB; 
    }

    public void setTeamA(Team t) { 
        this.teamA = t; 
    }

    public void setTeamB(Team t) { 
        this.teamB = t; 
    }

    public Ball getBall() { 
        return ball; 
    }

    public void setBall(Ball b) { 
        this.ball = b; 
    }

    public Stats getStats() { 
        return stats; 
    }

    public List<EventRecord> getEvents() { 
        return events; 
    }

    public int getTimeSeconds() { 
        return timeSeconds; 
    } 

    public void setTimeSeconds(int s) { 
        this.timeSeconds = s; 
    }

    public void recordEvent(String text, String actor) {
        events.add(new EventRecord(LocalTime.now().toString(), text, actor));
    }

    public String renderHistory() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== История матча ===\n");
        for (EventRecord e : events) sb.append("[").append(e.time()).append("] ").append(e.actor()).append(" => ").append(e.text()).append("\n");
        return sb.toString();
    }

    public int distanceToGoal(Player p){
        return Math.abs(goalXFor(p.getTeam()) - p.getX());
    }

    public int goalXFor(Team t) { 
        return t.getType()==TeamType.SNAILS?1:width-2; 
    }

    public Player findNearestOpponent(Player p) {
        Team opp = p.getTeam().getOpponent();
        Player best = null; int bestd = Integer.MAX_VALUE;
        for (Player o: opp.getPlayers()) {
            int d = Math.abs(p.getX()-o.getX()) + Math.abs(p.getY()-o.getY());
            if (d < bestd){ 
                bestd = d; best = o; 
            }
        }
        return best;
    }

    public int distance(int x1,int y1,int x2,int y2){ return Math.abs(x1-x2)+Math.abs(y1-y2); }
}
