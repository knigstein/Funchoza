package sim.model;

import sim.engine.MatchContext;
import sim.engine.Team;
import sim.model.GameEntity;
import sim.model.Movable;
import sim.exceptions.SeriousFoulException;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Player extends GameEntity implements Movable {
    private final String name;
    private final Team team;
    private final sim.model.Position position;
    private final int speed;
    private final int shotPower;
    private final int accuracy;
    private final int aggression;
    private int stamina;
    private boolean hasBall;
    private int goals;
    private int fouls;
    private final String ability;

    public Player(String name, Team team, sim.model.Position position, int speed, int shotPower,
                  int accuracy, int aggression, int stamina, String ability, int x, int y) 
                  {

        super(name + "-" + team.getType(), x, y);
        this.name = name;
        this.team = team;
        this.position = position;
        this.speed = speed;
        this.shotPower = shotPower;
        this.accuracy = accuracy;
        this.aggression = aggression;
        this.stamina = stamina;
        this.ability = ability;
        this.hasBall = false;

    }

    @Override
    public void update(MatchContext ctx) {
        Random rnd = ctx.getRandom();
        if (stamina <= 0) return;
        stamina = Math.max(0, stamina - 1);

        if (hasBall) {
            if (rnd.nextInt(100) < 40) attemptShot(ctx);
            else attemptPass(ctx);
        } 

        else {
            if (rnd.nextInt(10) < speed) moveTowards(ctx.getBall().getX(), ctx.getBall().getY(), ctx);
            if (ctx.distance(x,y, ctx.getBall().getX(), ctx.getBall().getY()) <= 1 && rnd.nextInt(100) < aggression*5) {
                try {
                    tackle(ctx);
                } catch (SeriousFoulException e) {
                    ctx.recordEvent(e.getMessage(), this.getId());
                    fouls++;
                }
            }
        }
    }

    @Override
    public void moveTowards(int tx, int ty, MatchContext ctx) {
        int dx = Integer.compare(tx, x);
        int dy = Integer.compare(ty, y);
        int steps = Math.max(1, speed/3);
        x = clamp(x + dx*steps, 1, ctx.getWidth()-2);
        y = clamp(y + dy*steps, 1, ctx.getHeight()-2);
    }

    private int clamp(int v, int a, int b) { 
        return Math.max(a, Math.min(b, v)); 
    }

    private void attemptPass(MatchContext ctx) {
        List<Player> mates = team.getPlayers();
        Player target = mates.get(ctx.getRandom().nextInt(mates.size()));
        int chance = accuracy + stamina/20 + ctx.getRandom().nextInt(10);
        ctx.recordEvent(String.format("%s пытается пасовать к %s", name, target.name), getId());
        hasBall = false;
        if (chance > 8 + ctx.getRandom().nextInt(10)) {
            target.hasBall = true;
            ctx.getBall().setPosition(target.x, target.y);
            ctx.getStats().incrementPasses();
            ctx.recordEvent("Передача успешна", getId());
        } 
        
        else {
            Player opp = ctx.findNearestOpponent(this);
            if (opp != null) {
                opp.hasBall = true;
                ctx.getBall().setPosition(opp.x, opp.y);
                ctx.recordEvent("Передача перехвачена " + opp.name, opp.getId());
            } 
            
            else {
                ctx.getBall().setPosition(ctx.getWidth()/2, ctx.getHeight()/2);
                ctx.recordEvent("Мяч свободен", "system");
            }
        }
    }

    private void attemptShot(MatchContext ctx) {
        ctx.getStats().incrementShots();
        int dist = ctx.distanceToGoal(this);
        int power = shotPower + ctx.getRandom().nextInt(10);
        int acc = accuracy + ctx.getRandom().nextInt(10) - dist;
        ctx.recordEvent(name + " бьёт по воротам с дистанции " + dist, getId());
        hasBall = false;
        ctx.getBall().setPosition(ctx.goalXFor(team), ctx.getHeight()/2);
        Player keeper = team.getOpponent().getGoalkeeper();
        boolean goal = acc + power > 12 + ctx.getRandom().nextInt(10);
        if (!goal) {
            if (keeper != null && ctx.getRandom().nextInt(10) < keeper.stamina/20 + keeper.accuracy/5) {
                ctx.recordEvent("Вратарь " + keeper.name + " отразил удар", keeper.getId());
            } else {
                ctx.recordEvent("Удар мимо или в створ не дошёл", "system");
            }
        } 
        
        else {
            scorer(goalScoredBy(team, this, ctx), this, ctx);
        }
    }

    private Team goalScoredBy(Team t, Player scorer, MatchContext ctx) {
        scorer.goals++;
        ctx.getStats().addGoalFor(t.getType());
        ctx.recordEvent("ГООООЛ! " + scorer.name + " забивает за " + t.getType(), scorer.getId());
        ctx.getBall().setPosition(ctx.getWidth()/2, ctx.getHeight()/2);
        return t;
    }

    private void scorer(Team t, Player p, MatchContext ctx) { /* placeholder */ }

    private void tackle(MatchContext ctx) throws SeriousFoulException {
        int sev = ctx.getRandom().nextInt(100);
        if (sev < aggression * 3) throw new SeriousFoulException(name + " совершил грубый фол!");
        
        else {
            if (ctx.getRandom().nextBoolean()) {
                hasBall = true;
                ctx.getBall().setPosition(x,y);
                ctx.recordEvent(name + " отобрал мяч", getId());
            } 
            
            else {
                ctx.recordEvent(name + " не сумел отобрать мяч", getId());
            }
        }

        fouls++;
    }

    public String getName() { 
        return name; 
    }

    public Team getTeam() { 
        return team; 
    }

    public boolean hasBall() { 
        return hasBall; 
    } 

    public void setHasBall(boolean v) { 
        this.hasBall = v; 
    }

    public int getGoals() { 
        return goals; 
    }

    public int getFouls() { 
        return fouls; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player p = (Player)o;
        return Objects.equals(id, p.id);
    }

    @Override
    public int hashCode() { 
        return Objects.hash(id); 
    }

    @Override
    public String toString() {
        return String.format("%s(%s) pos=%s sp=%d acc=%d sta=%d", name, team.getType(), position, speed, accuracy, stamina);
    }
}
