package com.github.applejuiceyy.automa.client.lua.api;

import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import static com.github.applejuiceyy.automa.client.lua.api.Getter.*;

@LuaConvertible
public class Player {
    @LuaConvertible
    public BlockState getRayCast() {
        HitResult result = getClient().crosshairTarget;

        if (result != null && result.getType() == HitResult.Type.BLOCK) {
            return getWorld().getBlockState(((BlockHitResult) result).getBlockPos());
        }

        return null;
    }
    @LuaConvertible
    public Varargs getPos() {
        ClientPlayerEntity player = getPlayer();
        Vec3d pos = player.getLerpedPos(getClient().getTickDelta());
        return LuaValue.varargsOf(LuaValue.valueOf(pos.x), LuaValue.valueOf(pos.y), LuaValue.valueOf(pos.z));
    }
}
