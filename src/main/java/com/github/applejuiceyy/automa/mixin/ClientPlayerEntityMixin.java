package com.github.applejuiceyy.automa.mixin;

import com.github.applejuiceyy.automa.client.AutomaClient;
import com.github.applejuiceyy.automa.client.mixin_interface.ClientPlayerEntityInterface;
import com.github.applejuiceyy.automa.client.screen_handler_interface.AutomatedScreenHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin implements ClientPlayerEntityInterface {
    AutomatedScreenHandler<?> handler;

    @Redirect(method="tickMovement", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 1))
    boolean a(KeyBinding instance) {
        return (!AutomaClient.movementControls.requested() && instance.isPressed()) || AutomaClient.movementControls.sprinting;
    }

    @Inject(method="tickMovement", at=@At(value = "HEAD"))
    void b(CallbackInfo ci) {
        if (AutomaClient.movementControls.requested()) {
            ((ClientPlayerEntityAccessor) this).setTicksLeftToDoubleTapSprint(0);
        }
    }

    @Inject(method="isAutoJumpEnabled", at=@At(value = "HEAD"), cancellable = true)
    void c(CallbackInfoReturnable<Boolean> cir) {
        if (AutomaClient.movementControls.requested()) {
            cir.setReturnValue(AutomaClient.movementControls.autoJump);
        }
        cir.setReturnValue(AutomaClient.movementControls.autoJump || ((ClientPlayerEntityAccessor)this).isAutoJumpFieldEnabled());
    }

    @Override
    public AutomatedScreenHandler<?> getAutomatedScreenHandler() {
        return handler;
    }

    @Override
    public void setAutomatedScreenHandler(@Nullable AutomatedScreenHandler<?> handler) {
        this.handler = handler;
    }
}
