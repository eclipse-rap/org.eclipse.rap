/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rwt.widgets.DialogCallback;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;


public class MessageDialogUtil {

  public static void openInformation( Shell parent,
                                      String title,
                                      String message,
                                      final DialogCallback callback )
  {
    open( MessageDialog.INFORMATION, parent, title, message, callback );
  }

  public static void openError( Shell parent, String title, String message, DialogCallback callback )
  {
    open( MessageDialog.ERROR, parent, title, message, callback );
  }

  public static void openQuestion( Shell parent,
                                   String title,
                                   String message,
                                   DialogCallback callback )
  {
    open( MessageDialog.QUESTION, parent, title, message, callback );
  }

  public static void openConfirm( Shell parent,
                                  String title,
                                  String message,
                                  DialogCallback callback )
  {
    open( MessageDialog.CONFIRM, parent, title, message, callback );
  }

  public static void openWarning( Shell parent, String title, String message, DialogCallback callback )
  {
    open( MessageDialog.WARNING, parent, title, message, callback );
  }

  private static void open( int kind,
                            Shell parent,
                            String title,
                            String message,
                            final DialogCallback callback )
  {
    String[] buttonLabels = getButtonLabels( kind );
    MessageDialog dialog = new MessageDialog( parent,
                                              title,
                                              null,
                                              message,
                                              kind,
                                              buttonLabels,
                                              0 )
    {
      @Override
      protected int getShellStyle() {
        return super.getShellStyle() | SWT.SHEET;
      }
      @Override
      public boolean close() {
        boolean result = super.close();
        if( callback != null ) {
          callback.dialogClosed( getReturnCode() );
        }
        return result;
      }
    };
    dialog.setBlockOnOpen( false );
    dialog.open();
  }

  // Copy of MessageDialog#getButtonLabels( int )
  private static String[] getButtonLabels( int kind ) {
    String[] dialogButtonLabels;
    switch( kind ) {
      case MessageDialog.ERROR:
      case MessageDialog.INFORMATION:
      case MessageDialog.WARNING: {
        dialogButtonLabels = new String[]{
          IDialogConstants.get().OK_LABEL
        };
        break;
      }
      case MessageDialog.CONFIRM: {
        dialogButtonLabels = new String[]{
          IDialogConstants.get().OK_LABEL,
          IDialogConstants.get().CANCEL_LABEL
        };
        break;
      }
      case MessageDialog.QUESTION: {
        dialogButtonLabels = new String[]{
          IDialogConstants.get().YES_LABEL,
          IDialogConstants.get().NO_LABEL
        };
        break;
      }
      case MessageDialog.QUESTION_WITH_CANCEL: {
        dialogButtonLabels = new String[]{
          IDialogConstants.get().YES_LABEL,
          IDialogConstants.get().NO_LABEL,
          IDialogConstants.get().CANCEL_LABEL
        };
        break;
      }
      default: {
        throw new IllegalArgumentException( "Illegal value for kind in MessageDialog.open()" );
      }
    }
    return dialogButtonLabels;
  }

}
