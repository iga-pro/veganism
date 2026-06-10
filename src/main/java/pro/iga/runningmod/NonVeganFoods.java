package pro.iga.runningmod;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.Set;

/**
 * Список не-веганской еды (продукты животного происхождения).
 */
public final class NonVeganFoods {

    private static final Set<Item> ITEMS = Set.of(
            Items.BEEF, Items.COOKED_BEEF,
            Items.PORKCHOP, Items.COOKED_PORKCHOP,
            Items.CHICKEN, Items.COOKED_CHICKEN,
            Items.MUTTON, Items.COOKED_MUTTON,
            Items.RABBIT, Items.COOKED_RABBIT, Items.RABBIT_STEW,
            Items.COD, Items.COOKED_COD,
            Items.SALMON, Items.COOKED_SALMON,
            Items.TROPICAL_FISH, Items.PUFFERFISH,
            Items.ROTTEN_FLESH, Items.SPIDER_EYE,
            Items.HONEY_BOTTLE, Items.PUMPKIN_PIE
    );

    private NonVeganFoods() {
    }

    public static boolean isNonVegan(Item item) {
        return ITEMS.contains(item);
    }
}
