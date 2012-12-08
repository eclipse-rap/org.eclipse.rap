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
package org.eclipse.rap.rwt.internal.protocol;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class StylesUtil_Test extends TestCase {
  
  private Shell shell;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    Display display = new Display();
    shell = new Shell( display );
  }
  
  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testHasOneStyle() {
    Button button = new Button( shell, SWT.PUSH );
    
    String[] styles = StylesUtil.filterStyles( button, new String[] { "PUSH" } );
    
    assertEquals( 1, styles.length );
    assertEquals( "PUSH", styles[ 0 ] );
  }
  
  public void testHasTwoStyles() {
    Button button = new Button( shell, SWT.PUSH  | SWT.BORDER );
    
    String[] styles = StylesUtil.filterStyles( button, new String[] { "PUSH", "BORDER" } );
    
    assertEquals( 2, styles.length );
    assertEquals( "PUSH", styles[ 0 ] );
    assertEquals( "BORDER", styles[ 1 ] );
  }
  
  public void testHasOnlyOneOfTwoPossibleStyles() {
    Button button = new Button( shell, SWT.PUSH  );
    
    String[] styles = StylesUtil.filterStyles( button, new String[] { "PUSH", "BORDER" } );
    
    assertEquals( 1, styles.length );
    assertEquals( "PUSH", styles[ 0 ] );
  }
  
  public void testHasNoAllowedStyles() {
    Button button = new Button( shell, SWT.NONE  );
    
    String[] styles = StylesUtil.filterStyles( button, new String[] {} );
    
    assertEquals( 1, styles.length );
    assertEquals( "NONE", styles[ 0 ] );
  }
  
  public void testNoneStyles() {
    Composite composite = new Composite( shell, SWT.NONE  );
    
    String[] styles = StylesUtil.filterStyles( composite, new String[] { "NO_RADIO_GROUP" } );
    
    assertEquals( 1, styles.length );
    assertEquals( "NONE", styles[ 0 ] );
  }
  
  public void testWithNonExistingStyle() {
    Button button = new Button( shell, SWT.NONE  );
    
    try {
      StylesUtil.filterStyles( button, new String[] { "FOO" } );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
}
