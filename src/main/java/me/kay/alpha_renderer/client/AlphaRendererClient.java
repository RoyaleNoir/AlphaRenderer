package me.kay.alpha_renderer.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AlphaRendererClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FogHelper.Register();
    }
}
