package org.apache.ddlutils.platform.hsqldb;

/*
 * Copyright 1999-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.ddlutils.PlatformInfo;
import org.apache.ddlutils.model.ForeignKey;
import org.apache.ddlutils.model.Index;
import org.apache.ddlutils.model.Table;
import org.apache.ddlutils.platform.JdbcModelReader;

/**
 * Reads a database model from a HsqlDb database.
 *
 * @author Thomas Dudziak
 * @version $Revision: $
 */
public class HsqlDbModelReader extends JdbcModelReader
{
    /**
     * Creates a new model reader for HsqlDb databases.
     * 
     * @param platformInfo The platform specific settings
     */
    public HsqlDbModelReader(PlatformInfo platformInfo)
    {
        super(platformInfo);
        setDefaultCatalogPattern(null);
        setDefaultSchemaPattern(null);
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isInternalForeignKeyIndex(Table table, ForeignKey fk, Index index)
    {
        String name = index.getName();

        return (name != null) && name.startsWith("SYS_IDX_");
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isInternalPrimaryKeyIndex(Table table, Index index)
    {
        String name = index.getName();

        return (name != null) && name.startsWith("SYS_PK_");
    }
}