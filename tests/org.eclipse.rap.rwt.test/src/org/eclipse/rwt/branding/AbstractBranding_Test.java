/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.branding;

import junit.framework.TestCase;


public class AbstractBranding_Test extends TestCase {
  
  public void testDefaultValues() {
    AbstractBranding branding = new AbstractBranding() {
    };
    
    assertNull( branding.getServletName() );
    assertNull( branding.getDefaultEntryPoint() );
    assertNull( branding.getEntryPoints() );
    assertNull( branding.getThemeId() );
    assertNull( branding.getId() );
    assertNull( branding.getFavIcon() );
    assertNull( branding.getHeaders() );
    assertNull( branding.getBody() );
    assertFalse( branding.showExitConfirmation() );
    assertEquals( "", branding.getExitConfirmationText() );
  }
}
