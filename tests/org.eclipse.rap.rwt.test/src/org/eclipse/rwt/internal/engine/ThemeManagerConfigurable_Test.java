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
package org.eclipse.rwt.internal.engine;

import javax.servlet.ServletContext;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.internal.theme.ThemeManagerHolder;


public class ThemeManagerConfigurable_Test extends TestCase {
  private static final String THEME_ID = "TestTheme";
  private static final String STYLE_SHEET = "resources/theme/TestExample.css";
  private static final String SPLIT = RWTServletContextListener.PARAMETER_SPLIT;
  
  private ThemeManagerConfigurable configurable;
  private ApplicationContext applicationContext;

  public void testConfigure() {
    setInitParameter( ( THEME_ID + SPLIT + STYLE_SHEET ) );

    configurable.configure( applicationContext );
    
    assertNotNull( getThemeManager().getTheme( THEME_ID ) );
  }
  
  public void testConfigureWithUnavailableThemeFile() {
    setInitParameter( THEME_ID + SPLIT + "unknown" );
    
    checkIllegalArgumentException();
  }
  
  public void testConfigurationWithMissingSplit() {
    setInitParameter( THEME_ID );
   
    checkIllegalArgumentException();
  }
  
  public void testConfigurationeWithTooManySplits() {
    setInitParameter( THEME_ID  + SPLIT + STYLE_SHEET + SPLIT + "too much" );

    checkIllegalArgumentException();
  }
  
  public void testReset() {
    setInitParameter( ( THEME_ID + SPLIT + STYLE_SHEET ) );
    configurable.configure( applicationContext );
    ThemeManagerHolder themeManager = applicationContext.getThemeManager();

    configurable.reset( applicationContext );
    
    assertNull( themeManager.getInstance().getTheme( THEME_ID ) );
  }
  
  protected void setUp() {
    ServletContext servletContext = Fixture.createServletContext();
    configurable = new ThemeManagerConfigurable( servletContext );
    applicationContext = new ApplicationContext();
  }
  
  protected void tearDown() {
    Fixture.setInitParameter( ThemeManagerConfigurable.THEMES_PARAM, null );
    resetThemeManager();
    Fixture.disposeOfServletContext();
  }

  private void resetThemeManager() {
    if( applicationContext.isActivated() ) {
      applicationContext.getThemeManager().resetInstance();
    }
  }
  
  private void setInitParameter( String value ) {
    Fixture.setInitParameter( ThemeManagerConfigurable.THEMES_PARAM, value );
  }
  
  private void checkIllegalArgumentException() {
    try {
      configurable.configure( applicationContext );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  private ThemeManager getThemeManager() {
    return applicationContext.getThemeManager().getInstance();
  }
}