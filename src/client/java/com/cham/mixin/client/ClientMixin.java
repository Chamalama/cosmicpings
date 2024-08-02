package com.cham.mixin.client;

import com.cham.CosmicpingsClient;
import com.cham.Module.Mod;
import com.cham.Module.Util.SkinHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(MinecraftClient.class)
public abstract class ClientMixin {

	@Inject(method = "tick", at = @At("HEAD"))
	private void init(CallbackInfo info) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player != null) {
			if(client.world != null) {
				SkinHelper.cacheData(client.player);
				for (AbstractClientPlayerEntity pe : client.world.getPlayers()) {
					SkinHelper.cacheData(pe);
				}
			}
			for (Mod mod : CosmicpingsClient.keyMap.values()) {

				if(mod.isEnabled()) {
					mod.onUpdate();
				}
				if (mod.getKeycode().wasPressed()) {
					if (mod.isShouldToggle()) {
						mod.toggle();
						client.player.sendMessage(Text.literal(mod.getMessage().replace("Literal", "")));
					}
					if(!mod.isShouldToggle()) {
						mod.onInfo();
					}
					break;
				}
			}
		}
	}
}