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

package org.eclipse.rap.demo.presentation;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;


public class ActionBar {

  static void create( final List actions,final Composite actionBar ) {
    Control[] children = actionBar.getChildren();
    for( int i = 0; i < children.length; i++ ) {
      children[ i ].dispose();
    }
  
    actionBar.setLayout( new RowLayout() );
    Iterator iterator = actions.iterator();    
    while( iterator.hasNext() ) {
      Object next = iterator.next();
      if( next instanceof Action ) {
        final Action action = ( Action )next;
        new ActionBarButton( action, actionBar );
        Label separator = new Label( actionBar, SWT.NONE );
        separator.setText( " " );
        Label separator2 = new Label( actionBar, SWT.NONE );
        separator2.setText( " " );
        Label separator3 = new Label( actionBar, SWT.NONE );
        separator3.setText( " " );
      } else {
        Label separator = new Label( actionBar, SWT.SEPARATOR | SWT.VERTICAL );
        separator.setForeground( Graphics.getColor( 255, 255, 255 ) );
        Label separator2 = new Label( actionBar, SWT.NONE );
        separator2.setText( " " );
        Label separator3 = new Label( actionBar, SWT.NONE );
        separator3.setText( " " );
      }
    }
    actionBar.layout();
  }
}
