/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.branding;

import junit.framework.TestCase;

import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.branding.Header;
import org.eclipse.rwt.internal.service.TemplateHolder;
import org.eclipse.swt.RWTFixture;


public class BrandingUtil_Test extends TestCase {
  
  private static class TestBranding extends AbstractBranding {
    String favIcon;
    Header[] headers;
    String exitConfMessage;
    boolean showExitConf;
    public String getBody() {
      return null;
    }
    public boolean showExitConfirmation() {
      return showExitConf;
    }
    public String getExitConfirmationText() {
      return exitConfMessage;
    }
    public String getFavIcon() {
      return favIcon;
    }
    public Header[] getHeaders() {
      return headers;
    }
    public String getServletName() {
      return "W4TDelegate";
    }
    public String getId() {
      return TestBranding.class.getName();
    }
  }
  
  public void testReplacePlaceholder() {
    String templateString = TemplateHolder.VAR_ENTRY_POINT.toString();
    TemplateHolder template = new TemplateHolder( templateString );
    
    BrandingUtil.replacePlaceholder( template,
                                     TemplateHolder.VAR_ENTRY_POINT,
                                     "replacement" );
    assertEquals( "replacement", getTemplateContent( template ).toString() );
    
    template.reset();
    BrandingUtil.replacePlaceholder( template,
                                     TemplateHolder.VAR_ENTRY_POINT,
                                     null );    
    assertEquals( "", getTemplateContent( template ).toString() );
  }

  private StringBuffer getTemplateContent( final TemplateHolder template ) {
    String[] tokens = template.getTokens();
    StringBuffer result = new StringBuffer();
    for( int i = 0; i < tokens.length; i++ ) {
      result.append( tokens[ i ] );
    }
    return result;
  }
  
  public void testHeaderMarkup() {
    String expected;
    String markup;
    TestBranding branding = new TestBranding();
    // No fav icon
    branding.favIcon = null;
    markup = BrandingUtil.headerMarkup( branding );
    assertEquals( "", markup );
    branding.favIcon = "";
    markup = BrandingUtil.headerMarkup( branding );
    assertEquals( "", markup );
    // Ordinary fav icon
    branding.favIcon = "my/fav/icon.ico";
    markup = BrandingUtil.headerMarkup( branding );
    expected 
      = "<link rel=\"shortcut icon\" " 
      + "type=\"image/x-icon\" " 
      + "href=\"my/fav/icon.ico\" />\n"; 
    assertEquals( expected, markup );
    // Some header without attributes
    branding.favIcon = null;
    branding.headers = new Header[] {
      new Header( "meta", new String[ 0 ], new String[ 0 ] )
    };
    markup = BrandingUtil.headerMarkup( branding );
    assertEquals( "<meta />\n", markup );
    // Some header with attributes
    branding.favIcon = null;
    branding.headers = new Header[] {
      new Header( "meta", new String[] { "name" }, new String[] { "value" } )
    };
    markup = BrandingUtil.headerMarkup( branding );
    assertEquals( "<meta name=\"value\" />\n", markup );
    // Header with attributes that have a null-name or-value: will be ignored
    branding.favIcon = null;
    branding.headers = new Header[] {
      new Header( "meta", 
                  new String[] { null, "name", null }, 
                  new String[] { null, null, "value" } )
    };
    markup = BrandingUtil.headerMarkup( branding );
    assertEquals( "<meta />\n", markup );
    // fav icon and header
    branding.favIcon = "my/fav/icon.ico";
    branding.headers = new Header[] {
      new Header( "meta", new String[] { "name" }, new String[] { "value" } )
    };
    markup = BrandingUtil.headerMarkup( branding );
    expected 
      = "<link href=\"my/fav/icon.ico\" " 
      + "type=\"image/x-icon\" " 
      + "rel=\"shortcut icon\" />\n"
      + "<meta name=\"value\" />\n";
  }
  
  public void testExitMessageScript() {
    String script;
    TestBranding branding = new TestBranding();
    assertEquals( "", BrandingUtil.exitMessageScript( branding ) );
    branding.exitConfMessage = "";
    assertEquals( "", BrandingUtil.exitMessageScript( branding ) );
    branding.exitConfMessage = "want to exit?";
    assertEquals( "", BrandingUtil.exitMessageScript( branding ) );
    branding.showExitConf = true;
    script = BrandingUtil.exitMessageScript( branding );
    assertTrue( script.indexOf( "want to exit?" ) != -1 );
    branding.exitConfMessage = "\"\n";
    script = BrandingUtil.exitMessageScript( branding );
    assertEquals( "app.setExitConfirmation( \"\\\"\\n\" );", script );
  }
  
  public void testGetCurrentBrandingId1() {
    BrandingUtil.findBranding();
    String currentBrandingId =  BrandingUtil.getCurrentBrandingId();
    assertEquals( BrandingManager.DEFAULT_BRANDING_ID, currentBrandingId );
  }
  
  public void testGetCurrentBrandingId2() {
    TestBranding branding = new TestBranding();
    BrandingManager.register( branding );
    try {
      BrandingUtil.findBranding();
      String currentBrandingId =  BrandingUtil.getCurrentBrandingId();
      assertEquals( branding.getId(), currentBrandingId );
    } finally {
      BrandingManager.deregister( branding );
    }
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
