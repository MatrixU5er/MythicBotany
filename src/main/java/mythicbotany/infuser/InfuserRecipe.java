package mythicbotany.infuser;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mythicbotany.register.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class InfuserRecipe implements Recipe<RecipeInput> {
    
    private final ItemStack output;
    private final NonNullList<Ingredient> inputs;
    private final int mana;
    private final int fromColor;
    private final int toColor;

    public InfuserRecipe(ItemStack output, int mana, int fromColor, int toColor, List<Ingredient> inputs) {
        this(output, mana, fromColor, toColor, inputs.toArray(Ingredient[]::new));
    }

    public InfuserRecipe(ItemStack output, int mana, int fromColor, int toColor, Ingredient... inputs) {
        this.output = output;
        this.mana = mana;
        this.fromColor = fromColor;
        this.toColor = toColor;
        this.inputs = NonNullList.of(Ingredient.EMPTY, inputs);
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return ModRecipes.infuser;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public int getManaUsage() {
        return this.mana;
    }

    public int fromColor() {
        return this.fromColor;
    }

    public int toColor() {
        return this.toColor;
    }

    @Override
    public boolean matches(@Nonnull RecipeInput inv, @Nonnull Level level) {
        List<Ingredient> ingredientsMissing = new ArrayList<>(this.inputs);
        IntStream.range(0, inv.size()).boxed().map(inv::getItem).filter(stack -> !stack.isEmpty()).forEach(stack ->
                ingredientsMissing.stream().filter(ingredient -> ingredient.test(stack)).findFirst().ifPresent(ingredientsMissing::remove)
        );
        return ingredientsMissing.isEmpty();
    }

    @Nonnull
    public ItemStack getResultItem() {
        return this.output;
    }
    
    @Nonnull
    @Override
    public ItemStack getResultItem(@Nonnull HolderLookup.Provider registries) {
        return this.getResultItem();
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull RecipeInput container, @Nonnull HolderLookup.Provider registries) {
        return this.getResultItem();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    public ItemStack result(List<ItemStack> inputs) {
        if (inputs.size() != this.inputs.size()) return ItemStack.EMPTY;
        outer: for (Ingredient item : this.inputs) {
            for (ItemStack stack : inputs) {
                if (item.test(stack))
                    continue outer;
            }
            return ItemStack.EMPTY;
        }
        return this.output.copy();
    }

    @Nonnull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.inputs;
    }

    @Nullable
    public static Pair<InfuserRecipe, ItemStack> getOutput(Level level, List<ItemStack> inputs) {
        if (!inputs.isEmpty()) {
            if (inputs.stream().anyMatch(stack -> stack.getCount() != 1)) {
                return null;
            }
            for (var holder : level.getRecipeManager().getAllRecipesFor(ModRecipes.infuser)) {
                InfuserRecipe infuserRecipe = holder.value();
                ItemStack stack = infuserRecipe.result(inputs);
                if (!stack.isEmpty()) return Pair.of(infuserRecipe, stack.copy());
            }
        }
        return null;
    }

    public static class Serializer implements RecipeSerializer<InfuserRecipe> {

        public static Serializer INSTANCE = new Serializer();
        private static final MapCodec<InfuserRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.STRICT_CODEC.fieldOf("output").forGetter(recipe -> recipe.output),
                ExtraCodecs.POSITIVE_INT.fieldOf("mana").forGetter(recipe -> recipe.mana),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("fromColor").forGetter(recipe -> recipe.fromColor),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("toColor").forGetter(recipe -> recipe.toColor),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(recipe -> List.copyOf(recipe.inputs))
        ).apply(instance, InfuserRecipe::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, InfuserRecipe> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, recipe -> recipe.output,
                ByteBufCodecs.VAR_INT, recipe -> recipe.mana,
                ByteBufCodecs.VAR_INT, recipe -> recipe.fromColor,
                ByteBufCodecs.VAR_INT, recipe -> recipe.toColor,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), recipe -> List.copyOf(recipe.inputs),
                InfuserRecipe::new
        );

        private Serializer() {

        }

        @Override
        public MapCodec<InfuserRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, InfuserRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
