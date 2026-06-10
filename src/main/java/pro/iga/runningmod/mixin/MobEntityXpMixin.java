package pro.iga.runningmod.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import pro.iga.runningmod.ModConfig;
import pro.iga.runningmod.RunningMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * "Дух животных" — визер, который ванильно роняет 50 опыта. Для помеченного
 * Духа отдаём настраиваемое (меньшее) количество опыта.
 */
@Mixin(MobEntity.class)
public class MobEntityXpMixin {

    @Inject(method = "getXpToDrop", at = @At("HEAD"), cancellable = true)
    private void runningmod$spiritXp(CallbackInfoReturnable<Integer> cir) {
        Entity self = (Entity) (Object) this;
        if (self.getCommandTags().contains(RunningMod.SPIRIT_TAG)) {
            cir.setReturnValue(Math.max(0, ModConfig.spiritXp));
        }
    }
}
