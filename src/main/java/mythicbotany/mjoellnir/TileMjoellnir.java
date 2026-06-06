package mythicbotany.mjoellnir;

import mythicbotany.register.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.moddingx.libx.base.tile.BlockEntityBase;

import javax.annotation.Nonnull;

public class TileMjoellnir extends BlockEntityBase {

    private ItemStack stack;
    
    public TileMjoellnir(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.stack = new ItemStack(ModBlocks.mjoellnir);
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
        this.setChanged();
        this.setDispatchable();
    }

    @Override
    protected void loadAdditional(@Nonnull CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        if (nbt.contains("hammer", Tag.TAG_COMPOUND)) {
            this.stack = ItemStack.parseOptional(registries, nbt.getCompound("hammer"));
        } else {
            this.stack = new ItemStack(ModBlocks.mjoellnir);
        }
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        nbt.put("hammer", this.stack.save(registries, new CompoundTag()));
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag nbt = super.getUpdateTag(registries);
        if (this.level != null && !this.level.isClientSide) {
            nbt.put("hammer", this.stack.save(registries, new CompoundTag()));
        }
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt, HolderLookup.Provider registries) {
        super.handleUpdateTag(nbt, registries);
        if (nbt.contains("hammer", Tag.TAG_COMPOUND)) {
            this.stack = ItemStack.parseOptional(registries, nbt.getCompound("hammer"));
        } else {
            this.stack = new ItemStack(ModBlocks.mjoellnir);
        }
    }
}
