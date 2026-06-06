package mythicbotany.functionalflora;

import com.google.common.collect.ImmutableSet;
import mythicbotany.functionalflora.base.FunctionalFlowerBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import org.moddingx.libx.LibX;
import vazkii.botania.api.block.PetalApothecary;
import vazkii.botania.api.block_entity.RadiusDescriptor;
import vazkii.botania.client.fx.WispParticleData;

import javax.annotation.Nullable;
import java.util.Set;

public class Aquapanthus extends FunctionalFlowerBase {

    public static final int MAX_TICK_TO_NEXT_CHECK = 5;
    public static final int MANA_PER_TICK = 2;
    public static final int TICKS_TO_FILL = 20;
    public static final Set<ResourceLocation> FILLING_SLOW_IDS = ImmutableSet.of(
            ResourceLocation.fromNamespaceAndPath("exnihilosequentia", "barrel_wood"),
            ResourceLocation.fromNamespaceAndPath("exnihilosequentia", "barrel_stone"),
            ResourceLocation.fromNamespaceAndPath("excompressum", "oak_crucible"),
            ResourceLocation.fromNamespaceAndPath("excompressum", "spruce_crucible"),
            ResourceLocation.fromNamespaceAndPath("excompressum", "birch_crucible"),
            ResourceLocation.fromNamespaceAndPath("excompressum", "jungle_crucible"),
            ResourceLocation.fromNamespaceAndPath("excompressum", "acacia_crucible"),
            ResourceLocation.fromNamespaceAndPath("excompressum", "dark_oak_crucible")
    );
    public static final Set<ResourceLocation> FILLING_FAST_IDS = ImmutableSet.of(
            ResourceLocation.fromNamespaceAndPath("exnihilosequentia", "crucible_wood"),
            ResourceLocation.fromNamespaceAndPath("exnihilosequentia", "crucible_fired")
    );

    private transient int tickToNextCheck = 0;
    @Nullable
    private BlockPos currentlyFilling = null;
    private int fillingSince = 0;

    public Aquapanthus(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 0x4444FF, false);
    }

    @Override
    protected void tickFlower() {
        //noinspection ConstantConditions
        if (!this.level.isClientSide) {
            if (this.currentlyFilling != null) {
                if (this.mana >= MANA_PER_TICK) {
                    if (this.fill()) {
                        this.mana = Mth.clamp(this.mana - MANA_PER_TICK, 0, this.maxMana);
                        this.didWork = true;
                        this.fillingSince += 1;
                    } else {
                        this.fillingSince = 0;
                        this.currentlyFilling = null;
                    }
                }
                if (this.level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    LibX.getNetwork().updateBE(serverLevel, this.worldPosition);
                }
                this.setChanged();
            } else {
                if (this.tickToNextCheck > 0) {
                    this.tickToNextCheck -= 1;
                    return;
                }
                this.tickToNextCheck = MAX_TICK_TO_NEXT_CHECK;

                BlockPos basePos = this.worldPosition.immutable();
                outer: for (int xd = -3; xd <= 3; xd++) {
                    for (int zd = -3; zd <= 3; zd++) {
                        BlockPos pos = basePos.offset(xd, 0, zd);
                        BlockState state = this.level.getBlockState(pos);
                        BlockEntity te = this.level.getBlockEntity(pos);
                        if (this.canFill(state, te)) {
                            this.currentlyFilling = pos;
                            this.fillingSince = 0;
                            this.setChanged();
                            break outer;
                        }
                    }
                }
            }
        } else {
            if (this.currentlyFilling != null && this.fillingSince > 0) {
                double progress = this.fillingSince / (double) TICKS_TO_FILL;

                double x = ((this.currentlyFilling.getX() - this.worldPosition.getX()) * progress) + this.worldPosition.getX() + 0.5;
                double y = this.worldPosition.getY() + (1.5 * Math.sin(progress * Math.PI));
                double z = ((this.currentlyFilling.getZ() - this.worldPosition.getZ()) * progress) + this.worldPosition.getZ() + 0.5;

                double xd = ((this.currentlyFilling.getX() - this.worldPosition.getX()) * progress) / 10;
                double yd = Math.sin(progress * Math.PI) / 10;
                double zd = ((this.currentlyFilling.getZ() - this.worldPosition.getZ()) * progress) / 10;

                WispParticleData data = WispParticleData.wisp(0.85F, 0.1f, 0.1f, 1, 0.25F);
                this.level.addParticle(data, x, y, z, xd, yd, zd);
                data = WispParticleData.wisp((float) Math.random() * 0.1F + 0.1F, 0.2f, 0.2f, 1, 0.9F);
                this.level.addParticle(data, x, y, z, (float) (Math.random() - 0.5) * 0.05F, (float) (Math.random() - 0.5) * 0.05F, (float) (Math.random() - 0.5) * 0.05F);
            }
        }
    }

    private boolean canFill(BlockState state, @Nullable BlockEntity te) {
        if (state.getBlock() == Blocks.CAULDRON || (state.getBlock() == Blocks.WATER_CAULDRON && state.getValue(LayeredCauldronBlock.LEVEL) < 3)) {
            return true;
        } else if (te instanceof PetalApothecary && ((PetalApothecary) te).getFluid() == PetalApothecary.State.EMPTY) {
            return true;
        } else if ((FILLING_SLOW_IDS.contains(BuiltInRegistries.BLOCK.getKey(state.getBlock())) || FILLING_FAST_IDS.contains(BuiltInRegistries.BLOCK.getKey(state.getBlock()))) && te != null) {
            //noinspection ConstantConditions
            IFluidHandler handler = this.level.getCapability(Capabilities.FluidHandler.BLOCK, te.getBlockPos(), state, te, Direction.UP);
            if (handler != null) {
                int filled;
                if (FILLING_FAST_IDS.contains(BuiltInRegistries.BLOCK.getKey(state.getBlock()))) {
                    filled = handler.fill(new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE);
                } else {
                    filled = handler.fill(new FluidStack(Fluids.WATER, (FluidType.BUCKET_VOLUME / 3) + 1), IFluidHandler.FluidAction.SIMULATE);
                }
                // extra check for capacity is required as excompressum seems to accept more fluid even if full
                return filled > 0 && handler.getFluidInTank(0).getAmount() < handler.getTankCapacity(0);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    private boolean fill() {
        //noinspection ConstantConditions
        BlockState state = this.level.getBlockState(this.currentlyFilling);
        BlockEntity be = this.level.getBlockEntity(this.currentlyFilling);
        if (state.getBlock() == Blocks.CAULDRON || (state.getBlock() == Blocks.WATER_CAULDRON && state.getValue(LayeredCauldronBlock.LEVEL) < 3) || (be instanceof PetalApothecary && ((PetalApothecary) be).getFluid() == PetalApothecary.State.EMPTY)) {
            if (this.fillingSince >= TICKS_TO_FILL) {
                if (state.getBlock() == Blocks.CAULDRON) {
                    this.level.setBlockAndUpdate(this.currentlyFilling, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 1));
                } else if (state.getBlock() == Blocks.WATER_CAULDRON) {
                    this.level.setBlockAndUpdate(this.currentlyFilling, state.setValue(LayeredCauldronBlock.LEVEL, Mth.clamp(state.getValue(LayeredCauldronBlock.LEVEL) + 1, 0, 3)));
                } else if (be instanceof PetalApothecary) {
                    ((PetalApothecary) be).setFluid(PetalApothecary.State.WATER);
                    be.setChanged();
                }
                return false;
            }
            return true;
        } else if ((FILLING_SLOW_IDS.contains(BuiltInRegistries.BLOCK.getKey(state.getBlock())) || FILLING_FAST_IDS.contains(BuiltInRegistries.BLOCK.getKey(state.getBlock()))) && be != null) {
            if (this.fillingSince >= TICKS_TO_FILL) {
                //noinspection ConstantConditions
                IFluidHandler handler = this.level.getCapability(Capabilities.FluidHandler.BLOCK, this.currentlyFilling, state, be, Direction.UP);
                if (handler != null) {
                    if (FILLING_FAST_IDS.contains(BuiltInRegistries.BLOCK.getKey(state.getBlock()))) {
                        handler.fill(new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
                    } else {
                        handler.fill(new FluidStack(Fluids.WATER, (FluidType.BUCKET_VOLUME / 3) + 1), IFluidHandler.FluidAction.EXECUTE);
                    }
                    be.setChanged();
                }
                return false;
            } else {
                return this.canFill(state, be);
            }
        } else {
            return false;
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public RadiusDescriptor getRadius() {
        return RadiusDescriptor.Rectangle.square(this.worldPosition, 3);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        if (nbt.contains("waterFilling")) {
            CompoundTag fillingTag = nbt.getCompound("waterFilling");
            this.currentlyFilling = new BlockPos(fillingTag.getInt("x"), fillingTag.getInt("y"), fillingTag.getInt("z"));
        } else {
            this.currentlyFilling = null;
        }
        this.fillingSince = nbt.getInt("filling_since");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        if (this.currentlyFilling != null) {
            CompoundTag fillingTag = new CompoundTag();
            fillingTag.putInt("x", this.currentlyFilling.getX());
            fillingTag.putInt("y", this.currentlyFilling.getY());
            fillingTag.putInt("z", this.currentlyFilling.getZ());
            nbt.put("waterFilling", fillingTag);
            nbt.putInt("filling_since", this.fillingSince);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag updateTag = super.getUpdateTag(registries);
        //noinspection ConstantConditions
        if (!this.level.isClientSide) {
            if (this.currentlyFilling != null) {
                CompoundTag fillingTag = new CompoundTag();
                fillingTag.putInt("x", this.currentlyFilling.getX());
                fillingTag.putInt("y", this.currentlyFilling.getY());
                fillingTag.putInt("z", this.currentlyFilling.getZ());
                updateTag.put("waterFilling", fillingTag);
            }
            updateTag.putInt("filling_since", this.fillingSince);
        }
        return updateTag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        //noinspection ConstantConditions
        if (this.level.isClientSide) {
            if (tag.contains("waterFilling")) {
                CompoundTag fillingTag = tag.getCompound("waterFilling");
                this.currentlyFilling = new BlockPos(fillingTag.getInt("x"), fillingTag.getInt("y"), fillingTag.getInt("z"));
            } else {
                this.currentlyFilling = null;
            }
            this.fillingSince = tag.getInt("filling_since");
        }
        super.handleUpdateTag(tag, registries);
    }
}
