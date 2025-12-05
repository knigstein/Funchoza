package sim.model;

import sim.engine.MatchContext;

public class Ball extends GameEntity {
    public Ball(int x, int y){ super("BALL", x, y); }
    @Override
    public void update(sim.engine.MatchContext ctx){}
    public void setPosition(int nx, int ny){ 
        this.x = nx; this.y = ny; 
    }
}
