package mythicbotany.data.recipes.extension;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.moddingx.libx.datagen.provider.recipe.RecipeExtension;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.crafting.ManaInfusionRecipe;
import vazkii.botania.common.crafting.StateIngredients;

public interface ManaInfusionExtension extends RecipeExtension {

    default void manaInfusion(ItemLike input, ItemLike result, int mana) {
        this.manaInfusion(input, new ItemStack(result), mana);
    }

    default void manaInfusion(TagKey<Item> input, ItemLike result, int mana) {
        this.manaInfusion(input, new ItemStack(result), mana);
    }

    default void manaInfusion(Ingredient input, ItemLike result, int mana) {
        this.manaInfusion(input, new ItemStack(result), mana);
    }
    
    default void manaInfusion(ItemLike input, ItemStack result, int mana) {
        this.manaInfusion(Ingredient.of(input), result, mana);
    }
    
    default void manaInfusion(TagKey<Item> input, ItemStack result, int mana) {
        this.manaInfusion(Ingredient.of(input), result, mana);
    }
    
    default void manaInfusion(Ingredient input, ItemStack result, int mana) {
        ResourceLocation id = this.provider().loc(result.getItem(), "mana_infusion");
        this.output().accept(id, new ManaInfusionRecipe(result, input, mana, "", StateIngredients.NONE), null);
    }

    default void manaAlchemy(ItemLike input, ItemLike result, int mana) {
        this.manaAlchemy(input, new ItemStack(result), mana);
    }

    default void manaAlchemy(TagKey<Item> input, ItemLike result, int mana) {
        this.manaAlchemy(input, new ItemStack(result), mana);
    }

    default void manaAlchemy(Ingredient input, ItemLike result, int mana) {
        this.manaAlchemy(input, new ItemStack(result), mana);
    }
    
    default void manaAlchemy(ItemLike input, ItemStack result, int mana) {
        this.manaAlchemy(Ingredient.of(input), result, mana);
    }

    default void manaAlchemy(TagKey<Item> input, ItemStack result, int mana) {
        this.manaAlchemy(Ingredient.of(input), result, mana);
    }

    default void manaAlchemy(Ingredient input, ItemStack result, int mana) {
        ResourceLocation id = this.provider().loc(result.getItem(), "mana_alchemy");
        this.output().accept(id, new ManaInfusionRecipe(result, input, mana, "", StateIngredients.of(BotaniaBlocks.alchemyCatalyst)), null);
    }

    default void manaConjuration(ItemLike input, ItemLike result, int mana) {
        this.manaConjuration(input, new ItemStack(result), mana);
    }

    default void manaConjuration(TagKey<Item> input, ItemLike result, int mana) {
        this.manaConjuration(input, new ItemStack(result), mana);
    }

    default void manaConjuration(Ingredient input, ItemLike result, int mana) {
        this.manaConjuration(input, new ItemStack(result), mana);
    }
    
    default void manaConjuration(ItemLike input, ItemStack result, int mana) {
        this.manaConjuration(Ingredient.of(input), result, mana);
    }

    default void manaConjuration(TagKey<Item> input, ItemStack result, int mana) {
        this.manaConjuration(Ingredient.of(input), result, mana);
    }

    default void manaConjuration(Ingredient input, ItemStack result, int mana) {
        ResourceLocation id = this.provider().loc(result.getItem(), "mana_conjuration");
        this.output().accept(id, new ManaInfusionRecipe(result, input, mana, "", StateIngredients.of(BotaniaBlocks.conjurationCatalyst)), null);
    }
}
