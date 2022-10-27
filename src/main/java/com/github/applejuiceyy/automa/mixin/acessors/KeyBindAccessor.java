package com.github.applejuiceyy.automa.mixin.acessors;

import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBinding.class)
public interface KeyBindAccessor {
    @Accessor("timesPressed")
    int getTimesPressed();
    @Accessor("timesPressed")
    void setTimesPressed(int value);
}
