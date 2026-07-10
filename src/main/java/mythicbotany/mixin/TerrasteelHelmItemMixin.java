package mythicbotany.mixin;

import mythicbotany.core.TerraArmorChecker;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.common.item.equipment.armor.terrasteel.TerrasteelHelmItem;

@Mixin(value = TerrasteelHelmItem.class, remap = false)
public abstract class TerrasteelHelmItemMixin {

    @Inject(method = "hasTerraArmorSet", at = @At("HEAD"), cancellable = true)
    private static void mythicbotany$acceptAlfsteelArmor(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (TerraArmorChecker.hasTerraHelmet(player)) {
            cir.setReturnValue(true);
        }
    }
}
