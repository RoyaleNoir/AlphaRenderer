package me.kay.alpha_renderer.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.kay.alpha_renderer.client.FogHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRendererMixin {
    @Shadow private static float red;
    @Shadow private static float green;
    @Shadow private static float blue;

    private static Vector3f GetColorOld(float tickDelta, ClientWorld world, long color){
        float skyAngle = world.getSkyAngle(tickDelta);
        float multiplier = MathHelper.clamp(MathHelper.cos(skyAngle * ((float)Math.PI * 2F)) * 2.0F + 0.5F, 0.0f, 1.0f);
        float f3 = (float)(color >> 16 & 255L) / 255F;
        float f4 = (float)(color >> 8 & 255L) / 255F;
        float f5 = (float)(color & 255L) / 255F;
        f3 *= multiplier;
        f4 *= multiplier;
        f5 *= multiplier;
        // System.out.println(multiplier);
        return new Vector3f(f3, f4, f5);
    }

    @Inject(at=@At("HEAD"), cancellable = true, method= "render(Lnet/minecraft/client/render/Camera;FLnet/minecraft/client/world/ClientWorld;IF)V")
    private static void OldFogRender(Camera camera, float tickDelta, ClientWorld world, int i, float f, CallbackInfo ci)
    {
        Entity entity = camera.getFocusedEntity();

        float lightMul = FogHelper.getBrightness(tickDelta);

        float f1 = 1.0F / (float)(4 - FogHelper.RENDER_DISTANCE);
        f1 = 1.0F - (float)Math.pow(f1, 0.25D);
        Vector3f skyColor = GetColorOld(tickDelta, world, 0x88bbffL);
        Vector3f fogColor = GetColorOld(tickDelta, world, 0xc0d8ffL);

        red = MathHelper.lerp(f1, fogColor.getX(), skyColor.getX());
        green = MathHelper.lerp(f1, fogColor.getY(), skyColor.getY());
        blue = MathHelper.lerp(f1, fogColor.getZ(), skyColor.getZ());

        FluidState fluidState = camera.getSubmergedFluidState();
        if (fluidState.isIn(FluidTags.WATER)) {
            red = 0.02f;
            green = 0.02f;
            blue = 0.2f;
        } else if (fluidState.isIn(FluidTags.LAVA)) {
            red = 0.6f;
            green = 0.1f;
            blue = 0.0f;
        }

        red *= lightMul;
        green *= lightMul;
        blue *= lightMul;

        RenderSystem.clearColor(red, green, blue, 0.0F);
        ci.cancel();
    }

    @Inject(at=@At("HEAD"), cancellable = true, method= "applyFog(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/BackgroundRenderer$FogType;FZ)V")
    private static void OldFogSettings(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo ci){
        FluidState fluidState = camera.getSubmergedFluidState();
        Entity entity = camera.getFocusedEntity();

        int alphafog = 256 >> FogHelper.RENDER_DISTANCE; // Tiny

        if (fluidState.isIn(FluidTags.WATER)) {
            // glFogi(GL_FOG_MODE, GL_EXP);
            RenderSystem.fogMode(GlStateManager.FogMode.EXP);
            // glFogf(GL_FOG_DENSITY, 1.0F);
            RenderSystem.fogDensity(1.0F);
            // Set Color to 0.4, 0.4, 0.9
        } else if (fluidState.isIn(FluidTags.LAVA)) {
            // glFogi(GL_FOG_MODE, GL_EXP);
            RenderSystem.fogMode(GlStateManager.FogMode.EXP);
            // glFogf(GL_FOG_DENSITY, 1.0F);
            RenderSystem.fogDensity(2.0F);
            // Set Color to 0.4, 0.3, 0.3
        } else {
            RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
            if (fogType == BackgroundRenderer.FogType.FOG_SKY)
            {
                RenderSystem.fogStart(0.0f);
                RenderSystem.fogEnd(alphafog * 0.8f);
            } else {
                RenderSystem.fogStart(alphafog * 0.25f);
                RenderSystem.fogEnd(alphafog);
            }
            // TODO: Enable once the Herobrine video is finished
            RenderSystem.setupNvFogDistance();
        }

        ci.cancel();
    }
}
