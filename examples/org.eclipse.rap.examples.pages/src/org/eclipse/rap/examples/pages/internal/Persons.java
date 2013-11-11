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
package org.eclipse.rap.examples.pages.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class Persons {

  public static Person[] get( Display display ) {
    List<Person> persons = getPersons( display );
    Person[] personArray = new Person[ persons.size() ];
    persons.toArray( personArray );
    return personArray;
  }

  private static List<Person> getPersons( Display display ) {
    List<Person> persons = new ArrayList<Person>();
    Person a = new Person( "Adam", "Archer", loadImage( display, "PersonA.png" ), "555 123456", "adam@mail.domain" );
    Person b = new Person( "Barabara", "Baker", loadImage( display, "PersonB.png" ), "555 123456", "barbara@mail.domain" );
    Person c = new Person( "Casper", "Carter", loadImage( display, "PersonC.png" ), "555 123456", "casper@mail.domain" );
    Person d = new Person( "Damien", "Dyer", loadImage( display, "PersonD.png" ), "555 123456", "damien@mail.domain" );
    Person e = new Person( "Edward", "Evans", loadImage( display, "PersonE.png" ), "555 123456", "edward@mail.domain" );
    Person f = new Person( "Frank", "Farmer", loadImage( display, "PersonF.png" ), "555 123456", "frank@mail.domain" );
    Person g = new Person( "Gabriel", "Gardener", loadImage( display, "PersonG.png" ), "555 123456", "gabriel@mail.domain" );
    Person h = new Person( "Hanna", "Hawkins", loadImage( display, "PersonH.png" ), "555 123456", "hawkins@mail.domain" );
    Person i = new Person( "Ian", "Ivanov", loadImage( display, "PersonI.png" ), "555 123456", "ian@mail.domain" );
    persons.add( a );
    persons.add( b );
    persons.add( c );
    persons.add( d );
    persons.add( e );
    persons.add( f );
    persons.add( g );
    persons.add( h );
    persons.add( i );
    return persons;
  }

  private static Image loadImage( Display display, String name ) {
    String prefix = "/resources/";
    return new Image( display, Persons.class.getResourceAsStream( prefix + name ) );
  }
}
