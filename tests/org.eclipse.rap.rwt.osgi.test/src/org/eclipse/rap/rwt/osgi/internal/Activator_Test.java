/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.osgi.internal;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Dictionary;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.osgi.ApplicationLauncher;
import org.osgi.framework.*;


public class Activator_Test extends TestCase {
  
  private BundleContext context;
  private ServiceRegistration serviceRegistration;

  public void testStart() throws Exception {
    new Activator().start( context );
    
    checkTrackersHaveBeenOpend();
    checkApplicationLauncherHasBeenRegistered();
  }
  
  public void testStop() {
    Activator activator = new Activator();
    activator.start( context );
    
    activator.stop( context );
    
    checkTrackersHaveBeenClosed();
    checkApplicationLauncherHasBeenUnregistered();
  }
  protected void setUp() {
    mockBundleContext();
  }

  private void checkApplicationLauncherHasBeenUnregistered() {
    verify( serviceRegistration ).unregister();
  }

  private void checkTrackersHaveBeenClosed() {
    verify( context, times( 2 ) ).removeServiceListener( any( ServiceListener.class ) );
  }
  
  @SuppressWarnings( "unchecked" )
  private void checkApplicationLauncherHasBeenRegistered() {
    verify( context )
      .registerService( eq( ApplicationLauncher.class.getName() ),
                        any( ApplicationLauncherImpl.class ), 
                        any( Dictionary.class ) );
  }

  private void checkTrackersHaveBeenOpend() throws Exception {
    verify( context, times( 2 ) )
      .addServiceListener( any( ServiceListener.class ), any( String.class ) );
  }

  @SuppressWarnings( "unchecked" )
  private void mockBundleContext() {
    context = mock( BundleContext.class );
    serviceRegistration = mock( ServiceRegistration.class );
    when( context.registerService( eq( ApplicationLauncher.class.getName() ),
                                   any( ApplicationLauncherImpl.class ),
                                   any( Dictionary.class ) ) ).thenReturn( serviceRegistration );
  }
}