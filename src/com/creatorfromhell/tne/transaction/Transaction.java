package com.creatorfromhell.tne.transaction;

import java.util.UUID;


public class Transaction {
  
  private TransactionType type;
  private String from;
  private String to = "";
  private Double amount = 0.0;
  private Double fromBalance = 0.0;
  private Double toBalance = 0.0;
  
  public Transaction(UUID from) {
    this.from = from.toString();
  }
  
  public Transaction(UUID from, UUID to) {
    this.from = from.toString();
    this.to = to.toString();
  }
  
  public Transaction(UUID from, UUID to, Double amount) {
    this.from = from.toString();
    this.to = to.toString();
    this.amount = amount;
  }
  
  @Override
  public String toString() {
    return "{"
            + "'from':'" + from.toString() + "'"
            + "'to':'" + to.toString() + "'"
            + "'amount':'" + amount + "'"
            + "'frombalance':'" + fromBalance + "'"
            + "'tobalance':'" + toBalance + "'"
            + "}";
  }

  
  public TransactionType getType() {
    return type;
  }

  
  public void setType(TransactionType type) {
    this.type = type;
  }

  
  public UUID getFrom() {
    return UUID.fromString(from);
  }

  
  public void setFrom(UUID from) {
    this.from = from.toString();
  }

  
  public UUID getTo() {
    return UUID.fromString(to);
  }

  
  public void setTo(UUID to) {
    this.to = to.toString();
  }

  
  public Double getAmount() {
    return amount;
  }

  
  public void setAmount(Double amount) {
    this.amount = amount;
  }

  
  public Double getFromBalance() {
    return fromBalance;
  }

  
  public void setFromBalance(Double fromBalance) {
    this.fromBalance = fromBalance;
  }

  
  public Double getToBalance() {
    return toBalance;
  }

  
  public void setToBalance(Double toBalance) {
    this.toBalance = toBalance;
  }
}