package com.cham.Module.Util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SkinHelper {

    public static final Map<String, Identifier> playerSkinCache = new HashMap<>();

    public static void cacheData(LivingEntity player) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(player instanceof PlayerEntity) {
            Identifier skinTexture = client.getEntityRenderDispatcher().getRenderer(player).getTexture(player);
            if(!playerSkinCache.containsKey(player.getName().getLiteralString())) {
                playerSkinCache.put(player.getName().getLiteralString(), skinTexture);
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

    public static Optional<AbstractClientPlayerEntity> fromUUID(UUID id) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client != null && client.world != null) {
            for (PlayerEntity player : client.world.getPlayers()) {
                if(player.getUuid().equals(id) && player instanceof AbstractClientPlayerEntity) {
                    return Optional.of((AbstractClientPlayerEntity)player);
                }
            }
        }
        return Optional.empty();
    }


}
