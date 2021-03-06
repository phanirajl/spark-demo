package no.sasoria.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import com.datastax.driver.core.schemabuilder.SchemaStatement;
import com.google.common.collect.ImmutableMap;
import com.datastax.driver.core.exceptions.AlreadyExistsException;

/**
 * An instance of this class runs Datastax' cassandra-driver to create a @code keyspace}
 * and {@code table} for movies. 
 * 
 * keyspace: movielens
 * table: movies
 * 
 */
public class CassandraApp {
	public static void main(String[] args) {
		Cluster cluster = Cluster.builder()
				.withClusterName("cassandraCluster")
				.addContactPoint("127.0.0.1")
				.build();

		Session session = cluster.connect();
		
		try {
			// Create keyspace
			session.execute(createKeyspaceQuery());
		} catch(AlreadyExistsException e) {
			System.out.println(e);
		}
		
		try {
			// Create table
			session.execute(createTableQuery());
		} catch(AlreadyExistsException e) {
			System.out.println(e);
		}
		
		session.close();
		cluster.close();
	}

	/**
	 * Creates a keyspace movielens with replicationfactor 1.
	 * @return
	 */
	private static SchemaStatement createKeyspaceQuery() {
		//https://docs.datastax.com/en/latest-java-driver-api/com/datastax/driver/core/schemabuilder/class-use/Create.html
		return SchemaBuilder.createKeyspace("movielens")
				.with()
				// replication factor 1 to avoid QueryExecutor: Error in spark with the cassandra-connector.
				.replication(ImmutableMap.<String,Object>of("class", "SimpleStrategy", "replication_factor", 1));
	}
	
	/**
	 * Creates a table movies of with id, title and genres columns.
	 * @return
	 */
	private static SchemaStatement createTableQuery() {
		return SchemaBuilder.createTable("movielens", "movies")
				.addPartitionKey("id", DataType.text())
				.addColumn("title", DataType.text())
				.addColumn("genres", DataType.text());
		
	}
}
