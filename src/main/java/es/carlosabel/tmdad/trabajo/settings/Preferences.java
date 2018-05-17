package es.carlosabel.tmdad.trabajo.settings;

import es.carlosabel.tmdad.trabajo.repo.DBAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Keeps the preferences of the application
 * - Getters to retrieve
 * - Setters to save
 */
@Service
public class Preferences {

    /**
     * To keep the preferences saved on the database (and to retrieve them on startup)
     */
    @Autowired
    DBAccess database;

    //---------------------processorName-----------------
    static final String key_processorName = "KEY_PROCESSOR_NAME";
    private String processorName;

    public String getProcessorName() {
        return processorName;
    }

    public void setProcessorName(String processorName) {
        this.processorName = processorName;
        database.setSetting(key_processorName, processorName);
    }

    //---------------------query------------------
    static final String key_query = "KEY_QUERY";
    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
        database.setSetting(key_query, query);
    }

    //------------startup loader---------------
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        processorName = database.getSetting(key_processorName);
        query = database.getSetting(key_query);

        System.out.println("Loaded preferences PROCESSOR=" + processorName + " QUERY=" + query);
    }
}
