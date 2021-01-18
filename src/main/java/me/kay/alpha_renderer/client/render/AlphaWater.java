package me.kay.alpha_renderer.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class AlphaWater extends CellularTexture{

    private float[] soupHeat;
    private float[] newSoupHeat;
    private float[] potHeat;
    private float[] flameHeat;

    private int offset = 0;

    private final Random random = new Random();

    public AlphaWater(Identifier ID, boolean isFlowing) {
        super(ID, isFlowing);
    }

    private int colorToInt(float r, float g, float b, float a)
    {
        int col = ((int)a) << (3 * 8);
        col += ((int)b) << (2 * 8);
        col += ((int)g) << (8);
        col += ((int)r);
        return col;
    }

    public void onStartup(){
        soupHeat = new float[spriteW * spriteH];
        newSoupHeat = new float[spriteW * spriteH];
        potHeat = new float[spriteW * spriteH];
        flameHeat = new float[spriteW * spriteH];
    }

    public void onShutdown(){
        soupHeat = null;
        newSoupHeat = null;
        potHeat = null;
        flameHeat = null;
    }

    @Override
    public void cellTick() {
        offset++;
        offset %= spriteH;
        for(int px = 0; px < spriteW; px++)
        {
            for (int py = 0; py < spriteH; py++)
            {
                float localSoupHeat = 0.0F;
                int start = isFlowing ? py - 2 : px - 1;
                int end = isFlowing ? py : px + 1;
                for (int sp = start; sp <= end; sp++)
                {
                    int sx = Math.floorMod(isFlowing ? px : sp, spriteW);
                    int sy = Math.floorMod(isFlowing ? sp : py, spriteH);

                    localSoupHeat += soupHeat[sy * spriteW + sx];
                }
                float div = isFlowing ? 3.2F : 3.3F;
                newSoupHeat[py * spriteW + px] = localSoupHeat / div + potHeat[py * spriteW + px] * 0.8F;

            }
        }
        for(int px = 0; px < spriteW; px++)
        {
            for (int py = 0; py < spriteH; py++)
            {

                potHeat[py * spriteW + px] += flameHeat[py * spriteW + px] * 0.05;
                if (potHeat[py * spriteW + px] < 0) potHeat[py * spriteW + px] = 0;

                float sub = isFlowing ? 0.3F : 0.1F;
                flameHeat[py * spriteW + px] -= sub;

                float chance = isFlowing ? 0.2F : 0.05F;
                if(random.nextFloat() <= chance) flameHeat[py * spriteW + px] = 0.5F;

            }
        }

        float[] temp = soupHeat;
        soupHeat = newSoupHeat;
        newSoupHeat = soupHeat;

        for(int px = 0; px < spriteW; px++)
        {
            for (int py = 0; py < spriteH; py++)
            {
                int sy = isFlowing ? Math.floorMod(py - offset, spriteH) : py;
                float col = MathHelper.clamp(soupHeat[sy * spriteW + px], 0.0F, 1.0F);
                col *= col;
                texture.setPixelColor(px, py, colorToInt(32 + col * 32, 50 + col * 64, 255, 146 + col * 50));
            }
        }
    }
}
