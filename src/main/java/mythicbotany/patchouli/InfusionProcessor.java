package mythicbotany.patchouli;

import mythicbotany.MythicBotany;
import mythicbotany.infuser.InfuserRecipe;
import mythicbotany.register.ModRecipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import vazkii.botania.client.patchouli.processor.PetalApothecaryProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class InfusionProcessor extends PetalApothecaryProcessor {

    protected InfuserRecipe recipe;
    protected ResourceLocation recipeId;

    public InfusionProcessor() {

    }

    @Override
    public void setup(Level level, IVariableProvider variables) {
        var registries = level.registryAccess();
        ResourceLocation id = ResourceLocation.parse(variables.get("recipe", registries).asString());
        this.recipeId = id;
        this.recipe = level.getRecipeManager().getAllRecipesFor(ModRecipes.infuser).stream()
                .filter(holder -> holder.id().equals(id))
                .map(holder -> holder.value())
                .findFirst()
                .orElse(null);
        if (this.recipe == null) {
            MythicBotany.logger.warn("Missing mythicbotany infusion recipe: " + id);
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
                case "mana" -> IVariable.wrap(this.recipe.getManaUsage());
                default -> null;
            };
        }
    }
}
