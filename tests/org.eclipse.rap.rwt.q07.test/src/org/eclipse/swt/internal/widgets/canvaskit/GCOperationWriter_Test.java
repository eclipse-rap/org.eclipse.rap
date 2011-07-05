/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.canvaskit;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;

public class GCOperationWriter_Test extends TestCase {

  public void testProcessTextWithTabDelimiterMnemonicFlag() {
    String text = "text with \ttab, \nnew line and &mnemonic";
    
    int flags = SWT.DRAW_TAB | SWT.DRAW_DELIMITER | SWT.DRAW_MNEMONIC;
    String result = GCOperationWriter.processText( text, flags );
    
    String expected = "text with &nbsp;&nbsp;&nbsp;&nbsp;tab, <br/>new line and mnemonic";
    assertEquals( expected, result );
  }
  
  public void testProcessTextWithTabDelimiterFlag() {
    String text = "text with \ttab, \nnew line and &mnemonic";
    
    String result = GCOperationWriter.processText( text, SWT.DRAW_TAB | SWT.DRAW_DELIMITER );
    
    String expected = "text with &nbsp;&nbsp;&nbsp;&nbsp;tab, <br/>new line and &amp;mnemonic";
    assertEquals( expected, result );
  }
  
  public void testProcessTextWithTabFlag() {
    String text = "text with \ttab, \nnew line and &mnemonic";
    
    String result = GCOperationWriter.processText( text, SWT.DRAW_TAB );
    
    String expected = "text with &nbsp;&nbsp;&nbsp;&nbsp;tab, new line and &amp;mnemonic";
    assertEquals( expected, result );
  }
  
  public void testProcessTextWithNoFlag() {
    String text = "text with \ttab, \nnew line and &mnemonic";
    
    String result = GCOperationWriter.processText( text, SWT.NONE );
    
    String expected = "text with tab, new line and &amp;mnemonic";
    assertEquals( expected, result );
  }

  public void testProcessTextWithDrawMnemonic() {
    String text = "text without mnemonic";
    String expected = text;
    String result = GCOperationWriter.processText( text, SWT.DRAW_MNEMONIC );
    assertEquals( expected, result );

    text = "text with &mnemonic";
    expected = "text with mnemonic";
    result = GCOperationWriter.processText( text, SWT.DRAW_MNEMONIC );
    assertEquals( expected, result );

    expected = "text with &amp;mnemonic";
    result = GCOperationWriter.processText( text, SWT.NONE );
    assertEquals( expected, result );

    text = "text with &&mnemonic";
    expected = "text with &amp;mnemonic";
    result = GCOperationWriter.processText( text, SWT.DRAW_MNEMONIC );
    assertEquals( expected, result );

    expected = "text with &amp;&amp;mnemonic";
    result = GCOperationWriter.processText( text, SWT.NONE );
    assertEquals( expected, result );
  }
}
