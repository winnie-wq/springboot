package org.example.medicinalplant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.medicinalplant.entity.ChatHistory;

@Mapper
public interface ChatHistoryMapper extends BaseMapper<ChatHistory> {

    /**
     * 获取用户的最近对话记录
     */
    @Select(
            "SELECT * FROM medicinal_chat_history WHERE user_id = #{userId} "
                    + "ORDER BY created_at DESC LIMIT #{limit}")
    List<ChatHistory> getRecentChats(@Param("userId") Integer userId, @Param("limit") Integer limit);

    /**
     * 获取会话的所有对话
     */
    @Select(
            "SELECT * FROM medicinal_chat_history WHERE session_id = #{sessionId} "
                    + "ORDER BY created_at ASC")
    List<ChatHistory> getSessionChats(@Param("sessionId") String sessionId);

    /**
     * 删除用户的旧对话记录（保留最近 N 条）
     */
    @Delete(
            "DELETE FROM medicinal_chat_history WHERE user_id = #{userId} "
                    + "AND id NOT IN (SELECT id FROM (SELECT id FROM medicinal_chat_history "
                    + "WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{keepCount}) t)")
    void deleteOldChats(@Param("userId") Integer userId, @Param("keepCount") Integer keepCount);
}
