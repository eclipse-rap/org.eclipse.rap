/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.branding;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.branding.Header;
import org.eclipse.rwt.internal.util.ParamCheck;


public class BrandingManager {
  public static final String DEFAULT_SERVLET_NAME = "rap";
  public static final String DEFAULT_BRANDING_ID = "org.eclipse.rap.rwt.branding.default";

  private static final Header[] EMPTY_HEADERS = new Header[ 0 ];
  private static final String[] EMPTY_ENTRY_POINTS = new String[ 0 ];
  
  private static final AbstractBranding DEFAULT_BRANDING = new AbstractBranding() {
    public String getBody() {
      return "";
    }
    public String[] getEntryPoints() {
      return EMPTY_ENTRY_POINTS;
    }
    public String getDefaultEntryPoint() {
      return null;
    }
    public String getFavIcon() {
      return null;
    }
    public Header[] getHeaders() {
      return EMPTY_HEADERS;
    }
    public String getServletName() {
      return BrandingManager.DEFAULT_SERVLET_NAME;
    }
    public String getThemeId() {
      return null;
    }
    public String getId() {
      return BrandingManager.DEFAULT_BRANDING_ID;
    }
    public String getTitle() {
      return "RAP Startup Page";
    }
  };
  
  private final List brandings;

  public BrandingManager() {
    brandings = new LinkedList();
  }

  public void register( final AbstractBranding branding ) {
    ParamCheck.notNull( branding, "branding" );
    synchronized( brandings ) {
      brandings.add( branding );
    }
  }
  
  public void deregister( final AbstractBranding branding ) {
    ParamCheck.notNull( branding, "branding" );
    synchronized( brandings ) {
      brandings.remove( branding );
    }
  }
  
  public void deregisterAll() {
    synchronized( brandings ) {
      brandings.clear();
    }
  }
  
  public AbstractBranding[] getAll() {
    AbstractBranding[] result;
    synchronized( brandings ) {
      result = new AbstractBranding[ brandings.size() ];
      brandings.toArray( result );
    }
    return result;
  }

  public AbstractBranding find( String servletName, String entryPoint ) {
    ParamCheck.notNull( servletName, "servletName" );
    AbstractBranding result = null;
    AbstractBranding[] brandings = getAll();
    for( int i = 0; result == null && i < brandings.length; i++ ) {
      AbstractBranding branding = brandings[ i ];
      if( servletName.equals( branding.getServletName() ) ) {
        if( matches( branding, entryPoint ) ) {
          result = branding;
        }
        if( result == null ) {
          String text = "Entry point ''{0}'' not allowed for branding ''{1}''";
          Object[] args = new Object[]{ entryPoint, branding };
          String msg = MessageFormat.format( text, args );
          throw new IllegalArgumentException( msg );
        }
      }
    }
    if( result == null ) {
      result = BrandingManager.DEFAULT_BRANDING;
    }
    return result;
  }

  private static boolean matches( AbstractBranding branding, String entryPoint ) {
    boolean result = false;
    String defaultEntryPoint = branding.getDefaultEntryPoint();
    if( defaultEntryPoint != null && !"".equals( defaultEntryPoint ) ) {
      if(    entryPoint == null 
          || "".equals( entryPoint ) 
          || defaultEntryPoint.equals( entryPoint ) ) 
      {
        result = true;
      }
    }
    String[] entryPoints = branding.getEntryPoints();
    if( entryPoints != null && entryPoints.length > 0 ) {
      if( search( entryPoints, entryPoint ) != -1 ) {
        result = true;
      }
    } else {
      result = true;
    }
    return result;
  }

  private static int search( String[] strings, String string ) {
    int result = -1;
    // Assume that strings does not contain null value(s)
    if( string != null ) {
      for( int i = 0; result == -1 && i < strings.length; i++ ) {
        if( string.equals( strings[ i ] ) ) {
          result = i;
        }
      }
    }
    return result;
  }
}