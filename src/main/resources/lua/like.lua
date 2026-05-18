local userId = ARGV[1]
local targetId = ARGV[2]
local type = ARGV[3]
local op = ARGV[4]

if op == 1 then
  -- like: only if not liked before
  local added = redis.call("SADD", KEYS[1], userId)
  if added == 1 then
    redis.call("SADD", KEYS[2], targetId)
    redis.call("ZINCRBY", KEYS[3], 1, targetId)
    redis.call("XADD", KEYS[4], "*",
      "op","like","userId",userId,"type",type,"targetId",targetId)
    return 1
  else
    return 0
  end
else
  -- unlike: only if liked before
  local removed = redis.call("SREM", KEYS[1], userId)
  if removed == 1 then
    redis.call("SREM", KEYS[2], targetId)
    redis.call("ZINCRBY", KEYS[3], -1, targetId)
    redis.call("XADD", KEYS[4], "*",
      "op","unlike","userId",userId,"type",type,"targetId",targetId)
    return 1
  else
    return 0
  end
end