package me.kay.alpha_renderer.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;

public class FogHelper {
    private static final float[] lightMap;

    private static float lastBrightness = 0.0f;
    private static float currentBrightness = 0.0f;

    public static final int RENDER_DISTANCE = 3;

    static
    {
        lightMap = new float[16];
        float f = 0.05F;
        for(int i = 0; i <= 15; i++)
        {
            float f1 = 1.0F - (float)i / 15F;
            lightMap[i] = ((1.0F - f1) / (f1 * 3F + 1.0F)) * (1.0F - f) + f;
        }
    }

    public static void Register()
    {
        ClientTickEvents.END_WORLD_TICK.register(client -> tick());
    }

    public static int oldLight(int block, int sky, ClientWorld world, float delta)
    {
        float skyAngle = world.getSkyAngle(delta);
        float f1 = 1.0F - (MathHelper.cos(skyAngle * ((float)Math.PI * 2F)) * 2.0F + 0.2F);
        f1 = MathHelper.clamp(f1, 0.0F, 1.0F);

        int sub = (int)(f1 * 11F);
        return Math.max(0, Math.max(sky - sub, block));
    }

    public static void tick() {
        lastBrightness = currentBrightness;
        Entity entity = MinecraftClient.getInstance().cameraEntity;
        ClientWorld world = MinecraftClient.getInstance().world;
        if(entity != null && world != null) {
            int blockBrightness = entity.world.getLightLevel(LightType.BLOCK, entity.getBlockPos());
            int skyBrightness = entity.world.getLightLevel(LightType.SKY, entity.getBlockPos());

            int oldBrightness = oldLight(blockBrightness, skyBrightness, world, 0.0f);

            float newBrightness = lightMap[oldBrightness];
            float lf1 = (float)(3 - RENDER_DISTANCE) / 3F;
            float lf2 = newBrightness * (1.0F - lf1) + lf1;
            currentBrightness += (lf2 - currentBrightness) * 0.1F;
        }
    }

    public static float getBrightness(float delta)
    {
        return MathHelper.lerp(delta, lastBrightness, currentBrightness);
    }
}
