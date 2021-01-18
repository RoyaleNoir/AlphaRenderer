package me.kay.alpha_renderer.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Inject(at = @At("HEAD"), method = "renderOrderedTooltip", cancellable = true)
    private void noMouseTooltips(MatrixStack matrices, List<? extends OrderedText> lines, int x, int y, CallbackInfo ci){
        ci.cancel();
    }
}
