package sim.engine;

import sim.model.Ball;
import sim.model.Player;
import sim.model.Position;
import sim.model.GameEntity;
import sim.model.Movable;
import sim.exceptions.SeriousFoulException;
import sim.exceptions.IllegalGameStateException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MatchEngine {
    private final int width, height, durationSeconds;
    private final MatchContext ctx;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final AtomicBoolean paused = new AtomicBoolean(false);
    private Thread inputThread;

    public MatchEngine(int height, int width, int durationSeconds){
        this.height = height; this.width = width; this.durationSeconds = durationSeconds;
        this.ctx = new MatchContext(width, height);
    }

    public void setupTeams() {
        Team snails = new Team(TeamType.SNAILS);
        Team sharks = new Team(TeamType.SHARKS);
        snails.setOpponent(sharks); sharks.setOpponent(snails);

        ctx.setTeamA(snails); ctx.setTeamB(sharks);

        Random rnd = ctx.getRandom();
        for (Team t : new Team[]{snails, sharks}) {
            Player gk = new Player("GK", t, Position.GOALKEEPER, rnd.nextInt(5)+1, 5, 6, 3, 80,
                    t.getType()==TeamType.SNAILS?"Защитная раковина":"Острые зубы",
                    t==snails?2:width-3, height/2);
            t.addPlayer(gk);
            for (int i=1;i<5;i++){
                Position pos = i<=2?Position.DEFENDER:(i==3?Position.MIDFIELDER:Position.FORWARD);
                String name = (t.getType()==TeamType.SNAILS ? "S" : "A") + i;
                int sp = t.getType()==TeamType.SNAILS ? Math.max(1, rnd.nextInt(6)) : Math.max(1, rnd.nextInt(10));
                int shot = rnd.nextInt(10)+1;
                int acc = rnd.nextInt(10)+1;
                int agg = t.getType()==TeamType.SHARKS ? rnd.nextInt(8)+2 : rnd.nextInt(6);
                int sta = 60 + rnd.nextInt(41);
                String ability = t.getType()==TeamType.SNAILS ? "Слизистый путь" : "Резкий рывок";
                if (t.getType()==TeamType.SNAILS && i==2) ability = "Оставляет слизь: замедляет соперника при контакте";
                if (t.getType()==TeamType.SHARKS && i==3) ability = "Буйный укус: высокий шанс фола, но сильнее в отборе";
                Player p = new Player(name, t, pos, sp, shot, acc, agg, sta, ability,
                        t==snails?3:width-4, height/2 - 2 + i);
                t.addPlayer(p);
            }
        }

        ctx.setBall(new Ball(width/2, height/2));
        Player poss = ctx.getTeamA().getPlayers().get(ctx.getRandom().nextInt(5));
        poss.setHasBall(true);
        ctx.getBall().setPosition(poss.getX(), poss.getY());
    }

    public void start() throws InterruptedException {
        if (executor.isShutdown()) throw new IllegalGameStateException("Engine already stopped");
        startInputThread();
        final int[] seconds = {0};
        executor.scheduleAtFixedRate(() -> {
            if (paused.get()) return;
            tick(seconds[0]++);
            if (seconds[0] >= durationSeconds) {
                executor.shutdown();
                try { 
                    endMatch(); 
                } 
                catch(Exception e) { 
                    e.printStackTrace(); 
                }
            }
        }, 0,1, TimeUnit.SECONDS);
        executor.awaitTermination(durationSeconds+5, TimeUnit.SECONDS);
    }

    private void tick(int tickNumber) {
        ctx.setTimeSeconds(tickNumber);
        for (Player p: ctx.getTeamA().getPlayers()) {
            try { p.update(ctx); } catch (Exception e){ 
                ctx.recordEvent("Ошибка у " + p.getName(), p.getId()); 
            }
        }
        for (Player p: ctx.getTeamB().getPlayers()) {
            try { 
                p.update(ctx); 
            } 
            catch (Exception e){ 
                ctx.recordEvent("Ошибка у " + p.getName(), p.getId()); 
            }
        }
        boolean someoneHasBall = ctx.getTeamA().getPlayers().stream().anyMatch(Player::hasBall) ||
                                ctx.getTeamB().getPlayers().stream().anyMatch(Player::hasBall);
        if (!someoneHasBall) {
            ctx.getBall().setPosition(Math.max(1, Math.min(width-2, ctx.getBall().getX() + (ctx.getRandom().nextInt(3)-1))),
                    Math.max(1, Math.min(height-2, ctx.getBall().getY() + (ctx.getRandom().nextInt(3)-1))));
        }
        // if (ctx.getRandom().nextInt(1000) < 5) {
        //     Player offender = ctx.getRandom().nextBoolean() ? ctx.getTeamA().getPlayers().get(ctx.getRandom().nextInt(5))
        //             : ctx.getTeamB().getPlayers().get(ctx.getRandom().nextInt(5));
        //     try {
        //         offender.tackle(ctx);
        //     } catch (Exception e) {
        //         ctx.recordEvent(e.getMessage(), offender.getId());
        //     }
        // }
        if (tickNumber % 5 == 0) {
            sim.ui.ConsoleUI.renderField(ctx);
        }
    }

    private void startInputThread() {
        inputThread = new Thread(() -> {
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(System.in))) {
                while (true) {
                    String line = br.readLine();
                    if (line == null) break;
                    if (!paused.get()) {
                        paused.set(true);
                        System.out.println("\n== ПАУЗА == Нажмите Enter чтобы продолжить, или введите: stats / history / quit");
                    } else {
                        String cmd = line.trim().toLowerCase();
                        if (cmd.isEmpty()) { paused.set(false); System.out.println("Возобновление..."); }
                        else if (cmd.equals("stats")) System.out.println(ctx.getStats().toString());
                        else if (cmd.equals("history")) System.out.println(ctx.renderHistory());
                        else if (cmd.equals("quit")) { executor.shutdownNow(); break; }
                        else System.out.println("Неизвестная команда.");
                    }
                }
            } catch (Exception e) {
                System.out.println("Input thread error: " + e.getMessage());
            }
        });
        inputThread.setDaemon(true);
        inputThread.start();
    }

    private void endMatch() throws IOException {
        System.out.println("\nМатч завершён! Сохранение истории в history.txt");
        Files.writeString(Path.of("history.txt"), ctx.renderHistory());
        sim.ui.ConsoleUI.renderFinalStats(ctx);
    }
}
