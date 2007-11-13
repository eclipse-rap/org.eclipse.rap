/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import java.util.Locale;

import org.eclipse.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

public final class NLSTab extends ExampleTab {

  private static final String LOCALE_DATA = "locale";

  final static class NLSTabMessages {
    
    private static final String BUNDLE_NAME 
      = "org.eclipse.rap.demo.controls.NLSTabMessages";
    
    public String TranslatableMessage;

    public static NLSTabMessages get() {
      return ( NLSTabMessages )RWT.NLS.getUTF8Encoded( BUNDLE_NAME, 
                                                       NLSTabMessages.class );
    }
    
    private NLSTabMessages() {
    }
  }

  private Label lblTranslatable;

  public NLSTab( final CTabFolder folder ) {
    super( folder, "NLS" );
  }

  protected void createStyleControls( final Composite parent ) {
    Label lblInfo = new Label( parent, SWT.NONE );
    String info 
      = "Select one of the locales below to be set for the current session:";
    lblInfo.setText( info );
    String text = "Default (" + RWT.getLocale().getDisplayLanguage() + ")";
    Button btnDefault = createLocaleButton( parent, text, null );
    btnDefault.setSelection( true );
    createLocaleButton( parent, "English", Locale.ENGLISH );
    createLocaleButton( parent, "German", Locale.GERMAN );
    createLocaleButton( parent, "Spanish", new Locale( "es" ) );
  }

  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    lblTranslatable = new Label( parent, SWT.NONE );
    updateTranslatable();
  }
  
  private Button createLocaleButton( final Composite parent, 
                                     final String text, 
                                     final Locale locale ) 
  {
    SelectionListener listener = new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        Locale locale = ( Locale )event.widget.getData( LOCALE_DATA );
        RWT.setLocale( locale );
        updateTranslatable();
      }
    };
    Button result = new Button( parent, SWT.RADIO );
    result.setText( text );
    result.setData( LOCALE_DATA, locale );
    result.addSelectionListener( listener );
    return result;
  }

  private void updateTranslatable() {
    lblTranslatable.setText( NLSTabMessages.get().TranslatableMessage );
  }
}
