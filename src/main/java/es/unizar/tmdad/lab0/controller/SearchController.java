package es.unizar.tmdad.lab0.controller;

import es.unizar.tmdad.lab0.service.TwitterLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.social.UncategorizedApiException;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;


@Controller
public class SearchController {

    @Autowired
    TwitterLookupService twitter;

    @RequestMapping("/")
    public String greeting() {
        return "index";
    }

    @ResponseStatus(value= HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UncategorizedApiException.class)
    @ResponseBody
    public SearchResults handleUncategorizedApiException() {
        return twitter.emptyAnswer();
    }
    
    @RequestMapping("/template")
    public String template() {
        return "template";
    }
    
    @RequestMapping("/admin")
    public String configuration(){
        return "admin";
    }
    
    @MessageMapping(/*app*/"/settings")
    public void searchQuery(String id, @Header String query, @Header String processor, @Header String level) throws Exception {
        if(!"1234".equals(id)){
            System.out.println("No permissions!");
            return;
        }
        System.out.println("Received search="+query+" - processor="+processor+" - level="+level);
        twitter.changeSettings(query,processor,level);
    }
    
    @EventListener
    private void handleSessionSubscribe(SessionSubscribeEvent event){
        System.out.println("user subscribed");
        twitter.subscribeUser();
    }
    
    @EventListener
    private void handleSessionUnsubscribe(SessionUnsubscribeEvent event){
        System.out.println("user unsubscribed");
        twitter.unSubscribeUser();
    }
    
    
}