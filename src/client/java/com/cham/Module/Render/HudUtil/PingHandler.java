package com.cham.Module.Render.HudUtil;

import com.cham.CosmicpingsClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public class PingHandler {

    public static Map<String, PingData> data = new HashMap<>();

    private static MinecraftClient client = MinecraftClient.getInstance();

    public static void onRenderWorld(MatrixStack stack, Matrix4f projectionMatrix, float tickDelta) {

        updatePings();

        if (client.currentScreen != null) {
            return;
        }

        ClientWorld world = client.world;

        if (world != null) {

            Matrix4f modelViewMatrix = stack.peek().getPositionMatrix();

            for (PingData ping : CosmicpingsClient.getINSTANCE().getPingList()) {
                ping.screenPos = MathHelper.project3Dto2D(ping.pos, modelViewMatrix, projectionMatrix);
            }
        }
    }

    public static String worldName(PlayerEntity player) {

        World world = player.getWorld();
        String worldKey = world.getRegistryKey().getValue().toString().replace("minecraft:", "");
        String worldName;

        if(worldKey.equalsIgnoreCase("overworld")) {
            worldName = "Spawn";
        }else if(worldKey.startsWith("skyblock_world")) {
            worldName = "Skyblock-Island";
        }else if(worldKey.contains("adventure_ruins-0")) {
            worldName = "Abandoned-Ruins";
        }else if(worldKey.contains("adventure_wasteland-0")) {
            worldName = "Lost-Wasteland";
        }else if(worldKey.contains("adventure_demonic_realm-0")) {
            worldName = "Demonic-Realm";
        }else{
            worldName = worldKey;
        }
        return worldName;
    }

    public static Map<String, PingData> getData() {
        return data;
    }


    public static void updatePings() {
        ClientWorld world = client.world;

        if (world != null) {
            for (PingData ping : CosmicpingsClient.getINSTANCE().getPingList()) {
                ping.aliveTime = Math.toIntExact(System.currentTimeMillis() - ping.spawnTime);
            }
            CosmicpingsClient.getINSTANCE().getPingList().removeIf(pingData -> pingData.aliveTime > 30000);
        }
    }

}
