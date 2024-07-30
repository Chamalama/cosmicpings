package com.cham.Module.Render.HudUtil;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;

import java.util.UUID;

public class PingData {

    public String senderName;
    public String senderId;
    public Vec3d pos;
    public Vector4f screenPos;
    public long spawnTime;
    public long aliveTime;
    public long despawnTime;
    public Vector4f color;
    public boolean deathPing;


    public PingData(String senderName, String senderId, Vec3d pos, Vector4f color, long spawnTime, long despawnTime, boolean deathPing) {
        this.senderName = senderName;
        this.senderId = senderId;
        this.pos = pos;
        this.spawnTime = spawnTime;
        this.color = color;
        this.deathPing = deathPing;
        this.despawnTime = despawnTime;
        this.aliveTime = 0;
    }

}
