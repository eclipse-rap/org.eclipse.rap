/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.widgets;

import org.eclipse.rap.rwt.RWT;

public abstract class Dialog {

  private int style;
  private Shell parent;
  private String title;

  public Dialog( final Shell parent ) {
    this( parent, RWT.APPLICATION_MODAL );
  }

  public Dialog( final Shell parent, final int style ) {
    checkParent( parent );
    this.parent = parent;
    this.style = style;
    title = "";
  }

  public Shell getParent() {
    return parent;
  }

  public int getStyle() {
    return style;
  }

  public String getText() {
    return title;
  }

  public void setText( final String string ) {
    if( string == null ) {
      error( RWT.ERROR_NULL_ARGUMENT );
    }
    title = string;
  }

  protected void checkSubclass() {
    // TODO [rst] Do we copy this mechanism?
    // if (!Display.isValidClass (getClass ())) {
    // error (RWT.ERROR_INVALID_SUBCLASS);
    // }
  }

  void checkParent( final Shell parent ) {
    if( parent == null ) {
      error( RWT.ERROR_NULL_ARGUMENT );
    }
    parent.checkWidget();
  }

  void error( final int code ) {
    RWT.error( code );
  }
}
