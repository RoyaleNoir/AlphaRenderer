package me.kay.alpha_renderer.mixin;

import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.swing.text.html.BlockView;

@Mixin(BiomeColors.class)
public abstract class BiomeColorsMixin {
    @Inject(at=@At("TAIL"), method="getWaterColor", cancellable = true)
    private static void whiteWater(BlockRenderView world, BlockPos pos, CallbackInfoReturnable<Integer> cir){
        cir.setReturnValue(16777215);
    }
}
