package mythicbotany.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import mythicbotany.core.FancySkyChecker;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.client.render.world.SkyblockSkyRenderer;

/** Renders Botania's Garden of Glass sky effects in Alfheim. */
@Mixin(value = LevelRenderer.class, priority = 800)
public abstract class LevelRendererMixin {

    @Shadow
    @Nullable
    private VertexBuffer starBuffer;

    @Unique
    private static final Matrix4f MYTHICBOTANY_SUN_SCALE = new Matrix4f().scale(2F, 1F, 2F);

    @Unique
    private static final Matrix4f MYTHICBOTANY_MOON_SCALE = new Matrix4f().scale(1.5F, 1F, 1.5F);

    @Inject(
            method = "renderSky",
            slice = @Slice(from = @At(
                    ordinal = 0,
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"
            )),
            at = @At(
                    shift = At.Shift.AFTER,
                    ordinal = 0,
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V"
            ),
            require = 0
    )
    private void mythicbotany$renderExtras(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick,
            Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) {
        if (FancySkyChecker.isFancySky()) {
            PoseStack poseStack = new PoseStack();
            poseStack.mulPose(frustumMatrix);
            SkyblockSkyRenderer.renderExtra(poseStack, Minecraft.getInstance().level, partialTick, 0);
        }
    }

    @ModifyVariable(
            method = "renderSky",
            slice = @Slice(
                    from = @At(ordinal = 1, value = "INVOKE",
                            target = "Lnet/minecraft/client/multiplayer/ClientLevel;getTimeOfDay(F)F"),
                    to = @At(ordinal = 0, value = "INVOKE",
                            target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V")
            ),
            at = @At(value = "CONSTANT", args = "floatValue=30.0"),
            ordinal = 1,
            require = 0
    )
    private Matrix4f mythicbotany$makeSunBigger(Matrix4f matrix) {
        if (FancySkyChecker.isFancySky()) {
            matrix = new Matrix4f(matrix);
            matrix.mul(MYTHICBOTANY_SUN_SCALE);
        }
        return matrix;
    }

    @ModifyVariable(
            method = "renderSky",
            slice = @Slice(
                    from = @At(ordinal = 0, value = "INVOKE",
                            target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V"),
                    to = @At(ordinal = 1, value = "INVOKE",
                            target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V")
            ),
            at = @At(value = "CONSTANT", args = "floatValue=20.0"),
            ordinal = 1,
            require = 0
    )
    private Matrix4f mythicbotany$makeMoonBigger(Matrix4f matrix) {
        if (FancySkyChecker.isFancySky()) {
            matrix.mul(MYTHICBOTANY_MOON_SCALE);
        }
        return matrix;
    }

    @Inject(
            method = "renderSky",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;getStarBrightness(F)F"),
            require = 0
    )
    private void mythicbotany$renderExtraStars(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick,
            Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) {
        if (FancySkyChecker.isFancySky()) {
            PoseStack poseStack = new PoseStack();
            poseStack.mulPose(frustumMatrix);
            SkyblockSkyRenderer.renderStars(this.starBuffer, poseStack, projectionMatrix, partialTick, skyFogSetup);
        }
    }
}
