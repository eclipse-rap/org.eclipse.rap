/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.pages;

import static org.eclipse.rap.examples.pages.internal.ImageUtil.getImage;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.eclipse.rap.examples.ExampleUtil;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rap.examples.pages.internal.ExamplesMessages;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.ClientInfo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;


public class InternationalizationExamplePage implements IExamplePage {

  private Language[] languages;
  private Composite parentContainer;
  private Composite contentContainer;

  public void createControl( Composite parent ) {
    initLanguages( parent.getDisplay() );
    parentContainer = parent;
    parentContainer.addListener( SWT.Dispose, new LocaleResetListener() );
    parentContainer.setLayout( ExampleUtil.createMainLayout( 1 ) );
    createLocaleSwitcherArea();
    createContentArea();
  }

  private void initLanguages( Device device ) {
    languages = new Language[] {
      new Language( null, getImage( device, "no-flag.png" ) ),
      new Language( Locale.ENGLISH, getImage( device, "en-flag.png" ) ),
      new Language( Locale.GERMAN, getImage( device, "de-flag.png" ) ),
      new Language( Locale.FRENCH, getImage( device, "fr-flag.png" ) ),
      new Language( new Locale( "bg", "", "" ), getImage( device, "bg-flag.png" ) ),
      new Language( Locale.CHINESE, getImage( device, "zh-flag.png" ) )
    };
  }

  private void createLocaleSwitcherArea() {
    Composite container = new Composite( parentContainer, SWT.NONE );
    container.setLayout( ExampleUtil.createGridLayout( 3, false, true, true ) );
    container.setLayoutData( ExampleUtil.createHorzFillData() );
    Label clientLanguage = new Label( container, SWT.NONE );
    clientLanguage.setLayoutData( createClientLanguageGridData() );
    ClientInfo clientInfo = RWT.getClient().getService( ClientInfo.class );
    clientLanguage.setText(   "Browser default language: "
                            + clientInfo.getLocale().getDisplayLanguage() );
    new Label( container, SWT.NONE ).setText( "Select language: " );
    ToolBar toolBar = new ToolBar( container, SWT.NONE );
    toolBar.setLayoutData( createToolBarGridData() );
    ToolItem dropDown = new ToolItem( toolBar, SWT.DROP_DOWN );
    dropDown.setText( languages[ 0 ].name );
    dropDown.setImage( languages[ 0 ].flag );
    Menu dropDownMenu = createDropDownMenu( dropDown );
    dropDown.addListener( SWT.Selection, new DropDownSelectionListener( dropDownMenu ) );
  }

  private Menu createDropDownMenu( ToolItem dropDown ) {
    Menu menu = new Menu( dropDown.getParent().getShell(), SWT.POP_UP );
    for( int i = 0; i < languages.length; i++ ) {
      MenuItem item = new MenuItem( menu, SWT.PUSH );
      item.setText( languages[ i ].name );
      item.setImage( languages[ i ].flag );
      item.setData( languages[ i ] );
      item.addListener( SWT.Selection, new MenuItemSelectionListener( dropDown ) );
    }
    return menu;
  }

  private void createContentArea() {
    if( contentContainer != null ) {
      contentContainer.dispose();
    }
    contentContainer = new Composite( parentContainer, SWT.NONE );
    contentContainer.setLayoutData( ExampleUtil.createFillData() );
    contentContainer.setLayout( ExampleUtil.createGridLayout( 1, false, true, true ) );
    createWhatIsUnicodeArea();
    createCurrentDateArea();
    parentContainer.layout();
  }

  private void createWhatIsUnicodeArea() {
    Label title = new Label( contentContainer, SWT.CENTER );
    title.setLayoutData( ExampleUtil.createHorzFillData() );
    title.setData( RWT.CUSTOM_VARIANT, "heading" );
    title.setText( ExamplesMessages.get().WhatIsUnicode_Title );
    Label text = new Label( contentContainer, SWT.WRAP );
    text.setLayoutData( ExampleUtil.createFillData() );
    text.setText( ExamplesMessages.get().WhatIsUnicode_Descritption );
  }

  private void createCurrentDateArea() {
    Label today = new Label( contentContainer, SWT.RIGHT );
    today.setLayoutData( ExampleUtil.createHorzFillData() );
    Locale locale = RWT.getLocale();
    Calendar calendar = Calendar.getInstance( locale );
    DateFormat formatter = DateFormat.getDateInstance( DateFormat.FULL, locale );
    today.setText( formatter.format( calendar.getTime() ) );
  }

  private GridData createClientLanguageGridData() {
    return new GridData( SWT.FILL, SWT.CENTER, true, false );
  }

  private GridData createToolBarGridData() {
    return new GridData( 150, SWT.DEFAULT );
  }

  private final class Language {

    public final String name;
    public final Locale locale;
    public final Image flag;

    public Language( Locale locale, Image flag ) {
      if( locale == null ) {
        this.name = "Default";
      } else {
        this.name = locale.getDisplayLanguage();
      }
      this.locale = locale;
      this.flag = flag;
    }

  }

  private final class MenuItemSelectionListener implements Listener {

    private final ToolItem dropDown;

    public MenuItemSelectionListener( ToolItem dropDown ) {
      this.dropDown = dropDown;
    }

    public void handleEvent( Event event ) {
      MenuItem item = ( MenuItem )event.widget;
      Language language = ( Language )item.getData();
      dropDown.setText( language.name );
      dropDown.setImage( language.flag );
      RWT.setLocale( language.locale );
      createContentArea();
    }

  }

  private final class DropDownSelectionListener implements Listener {

    private final Menu menu;

    public DropDownSelectionListener( Menu menu ) {
      this.menu = menu;
    }

    public void handleEvent( Event event ) {
      ToolItem dropDown = ( ToolItem )event.widget;
      if( event.detail == SWT.ARROW ) {
        Point point = dropDown.getParent().toDisplay( event.x, event.y );
        menu.setLocation( point );
        menu.setVisible( true );
      }
    }

  }

  private final class LocaleResetListener implements Listener {

    public void handleEvent( Event event ) {
      RWT.setLocale( null );
    }

  }

}
