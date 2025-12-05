package sim.model;

import sim.engine.MatchContext;

public interface Movable {
    void moveTowards(int tx, int ty, MatchContext ctx);
}
