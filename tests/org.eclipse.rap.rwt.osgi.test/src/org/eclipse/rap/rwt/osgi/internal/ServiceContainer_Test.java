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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.TestCase;

import org.eclipse.rap.rwt.osgi.internal.ServiceContainer.ServiceHolder;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;


public class ServiceContainer_Test extends TestCase {
  private BundleContext bundleContext;
  private ServiceContainer< Object > container;
  private ServiceReference< Object > serviceReference;

  public void testAddService() {
    Object service = new Object();
    
    ServiceHolder< Object > holder1 = container.add( service );
    ServiceHolder< Object > holder2 = container.add( service );

    assertEquals( 1, container.size() );
    assertSame( holder1, holder2 );
    assertSame( service, holder1.getService());
  }
  
  public void testAddServiceReference() {
    Object service = new Object();
    mockServiceReference( service );
    
    ServiceHolder< Object > holder1 = container.add( serviceReference );
    ServiceHolder< Object > holder2 = container.add( serviceReference );
    ServiceHolder< Object > holder3 = container.add( service );
    
    assertEquals( 1, container.size() );
    assertSame( holder1, holder2 );
    assertSame( holder1, holder3 );
    assertSame( serviceReference, holder1.getReference() );
    assertSame( service, holder1.getService());
  }
  
  public void testFind() {
    Object service = new Object();
    container.add( service );

    ServiceHolder< Object > holder = container.find( service );
    
    assertSame( service, holder.getService() );
  }

  public void testContains() {
    Object service = new Object();
    container.add( service );
    
    boolean contains = container.contains( service );
    
    assertTrue( contains );
  }
  
  public void testClear() {
    Object service = new Object();
    container.add( service );
    
    container.clear();
    
    assertEquals( 0, container.size() );
  }
  
  public void testGetServices() {
    Object service = new Object();
    ServiceHolder< Object > holder = container.add( service );
    
    ServiceHolder< Object >[] services = container.getServices();
    
    assertEquals( 1, services.length );
    assertSame( holder.getService(), services[ 0 ].getService() );
  }
  
  public void testRemove() {
    Object service = new Object();
    container.add( service );
    
    container.remove( service );
    
    assertEquals( 0, container.size() );
  }
  
  protected void setUp() {
    bundleContext = mock( BundleContext.class );
    container = new ServiceContainer< Object >( bundleContext );
  }
  
  @SuppressWarnings( "unchecked" )
  private void mockServiceReference( Object service ) {
    serviceReference = mock( ServiceReference.class );
    when( bundleContext.getService( serviceReference ) ).thenReturn( service );
  }
}