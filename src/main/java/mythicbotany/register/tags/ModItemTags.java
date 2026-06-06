package mythicbotany.register.tags;

import mythicbotany.MythicBotany;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {
    
    public static final TagKey<Item> RITUAL_RUNES = ItemTags.create(MythicBotany.getInstance().resource("ritual_runes"));
    public static final TagKey<Item> ALFHEIM_ORES = ItemTags.create(MythicBotany.getInstance().resource("alfheim_ores"));
    public static final TagKey<Item> ELEMENTIUM_WEAPONS = ItemTags.create(MythicBotany.getInstance().resource("elementium_weapons"));
    public static final TagKey<Item> FEYSYTHIA_LEVEL_1 = ItemTags.create(MythicBotany.getInstance().resource("feysythia_level_0"));
    public static final TagKey<Item> FEYSYTHIA_LEVEL_2 = ItemTags.create(MythicBotany.getInstance().resource("feysythia_level_1"));
    public static final TagKey<Item> FEYSYTHIA_LEVEL_3 = ItemTags.create(MythicBotany.getInstance().resource("feysythia_level_2"));
    public static final TagKey<Item> FEYSYTHIA_LEVEL_4 = ItemTags.create(MythicBotany.getInstance().resource("feysythia_level_3"));
    public static final TagKey<Item> FEYSYTHIA_LEVEL_5 = ItemTags.create(MythicBotany.getInstance().resource("feysythia_level_4"));
    public static final TagKey<Item> INGOTS_MANASTEEL = common("ingots/manasteel");
    public static final TagKey<Item> INGOTS_ELEMENTIUM = common("ingots/elementium");
    public static final TagKey<Item> INGOTS_TERRASTEEL = common("ingots/terrasteel");
    public static final TagKey<Item> NUGGETS_TERRASTEEL = common("nuggets/terrasteel");
    public static final TagKey<Item> DUSTS_MANA = common("dusts/mana");
    public static final TagKey<Item> GEMS_MANA_DIAMOND = common("gems/mana_diamond");
    public static final TagKey<Item> GEMS_DRAGONSTONE = common("gems/dragonstone");

    private static TagKey<Item> common(String path) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", path));
    }
}
