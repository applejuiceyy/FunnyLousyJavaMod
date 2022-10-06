local r = require

local step = r("coroutine_manager")

local thread = coroutine.create(function()
    r("main")
end)

step(thread)