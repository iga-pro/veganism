package pro.iga.runningmod;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Экран настроек мода на Cloth Config. Открывается командой /prop
 * и кнопкой в Mod Menu. Подписи берутся из {@link Lang} и переводятся
 * вместе с остальным модом.
 */
public final class ConfigScreens {

    private ConfigScreens() {
    }

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal(Lang.cfgTitle()));

        ConfigEntryBuilder eb = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Text.literal(Lang.catGeneral()));

        general.addEntry(eb.startEnumSelector(Text.literal(Lang.optLanguage()), Lang.Mode.class, ModConfig.language)
                .setDefaultValue(Lang.Mode.AUTO)
                .setEnumNameProvider(e -> Lang.languageName((Lang.Mode) e))
                .setSaveConsumer(v -> ModConfig.language = v)
                .build());

        general.addEntry(eb.startBooleanToggle(Text.literal(Lang.optRevenge()), ModConfig.revengeEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(v -> ModConfig.revengeEnabled = v)
                .build());

        general.addEntry(eb.startBooleanToggle(Text.literal(Lang.optScream()), ModConfig.screamEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(v -> ModConfig.screamEnabled = v)
                .build());

        general.addEntry(eb.startIntSlider(Text.literal(Lang.optWitnessRadius()),
                        (int) ModConfig.witnessRadius, 0, 64)
                .setDefaultValue(24)
                .setSaveConsumer(v -> ModConfig.witnessRadius = v)
                .build());

        general.addEntry(eb.startIntSlider(Text.literal(Lang.optScreamRadius()),
                        (int) ModConfig.screamRadius, 0, 64)
                .setDefaultValue(24)
                .setSaveConsumer(v -> ModConfig.screamRadius = v)
                .build());

        general.addEntry(eb.startIntSlider(Text.literal(Lang.optAnger()),
                        ModConfig.angerSeconds, 0, 120)
                .setDefaultValue(30)
                .setSaveConsumer(v -> ModConfig.angerSeconds = v)
                .build());

        general.addEntry(eb.startIntSlider(Text.literal(Lang.optDamage()),
                        Math.round(ModConfig.damageMultiplier * 10), 0, 50)
                .setDefaultValue(10)
                .setTextGetter(v -> Text.literal(String.format("%.1f", v / 10.0)))
                .setSaveConsumer(v -> ModConfig.damageMultiplier = v / 10.0f)
                .build());

        general.addEntry(eb.startIntSlider(Text.literal(Lang.optParticles()),
                        Math.round(ModConfig.particleMultiplier * 10), 0, 50)
                .setDefaultValue(10)
                .setTextGetter(v -> Text.literal(String.format("%.1f", v / 10.0)))
                .setSaveConsumer(v -> ModConfig.particleMultiplier = v / 10.0f)
                .build());

        general.addEntry(eb.startBooleanToggle(Text.literal(Lang.optVeganBonus()), ModConfig.veganBonusEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(v -> ModConfig.veganBonusEnabled = v)
                .build());

        general.addEntry(eb.startIntSlider(Text.literal(Lang.optVeganBonusTime()),
                        ModConfig.veganBonusSeconds, 10, 1200)
                .setDefaultValue(300)
                .setSaveConsumer(v -> ModConfig.veganBonusSeconds = v)
                .build());

        ConfigCategory wither = builder.getOrCreateCategory(Text.literal(Lang.catSpirit()));

        wither.addEntry(eb.startBooleanToggle(Text.literal(Lang.optWitherEnabled()), ModConfig.witherEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(v -> ModConfig.witherEnabled = v)
                .build());

        wither.addEntry(eb.startIntSlider(Text.literal(Lang.optThreshold()),
                        ModConfig.witherKillThreshold, 1, 20)
                .setDefaultValue(3)
                .setSaveConsumer(v -> ModConfig.witherKillThreshold = v)
                .build());

        wither.addEntry(eb.startIntSlider(Text.literal(Lang.optHealth()),
                        ModConfig.witherHealth, 1, 300)
                .setDefaultValue(10)
                .setSaveConsumer(v -> ModConfig.witherHealth = v)
                .build());

        wither.addEntry(eb.startIntSlider(Text.literal(Lang.optSpiritXp()),
                        ModConfig.spiritXp, 0, 50)
                .setDefaultValue(5)
                .setSaveConsumer(v -> ModConfig.spiritXp = v)
                .build());

        return builder.build();
    }
}
