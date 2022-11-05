package com.github.applejuiceyy.automa.client.lua;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import static com.github.applejuiceyy.automa.client.lua.api.Getter.getClient;

public class Print extends VarArgFunction {
    @Override
    public Varargs invoke(Varargs args) {
        if (getClient().player != null) {
            getClient().player.sendMessage(Text.empty()
                            .append(Text.literal("[Automa] ").setStyle(Style.EMPTY.withColor(0xff4400)))
                    .append(Text.of(args.toString())));
        }
        else {
            System.out.println(args.toString());
        }

        return LuaValue.NIL;
    }
}
