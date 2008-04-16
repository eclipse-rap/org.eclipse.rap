/*******************************************************************************
 * Copyright (c) 2006 The Pampered Chef, Inc. and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html Contributors:
 * The Pampered Chef, Inc. - initial API and implementation Brad Reynolds - bug
 * 116920 Samy Abou-Shama - adapted for RAP and implemented Snippet as Control
 ******************************************************************************/
package org.eclipse.rap.demo.databinding;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Demonstrates nested selection.<br>
 * At the first level, user may select a person.<br>
 * At the second level, user may select a city to associate with the selected<br>
 * person or edit the person's name.
 */
public class Snippet001NestedSelectionWithCombo extends Group {

  private final ViewModel viewModel = new ViewModel();
  
  // Minimal JavaBeans support
  public static abstract class AbstractModelObject {
    private final PropertyChangeSupport propertyChangeSupport
      = new PropertyChangeSupport( this );

    public void addPropertyChangeListener( final PropertyChangeListener lsnr ) {
      propertyChangeSupport.addPropertyChangeListener( lsnr );
    }

    public void addPropertyChangeListener( final String propertyName,
                                           final PropertyChangeListener lsnr )
    {
      propertyChangeSupport.addPropertyChangeListener( propertyName, lsnr );
    }

    public void removePropertyChangeListener( final PropertyChangeListener lsnr )
    {
      propertyChangeSupport.removePropertyChangeListener( lsnr );
    }

    public void removePropertyChangeListener( final String propertyName,
                                              final PropertyChangeListener lsnr )
    {
      propertyChangeSupport.removePropertyChangeListener( propertyName,
                                                          lsnr );
    }

    protected void firePropertyChange( final String propertyName,
                                       final Object oldValue,
                                       final Object newValue )
    {
      propertyChangeSupport.firePropertyChange( propertyName,
                                                oldValue,
                                                newValue );
    }
  }
  
  // The data model class. This is normally a persistent class of some sort.
  //
  // This example implements full JavaBeans bound properties so that changes
  // to instances of this class will automatically be propogated to the UI.
  public static class Person extends AbstractModelObject {

    // Constructor
    public Person( final String name, final String city ) {
      this.name = name;
      this.city = city;
    }
    // Some JavaBean bound properties...
    String name;
    String city;

    public String getName() {
      return name;
    }

    public void setName( final String name ) {
      String oldValue = this.name;
      this.name = name;
      firePropertyChange( "name", oldValue, name );
    }

    public String getCity() {
      return city;
    }

    public void setCity( final String city ) {
      String oldValue = this.city;
      this.city = city;
      firePropertyChange( "city", oldValue, city );
    }
  }
  
  // The View's model--the root of our GUI's Model graph
  //
  // Typically each View class has a corresponding ViewModel class.
  // The ViewModel is responsible for getting the objects to edit from the
  // DAO. Since this snippet doesn't have any persistent objects to
  // retrieve, this ViewModel just instantiates some objects to edit.
  //
  // This ViewModel also implements JavaBean bound properties.
  static class ViewModel extends AbstractModelObject {

    // The model to bind
    private final ArrayList people = new ArrayList();
    {
      people.add( new Person( "Wile E. Coyote", "Tucson" ) );
      people.add( new Person( "Road Runner", "Lost Horse" ) );
      people.add( new Person( "Bugs Bunny", "Forrest" ) );
    }
    // Choice of cities for the Combo
    private final ArrayList cities = new ArrayList();
    {
      cities.add( "Tucson" );
      cities.add( "AcmeTown" );
      cities.add( "Lost Horse" );
      cities.add( "Forrest" );
      cities.add( "Lost Mine" );
    }

    public ArrayList getPeople() {
      return people;
    }

    public ArrayList getCities() {
      return cities;
    }
  }

  
  public Snippet001NestedSelectionWithCombo( final Composite parent, final int style ) {
    super( parent, style );
    createPartControl();
  }

  public void createPartControl() {
    // Initiating the realm
    Realm realm = SWTObservables.getRealm( Display.getCurrent() );

    FormLayout formLayout = new FormLayout();
    formLayout.marginHeight = DatabindingSnippetsView.GROUP_MARGIN_HEIGHT;
    formLayout.marginWidth = DatabindingSnippetsView.GROUP_MARGIN_WIDTH;
    setLayout( formLayout );
    setText( "Nested Selection Snippet 001 - Binding Example" );
    // Info Label
    Label info1 = new Label( this, SWT.NONE );
    info1.setText( "This snippet demonstrates the databinding"
                   + " using a Lists and Combos" );
    FormData data = new FormData( DatabindingSnippetsView.UNDEFINDED,
                                  SWT.DEFAULT );
    data.top = new FormAttachment( 0, DatabindingSnippetsView.TOP_MARGIN );
    data.left = new FormAttachment( 0, 0 );
    info1.setLayoutData( data );
    Label info2 = new Label( this, SWT.NONE );
    info2.setText( "Modifications in the TextBox and the"
                   + " Combobox will update the model "
                   + "(for the selection in the list)." );
    data = new FormData( DatabindingSnippetsView.UNDEFINDED, SWT.DEFAULT );
    data.top = new FormAttachment( info1, DatabindingSnippetsView.TOP_MARGIN );
    data.left = new FormAttachment( 0, 0 );
    info2.setLayoutData( data );
    List peopleList = new List( this, SWT.BORDER );
    data = new FormData( DatabindingSnippetsView.STD_TEXT_WIDTH, SWT.DEFAULT );
    data.top = new FormAttachment( info2, DatabindingSnippetsView.TOP_MARGIN );
    data.left = new FormAttachment( 0, 0 );
    peopleList.setLayoutData( data );
    ListViewer peopleListViewer = new ListViewer( peopleList );
    IObservableSet staticObservableSet
      = Observables.staticObservableSet(realm,
                                        new HashSet( viewModel.getPeople() ) );
    IObservableMap attributeMap
      = BeansObservables.observeMap( staticObservableSet,
                                     Person.class,
                                     "name" );
    ObservableMapLabelProvider omlProvider
      = new ObservableMapLabelProvider( attributeMap );
    peopleListViewer.setLabelProvider( omlProvider );
    peopleListViewer.setContentProvider( new ArrayContentProvider() );
    peopleListViewer.setInput( viewModel.getPeople() );
    Text name = new Text( this, SWT.BORDER );
    data = new FormData( DatabindingSnippetsView.STD_TEXT_WIDTH, SWT.DEFAULT );
    data.top = new FormAttachment( peopleList,
                                   DatabindingSnippetsView.TOP_MARGIN );
    data.left = new FormAttachment( 0, 0 );
    name.setLayoutData( data );
    Combo city = new Combo( this, SWT.BORDER | SWT.READ_ONLY );
    data = new FormData( DatabindingSnippetsView.STD_TEXT_WIDTH, SWT.DEFAULT );
    data.top = new FormAttachment( name, DatabindingSnippetsView.TOP_MARGIN );
    data.left = new FormAttachment( 0, 0 );
    city.setLayoutData( data );

    DataBindingContext dbc = new DataBindingContext( realm );
    IObservableValue selectedPerson
      = ViewersObservables.observeSingleSelection( peopleListViewer );
    dbc.bindValue( SWTObservables.observeText( name, SWT.Modify ),
                   BeansObservables.observeDetailValue( realm,
                                                        selectedPerson,
                                                        "name",
                                                        String.class ),
                   null,
                   null );
    ComboViewer cityViewer = new ComboViewer( city );
    cityViewer.setContentProvider( new ArrayContentProvider() );
    cityViewer.setInput( viewModel.getCities() );
    IObservableValue citySelection
      = ViewersObservables.observeSingleSelection( cityViewer );
    dbc.bindValue( citySelection,
                   BeansObservables.observeDetailValue( realm,
                                                        selectedPerson,
                                                        "city",
                                                        String.class ),
                   null,
                   null );
  }
}
