package es.unizar.tmdad.lab0.processors;

import java.util.HashMap;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Clase ProcessorsList
 */
@Service
public class ProcessorsList {

    private final HashMap<String, Processor> pool = new HashMap<>(4);

    @Autowired
    JumpLettersProcessor jlP;

    @Autowired
    JumpWordsProcessor jwP;

    @Autowired
    LeetProcessor lP;

    @Autowired
    NoProcessor nP;

    @PostConstruct
    private void init() {
        addProcessor("disabled", nP);
        addProcessor("jumpLetters", jlP);
        addProcessor("jumpWords", jwP);
        addProcessor("leet", lP);
    }

    private void addProcessor(String name, Processor processor) {
        pool.put(name, processor);
    }

    public Set<String> getNames() {
        return pool.keySet();
    }

    public Processor getByName(String name) {
        return pool.get(name);
    }

}
