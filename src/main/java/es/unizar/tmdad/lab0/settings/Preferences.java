package es.unizar.tmdad.lab0.settings;

import es.unizar.tmdad.lab0.repo.DBAccess;
import javax.annotation.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * Clase Preferences. 
 */
@Service
public class Preferences {
    

    @Autowired
    DBAccess twac;

    static final String key_processorName = "KEY_PROCESSOR_NAME";
    private String processorName;

    public String getProcessorName() {
        return processorName;
    }

    public void setConfiguration(String processorName) {
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

        System.out.println("Loaded preferences PROCESSOR="+processorName+" QUERY="+query);
    }
}
