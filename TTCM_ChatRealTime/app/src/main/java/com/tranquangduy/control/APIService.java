package com.tranquangduy.control;


import com.tranquangduy.model.User;

import java.util.List;

public interface APIService {
    String NOTIFICATIOIN_URL = "https://fcm.googleapis.com/fcm/send";
    String SEVER_KEY = "AAAA3I4loaY:12312313123Jmcef_opaAz8rfsF4Ykm1m999FUp0HgrwYVJvFx8EFf73L9cl2pCy_QTW0KgWQM0igD-RCbKP2j7UJoNsj2bxHkiWYbZ7NJBnbSkRG1iqPuTd7qnJB3Mf6RB3JA7E5z5f";
    String CHANNEL_ID = "1000";

    void UPDATE_USERID(String id);
    void SEARCH_LISTUSER(List<User> listUser);
}
