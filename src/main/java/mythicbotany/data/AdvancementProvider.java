package mythicbotany.data;

import mythicbotany.advancement.AlfRepairTrigger;
import mythicbotany.advancement.MjoellnirTrigger;
import mythicbotany.advancement.ModCriteria;
import mythicbotany.alfheim.Alfheim;
import mythicbotany.register.ModBlocks;
import mythicbotany.register.ModEntities;
import mythicbotany.register.ModItems;
import mythicbotany.register.tags.ModItemTags;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantments;
import org.moddingx.libx.datagen.DatagenContext;
import org.moddingx.libx.datagen.provider.AdvancementProviderBase;
import vazkii.botania.common.item.BotaniaItems;

import java.util.Optional;

public class AdvancementProvider extends AdvancementProviderBase {

    public AdvancementProvider(DatagenContext ctx) {
        super(ctx);
    }

    @Override
    public void setup() {
        this.root().display(wandIcon())
                .background(this.mod.resource("textures/block/alfsteel_block.png"))
                .task(this.items(ModItemTags.INGOTS_TERRASTEEL));

        this.advancement("all_runes").display(ModItems.joetunheimRune)
                .tasks(this.itemTasks(
                        BotaniaItems.runeAir,
                        BotaniaItems.runeAutumn,
                        BotaniaItems.runeEarth,
                        BotaniaItems.runeEnvy,
                        BotaniaItems.runeFire,
                        BotaniaItems.runeGluttony,
                        BotaniaItems.runeGreed,
                        BotaniaItems.runeLust,
                        BotaniaItems.runeMana,
                        BotaniaItems.runePride,
                        BotaniaItems.runeSloth,
                        BotaniaItems.runeSpring,
                        BotaniaItems.runeSummer,
                        BotaniaItems.runeWater,
                        BotaniaItems.runeWinter,
                        BotaniaItems.runeWrath,
                        ModItems.asgardRune, ModItems.vanaheimRune, ModItems.alfheimRune,
                        ModItems.midgardRune, ModItems.joetunheimRune, ModItems.muspelheimRune,
                        ModItems.niflheimRune, ModItems.nidavellirRune, ModItems.helheimRune
                ));

        this.advancement("mimir").display(ModItems.gjallarHornFull)
                .task(this.eat(ModItems.gjallarHornFull));

        this.advancement("alfheim").parent("mimir").display(ModItems.dreamCherry)
                .task(this.enter(Alfheim.DIMENSION));

        this.advancement("andwari").parent("alfheim").display(ModItems.andwariRing)
                .task(this.items(ModItems.andwariRing));

        this.advancement("mjoellnir").parent("mimir").display(ModBlocks.mjoellnir)
                .task(this.items(ModBlocks.mjoellnir));

        this.advancement("kill_pixie").parent("mjoellnir").display(ModItems.alfPixieSpawnEgg)
                .task(new Criterion<>(ModCriteria.MJOELLNIR, new MjoellnirTrigger.Instance(Optional.empty(), Optional.empty(), Optional.of(this.entity(ModEntities.alfPixie)))));

        this.advancement("alfsteel").display(ModItems.alfsteelIngot)
                .task(this.items(ModItems.alfsteelIngot));

        this.advancement("mending_repair").parent("alfsteel").display(ModBlocks.alfsteelPylon)
                .task(new Criterion<>(ModCriteria.ALF_REPAIR, new AlfRepairTrigger.Instance(this.stack(Enchantments.MENDING).build())));
    }
    
    private static ItemStack wandIcon() {
        ItemStack stack = new ItemStack(BotaniaItems.dreamwoodWand);
        CompoundTag tag = new CompoundTag();
        tag.putInt("color1", 4);
        tag.putInt("color2", 3);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return stack;
    }
}
