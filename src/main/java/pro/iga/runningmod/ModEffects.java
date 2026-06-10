package pro.iga.runningmod;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Кастомные эффекты мода.
 */
public final class ModEffects {

    public static final StatusEffect VEGANISM = new VeganismEffect();

    private ModEffects() {
    }

    public static void init() {
        Registry.register(Registries.STATUS_EFFECT, new Identifier("veganism", "veganism"), VEGANISM);
    }

    private static final class VeganismEffect extends StatusEffect {
        private VeganismEffect() {
            super(StatusEffectCategory.BENEFICIAL, 0x4CAF50); // зелёный
        }
    }
}
