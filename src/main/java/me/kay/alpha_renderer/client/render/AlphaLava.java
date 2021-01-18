package me.kay.alpha_renderer.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class AlphaLava extends CellularTexture{

    private float[] soupHeat;
    private float[] newSoupHeat;
    private float[] potHeat;
    private float[] flameHeat;

    private int offset = 0;

    private final Random random = new Random();

    public AlphaLava(Identifier ID, boolean isFlowing) {
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
        offset %= spriteH * 3;
        for(int px = 0; px < spriteW; px++)
        {
            for (int py = 0; py < spriteH; py++)
            {
                int rowSin = (int) (1.2F * Math.sin(Math.toRadians(22.5 * py)));
                int colSin = (int) (1.2F * Math.sin(Math.toRadians(22.5 * px)));

                float localSoupHeat = 0.0f;
                for (int sx = px - 1; sx <= px + 1; sx++) {
                    for (int sy = py - 1; sy <= py + 1; sy++) {
                        localSoupHeat += soupHeat[Math.floorMod(sy + colSin, spriteH) * spriteW + Math.floorMod(sx + rowSin, spriteW)];
                    }
                }

                float localPotHeat = potHeat[py * spriteW + px]
                        + potHeat[py * spriteW + Math.floorMod(px + 1, spriteW)]
                        + potHeat[Math.floorMod(py + 1, spriteH) * spriteW + px]
                        + potHeat[Math.floorMod(py + 1, spriteH) * spriteW + Math.floorMod(px + 1, spriteW)];

                newSoupHeat[py * spriteW + px] = localSoupHeat / 10F + (localPotHeat / 4.0F) * 0.8F;

                potHeat[py * spriteW + px] += flameHeat[py * spriteW + px] * 0.01;
                if (potHeat[py * spriteW + px] < 0) potHeat[py * spriteW + px] = 0;

                flameHeat[py * spriteW + px] -= 0.06f;
                if (random.nextFloat() <= 0.005) flameHeat[py * spriteW + px] = 1.5F;

            }
        }

        float[] temp = soupHeat;
        soupHeat = newSoupHeat;
        newSoupHeat = soupHeat;

        for(int px = 0; px < spriteW; px++)
        {
            for (int py = 0; py < spriteH; py++)
            {
                int sy = isFlowing ? Math.floorMod(py - offset / 3, spriteH) : py;
                float col = MathHelper.clamp(2.0F * soupHeat[sy * spriteW + px], 0.0F, 1.0F);
                float col2 = col * col;
                float col4 = col2 * col2;
                texture.setPixelColor(px, py, colorToInt(col * 100 + 155, col2 * 255, col4 * 128, 255));
            }
        }
    }
}
