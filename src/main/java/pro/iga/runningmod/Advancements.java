package pro.iga.runningmod;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Выдача достижений мода из кода. Все достижения мода (кроме корневого)
 * имеют единственный критерий {@code "code"} с триггером
 * {@code minecraft:impossible} — он не срабатывает сам, мы выдаём его вручную.
 */
public final class Advancements {

    /** Имя критерия в JSON-файлах достижений ({@code data/veganism/advancement}). */
    private static final String CRITERION = "code";

    private Advancements() {
    }

    /** Выдать игроку достижение {@code veganism:<id>}, если оно ещё не получено. */
    public static void grant(ServerPlayerEntity player, String id) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }
        AdvancementEntry advancement = server.getAdvancementLoader()
                .get(Identifier.of("veganism", id));
        if (advancement != null) {
            player.getAdvancementTracker().grantCriterion(advancement, CRITERION);
        }
    }
}
