import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Rule;
import org.junit.Test;

import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.runner.Result;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;



public class stub {
    String host = "localhost";
    int port = 8080;


    public static void main(String[] args) throws IOException {
        //экхемпляр класса wiremock
        WireMockServer wireMockServer = new WireMockServer();

        //старт заглушки
        wireMockServer.start();

        //get запрос
        configureFor("localhost", 8080);
        stubFor(get(urlEqualTo("/test"))
                .willReturn(aResponse()
                        .withBody("get req done")));

        //post запрос
        configureFor("localhost", 8080);
        stubFor(post(urlEqualTo("/redirect"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("post req done")));


        //библиотека API Apache HttpClient для представления клиента, подключающегося к серверу:
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet request = new HttpGet("http://localhost:8080/test");
        HttpPost request1 = new HttpPost("http://localhost:8080/redirect");
        HttpResponse httpResponse = httpClient.execute(request);
        HttpResponse httpResponse1 = httpClient.execute(request1);

        String responseString = convertResponseToString(httpResponse);
        String responseString1 = convertResponseToString(httpResponse1);

        verify(postRequestedFor(urlEqualTo("/redirect")));
        verify(getRequestedFor(urlEqualTo("/test")));
        assertEquals("asd", responseString);
        assertEquals("asd", responseString1);

        //wireMockServer.stop();
    }

    private static String convertResponseToString(HttpResponse response) throws IOException {
        InputStream responseStream = response.getEntity().getContent();
        Scanner scanner = new Scanner(responseStream, "UTF-8");
        String responseString = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return responseString;
    }
}
