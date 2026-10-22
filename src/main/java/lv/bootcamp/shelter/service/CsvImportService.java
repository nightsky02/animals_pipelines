package lv.bootcamp.shelter.service;

import lombok.extern.slf4j.Slf4j;
import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ImportResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CsvImportService {

    public ImportResult importAnimals(Path inputPath) {
        log.info("Starting import from {}", inputPath);

        List<Animal> allAnimals = new ArrayList<>();
        int skippedRows = 0;
        // TODO Step 1:
        // 1) Read intake.csv with UTF-8.
        // 2) Skip header row.
        // 3) Skip malformed rows and log warnings.
        // 4) Allow blank age as unknown (null), but reject non-numeric age values.
        // 5) Parse intakeDate using DateTimeFormatter.
        // 6) Map each row to Animal object.

        try(BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8)){
            reader.readLine();

            String line;
            while((line = reader.readLine()) != null){
                String[] parts = line.split(",");

                if(parts.length != 5 || !validateLineData(parts)){
                    log.info("Skipped incorrect line {}", line);
                    skippedRows++;
                    continue;
                }


                String name = parts[0].trim();
                String species = parts[1].trim();

                int age = -1;
                if(!parts[2].trim().isEmpty()){
                    age = Integer.parseInt(parts[2].trim());
                }

                boolean vaccinated = Boolean.parseBoolean(parts[3].trim());

                try{
                    LocalDate date = LocalDate.parse(
                            parts[4].trim(),
                            DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    );

                    allAnimals.add(new Animal(name, species, age, vaccinated, date));
                } catch(DateTimeParseException ex){
                    log.info("Skipped incorrect line {}", line);
                }
            }
        }catch(IOException ex){
            log.error("Cannot read the file {}", inputPath.toString());
        }
        return new ImportResult(allAnimals, skippedRows);
    }
    private boolean validateLineData(String[] parts){
        // parts[0] - name
        // parts[1] - species
        // parts[2] - age } Optional
        // parts[3] - vaccinated
        // parts[4] - intakeDate

        if(parts[0].trim().isEmpty() || parts[1].trim().isEmpty() || parts[3].trim().isEmpty() || parts[4].trim().isEmpty())
            return false;

        if(!parts[2].trim().isEmpty()){
            try{
                int age = Integer.parseInt(parts[2].trim());
                if(age < 0){
                    return false;
                }
            } catch(NumberFormatException ex){
                return false;
            }
        }

        return parts[3].trim().compareTo("true") != 0 || parts[3].trim().compareTo("false") != 0;
    }
}
