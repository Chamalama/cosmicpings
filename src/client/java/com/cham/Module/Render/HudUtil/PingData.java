package com.cham.Module.Render.HudUtil;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;

public class PingData {

    public String senderName;
    public String senderId;
    public Vec3d pos;
    public Vector4f screenPos;
    @Getter
    public long spawnTime;
    @Setter
    @Getter
    public long aliveTime;
    @Getter
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
