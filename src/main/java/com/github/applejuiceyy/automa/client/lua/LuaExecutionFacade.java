package com.github.applejuiceyy.automa.client.lua;

import com.github.applejuiceyy.automa.client.AutomaClient;
import com.github.applejuiceyy.automa.client.lua.api.Inventory;
import com.github.applejuiceyy.automa.client.lua.api.Player;
import org.luaj.vm2.*;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class LuaExecutionFacade {
    Globals globals = new Globals();
    Path dir;
    public LuaBoundaryControl boundary;

    boolean executing = true;

    public final LuaEvent tick;
    public final LuaEvent attackItem;
    public final LuaEvent useItem;

    public final LuaEvent blockBreaking;
    public final LuaEvent blockBreakingCancel;
    public final LuaEvent brokeBlock;

    LuaValue coroutineManager;
    LuaValue require = new Require(this);

    public LuaExecutionFacade(Path dir) {
        this.dir = dir;

        globals.load(new JseBaseLib());
        globals.load(new Bit32Lib());
        globals.load(new TableLib());
        globals.load(new StringLib());
        globals.load(new JseMathLib());
        globals.load(new DebugLib());
        globals.load(new CoroutineLib());

        LuaC.install(globals);

        globals.set("require", require);

        coroutineManager = require.call("coroutine_manager");

        boundary = new LuaBoundaryControl(this);
        boundary.loadAllClasses();

        tick = new LuaEvent(this);
        useItem = new LuaEvent(this);
        attackItem = new LuaEvent(this);
        blockBreaking = new LuaEvent(this);
        blockBreakingCancel = new LuaEvent(this);
        brokeBlock = new LuaEvent(this);


        globals.set("tick", boundary.J2L(tick));
        globals.set("useItem", boundary.J2L(useItem));
        globals.set("attackItem", boundary.J2L(attackItem));
        globals.set("blockBreaking", boundary.J2L(blockBreaking));
        globals.set("blockBreakingCancel", boundary.J2L(blockBreakingCancel));
        globals.set("brokeBlock", boundary.J2L(brokeBlock));

        globals.set("inventory", boundary.J2L(new Inventory()));
        globals.set("player", boundary.J2L(new Player()));


        new LuaExecutionStatistic(this);

        wrapCall(this::runBootstrap);
    }

    public void stop() {
        executing = false;
    }

    public void tick() {
        wrapCall(() -> tick.fire(LuaValue.varargsOf(new LuaValue[0])));
    }

    void runBootstrap() {
        require.call("bootstrap");
    }

    public LuaValue loadResource(String name) throws FileNotFoundException {
        InputStream boostrap = this.getClass().getResourceAsStream("/assets/" + AutomaClient.MOD_ID + "/scripts/" + name +  ".lua");

        if (boostrap == null) {
            throw new FileNotFoundException("Resource not found");
        }

        byte[] bytes;

        try {
            bytes = boostrap.readAllBytes();
        }
        catch (IOException err) {
            throw new LuaError("Cannot read boostrap script");
        }

        return loadString(new String(bytes), name);
    }

    public LuaValue loadFile(String name) throws FileNotFoundException, NoSuchFileException {
        String content;

        try {
            content = Files.readString(dir.resolve(name + ".lua"), StandardCharsets.US_ASCII);
        } catch (FileNotFoundException | NoSuchFileException err) {
            throw err;
        } catch (IOException err) {
            throw new RuntimeException(err);
        }

        return loadString(content, name);
    }

    public LuaValue loadString(String content, String chunkname) {
        return globals.load(content, chunkname);
    }

    public LuaThread asThread(LuaValue value) {
        return new LuaThread(globals, value);
    }

    public boolean wrapCall(Runnable calling) {
        try {
            calling.run();
            return true;
        }
        catch (Exception err) {
            err.printStackTrace();
            stop();
            return false;
        }
    }

    public boolean performEvent(LuaEvent event) {
        LuaEvent.CancellationState cancel = new LuaEvent.CancellationState();
        boolean ran = wrapCall(() -> event.fire(LuaValue.varargsOf(new LuaValue[]{boundary.J2L(cancel)})));

        return ran && cancel.isCancelled();
    }

    public void manageCoroutine(LuaValue value) {
        coroutineManager.call(value);
    }
}
