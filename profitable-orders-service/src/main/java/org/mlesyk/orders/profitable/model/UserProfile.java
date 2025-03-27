package org.mlesyk.orders.profitable.model;

public class UserProfile {
    // 0-5
    private Integer accountingLevel;

    // 0-5
    private Integer brokerRelationsLevel;

    // 0-10
    private Double factionStanding;

    // 0-10
    private Double corpStanding;

    public UserProfile() {
        accountingLevel = 0;
        brokerRelationsLevel = 0;
        factionStanding = 0.0;
        corpStanding = 0.0;
    }

    public UserProfile(Integer accountingLevel, Integer brokerRelationsLevel, Double factionStanding, Double corpStanding) {
        this.accountingLevel = accountingLevel;
        this.brokerRelationsLevel = brokerRelationsLevel;
        this.factionStanding = factionStanding;
        this.corpStanding = corpStanding;
    }

    public Integer getAccountingLevel() {
        return accountingLevel;
    }

    public void setAccountingLevel(Integer accountingLevel) {
        this.accountingLevel = accountingLevel;
    }

    public Integer getBrokerRelationsLevel() {
        return brokerRelationsLevel;
    }

    public void setBrokerRelationsLevel(Integer brokerRelationsLevel) {
        this.brokerRelationsLevel = brokerRelationsLevel;
    }

    public Double getFactionStanding() {
        return factionStanding;
    }

    public void setFactionStanding(Double factionStanding) {
        this.factionStanding = factionStanding;
    }

    public Double getCorpStanding() {
        return corpStanding;
    }

    public void setCorpStanding(Double corpStanding) {
        this.corpStanding = corpStanding;
    }
}
