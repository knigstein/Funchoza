package sim.ui;

import sim.engine.MatchContext;
import sim.model.Player;

public class ConsoleUI {

    private static final String BORDER = "🟥";     // Красная рамка поля
    private static final String EMPTY = "  ";      // Пустая клетка (2 пробела)
    private static final String BALL  = "⚽";      // Мяч
    private static final String SNAIL = "🐌";      // Игроки улитки
    private static final String SHARK = "🦈";      // Игроки акулы

    private static final String GOAL_LEFT  = "🟦"; // Ворота команды A
    private static final String GOAL_RIGHT = "🟩"; // Ворота команды B

    // ════════════════════════════════════════════════════════════════════════
    //  Основной рендеринг поля
    // ════════════════════════════════════════════════════════════════════════
    public static void renderField(MatchContext ctx) {

        int h = ctx.getHeight();
        int w = ctx.getWidth();
        StringBuilder sb = new StringBuilder();

        sb.append("\n⏱ Время матча: ").append(formatTime(ctx.getTimeSeconds())).append("\n\n");

        for (int r = 0; r < h; r++) {

            for (int c = 0; c < w; c++) {

                // Верхняя/нижняя граница
                if (r == 0 || r == h - 1 || c == 0 || c == w - 1) {
                    sb.append(BORDER);
                    continue;
                }

                // Ворота слева
                if (c == 1 && r >= h/3 && r <= 2*h/3) {
                    sb.append(GOAL_LEFT);
                    continue;
                }

                // Ворота справа
                if (c == w - 2 && r >= h/3 && r <= 2*h/3) {
                    sb.append(GOAL_RIGHT);
                    continue;
                }

                // Мяч
                if (ctx.getBall().getX() == c && ctx.getBall().getY() == r) {
                    sb.append(BALL);
                    continue;
                }

                boolean drawn = false;

                // Улитки
                for (Player p : ctx.getTeamA().getPlayers()) {
                    if (p.getX() == c && p.getY() == r) {
                        sb.append(SNAIL);
                        drawn = true;
                        break;
                    }
                }
                if (drawn) continue;

                // Акулы
                for (Player p : ctx.getTeamB().getPlayers()) {
                    if (p.getX() == c && p.getY() == r) {
                        sb.append(SHARK);
                        drawn = true;
                        break;
                    }
                }
                if (drawn) continue;

                // Центр поля — тонкая линия
                if (r == h / 2) {
                    sb.append("─ ");
                    continue;
                }

                // В остальных местах пусто
                sb.append(EMPTY);
            }
            sb.append("\n");
        }

        sb.append("\n⚔ Счёт: ")
          .append(ctx.getStats().getGoalsFor()[0])
          .append(" : ")
          .append(ctx.getStats().getGoalsFor()[1])
          .append("\n");

        // sb.append("📊 Владение: Улитки 🐌 ")
        //   .append(ctx.getStats().possessionA())
        //   .append("% / Акулы 🦈 ")
        //   .append(ctx.getStats().possessionB())
        //   .append("%\n");

        System.out.println(sb.toString());
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Финальная статистика
    // ════════════════════════════════════════════════════════════════════════
    public static void renderFinalStats(MatchContext ctx) {
        System.out.println("\n=== 🏁 Финальная статистика матча 🏁 ===");
        System.out.println("Счёт: " + ctx.getStats().getGoalsFor()[0] + " - " + ctx.getStats().getGoalsFor()[1]);
        System.out.println("Удары: " + ctx.getStats().getShots() + " | Передачи: " + ctx.getStats().getPasses());
        System.out.println("\n📜 История матча:\n");
        System.out.println(ctx.renderHistory());
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Форматирование времени
    // ════════════════════════════════════════════════════════════════════════
    private static String formatTime(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("%02d:%02d", min, sec);
    }
}
