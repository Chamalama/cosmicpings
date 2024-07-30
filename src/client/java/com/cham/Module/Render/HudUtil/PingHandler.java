package com.cham.Module.Render.HudUtil;

import com.cham.CosmicpingsClient;
import com.cham.Module.Keybind;
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
    public static Map<String, PingData> deathData = new HashMap<>();

    private static MinecraftClient client = MinecraftClient.getInstance();

    public static void onRenderWorld(MatrixStack stack, Matrix4f projectionMatrix) {

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
        }else if(worldKey.startsWith("realm_lake")) {
            worldName = "Lake-Realm";
        }else if(worldKey.startsWith("skyblock_world")) {
            worldName = "Skyblock-Island";
        }else if(worldKey.contains("adventure_ruins-0")) {
            worldName = "Abandoned-Ruins";
        }else if(worldKey.contains("adventure_wasteland-0")) {
            worldName = "Lost-Wasteland";
        }else if(worldKey.contains("adventure_demonic_realm-0")) {
            worldName = "Demonic-Realm";
        }else if(worldKey.contains("adventure_1_scav-0")) {
            worldName = "Scavenger-1";
        }else if(worldKey.contains("adventure_2_scav-0")) {
            worldName = "Scavenger-2";
        }else if(worldKey.contains("adventure_3_scav-0")) {
            worldName = "Scavenger-3";
        }else if(worldKey.contains("adventure_4_scav-0")) {
            worldName = "Scavenger-4";
        }else if(worldKey.contains("adventure_5_scav-0")) {
            worldName = "Scavenger-5";
        }else if(worldKey.contains("adventure_6_scav-0")) {
            worldName = "Scavenger-6";
        }else{
            worldName = null;
        }
        return worldName;
    }

    public static Map<String, PingData> getData() {
        return data;
    }

    public static Map<String, PingData> getDeathData() {
        return deathData;
    }

    public static void updatePings() {
        ClientWorld world = client.world;

        if (world != null) {

            for (PingData ping : CosmicpingsClient.getINSTANCE().getPingList()) {
                ping.aliveTime = Math.toIntExact(System.currentTimeMillis() - ping.spawnTime);
            }

            CosmicpingsClient.getINSTANCE().getPingList().removeIf(pingData -> pingData.aliveTime >= pingData.despawnTime);
        }
    }

}
