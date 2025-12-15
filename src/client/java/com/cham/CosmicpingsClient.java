package com.cham;

import com.cham.Module.Keybind;
import com.cham.Module.Mod;
import com.cham.Module.Render.HudUtil.PingData;
import com.cham.Module.Render.HudUtil.PingHandler;
import com.cham.Module.Render.HudUtil.PingHud;
import com.cham.Module.Util.DirectionalSoundInstance;
import com.cham.Module.Util.SkinHelper;
import com.cham.config.PingConfig;
import com.cham.config.PingConfigStorage;
import com.cham.screen.PingConfigScreen;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Setter
@Getter
public class CosmicpingsClient implements ClientModInitializer {

    @Getter
	public static Map<KeyBinding, Mod> keyMap = new HashMap<>();

	@Getter
    public static CosmicpingsClient INSTANCE;

	private final List<PingData> pingList = new ArrayList<>();
    private String connectedServer;
    private String prisons = "cosmicprisons.com";
    private String sky = "cosmicsky.net";
    private boolean cooldown;
	public int timer = 0;

    private PingConfig pingConfig;
    private PingConfigStorage pingConfigStorage;

	@Override
	public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((clientPlayNetworkHandler, packetSender, minecraftClient) -> {
            if(minecraftClient.player == null || clientPlayNetworkHandler.getServerInfo() == null) return;
            connectedServer = clientPlayNetworkHandler.getServerInfo().address;
        });
		INSTANCE = this;
        pingConfig = new PingConfig();
        pingConfigStorage = new PingConfigStorage();
		Keybind.register();
		HudRenderCallback.EVENT.register(new PingHud());
        runClientTick();
        runMessages();
	}

    private void runClientTick() {
        ClientTickEvents.END_CLIENT_TICK.register((client -> {
            if(client.player == null) return;
            if(cooldown) {
                timer++;
            }
            if(timer >= 80) {
                cooldown = false;
                timer = 0;
            }
            if(client.world == null) return;
            for(Entity entity : client.world.getEntities()) {
                if(!(entity instanceof LivingEntity le) || SkinHelper.isPlayerCached(le.getUuidAsString())) continue;
                SkinHelper.cacheData(le);
            }
            for (Mod mod : CosmicpingsClient.getKeyMap().values()) {
                if(mod.isEnabled()) {
                    mod.onUpdate();
                }
                if (mod.getKeycode().isPressed() && !mod.getKeycode().wasPressed()) {
                    mod.lastPressed = System.currentTimeMillis();
                } else if (mod.lastPressed > 0L && !mod.getKeycode().isPressed()) {
                    if (mod.isShouldToggle()) {
                        mod.toggle();
                        client.player.sendMessage(Text.literal(mod.getMessage().replace("Literal", "")), false);
                    }else{
                        mod.onInfo();
                    }
                    mod.lastPressed = 0L;
                    break;
                }
            }
        }));
        WorldRenderEvents.AFTER_TRANSLUCENT.register((worldRenderContext -> {
            PingHandler.onRenderWorld(worldRenderContext.projectionMatrix(), worldRenderContext.tickCounter().getTickProgress(false));
        }));
    }

    private void runMessages() {
        final MinecraftClient client = MinecraftClient.getInstance();
        ClientReceiveMessageEvents.GAME.register((text, bool) -> {
            final boolean isSky = connectedServer.equalsIgnoreCase(sky);
            final boolean isPrisons = connectedServer.equalsIgnoreCase(prisons);
            if (client.player == null) return;
            final String message = formatMessage(text.getString());
            if(isPrisons && !message.contains("[GC]")) return;
            if(!message.contains("[!]") && !message.contains("has pinged at")) return;
            boolean isAlliance = message.startsWith("ALLIANCE");
            boolean isTruce = message.startsWith("TRUCE");
            boolean isGangChat = message.startsWith("[GC]");
            final String[] formatted = formatPingMessage(message, isPrisons);
            final String actualName = formatted[0];
            final String playerName = formatted[1];
            final String worldName = formatted[2];
            if (message.contains("has pinged")) {
                if (isSky) {
                    if ((isTruce && !pingConfig.isTrucePingEnabled()) || (isAlliance && !pingConfig.isAllyPingEnabled()) || (!isAlliance && !isTruce))
                        return;
                    generatePing(playerName, actualName, worldName);
                }
                if (isPrisons) {
                    if ((isGangChat && !pingConfig.isAllyPingEnabled())) return;
                    if (!isGangChat) return;
                    generatePing(playerName, actualName, worldName);
                }
            }
        });
    }

    private void generatePing(String playerName, String actualName, String worldName) {
        if(MinecraftClient.getInstance().player == null) return;
        final Pattern pattern = Pattern.compile("(-?\\d+)x (-?\\d+)y (-?\\d+)z");
        final Matcher matcher = pattern.matcher(playerName);
        if (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            int z = Integer.parseInt(matcher.group(3));
            final Vec3d pingPos = new Vec3d(x, y, z);
            final String world = PingHandler.worldName(MinecraftClient.getInstance().player);
            PingData data;
            if (playerName.contains("died")) {
                data = new PingData(actualName, actualName, pingPos, new Vector4f(), System.currentTimeMillis(), 180000, true);
                if (PingHandler.getDeathData().containsKey(actualName)) {
                    PingData deathData = PingHandler.getDeathData().get(actualName);
                    CosmicpingsClient.getINSTANCE().getPingList().remove(deathData);
                }
                PingHandler.getDeathData().put(actualName, data);
            } else {
                data = new PingData(actualName, actualName, pingPos, new Vector4f(), System.currentTimeMillis(), 30000, false);
                if (PingHandler.getData().containsKey(actualName)) {
                    final PingData normalData = PingHandler.getData().get(actualName);
                    CosmicpingsClient.getINSTANCE().getPingList().remove(normalData);
                }
                if (!world.equalsIgnoreCase(worldName)) return;
                PingHandler.getData().put(actualName, data);
            }
            playPingSound(MinecraftClient.getInstance(), data, pingConfig);
            CosmicpingsClient.getINSTANCE().getPingList().add(data);
        }
    }

    private static void playPingSound(MinecraftClient client, PingData ping, PingConfig pingConfig) {
        client.getSoundManager().play(
                new DirectionalSoundInstance(
                        SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(),
                        SoundCategory.MASTER,
                        (float) pingConfig.getPingVolume(),
                        0.9f,
                        0,
                        ping.pos
                )
        );
    }

    public boolean isPrisons() {
        return connectedServer.equalsIgnoreCase(prisons);
    }

    public boolean isSky() {
        return connectedServer.equalsIgnoreCase(sky);
    }

    private static String formatMessage(String s) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char currentChar = s.charAt(i);
            if (currentChar == '§' && i + 1 < s.length()) {
                i++;
            } else {
                builder.append(currentChar);
            }
        }
        return builder.toString();
    }

    private String[] formatPingMessage(String message, boolean prisons) {
        if(prisons && message.length() > 5 && message.contains("[GC]")) message = message.substring(5);
        String playerName = message.substring(message.indexOf("]")).replace("]", "").trim();
        String actualName = playerName.substring(0, playerName.indexOf(" "));
        String worldName = playerName.substring(playerName.lastIndexOf("z")).substring(2);
        worldName = worldName.substring(0, worldName.indexOf(" "));
        playerName = playerName.replaceAll("§[0-9a-fA-Fk-oK-OrR]", "");
        return new String[]{actualName, playerName, worldName};
    }


}