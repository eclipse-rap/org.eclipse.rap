/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.browser;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.widgets.IBrowserAdapter;
import org.eclipse.rap.rwt.widgets.Composite;


/**
 * <p>Currently implemented</p>
 * <ul><li>text and url property</li></ul>
 * <p>The enabled property in not (yet) evaluated.</p>
 */
public class Browser extends Composite {

  private static final String ABOUT_BLANK = "about:blank";
  
  private String url;
  private String html;

  private final IBrowserAdapter browserAdapter = new BrowserAdapter();

  /**
   * <p>The <code>style</code> flag is not yet evaluated.</p>
   */
  public Browser( final Composite composite, final int style ) {
    super( composite, style );
  }
  
  public boolean setUrl( final String url ) {
    checkWidget();
    if( url == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    LocationEvent event;
    event = new LocationEvent( this, LocationEvent.CHANGING, url );
    event.processEvent();
    boolean result = event.doit;
    if( result ) {
      this.url = url;
      html = null;
      event = new LocationEvent( this, LocationEvent.CHANGED, url );
      event.processEvent();
    }
    return result;
  }
  
  public String getUrl() {
    checkWidget();
    return url;
  }

  public boolean setText( final String html ) {
    checkWidget();
    if( html == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    LocationEvent event;
    event = new LocationEvent( this, LocationEvent.CHANGING, ABOUT_BLANK );
    event.processEvent();
    boolean result = event.doit;
    if( result ) {
      this.html = html;
      url = null;
      event = new LocationEvent( this, LocationEvent.CHANGED, ABOUT_BLANK );
      event.processEvent();
    }
    return result;
  }

  public void addLocationListener( final LocationListener listener ) {
    LocationEvent.addListener( this, listener );
  }
  
  public void removeLocationListener( final LocationListener listener ) {
    LocationEvent.removeListener( this, listener );
  }
  
  public Object getAdapter( Class adapter ) {
    Object result;
    if( IBrowserAdapter.class.equals( adapter ) ) {
      result = browserAdapter;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }

  private final class BrowserAdapter implements IBrowserAdapter {
    public String getText() {
      return Browser.this.html;
    }
  }
}
