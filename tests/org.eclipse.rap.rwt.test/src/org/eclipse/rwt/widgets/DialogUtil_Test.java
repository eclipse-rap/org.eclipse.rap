/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.widgets;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.TestCase;

import org.eclipse.rwt.internal.widgets.IDialogAdapter;
import org.eclipse.swt.widgets.Dialog;
import org.mockito.Mockito;


public class DialogUtil_Test extends TestCase {

  public void testOpenWithNullDialog() {
    try {
      DialogUtil.open( null, mock( DialogCallback.class ) );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testOpenWithNullDialogCallback() {
    Dialog dialog = mock( Dialog.class );
    try {
      DialogUtil.open( dialog, null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testOpen() {
    IDialogAdapter dialogAdapter = mock( IDialogAdapter.class );
    Dialog dialog = mock( Dialog.class );
    when( dialog.getAdapter( IDialogAdapter.class ) ).thenReturn( dialogAdapter );
    DialogCallback dialogCallback = mock( DialogCallback.class );

    DialogUtil.open( dialog, dialogCallback );
    
    Mockito.verify( dialogAdapter ).openNonBlocking( dialogCallback );
  }
}
