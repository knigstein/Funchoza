package sim.model;

public abstract class GameEntity {
    protected int x, y;
    protected final String id;

    public GameEntity(String id, int x, int y) {
        this.id = id; this.x = x; this.y = y;
    }

    public abstract void update(sim.engine.MatchContext ctx);

    public String getId() { 
        return id; 
    }

    public int getX() { 
        return x; 
    }

    public int getY() { 
        return y; 
    }

    @Override
    public String toString() {
        return id + "@" + x + "," + y;
    }
}
