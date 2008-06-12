/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Brad Reynolds - bug 116920
 *     Samy Abou-Shama (NOMAD business software GmbH) Adapted as view for RAP
 ******************************************************************************/

package org.eclipse.rap.demo.databinding.nestedselection;

import org.eclipse.core.databinding.*;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.IObserving;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.internal.databinding.conversion.ObjectToStringConverter;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.*;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.rap.demo.databinding.DatabindingSnippetsView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.ViewPart;

/**
 * @since 1.0
 */
public class TestMasterDetailView extends ViewPart {
  private Group bindingGroup;
  private Table personsTable = null;
  private Label label1 = null;
  private Text name = null;
  private Label label2 = null;
  private Text address = null;
  private Label label3 = null;
  private Text city = null;
  private Label label4 = null;
  private Text state = null;
  private Table ordersTable = null;
  private Text validationStatus;
  private final SimpleModel model = new SimpleModel();

  /**
   * @since 1.0
   */
  private static final class CustomUpdateValueStrategy
    extends UpdateValueStrategy
  {

    protected IStatus doSet( final IObservableValue observableValue,
                             final Object value )
    {
      IStatus result = super.doSet( observableValue, value );
      if( result.isOK() ) {
        Object changed = observableValue;
        if( changed instanceof IObserving ) {
          changed = ( ( IObserving )changed ).getObserved();
        }
      }
      return result;
    }
  }

  /**
   * This method initializes table
   */
  private void createTable() {
    personsTable = new Table( bindingGroup, SWT.NONE );
    personsTable.setHeaderVisible( true );
    personsTable.setLinesVisible( true );
    TableColumn tableColumn = new TableColumn( personsTable, SWT.NONE );
    tableColumn.setWidth( 60 );
    tableColumn.setText( "Name" );
    TableColumn tableColumn1 = new TableColumn( personsTable, SWT.NONE );
    tableColumn1.setWidth( 60 );
    tableColumn1.setText( "State" );
    FormData data = new FormData( DatabindingSnippetsView.STD_TEXT_WIDTH_LARGE,
                                  100 );
    data.top = new FormAttachment( 0, DatabindingSnippetsView.TOP_MARGIN );
    data.left = new FormAttachment( 0, 0 );
    personsTable.setLayoutData( data );
  }

  /**
   * This method initializes table1
   */
  private void createTable1() {
    ordersTable = new Table( bindingGroup, SWT.NONE );
    ordersTable.setHeaderVisible( true );
    ordersTable.setLinesVisible( true );
    TableColumn tableColumn2 = new TableColumn( ordersTable, SWT.NONE );
    tableColumn2.setWidth( 60 );
    tableColumn2.setText( "Order No" );
    TableColumn tableColumn3 = new TableColumn( ordersTable, SWT.NONE );
    tableColumn3.setWidth( 60 );
    tableColumn3.setText( "Date" );
    FormData data = new FormData( DatabindingSnippetsView.STD_TEXT_WIDTH_LARGE,
                                  100 );
    data.top = new FormAttachment( label4,
                                   DatabindingSnippetsView.TOP_MARGIN * 3 );
    data.left = new FormAttachment( 0, 0 );
    ordersTable.setLayoutData( data );
  }

  private void bind( final Control parent ) {
    Realm realm = SWTObservables.getRealm( parent.getDisplay() );
    TableViewer peopleViewer = new TableViewer( personsTable );
    ObservableListContentProvider peopleViewerContent
      = new ObservableListContentProvider();
    peopleViewer.setContentProvider( peopleViewerContent );
    
    String[] attrSimplePerson = new String[]{
      "name",
      "state"
    };
    IObservableMap[] attributeMaps 
      = BeansObservables.observeMaps( peopleViewerContent.getKnownElements(),
                                      SimplePerson.class,
                                      attrSimplePerson );
    ObservableMapLabelProvider omlProvider
      = new ObservableMapLabelProvider( attributeMaps );
    peopleViewer.setLabelProvider( omlProvider );
    peopleViewer.setInput( new WritableList( realm,
                                             model.getPersonList(),
                                             SimpleModel.class ) );
    IObservableValue selectedPerson
      = ViewersObservables.observeSingleSelection( peopleViewer );
    DataBindingContext dbc = new DataBindingContext( realm ) {
      protected UpdateValueStrategy
        createTargetToModelUpdateValueStrategy( IObservableValue fromValue,
                                                IObservableValue toValue )
      {
        return new CustomUpdateValueStrategy();
      }
    };
    IConverter upperCaseConverter = new IConverter() {
      public Object convert( Object fromObject ) {
        return ( ( String )fromObject ).toUpperCase();
      }
      public Object getFromType() {
        return String.class;
      }
      public Object getToType() {
        return String.class;
      }
    };
    IValidator vowelValidator = new IValidator() {
      public IStatus validate( Object value ) {
        String s = ( String )value;
        if( !s.matches( "[aeiouAEIOU]*" ) ) {
          return ValidationStatus.error( "only vowels allowed" );
        }
        return Status.OK_STATUS;
      }
    };
    
    IObservableValue modelObservableValue 
      = BeansObservables.observeDetailValue( realm,
                                             selectedPerson,
                                             "name",
                                             String.class );
    ISWTObservableValue targetObservableValue
      = SWTObservables.observeText( name, SWT.Modify );
    CustomUpdateValueStrategy customUpdateValueStrategy
      = new CustomUpdateValueStrategy();
    customUpdateValueStrategy.setConverter( upperCaseConverter );
    UpdateValueStrategy targetToModel 
      = customUpdateValueStrategy.setAfterGetValidator( vowelValidator );
    Binding binding = dbc.bindValue( targetObservableValue,
                                     modelObservableValue,
                                     targetToModel,
                                     null );
    
    UpdateValueStrategy updateValueStrategy = new UpdateValueStrategy();
    updateValueStrategy.setConverter( new ObjectToStringConverter() );
    dbc.bindValue( SWTObservables.observeText( validationStatus, SWT.NONE ),
                   binding.getValidationStatus(),
                   null,
                   updateValueStrategy );
    dbc.bindValue( SWTObservables.observeText( address, SWT.Modify ),
                   BeansObservables.observeDetailValue( realm,
                                                        selectedPerson,
                                                        "address",
                                                        String.class ),
                   null,
                   null );
    dbc.bindValue( SWTObservables.observeText( city, SWT.Modify ),
                   BeansObservables.observeDetailValue( realm,
                                                        selectedPerson,
                                                        "city",
                                                        String.class ),
                   null,
                   null );
    dbc.bindValue( SWTObservables.observeText( state, SWT.Modify ),
                   BeansObservables.observeDetailValue( realm,
                                                        selectedPerson,
                                                        "state",
                                                        String.class ),
                   null,
                   null );
    TableViewer ordersViewer = new TableViewer( ordersTable );
    ObservableListContentProvider ordersViewerContent
      = new ObservableListContentProvider();
    ordersViewer.setContentProvider( ordersViewerContent );
    String[] propertyNames = new String[]{
      "orderNumber",
      "date"
    };
    IObservableMap[] observeMaps
      = BeansObservables.observeMaps( ordersViewerContent.getKnownElements(),
                                      SimpleOrder.class,
                                      propertyNames );
    ObservableMapLabelProvider observableMapLabelProvider
      = new ObservableMapLabelProvider( observeMaps );
    ordersViewer.setLabelProvider( observableMapLabelProvider );
    IObservableList orders
      = BeansObservables.observeDetailList( realm,
                                            selectedPerson,
                                            "orders",
                                            SimpleOrder.class );
    ordersViewer.setInput( orders );
  }

  public void createPartControl( final Composite parent ) {
    FormLayout formLayout = new FormLayout();
    formLayout.marginHeight = DatabindingSnippetsView.GROUP_MARGIN_HEIGHT;
    formLayout.marginWidth = DatabindingSnippetsView.GROUP_MARGIN_WIDTH;
    parent.setLayout( new FormLayout() );
    bindingGroup = new Group( parent, SWT.NONE );
    bindingGroup.setText(   "Adaptation of the Databinding "
                          + "TestMasterDetailView Example" );
    FormData data = new FormData( DatabindingSnippetsView.GROUP_WIDTH,
                                  SWT.DEFAULT );
    data.top = new FormAttachment( 0, DatabindingSnippetsView.TOP_MARGIN );
    data.left
      = new FormAttachment( 0, DatabindingSnippetsView.GROUP_MARGIN_WIDTH );
    bindingGroup.setLayoutData( data );
    FormLayout formLayoutGroup = new FormLayout();
    formLayoutGroup.marginHeight = DatabindingSnippetsView.GROUP_MARGIN_HEIGHT;
    formLayoutGroup.marginWidth = DatabindingSnippetsView.GROUP_MARGIN_WIDTH;
    bindingGroup.setLayout( formLayoutGroup );
    createTable();
    label1 = new Label( bindingGroup, SWT.NONE );
    label1.setText( "Name" );
    data = new FormData( DatabindingSnippetsView.STD_LABEL_WIDTH, SWT.DEFAULT );
    data.top = new FormAttachment( personsTable,
                                   DatabindingSnippetsView.TOP_MARGIN * 3 );
    data.left = new FormAttachment( 0, 0 );
    label1.setLayoutData( data );
    name = new Text( bindingGroup, SWT.BORDER );
    data = new FormData( DatabindingSnippetsView.STD_LABEL_WIDTH, SWT.DEFAULT );
    data.top = new FormAttachment( personsTable,
                                   DatabindingSnippetsView.TOP_MARGIN * 3 );
    data.left = new FormAttachment( label1, 0 );
    name.setLayoutData( data );
    label2 = new Label( bindingGroup, SWT.NONE );
    label2.setText( "Address" );
    data = new FormData( DatabindingSnippetsView.STD_LABEL_WIDTH, SWT.DEFAULT );
    data.top = new FormAttachment( name, DatabindingSnippetsView.TOP_MARGIN );
    data.left = new FormAttachment( 0, 0 );
    label2.setLayoutData( data );
    address = new Text( bindingGroup, SWT.BORDER );
    data = new FormData( DatabindingSnippetsView.STD_LABEL_WIDTH, SWT.DEFAULT );
    data.top = new FormAttachment( name, DatabindingSnippetsView.TOP_MARGIN );
    data.left = new FormAttachment( label2, 0 );
    address.setLayoutData( data );
    label3 = new Label( bindingGroup, SWT.NONE );
    label3.setText( "City" );
    data = new FormData( DatabindingSnippetsView.STD_LABEL_WIDTH, SWT.DEFAULT );
    data.top
      = new FormAttachment( address, DatabindingSnippetsView.TOP_MARGIN );
    data.left = new FormAttachment( 0, 0 );
    label3.setLayoutData( data );
    city = new Text( bindingGroup, SWT.BORDER );
    data = new FormData( DatabindingSnippetsView.STD_LABEL_WIDTH, SWT.DEFAULT );
    data.top
      = new FormAttachment( address, DatabindingSnippetsView.TOP_MARGIN );
    data.left = new FormAttachment( label3, 0 );
    city.setLayoutData( data );
    label4 = new Label( bindingGroup, SWT.NONE );
    label4.setText( "State" );
    data = new FormData( DatabindingSnippetsView.STD_LABEL_WIDTH, SWT.DEFAULT );
    data.top = new FormAttachment( city, DatabindingSnippetsView.TOP_MARGIN );
    data.left = new FormAttachment( 0, 0 );
    label4.setLayoutData( data );
    state = new Text( bindingGroup, SWT.BORDER );
    data = new FormData( DatabindingSnippetsView.STD_LABEL_WIDTH, SWT.DEFAULT );
    data.top = new FormAttachment( city, DatabindingSnippetsView.TOP_MARGIN );
    data.left = new FormAttachment( label4, 0 );
    state.setLayoutData( data );
    createTable1();
    validationStatus = new Text( bindingGroup, SWT.READ_ONLY | SWT.BORDER );
    data = new FormData( 400, SWT.DEFAULT );
    data.top = new FormAttachment( ordersTable,
                                   DatabindingSnippetsView.TOP_MARGIN );
    data.left = new FormAttachment( 0, 0 );
    validationStatus.setLayoutData( data );
    bind( parent );
  }

  public void setFocus() {
  }
}
