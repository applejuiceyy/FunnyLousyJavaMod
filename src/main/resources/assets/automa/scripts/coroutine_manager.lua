local step

function await(...)
    local evts = {...}
    coroutine.yield(function(thread)
        local undos = {}

        local call = function (...)
            for _, v in pairs(undos) do
                v()
            end

            step(thread, ...)
        end

        for _, v in pairs(evts) do
            table.insert(undos, v:subscribe(call))
        end
    end)
end

function step(thread, ...)
    local result, event = coroutine.resume(thread, ...)

    if not result then
        error(event)
    end

    if event ~= nil then
        event(thread)
    end
end

return step
