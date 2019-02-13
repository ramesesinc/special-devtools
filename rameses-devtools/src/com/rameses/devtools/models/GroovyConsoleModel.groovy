package com.rameses.devtools.models;

import com.rameses.rcp.common.*
import com.rameses.rcp.annotations.*
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*
import java.rmi.server.*;
import com.rameses.seti2.models.*;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.codehaus.groovy.runtime.InvokerHelper;

public class GroovyConsoleModel {

    @Binding
    def binding;
    
    def textArea;
    def errs;
    def selectedItem;
    def items = [];
    def cols = [];
    
    def listModel = [
        getColumnList: {
            return cols;
        },
        fetchList: {
            return items;
        }
    ] as BasicListModel;
    
    def svcProvider = [ 
        create: {svcName ->
            return  InvokerProxy.getInstance().create( svcName );
        },
        lookup: {svcName ->
            return  InvokerProxy.getInstance().create( svcName );
        },
    ];
    
    void exec() {
        def m = [:];
        cols.clear();
        items.clear();
        errs = null;
        try {
            GroovyShell shell = new GroovyShell();
            def script = shell.parse(textArea);
            groovy.lang.Binding bnd = new groovy.lang.Binding();
            bnd.setVariable( "SERVICE", svcProvider );
            Script s = InvokerHelper.createScript( script.getClass(), bnd);
            def result = s.run();
            
            if( result instanceof List  ) {
                if( result ) {
                    def r = result[0];
                    r.each { k,v->
                        cols << [name: k, caption:k];
                    }
                    items = result;
                }
            }
            else if( result instanceof Map ) {
                cols << [name: 'key', caption:'Key', width:200];
                cols << [name: 'value', caption:'Value'];
                result.each { k,v->
                    items << [key: k, value: v ];
                }
            }
            else {
                cols << [name: 'result', caption:'Result'];
                if( result !=null ) items << [result: result];
            }            
        }
        catch(e) {
            errs = e.message;
        }
        listModel.reloadAll();
        binding.refresh();
    }
    
    void clear() {
        textArea = null;
        binding.refresh();
    }
}