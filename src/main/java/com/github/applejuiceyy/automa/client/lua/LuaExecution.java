package com.github.applejuiceyy.automa.client.lua;

import com.github.applejuiceyy.automa.client.AutomaClient;
import com.github.applejuiceyy.automa.client.lua.api.Player;
import com.github.applejuiceyy.automa.client.lua.api.ScreenAPI;
import com.github.applejuiceyy.automa.client.lua.api.controls.inventoryControls.InventoryControlsLA;
import com.github.applejuiceyy.automa.client.lua.api.controls.lookControls.LookControlsLA;
import com.github.applejuiceyy.automa.client.lua.api.controls.movementControls.MovementControlsLA;
import com.github.applejuiceyy.automa.client.lua.api.listener.CancellationState;
import com.github.applejuiceyy.automa.client.lua.api.listener.Event;
import com.github.applejuiceyy.automa.client.lua.api.World;
import com.github.applejuiceyy.automa.client.lua.boundary.LuaBoundaryControl;
import com.github.applejuiceyy.automa.client.lua.entrypoint.AutomationEntrypoint;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.joml.Vector3f;
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
import java.util.List;

import static com.github.applejuiceyy.automa.client.lua.api.Getter.getClient;

public class LuaExecution {

    public final Globals globals = new Globals();
    public final Path dir;

    public final LuaBoundaryControl boundary;

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

    private LuaExecutionAttempt currentExecutor;

    public LuaExecution(Path dir) {
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


        ScreenAPI api;
        globals.set("inventory", boundary.J2L(new InventoryControlsLA(this, AutomaClient.inventoryControls)).arg1());
        globals.set("keyboard", boundary.J2L(new MovementControlsLA(this, AutomaClient.movementControls)).arg1());
        globals.set("mouse", boundary.J2L(new LookControlsLA(this, AutomaClient.lookControls)).arg1());
        globals.set("screen", boundary.J2L(api = new ScreenAPI(this)).arg1());
        globals.set("world", boundary.J2L(new World()).arg1());
        globals.set("vec", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                return boundary.J2L(new Vector3f((float) args.checkdouble(1), (float) args.checkdouble(2), (float) args.checkdouble(3)));
            }
        });
        globals.set("player", boundary.J2L(new Player(api)).arg1());

        List<EntrypointContainer<AutomationEntrypoint>> entries =
                FabricLoaderImpl.INSTANCE.getEntrypointContainers("automation", AutomationEntrypoint.class);

        for (EntrypointContainer<AutomationEntrypoint> entry: entries){
            entry.getEntrypoint().loadRuntime(this);
        }

        wrapCall(this::runBootstrap);
    }

    public String getName() {
        return dir.getFileName().toString();
    }

    public void stop() {
        executing = false;
    }

    public boolean isRunning() {
        return executing;
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

    public void executeAttempt(Runnable runnable) {
        if (currentExecutor != null) {
            throw new RuntimeException("an executor is already running");
        }

        currentExecutor = new LuaExecutionAttempt(runnable);
        try {
            currentExecutor.execute();
        }
        finally {
            currentExecutor = null;
        }
    }

    public void executeInMain(Runnable runnable) {
        if (currentExecutor != null) {
            currentExecutor.executeInMain(runnable);
        } else {
            throw new RuntimeException("Not executing");
        }
    }

    public boolean wrapCall(Runnable calling, boolean stopExecution) {
        if (!executing) {
            return false;
        }
        try {
            executeAttempt(calling);
            return true;
        }
        catch (Exception err) {
            if (stopExecution) {
                stop();
            }

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
    public LuaThread createCoroutine(LuaValue func) { return new LuaThread(globals, func);}
}
