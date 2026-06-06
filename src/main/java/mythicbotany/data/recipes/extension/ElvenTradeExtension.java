package mythicbotany.data.recipes.extension;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.moddingx.libx.datagen.provider.recipe.RecipeExtension;
import vazkii.botania.common.crafting.ElvenTradeRecipe;

import java.util.Arrays;
import java.util.List;

public interface ElvenTradeExtension extends RecipeExtension {

    default void elvenTrade(ItemLike output, ItemLike... inputs) {
        this.elvenTrade(new ItemStack(output), inputs);
    }

    default void elvenTrade(ItemLike output, Ingredient... inputs) {
        this.elvenTrade(new ItemStack(output), inputs);
    }
    
    default void elvenTrade(ItemStack output, ItemLike... inputs) {
        this.elvenTrade(output, Arrays.stream(inputs).map(Ingredient::of).toArray(Ingredient[]::new));
    }

    default void elvenTrade(ItemStack output, Ingredient... inputs) {
        ResourceLocation id = this.provider().loc(output.getItem(), "elven_trade");
        this.output().accept(id, new ElvenTradeRecipe(List.of(output), List.of(inputs)), null);
    }
}
