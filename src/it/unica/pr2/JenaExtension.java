package it.unica.pr2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.pfunction.PFuncAssignToObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

/**
* Estensione dell'applicativo Open Source Apache Jena che permette di abbreviare
* e di ri-estendere URL letti da un file rdf.
* 
* @author  Simone Cusimano & Giancarlo Lelli
* @version 1.0
*/

public class JenaExtension extends PFuncAssignToObject{

    final String BASE_URL = "https://www.googleapis.com/urlshortener/v1/url?";
    final String API_KEY = "AIzaSyDuCjNg-TQNcgkBeYS_Lt7F1cCjmO8-Ri0";
    
    /**
    * Abbrevia, sfruttando le API Google, l'URL preso come parametro.
    *
    * @param   param URL da abbreviare
    * @param   key API_KEY del servizio Google Shortener
    * @return  un Node contentente il risultato dell'abbreviazione, 
    *          "Invalid Input" se l'URL non è valido,
    *          "Invalid Http Request" se la richiesta non va a buon fine.
    */
    private Node shortenURL(String param, String key) {
        if(!validateURL(param))
            return NodeFactory.createLiteral("ERROR!!");
        
        HttpsURLConnection crawler = connectionBuilder(BASE_URL+"key=",key,true);
        
        try { 
            String json = "{ \"longUrl\" : \"" + param + "\" }";
            try (OutputStreamWriter out = new OutputStreamWriter(crawler.getOutputStream())) {
                out.write(json);
                out.flush();
            }
            
            if(crawler.getResponseCode() == 200)
            {
                try {
                    InputStream inputStream = crawler.getInputStream();
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> jsonMap = mapper.readValue(inputStream, Map.class);
                    String shortnedUrl = jsonMap.get("id").toString();
                    return NodeValue.makeNodeString(shortnedUrl).asNode();
                } catch(IOException e) { }
            } 
        }catch(IOException e) { }
        return NodeFactory.createLiteral("ERROR!!");
    }

    /**
    * Allunga, sfruttando le API Google, l'URL, già abbreviato, preso come parametro.
    *
    * @param   param URL da allungare
    * @return  un Node contentente il risultato dell'allungamento, 
    *          "Invalid Input" se l'URL non è valido,
    *          "Invalid Http Request"
    */
    private Node explodeURL(String param) {
        if(!validateURL(param) || !validShortedURL(param))
            return NodeFactory.createLiteral("ERROR!!");

        HttpsURLConnection crawler = connectionBuilder(BASE_URL+"shortUrl=",param,false);

        try {
            if(crawler.getResponseCode() == 200)
            {
                try {
                    InputStream inputStream = crawler.getInputStream();
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> jsonMap = mapper.readValue(inputStream, Map.class);
                    String shortnedUrl = jsonMap.get("longUrl").toString();
                    return NodeValue.makeNodeString(shortnedUrl).asNode();
                } catch(IOException e){ }
            }
        } catch(IOException e){ }
        
        return NodeFactory.createLiteral("ERROR!!");
    }

    /**
    * Controlla se l'URL preso come parametro è un URL http o https.
    *
    * @param   param URL da validare
    * @return  TRUE se l'URL è valido,
    *          FALSE altrimenti.
    * 
    */
    private Boolean validateURL(String param){
        String[] schemes = {"http","https"}; // DEFAULT schemes = "http", "https"
        return param.startsWith(schemes[0], 0) || param.startsWith(schemes[1], 0);
    }
    
    /**
    * Controlla se l'URL preso come parametro è un URL già abbreviato
    *
    * @param   param URL da controllare
    * @return  TRUE se l'URL è valido,
    *          FALSE altrimenti.
    */   
    private Boolean validShortedURL(String param) {
        try
        {
            param = param.replace("\"", "");
            URL obj = new URL(param);
            return obj.getHost().equals("goo.gl");
        } catch (MalformedURLException e)
        {
            return false;
        }
    }
    
    /**
    * Si occupa di creare l'endpoint per la comunicazione
    *
    * @param   param URL della risorsa
    * @param   key API_KEY del servizio Google Shortener
    * @param   output flag tra le due possibili richieste (GET/POST)
    * @return  Restituisce l'oggetto che si occuperà di gestire la comunicazione con
    *          l'endpoint di Google
    */   
    private HttpsURLConnection connectionBuilder(String url, String key, Boolean output){
        try {
            URL obj = new URL(url + key);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            if(output)
            {
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
            }
            else
                con.setRequestMethod("GET");
            
            return con;
        }
        catch(IOException e)
        {
            System.out.println("[EXCEPTION] => connectionBuilder");
            System.out.println("[EXCEPTION DETAIL] => " + e.getMessage());
        }
        
        return null;
    }

    /**
    * Metodo core dell'estensione. Si prende carico della logica computazionale.
    *
    * @param   node parametro della query
    * @return  Restituisce un oggetto Node contentente l'URL abbreviato/allungato
    *
    */   
    @Override
    public Node calc(Node node) {
        String stringParam = "Invalid input.";
        
        if(!node.isLiteral() && node.isURI())
            stringParam = node.toString();
        else if(node.isLiteral())
            stringParam = node.getLiteralLexicalForm();
        else
            return NodeFactory.createLiteral(stringParam);

        Boolean action = validShortedURL(stringParam);
        Node returnValue;
        
        if(!action)
            returnValue = shortenURL(stringParam, API_KEY);
        else
            returnValue = explodeURL(stringParam);
        
        return NodeFactory.createLiteral(returnValue.getLiteralLexicalForm());
    }
}