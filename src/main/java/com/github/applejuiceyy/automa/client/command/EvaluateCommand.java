package com.github.applejuiceyy.automa.client.command;

import com.github.applejuiceyy.automa.client.lua.LuaExecutionContainer;
import com.github.applejuiceyy.automa.client.lua.LuaExecution;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class EvaluateCommand {
    public static ArgumentBuilder<FabricClientCommandSource, ?> create() {
        ArgumentBuilder<FabricClientCommandSource, ?> literal = LiteralArgumentBuilder.literal("eval");
        RequiredArgumentBuilder<FabricClientCommandSource, ?> sub = RequiredArgumentBuilder.argument("script", StringArgumentType.greedyString());
        sub.executes(context -> {
            LuaExecution executor;


            if((executor = LuaExecutionContainer.getExecutor()) != null) {
                return executor.wrapCall(() -> executor.manageCoroutine(executor.createCoroutine(executor.globals.load(context.getArgument("script", String.class), "runc"))), false)? 1 : 0;
            }

            return 0;
        });
        return literal.then(sub);
    }
}
