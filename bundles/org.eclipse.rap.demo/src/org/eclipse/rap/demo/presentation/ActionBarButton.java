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


import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

class ActionBarButton
  extends MouseAdapter
  implements IPropertyChangeListener
{
  private final Action action;
  private final Label label;
  
  ActionBarButton( final Action action, final Composite actionBar ) {
    this.action = action;
    this.label = new Label( actionBar, SWT.NONE );
    label.setText( action.getText() );
    FontData fontData = label.getFont().getFontData()[ 0 ];
    label.setFont( Graphics.getFont( fontData.getName(),
                                     fontData.getHeight() + 2,
                                     fontData.getStyle() ) );
    label.pack();
    action.addPropertyChangeListener( this );
    adjustEnablement( action.isEnabled() );
  }

  public void mouseUp( final MouseEvent event ) {
    run();
  }

  public void run() {
    action.run();
  }

  public void propertyChange( final PropertyChangeEvent event ) {
    if( "enabled".equals( event.getProperty() ) ) {
      adjustEnablement( ( ( Boolean )event.getNewValue() ).booleanValue() );
    }
  }

  private void adjustEnablement( boolean booleanValue ) {
    if( booleanValue ) {
      label.addMouseListener( this );
      label.setForeground( Graphics.getColor( 255, 255, 255 ) );
    } else {
      label.removeMouseListener( this );
      label.setForeground( Graphics.getColor( 192, 192, 192 ) );
    }
  }
}