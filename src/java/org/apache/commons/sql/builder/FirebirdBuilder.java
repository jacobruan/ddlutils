package org.apache.commons.sql.builder;

/*
 * Copyright 1999-2004 The Apache Software Foundation.
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

import java.io.IOException;
import java.sql.Types;

import org.apache.commons.sql.model.Column;
import org.apache.commons.sql.model.Database;
import org.apache.commons.sql.model.ForeignKey;
import org.apache.commons.sql.model.Table;

/**
 * An SQL Builder for the Firebird database.
 * 
 * @author <a href="mailto:tomdz@apache.org">Thomas Dudziak</a>
 */
public class FirebirdBuilder extends SqlBuilder
{
    public FirebirdBuilder()
    {
        setPrimaryKeyEmbedded(true);
        setForeignKeysEmbedded(false);
        setCommentPrefix("/*");
        setCommentSuffix("*/");
        addNativeTypeMapping(Types.BIGINT,        "DECIMAL(18,0)");
        addNativeTypeMapping(Types.BINARY,        "BLOB");
        addNativeTypeMapping(Types.BIT,           "DECIMAL(1,0)");
        addNativeTypeMapping(Types.BOOLEAN,       "DECIMAL(1,0)");
        addNativeTypeMapping(Types.CLOB,          "BLOB SUB_TYPE TEXT");
        addNativeTypeMapping(Types.DOUBLE,        "DOUBLE PRECISION");
        addNativeTypeMapping(Types.LONGVARBINARY, "BLOB");
        addNativeTypeMapping(Types.LONGVARCHAR,   "BLOB SUB_TYPE TEXT");
        addNativeTypeMapping(Types.REAL,          "FLOAT");
        addNativeTypeMapping(Types.TINYINT,       "SMALLINT");
        addNativeTypeMapping(Types.VARBINARY,     "BLOB");
    }

    /* (non-Javadoc)
     * @see org.apache.commons.sql.builder.SqlBuilder#getDatabaseName()
     */
    public String getDatabaseName()
    {
        return "Firebird";
    }

    /* (non-Javadoc)
     * @see org.apache.commons.sql.builder.SqlBuilder#dropDatabase(org.apache.commons.sql.model.Database)
     */
    public void dropDatabase(Database database) throws IOException
    {
        super.dropDatabase(database);
        print("COMMIT");
        printEndOfStatement();
    }

    /* (non-Javadoc)
     * @see org.apache.commons.sql.builder.SqlBuilder#createTables(org.apache.commons.sql.model.Database)
     */
    public void createTables(Database database) throws IOException
    {
        super.createTables(database);
        print("COMMIT");
        printEndOfStatement();
    }

    /* (non-Javadoc)
     * @see org.apache.commons.sql.builder.SqlBuilder#writeExternalForeignKeyCreateStmt(org.apache.commons.sql.model.Table, org.apache.commons.sql.model.ForeignKey, int)
     */
    protected void writeExternalForeignKeyCreateStmt(Table table, ForeignKey key, int numKey) throws IOException
    {
        super.writeExternalForeignKeyCreateStmt(table, key, numKey);
        if (key.getForeignTable() != null)
        {
            print("COMMIT");
            printEndOfStatement();
        }
    }

    /* (non-Javadoc)
     * @see org.apache.commons.sql.builder.SqlBuilder#createTable(org.apache.commons.sql.model.Table)
     */
    public void createTable(Table table) throws IOException
    {
        super.createTable(table);

        // creating generator and trigger for auto-increment
        Column column = table.getAutoIncrementColumn();

        if (column != null)
        {
            print("CREATE GENERATOR gen_");
            print(table.getName());
            print("_");
            print(column.getName());
            printEndOfStatement();
            print("CREATE TRIGGER trg_");
            print(table.getName());
            print("_");
            print(column.getName());
            print(" FOR ");
            println(table.getName());
            println("ACTIVE BEFORE INSERT POSITION 0");
            println("AS");
            println("BEGIN");
            print("IF (NEW.");
            print(column.getName());
            println(" IS NULL) THEN");
            print("NEW.");
            print(column.getName());
            print(" = GEN_ID(gen_");
            print(table.getName());
            print("_");
            print(column.getName());
            println(", 1);");
            print("END");
            printEndOfStatement();
        }
    }

    /* (non-Javadoc)
     * @see org.apache.commons.sql.builder.SqlBuilder#dropTable(org.apache.commons.sql.model.Table)
     */
    public void dropTable(Table table) throws IOException
    {
        // dropping generator and trigger for auto-increment
        Column column = table.getAutoIncrementColumn();

        if (column != null)
        {
            print("DROP TRIGGER trg_");
            print(table.getName());
            print("_");
            print(column.getName());
            printEndOfStatement();
            print("DROP GENERATOR gen_");
            print(table.getName());
            print("_");
            print(column.getName());
            printEndOfStatement();
        }
        super.dropTable(table);
    }

    /* (non-Javadoc)
     * @see org.apache.commons.sql.builder.SqlBuilder#printAutoIncrementColumn(Table,Column)
     */
    protected void writeColumnAutoIncrementStmt(Table table, Column column) throws IOException
    {
        // we're using a generator
    }
}
