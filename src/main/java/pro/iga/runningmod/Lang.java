package pro.iga.runningmod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

/**
 * Локализация мода. Все игровые сообщения берутся отсюда.
 *
 * <p>Язык определяется так:
 * <ul>
 *   <li>{@code AUTO} — берётся из настроек самого Minecraft ({@link #gameLanguage});</li>
 *   <li>{@code RUSSIAN}/{@code ENGLISH} — принудительно, выбирается в настройках мода.</li>
 * </ul>
 * Поддерживаются русский и английский; для остальных языков игры
 * используется английский.
 */
public final class Lang {

    /** Режим выбора языка (настройка мода). */
    public enum Mode {
        AUTO, RUSSIAN, ENGLISH
    }

    /** Код языка игры (например, "en_us"), который проставляет клиент. */
    public static volatile String gameLanguage = "en_us";

    private Lang() {
    }

    /** Текущий код: "ru" или "en". */
    public static String code() {
        switch (ModConfig.language) {
            case RUSSIAN:
                return "ru";
            case ENGLISH:
                return "en";
            default:
                return gameLanguage != null && gameLanguage.startsWith("ru") ? "ru" : "en";
        }
    }

    private static boolean ru() {
        return "ru".equals(code());
    }

    /** Выбор строки по текущему языку. */
    private static String s(String ru, String en) {
        return ru() ? ru : en;
    }

    // ---------- Имена / ник ----------

    /** Ник, от которого пишет мод. */
    public static String nick() {
        return "Mod";
    }

    /**
     * Имя сущности на текущем языке. Для русского — словарь {@link RussianNames},
     * иначе ванильное имя (его Minecraft сам берёт из своего языка).
     */
    public static MutableText entityName(Entity entity) {
        if (entity instanceof PlayerEntity) {
            return entity.getName().copy();
        }
        if (ru()) {
            return RussianNames.of(entity);
        }
        return entity.getName().copy();
    }

    // ---------- Фразы ----------

    public static String running() {
        return s("Я бегу!", "Im Running!");
    }

    private static final String[] ATTACK_RU = {
            "Не трогай!", "Отстань!", "Ай, больно!", "Хватит меня бить!", "За что?!",
            "Прекрати!", "Оставь меня в покое!", "Ну хватит уже!", "Полегче!",
            "Я тебе ничего не сделал!"
    };
    private static final String[] ATTACK_EN = {
            "Don't touch me!", "Leave me alone!", "Ow, that hurts!", "Stop hitting me!", "What for?!",
            "Cut it out!", "Get away from me!", "That's enough!", "Easy there!",
            "I did nothing to you!"
    };

    public static String[] attackPhrases() {
        return ru() ? ATTACK_RU : ATTACK_EN;
    }

    private static final String[] REVENGE_RU = {
            "Зря ты это сделал...", "Ты пожалеешь!", "Зря!", "Мы отомстим за него!",
            "Не надо было...", "Теперь держись!", "Ты за это ответишь!", "Зря ты тронул наших!"
    };
    private static final String[] REVENGE_EN = {
            "You shouldn't have done that...", "You'll regret it!", "Big mistake!", "We'll avenge them!",
            "You shouldn't have...", "Now you're in for it!", "You'll pay for this!", "Don't touch our own!"
    };

    public static String[] revengePhrases() {
        return ru() ? REVENGE_RU : REVENGE_EN;
    }

    public static String spiritName() {
        return s("Дух животных", "Spirit of the Animals");
    }

    public static String spiritSummon() {
        return s("Ты убил слишком многих!", "You've killed too many!");
    }

    public static String spiritLaugh() {
        return s("Ха-ха-ха! Так тебе и надо!", "Ha-ha-ha! Serves you right!");
    }

    // ---------- Экран настроек ----------

    public static String cfgTitle() {
        return s("Настройки Veganism", "Veganism Settings");
    }

    public static String catGeneral() {
        return s("Общее", "General");
    }

    public static String catSpirit() {
        return s("Дух животных", "Spirit of the Animals");
    }

    public static String optLanguage() {
        return s("Язык сообщений", "Message language");
    }

    public static Text languageName(Mode mode) {
        switch (mode) {
            case RUSSIAN:
                return Text.literal("Русский");
            case ENGLISH:
                return Text.literal("English");
            default:
                return Text.literal(s("Авто (из игры)", "Auto (from game)"));
        }
    }

    public static String optRevenge() {
        return s("Месть мобов", "Mob revenge");
    }

    public static String optScream() {
        return s("Крик животных", "Animal scream");
    }

    public static String optWitnessRadius() {
        return s("Радиус мести (блоки)", "Revenge radius (blocks)");
    }

    public static String optScreamRadius() {
        return s("Радиус крика (блоки)", "Scream radius (blocks)");
    }

    public static String optAnger() {
        return s("Длительность злости (сек)", "Anger duration (sec)");
    }

    public static String optDamage() {
        return s("Множитель урона (×0.1)", "Damage multiplier (×0.1)");
    }

    public static String optParticles() {
        return s("Множитель партиклов (×0.1)", "Particle multiplier (×0.1)");
    }

    public static String optWitherEnabled() {
        return s("Призывать Духа животных", "Summon the Spirit");
    }

    public static String optThreshold() {
        return s("Животных для призыва", "Animals to summon");
    }

    public static String optHealth() {
        return s("Здоровье Духа (HP)", "Spirit health (HP)");
    }

    public static String optSpiritXp() {
        return s("Опыт с Духа", "Spirit XP drop");
    }
}
