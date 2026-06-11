package pro.iga.runningmod.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import pro.iga.runningmod.RunningMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * "Дух животных" — это визер, который ванильно роняет звезду Незера.
 * Для помеченного тегом Духа отменяем дроп снаряжения (звезды),
 * чтобы из него падал только листок (его роняет RunningMod#onSpiritDeath).
 */
@Mixin(WitherEntity.class)
public class WitherEntityMixin {

    @Inject(method = "dropEquipment", at = @At("HEAD"), cancellable = true)
    private void runningmod$noNetherStar(ServerWorld world, DamageSource source,
                                         boolean causedByPlayer, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (self.getCommandTags().contains(RunningMod.SPIRIT_TAG)) {
            ci.cancel();
        }
    }
}
