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
package org.eclipse.swt.internal.widgets;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import junit.framework.TestCase;

import org.mockito.Mockito;


public class RichTextParser_Test extends TestCase {
  
  private IRichTextParserCallback callback;
  
  public void testIsRichTextWithEmptyString() {
    assertFalse( RichTextParser.isRichText( "" ) );
  }

  public void testIsRichTextWithSimpleString() {
    assertFalse( RichTextParser.isRichText( "foo" ) );
  }
  
  public void testIsRichTextWithHtmlString() {
    assertTrue( RichTextParser.isRichText( "<html>..." ) );
  }
  
  public void testParseWithInvalidText() {
    RichTextParser parser = new RichTextParser( callback );
    
    try {
      parser.parse( "foo" );
      fail();
    } catch( RichTextParserException expected ) {
    }
  }
  
  public void testParseWithEmptyText() {
    RichTextParser parser = new RichTextParser( callback );
    
    parser.parse( "<html></html>" );
    
    verify( callback ).beginHtml();
    verify( callback ).endHtml();
    Mockito.verifyNoMoreInteractions( callback );
  }
  
  public void testParseWithLineBreak() {
    RichTextParser parser = new RichTextParser( callback );
    
    parser.parse( "<html><br/></html>" );
    
    verify( callback ).beginHtml();
    verify( callback ).lineBreak();
    verify( callback ).endHtml();
    Mockito.verifyNoMoreInteractions( callback );
  }
  
  public void testParseWithParagraphAndLineBreak() {
    RichTextParser parser = new RichTextParser( callback );
    
    parser.parse( "<html>a text<br/>with a line break.</html>" );

    verify( callback ).beginHtml();
    verify( callback ).text( "a text" );
    verify( callback ).lineBreak();
    verify( callback ).text( "with a line break." );
    verify( callback ).endHtml();
    Mockito.verifyNoMoreInteractions( callback );
  }
  
  public void testParseWithFont() {
    RichTextParser parser = new RichTextParser( callback );
    
    parser.parse( "<html><font name='font-name' height='12'>text</font></html>" );
    
    verify( callback ).beginHtml();
    verify( callback ).beginFont( "font-name", 12 );
    verify( callback ).text( "text" );
    verify( callback ).endFont();
    verify( callback ).endHtml();
    Mockito.verifyNoMoreInteractions( callback );
  }
  
  public void testParseWithImage() {
    RichTextParser parser = new RichTextParser( callback );
    
    parser.parse( "<html><img src='foo.png'/>some text</html>" );
    
    verify( callback ).beginHtml();
    verify( callback ).image( "foo.png" );
    verify( callback ).text( "some text" );
    verify( callback ).endHtml();
    Mockito.verifyNoMoreInteractions( callback );
  }
  
  public void testParseWithInvalidXml() {
    RichTextParser parser = new RichTextParser( callback );
    
    try {
      parser.parse( "<html>" );
      fail();
    } catch( RichTextParserException expected ) {
    }
  }
  
  public void testParseWithUnsupportedElement() {
    RichTextParser parser = new RichTextParser( callback );
    
    try {
      parser.parse( "<html><foo>some text</foo></html>" );
      fail();
    } catch( RichTextParserException expected ) {
    }
  }
  
  @Override
  protected void setUp() throws Exception {
    callback = mock( IRichTextParserCallback.class );
  }
}
