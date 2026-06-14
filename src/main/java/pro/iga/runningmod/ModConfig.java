package pro.iga.runningmod;

/**
 * Общие настройки мода. В одиночной игре клиентский GUI и серверная логика
 * работают в одном процессе, поэтому читают/пишут эти же статические поля.
 */
public final class ModConfig {

    /** Включена ли "месть мобов". */
    public static boolean revengeEnabled = true;
    /** Включён ли предсмертный крик животных. */
    public static boolean screamEnabled = true;

    /** Радиус, в котором мобы замечают убийство и звереют (блоки). */
    public static double witnessRadius = 24.0;
    /** Радиус разлёта красных частиц при крике (блоки). */
    public static double screamRadius = 24.0;
    /** Сколько секунд мобы остаются злыми. */
    public static int angerSeconds = 30;
    /** Множитель урона при столкновении (поверх урона за сложность). */
    public static float damageMultiplier = 1.0f;
    /** Множитель количества партиклов (во всех эффектах). */
    public static float particleMultiplier = 1.0f;

    /** Призывать ли "Духа животных" (визера). */
    public static boolean witherEnabled = true;
    /** Сколько животных нужно убить для призыва. */
    public static int witherKillThreshold = 3;
    /** Здоровье "Духа животных". */
    public static int witherHealth = 10;
    /** Сколько опыта роняет "Дух животных" (обычный визер — 50). */
    public static int spiritXp = 5;

    /** Выдавать ли веганский бонус за стрик без мяса. */
    public static boolean veganBonusEnabled = true;
    /** Сколько секунд без не-веганской еды нужно держать для бонуса. */
    public static int veganBonusSeconds = 300;

    /** Язык сообщений мода: авто (из игры) / русский / английский. */
    public static Lang.Mode language = Lang.Mode.AUTO;

    private ModConfig() {
    }

    public static int angerTicks() {
        return angerSeconds * 20;
    }
}
