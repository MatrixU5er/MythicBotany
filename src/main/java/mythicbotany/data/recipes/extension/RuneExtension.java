package mythicbotany.data.recipes.extension;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.moddingx.libx.datagen.provider.recipe.RecipeExtension;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.crafting.RunicAltarRecipe;

import java.util.Arrays;

public interface RuneExtension extends RecipeExtension {

    default void runeAltar(ItemLike output, int mana, ItemLike... inputs) {
        this.runeAltar(new ItemStack(output), mana, inputs);
    }

    default void runeAltar(ItemLike output, int mana, Ingredient... inputs) {
        this.runeAltar(new ItemStack(output), mana, inputs);
    }

    default void runeAltar(ItemStack output, int mana, ItemLike... inputs) {
        this.runeAltar(output, mana, Arrays.stream(inputs).map(Ingredient::of).toArray(Ingredient[]::new));
    }

    default void runeAltar(ItemStack output, int mana, Ingredient... inputs) {
        ResourceLocation id = this.provider().loc(output.getItem(), "runic_altar");
        this.output().accept(id, new RunicAltarRecipe(output, Ingredient.of(BotaniaBlocks.livingrock), mana, inputs, new Ingredient[0]), null);
    }
}
