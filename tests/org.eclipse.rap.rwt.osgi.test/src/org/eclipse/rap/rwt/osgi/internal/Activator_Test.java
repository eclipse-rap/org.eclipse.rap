/*******************************************************************************
 * Copyright (c) 2011, 2015 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.osgi.internal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.rwt.osgi.ApplicationLauncher;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.*;


public class Activator_Test {

  private BundleContext context;
  private ServiceRegistration<?> serviceRegistration;

  @Before
  public void setUp() {
    mockBundleContext();
  }

  @Test
  public void testStart_opensTrackers() throws Exception {
    new Activator().start( context );

    verify( context, times( 2 ) ).addServiceListener( any( ServiceListener.class ),
                                                      any( String.class ) );
  }

  @Test
  public void testStart_registersApplicationLauncher() throws Exception {
    new Activator().start( context );

    verify( context ).registerService( eq( ApplicationLauncher.class.getName() ),
                                       any( ApplicationLauncherImpl.class ),
                                       isNull() );
  }

  @Test
  public void testStop_closesTrackers() {
    Activator activator = new Activator();
    activator.start( context );

    activator.stop( context );

    verify( context, times( 2 ) ).removeServiceListener( any( ServiceListener.class ) );
  }

  @Test
  public void testStop_unregistersApplicationLauncher() {
    Activator activator = new Activator();
    activator.start( context );

    activator.stop( context );

    verify( serviceRegistration ).unregister();
  }

  private void mockBundleContext() {
    context = mock( BundleContext.class );
    serviceRegistration = mock( ServiceRegistration.class );
    doReturn( serviceRegistration )
      .when( context ).registerService( eq( ApplicationLauncher.class.getName() ),
                                        any( ApplicationLauncherImpl.class ),
                                        isNull() );
  }

}
