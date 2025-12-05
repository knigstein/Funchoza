package sim.engine;

import sim.model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Team {
    private final TeamType type;
    private final List<Player> players = new ArrayList<>();
    private Team opponent;

    public Team(TeamType t){ 
        this.type = t; 
    }

    public TeamType getType(){ 
        return type; 
    }

    public void setOpponent(Team o){ 
        this.opponent = o; 
    }

    public Team getOpponent(){ 
        return opponent; 
    }

    public void addPlayer(Player p) { 
        players.add(p); 
    }

    public List<Player> getPlayers() { 
        return players; 
    }

    public Player getGoalkeeper(){
        return players.stream().filter(p->p.toString().contains("GOALKEEPER")).findFirst().orElse(players.get(0));
    }

    @Override
    public boolean equals(Object o){
        if (this==o) return true;
        if (!(o instanceof Team)) return false;
        Team t = (Team)o;
        return Objects.equals(type, t.type);
    }

    @Override
    public int hashCode() { 
        return Objects.hash(type); 
    }

    @Override
    public String toString() { 
        return "Team{" + type + "}"; 
    }
}
