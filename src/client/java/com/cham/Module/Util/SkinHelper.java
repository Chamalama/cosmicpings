package com.cham.Module.Util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SkinHelper {

    public static final Map<String, Identifier> playerSkinCache = new HashMap<>();

    static final MinecraftClient client = MinecraftClient.getInstance();

    public static void cacheData(LivingEntity player) {
        if(player instanceof PlayerEntity p) {
            final SkinTextures skinTextures = client.getSkinProvider().getSkinTextures(p.getGameProfile());
            if(skinTextures == null) return;
            final String name = p.getName().getString();
            if(playerSkinCache.containsKey(name)) {
                if(!skinTextures.texture().equals(playerSkinCache.get(name))) {
                    playerSkinCache.remove(name);
                }
            }
            if (!playerSkinCache.containsKey(name)) {
                playerSkinCache.put(name, skinTextures.texture());
            }
        }
    }

    public static Identifier getPlayerSkin(String name) {
        return playerSkinCache.get(name);
    }

    public static boolean isPlayerCached(String name) {
        return playerSkinCache.containsKey(name);
    }

    public static Identifier getPlayerSkin(AbstractClientPlayerEntity player) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return null;

        return player.getSkinTextures().texture();
    }

}
