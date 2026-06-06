package mythicbotany.rune;

import mythicbotany.MythicPlayerData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.moddingx.libx.mod.ModX;
import vazkii.botania.common.item.WandOfTheForestItem;

import javax.annotation.Nonnull;

public class BlockCentralRuneHolder extends BlockRuneHolder<TileCentralRuneHolder> {

    public BlockCentralRuneHolder(ModX mod, Properties properties) {
        this(mod, properties, new Item.Properties());
    }

    public BlockCentralRuneHolder(ModX mod, Properties properties, Item.Properties itemProperties) {
        super(mod, TileCentralRuneHolder.class, properties, itemProperties);
    }

    @Nonnull
    @Override
    protected ItemInteractionResult useItemOn(@Nonnull ItemStack held, @Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        if (held.getItem() instanceof WandOfTheForestItem) {
            if (!level.isClientSide) {
                if (!MythicPlayerData.getData(player).getBoolean("MimirKnowledge")) {
                    player.sendSystemMessage(Component.translatable("message.mythicbotany.mimir_unknown").withStyle(ChatFormatting.GRAY));
                } else {
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof TileCentralRuneHolder tile) {
                        tile.tryStartRitual(player);
                    }
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return super.useItemOn(held, state, level, pos, player, hand, hit);
        }
    }

    @Override
    public void onRemove(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileCentralRuneHolder) {
            ((TileCentralRuneHolder) te).cancelRecipe();
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
