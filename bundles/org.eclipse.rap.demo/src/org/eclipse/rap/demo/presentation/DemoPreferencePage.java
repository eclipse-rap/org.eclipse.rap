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

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class DemoPreferencePage extends PreferencePage
  implements IWorkbenchPreferencePage
{

  public DemoPreferencePage() {
  }

  public DemoPreferencePage( final String title ) {
    super( title );
  }

  public DemoPreferencePage( final String title,
                             final ImageDescriptor image )
  {
    super( title, image );
  }

  protected Control createContents( final Composite parent ) {
    Label label = new Label( parent, SWT.NONE );
    label.setText( "Here should be the content of the Preference Page..." );
    return label;
  }

  public void init( final IWorkbench workbench ) {
  }
}
