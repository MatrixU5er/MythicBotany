package mythicbotany.patchouli;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

import javax.annotation.Nonnull;
import java.util.function.UnaryOperator;

/**
 * Patchouli projection of the Mana Infuser platform.
 *
 * <p>Botania's 1.21 TerraPlateComponent no longer reads the template's block
 * parameters and always renders the vanilla Terra Plate platform. Keep this
 * local component parameterized so the lexicon shows the actual Infuser
 * multiblock.</p>
 */
public class ManaInfuserPlateComponent implements ICustomComponent {

    public IVariable plate;
    public IVariable corner;
    public IVariable center;
    public IVariable edge;

    private transient int x;
    private transient int y;
    private transient ItemStack plateBlock;
    private transient ItemStack cornerBlock;
    private transient ItemStack centerBlock;
    private transient ItemStack edgeBlock;

    @Override
    public void build(int componentX, int componentY, int pageNum) {
        this.x = componentX;
        this.y = componentY;
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider registries) {
        this.plateBlock = this.resolveItem(this.plate, lookup, registries);
        this.cornerBlock = this.resolveItem(this.corner, lookup, registries);
        this.centerBlock = this.resolveItem(this.center, lookup, registries);
        this.edgeBlock = this.resolveItem(this.edge, lookup, registries);
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, @Nonnull IComponentRenderContext context, float partialTicks, int mouseX, int mouseY) {
        var pose = graphics.pose();
        pose.pushPose();
        pose.translate(0, 0, -10);

        this.renderItem(graphics, context, mouseX, mouseY, this.x + 13, this.y + 1, this.cornerBlock);
        pose.translate(0, 0, 5);
        this.renderItem(graphics, context, mouseX, mouseY, this.x + 20, this.y + 4, this.edgeBlock);
        this.renderItem(graphics, context, mouseX, mouseY, this.x + 7, this.y + 4, this.edgeBlock);
        pose.translate(0, 0, 5);
        this.renderItem(graphics, context, mouseX, mouseY, this.x + 13, this.y + 8, this.cornerBlock);
        this.renderItem(graphics, context, mouseX, mouseY, this.x + 27, this.y + 8, this.centerBlock);
        this.renderItem(graphics, context, mouseX, mouseY, this.x, this.y + 8, this.cornerBlock);
        pose.translate(0, 0, 5);
        this.renderItem(graphics, context, mouseX, mouseY, this.x + 7, this.y + 12, this.edgeBlock);
        this.renderItem(graphics, context, mouseX, mouseY, this.x + 20, this.y + 12, this.edgeBlock);
        pose.translate(0, 0, 5);
        this.renderItem(graphics, context, mouseX, mouseY, this.x + 14, this.y + 15, this.cornerBlock);
        pose.translate(0, 0, 5);
        this.renderItem(graphics, context, mouseX, mouseY, this.x + 13, this.y, this.plateBlock);

        pose.popPose();
    }

    private void renderItem(GuiGraphics graphics, IComponentRenderContext context, int mouseX, int mouseY, int x, int y, ItemStack stack) {
        context.renderItemStack(graphics, x, y, mouseX, mouseY, stack);
    }

    private ItemStack resolveItem(IVariable variable, UnaryOperator<IVariable> lookup, HolderLookup.Provider registries) {
        return IVariable.wrap(lookup.apply(variable).unwrap(), registries).as(ItemStack.class);
    }
}
