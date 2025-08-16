package irk.staryo.model;

import irk.staryo.enums.FundingStage;

public class Startup {
    private String name;
    private String description;
    private String fundingStage;
    private Integer ticketSize;
    private String location;
    private Integer foundYear;
    private String sector;
    private ProceedsScenarioTrend proceedsScenarioTrend;

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
    public String getFundingStage() {return fundingStage;}
    public void setFundingStage(String fundingStage) {this.fundingStage = fundingStage;}
    public String getLocation() {return location;}
    public void setLocation(String location) {this.location = location;}
    public Integer getFoundYear() {return foundYear;}
    public void setFoundYear(Integer foundYear) {this.foundYear = foundYear;}
    public Integer getTicketSize() {return ticketSize;}
    public void setTicketSize(Integer ticketSize) {this.ticketSize = ticketSize;}
    public String getSector() { return sector;}
    public void setSector(String sector) {this.sector = sector;}
    public ProceedsScenarioTrend getProceedsScenarioTrend() {return proceedsScenarioTrend;}
    public void setProceedsScenarioTrend(ProceedsScenarioTrend proceedsScenarioTrend) {this.proceedsScenarioTrend = proceedsScenarioTrend;}

    @Override
    public String toString() {
        return "Startup{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", fundingStage='" + fundingStage + '\'' +
                ", ticketSize=" + ticketSize +
                ", location='" + location + '\'' +
                ", foundYear=" + foundYear +
                ", sector='" + sector + '\'' +
                ", proceedsScenarioTrend=" + proceedsScenarioTrend +
                '}';
    }
}
