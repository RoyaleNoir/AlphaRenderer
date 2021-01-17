package me.kay.alpha_renderer.client.render;

import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.function.Function;

public abstract class CellularTexture extends AbstractTexture implements TextureTickListener {

    private boolean isReplaced;
    protected final boolean isFlowing;

    private int mipLevels;

    private Sprite originalSprite;
    private final Identifier spriteID;
    private final Identifier texID;
    private final Identifier cellID;
    private SpriteAtlasTexture atlasTexture;

    protected int spriteW;
    protected int spriteH;
    protected int spriteU;
    protected int spriteV;

    protected NativeImage texture;

    public CellularTexture(Identifier ID, boolean isFlowing)
    {
        this.isReplaced = false;
        this.spriteID = ID;
        this.texID = new Identifier(spriteID.getNamespace(), spriteID.getPath() + "_auto");
        this.cellID = new Identifier(spriteID.getNamespace(), spriteID.getPath() + "_cell");
        this.isFlowing = isFlowing;

        // If not already registered, allocate sprite
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register(
                (atlasTexture, registry) -> registry.register(spriteID));

        CellularTextureManager.AddTexture(texID, this);
    }

    public abstract void onStartup();
    public abstract void onShutdown();

    public void replaceSprite()
    {
        if(!isReplaced) {
            // Function to get the sprite out of the atlas
            final Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
            // Save for future reference
            this.originalSprite = atlas.apply(spriteID);
            this.atlasTexture = originalSprite.getAtlas();
            // Texture size
            this.spriteW = originalSprite.getWidth();
            this.spriteH = originalSprite.getHeight();
            // Texture position on atlas (idk how I came up with this)
            this.spriteU = (int)(originalSprite.getMinU() * (1.0 / ((originalSprite.getMaxU() - originalSprite.getMinU()) / originalSprite.getWidth())));
            this.spriteV = (int)(originalSprite.getMinV() * (1.0 / ((originalSprite.getMaxV() - originalSprite.getMinV()) / originalSprite.getHeight())));

            if(isFlowing) {
                spriteW /= 2;
                spriteH /= 2;
            }

            // Actually setup texture
            this.mipLevels = MinecraftClient.getInstance().options.mipmapLevels;
            this.texture = new NativeImage(spriteW, spriteH, false);
            texture.fillRect(0, 0, spriteW, spriteH, 0xFF00FFFF);

            MinecraftClient.getInstance().getTextureManager().registerTexture(texID, this);

            isReplaced = true;
            onStartup();
        }
    }

    public void shutdown(){
        if(isReplaced)
        {
            MinecraftClient.getInstance().getTextureManager().destroyTexture(texID);
            texture.close();
            isReplaced = false;
            onShutdown();
        }
    }

    @Override
    public void load(ResourceManager manager) throws IOException {

    }

    public abstract void cellTick();

    @Override
    public void tick() {
        if(isReplaced)
        {
            cellTick();
            atlasTexture.bindTexture();
            for(int i = 0; i <= mipLevels; i++)
            {
                texture.upload(i, spriteU >> i, spriteV >> i, 0, 0, spriteW >> i, spriteH >> i, true, true, true, false);

                if(isFlowing)
                {
                    texture.upload(i, (spriteU >> i), (spriteV >> i) + (spriteH >> i), 0, 0, spriteW >> i, spriteH >> i, true, true, true, false);
                    texture.upload(i, (spriteU >> i) + (spriteW >> i), (spriteV >> i), 0, 0, spriteW >> i, spriteH >> i, true, true, true, false);
                    texture.upload(i, (spriteU >> i) + (spriteW >> i), (spriteV >> i) + (spriteH >> i), 0, 0, spriteW >> i, spriteH >> i, true, true, true, false);
                }
            }
        }
    }
}
