package br.chatup.tcc.database;

        import java.util.List;

        import br.chatup.tcc.bean.ChatMessage;

/**
 * Created by Luan on 9/3/2016.
 */
public interface ChatMessagesDao {
    void deleteUserInfo();
    void insertChatMsg(ChatMessage chatMessage);
    void insertPreference(String prefType, String prefValue);
    void updatePreference(String prefType, String prefValue);
    List<ChatMessage> findAllByContactJID(String contactJID);
    ChatMessage findLastOneByContactJID(String contactJID);
    List<String> findContactsFromActiveChats();
    int findTranslationMode(String property);

}
