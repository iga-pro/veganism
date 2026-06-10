package pro.iga.runningmod.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.MilkBucketItem;
import pro.iga.runningmod.ModEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Молоко не снимает "Веганство": при попытке эффект сохраняется,
 * а сверху накладывается Тошнота на 10 секунд (ур.2).
 */
@Mixin(MilkBucketItem.class)
public class MilkBucketItemMixin {

    @Redirect(
            method = "finishUsing",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;clearStatusEffects()Z"
            )
    )
    private boolean runningmod$keepVeganism(LivingEntity user) {
        StatusEffectInstance vegan = user.getStatusEffect(ModEffects.VEGANISM);
        boolean result = user.clearStatusEffects();
        if (vegan != null) {
            // Возвращаем "Веганство" с оставшейся длительностью.
            user.addStatusEffect(new StatusEffectInstance(
                    ModEffects.VEGANISM, vegan.getDuration(), vegan.getAmplifier()));
            // Наказание за попытку: Тошнота 10 секунд, ур.2.
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 1));
        }
        return result;
    }
}
