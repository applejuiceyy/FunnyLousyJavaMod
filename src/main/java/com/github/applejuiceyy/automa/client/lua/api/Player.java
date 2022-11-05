package com.github.applejuiceyy.automa.client.lua.api;

import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import com.github.applejuiceyy.automa.client.lua.annotation.Property;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Vector3f;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import static com.github.applejuiceyy.automa.client.lua.api.Getter.*;

@LuaConvertible
public class Player {
    private final ScreenAPI screenapi;

    public Player(ScreenAPI api) {
        this.screenapi = api;
    }

    @LuaConvertible
    public BlockState getRayCast() {
        return getRayCast(false);
    }

    @LuaConvertible
    public Vector3f getRayCastPosition() {
        return getRayCastPosition(false);
    }

    @LuaConvertible
    public BlockState getRayCast(boolean includeFluids) {
        assert getClient().player != null;
        HitResult result = getClient().player
                        .raycast(getInteractionManager().getReachDistance(), getClient().getTickDelta(), includeFluids);

        if (result != null && result.getType() == HitResult.Type.BLOCK) {
            return getWorld().getBlockState(((BlockHitResult) result).getBlockPos());
        }

        return null;
    }

    @LuaConvertible
    public Vector3f getRayCastPosition(boolean includeFluids) {
        assert getClient().player != null;
        HitResult result = getClient().player
                .raycast(getInteractionManager().getReachDistance(), getClient().getTickDelta(), includeFluids);

        if (result != null && result.getType() == HitResult.Type.BLOCK) {
            Vec3i vec = ((BlockHitResult) result).getBlockPos();
            return new Vector3f(vec.getX(), vec.getY(), vec.getZ());
        }

        return null;
    }


    @LuaConvertible
    public Vector3f getPos() {
        ClientPlayerEntity player = getPlayer();
        return player.getLerpedPos(getClient().getTickDelta()).toVector3f();
    }

    @LuaConvertible
    public Vector3f getVel() {
        return getPlayer().getVelocity().toVector3f();
    }


    @LuaConvertible
    public boolean isOnGround() {
        return getPlayer().isOnGround();
    }
}
