/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.widgets;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.rap.rwt.internal.widgets.IDialogAdapter;
import org.eclipse.swt.widgets.Dialog;
import org.junit.Before;
import org.junit.Test;


public class DialogUtil_Test {

  private Dialog dialog;
  private IDialogAdapter dialogAdapter;

  @Before
  public void setUp() {
    dialog = mock( Dialog.class );
    dialogAdapter = mock( IDialogAdapter.class );
    when( dialog.getAdapter( IDialogAdapter.class ) ).thenReturn( dialogAdapter );
  }

  @Test
  public void testOpenWithNullDialog() {
    DialogCallback dialogCallback = mock( DialogCallback.class );
    try {
      DialogUtil.open( null, dialogCallback );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testOpenWithNullDialogCallback() {
    DialogUtil.open( dialog, null );

    verify( dialogAdapter ).openNonBlocking( same( ( DialogCallback )null ) );
  }

  @Test
  public void testOpen() {
    DialogCallback dialogCallback = mock( DialogCallback.class );

    DialogUtil.open( dialog, dialogCallback );

    verify( dialogAdapter ).openNonBlocking( dialogCallback );
  }

}
