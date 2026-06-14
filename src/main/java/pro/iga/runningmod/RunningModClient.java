package pro.iga.runningmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

import java.util.Random;

/**
 * Клиентская часть мода:
 *  - при заходе в креативе или переключении в креатив выводит предупреждение,
 *    что в творческом режиме (и с читами) механики мода не работают;
 *  - при ударе по любой сущности она "жалуется" случайной фразой;
 *  - сообщает серверной логике язык игры (для режима "Авто").
 */
public class RunningModClient implements ClientModInitializer {

    private final Random random = new Random();

    /** Игровой режим в прошлом тике — чтобы поймать переключение в креатив. */
    private GameMode lastGameMode = null;

    /** Подхватывает текущий язык игры в общий помощник локализации. */
    private static void refreshGameLanguage() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getLanguageManager() != null) {
            Lang.gameLanguage = client.getLanguageManager().getLanguage();
        }
    }

    /** Выводит в чат предупреждение про креатив (на языке игры). */
    private static void warnCreative(MinecraftClient client) {
        refreshGameLanguage();
        Text message = Text.literal("[Veganism] ").formatted(Formatting.GOLD)
                .append(Text.literal(Lang.creativeWarning()).formatted(Formatting.YELLOW));
        client.inGameHud.getChatHud().addMessage(message);
    }

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> refreshGameLanguage());

        // Каждый тик следим за игровым режимом: при входе в мир в креативе
        // или при переключении в креатив показываем предупреждение.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.interactionManager == null) {
                lastGameMode = null; // вышли из мира — сбрасываем, чтобы предупредить снова
                return;
            }
            GameMode mode = client.interactionManager.getCurrentGameMode();
            if (mode != lastGameMode) {
                if (mode == GameMode.CREATIVE) {
                    warnCreative(client);
                }
                lastGameMode = mode;
            }
        });

        // Команда /prop открывает GUI настроек.
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("prop").executes(ctx -> {
                    refreshGameLanguage();
                    MinecraftClient client = MinecraftClient.getInstance();
                    client.execute(() -> client.setScreen(ConfigScreens.create(null)));
                    return 1;
                }))
        );

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient) {
                refreshGameLanguage();
                String[] phrases = Lang.attackPhrases();
                String phrase = phrases[random.nextInt(phrases.length)];

                Text message = Text.literal("<")
                        .append(Lang.entityName(entity).formatted(Formatting.YELLOW))
                        .append(Text.literal("> "))
                        .append(Text.literal(phrase).formatted(Formatting.WHITE));

                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(message);
            }
            return ActionResult.PASS;
        });
    }
}
