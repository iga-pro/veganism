package pro.iga.runningmod.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import pro.iga.runningmod.ModEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Делает черепа визера менее опасными:
 *  - уменьшает урон от прямого попадания;
 *  - запрещает взрыву черепа ломать блоки.
 */
@Mixin(WitherSkullEntity.class)
public class WitherSkullEntityMixin {

    // Прямое попадание черепа: урон 8 -> 2.
    @ModifyConstant(method = "onEntityHit", constant = @Constant(floatValue = 8.0F))
    private float runningmod$weakerSkull(float original) {
        return 2.0F;
    }

    // Взрыв черепа не разрушает блоки (урон сущностям сохраняется).
    @Redirect(
            method = "onCollision",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFZLnet/minecraft/world/World$ExplosionSourceType;)Lnet/minecraft/world/explosion/Explosion;"
            )
    )
    private Explosion runningmod$noBlockDamage(World world, Entity entity, double x, double y, double z,
                                               float power, boolean createFire, World.ExplosionSourceType type) {
        return world.createExplosion(entity, x, y, z, power, createFire, World.ExplosionSourceType.NONE);
    }

    // Череп не накладывает эффект "Иссушение".
    @Redirect(
            method = "onEntityHit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z"
            )
    )
    private boolean runningmod$noWitherEffect(LivingEntity target, StatusEffectInstance effect, Entity source) {
        return false;
    }

    // Вместо иссушения череп накладывает на игрока "Веганство" на 5 минут (ур.2).
    @Inject(method = "onEntityHit", at = @At("TAIL"))
    private void runningmod$applyVeganism(EntityHitResult entityHitResult, CallbackInfo ci) {
        Entity hit = entityHitResult.getEntity();
        if (!hit.getWorld().isClient && hit instanceof PlayerEntity player) {
            player.addStatusEffect(new StatusEffectInstance(ModEffects.VEGANISM, 6000, 1));
        }
    }
}
