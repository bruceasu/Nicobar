package com.netflix.nicobar.cassandra.internal;

import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.query.RowSliceQuery;

/**
 * Hystrix command to get rows from Cassandra specified by a set of row keys.
 * See http://crlog.info/2011/06/13/cassandra-query-language-cql-v1-0-0-updated/
 * @param <RowKeyType> the type of the row key, String, Integer etc.
 * @author Vasanth Asokan, modified from hystrix command implementations in Zuul
 *         Zuul (https://github.com/Netflix/zuul)
 */
public class HystrixCassandraGetRowsByKeys<RowKeyType> extends AbstractCassandraHystrixCommand<Rows<RowKeyType, String>> {
    private final Keyspace keyspace;
    private final ColumnFamily<RowKeyType, String> columnFamily;
    private final RowKeyType[] rowKeys;
    private String[] columns;

    @SuppressWarnings("unchecked")
    public HystrixCassandraGetRowsByKeys(Keyspace keyspace, String columnFamilyName, RowKeyType... rowKeys) {
        this.keyspace = keyspace;
        this.columnFamily = getColumnFamilyViaColumnName(columnFamilyName, rowKeys[0]);
        this.rowKeys = rowKeys;
    }

    /**
     * Restrict the response to only these columns.
     *
     * Example usage: new HystrixCassandraGetRow(args).withColumns("column1",
     * "column2").execute()
     *
     * @param columns list of column names.
     * @return result row sets.
     */
    public HystrixCassandraGetRowsByKeys<RowKeyType> withColumns(String... columns) {
        this.columns = columns;
        return this;
    }

    @Override
    protected Rows<RowKeyType, String> run() throws Exception {
        RowSliceQuery<RowKeyType, String> rowQuery = null;
        rowQuery = keyspace.prepareQuery(columnFamily).getKeySlice(rowKeys);

        /* apply column slice if we have one */
        if (columns != null) {
            rowQuery = rowQuery.withColumnSlice(columns);
        }
        Rows<RowKeyType, String> result = rowQuery.execute().getResult();
        return result;
    }
}
