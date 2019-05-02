import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author Adam Stogsdill
 *
 */
public class QueryConversionData {

    private int[] conversion_array;
    private ResultSet information;
    private String[] column_names;
    private ArrayList<Object[]> query_hold = new ArrayList<Object[]>();
    private ArrayList<Object> list = new ArrayList<Object>();


    public QueryConversionData(ResultSet a) throws SQLException {
        conversion_array = new int[a.getMetaData().getColumnCount()];
        column_names = new String[a.getMetaData().getColumnCount()];
        information = a;
        this.automaticParse(a);
    }


    /**
     * AutomaticParse is a method that takes in the ResultSet from the Query and creates the
     * conversion array and outputs information alongside the process. First it prints the MetaData
     * information of each column of the query. Then sends this to a method to automatically output the
     * information into the console.
     * @param a
     * @throws SQLException
     */
    public void automaticParse(ResultSet a) throws SQLException {
        int num_of_columns = a.getMetaData().getColumnCount();
        System.out.println("Number_Of_Columns: " + num_of_columns);

        int[] conversion = new int[num_of_columns];
        for (int x = 1; x <= num_of_columns; x++) {
            conversion[x - 1] = a.getMetaData().getColumnType(x);
            column_names[x-1] = a.getMetaData().getColumnName(x);
            System.out.println(a.getMetaData().getColumnName(x) + "  TYPE: " + a.getMetaData().getColumnType(x));
        }
        //System.out.println();
        if(num_of_columns == 1){
            while(a.next()){
                list.add(a.getString(1));
            }
        }
        else { OUPUTQUERY(conversion, a); }
    }

    /**
     * OUTPUTQUERY is a method that automatically outputs the query to the console
     * @param conversion_array, array that is required to be parsed to know how to convert data.
     * @param a, ResultSet that is pertaining to the object
     * @throws SQLException
     */
    public void OUPUTQUERY(int[] conversion_array, ResultSet a) throws SQLException {
        String result = "";
        this.conversion_array = conversion_array;
        while (a.next()) {
            Object[] obj = new Object[conversion_array.length];
            for (int i = 0; i < conversion_array.length; i++) {
                switch(conversion_array[i]) {
                    case 12:
                        String string = a.getString(i+1);
                        obj[i] = string;
                        result += string + " ";
                        break;
                    case 4:
                        int integer = a.getInt(i+1);
                        obj[i] = integer;
                        result += integer + " ";
                        break;
                }
            }
            this.query_hold.add(obj);
            result += "\n";
        }
        //System.out.println(result);
    }




    public int[]     getConversionArray() 		{ return this.conversion_array; }
    public ResultSet getResultSet() 	  		{ return this.information;      }
    public String[]  getColumnName()      		{ return this.column_names;     }
    public ArrayList<Object[]> getObjectArray()	{ return this.query_hold;		}
    public Object[] getObjectList()             { return this.list.toArray();   }







}
