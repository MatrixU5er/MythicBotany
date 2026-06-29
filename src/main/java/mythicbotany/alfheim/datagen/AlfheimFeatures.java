package mythicbotany.alfheim.datagen;

import mythicbotany.alfheim.worldgen.AlfheimWorldGen;
import mythicbotany.alfheim.worldgen.feature.AbandonedApothecaryConfiguration;
import mythicbotany.alfheim.worldgen.tree.RandomFoliagePlacer;
import mythicbotany.alfheim.worldgen.tree.ShatteredTrunkPlacer;
import mythicbotany.register.ModBlocks;
import mythicbotany.register.ModFeatures;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import org.moddingx.libx.datagen.DatagenContext;
import org.moddingx.libx.datagen.provider.sandbox.FeatureProviderBase;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.item.BotaniaItems;

import java.util.List;

public class AlfheimFeatures extends FeatureProviderBase {
    
    public final Holder<ConfiguredWorldCarver<?>> cave = this.carver((WorldCarver<CarverConfiguration>) (WorldCarver<?>) WorldCarver.CAVE, this.holder(Carvers.CAVE).value().config());
    public final Holder<ConfiguredWorldCarver<?>> canyon = this.carver((WorldCarver<CarverConfiguration>) (WorldCarver<?>) WorldCarver.CANYON, this.holder(Carvers.CANYON).value().config());

    public final Holder<ConfiguredFeature<?, ?>> metamorphicForestStone = this.metamorphicStone(BotaniaBlocks.FUCHSITE);
    public final Holder<ConfiguredFeature<?, ?>> metamorphicMountainStone = this.metamorphicStone(BotaniaBlocks.GNEISS);
    public final Holder<ConfiguredFeature<?, ?>> metamorphicFungalStone = this.metamorphicStone(BotaniaBlocks.MYCELITE);
    public final Holder<ConfiguredFeature<?, ?>> metamorphicSwampStone = this.metamorphicStone(BotaniaBlocks.ROSY_TALC);
    public final Holder<ConfiguredFeature<?, ?>> metamorphicDesertStone = this.metamorphicStone(BotaniaBlocks.SOLITE);
    public final Holder<ConfiguredFeature<?, ?>> metamorphicTaigaStone = this.metamorphicStone(BotaniaBlocks.CATACLASITE);
    public final Holder<ConfiguredFeature<?, ?>> metamorphicMesaStone = this.metamorphicStone(BotaniaBlocks.LUNITE);
    
    public final Holder<ConfiguredFeature<?, ?>> dreamwoodTrees = this.feature(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                    SimpleStateProvider.simple(BotaniaBlocks.DREAMWOOD),
                    new ShatteredTrunkPlacer(7, 4, 0),
                    SimpleStateProvider.simple(ModBlocks.dreamwoodLeaves.defaultBlockState().setValue(BlockStateProperties.PERSISTENT, true)),
                    new RandomFoliagePlacer(UniformInt.of(2, 2), UniformInt.of(0, 0)),
                    new TwoLayersFeatureSize(1, 0, 1)
    ).ignoreVines().build());
    
    public final Holder<ConfiguredFeature<?, ?>> motifFlowers = this.feature(ModFeatures.motifFlowers);
    public final Holder<ConfiguredFeature<?, ?>> alfheimGrass = this.feature((Feature<FeatureConfiguration>) (Feature<?>) Feature.RANDOM_PATCH, this.holder(VegetationFeatures.PATCH_GRASS_JUNGLE).value().config());
    public final Holder<ConfiguredFeature<?, ?>> manaCrystals = this.feature(ModFeatures.manaCrystals);
    public final Holder<ConfiguredFeature<?, ?>> abandonedApothecaries = this.feature(ModFeatures.abandonedApothecaries, new AbandonedApothecaryConfiguration(
            List.of(
                    BotaniaBlocks.PETAL_APOTHECARY.defaultBlockState(),
                    BotaniaBlocks.PETAL_APOTHECARY_FUCHSITE.defaultBlockState(),
                    BotaniaBlocks.PETAL_APOTHECARY_LIVINGROCK.defaultBlockState(),
                    BotaniaBlocks.PETAL_APOTHECARY_GNEISS.defaultBlockState(),
                    BotaniaBlocks.PETAL_APOTHECARY_MYCELITE.defaultBlockState(),
                    BotaniaBlocks.PETAL_APOTHECARY_ROSY_TALC.defaultBlockState(),
                    BotaniaBlocks.PETAL_APOTHECARY_SOLITE.defaultBlockState(),
                    BotaniaBlocks.PETAL_APOTHECARY_CATACLASITE.defaultBlockState(),
                    BotaniaBlocks.PETAL_APOTHECARY_LUNITE.defaultBlockState(),
                    BotaniaBlocks.PETAL_APOTHECARY_MOSSY.defaultBlockState()
            ),
            List.of(
                    BotaniaItems.WHITE_MYSTICAL_PETAL, BotaniaItems.ORANGE_MYSTICAL_PETAL, BotaniaItems.MAGENTA_MYSTICAL_PETAL,
                    BotaniaItems.LIGHT_BLUE_MYSTICAL_PETAL, BotaniaItems.YELLOW_MYSTICAL_PETAL, BotaniaItems.LIME_MYSTICAL_PETAL,
                    BotaniaItems.PINK_MYSTICAL_PETAL, BotaniaItems.GRAY_MYSTICAL_PETAL, BotaniaItems.LIGHT_GRAY_MYSTICAL_PETAL,
                    BotaniaItems.CYAN_MYSTICAL_PETAL, BotaniaItems.PURPLE_MYSTICAL_PETAL, BotaniaItems.BLUE_MYSTICAL_PETAL,
                    BotaniaItems.BROWN_MYSTICAL_PETAL, BotaniaItems.GREEN_MYSTICAL_PETAL, BotaniaItems.RED_MYSTICAL_PETAL,
                    BotaniaItems.BLACK_MYSTICAL_PETAL
            )
    ));
    public final Holder<ConfiguredFeature<?, ?>> elementiumOre = this.feature(Feature.ORE, new OreConfiguration(AlfheimWorldGen.alfheimStone, ModBlocks.elementiumOre.defaultBlockState(), 9));
    public final Holder<ConfiguredFeature<?, ?>> dragonstoneOre = this.feature(Feature.ORE, new OreConfiguration(AlfheimWorldGen.alfheimStone, ModBlocks.dragonstoneOre.defaultBlockState(), 4));
    public final Holder<ConfiguredFeature<?, ?>> goldOre = this.feature(Feature.ORE, new OreConfiguration(AlfheimWorldGen.alfheimStone, ModBlocks.goldOre.defaultBlockState(), 9));
    public final Holder<ConfiguredFeature<?, ?>> wheatFields = this.feature(ModFeatures.wheatFields);
    
    public AlfheimFeatures(DatagenContext ctx) {
        super(ctx);
    }
    
    private Holder<ConfiguredFeature<?, ?>> metamorphicStone(Block block) {
        return this.feature(Feature.ORE, new OreConfiguration(AlfheimWorldGen.livingrock, block.defaultBlockState(), 27));
    }
}
