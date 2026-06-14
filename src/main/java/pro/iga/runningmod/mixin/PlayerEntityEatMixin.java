package pro.iga.runningmod.mixin;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import pro.iga.runningmod.ModEffects;
import pro.iga.runningmod.NonVeganFoods;
import pro.iga.runningmod.VeganStreak;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Пока действует "Веганство", нельзя есть не-веганскую еду:
 * голод не восстанавливается, накладывается Тошнота 5с и Голод 10с (ур.2).
 */
@Mixin(PlayerEntity.class)
public class PlayerEntityEatMixin {

    @Inject(method = "eatFood", at = @At("HEAD"), cancellable = true)
    private void runningmod$veganEat(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        boolean nonVegan = NonVeganFoods.isNonVegan(stack.getItem());
        if (nonVegan && !world.isClient) {
            // Съел мясо — веганский стрик обнуляется.
            VeganStreak.reset(self.getUuid());
        }
        if (self.hasStatusEffect(ModEffects.VEGANISM) && nonVegan) {
            if (!world.isClient) {
                self.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 2));  // 10с
                self.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 200, 1));   // 10с
            }
            world.playSound(null, self.getX(), self.getY(), self.getZ(),
                    SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5f, 0.9f);
            if (!self.getAbilities().creativeMode) {
                stack.decrement(1);
            }
            // Голод НЕ восстанавливается — отменяем штатное поедание.
            cir.setReturnValue(stack);
        }
    }
}
