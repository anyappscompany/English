package ua.com.anyapps.english.Server;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import ua.com.anyapps.english.Server.Translation.TranslationResult;

public interface ServerApi {
    // новый ключ доступа
    //@Headers({"Host: i98825p1.bget.ru", "Connection: keep-alive", "Cache-Control: max-age=0", "Upgrade-Insecure-Requests: 1", "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.27 Safari/537.36", "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3", "Accept-Encoding: gzip, deflate", "Accept-Language: ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7"})
    @GET("translator/{txtstring}")
    Call<TranslationResult> getText(@Path("txtstring") String txtString);
}
