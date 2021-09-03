package it.polito.ezshop.data;

public class MyOrder implements it.polito.ezshop.data.Order{

    private Integer id;
    private String productCode;
    private double pricePerUnit;
    private Integer quantity;
    private String status;
    private Integer balanceId;

    public MyOrder(Integer id){

        this.id=id;
    }

    public MyOrder(Integer id, String productCode, double pricePerUnit, Integer quantity, String status){
        this.id = id;
        this.productCode = productCode;
        this.pricePerUnit = pricePerUnit;
        this.quantity = quantity;
        this.status = status;
        this.balanceId = 0;
    }

    @Override
    public Integer getBalanceId(){
         return this.balanceId;
    }

    @Override
    public void setBalanceId(Integer balanceId){
        this.balanceId=balanceId;
    }

    @Override
    public String getProductCode() {
        return this.productCode;
    }

    @Override
    public void setProductCode(String productCode) {
        this.productCode=productCode;
    }

    @Override
    public double getPricePerUnit() {
        return this.pricePerUnit;
    }

    @Override
    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit=pricePerUnit;
    }

    @Override
    public int getQuantity() {
        return this.quantity;
    }

    @Override
    public void setQuantity(int quantity) {
        this.quantity=quantity;
    }

    @Override
    public String getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(String status) {
        this.status=status;
    }

    @Override
    public Integer getOrderId() {
        return  this.id;
    }

    @Override
    public void setOrderId(Integer orderId) {
        this.id=orderId;
    }
}
