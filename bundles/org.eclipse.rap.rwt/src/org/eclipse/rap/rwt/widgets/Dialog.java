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

  int style;
  Shell parent;
  String title;

  public Dialog( Shell parent ) {
    // TODO [rst] which one of APPLICATION_MODAL, SYSTEM_MODAL and PRIMARY_MODAL
    // is
    // suitable?
    // this (parent, RWT.PRIMARY_MODAL);
    this( parent, RWT.APPLICATION_MODAL );
  }

  public Dialog( Shell parent, int style ) {
    checkParent( parent );
    this.parent = parent;
    this.style = style;
    title = "";
  }

  protected void checkSubclass() {
    // TODO [rst] Do we copy this mechanism?
    // if (!Display.isValidClass (getClass ())) {
    // error (RWT.ERROR_INVALID_SUBCLASS);
    // }
  }

  void checkParent( Shell parent ) {
    if( parent == null )
      error( RWT.ERROR_NULL_ARGUMENT );
    parent.checkWidget();
  }

  void error( int code ) {
    RWT.error( code );
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

  public void setText( String string ) {
    if( string == null )
      error( RWT.ERROR_NULL_ARGUMENT );
    title = string;
  }
}
