package br.chatup.tcc.database;

import java.util.List;

import br.chatup.tcc.bean.ChatMessage;

/**
 * Created by Luan on 9/3/2016.
 */
public interface ChatMessagesDao {
    void insert(ChatMessage chatMessage);
    List<ChatMessage> findAllByContactJID(String contactJID);
    ChatMessage findLastOneByContactJID(String contactJID);
    List<String> findContactsFromActiveChats();
}
