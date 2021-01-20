package me.kay.alpha_renderer.mixin;

import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Inject(method = "Lnet/minecraft/entity/ItemEntity;canMerge()Z", at = @At("TAIL"), cancellable = true)
    private void stack(CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue(false);
    }
    @Inject(method = "Lnet/minecraft/entity/ItemEntity;canMerge(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z", at = @At("TAIL"), cancellable = true)
    private static void stack2(ItemStack stack1, ItemStack stack2, CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue(false);
    }
}
