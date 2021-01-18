package me.kay.alpha_renderer.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Shadow public float renderYaw;
    @Shadow public float renderPitch;
    @Shadow public float lastRenderYaw;
    @Shadow public float lastRenderPitch;

    @Inject(method = "tickNewAi", at = @At("TAIL"))
    private void removeLag(CallbackInfo ci)
    {
        this.renderYaw = lastRenderYaw + (renderYaw - lastRenderYaw) * 2F;
        this.renderPitch = lastRenderPitch + (renderPitch - lastRenderPitch) * 2F;
    }
}
