import com.rameses.osiris2.client.InvokerProxy;
import com.rameses.osiris2.client.OsirisContext;
import com.rameses.rcp.framework.ClientContext;
import java.util.HashMap;
import java.util.Map;
/*
 * TestProxy.java
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
public class TestProxy {
    
    private Map map;
    private Map env;
    ClientContext ctx = OsirisContext.getClientContext();
    
    /** Creates a new instance of TestProxy */
    public TestProxy(Map m) {
        map = new HashMap();
        map.put("app.cluster","osiris3");
        map.put("app.host","localhost:8070");
        map.putAll( m );
        if(!map.containsKey("app.host")) throw new RuntimeException("app.host is required");
        if(!map.containsKey("app.context")) throw new RuntimeException("app.context is required");
        ctx.setAppEnv( map );
        ctx.setHeaders(map);
    }

    public Object create(String serviceName) {
        return InvokerProxy.getInstance().create( serviceName );
    }
    
}
