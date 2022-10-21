package com.github.applejuiceyy.automa.client.lua;

import com.github.applejuiceyy.automa.client.AutomaClient;
import com.github.applejuiceyy.automa.client.lua.api.Player;
import com.github.applejuiceyy.automa.client.lua.api.controls.inventoryControls.InventoryControls;
import com.github.applejuiceyy.automa.client.lua.api.controls.inventoryControls.InventoryControlsLA;
import com.github.applejuiceyy.automa.client.lua.api.controls.lookControls.LookControlsLA;
import com.github.applejuiceyy.automa.client.lua.api.controls.movementControls.MovementControlsLA;
import com.github.applejuiceyy.automa.client.lua.api.listener.CancellationState;
import com.github.applejuiceyy.automa.client.lua.api.listener.Event;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
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

import static com.github.applejuiceyy.automa.client.lua.api.Getter.getClient;

public class LuaExecutionFacade {

    public final Globals globals = new Globals();
    public final Path dir;

    public final LuaBoundaryControl boundary;
    public final LuaExecutionDebugger stat;

    boolean executing = true;

    public final Event tick;
    public final Event render;
    public final Event attackItem;
    public final Event useItem;

    public final Event blockBreaking;
    public final Event blockBreakingCancel;
    public final Event brokeBlock;
    public final Event blockPlacing;

    public final LuaValue coroutineManager;
    public final LuaValue require = new Require(this);

    public LuaExecutionFacade(Path dir) {
        this.dir = dir;

        globals.load(new JseBaseLib());
        globals.load(new Bit32Lib());
        globals.load(new TableLib());
        globals.load(new StringLib());
        globals.load(new JseMathLib());
        globals.load(new DebugLib());
        globals.load(new CoroutineLib());

        stat = new LuaExecutionDebugger(this);
        globals.get("coroutine").set("create", stat.generateCustomCoroutineCreate());

        LuaC.install(globals);

        globals.set("require", require);
        globals.set("print", new Print());

        coroutineManager = require.call("coroutine_manager");

        boundary = new LuaBoundaryControl(this);
        boundary.loadAllClasses();

        tick = new Event(this);
        render = new Event(this);
        useItem = new Event(this);
        attackItem = new Event(this);
        blockBreaking = new Event(this);
        blockBreakingCancel = new Event(this);
        brokeBlock = new Event(this);
        blockPlacing = new Event(this);


        globals.set("tick", boundary.J2L(tick).arg1());
        globals.set("render", boundary.J2L(render).arg1());
        globals.set("useItem", boundary.J2L(useItem).arg1());
        globals.set("attackItem", boundary.J2L(attackItem).arg1());
        globals.set("blockBreaking", boundary.J2L(blockBreaking).arg1());
        globals.set("blockBreakingCancel", boundary.J2L(blockBreakingCancel).arg1());
        globals.set("brokeBlock", boundary.J2L(brokeBlock).arg1());
        globals.set("blockPlacing", boundary.J2L(blockPlacing).arg1());

        globals.set("player", boundary.J2L(new Player()).arg1());

        globals.set("inventory", boundary.J2L(new InventoryControlsLA(this, AutomaClient.inventoryControls)).arg1());
        globals.set("keyboard", boundary.J2L(new MovementControlsLA(this, AutomaClient.movementControls)).arg1());
        globals.set("mouse", boundary.J2L(new LookControlsLA(this, AutomaClient.lookControls)).arg1());


        new LuaExecutionDebugger(this);

        wrapCall(this::runBootstrap);
    }

    public String getName() {
        return dir.getFileName().toString();
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

    public boolean wrapCall(Runnable calling, boolean stopExecution) {
        try {
            calling.run();
            return true;
        }
        catch (Exception err) {
            stop();

            if (getClient().player != null) {
                getClient().player.sendMessage(Text.literal("[Automa] ").setStyle(Style.EMPTY.withColor(0xff4400)).append(Text.of(err.toString())));
            }
            else {
                err.printStackTrace();
            }

            return false;
        }
    }

    public boolean wrapCall(Runnable calling) {
        return wrapCall(calling, true);
    }

    public boolean performEvent(Event event) {
        CancellationState cancel = new CancellationState();
        boolean ran = performEventWith(event, boundary.J2L(cancel));
        return ran && cancel.isCancelled();
    }

    public boolean performEventWith(Event event, Varargs value) {
        return wrapCall(() -> event.fire(value));
    }

    public void manageCoroutine(LuaValue value) {
        coroutineManager.call(value);
    }
    public LuaThread createCoroutine(LuaValue func) { return stat.createCoroutine(func);}
}
