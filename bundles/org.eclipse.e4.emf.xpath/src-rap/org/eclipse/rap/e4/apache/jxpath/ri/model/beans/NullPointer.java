/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.rap.e4.apache.jxpath.ri.model.beans;

import java.util.Locale;

import org.eclipse.rap.e4.apache.jxpath.JXPathContext;
import org.eclipse.rap.e4.apache.jxpath.ri.QName;
import org.eclipse.rap.e4.apache.jxpath.ri.model.NodePointer;

/**
 * Pointer whose value is <code>null</code>.
 * @author Dmitri Plotnikov
 * @version $Revision: 652915 $ $Date: 2008-05-02 16:12:57 -0500 (Fri, 02 May 2008) $
 */
public class NullPointer extends PropertyOwnerPointer {
    private QName name;
    private String id;

    private static final long serialVersionUID = 2193425983220679887L;

    /**
     * Create a new NullPointer.
     * @param name node name
     * @param locale Locale
     */
    public NullPointer(QName name, Locale locale) {
        super(null, locale);
        this.name = name;
    }

    /**
     * Used for the root node.
     * @param parent parent pointer
     * @param name node name
     */
    public NullPointer(NodePointer parent, QName name) {
        super(parent);
        this.name = name;
    }

    /**
     * Create a new NullPointer.
     * @param locale Locale
     * @param id String
     */
    public NullPointer(Locale locale, String id) {
        super(null, locale);
        this.id = id;
    }

    public QName getName() {
        return name;
    }

    public Object getBaseValue() {
        return null;
    }

    public boolean isCollection() {
        return false;
    }

    public boolean isLeaf() {
        return true;
    }

    public boolean isActual() {
        return false;
    }

    public PropertyPointer getPropertyPointer() {
        return new NullPropertyPointer(this);
    }

    public NodePointer createPath(JXPathContext context, Object value) {
        if (parent != null) {
            return parent.createPath(context, value).getValuePointer();
        }
        throw new UnsupportedOperationException(
            "Cannot create the root object: " + asPath());
    }

    public NodePointer createPath(JXPathContext context) {
        if (parent != null) {
            return parent.createPath(context).getValuePointer();
        }
        throw new UnsupportedOperationException(
            "Cannot create the root object: " + asPath());
    }

    public NodePointer createChild(
        JXPathContext context,
        QName name,
        int index) {
        return createPath(context).createChild(context, name, index);
    }

    public NodePointer createChild(
        JXPathContext context,
        QName name,
        int index,
        Object value) {
        return createPath(context).createChild(context, name, index, value);
    }

    public int hashCode() {
        return name == null ? 0 : name.hashCode();
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof NullPointer)) {
            return false;
        }

        NullPointer other = (NullPointer) object;
        return name == other.name || name != null && name.equals(other.name);
    }

    public String asPath() {
        if (id != null) {
            return "id(" + id + ")";
        }
        return parent == null ? "null()" : super.asPath();
    }

    public int getLength() {
        return 0;
    }
}
