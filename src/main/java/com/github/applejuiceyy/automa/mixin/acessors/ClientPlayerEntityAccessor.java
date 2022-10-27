package com.github.applejuiceyy.automa.mixin.acessors;

import com.github.applejuiceyy.automa.client.screen_handler_interface.AutomatedScreenHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayerEntity.class)
public interface ClientPlayerEntityAccessor {
    @Accessor("ticksLeftToDoubleTapSprint")
    int getTicksLeftToDoubleTapSprint();

    @Accessor("ticksLeftToDoubleTapSprint")
    void setTicksLeftToDoubleTapSprint(int value);


    @Accessor("autoJumpEnabled")
    boolean isAutoJumpFieldEnabled();
}
