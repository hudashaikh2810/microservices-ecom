-- KEYS[1] = Redis key
-- ARGV[1] = capacity
-- ARGV[2] = refillRatePerSecond
-- ARGV[3] = now in milliseconds

local key = KEYS[1]
local capacity = tonumber(ARGV[1])
local refillRatePerSecond = tonumber(ARGV[2])
local now = tonumber(ARGV[3])

-- Read existing state
local data = redis.call('HMGET', key, 'tokens', 'lastAccessed')

local tokens
local lastAccessed

if data[1] == false or data[1] == nil then
    -- First request for this client
    tokens = capacity
    lastAccessed = now
else
    tokens = tonumber(data[1])
    lastAccessed = tonumber(data[2])
end

-- Refill based on elapsed time
local elapsedSeconds = (now - lastAccessed) / 1000
local tokensToAdd = elapsedSeconds * refillRatePerSecond
local tokensAfterRefill = math.min(capacity, tokens + tokensToAdd)

local allowed
local remainingTokens
local retryAfter

if tokensAfterRefill >= 1 then
    allowed = 1
    remainingTokens = tokensAfterRefill - 1
    retryAfter = 0
else
    allowed = 0
    remainingTokens = tokensAfterRefill
    retryAfter = math.ceil(
        (1 - tokensAfterRefill) / refillRatePerSecond
    )
end

-- Save state
redis.call(
    'HMSET',
    key,
    'tokens', tostring(remainingTokens),
    'lastAccessed', tostring(now)
)

-- Reset TTL
local ttl = math.floor(capacity / refillRatePerSecond)
redis.call('EXPIRE', key, ttl)

return {allowed, retryAfter}