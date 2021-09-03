package it.polito.ezshop.data;

import java.util.List;

public class MySaleTransaction extends MyBalanceOperation implements it.polito.ezshop.data.SaleTransaction {
    private Integer transactionId;
    private List<TicketEntry> entries;
    private double discountRate;
    private double price;

    public MySaleTransaction(Integer transactionId, List<TicketEntry> entries, double discountRate, double price) {
        super();
        this.transactionId=transactionId;
        this.entries=entries;
        this.discountRate=discountRate;
        this.price=price;
    }

    @Override
    public Integer getTicketNumber() {
        return transactionId;
    }

    @Override
    public void setTicketNumber(Integer ticketNumber) {
        this.transactionId=ticketNumber;
    }

    @Override
    public List<TicketEntry> getEntries() {
        //list of entered products in the transactions/ticket
        return entries;
    }

    @Override
    public void setEntries(List<TicketEntry> entries) {
        this.entries=entries;
    }

    @Override
    public double getDiscountRate() {
        return discountRate;
    }

    @Override
    public void setDiscountRate(double discountRate) {
        this.discountRate=discountRate;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void setPrice(double price) {
        this.price=price;
    }
}
