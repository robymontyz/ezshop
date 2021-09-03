package it.polito.ezshop.data;

public class MyCustomer implements it.polito.ezshop.data.Customer {

     private Integer customerId;
     private String loyaltyCardId="";
     private String customerName;
     private Integer points=0;

//     public Customer(String customerName) {
//         this.customerName=customerName;
//     }


     public MyCustomer(Integer customerId, String customerName, String loyaltyCardId, Integer points){
         this.customerId=customerId;
         this.loyaltyCardId=loyaltyCardId;
         this.customerName=customerName;
         this.points=points;
     }

    @Override
    public String getCustomerName() {
        return this.customerName;
    }

    @Override
    public void setCustomerName(String customerName) {
        this.customerName=customerName;
    }

    @Override
    public String getCustomerCard() {
        return this.loyaltyCardId;
    }

    @Override
    public void setCustomerCard(String customerCard) {
        this.loyaltyCardId=customerCard;
    }

    @Override
    public Integer getId() {
        return customerId;
    }

    @Override
    public void setId(Integer id) {
        this.customerId=id;
    }

    @Override
    public Integer getPoints() {
        return points;
    }

    @Override
    public void setPoints(Integer points) {
        this.points=points;
    }
}
