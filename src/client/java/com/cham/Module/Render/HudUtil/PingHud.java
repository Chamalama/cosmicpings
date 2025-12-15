package com.cham.Module.Render.HudUtil;

import com.cham.CosmicpingsClient;
import com.cham.Module.Keybind;
import com.cham.Module.Render.Ping;
import com.cham.Module.Render.TrucePing;
import com.cham.Module.Util.SkinHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3x2fStack;
import org.joml.Vector4f;

public class PingHud implements HudRenderCallback {

    private static final Identifier PING_STANDARD = Identifier.of("cosmicpings", "textures/ping_standard.png");

    final MinecraftClient client = MinecraftClient.getInstance();

    @Getter
    @Setter
    public static Vector4f currPingPos;
    public static double distance = -1;

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter renderTickCounter) {
        if(client.currentScreen != null || client.player == null) return;
        final Matrix3x2fStack stack = context.getMatrices();
        double uiScale = client.getWindow().getScaleFactor();
        final Vec3d cameraPosVec = client.player.getCameraPosVec(renderTickCounter.getDynamicDeltaTicks());
        int scaleDist = 10;
        if ((Ping.INSTANCE.lastPressed != 0L || TrucePing.INSTANCE.lastPressed != 0L) && System.currentTimeMillis() - Ping.INSTANCE.lastPressed > 100L) {
            stack.pushMatrix();
            int shadowBlack = ColorHelper.getArgb(135, 0, 0, 0);
            if (currPingPos != null) {
                var screenPos = currPingPos;
                Vector4f screenPos2 = screenPosWindowed(screenPos, 16, client.getWindow());

                stack.translate((float) (screenPos2.x / uiScale), (float) (screenPos2.y / uiScale), stack);
                stack.scale((float) (2 / uiScale), (float) (2 / uiScale), stack);
                stack.scale(1.075F, 1.075F, stack);

                if (distance > scaleDist) stack.scale(1.01f, 1.01f, stack);

                stack.scale(1.025F, 1.025F, stack);

                final String distanceText = "" + Formatting.GREEN + Formatting.BOLD +  "Ping";
                int distanceTextWidth = client.textRenderer.getWidth(distanceText);

                stack.translate(-distanceTextWidth / 2f, -1f, stack);
                context.fill(-2, -2, client.textRenderer.getWidth(distanceText) + 1, client.textRenderer.fontHeight, shadowBlack);
                context.drawCenteredTextWithShadow(client.textRenderer, distanceText, 12, -1, Colors.RED);
                stack.translate(distanceTextWidth / 2f, 0, stack);
            }
            stack.popMatrix();
        }

        for (PingData ping : CosmicpingsClient.getINSTANCE().getPingList()) {
            if (ping.screenPos == null) continue;
            stack.pushMatrix();
            int shadowBlack = ColorHelper.getArgb(135, 0, 0, 0);
            double distance = cameraPosVec.distanceTo(ping.pos);
            Vector4f screenPos = screenPosWindowed(ping.screenPos, 16, client.getWindow());
            boolean onScreen = screenPos == ping.screenPos;

            final Identifier id = SkinHelper.getPlayerSkin(ping.senderId);
            if (!onScreen) continue;
            stack.translate((float) (screenPos.x / uiScale), (float) (screenPos.y / uiScale));
            stack.scale((float) (2 / uiScale), (float) (2 / uiScale), stack);
            stack.scale(1.075F, 1.075F, stack);
            if (distance > scaleDist && onScreen) stack.scale(1.01f, 1.01f, stack);

            if (id != null) {
                context.drawTexture(RenderPipelines.GUI_TEXTURED, id, -9, -45,  2, 2, 16, 16,2, 2, 16, 16, ColorHelper.getArgb(190, 255, 255, 255));
            } else {
                context.drawTexture(RenderPipelines.GUI_TEXTURED, PING_STANDARD, -4, -30, 8, 8, 8, 8, 8, 8);
            }

            stack.scale(1.025F, 1.025F, stack);

            String distanceText = Formatting.LIGHT_PURPLE + String.format("%.1fm", distance);
            int distanceTextWidth = client.textRenderer.getWidth(distanceText);

            stack.translate(-distanceTextWidth / 2f, -1f, stack);
            context.fill(-2, -2, client.textRenderer.getWidth(distanceText) + 1, client.textRenderer.fontHeight, shadowBlack);
            context.drawTextWithShadow(client.textRenderer, distanceText, 0, 0, Colors.PURPLE);
            stack.translate(distanceTextWidth / 2f, 0, stack);

            String timeText = Formatting.LIGHT_PURPLE.toString() + (int) (ping.aliveTime / 1000.0) + "s ago";
            int distanceTimeWidth = client.textRenderer.getWidth(timeText);

            stack.translate(-distanceTimeWidth / 2f, -11f, stack);
            context.fill(-2, -2, client.textRenderer.getWidth(timeText) + 1, client.textRenderer.fontHeight, shadowBlack);
            context.drawTextWithShadow(client.textRenderer, timeText, 0, 0, Colors.PURPLE);
            stack.translate(distanceTimeWidth / 2f, 0, stack);

            String nameText = ping.deathPing ? ping.senderName + "'s Death Point" : Formatting.LIGHT_PURPLE + ping.senderName;
            int nameTextWidth = client.textRenderer.getWidth(nameText);

            stack.scale(0.8f, 0.8f, stack);
            if (distance > scaleDist) stack.scale(1.25f, 1.25f);
            stack.translate(-nameTextWidth / 2f, -11f);
            context.fill(-2, -2, client.textRenderer.getWidth(nameText) + 1, client.textRenderer.fontHeight, shadowBlack);
            context.drawTextWithShadow(client.textRenderer, nameText, 0, 0, Colors.PURPLE);
            stack.translate(nameTextWidth / 2f, 0);
            stack.popMatrix();
        }

    }

    public static Vector4f screenPosWindowed(Vector4f screenPos, int margin, Window wnd) {
        Vector4f newScreenPos = screenPos;
        final int width = wnd.getWidth();
        final int height = wnd.getHeight();
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


}

