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

import org.eclipse.rwt.engine.Configurator;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;


public class Matcher_Test extends TestCase {
  private static final String KEY = "key";
  private static final String VALUE = "value";
  private static final String KEY_VALUE = "(" + KEY + "=" + VALUE + ")";

  private ServiceReference< HttpService > httpServiceReference;
  private ServiceReference< Configurator > configuratorReference;
  private Matcher matcher;

  public void testMatchesWithoutFilters() {
    boolean matches = matcher.matches();
    
    assertTrue( matches );
  }
  
  public void testMatchesWithNonMatchingConfiguratorFilter() {
    createConfiguratorFilter();

    boolean matches = matcher.matches();

    assertFalse( matches );
  }

  public void testMatchesWithNonMatchingHttpServiceFilter() {
    createHttpServiceFilter();
    
    boolean matches = matcher.matches();
    
    assertFalse( matches );
  }

  public void testMatchesWithNonMatchingFilters() {
    createConfiguratorFilter();
    createHttpServiceFilter();
    
    boolean matches = matcher.matches();
    
    assertFalse( matches );
  }
  
  public void testMatchesWithMatchingConfiguratorFilter() {
    createConfiguratorFilter();
    createConfiguratorProperties();

    boolean matches = matcher.matches();

    assertTrue( matches );
  }

  public void testMatchesWithMatchingHttpServiceFilter() {
    createHttpServiceFilter();
    createHttpServiceProperties();
    
    boolean matches = matcher.matches();
    
    assertTrue( matches );
  }
  
  public void testMatchesWithMatchingConfigurationFilterAndNonMatchingHttpServiceFilter() {
    createConfiguratorFilter();
    createConfiguratorProperties();
    createHttpServiceFilter();
    
    boolean matches = matcher.matches();
    
    assertFalse( matches );
  }

  public void testMatchesWithNonMatchingConfigurationFilterAndMatchingHttpServiceFilter() {
    createConfiguratorFilter();
    createHttpServiceFilter();
    createHttpServiceProperties();
    
    boolean matches = matcher.matches();
    
    assertFalse( matches );
  }

  public void testMatchesWithMatchingFilters() {
    createConfiguratorFilter();
    createConfiguratorProperties();
    createHttpServiceFilter();
    createHttpServiceProperties();
    
    boolean matches = matcher.matches();
    
    assertTrue( matches );
  }
  
  public void testIllegalFilterSyntax() {
    String configuratorKey = Matcher.createTargetKey( Configurator.class );
    when( httpServiceReference.getProperty( configuratorKey ) ).thenReturn( "(((" );

    try {
      matcher.matches();
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testMatchesWithNullConfiguratorReference() {
    matcher = new Matcher( httpServiceReference, null );
    
    boolean matches = matcher.matches();
    
    assertFalse( matches );
  }
  
  public void testMatchesWithNullHttpServiceReference() {
    matcher = new Matcher( null, configuratorReference );
    
    boolean matches = matcher.matches();
    
    assertFalse( matches );
  }
  
  @SuppressWarnings( "unchecked" )
  protected void setUp() {
    httpServiceReference = mock( ServiceReference.class );
    configuratorReference = mock( ServiceReference.class );
    matcher = new Matcher( httpServiceReference, configuratorReference );
  }
  
  private void createHttpServiceFilter() {
    String httpServiceKey = Matcher.createTargetKey( HttpService.class );
    when( configuratorReference.getProperty( httpServiceKey ) ).thenReturn( KEY_VALUE );
  }
  
  private void createConfiguratorFilter() {
    String configuratorKey = Matcher.createTargetKey( Configurator.class );
    when( httpServiceReference.getProperty( configuratorKey ) ).thenReturn( KEY_VALUE );
  }
  
  private void createConfiguratorProperties() {
    when( configuratorReference.getProperty( KEY ) ).thenReturn( VALUE );
  }
  
  private void createHttpServiceProperties() {
    when( httpServiceReference.getProperty( KEY ) ).thenReturn( VALUE );
  }
}