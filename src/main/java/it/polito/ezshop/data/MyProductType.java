package it.polito.ezshop.data;

public class MyProductType implements it.polito.ezshop.data.ProductType{
    private Integer id;
    private String productCode;
    private String description;
    private double pricePerUnit;
    private Integer quantity;
    private String notes;
    private String location;

    public MyProductType(Integer id, String productCode, String description, double pricePerUnit, Integer quantity, String notes, String location){
        this.id = id;
        this.productCode = productCode;
        this.description = description;
        this.pricePerUnit = pricePerUnit;
        this.quantity = quantity;
        this.notes = notes;
        this.location = location;
    }

    @Override
    public Integer getQuantity() {
        return this.quantity;
    }

    @Override
    public void setQuantity(Integer quantity) {
        this.quantity=quantity;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String getNote() {
        return this.notes;
    }

    @Override
    public void setNote(String note) {
        this.notes=note;
    }

    @Override
    public String getProductDescription() {
        return this.description;
    }

    @Override
    public void setProductDescription(String productDescription) {
        this.description=productDescription;
    }

    @Override
    public String getBarCode() {
        return this.productCode;
    }

    @Override
    public void setBarCode(String barCode) {
        this.productCode=barCode;
    }

    @Override
    public Double getPricePerUnit() {
        return this.pricePerUnit;
    }

    @Override
    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit=pricePerUnit;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id=id;
    }

    public static boolean validateProductCode(String barcode)
    {
        int sum=0,multiplier,ret;
        // 12<=length(barcode)<=14
        if(barcode.length()<12 || barcode.length()>14)
            return false;
        if(barcode.length() % 2 == 0)
        {
            // even length
            for (int i=barcode.length()-1; i>=0; i--)
            {
                if(i!=barcode.length()-1) {
                    if (i % 2 == 0) {
                        sum += (barcode.charAt(i) - '0') * 3;
                    } else {
                        sum += barcode.charAt(i) - '0';
                    }
                }
            }
        }
        else
        {
            // odd length
            for (int i=barcode.length()-1; i>=0; i--)
            {
                if(i!=barcode.length()-1) {
                    if (i % 2 == 0) {
                        sum += barcode.charAt(i) - '0';
                    } else {
                        sum += (barcode.charAt(i) - '0')*3;
                    }
                }
            }
        }

        multiplier = sum/10;
        ret = sum % 10;
        if(ret>0)
            multiplier++;

        return 10*multiplier-sum == (barcode.charAt(barcode.length()-1)-'0');
    }
}
