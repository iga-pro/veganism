package pro.iga.runningmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Серверная часть мода:
 *  - "месть мобов": при убийстве сущности окрестные мобы бегут за игроком,
 *    на столкновении снимают HP (по сложности × множитель) и пускают частицы;
 *  - предсмертный "крик" животных красными частицами на радиус крика;
 *  - после убийства N животных рядом спавнится "Дух животных" (визер) и атакует.
 */
public class RunningMod implements ModInitializer {

    private static final int DAMAGE_COOLDOWN = 10; // задержка между ударами моба

    // Тег-метка для "Духа животных", чтобы отличать его от обычного визера
    // (используется миксином, чтобы отменить ванильный дроп звезды Незера).
    public static final String SPIRIT_TAG = "runningmod_spirit";

    private static final DustParticleEffect RED_DUST =
            new DustParticleEffect(new Vector3f(1.0f, 0.0f, 0.0f), 1.0f);

    // Обычные мобы, которые "выходят" из Духа при его смерти.
    private static final EntityType<?>[] SPAWN_ANIMALS = {
            EntityType.PIG, EntityType.COW, EntityType.SHEEP, EntityType.CHICKEN, EntityType.RABBIT
    };

    private final Map<UUID, AngryMob> angryMobs = new HashMap<>();
    private final Map<UUID, Integer> animalKills = new HashMap<>();
    private final List<Spirit> spirits = new ArrayList<>();
    private final Random random = new Random();

    @Override
    public void onInitialize() {
        ModEffects.init();

        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (entity instanceof ServerPlayerEntity player) {
                if (killedEntity instanceof AnimalEntity) {
                    Advancements.grant(player, "first_kill");
                    if (ModConfig.screamEnabled) {
                        screamOnDeath(world, killedEntity);
                    }
                    if (ModConfig.witherEnabled) {
                        countAnimalKill(world, player);
                    }
                }
                if (ModConfig.revengeEnabled) {
                    angerNearbyMobs(world, player, killedEntity);
                }
            }
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (entity instanceof ServerPlayerEntity player) {
                // Игрок умер — его "Дух животных" смеётся и исчезает.
                onPlayerDeath(player);
            } else if (entity instanceof WitherEntity wither) {
                // Дух убит — роняет листок и выпускает животных.
                onSpiritDeath(wither, source);
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(this::tick);
    }

    /** Масштабирует количество партиклов по настройке. */
    private int particles(int base) {
        return Math.max(0, Math.round(base * ModConfig.particleMultiplier));
    }

    /** Считает убитых животных и при достижении порога призывает Духа. */
    private void countAnimalKill(ServerWorld world, ServerPlayerEntity player) {
        int count = animalKills.merge(player.getUuid(), 1, Integer::sum);
        if (count >= ModConfig.witherKillThreshold) {
            animalKills.put(player.getUuid(), 0);
            summonSpirit(world, player);
        }
    }

    /** Призыв "Духа животных" — визера с заданным HP, атакующего игрока. */
    private void summonSpirit(ServerWorld world, ServerPlayerEntity player) {
        WitherEntity wither = EntityType.WITHER.create(world);
        if (wither == null) {
            return;
        }

        double angle = random.nextDouble() * Math.PI * 2.0;
        double dist = 4.0;
        double x = player.getX() + Math.cos(angle) * dist;
        double z = player.getZ() + Math.sin(angle) * dist;
        double y = player.getY(); // на уровне игрока, чтобы можно было достать
        wither.refreshPositionAndAngles(x, y, z, (float) Math.toDegrees(angle) + 180.0f, 0.0f);

        EntityAttributeInstance maxHp = wither.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (maxHp != null) {
            maxHp.setBaseValue(ModConfig.witherHealth);
        }
        wither.setHealth(ModConfig.witherHealth);
        wither.setCustomName(Text.literal(Lang.spiritName()).formatted(Formatting.DARK_RED));
        wither.setCustomNameVisible(true);
        wither.setPersistent();
        wither.addCommandTag(SPIRIT_TAG);
        wither.setTarget(player);
        world.spawnEntity(wither);
        spirits.add(new Spirit(wither, player.getUuid()));
        Advancements.grant(player, "summon_spirit");

        world.spawnParticles(RED_DUST, x, y, z, particles(50), 0.6, 0.9, 0.6, 0.05);
        world.playSound(null, x, y, z,
                SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.0f, 1.0f);
        spiritSay(world, Lang.spiritSummon());
    }

    /** Игрок умер — его Дух смеётся и исчезает, счётчик убитых животных сбрасывается. */
    private void onPlayerDeath(ServerPlayerEntity player) {
        animalKills.put(player.getUuid(), 0);
        if (spirits.isEmpty()) {
            return;
        }
        Iterator<Spirit> it = spirits.iterator();
        while (it.hasNext()) {
            Spirit spirit = it.next();
            WitherEntity wither = spirit.wither;
            if (wither.isRemoved() || !wither.isAlive()) {
                it.remove();
            } else if (spirit.playerId.equals(player.getUuid())) {
                laughAndVanish(wither);
                it.remove();
            }
        }
    }

    /** Дух торжествующе смеётся и растворяется. */
    private void laughAndVanish(WitherEntity wither) {
        ServerWorld world = (ServerWorld) wither.getWorld();
        double x = wither.getX();
        double y = wither.getBodyY(0.5);
        double z = wither.getZ();

        world.playSound(null, x, y, z,
                SoundEvents.ENTITY_WITCH_CELEBRATE, SoundCategory.HOSTILE, 1.5f, 0.8f);
        world.spawnParticles(RED_DUST, x, y, z, particles(40), 0.6, 0.9, 0.6, 0.05);
        world.spawnParticles(ParticleTypes.SMOKE, x, y, z, particles(20), 0.4, 0.6, 0.4, 0.02);
        spiritSay(world, Lang.spiritLaugh());

        wither.discard();
    }

    /** Дух убит игроком: роняет листок и выпускает обычных животных. */
    private void onSpiritDeath(WitherEntity wither, DamageSource source) {
        boolean wasSpirit = spirits.removeIf(spirit -> spirit.wither == wither);
        if (!wasSpirit) {
            return; // обычный визер, не наш Дух
        }

        if (source.getAttacker() instanceof ServerPlayerEntity killer) {
            Advancements.grant(killer, "defeat_spirit");
        }

        ServerWorld world = (ServerWorld) wither.getWorld();
        double x = wither.getX();
        double y = wither.getY();
        double z = wither.getZ();

        // Выпадает листок.
        world.spawnEntity(new ItemEntity(world, x, y + 1.0, z, new ItemStack(Items.OAK_LEAVES)));

        // Из Духа "выходят" обычные животные.
        int amount = 4 + random.nextInt(4); // 4..7
        for (int i = 0; i < amount; i++) {
            EntityType<?> type = SPAWN_ANIMALS[random.nextInt(SPAWN_ANIMALS.length)];
            Entity animal = type.create(world);
            if (animal != null) {
                double ax = x + (random.nextDouble() - 0.5) * 4.0;
                double az = z + (random.nextDouble() - 0.5) * 4.0;
                animal.refreshPositionAndAngles(ax, y, az, random.nextFloat() * 360.0f, 0.0f);
                world.spawnEntity(animal);
            }
        }

        world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, x, y + 1.0, z, particles(30), 1.0, 1.0, 1.0, 0.0);
        world.playSound(null, x, y, z, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.NEUTRAL, 0.8f, 1.2f);
    }

    /** Предсмертный "ор": звук + разлёт красных частиц на весь радиус крика. */
    private void screamOnDeath(ServerWorld world, LivingEntity killed) {
        double x = killed.getX();
        double y = killed.getY() + killed.getHeight() * 0.5;
        double z = killed.getZ();
        double radius = ModConfig.screamRadius;

        int count = particles(Math.min(500, (int) (radius * 14)));
        for (int i = 0; i < count; i++) {
            double r = radius * Math.cbrt(random.nextDouble());
            double theta = random.nextDouble() * Math.PI * 2.0;
            double phi = Math.acos(2.0 * random.nextDouble() - 1.0);
            double ox = r * Math.sin(phi) * Math.cos(theta);
            double oy = r * Math.cos(phi) * 0.5;
            double oz = r * Math.sin(phi) * Math.sin(theta);
            world.spawnParticles(RED_DUST, x + ox, y + oy, z + oz, 1, 0.0, 0.0, 0.0, 0.0);
        }

        world.playSound(null, x, y, z,
                SoundEvents.ENTITY_GOAT_SCREAMING_DEATH, SoundCategory.HOSTILE, 1.2f, 1.0f);
    }

    private void angerNearbyMobs(ServerWorld world, ServerPlayerEntity player, LivingEntity killed) {
        Box area = killed.getBoundingBox().expand(ModConfig.witnessRadius);
        List<MobEntity> witnesses = world.getEntitiesByClass(
                MobEntity.class,
                area,
                mob -> mob.isAlive() && mob != killed
        );

        for (MobEntity mob : witnesses) {
            AngryMob existing = angryMobs.get(mob.getUuid());
            if (existing != null) {
                existing.player = player;
                existing.ticksLeft = ModConfig.angerTicks();
            } else {
                angryMobs.put(mob.getUuid(), new AngryMob(mob, player));
            }
        }

        if (!witnesses.isEmpty()) {
            MobEntity speaker = witnesses.get(random.nextInt(witnesses.size()));
            broadcast(world, speaker, randomRevenge());
        }
    }

    private void tick(MinecraftServer server) {
        tickSpirits(server);

        if (ModConfig.veganBonusEnabled) {
            tickVeganBonus(server);
        }

        if (angryMobs.isEmpty()) {
            return;
        }

        List<UUID> toRemove = new ArrayList<>();

        for (Map.Entry<UUID, AngryMob> entry : angryMobs.entrySet()) {
            AngryMob angry = entry.getValue();
            MobEntity mob = angry.mob;
            ServerPlayerEntity player = angry.player;

            angry.ticksLeft--;
            if (angry.damageCooldown > 0) {
                angry.damageCooldown--;
            }

            if (angry.ticksLeft <= 0 || mob.isRemoved() || !mob.isAlive()
                    || player.isRemoved() || player.isDead()
                    || mob.getWorld() != player.getWorld()) {
                toRemove.add(entry.getKey());
                continue;
            }

            mob.setTarget(player);
            mob.getNavigation().startMovingTo(player, 1.3);

            ServerWorld world = (ServerWorld) mob.getWorld();
            if (mob.age % 10 == 0) {
                world.spawnParticles(ParticleTypes.ANGRY_VILLAGER,
                        mob.getX(), mob.getEyeY() + 0.4, mob.getZ(), particles(1), 0.2, 0.2, 0.2, 0.0);
            }

            if (mob.squaredDistanceTo(player) < 2.0 && angry.damageCooldown <= 0) {
                float damage = damageForDifficulty(world.getDifficulty()) * ModConfig.damageMultiplier;
                player.damage(world.getDamageSources().mobAttack(mob), damage);
                angry.damageCooldown = DAMAGE_COOLDOWN;

                world.spawnParticles(RED_DUST,
                        player.getX(), player.getBodyY(0.6), player.getZ(),
                        particles(10), 0.4, 0.5, 0.4, 0.0);
                world.spawnParticles(ParticleTypes.CRIT,
                        player.getX(), player.getBodyY(0.6), player.getZ(),
                        particles(6), 0.3, 0.3, 0.3, 0.1);

                if (random.nextFloat() < 0.25f) {
                    broadcast(world, mob, randomRevenge());
                }
            }
        }

        for (UUID id : toRemove) {
            angryMobs.remove(id);
        }
    }

    /** Прижимает "Духов" к земле, чтобы они не улетали вверх. */
    private void tickSpirits(MinecraftServer server) {
        if (spirits.isEmpty()) {
            return;
        }
        Iterator<Spirit> it = spirits.iterator();
        while (it.hasNext()) {
            Spirit spirit = it.next();
            WitherEntity wither = spirit.wither;
            if (wither.isRemoved() || !wither.isAlive()) {
                it.remove();
                continue;
            }

            ServerPlayerEntity owner = server.getPlayerManager().getPlayer(spirit.playerId);
            double anchorY = (owner != null && owner.getWorld() == wither.getWorld())
                    ? owner.getY() : wither.getY();

            Vec3d v = wither.getVelocity();
            if (wither.getY() > anchorY + 1.0) {
                // слишком высоко — тянем вниз
                wither.setVelocity(v.x, Math.min(v.y, -0.25), v.z);
                wither.velocityModified = true;
            } else if (v.y > 0.0) {
                // у земли — не даём подниматься
                wither.setVelocity(v.x, 0.0, v.z);
                wither.velocityModified = true;
            }
        }
    }

    /**
     * "Веганский бонус": пока игрок держит стрик без не-веганской еды дольше
     * порога, ему раз в 2 секунды обновляется бафф ("Чистая совесть").
     * При первом достижении порога выдаётся одноимённое достижение.
     */
    private void tickVeganBonus(MinecraftServer server) {
        int needed = Math.max(1, ModConfig.veganBonusSeconds) * 20;
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (player.isSpectator()) {
                continue;
            }
            int streak = VeganStreak.increment(player.getUuid());
            if (streak < needed) {
                continue;
            }
            if (streak == needed) {
                Advancements.grant(player, "clean_conscience");
            }
            if (streak % 40 == 0) {
                applyVeganBuff(player);
            }
        }
    }

    /** Бафф за веганский стрик: лёгкая регенерация и скорость. */
    private void applyVeganBuff(ServerPlayerEntity player) {
        // 80 тиков длительности при обновлении раз в 40 тиков — эффект непрерывен.
        // ambient=true, particlesHidden, иконка видна.
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.REGENERATION, 80, 0, true, false, true));
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SPEED, 80, 0, true, false, true));
    }

    /** Чем выше сложность, тем больше HP снимается при столкновении. */
    private float damageForDifficulty(Difficulty difficulty) {
        return switch (difficulty) {
            case PEACEFUL -> 0.5f;
            case EASY -> 1.0f;
            case NORMAL -> 2.0f;
            case HARD -> 3.0f;
        };
    }

    private String randomRevenge() {
        String[] phrases = Lang.revengePhrases();
        return phrases[random.nextInt(phrases.length)];
    }

    private void broadcast(ServerWorld world, MobEntity speaker, String phrase) {
        Text message = Text.literal("<")
                .append(Lang.entityName(speaker).formatted(Formatting.RED))
                .append(Text.literal("> "))
                .append(Text.literal(phrase).formatted(Formatting.WHITE));
        world.getServer().getPlayerManager().broadcast(message, false);
    }

    /** Сообщение в чат от имени "Духа животных". */
    private void spiritSay(ServerWorld world, String phrase) {
        Text message = Text.literal("<")
                .append(Text.literal(Lang.spiritName()).formatted(Formatting.DARK_RED))
                .append(Text.literal("> "))
                .append(Text.literal(phrase).formatted(Formatting.WHITE));
        world.getServer().getPlayerManager().broadcast(message, false);
    }

    /** Состояние одного разозлённого моба. */
    private static final class AngryMob {
        final MobEntity mob;
        ServerPlayerEntity player;
        int ticksLeft = ModConfig.angerTicks();
        int damageCooldown = 0;

        AngryMob(MobEntity mob, ServerPlayerEntity player) {
            this.mob = mob;
            this.player = player;
        }
    }

    /** Призванный "Дух животных" и игрок, для которого он создан. */
    private static final class Spirit {
        final WitherEntity wither;
        final UUID playerId;

        Spirit(WitherEntity wither, UUID playerId) {
            this.wither = wither;
            this.playerId = playerId;
        }
    }
}
