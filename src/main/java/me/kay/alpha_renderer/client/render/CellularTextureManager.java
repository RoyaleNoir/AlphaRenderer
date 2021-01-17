package me.kay.alpha_renderer.client.render;

import com.google.common.collect.Maps;
import net.minecraft.util.Identifier;

import java.util.Map;

public class CellularTextureManager {
    private static final Map<Identifier, CellularTexture> CELLULAR_DICT = Maps.newIdentityHashMap();

    public static void AddTexture(Identifier ID, CellularTexture tex){
        CELLULAR_DICT.put(ID, tex);
        System.out.println("Added cellular texture to dict:" + ID.toString());
    }

    public static void RemoveTexture(Identifier ID)
    {
        CELLULAR_DICT.remove(ID);
    }

    public static void OnReload() {
        for(Map.Entry<Identifier, CellularTexture> entry : CELLULAR_DICT.entrySet())
        {
            entry.getValue().shutdown();
            entry.getValue().replaceSprite();
        }
    }
}
