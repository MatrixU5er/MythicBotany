package mythicbotany.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mythicbotany.MythicBotany;
import mythicbotany.register.ModBlocks;
import mythicbotany.register.ModRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

@JeiPlugin
public class MythicJei implements IModPlugin {

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath("mythicbotany", "jeiplugin");
    }

    @Override
    public void registerCategories(@Nonnull IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new InfusionCategory(registration.getJeiHelpers().getGuiHelper()),
                new RuneRitualCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(@Nonnull IRecipeRegistration registration) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            MythicBotany.logger.warn("Skipping MythicBotany JEI recipe registration because no client level is available.");
            return;
        }
        RecipeManager recipes = level.getRecipeManager();

        registration.addRecipes(InfusionCategory.TYPE, recipes.getAllRecipesFor(ModRecipes.infuser).stream().map(holder -> holder.value()).collect(Collectors.toList()));
        registration.addRecipes(RuneRitualCategory.TYPE, recipes.getAllRecipesFor(ModRecipes.runeRitual).stream().map(holder -> holder.value()).collect(Collectors.toList()));
    }

    @Override
    public void registerRecipeCatalysts(@Nonnull IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.manaInfuser), InfusionCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.centralRuneHolder), RuneRitualCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.runeHolder), RuneRitualCategory.TYPE);
    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime runtime) {
        LittleBoxItemRenderer.setParent(runtime.getIngredientManager().getIngredientRenderer(VanillaTypes.ITEM_STACK));
    }
}
