package org.eclipse.rap.e4.demo;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.e4.E4ApplicationConfig;
import org.eclipse.rap.e4.E4EntryPointFactory;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.application.Application.OperationMode;
import org.eclipse.rap.rwt.client.WebClient;


public class BasicApplication implements ApplicationConfiguration {

    public void configure(Application application) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(WebClient.PAGE_TITLE, "Hello e4 RAP");
        application.addEntryPoint("/hello", new E4EntryPointFactory(
        		E4ApplicationConfig.create(
        				"platform:/plugin/org.eclipse.rap.e4.demo/Application.e4xmi",
        				"bundleclass://org.eclipse.rap.e4.demo/org.eclipse.rap.e4.demo.lifecycle.LoginLifcecycle"
        			)
        		), properties);
        application.setOperationMode( OperationMode.SWT_COMPATIBILITY );
    }

}
