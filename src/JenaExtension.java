import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase2;
import com.hp.hpl.jena.sparql.util.FmtUtils;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
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
                shortenURL(stringParam, apiKey);
                break;
            case 2:
                explodeURL(stringParam, apiKey);
                break;
        }
        return returnValue;
    }

    private NodeValue shortenURL(String param, String key) {
        if(!validateURL(param))
            throw new IllegalArgumentException("Url non valido");
        
        HttpsURLConnection crawler = shortConnectionBuilder(key);
        
        try { 
            OutputStreamWriter out = new OutputStreamWriter(crawler.getOutputStream());
            out.write("longUrl="+param);
            out.flush();
            out.close();
            
            InputStream inputStream = crawler.getInputStream();
            //String encoding = crawler.getContentEncoding();
            String response = inputStream.toString();
            System.out.println(response);
        
        }catch(Exception x) 
        {
            x.printStackTrace();
        }
        return null;
    }

    private NodeValue explodeURL(String param, String key) {
        if(!validateURL(param))
            throw new IllegalArgumentException("Url non valido");
        return null;
    }
    
    private Boolean validateURL(String param){
        String[] schemes = {"http","https"}; // DEFAULT schemes = "http", "https", "ftp"
        param = param.replace("\"", "");
        UrlValidator urlValidator = new UrlValidator(schemes);
        if (urlValidator.isValid(param)) {
           System.out.println("url is valid");
           return true;
        } else {
           System.out.println("url is invalid");
           return false;
        }
    }
    
    private HttpsURLConnection shortConnectionBuilder(String key){
        String url = "https://www.googleapis.com/urlshortener/v1/url?key=" + key;

        try {
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type","application/json");
            con.setRequestProperty("charset", "UTF-8"); 
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            return con;
        }
        catch(Exception e)
        {
            System.out.println("excp shortConnectionBuilder " + e.getMessage());
        }
        
        return null;
    }
}
