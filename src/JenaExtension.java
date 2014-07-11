import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase2;
import com.hp.hpl.jena.sparql.util.FmtUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.validator.routines.UrlValidator;

public class JenaExtension extends FunctionBase2{

    @Override
    public NodeValue exec(NodeValue nv, NodeValue nv1) {
        Node param = nv.asNode();
        Node action = nv1.asNode();
        String stringParam = FmtUtils.stringForNode(param);
        Integer choose = Integer.parseInt(FmtUtils.stringForNode(action));
        
        NodeValue returnValue = null;
        final String apiKey = "AIzaSyDuCjNg-TQNcgkBeYS_Lt7F1cCjmO8-Ri0";

        switch(choose){
            case 1:
                returnValue = shortenURL(stringParam, apiKey);
                break;
            case 2:
                returnValue = explodeURL(stringParam);
                break;
        }
        return returnValue;
    }

    private NodeValue shortenURL(String param, String key) {
        if(!validateURL(param))
            throw new IllegalArgumentException("[EXECEPTION] => Invalid Url.");
        
        HttpsURLConnection crawler = connectionBuilder("https://www.googleapis.com/urlshortener/v1/url?key=",key,true);
        
        try { 
            String json = "{ \"longUrl\" : " + param + " }";
            OutputStreamWriter out = new OutputStreamWriter(crawler.getOutputStream());
            out.write(json);
            out.flush();
            out.close();
            
            if(crawler.getResponseCode() == 200)
            {
                try {
                    InputStream inputStream = crawler.getInputStream();
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> jsonMap = mapper.readValue(inputStream, Map.class);
                    String shortnedUrl = jsonMap.get("id").toString();
                    return NodeValue.makeNodeString(shortnedUrl);
                } catch(IOException e) 
                {
                    System.out.println("[EXCEPTION] => shortenURL");
                    System.out.println("[EXCEPTION DETAIL] => " + e.getMessage());
                }
            }
            else
            {
                System.out.println(crawler.getErrorStream());
            }
        
        }catch(IOException e) 
        {
            System.out.println("[EXCEPTION] => shortenURL");
            System.out.println("[EXCEPTION DETAIL] => " + e.getMessage());
        }
        return null;
    }

    private NodeValue explodeURL(String param) {
        
        if(!validateURL(param) || !validShortedURL(param))
            throw new IllegalArgumentException("[EXECEPTION] => Invalid Url.");
        
        HttpsURLConnection crawler = connectionBuilder("https://www.googleapis.com/urlshortener/v1/url?shortUrl=",param,false);
        try {
            if(crawler.getResponseCode() == 200)
            {
                try {
                    InputStream inputStream = crawler.getInputStream();
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> jsonMap = mapper.readValue(inputStream, Map.class);
                    String shortnedUrl = jsonMap.get("longUrl").toString();
                    return NodeValue.makeNodeString(shortnedUrl);
                } catch(IOException e) 
                {
                    System.out.println("[EXCEPTION] => explodeURL");
                    System.out.println("[EXCEPTION DETAIL] => " + e.getMessage());
                }
            }
            else
            {
                System.out.println(crawler.getErrorStream());
            }
        } catch(IOException e)
        {
            System.out.println("[EXCEPTION] => explodeURL");
            System.out.println("[EXCEPTION DETAIL] => " + e.getMessage());
        }
        
        return null;
    }
    
    private Boolean validateURL(String param){
        String[] schemes = {"http","https"}; // DEFAULT schemes = "http", "https", "ftp"
        param = param.replace("\"", "");
        UrlValidator urlValidator = new UrlValidator(schemes);
        return urlValidator.isValid(param);
    }
    
    private Boolean validShortedURL(String param) {
        try {
            param = param.replace("\"", "");
            URL obj = new URL(param);
            return obj.getHost().equals("goo.gl");
        }catch (MalformedURLException e)
        {
            System.out.println("[EXCEPTION] => validShortedURL");
            System.out.println("[EXCEPTION DETAIL] => " + e.getMessage());
        }
        return null;
    }
    
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
}