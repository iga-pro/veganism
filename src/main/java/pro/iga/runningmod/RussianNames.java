package pro.iga.runningmod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

/**
 * Словарь русских имён сущностей. Если сущность не найдена в словаре,
 * используется её обычное (ванильное) имя.
 */
public final class RussianNames {

    private static final Map<EntityType<?>, String> NAMES = new HashMap<>();

    static {
        // Мирные / животные
        NAMES.put(EntityType.PIG, "Свинья");
        NAMES.put(EntityType.COW, "Корова");
        NAMES.put(EntityType.MOOSHROOM, "Грибная корова");
        NAMES.put(EntityType.SHEEP, "Овца");
        NAMES.put(EntityType.CHICKEN, "Курица");
        NAMES.put(EntityType.RABBIT, "Кролик");
        NAMES.put(EntityType.HORSE, "Лошадь");
        NAMES.put(EntityType.DONKEY, "Осёл");
        NAMES.put(EntityType.MULE, "Мул");
        NAMES.put(EntityType.LLAMA, "Лама");
        NAMES.put(EntityType.TRADER_LLAMA, "Лама торговца");
        NAMES.put(EntityType.CAMEL, "Верблюд");
        NAMES.put(EntityType.GOAT, "Коза");
        NAMES.put(EntityType.WOLF, "Волк");
        NAMES.put(EntityType.CAT, "Кошка");
        NAMES.put(EntityType.OCELOT, "Оцелот");
        NAMES.put(EntityType.FOX, "Лиса");
        NAMES.put(EntityType.PANDA, "Панда");
        NAMES.put(EntityType.POLAR_BEAR, "Белый медведь");
        NAMES.put(EntityType.BEE, "Пчела");
        NAMES.put(EntityType.PARROT, "Попугай");
        NAMES.put(EntityType.BAT, "Летучая мышь");
        NAMES.put(EntityType.TURTLE, "Черепаха");
        NAMES.put(EntityType.DOLPHIN, "Дельфин");
        NAMES.put(EntityType.AXOLOTL, "Аксолотль");
        NAMES.put(EntityType.FROG, "Лягушка");
        NAMES.put(EntityType.TADPOLE, "Головастик");
        NAMES.put(EntityType.ALLAY, "Алей");
        NAMES.put(EntityType.SNIFFER, "Нюхач");
        NAMES.put(EntityType.SQUID, "Спрут");
        NAMES.put(EntityType.GLOW_SQUID, "Светящийся спрут");
        NAMES.put(EntityType.COD, "Треска");
        NAMES.put(EntityType.SALMON, "Лосось");
        NAMES.put(EntityType.PUFFERFISH, "Иглобрюх");
        NAMES.put(EntityType.TROPICAL_FISH, "Тропическая рыба");
        NAMES.put(EntityType.STRIDER, "Лавоход");
        NAMES.put(EntityType.VILLAGER, "Житель");
        NAMES.put(EntityType.WANDERING_TRADER, "Странствующий торговец");
        NAMES.put(EntityType.IRON_GOLEM, "Железный голем");
        NAMES.put(EntityType.SNOW_GOLEM, "Снежный голем");

        // Враждебные
        NAMES.put(EntityType.ZOMBIE, "Зомби");
        NAMES.put(EntityType.ZOMBIE_VILLAGER, "Зомби-житель");
        NAMES.put(EntityType.HUSK, "Кадавр");
        NAMES.put(EntityType.DROWNED, "Утопленник");
        NAMES.put(EntityType.SKELETON, "Скелет");
        NAMES.put(EntityType.STRAY, "Зимогор");
        NAMES.put(EntityType.WITHER_SKELETON, "Скелет-иссушитель");
        NAMES.put(EntityType.CREEPER, "Крипер");
        NAMES.put(EntityType.SPIDER, "Паук");
        NAMES.put(EntityType.CAVE_SPIDER, "Пещерный паук");
        NAMES.put(EntityType.ENDERMAN, "Эндермен");
        NAMES.put(EntityType.ENDERMITE, "Эндермит");
        NAMES.put(EntityType.SILVERFISH, "Чешуйница");
        NAMES.put(EntityType.WITCH, "Ведьма");
        NAMES.put(EntityType.SLIME, "Слизень");
        NAMES.put(EntityType.MAGMA_CUBE, "Магмовый куб");
        NAMES.put(EntityType.BLAZE, "Ифрит");
        NAMES.put(EntityType.GHAST, "Гаст");
        NAMES.put(EntityType.PIGLIN, "Пиглин");
        NAMES.put(EntityType.PIGLIN_BRUTE, "Свирепый пиглин");
        NAMES.put(EntityType.ZOMBIFIED_PIGLIN, "Зомбифицированный пиглин");
        NAMES.put(EntityType.HOGLIN, "Хоглин");
        NAMES.put(EntityType.ZOGLIN, "Зоглин");
        NAMES.put(EntityType.PHANTOM, "Фантом");
        NAMES.put(EntityType.GUARDIAN, "Страж");
        NAMES.put(EntityType.ELDER_GUARDIAN, "Древний страж");
        NAMES.put(EntityType.SHULKER, "Шалкер");
        NAMES.put(EntityType.VEX, "Досаждатель");
        NAMES.put(EntityType.PILLAGER, "Разбойник");
        NAMES.put(EntityType.VINDICATOR, "Поборник");
        NAMES.put(EntityType.EVOKER, "Заклинатель");
        NAMES.put(EntityType.RAVAGER, "Разоритель");
        NAMES.put(EntityType.WARDEN, "Хранитель");

        // Боссы
        NAMES.put(EntityType.ENDER_DRAGON, "Дракон Края");
        NAMES.put(EntityType.WITHER, "Иссушитель");
    }

    private RussianNames() {
    }

    /**
     * Возвращает имя сущности на русском. Для игроков — их настоящий ник.
     */
    public static MutableText of(Entity entity) {
        if (entity instanceof PlayerEntity) {
            return entity.getName().copy();
        }
        String ru = NAMES.get(entity.getType());
        return ru != null ? Text.literal(ru) : entity.getName().copy();
    }
}
