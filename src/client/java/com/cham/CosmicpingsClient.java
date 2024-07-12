package com.cham;

import com.cham.Module.Keybind;
import com.cham.Module.Mod;
import com.cham.Module.Render.HudUtil.PingData;
import com.cham.Module.Render.HudUtil.PingHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;

import java.util.*;

public class CosmicpingsClient implements ClientModInitializer {

	public static Map<KeyBinding, Mod> keyMap = new Hashtable<>();

	public static CosmicpingsClient INSTANCE;

	public List<PingData> pingList = new ArrayList<>();

	public static List<UUID> cd = new ArrayList<>();

	public static int timer = 0;
	public static int clientTimer = 0;

	@Override
	public void onInitializeClient() {

		INSTANCE = this;

		Keybind.register();

		HudRenderCallback.EVENT.register(new PingHud());


		ClientTickEvents.END_CLIENT_TICK.register((client -> {
			if(client.player != null) {
				if (cd.contains(client.player.getUuid())) {
					timer++;
					if(timer > 80) {
						cd.remove(client.player.getUuid());
						timer = 0;
					}
				}
			}
		}));

	}


	public static Map<KeyBinding, Mod> getKeyMap() {
		return keyMap;
	}

	public static Mod getModFromBinding(KeyBinding keyBinding) {
		return keyMap.get(keyBinding);
	}


	public static CosmicpingsClient getINSTANCE() {
		return INSTANCE;
	}

	public List<PingData> getPingList() {
		return pingList;
	}

}