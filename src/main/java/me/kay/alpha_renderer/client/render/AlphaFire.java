package me.kay.alpha_renderer.client.render;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class AlphaFire extends CellularTexture{

    private float[] flameHeat;
    private float[] newFlameHeat;
    private int flameHeight;

    private final Random random = new Random();

    public AlphaFire(Identifier ID) {
        super(ID, false);
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
        flameHeight = 20 * (spriteW / 16);
        flameHeat = new float[spriteW * 20];
        newFlameHeat = new float[spriteW * 20];
    }

    public void onShutdown(){
        flameHeat = null;
        newFlameHeat = null;
    }

    @Override
    public void cellTick() {
        for(int px = 0; px < spriteW; px++)
        {
            for (int py = 0; py < flameHeight; py++)
            {
                int oldMultiplier = 18;
                float heat = flameHeat[px + ((py + 1) % flameHeight) * spriteW] * oldMultiplier;
                for(int kx = px -1; kx <= px+1; kx++)
                {
                    for(int ky = py; ky <= py+1; ky++)
                    {
                        if(kx >= 0 && kx < spriteW && ky >= 0 && ky < flameHeight)
                        {
                            heat += flameHeat[kx + ky * spriteW];
                        }
                        oldMultiplier++;
                    }
                }

                newFlameHeat[px + py * spriteW] = heat / ((float)oldMultiplier * 1.06f);
                if(py >= flameHeight - 1) {
                    newFlameHeat[px + py * spriteW] = (float) (Math.random() * Math.random() * Math.random() * 4.0 + Math.random() * 0.1 + 0.2);
                }
            }
        }

        float[] temp = flameHeat;
        flameHeat = newFlameHeat;
        newFlameHeat = flameHeat;

        for(int px = 0; px < spriteW; px++)
        {
            for (int py = 0; py < spriteH; py++)
            {
                float heat = MathHelper.clamp(flameHeat[px + py * spriteW] * 1.8F * (16F / spriteH), 0.0F, 1.0F);
                int r = (int)(heat * 155F + 100F);
                int g = (int)(heat * heat * 255F);
                int b = (int)(heat * heat * heat * heat * heat * heat * heat * heat * heat * heat * 255F);
                int a = heat >= 0.5F ? 255 : 0;
                texture.setPixelColor(px, py, colorToInt(r, g, b, a));
            }
        }
    }
}
