package me.kay.alpha_renderer.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "getAttackCooldownProgressPerTick", at=@At("TAIL"), cancellable = true)
    public void oldSpeed(CallbackInfoReturnable<Float> cir)
    {
        cir.setReturnValue(1.0f / 20.0f);
    }
}
