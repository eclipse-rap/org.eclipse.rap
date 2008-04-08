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
package org.eclipse.swt.internal.widgets;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.internal.lifecycle.IRenderRunnable;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;

public final class WidgetAdapter implements IWidgetAdapter {

  private final String id;
  private boolean initialized;
  private final Map preservedValues;
  private String jsParent;
  private IRenderRunnable renderRunnable;
  private String variant;

  public WidgetAdapter() {
    this( IdGenerator.getInstance().newId() );
  }

  public WidgetAdapter( final String id ) {
    this.id = id;
    preservedValues = new HashMap();
  }

  public String getId() {
    return id;
  }

  public boolean isInitialized() {
    return initialized;
  }

  public void setInitialized( final boolean initialized ) {
    this.initialized = initialized;
  }

  public void preserve( final String propertyName, final Object value ) {
    preservedValues.put( propertyName, value );
  }

  public Object getPreserved( final String propertyName ) {
    return preservedValues.get( propertyName );
  }

  public void clearPreserved() {
    preservedValues.clear();
  }

  public String getJSParent() {
    return jsParent;
  }

  public void setJSParent( final String jsParent ) {
    this.jsParent = jsParent;
  }

  public void setRenderRunnable( final IRenderRunnable renderRunnable ) {
    if( this.renderRunnable != null ) {
      throw new IllegalStateException( "A renderRunnable was already set." );
    }
    this.renderRunnable = renderRunnable;
  }

  public IRenderRunnable getRenderRunnable() {
    return renderRunnable;
  }

  public void clearRenderRunnable() {
    renderRunnable = null;
  }

  public String getVariant() {
    return variant;
  }

  public void setVariant( final String variant ) {
    this.variant = variant;
  }
}
