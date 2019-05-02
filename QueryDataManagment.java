import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 *
 * @author Adam Stogsdill
 *
 */
public class QueryDataManagment {


    private ArrayList<QueryConversionData> data = new ArrayList<QueryConversionData>();
    private ArrayList<String> queryHistory = new ArrayList<String>();


    /**
     * CreateQuery is a method that adds a Query to the QueryDataManagment "data" ArrayList which holds all the
     * QueryConversionData Classes. Then also adds the Query to a "queryHistory" ArrayList which can be called later
     * in if the user chooses to use one of the CallQuery methods.
     * @param rs, required ResultSet that will be called and created into a QueryConversionData Class
     * @param Query, String that is added to the "queryHistory" ArrayList for future use
     * @throws SQLException
     */
    public void createQuery(ResultSet rs, String Query) throws SQLException {
        this.data.add(new QueryConversionData(rs));
        this.queryHistory.add(Query);
    }


    /**
     * CallQuery is a method that takes in an integer from the "data" ArrayList directly which means that this method
     * is faster than others in its family and is useful when the user knows the array index of the query that they want
     * to call once more.
     * @param a, index of the Query that you want from the "data" ArrayList
     * @throws SQLException
     */
    public void callQuery(int a) throws SQLException{
        data.get(a).OUPUTQUERY(data.get(a).getConversionArray(), data.get(a).getResultSet());
    }


    /**
     * CallQuery is a method that takes in a string from the "queryHistory" ArrayList and executed the query that we basically
     * have cached without having the user call the server for the information again.
     * @param query, the Query that was used to
     * @throws SQLException
     */
    public void callQuery(String query) throws SQLException {
        for(int i = 0; i < queryHistory.size(); i++) {
            if(queryHistory.get(i).equalsIgnoreCase(query))
                data.get(i).OUPUTQUERY(data.get(i).getConversionArray(), data.get(i).getResultSet());
        }
    }


    public QueryConversionData getQuery(int a) throws SQLException{
        return data.get(a);
    }


    public QueryConversionData getQuery(String query) throws SQLException {
        for(int i = 0; i < queryHistory.size(); i++) {
            if(queryHistory.get(i).equalsIgnoreCase(query))
                return data.get(i);
        }
        return null;
    }

    public int locationInHistory(String q){
        for(int i = 0; i < queryHistory.size(); i++){
            if(queryHistory.get(i).equals(q))
                return i;
        }
        return -1;
    }

    public int getSize(){
        return this.data.size() - 1;
    }
}
