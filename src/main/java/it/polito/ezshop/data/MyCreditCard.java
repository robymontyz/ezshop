package it.polito.ezshop.data;

public class MyCreditCard {
    private String cardNumber;
    private double balance;

    public MyCreditCard(String cardNumber, double balance) {
        this.cardNumber = cardNumber;
        this.balance = balance;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public static boolean validateWithLuhn(String creditCard) {
        int dimension = creditCard.length();

        int temp, sum = 0;
        for (int i = dimension - 1; i >= 0; i--) {
            temp = Character.getNumericValue((creditCard.charAt(i)));
            if (i % 2 == 0) {
                temp =  temp * 2;
                //sum += temp / 10;
                if (temp>9)
                    temp-=9;
            }
            sum += temp;
        }
        return ((sum % 10) == 0);//creditCard.charAt(dimension-1)-'0');
    }


}
