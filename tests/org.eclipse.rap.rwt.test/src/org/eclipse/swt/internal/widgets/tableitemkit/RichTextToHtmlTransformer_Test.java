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
package org.eclipse.swt.internal.widgets.tableitemkit;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.RichTextParser;
import org.eclipse.swt.internal.widgets.RichTextParserException;
import org.eclipse.swt.widgets.*;


public class RichTextToHtmlTransformer_Test extends TestCase {

  private RichTextToHtmlTransformer transformer;

  public void testParseWithEmptyText() {
    RichTextParser parser = new RichTextParser( transformer );
    
    parser.parse( "<html></html>" );
    
    assertEquals( "<div style=\"line-height:normal;\"></div>", transformer.getHtml() );
  }

  public void testParseWithReservedXmlEntities() {
    RichTextParser parser = new RichTextParser( transformer );
    try {
      parser.parse( "<html>3 <> 2 & \"</html>" );
    } catch( RichTextParserException expected ) {
    }
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableItem tableItem = new TableItem( table, SWT.NONE );
    transformer = new RichTextToHtmlTransformer( tableItem );
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
