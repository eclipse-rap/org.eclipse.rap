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
package org.eclipse.rap.e4.apache.jxpath.ri;

import java.io.Serializable;
import java.util.HashMap;

import org.eclipse.rap.e4.apache.jxpath.Pointer;
import org.eclipse.rap.e4.apache.jxpath.ri.model.NodeIterator;
import org.eclipse.rap.e4.apache.jxpath.ri.model.NodePointer;

/**
 * Namespace resolver for {@link JXPathContextReferenceImpl}.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 668329 $ $Date: 2008-06-16 16:59:48 -0500 (Mon, 16 Jun 2008) $
 */
public class NamespaceResolver implements Cloneable, Serializable {
    private static final long serialVersionUID = 1085590057838651311L;

    /** Parent NamespaceResolver */
    protected final NamespaceResolver parent;
    /** namespace map */
    protected HashMap namespaceMap = new HashMap();
    /** reverse lookup map */
    protected HashMap reverseMap = new HashMap();
    /** pointer */
    protected NodePointer pointer;
    private boolean sealed;

    /**
     * Find the namespace prefix for the specified namespace URI and NodePointer.
     * @param pointer location
     * @param namespaceURI to check
     * @return prefix if found
     * @since JXPath 1.3
     */
    protected static String getPrefix(NodePointer pointer, String namespaceURI) {
        NodePointer currentPointer = pointer;
        while (currentPointer != null) {
            NodeIterator ni = currentPointer.namespaceIterator();
            for (int position = 1; ni != null && ni.setPosition(position); position++) {
                NodePointer nsPointer = ni.getNodePointer();
                String uri = nsPointer.getNamespaceURI();
                if (uri.equals(namespaceURI)) {
                    String prefix = nsPointer.getName().getName();
                    if (!"".equals(prefix)) {
                        return prefix;
                    }
                }
            }
            currentPointer = pointer.getParent();
        }
        return null;
    }

    /**
     * Create a new NamespaceResolver.
     */
    public NamespaceResolver() {
        this(null);
    }

    /**
     * Create a new NamespaceResolver.
     * @param parent NamespaceResolver
     */
    public NamespaceResolver(NamespaceResolver parent) {
        this.parent = parent;
    }

    /**
     * Registers a namespace prefix.
     *
     * @param prefix A namespace prefix
     * @param namespaceURI A URI for that prefix
     */
    public synchronized void registerNamespace(String prefix, String namespaceURI) {
        if (isSealed()) {
            throw new IllegalStateException(
                    "Cannot register namespaces on a sealed NamespaceResolver");
        }
        namespaceMap.put(prefix, namespaceURI);
        reverseMap.put(namespaceURI, prefix);
    }

    /**
     * Register a namespace for the expression context.
     * @param pointer the Pointer to set.
     */
    public void setNamespaceContextPointer(NodePointer pointer) {
        this.pointer = pointer;
    }

    /**
     * Get the namespace context pointer.
     * @return Pointer
     */
    public Pointer getNamespaceContextPointer() {
        if (pointer == null && parent != null) {
            return parent.getNamespaceContextPointer();
        }
        return pointer;
    }

    /**
     * Given a prefix, returns a registered namespace URI. If the requested
     * prefix was not defined explicitly using the registerNamespace method,
     * JXPathContext will then check the context node to see if the prefix is
     * defined there. See
     * {@link #setNamespaceContextPointer(NodePointer) setNamespaceContextPointer}.
     *
     * @param prefix The namespace prefix to look up
     * @return namespace URI or null if the prefix is undefined.
     */
    public synchronized String getNamespaceURI(String prefix) {
        String uri = getExternallyRegisteredNamespaceURI(prefix);
        return uri == null && pointer != null ? pointer.getNamespaceURI(prefix)
                : uri;
    }

    /**
     * Given a prefix, returns an externally registered namespace URI.
     *
     * @param prefix The namespace prefix to look up
     * @return namespace URI or null if the prefix is undefined.
     * @since JXPath 1.3
     */
     protected synchronized String getExternallyRegisteredNamespaceURI(
            String prefix) {
        String uri = (String) namespaceMap.get(prefix);
        return uri == null && parent != null ? parent
                .getExternallyRegisteredNamespaceURI(prefix) : uri;
    }

    /**
     * Get the prefix associated with the specifed namespace URI.
     * @param namespaceURI the ns URI to check.
     * @return String prefix
     */
    public synchronized String getPrefix(String namespaceURI) {
        String prefix = getExternallyRegisteredPrefix(namespaceURI);
        return prefix == null && pointer != null ? getPrefix(pointer,
                namespaceURI) : prefix;
    }

    /**
     * Get the nearest prefix found that matches an externally-registered namespace.
     * @param namespaceURI the ns URI to check.
     * @return String prefix if found.
     * @since JXPath 1.3
     */
    protected synchronized String getExternallyRegisteredPrefix(String namespaceURI) {
        String prefix = (String) reverseMap.get(namespaceURI);
        return prefix == null && parent != null ? parent
                .getExternallyRegisteredPrefix(namespaceURI) : prefix;
    }

    /**
     * Learn whether this NamespaceResolver has been sealed.
     * @return boolean
     */
    public boolean isSealed() {
        return sealed;
    }

    /**
     * Seal this {@link NamespaceResolver}.
     */
    public void seal() {
        sealed = true;
        if (parent != null) {
            parent.seal();
        }
    }

    public Object clone() {
        try {
            NamespaceResolver result = (NamespaceResolver) super.clone();
            result.sealed = false;
            return result;
        }
        catch (CloneNotSupportedException e) {
            // Of course, it's supported.
            e.printStackTrace();
            return null;
        }
    }
}
