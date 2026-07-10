package mythicbotany.mixin;

import mythicbotany.core.NoHudBackground;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.client.core.helper.RenderHelper;

@Mixin(value = RenderHelper.class, remap = false)
public abstract class RenderHelperMixin {

    @Inject(method = "renderHUDBox", at = @At("HEAD"), cancellable = true)
    private static void mythicbotany$honorHudBackgroundConfig(GuiGraphics graphics, int startX, int startY,
            int endX, int endY, CallbackInfo ci) {
        if (!NoHudBackground.shouldRenderHudBackground()) {
            ci.cancel();
        }
    }
}
