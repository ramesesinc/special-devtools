import com.rameses.service.ScriptServiceContext;
import com.rameses.service.ServiceProxy;
import com.rameses.service.ServiceProxyInvocationHandler;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import java.util.HashMap;
import java.util.Map;
/*
 * TestScriptProxy.java
 *
 * Created on June 20, 2014, 9:57 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author Elmo
 */
public class TestScriptProxy {
    
    private Map env;
    private ScriptServiceContext ctx; 
    private GroovyClassLoader loader;
    
    /** Creates a new instance of TestProxy */
    public TestScriptProxy(Map m) {
        env = new HashMap();
        env.put("app.cluster","osiris3");
        env.put("app.host","localhost:8070");
        env.putAll( m );
        if(!env.containsKey("app.host")) throw new RuntimeException("app.host is required");
        if(!env.containsKey("app.context")) throw new RuntimeException("app.context is required");
        
        ctx = new ScriptServiceContext(env); 
        loader = new GroovyClassLoader(TestScriptProxy.class.getClassLoader());
    }

    public Object create(String serviceName) throws Exception { 
        StringBuilder builder = new StringBuilder();
        builder.append( "public class MyMetaClass  { \n" );
        builder.append( "    def invoker; \n");
        builder.append( "    public Object invokeMethod(String string, Object args) { \n");
        builder.append( "        return invoker.invokeMethod(string, args); \n" );
        builder.append( "    } \n");
        builder.append(" } ");
        Class metaClass = loader.parseClass( builder.toString() );       
        
        ServiceProxy sp = ctx.create( serviceName, env );
        ServiceProxyInvocationHandler si = new ServiceProxyInvocationHandler(sp);
        Object obj = metaClass.newInstance();
        ((GroovyObject)obj).setProperty( "invoker", si );
        return obj;         
    }
    
}
