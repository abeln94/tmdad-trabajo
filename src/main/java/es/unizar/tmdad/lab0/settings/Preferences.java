package es.unizar.tmdad.lab0.settings;

import es.unizar.tmdad.lab0.processors.Processor;
import java.util.HashMap;
import org.springframework.stereotype.Service;

/**
 * Clase Preferences. Sustituir con base de datos externa o algo
 */
@Service
public class Preferences {

    private static final String PROCESSOR_NAME = "processor_name";
    private static final String PROCESSOR_NAME_DEFAULT = "disabled";
    private static final String PROCESSOR_LEVEL = "processor_level";
    private static final String PROCESSOR_LEVEL_DEFAULT = Processor.level.NONE.toString();

    private final HashMap<String, String> preferences = new HashMap<>();

    public String getProcessorName() {
        return preferences.getOrDefault(PROCESSOR_NAME, PROCESSOR_NAME_DEFAULT);
    }

    public void setProcessorName(String name) {
        preferences.put(PROCESSOR_NAME, name);
    }

    public Processor.level getProcessorLevel() {
        try {
            return Processor.level.valueOf(preferences.getOrDefault(PROCESSOR_LEVEL, PROCESSOR_LEVEL_DEFAULT));
        } catch (IllegalArgumentException e) {
            return Processor.level.NONE;
        }
    }

    public void setProcessorLevel(Processor.level level) {
        preferences.put(PROCESSOR_LEVEL, level.toString());
    }
}
