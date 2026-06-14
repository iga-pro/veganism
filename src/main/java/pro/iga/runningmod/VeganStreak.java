package pro.iga.runningmod;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Учёт "веганского стрика" — сколько тиков подряд игрок не ел не-веганскую еду.
 * Пока стрик длиннее порога, игроку выдаётся бонус ("Чистая совесть").
 *
 * <p>Хранится в памяти, привязано к UUID игрока: сбрасывается при поедании
 * мяса и при перезапуске сервера, переживает смерть и пере-заход в мир.
 */
public final class VeganStreak {

    private static final Map<UUID, Integer> TICKS = new ConcurrentHashMap<>();

    private VeganStreak() {
    }

    /** Игрок поел не-веганскую еду — стрик обнуляется. */
    public static void reset(UUID id) {
        TICKS.put(id, 0);
    }

    /** Увеличить стрик на тик и вернуть новое значение. */
    public static int increment(UUID id) {
        return TICKS.merge(id, 1, Integer::sum);
    }
}
