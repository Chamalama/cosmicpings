package com.cham.mixin.client;

import com.cham.CosmicpingsClient;
import com.cham.Module.Render.HudUtil.PingData;
import com.cham.Module.Render.HudUtil.PingHandler;
import com.cham.Module.Util.DirectionalSoundInstance;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.client.util.TextCollector;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ChatMessages.class)
public class ChatMixin {

    private static boolean worked = false;
    private static boolean isAlliance = false;
    private static boolean isTruce = false;

    @Inject(at = @At("HEAD"), method = "method_27536")
    private static void onMethod(TextCollector textCollector, Style style, String message, CallbackInfoReturnable<Optional> cir) {
        if (!worked) {
            worked = true;
            if(style.getColor() != null) {
                if (style.getColor().getName().startsWith("dark_green")) {
                    isAlliance = true;
                }
                if(style.getColor().getName().startsWith("yellow")) {
                    isTruce = true;
                }
            }
            worked = false;
        }
    }


    @Inject(at = @At("HEAD"), method = "getRenderedChatMessage")
    private static void onChat(String message, CallbackInfoReturnable<String> cir) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null) {

            if(message.startsWith("[!]") && (isAlliance || isTruce)) {

                Vector4f pingColor = new Vector4f();
                if(isTruce) {
                    pingColor = new Vector4f(0.0f, 0.9f, 0.9f, 1.0f);
                }else{
                    pingColor = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f);
                }

                isAlliance = false;
                isTruce = false;

                String playerName = message.replace("[!] ", "");

                String actualName = playerName.substring(0, playerName.indexOf(" "));

                String worldName = playerName.substring(playerName.lastIndexOf("z")).substring(2);

                worldName = worldName.substring(0, worldName.indexOf(" "));


                playerName = playerName.replaceAll("§[0-9a-fA-Fk-oK-OrR]", "");
                Pattern pattern = Pattern.compile("(-?\\d+)x (-?\\d+)y (-?\\d+)z");
                Matcher matcher = pattern.matcher(playerName);

                if (matcher.find()) {

                    int x = Integer.parseInt(matcher.group(1));
                    int y = Integer.parseInt(matcher.group(2));
                    int z = Integer.parseInt(matcher.group(3));

                    Vec3d d = new Vec3d(x, y, z);

                    if(PingHandler.getData().containsKey(actualName)) {
                        PingData data = PingHandler.getData().get(actualName);
                        CosmicpingsClient.getINSTANCE().pingList.remove(data);
                    }

                    String world = PingHandler.worldName(client.player);

                    if(!world.equalsIgnoreCase(worldName)) {
                        return;
                    }


                    PingData data = new PingData(actualName, UUID.randomUUID(), d, UUID.randomUUID(), pingColor, System.currentTimeMillis());

                    PingHandler.getData().put(actualName, data);

                    playPingSound(client, data);

                    CosmicpingsClient.getINSTANCE().getPingList().add(data);

                }
            }
        }
    }


    private static void playPingSound(MinecraftClient client, PingData ping) {
        client.getSoundManager().play(
                new DirectionalSoundInstance(
                        SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(),
                        SoundCategory.MASTER,
                        0.8f,
                        0.9f,
                        0,
                        ping.pos
                )
        );
    }
}
