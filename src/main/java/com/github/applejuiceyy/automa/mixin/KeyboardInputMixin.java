package com.github.applejuiceyy.automa.mixin;

import com.github.applejuiceyy.automa.client.AutomaClient;
import com.github.applejuiceyy.automa.client.lua.LuaExecutionContainer;
import com.github.applejuiceyy.automa.client.lua.LuaExecutionFacade;
import net.minecraft.block.BlockState;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.item.ItemPlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {
    @Inject(method="tick",
            at=@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z",
                    ordinal = 3,
                    shift = At.Shift.BY,
                    by = 2
            )
    )
    void a(boolean slowDown, float f, CallbackInfo ci) {
        KeyboardInput tthis = (KeyboardInput)(Object) this;
        if(AutomaClient.movementControls.requested()) {
            tthis.pressingBack = false;
            tthis.pressingForward = false;
            tthis.pressingLeft = false;
            tthis.pressingRight = false;
        }

        tthis.pressingForward = tthis.pressingForward || AutomaClient.movementControls.forwards;
        tthis.pressingBack = tthis.pressingBack || AutomaClient.movementControls.backwards;
        tthis.pressingLeft = tthis.pressingLeft || AutomaClient.movementControls.left;
        tthis.pressingRight = tthis.pressingRight || AutomaClient.movementControls.right;
    }

    @Inject(method="tick",
            at=@At(value = "TAIL")
    )
    void b(boolean slowDown, float f, CallbackInfo ci) {
        KeyboardInput tthis = (KeyboardInput)(Object) this;
        if(AutomaClient.movementControls.requested()) {
            tthis.jumping = false;
            tthis.sneaking = false;
        }

        tthis.jumping = tthis.jumping || AutomaClient.movementControls.jumping;
        tthis.sneaking = tthis.sneaking || AutomaClient.movementControls.sneaking;
    }
}
