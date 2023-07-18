import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private Map<String, List<PageEntry>> index;

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        // прочтите тут все pdf и сохраните нужные данные,
        // тк во время поиска сервер не должен уже читать файлы
        index = new HashMap<>();

        File[] files = pdfsDir.listFiles();

        for (File file : files) {
            try {
                PdfDocument document = new PdfDocument(new PdfReader(file));
                int numPages = document.getNumberOfPages();

                for (int pageNumber = 1; pageNumber <= numPages; pageNumber++) {
                    var page = document.getPage(pageNumber);
                    var text = PdfTextExtractor.getTextFromPage(page);
                    var words = text.split("\\P{IsAlphabetic}+");

                    Map<String, Integer> freqs = new HashMap<>(); // мапа, где ключом будет слово, а значением - частота
                    for (var word : words) { // перебираем слова
                        if (word.isEmpty()) {
                            continue;
                        }
                        word = word.toLowerCase();
                        freqs.put(word, freqs.getOrDefault(word, 0) + 1);


                        PageEntry pageEntry = new PageEntry(file.getName(), pageNumber, freqs.get(word));
                        List<PageEntry> entries = index.getOrDefault(word, new ArrayList<>());
                        entries.add(pageEntry);
                        index.put(word, entries);
                    }

                }
                document.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public List<PageEntry> search(String word) {
        // тут реализуйте поиск по слову
        word = word.toLowerCase();

        return index.getOrDefault(word,Collections.emptyList());
    }
}
