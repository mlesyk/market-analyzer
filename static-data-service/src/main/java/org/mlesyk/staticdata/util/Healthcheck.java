package org.mlesyk.staticdata.util;

import org.springframework.stereotype.Component;

@Component
public class Healthcheck {
    private boolean sdeDownloaded;
    private boolean systemRepositoryInitialized;
    private boolean itemGroupRepositoryInitialized;

    public Healthcheck() {
        this.sdeDownloaded = false;
        this.systemRepositoryInitialized = false;
        this.itemGroupRepositoryInitialized = false;
    }

    public void setSdeDownloaded(boolean sdeDownloaded) {
        this.sdeDownloaded = sdeDownloaded;
    }

    public void setSystemRepositoryInitialized(boolean systemRepositoryInitialized) {
        this.systemRepositoryInitialized = systemRepositoryInitialized;
    }

    public void setItemGroupRepositoryInitialized(boolean marketGroupRepositoryInitialized) {
        this.itemGroupRepositoryInitialized = marketGroupRepositoryInitialized;
    }

    public boolean isServiceHealthy() {
        return sdeDownloaded && systemRepositoryInitialized && itemGroupRepositoryInitialized;
    }
}
