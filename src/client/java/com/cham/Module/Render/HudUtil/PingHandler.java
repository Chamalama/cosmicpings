package com.cham.Module.Render.HudUtil;

import com.cham.CosmicpingsClient;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public class PingHandler {

    @Getter
    public static Map<String, PingData> data = new HashMap<>();
    @Getter
    public static Map<String, PingData> deathData = new HashMap<>();

    public static void onRenderWorld(Matrix4f projectionMatrix, float tickCounter) {
        final MinecraftClient client = MinecraftClient.getInstance();
        if(client.player == null) return;
        updatePings();
        if(client.currentScreen != null) return;
        final ClientWorld world = client.world;
        if(world == null) return;
        for (PingData ping : CosmicpingsClient.getINSTANCE().getPingList()) {
            ping.screenPos = MathHelper.project3Dto2D(ping.pos, RenderSystem.getModelViewMatrix(), projectionMatrix);
        }
        var raycasted = MinecraftClient.getInstance().player.raycast(5000.0, tickCounter, false);
        if (raycasted.getType() == HitResult.Type.BLOCK) {
            final Vec3d cameraPosVec = client.player.getCameraPosVec(tickCounter);
            double distance = cameraPosVec.distanceTo(raycasted.getPos());
            var screenPos = MathHelper.project3Dto2D(raycasted.getPos(), RenderSystem.getModelViewMatrix(), projectionMatrix);
            PingHud.currPingPos = screenPos;
            PingHud.distance = distance;
        } else {
            PingHud.currPingPos = null;
            PingHud.distance = -1;
        }

    }

    public static String worldName(PlayerEntity player) {
        final World world = player.getWorld();
        String worldKey = world.getRegistryKey().getValue().toString().replace("minecraft:", "");
        String worldName;
        if(worldKey.contains("-")) {
            worldKey = worldKey.substring(0, worldKey.lastIndexOf("-")).replace("-", "");
        }
        switch (worldKey.toLowerCase()) {
            case "overworld" -> worldName = "Spawn";
            case "realm_lake" -> worldName = "Lake-Realm";
            case "skyblock" -> worldName = "Skyblock-Island";
            case "adventure_ruins" -> worldName = "Abandoned-Ruins";
            case "adventure_wasteland" -> worldName = "Lost-Wasteland";
            case "adventure_demonic_realm" -> worldName = "Demonic-Realm";
            case "koth_world" -> worldName = "Koth";
            case "adventure_abyss" -> worldName = "Abyss";
            case "outpost_stone" -> worldName = "Stone-Outpost";
            case "outpost_iron" -> worldName = "Iron-Outpost";
            case "outpost_diamond" -> worldName = "Diamond-Outpost";
            case "adventure_ruins_facility" -> worldName = "Chain-Facility";
            case "adventure_wasteland_facility" -> worldName = "Iron-Facility";
            case "adventure_demonic_realm_facility" -> worldName = "Diamond-Facility";
            case "world_lms" -> worldName = "LMS";
            case "world" -> worldName = "Spawn";
            case "cell_plot_citizen" -> worldName = "Citizen-Plots";
            case "cell_plot_merchant" -> worldName = "Merchant-Plots";
            case "cell_plot_king" -> worldName = "King-Plots";
            default -> worldName = null;
        }
        return worldName;
    }

    public static void updatePings() {
        final ClientWorld world = MinecraftClient.getInstance().world;
        if(world == null) return;
        for (PingData ping : CosmicpingsClient.getINSTANCE().getPingList()) {
            ping.setAliveTime(Math.toIntExact(System.currentTimeMillis() - ping.getSpawnTime()));
        }
        CosmicpingsClient.getINSTANCE().getPingList().removeIf(pingData -> pingData.getAliveTime() >= pingData.getDespawnTime());
    }

}
