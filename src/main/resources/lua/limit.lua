-- 定义变量：redis中key值、规定的时间段内访问次数、redis中过期时间、当前访问次数

local key = KEYS[1]
local limit = tonumber(ARGV[1])
local count = tonumber(ARGV[2])
local current = tonumber(redis.call('get', key) or "0")

if current + 1 > limit then
  return 0
end
   -- 没有超阈值，将当前访问数量+1，
   current = redis.call("INCRBY", key, "1")
if tonumber(current) == 1 then
   -- 设置过期时间
   redis.call("expire", key, count)
end
   return tonumber(current)