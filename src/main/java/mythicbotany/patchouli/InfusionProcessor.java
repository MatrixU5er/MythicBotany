package mythicbotany.patchouli;

import mythicbotany.MythicBotany;
import mythicbotany.infuser.InfuserRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import vazkii.botania.client.patchouli.processor.PetalApothecaryProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class InfusionProcessor extends PetalApothecaryProcessor {

    protected Recipe<?> recipe;
    protected ResourceLocation recipeId;

    public InfusionProcessor() {

    }

    public void setup(IVariableProvider variables) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            MythicBotany.logger.warn("Can't load mythicbotany infusion recipe for Patchouli without a client level.");
            this.recipeId = null;
            this.recipe = null;
            return;
        }
        var registries = level.registryAccess();
        ResourceLocation id = ResourceLocation.parse(variables.get("recipe", registries).asString());
        this.recipeId = id;
        this.recipe = null;
        level.getRecipeManager().byKey(id).ifPresent(recipe -> this.recipe = recipe.value());
        if (this.recipe == null) {
            MythicBotany.logger.warn("Missing mythicbotany infusion recipe: " + id);
        } else if (!(this.recipe instanceof InfuserRecipe)) {
            MythicBotany.logger.warn("Recipe is not a mythicbotany infusion recipe: " + id);
            this.recipe = null;
        }
    }

    @Override
    public IVariable process(Level level, String key) {
        if (this.recipe == null) {
            return null;
        } else {
            return switch (key) {
                case "output" -> IVariable.from(this.recipe.getResultItem(level.registryAccess()), level.registryAccess());
                case "recipe" -> IVariable.wrap(this.recipeId.toString());
                case "heading" -> IVariable.from(this.recipe.getResultItem(level.registryAccess()).getHoverName(), level.registryAccess());
                case "mana" -> IVariable.wrap(((InfuserRecipe) this.recipe).getManaUsage());
                default -> null;
            };
        }
    }
}
