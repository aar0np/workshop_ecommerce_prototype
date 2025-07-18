package com.datastax.tutorials;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.oss.driver.api.core.CqlSession;
import com.dtsx.astra.sdk.db.domain.Database;

@SpringBootTest
public class Test01_Connectivity {

    @Autowired
    private AstraClient astraClient;
    
    @Autowired
    private CqlSession cqlSession;
    
    @Test
    void should_display_astraClient() {
        System.out.println("List Databases available in your Organization (AstraClient)");
        // astra-spring-boot-starter 0.4-
        // System.out.println("+ Your OrganizationID: " + astraClient.apiDevopsOrganizations().organizationId());
        // new version 0.5+
         System.out.println("+ Your OrganizationID: " + astraClient.apiDevops().getOrganizationId());
        System.out.println("+ Your Databases: ");
        astraClient.apiDevopsDatabases()
        // astra-spring-boot-starter 0.4-
        //		.databasesNonTerminated()
        // new version 0.5+
        	.findAllNonTerminated()
              	.forEach(this::displayDB);
    }
    
    @Test
    void should_display_cqlSession() {
        System.out.println("Execute some Cql (CqlSession)");
        System.out.println("+ Your Keyspace: " + cqlSession.getKeyspace().get());
        System.out.println("+ Product Categories: ");
        cqlSession.execute("SELECT name FROM category WHERE parent_id = ffdac25a-0244-4894-bb31-a0884bc82aa9")
                .all().stream()
                .map(r -> r.getString("name"))
                .forEach(System.out::println);
    }
    
    private void displayDB(Database db) {
        System.out.println(db.getInfo().getName() + "\t : id=" + db.getId() + ", region=" + db.getInfo().getRegion());
    }
    
}
