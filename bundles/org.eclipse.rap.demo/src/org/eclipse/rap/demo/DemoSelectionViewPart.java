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

package org.eclipse.rap.demo;

import org.eclipse.rap.jface.viewers.ISelection;
import org.eclipse.rap.jface.viewers.IStructuredSelection;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.layout.GridData;
import org.eclipse.rap.rwt.layout.GridLayout;
import org.eclipse.rap.rwt.widgets.*;
import org.eclipse.rap.ui.*;
import org.eclipse.rap.ui.part.ViewPart;


public class DemoSelectionViewPart extends ViewPart {

  private List list;

  public void createPartControl( final Composite parent ) {
    parent.setLayout( new GridLayout( 1, false ) );
    Label label = new Label( parent, 0 );
    label.setText( "Selection Log" );
    label.setLayoutData( new GridData( 80, 15 ) );
    list = new List( parent, RWT.FLAT );
    list.setLayoutData( new GridData( RWT.FILL, RWT.FILL, true, true ) );
    createSelectionListener();
  }
  
  private void createSelectionListener() {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    ISelectionService selectionService = window.getSelectionService();
    selectionService.addSelectionListener( new ISelectionListener() {
      public void selectionChanged( final IWorkbenchPart part, 
                                    final ISelection selection ) 
      {
        String entry = part.getTitle() + " / ";
        IStructuredSelection sselection = ( IStructuredSelection )selection;
        Object firstElement = sselection.getFirstElement();
        if( firstElement == null ) {
          entry += "null";
        } else {
          entry += firstElement.toString();
        }
        list.add( entry, 0 );
        list.setSelection( 0 );
      } 
    } );
  }
}
