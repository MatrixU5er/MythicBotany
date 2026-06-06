package mythicbotany;

import mythicbotany.config.MythicConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.fml.ModList;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class RecipeRemover {

    public static final ResourceLocation GAIA_PYLON = ResourceLocation.fromNamespaceAndPath("botania", "gaia_pylon");
    public static final ResourceLocation HARD_PYLON = MythicBotany.getInstance().resource("gaia_pylon");
    public static final ResourceLocation GAIA_MANA_RING = ResourceLocation.fromNamespaceAndPath("botanicadds", "mana_ring_gaia");

    private RecipeRemover() {

    }

    public static void removeRecipes(RecipeManager rm) {
        Set<ResourceLocation> recipesToRemove = new HashSet<>();
        if (MythicConfig.replaceGaiaRecipe) {
            recipesToRemove.add(GAIA_PYLON);
        } else {
            recipesToRemove.add(HARD_PYLON);
        }
        
        if (ModList.get().isLoaded(GAIA_MANA_RING.getNamespace())) {
            recipesToRemove.add(GAIA_MANA_RING);
        }
        
        rm.replaceRecipes(rm.getRecipes().stream()
                .filter(recipe -> !recipesToRemove.contains(recipe.id()))
                .collect(Collectors.toList()));
    }
}
