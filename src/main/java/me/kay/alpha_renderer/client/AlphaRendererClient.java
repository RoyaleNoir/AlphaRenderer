package me.kay.alpha_renderer.client;

import me.kay.alpha_renderer.client.gui.AlphaInventoryScreen;
import me.kay.alpha_renderer.client.render.AlphaFire;
import me.kay.alpha_renderer.client.render.AlphaLava;
import me.kay.alpha_renderer.client.render.AlphaWater;
import me.kay.alpha_renderer.client.render.CellularTextureManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class AlphaRendererClient implements ClientModInitializer {
    public static final AlphaWater WATER_STILL = new AlphaWater(new Identifier("block/water_still"), false);
    public static final AlphaWater WATER_FLOW = new AlphaWater(new Identifier("block/water_flow"), true);
    public static final AlphaLava LAVA_STILL = new AlphaLava(new Identifier("block/lava_still"), false);
    public static final AlphaLava LAVA_FLOW = new AlphaLava(new Identifier("block/lava_flow"), true);
    public static final AlphaFire FIRE_0 = new AlphaFire(new Identifier("block/fire_0"));
    public static final AlphaFire FIRE_1 = new AlphaFire(new Identifier("block/fire_1"));

    private final Identifier listenerId = new Identifier("kay", "fluid_reload_listener");
    @Override
    public void onInitializeClient() {
        KeyBinding binding1 = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.alpha_inventory", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_I, "key.category.alpha"));
        FogHelper.Register();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener()
        {
            @Override
            public Identifier getFabricId()
            {
                return listenerId;
            }

            @Override
            public void apply(ResourceManager resourceManager) {
                CellularTextureManager.OnReload();
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (binding1.wasPressed()) {
                if(client.player != null)
                client.openScreen(new AlphaInventoryScreen(client.player));
            }
        });

        HudRenderCallback.EVENT.register((matrixStack, delta) -> {
            TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
            renderer.drawWithShadow(matrixStack, "Minecraft Alpha v1.0.16_02", 2, 2, 0xffffff);
        });
    }
}
