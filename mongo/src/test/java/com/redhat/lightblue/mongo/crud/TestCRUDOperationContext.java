/*
 Copyright 2013 Red Hat, Inc. and/or its affiliates.

 This file is part of lightblue.

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.redhat.lightblue.mongo.crud;

import com.redhat.lightblue.crud.CRUDOperationContext;
import com.redhat.lightblue.crud.CRUDOperation;
import com.redhat.lightblue.metadata.EntityMetadata;

import static com.redhat.lightblue.mongo.crud.AbstractMongoCrudTest.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of CRUDOperationContext for use in unit tests.
 *
 * @author nmalik
 */
public class TestCRUDOperationContext extends CRUDOperationContext {
    private final Map<String, EntityMetadata> map = new HashMap<>();

    public TestCRUDOperationContext(CRUDOperation op) {
        this("test",op);
    }

    public TestCRUDOperationContext(String name,CRUDOperation op) {
        super(op, name, factory, null, null);
    }

    public void add(EntityMetadata md) {
        map.put(md.getName(), md);
    }

    @Override
    public EntityMetadata getEntityMetadata(String entityName) {
        return map.get(entityName);
    }
}
