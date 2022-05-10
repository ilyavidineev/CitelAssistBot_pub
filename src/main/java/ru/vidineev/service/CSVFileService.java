package ru.vidineev.service;

import lombok.SneakyThrows;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.vidineev.core.CoreBot;
import ru.vidineev.entities.RecordString;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static ru.vidineev.constants.Constants.CSV_ARGS_START_INDEX;

@Service
public class CSVFileService {

    @Value("${CSV.filepath}")
    private String filePath;

    final String SEPARATOR = ";";

    private static final Logger log = Logger.getLogger(CoreBot.class);

    @SneakyThrows
    public List<RecordString> ReadCSVData() {

        ArrayList<RecordString> records = new ArrayList<>();

        File file = CSVFileService.getResourceFile(filePath);
        BufferedReader breader = new BufferedReader(new FileReader(file));

        String rawLine = "";

        while (true) {
            try {
                if ((rawLine = breader.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }

            String[] recEntry = rawLine.split(SEPARATOR);

            String number = recEntry[0];
            String method = recEntry[1];
            String text = recEntry[2];

            List<String> listOfInlineAnswers = new ArrayList<>(asList(recEntry).subList(CSV_ARGS_START_INDEX, recEntry.length));
            RecordString recordString = new RecordString(number, method, text, listOfInlineAnswers);
            records.add(recordString);
        }
        breader.close();

        return records;
    }

    //  Метод возвращает файл, путь к которому зависит от того, запускаешь ты приложение в IDE или на сервере в jar-файле
    @SneakyThrows
    public static File getResourceFile(String relativePath) {

        File file = null;
        URL location = CoreBot.class.getProtectionDomain().getCodeSource().getLocation();
        String codeLocation = location.toString();
        log.info("codeLocation: " + codeLocation);


        try {
            if (codeLocation.endsWith(".jar")) {
                //Call from jar
                log.info("Call from jar detected");
                Path path = Paths.get(location.toURI()).resolve("../" + relativePath).normalize();
                log.info("path: " + path.toString());
                file = path.toFile();

            } else {
                //Call from IDE
                log.info("Call from IDE detected");
                file = new File(CoreBot.class.getClassLoader().getResource(relativePath).getPath());
                log.info("File: " + CoreBot.class.getClassLoader().getResource(relativePath).getPath());
            }
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        return file;
    }
}
