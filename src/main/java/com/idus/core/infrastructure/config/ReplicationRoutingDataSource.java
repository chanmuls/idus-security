package com.idus.core.infrastructure.config;

import com.idus.core.infrastructure.type.DatabaseType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        DatabaseType dataSourceType = DatabaseType.READ;

        if (! TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            dataSourceType = DatabaseType.WRITE;
        }

        log.info("### dataSourceType : {}", dataSourceType);

        return dataSourceType;
    }
}
