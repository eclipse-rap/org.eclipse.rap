/*******************************************************************************
 * Copyright (c) 2006 The Pampered Chef, Inc. and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html Contributors:
 * The Pampered Chef, Inc. - initial API and implementation Brad Reynolds - bug
 * 116920 Samy Abou-Shama - adapted for RAP and implemented Snippet as Control
 ******************************************************************************/
package org.eclipse.rap.demo.databinding;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.rap.demo.databinding.nestedselection.ModelObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Hello, databinding. Bind changes in a GUI to a Model object but don't worry
 * about propagating changes from the Model to the GUI.
 * <p>
 * Illustrates the basic Model-ViewModel-Binding-View architecture typically
 * used in data binding applications.
 */
public class Snippet000HelloWorld extends Group {
  private final ViewModel viewModel = new ViewModel();
  private Text name;
  private Label labelName;
  
  // The data model class. This is normally a persistent class of some sort.
  //
  // In this example, we only push changes from the GUI to the model, so we
  // don't worry about implementing JavaBeans bound properties. If we need
  // our GUI to automatically reflect changes in the Person object, the
  // Person object would need to implement the JavaBeans property change
  // listener methods.
  static class Person extends ModelObject {
    
    // A property...
    String name = "HelloWorld";
    
    public String getName() {
      return name;
    }
    
    public void setName( final String name ) {
      String oldValue = this.name;
      this.name = name;
      firePropertyChange( "name", oldValue, this.name );
    }
  }

  // The View's model--the root of our Model graph for this particular GUI.
  //
  // Typically each View class has a corresponding ViewModel class.
  // The ViewModel is responsible for getting the objects to edit from the
  // DAO. Since this snippet doesn't have any persistent objects to
  // retrieve, this ViewModel just instantiates a model object to edit.
  static class ViewModel {
    
    // The model to bind
    private final Person person = new Person();
    
    public Person getPerson() {
      return person;
    }
  }

  
  public Snippet000HelloWorld( final Composite parent, final int style ) {
    super( parent, style );
    createPartControl();
  }

  public void createPartControl() {
    FormLayout formLayout = new FormLayout();
    formLayout.marginHeight = DatabindingSnippetsView.GROUP_MARGIN_HEIGHT;
    formLayout.marginWidth = DatabindingSnippetsView.GROUP_MARGIN_WIDTH;
    setLayout( formLayout );
    setText( "Hello World Snippet 0001 - Binding Example" );
    // Info Label
    Label info1 = new Label( this, SWT.NONE );
    info1.setText(   "This Textbox is linked to the Model (Person/Name). "
                   + "If you type in a text, it automatically updates "
                   + "the name property of the model" );
    FormData data = new FormData( DatabindingSnippetsView.UNDEFINDED,
                                  SWT.DEFAULT );
    data.top = new FormAttachment( 0, DatabindingSnippetsView.TOP_MARGIN );
    data.left = new FormAttachment( 0, 0 );
    info1.setLayoutData( data );
    name = new Text( this, SWT.BORDER );
    data = new FormData( DatabindingSnippetsView.STD_TEXT_WIDTH_LARGE,
                         SWT.DEFAULT );
    data.top = new FormAttachment( info1, DatabindingSnippetsView.TOP_MARGIN );
    data.left = new FormAttachment( 0, 0 );
    name.setLayoutData( data );
    Label info2 = new Label( this, SWT.NONE );
    info2.setText( "This Label is also linked (readonly) to the Model."
                   + " It gets updated on all changes" );
    data = new FormData( DatabindingSnippetsView.UNDEFINDED, SWT.DEFAULT );
    data.top = new FormAttachment( name, DatabindingSnippetsView.TOP_MARGIN );
    data.left = new FormAttachment( 0, 0 );
    info2.setLayoutData( data );
    labelName = new Label( this, SWT.BORDER );
    data = new FormData( DatabindingSnippetsView.STD_TEXT_WIDTH_LARGE,
                         SWT.DEFAULT );
    data.top = new FormAttachment( info2, DatabindingSnippetsView.TOP_MARGIN );
    data.left = new FormAttachment( 0, 0 );
    labelName.setLayoutData( data );
    labelName.setText( "not changed yet..." );
    
    Realm realm = SWTObservables.getRealm( Display.getCurrent() );
    DataBindingContext bindingContext = new DataBindingContext( realm );
    Person person = viewModel.getPerson();
    bindingContext.bindValue( SWTObservables.observeText( name, SWT.Modify ),
                              BeansObservables.observeValue( realm,
                                                             person,
                                                             "name" ),
                              null,
                              null );
    bindingContext.bindValue( SWTObservables.observeText( labelName ),
                              BeansObservables.observeValue( realm,
                                                             person,
                                                             "name" ),
                              null,
                              null );
  }
}
