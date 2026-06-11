package pro.iga.runningmod.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import pro.iga.runningmod.ModEffects;
import pro.iga.runningmod.NonVeganFoods;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Пока действует "Веганство", не-веганскую еду можно начать есть даже при
 * полной шкале голода — чтобы сработало "наказание" (эффекты без насыщения).
 */
@Mixin(Item.class)
public class ItemUseMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void runningmod$allowVeganPunishment(World world, PlayerEntity user, Hand hand,
                                                 CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        Item self = (Item) (Object) this;
        ItemStack stack = user.getStackInHand(hand);
        if (NonVeganFoods.isNonVegan(self) && user.hasStatusEffect(ModEffects.VEGANISM)) {
            user.setCurrentHand(hand);
            cir.setReturnValue(TypedActionResult.consume(stack));
        }
    }
}
