package pro.iga.runningmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;

import java.util.Random;

/**
 * Клиентская часть мода:
 *  - при заходе в мир выводит в чат "<Mod> Im Running!" (на языке игры);
 *  - при ударе по любой сущности она "жалуется" случайной фразой;
 *  - сообщает серверной логике язык игры (для режима "Авто").
 */
public class RunningModClient implements ClientModInitializer {

    private final Random random = new Random();

    /** Подхватывает текущий язык игры в общий помощник локализации. */
    private static void refreshGameLanguage() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getLanguageManager() != null) {
            Lang.gameLanguage = client.getLanguageManager().getLanguage();
        }
    }

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            refreshGameLanguage();

            Text message = Text.literal("<")
                    .append(Text.literal(Lang.nick()).formatted(Formatting.AQUA))
                    .append(Text.literal("> "))
                    .append(Text.literal(Lang.running()).formatted(Formatting.WHITE));

            client.inGameHud.getChatHud().addMessage(message);
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
