package com.cham.Module.Render.HudUtil;

import com.cham.CosmicpingsClient;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;

public class PingHud implements HudRenderCallback {

    private static final Identifier PING_STANDARD = Identifier.of("cosmicpings", "textures/ping_standard.png");

    @Override
    public void onHudRender(DrawContext context, float tickCounter) {

        MinecraftClient client = MinecraftClient.getInstance();
        if(client.currentScreen != null) return;
        MatrixStack stack = context.getMatrices();
        double uiScale = client.getWindow().getScaleFactor();
        Vec3d cameraPosVec = client.player.getCameraPosVec(tickCounter);
        int scaleDist = 10;

        for (PingData ping : CosmicpingsClient.getINSTANCE().pingList) {

            if (ping.screenPos == null) continue;

            stack.push();

            Vector4f pingColor = ping.color;
            int shadowBlack = ColorHelper.Argb.getArgb(135, 0, 0, 0);

            double distance = cameraPosVec.distanceTo(ping.pos);
            Vector4f screenPos = screenPosWindowed(ping.screenPos, 16, client.getWindow());
            //screenPos = ping.interpolatePosition(ping.prevPos, ping.currentPos, tickCounter);
            boolean onScreen = screenPos == ping.screenPos;

            stack.translate(screenPos.x / uiScale, screenPos.y / uiScale, 0); // stack to ping center
            stack.scale((float) (2 / uiScale), (float) (2 / uiScale), 1); // constant scale
            stack.scale(1.25F, 1.25F, 1.25F); // config scale

            // scale if ping is far and onscreen
            if (distance > scaleDist && onScreen) stack.scale(1.01f, 1.01f, 1);

            // draw ping icon
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(pingColor.x, pingColor.y, pingColor.z, pingColor.w);
            context.drawTexture(PING_STANDARD, -4, -2, 0, 0, 8, 8, 8, 8);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

            // skip drawing text if ping not on screen
            // distance text

            if(!onScreen) {
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                stack.pop();
                continue;
            }

            String timeText = (int)(ping.aliveTime / 1000.0) + " seconds ago";
            int distanceTimeWidth = client.textRenderer.getWidth(timeText);

            stack.translate(-distanceTimeWidth / 2f, -6f, 0);
            context.fill(-2, -2, client.textRenderer.getWidth(timeText) + 1, client.textRenderer.fontHeight, shadowBlack);
            context.drawTextWithShadow(client.textRenderer, timeText, 0, 0, -1);
            stack.translate(distanceTimeWidth / 2f, 0, 0);

            String distanceText = String.format("%.1fm", distance);
            int distanceTextWidth = client.textRenderer.getWidth(distanceText);

            stack.translate(-distanceTextWidth / 2f, -12f, 0);
            context.fill(-2, -2, client.textRenderer.getWidth(distanceText) + 1, client.textRenderer.fontHeight, shadowBlack);
            context.drawTextWithShadow(client.textRenderer, distanceText, 0, 0, -1);
            stack.translate(distanceTextWidth / 2f, 0, 0); // recenter x

            // username text

            String nameText = ping.senderName;
            int nameTextWidth = client.textRenderer.getWidth(nameText);

            stack.scale(0.8f, 0.8f, 1f);
            if (distance > scaleDist) stack.scale(1.25f, 1.25f, 1);

            stack.translate(-nameTextWidth / 2f, -14f, 0);
            context.fill(-2, -2, client.textRenderer.getWidth(nameText) + 1, client.textRenderer.fontHeight, shadowBlack);
            context.drawTextWithShadow(client.textRenderer, nameText, 0, 0, -1);
            stack.translate(nameTextWidth / 2f, 0, 0); // recenter x

            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

            stack.pop();
        }

    }

    public static Vector4f screenPosWindowed(Vector4f screenPos, int margin, Window wnd) {
        Vector4f newScreenPos = screenPos;
        int width = wnd.getWidth();
        int height = wnd.getHeight();

        if (newScreenPos.w < 0)
            newScreenPos = new Vector4f(width - newScreenPos.x, height - margin, newScreenPos.z, -newScreenPos.w);
        if (newScreenPos.x > width - margin)
            newScreenPos = new Vector4f(width - margin, newScreenPos.y, newScreenPos.z, newScreenPos.w);
        else if (newScreenPos.x < margin)
            newScreenPos = new Vector4f(margin, newScreenPos.y, newScreenPos.z, newScreenPos.w);
        if (newScreenPos.y > height - margin)
            newScreenPos = new Vector4f(newScreenPos.x, height - margin, newScreenPos.z, newScreenPos.w);
        else if (newScreenPos.y < margin)
            newScreenPos = new Vector4f(newScreenPos.x, margin, newScreenPos.z, newScreenPos.w);

        return newScreenPos;
    }

    private boolean isScreenCenter(Vector4f screenPos, int margin, Window wnd) {
        int width = wnd.getWidth();
        int height = wnd.getHeight();

        boolean isHorizontalCenter = screenPos.x > (width / 2f - margin) && screenPos.x < (width / 2f + margin);
        boolean isVerticalCenter = screenPos.y > (height / 2f - margin) && screenPos.y < (height / 2f + margin);

        return isHorizontalCenter && isVerticalCenter;

    }

}

