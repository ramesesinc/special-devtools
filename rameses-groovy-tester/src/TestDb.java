
import com.rameses.osiris3.data.MockConnectionManager;
import com.rameses.osiris3.persistence.EntityManager;
import com.rameses.osiris3.schema.SchemaManager;
import com.rameses.osiris3.schema.SchemaResourceProvider;
import com.rameses.osiris3.sql.SimpleDataSource;
import com.rameses.osiris3.sql.SqlContext;
import com.rameses.osiris3.sql.SqlManager;
import com.rameses.osiris3.sql.SqlUnitCache;
import com.rameses.sql.dialect.MsSqlDialect;
import com.rameses.sql.dialect.MySqlDialect;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author dell
 */
public class TestDb {

    private SchemaManager schemaManager;
    private MockConnectionManager cm;
    private SqlManager sqlManager;
    private SqlContext sqlContext;

    /**
     * Pass the ff:
     * dialect
     * driverClass
     * url
     * user
     * pwd
     */
    public TestDb(Map params) {
        schemaManager = SchemaManager.getInstance();
        cm = new MockConnectionManager();
        sqlManager = SqlManager.getInstance();
        
        String dialect = (String)params.get("dialect");
        String url = (String)params.get("url");
        String user = (String)params.get("user");
        String pwd = (String)params.get("pwd");
        
        if( dialect.equals("mysql")) {
            SimpleDataSource ds = new SimpleDataSource("com.mysql.jdbc.Driver", url, user, pwd);
            sqlContext = sqlManager.createContext(cm.getConnection("main", ds));
            sqlContext.setDialect(new MySqlDialect());
        }
        else if( dialect.equals("mssql") ) {
            //SQL SERVER
            SimpleDataSource ds = new SimpleDataSource("com.microsoft.sqlserver.jdbc.SQLServerDriver", url, user, pwd);
            sqlContext = sqlManager.createContext(cm.getConnection("main", ds));
            sqlContext.setDialect(new MsSqlDialect());
        }        
        else {
            throw new RuntimeException("dialect " + dialect + " not found");
        }
    }

    public void setPath( String dir ) {
        schemaManager.getConf().setResourceProvider( new MyResourceProvider(dir) );
    }

    public EntityManager lookup(String schemaName) {
        EntityManager em = new EntityManager(schemaManager, sqlContext, schemaName);
        em.setDebug(true);
        return em;
    }
    
    public void commit() {
        cm.commit();
        cm.close();
    }
    
    public void rollback() {
        cm.rollback();
    }
    
    public void clearCache() {
        schemaManager.getCache().clear();
        SqlUnitCache.clear(); 
    }
    
    public static class MyResourceProvider implements SchemaResourceProvider {
        private String homeDir;
        private Map<String, File> map = new HashMap();
        public MyResourceProvider(String d) {
            homeDir = d;
        }
        public InputStream getResource(String name) {
            if( !map.containsKey(name) ) {
                String n = homeDir+"/"+name+".xml";
                File f = new File(n);
                if(!f.exists()) {
                    throw new RuntimeException("File " + n + " does not exist");
                }
                map.put( name, f  );
            }
            File f = map.get(name);
            try {
                return new FileInputStream(f); 
            }
            catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    
}
