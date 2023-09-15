local hashKey = KEYS[1]
local batch = BATCH_SIGN

local value = tonumber(redis.call("HGET", hashKey, "value")) or 0
local max = tonumber(redis.call("HGET", hashKey, "max"))

local ids = {}

if max and max >= value + batch then
    local lastId = redis.call("HINCRBY", hashKey, "value", batch)
    lastId = tonumber(lastId)

    for i = 1, batch do
        table.insert(ids, lastId-batch + i)
    end
end

return ids

