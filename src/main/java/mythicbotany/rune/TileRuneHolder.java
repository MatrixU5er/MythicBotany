package mythicbotany.rune;

import mythicbotany.register.tags.ModItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.moddingx.libx.base.tile.BlockEntityBase;
import org.moddingx.libx.inventory.BaseItemStackHandler;

import javax.annotation.Nullable;

public class TileRuneHolder extends BlockEntityBase {

    private final BaseItemStackHandler inventory = BaseItemStackHandler.builder(1)
            .contentsChanged(() -> {
                this.setChanged();
                this.setDispatchable();
            })
            .validator(stack -> stack.is(ModItemTags.RITUAL_RUNES), 1)
            .defaultSlotLimit(1)
            .build();
            
    @Nullable
    private BlockPos target;
    private double floatProgress;
    
    public TileRuneHolder(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        this.inventory.deserializeNBT(registries, nbt.getCompound("Inventory"));
        this.target = NbtUtils.readBlockPos(nbt, "TargetPos").orElse(null);
        this.floatProgress = nbt.getDouble("FloatProgress");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        nbt.put("Inventory", this.inventory.serializeNBT(registries));
        if (this.target != null) {
            nbt.put("TargetPos", NbtUtils.writeBlockPos(this.target));
            nbt.putDouble("FloatProgress", this.floatProgress);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag nbt = super.getUpdateTag(registries);
        //noinspection ConstantConditions
        if (!this.level.isClientSide) {
            nbt.put("Inventory", this.inventory.serializeNBT(registries));
            if (this.target != null) {
                nbt.put("TargetPos", NbtUtils.writeBlockPos(this.target));
                nbt.putDouble("FloatProgress", this.floatProgress);
            }
        }
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt, HolderLookup.Provider registries) {
        super.handleUpdateTag(nbt, registries);
        //noinspection ConstantConditions
        if (this.level.isClientSide) {
            this.inventory.deserializeNBT(registries, nbt.getCompound("Inventory"));
            this.target = NbtUtils.readBlockPos(nbt, "TargetPos").orElse(null);
            this.floatProgress = nbt.getDouble("FloatProgress");
        }
    }

    @Nullable
    public BlockPos getTarget() {
        return this.target;
    }

    public double getFloatProgress() {
        return this.floatProgress;
    }

    public void setTarget(@Nullable BlockPos target, double floatProgress, boolean sync) {
        this.target = target;
        if (target != null) {
            this.floatProgress = Mth.clamp(floatProgress, 0, 1);
        } else {
            this.floatProgress = 0;
        }
        this.setChanged();
        if (sync) {
            this.setDispatchable();
        }
    }

    public AABB getRenderBoundingBox() {
        AABB aabb = new AABB(this.worldPosition).inflate(1);
        if (this.target != null) {
            // If the rune is floating to a target, we need to expand the render
            // aabb to include that target or runes will sometimes not render.
            return aabb.expandTowards(this.target.getX() - this.worldPosition.getX(), 0, this.target.getZ() - this.worldPosition.getZ()).inflate(1);
        } else {
            return aabb;
        }
    }
}
