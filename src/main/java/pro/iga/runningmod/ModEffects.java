package pro.iga.runningmod;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

/**
 * Кастомные эффекты мода.
 */
public final class ModEffects {

    /**
     * Эффект "Веганство". В 1.21 ссылки на эффекты идут через {@link RegistryEntry},
     * поэтому регистрируем и храним именно запись реестра.
     */
    public static final RegistryEntry<StatusEffect> VEGANISM =
            Registry.registerReference(Registries.STATUS_EFFECT,
                    Identifier.of("veganism", "veganism"), new VeganismEffect());

    private ModEffects() {
    }

    public static void init() {
        // Обращение к классу триггерит статическую регистрацию выше.
    }

    private static final class VeganismEffect extends StatusEffect {
        private VeganismEffect() {
            super(StatusEffectCategory.BENEFICIAL, 0x4CAF50); // зелёный
        }
    }
}
