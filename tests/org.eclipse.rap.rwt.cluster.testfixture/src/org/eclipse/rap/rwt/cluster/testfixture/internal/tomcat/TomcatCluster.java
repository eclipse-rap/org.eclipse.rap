package org.eclipse.rap.rwt.cluster.testfixture.internal.tomcat;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.rap.rwt.cluster.testfixture.internal.server.DelegatingServletEngine;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngineCluster;
import org.eclipse.rwt.lifecycle.IEntryPoint;


public class TomcatCluster implements IServletEngineCluster {
  private final List<DelegatingServletEngine> servletEngines;
  
  public TomcatCluster() {
    servletEngines = new LinkedList<DelegatingServletEngine>();
  }
  
  public IServletEngine addServletEngine() {
    TomcatEngine tomcatEngine = new TomcatEngine();
    DelegatingServletEngine result = new DelegatingServletEngine( tomcatEngine );
    servletEngines.add( result );
    return result;
  }
  
  public void removeServletEngine( IServletEngine servletEngine ) {
    checkBelongsToCluster( servletEngine );
    DelegatingServletEngine delegatingServletEngine = ( DelegatingServletEngine )servletEngine;
    TomcatEngine tomcatEngine = ( TomcatEngine )delegatingServletEngine.getDelegate();
    tomcatEngine.getEngine().setCluster( null );
  }

  public void start( Class<? extends IEntryPoint> entryPointClass ) throws Exception {
    for( DelegatingServletEngine servletEngine : servletEngines ) {
      configureEngine( ( TomcatEngine )servletEngine.getDelegate() );
      servletEngine.start( entryPointClass );
    }
  }

  public void stop() throws Exception {
    for( IServletEngine servletEngine : servletEngines ) {
      servletEngine.stop();
    }
  }

  private static void configureEngine( TomcatEngine servletEngine ) {
    new ClusterConfigurer( servletEngine.getEngine() ).configure();
  }

  private void checkBelongsToCluster( IServletEngine servletEngine ) {
    if( !servletEngines.contains( servletEngine ) ) {
      String msg = "Servlet engine does not belong to cluster: " + servletEngine;
      throw new IllegalArgumentException( msg );
    }
  }
}
