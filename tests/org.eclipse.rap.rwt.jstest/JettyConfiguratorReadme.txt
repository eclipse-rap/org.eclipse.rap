1. Run the 'Jetty Configurator' launch configuration.
2. All resources of the bundles:
   * 'org.eclipse.rap.rwt.q07.jstest' are accessible from 
     http://127.0.0.1:8081/org.eclipse.rap.rwt.q07.jstest/*
   * 'org.eclipse.rap.rwt.q07' are accessible from 
     http://127.0.0.1:8081/org.eclipse.rap.rwt.q07/*
3. The URL to execute the tests is printed to the console. Enter it in your browser to start the 
   tests.

If you want to change the port open the 'Jetty Configurator' launch configuration and edit the VM 
argument 'Dorg.osgi.service.http.port'.