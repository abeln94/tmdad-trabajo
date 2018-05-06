package es.unizar.tmdad.lab0.settings;

import es.unizar.tmdad.lab0.repo.DBAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Keeps the preferences of the application
 * - Getters to retrieve
 */
@Service
public class Preferences {

    /**
     * To keep the preferences saved on the database (and to retrieve them on startup)
     */
    @Autowired
    DBAccess twac;

    static final String key_processorName = "KEY_PROCESSOR_NAME";
    private String processorName;

    public String getProcessorName() {
        return processorName;
    }

    public void setConfiguration(String processorName) {//TODO: rename to setProcessorName
        this.processorName = processorName;
        twac.setSettings(key_processorName, processorName);
    }

    static final String key_query = "KEY_QUERY";
    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
        twac.setSettings(key_query, query);
    }

    //loader
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        processorName = twac.getSettings(key_processorName);
        query = twac.getSettings(key_query);

        System.out.println("Loaded preferences PROCESSOR=" + processorName + " QUERY=" + query);
    }
}
