package mythicbotany.rune;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mythicbotany.register.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RuneRitualRecipe implements Recipe<RecipeInput> {
    
    private static final Map<ResourceLocation, SpecialRuneInput> specialInputs = new HashMap<>();
    private static final Map<ResourceLocation, SpecialRuneOutput> specialOutputs = new HashMap<>();
    
    public static void registerSpecialInput(SpecialRuneInput action) {
        synchronized (specialInputs) {
            if (specialInputs.containsKey(action.id)) {
                throw new IllegalStateException("Special rune ritual input registered twice: " + action.id);
            }
            specialInputs.put(action.id, action);
        }
    }
    
    public static void registerSpecialOutput(SpecialRuneOutput action) {
        synchronized (specialOutputs) {
            if (specialOutputs.containsKey(action.id)) {
                throw new IllegalStateException("Special rune ritual output registered twice: " + action.id);
            }
            specialOutputs.put(action.id, action);
        }
    }

    private final Ingredient centerRune;
    private final List<RunePosition> runes;
    private final int mana;
    private final int ticks;
    private final List<Ingredient> inputs;
    private final List<ItemStack> outputs;
    @Nullable
    private final SpecialRuneInput specialInput;
    @Nullable
    private final SpecialRuneOutput specialOutput;

    public RuneRitualRecipe(Ingredient centerRune, List<RunePosition> runes, int mana, int ticks, List<Ingredient> inputs, List<ItemStack> outputs, @Nullable SpecialRuneInput specialInput, @Nullable SpecialRuneOutput specialOutput) {
        this.centerRune = centerRune;
        this.runes = ImmutableList.copyOf(runes);
        this.mana = mana;
        this.ticks = ticks;
        this.inputs = ImmutableList.copyOf(inputs);
        this.outputs = ImmutableList.copyOf(outputs);
        this.specialInput = specialInput;
        this.specialOutput = specialOutput;
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return ModRecipes.runeRitual;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public boolean matches(@Nonnull RecipeInput inv, @Nonnull Level level) {
        return false;
    }

    @Nonnull
    public ItemStack getResultItem() {
        if (this.outputs.size() == 1) {
            return this.outputs.get(0);
        }
        return ItemStack.EMPTY;
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

    public Ingredient getCenterRune() {
        return this.centerRune;
    }

    public List<RunePosition> getRunes() {
        return this.runes;
    }
    
    public int getMana() {
        return this.mana;
    }

    public int getTicks() {
        return this.ticks;
    }

    public List<Ingredient> getInputs() {
        return this.inputs;
    }

    public List<ItemStack> getOutputs() {
        return this.outputs;
    }

    @Nullable
    public SpecialRuneInput getSpecialInput() {
        return this.specialInput;
    }

    @Nullable
    public SpecialRuneOutput getSpecialOutput() {
        return this.specialOutput;
    }

    public static class RunePosition {

        public static final Codec<RunePosition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("rune").forGetter(RunePosition::getRune),
                Codec.INT.fieldOf("x").forGetter(RunePosition::getX),
                Codec.INT.fieldOf("z").forGetter(RunePosition::getZ),
                Codec.BOOL.optionalFieldOf("consume", false).forGetter(RunePosition::isConsumed)
        ).apply(instance, RunePosition::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, RunePosition> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, RunePosition::getRune,
                ByteBufCodecs.VAR_INT, RunePosition::getX,
                ByteBufCodecs.VAR_INT, RunePosition::getZ,
                ByteBufCodecs.BOOL, RunePosition::isConsumed,
                RunePosition::new
        );

        private static final int HFLIP = 1;
        private static final int VFLIP = 1 << 1;
        private static final int ROTATE = 1 << 2;
        
        private final Ingredient rune;
        private final int x;
        private final int z;
        private final int[] xcoords;
        private final int[] zcoords;
        private final boolean consume;
        
        public RunePosition(Ingredient rune, int x, int z, boolean consume) {
            this.rune = rune;
            this.x = x;
            this.z = z;
            this.consume = (x == 0 && z == 0) || consume;
            this.xcoords = new int[8];
            this.zcoords = new int[8];
            this.xcoords[0] = x;
            this.zcoords[0] = z;
            this.xcoords[HFLIP] = -x;
            this.zcoords[HFLIP] = z;
            this.xcoords[VFLIP] = x;
            this.zcoords[VFLIP] = -z;
            this.xcoords[HFLIP | VFLIP] = -x;
            this.zcoords[HFLIP | VFLIP] = -z;
            for (int i = 0; i < (HFLIP | VFLIP); i++) {
                this.xcoords[i | ROTATE] = -this.zcoords[i];
                this.zcoords[i | ROTATE] = this.xcoords[i];
            }
        }

        public Ingredient getRune() {
            return this.rune;
        }

        public int getX() {
            return this.x;
        }

        public int getZ() {
            return this.z;
        }

        public boolean isConsumed() {
            return this.consume;
        }

        public int getX(int transformIdx) {
            if (transformIdx < 0 || transformIdx >= 8) {
                return this.getX();
            } else {
                return this.xcoords[transformIdx];
            }
        }

        public int getZ(int transformIdx) {
            if (transformIdx < 0 || transformIdx >= 8) {
                return this.getZ();
            } else {
                return this.zcoords[transformIdx];
            }
        }
    }

    public static class Serializer implements RecipeSerializer<RuneRitualRecipe> {
        
        public static Serializer INSTANCE = new Serializer();
        private static final MapCodec<RuneRitualRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("center").forGetter(recipe -> recipe.centerRune),
                RunePosition.CODEC.listOf().fieldOf("runes").forGetter(recipe -> recipe.runes),
                Codec.INT.optionalFieldOf("mana", 0).forGetter(recipe -> recipe.mana),
                Codec.INT.optionalFieldOf("ticks", 200).forGetter(recipe -> recipe.ticks),
                Ingredient.CODEC_NONEMPTY.listOf().optionalFieldOf("inputs", List.of()).forGetter(recipe -> recipe.inputs),
                ItemStack.STRICT_CODEC.listOf().optionalFieldOf("outputs", List.of()).forGetter(recipe -> recipe.outputs),
                ResourceLocation.CODEC.optionalFieldOf("special_input").forGetter(recipe -> Optional.ofNullable(recipe.specialInput).map(special -> special.id)),
                ResourceLocation.CODEC.optionalFieldOf("special_output").forGetter(recipe -> Optional.ofNullable(recipe.specialOutput).map(special -> special.id))
        ).apply(instance, Serializer::create));
        private static final StreamCodec<RegistryFriendlyByteBuf, RuneRitualRecipe> STREAM_CODEC = StreamCodec.of(
                (buffer, recipe) -> {
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getCenterRune());
                    RunePosition.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, recipe.getRunes());
                    ByteBufCodecs.VAR_INT.encode(buffer, recipe.getMana());
                    ByteBufCodecs.VAR_INT.encode(buffer, recipe.getTicks());
                    Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, recipe.getInputs());
                    ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, recipe.getOutputs());
                    ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC).encode(buffer, Optional.ofNullable(recipe.getSpecialInput()).map(special -> special.id));
                    ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC).encode(buffer, Optional.ofNullable(recipe.getSpecialOutput()).map(special -> special.id));
                },
                buffer -> create(
                        Ingredient.CONTENTS_STREAM_CODEC.decode(buffer),
                        RunePosition.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer),
                        ByteBufCodecs.VAR_INT.decode(buffer),
                        ByteBufCodecs.VAR_INT.decode(buffer),
                        Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer),
                        ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer),
                        ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC).decode(buffer),
                        ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC).decode(buffer)
                )
        );
        
        private Serializer() {
            
        }

        @Override
        public MapCodec<RuneRitualRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RuneRitualRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static RuneRitualRecipe create(Ingredient centerRune, List<RunePosition> runes, int mana, int ticks, List<Ingredient> inputs, List<ItemStack> outputs, Optional<ResourceLocation> specialInputId, Optional<ResourceLocation> specialOutputId) {
            SpecialRuneInput specialInput = null;
            if (specialInputId.isPresent()) {
                specialInput = specialInputs.get(specialInputId.get());
                if (specialInput == null) {
                    throw new IllegalStateException("Unknown special rune input: " + specialInputId.get());
                }
            }
            SpecialRuneOutput specialOutput = null;
            if (specialOutputId.isPresent()) {
                specialOutput = specialOutputs.get(specialOutputId.get());
                if (specialOutput == null) {
                    throw new IllegalStateException("Unknown special rune output: " + specialOutputId.get());
                }
            }
            return new RuneRitualRecipe(centerRune, runes, mana, ticks, inputs, outputs, specialInput, specialOutput);
        }
    }
}
