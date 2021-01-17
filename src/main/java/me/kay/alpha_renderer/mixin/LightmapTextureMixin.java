package me.kay.alpha_renderer.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Changes the post-b1.8 lightmapping to more closely resemble alpha.
 * NOTE: The Day-Night cycle is still smoother than alpha because I don't have time to rewrite light updates.
 */
@Mixin(LightmapTextureManager.class)
public abstract class LightmapTextureMixin {

    @Shadow private boolean dirty;
    @Shadow private MinecraftClient client;
    @Shadow private float getBrightness(World world, int i){return 1.0f;}
    @Shadow @Final private NativeImage image;
    @Shadow @Final private NativeImageBackedTexture texture;

    /**
     * Probably no memory leaks (tm)
     * @param delta delta time
     */
    @Inject(at=@At("HEAD"), method = "update(F)V")
    public void update(float delta, CallbackInfo ci)
    {
        if(this.dirty)
        {
            this.client.getProfiler().push("lightTex");
            this.dirty = false; // This stops the normal code from running after, which is nice.
            ClientWorld clientWorld = this.client.world;
            if (clientWorld != null) {
                for(int i = 0; i < 16; i++)
                {
                    for(int j = 0; j < 16; j++)
                    {
                        float skyAngle = clientWorld.getSkyAngle(delta);
                        float f1 = 1.0F - (MathHelper.cos(skyAngle * ((float)Math.PI * 2F)) * 2.0F + 0.2F);
                        f1 = MathHelper.clamp(f1, 0.0F, 1.0F);

                        int sub = (int)(f1 * 11F);
                        int lightVal = Math.max(0, Math.max(i - sub, j));

                        float brightness = this.getBrightness(clientWorld, lightVal) * 0.95F + 0.05F;

                        int brightVal = (int)(brightness * 255);
                        this.image.setPixelColor(j, i, -16777216 | brightVal << 16 | brightVal << 8 | brightVal);
                    }
                }
                this.texture.upload();
                this.client.getProfiler().pop();
            }
        }
    }
}
