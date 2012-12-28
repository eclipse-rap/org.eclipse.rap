/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Appel and others.
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;


public class Matcher_Test {

  private static final String KEY = "key";
  private static final String VALUE = "value";
  private static final String KEY_VALUE = "(" + KEY + "=" + VALUE + ")";

  private ServiceReference<HttpService> httpServiceReference;
  private ServiceReference<ApplicationConfiguration> configuratorReference;
  private Matcher matcher;

  @Before
  @SuppressWarnings( "unchecked" )
  public void setUp() {
    httpServiceReference = mock( ServiceReference.class );
    configuratorReference = mock( ServiceReference.class );
    matcher = new Matcher( httpServiceReference, configuratorReference );
  }

  @Test
  public void testMatchesWithoutFilters() {
    boolean matches = matcher.matches();

    assertTrue( matches );
  }

  @Test
  public void testMatchesWithNonMatchingConfiguratorFilter() {
    createConfiguratorFilter();

    boolean matches = matcher.matches();

    assertFalse( matches );
  }

  @Test
  public void testMatchesWithNonMatchingHttpServiceFilter() {
    createHttpServiceFilter();

    boolean matches = matcher.matches();

    assertFalse( matches );
  }

  @Test
  public void testMatchesWithNonMatchingFilters() {
    createConfiguratorFilter();
    createHttpServiceFilter();

    boolean matches = matcher.matches();

    assertFalse( matches );
  }

  @Test
  public void testMatchesWithMatchingConfiguratorFilter() {
    createConfiguratorFilter();
    createConfiguratorProperties();

    boolean matches = matcher.matches();

    assertTrue( matches );
  }

  @Test
  public void testMatchesWithMatchingHttpServiceFilter() {
    createHttpServiceFilter();
    createHttpServiceProperties();

    boolean matches = matcher.matches();

    assertTrue( matches );
  }

  @Test
  public void testMatchesWithMatchingConfigurationFilterAndNonMatchingHttpServiceFilter() {
    createConfiguratorFilter();
    createConfiguratorProperties();
    createHttpServiceFilter();

    boolean matches = matcher.matches();

    assertFalse( matches );
  }

  @Test
  public void testMatchesWithNonMatchingConfigurationFilterAndMatchingHttpServiceFilter() {
    createConfiguratorFilter();
    createHttpServiceFilter();
    createHttpServiceProperties();

    boolean matches = matcher.matches();

    assertFalse( matches );
  }

  @Test
  public void testMatchesWithMatchingFilters() {
    createConfiguratorFilter();
    createConfiguratorProperties();
    createHttpServiceFilter();
    createHttpServiceProperties();

    boolean matches = matcher.matches();

    assertTrue( matches );
  }

  @Test
  public void testIllegalFilterSyntax() {
    String configuratorKey = Matcher.createTargetKey( ApplicationConfiguration.class );
    when( httpServiceReference.getProperty( configuratorKey ) ).thenReturn( "(((" );

    try {
      matcher.matches();
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testMatchesWithNullConfiguratorReference() {
    matcher = new Matcher( httpServiceReference, null );

    boolean matches = matcher.matches();

    assertFalse( matches );
  }

  @Test
  public void testMatchesWithNullHttpServiceReference() {
    matcher = new Matcher( null, configuratorReference );

    boolean matches = matcher.matches();

    assertFalse( matches );
  }

  private void createHttpServiceFilter() {
    String httpServiceKey = Matcher.createTargetKey( HttpService.class );
    when( configuratorReference.getProperty( httpServiceKey ) ).thenReturn( KEY_VALUE );
  }

  private void createConfiguratorFilter() {
    String configuratorKey = Matcher.createTargetKey( ApplicationConfiguration.class );
    when( httpServiceReference.getProperty( configuratorKey ) ).thenReturn( KEY_VALUE );
  }

  private void createConfiguratorProperties() {
    when( configuratorReference.getProperty( KEY ) ).thenReturn( VALUE );
  }

  private void createHttpServiceProperties() {
    when( httpServiceReference.getProperty( KEY ) ).thenReturn( VALUE );
  }
}
