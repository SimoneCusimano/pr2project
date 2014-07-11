
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase2;
import com.hp.hpl.jena.sparql.util.FmtUtils;
import org.apache.commons.validator.routines.UrlValidator;



public class JenaExtension extends FunctionBase2{

    @Override
    public NodeValue exec(NodeValue nv, NodeValue nv1) {
        Node param = nv.asNode();
        Node action = nv1.asNode();
        String stringParam = FmtUtils.stringForNode(param);
        String stringAction = FmtUtils.stringForNode(action);
        
        NodeValue returnValue = null;
        final String apiKey = "AIzaSyDuCjNg-TQNcgkBeYS_Lt7F1cCjmO8-Ri0";
        
        switch(stringAction){
            case "short":
                shortenURL(stringParam, apiKey);
                break;
            case "explode":
                explodeURL(stringParam, apiKey);
                break;
        }
        return returnValue;
    }

    private NodeValue shortenURL(String param, String key) {
        if(!validateURL(param))
            throw new IllegalArgumentException("Url non valido");
        return null;

    }

    private NodeValue explodeURL(String param, String key) {
        if(!validateURL(param))
            throw new IllegalArgumentException("Url non valido");
        return null;
    }
    
    private Boolean validateURL(String param){
        String[] schemes = {"http","https"}; // DEFAULT schemes = "http", "https", "ftp"
        UrlValidator urlValidator = new UrlValidator(schemes);
        if (urlValidator.isValid(param)) {
           System.out.println("url is valid");
           return true;
        } else {
           System.out.println("url is invalid");
           return false;
        }
    }
}
