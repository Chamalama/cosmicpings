package com.cham.Module.Render.HudUtil;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;

import java.util.UUID;

public class PingData {

    public String senderName;
    public UUID senderId;
    public Vector4f prevPos;
    public Vector4f currentPos;
    public Vec3d pos;
    public Vector4f screenPos;
    public UUID hitEntity;
    public ItemStack itemStack;
    public long spawnTime;
    public long aliveTime;


    public PingData(String senderName, UUID senderId, Vec3d pos, UUID hitEntity, long spawnTime) {
        this.senderName = senderName;
        this.senderId = senderId;
        this.pos = pos;
        this.prevPos = new Vector4f();
        this.currentPos = new Vector4f();
        this.hitEntity = hitEntity;
        this.spawnTime = spawnTime;
        this.aliveTime = 0;
    }

    public void setAliveTime(Integer aliveTime) {
        this.aliveTime = aliveTime;
    }

    public Vec3d interpolatePosition(Vec3d prevPos, Vec3d currentPos, float partialTicks) {
        return prevPos.add(currentPos.subtract(prevPos).multiply(partialTicks));
    }
    public Vector4f interpolatePosition(Vector4f prevPos, Vector4f currentPos, float partialTicks) {
        return new Vector4f(
                prevPos.x + (currentPos.x - prevPos.x) * partialTicks,
                prevPos.y + (currentPos.y - prevPos.y) * partialTicks,
                prevPos.z + (currentPos.z - prevPos.z) * partialTicks,
                prevPos.w + (currentPos.w - prevPos.w) * partialTicks
        );
    }
}
