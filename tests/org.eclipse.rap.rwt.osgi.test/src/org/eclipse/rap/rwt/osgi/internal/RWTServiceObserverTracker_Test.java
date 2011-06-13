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
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import junit.framework.TestCase;

import org.eclipse.rap.rwt.osgi.RWTServiceObserver;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.*;


public class RWTServiceObserverTracker_Test extends TestCase {

  private BundleContext bundleContext;
  private RWTServiceImpl rwtService;
  private ServiceReference< RWTServiceObserver> serviceReference;
  private RWTServiceObserver service;
  private RWTServiceObserverTracker tracker;

  public void testAddObserver() {
    tracker.addingService( serviceReference );
    
    verify( rwtService ).addObserver( service );
  }
  
  public void testRemoveObserver() {
    tracker.removedService( serviceReference, service );
    
    verify( rwtService ).removeObserver( service );
  }
  
  public void testOpen() {
    tracker.open();
    
    verify( rwtService ).addObserver( service );
  }
  
  @SuppressWarnings( "unchecked" )
  protected void setUp() throws Exception {
    mockBundleContext();
    rwtService = mock( RWTServiceImpl.class );
    tracker = new RWTServiceObserverTracker( bundleContext, rwtService );
    serviceReference = mock( ServiceReference.class );
    mockObserverService();
  }

  private void mockObserverService() {
    service = mock( RWTServiceObserver.class );
    when( bundleContext.getService( serviceReference ) ).thenReturn( service );
  }

  private void mockBundleContext() throws InvalidSyntaxException {
    bundleContext = mock( BundleContext.class );
    doAnswer( createServiceRegistrationTrigger() )
     .when( bundleContext ).addServiceListener( any( ServiceListener.class ), any( String.class ) );
  }

  private Answer createServiceRegistrationTrigger() {
    return new Answer() {
      public Object answer( InvocationOnMock invocation ) throws Throwable {
        triggerServiceRegistration( invocation );
        return null;
      }
    };
  }

  private void triggerServiceRegistration( InvocationOnMock invocation ) {
    ServiceListener listener = ( ServiceListener )invocation.getArguments()[ 0 ];
    listener.serviceChanged( new ServiceEvent( ServiceEvent.REGISTERED, serviceReference ) );
  }
}