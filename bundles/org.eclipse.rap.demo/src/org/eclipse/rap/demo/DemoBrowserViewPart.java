/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.demo;

import org.eclipse.rap.demo.DemoTreeViewPart.TreeObject;
import org.eclipse.rap.jface.viewers.ISelection;
import org.eclipse.rap.jface.viewers.IStructuredSelection;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.browser.Browser;
import org.eclipse.rap.rwt.widgets.Composite;
import org.eclipse.rap.ui.*;
import org.eclipse.rap.ui.part.ViewPart;

public class DemoBrowserViewPart extends ViewPart {

  Browser browser;

  public void createPartControl( final Composite parent ) {
    browser = new Browser( parent, RWT.NONE );
    browser.setUrl( "http://www.eclipse.org/birt/phoenix/examples/solution/TopSellingProducts.html" );
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
        IStructuredSelection sselection = ( IStructuredSelection )selection;
        Object firstElement = sselection.getFirstElement();
        if( firstElement instanceof TreeObject ) {
          String location = ( ( TreeObject )firstElement ).getLocation();
          if( location != null ) {
            browser.setUrl( location );
          }
        }
      }
    } );
  }
}
