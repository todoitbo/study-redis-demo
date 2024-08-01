local sourceList = KEYS[1]
local timeout = tonumber(ARGV[1]) -- 将参数转为数字
local start = redis.call('TIME')[1] -- 获取当前时间戳

while true do
    local message = redis.call('RPOP', sourceList) -- 使用 RPOP 替代 BRPOP
    if message then
        -- 如果成功弹出消息，则返回消息
        return message
    else
        -- 如果没有消息，检查是否超时
        local now = redis.call('TIME')[1]
        if now - start > timeout then
            -- 如果超时，返回 nil
            return nil
        else
            -- 如果没有超时，等待一段时间再继续尝试
            redis.call('SLEEP', 0.1) -- 等待 0.1 秒
        end
    end
end
