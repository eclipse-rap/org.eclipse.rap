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
package org.eclipse.rwt.internal.theme;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;


public class ThemeManagerHolder_Test extends TestCase {
  
  public void testActivateAndDeactivate() {
    ThemeManagerHolder themeManagerHolder = new ThemeManagerHolder();
    
    Theme beforeActivate = getTheme( themeManagerHolder );
    themeManagerHolder.activate();
    Theme afterActivate = getTheme( themeManagerHolder );
    themeManagerHolder.deactivate();
    Theme afterDeactivate = getTheme( themeManagerHolder );
    
    assertNull( beforeActivate );
    assertNotNull( afterActivate );
    assertSame( themeManagerHolder.getInstance(), themeManagerHolder.getInstance() );
    assertNull( afterDeactivate );
  }

  protected void setUp() {
    Fixture.setUp();
  }
  
  protected void tearDown() {
    Fixture.tearDown();
  }
  
  private Theme getTheme( ThemeManagerHolder themeManagerHolder ) {
    return themeManagerHolder.getInstance().getTheme( ThemeManager.DEFAULT_THEME_ID );
  }
}