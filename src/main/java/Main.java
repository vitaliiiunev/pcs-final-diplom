import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static final int PORT = 8989;

    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));

        // здесь создайте сервер, который отвечал бы на нужные запросы
        // слушать он должен порт 8989
        // отвечать на запросы /{word} -> возвращённое значение метода search(word) в JSON-формате

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                try (Socket clienSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clienSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clienSocket.getInputStream()))) {

                    out.println("Соединение с сервером ");

                    out.println("Введите поисковый запрос");
                    String word = in.readLine();

                    // Обработка запроса и формирование ответа
                    List<PageEntry> response = engine.search(word);

                    // Преобразование объекта Java в JSON
                    Gson gson = new Gson();
                    String jsonResponse = gson.toJson(response);

                    // Отправка ответа клиенту
                    out.println(jsonResponse);

                }

            }

        }
    }
}