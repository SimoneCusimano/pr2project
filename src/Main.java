
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.util.FileManager;


public class Main {
    
    public static void main(String args[]){
        
        /*
        Importare il file data.rdf e stamparlo nel terminale utilizzando il formato RDF/JSON
        FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
        Model model = FileManager.get().loadModel("c:/users/simone/documents/netbeansprojects/pr2project/data.rdf");
        model.write(System.out,"RDF/JSON");
        */
        
        //sparqlTest();
        
        JenaExtension pippo = new JenaExtension();
        pippo.exec(NodeValue.makeNodeString("http://informatica.unica.it"),NodeValue.makeNodeInteger(1));
    }
    
    static void sparqlTest(){
        FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
        Model model = FileManager.get().loadModel("c:/users/simone/documents/netbeansprojects/pr2project/data.rdf");
        
        String queryString =
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
                "SELECT * WHERE { " +
                " ?person foaf:name ?x ." +
                //" FILTER(?x = \"Simone Cusimano\")" +
                "}";
        //SELECT qualsiasi cosa WHERE 'una persona' 'ha un nome' 
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        
        try{
            ResultSet results = qexec.execSelect();
            while ( results.hasNext() ){
                QuerySolution soln = results.nextSolution();
                Literal name = soln.getLiteral("x");
                System.out.println(name);
            }
        } finally {
            qexec.close();
        }
    }
}
