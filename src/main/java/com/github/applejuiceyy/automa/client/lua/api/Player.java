package com.github.applejuiceyy.automa.client.lua.api;

import com.github.applejuiceyy.automa.client.lua.annotation.LuaConvertible;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import static com.github.applejuiceyy.automa.client.lua.api.Getter.*;

@LuaConvertible
public class Player {
    public BlockState getRayCast() {
        HitResult result = getPlayer().raycast(getInteractionManager().getReachDistance(), 0, false);

        if (result.getType() == HitResult.Type.BLOCK) {
            return getWorld().getBlockState(((BlockHitResult) result).getBlockPos());
        }

        return null;
    }

    public Varargs getPos() {
        ClientPlayerEntity player = getPlayer();
        return LuaValue.varargsOf(LuaValue.valueOf(player.getPos().x), LuaValue.valueOf(player.getPos().y), LuaValue.valueOf(player.getPos().z));
    }
}
