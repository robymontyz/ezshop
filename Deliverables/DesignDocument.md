# Design Document

Authors: Roberto Alessi (290180), Michelangelo Bartolomucci (292422), Gianvito Marzo (281761), Roberto Torta (290184)

Date: 03/06/2021

Version: 2.3

# Contents

- [High level design](#package-diagram)
- [Low level design](#class-diagram)
- [Verification traceability matrix](#verification-traceability-matrix)
- [Verification sequence diagrams](#verification-sequence-diagrams)

# High level design

EZShop is a standalone application working indipendently on a single cash register. In the implementation, the façade class is EZShop.

```plantuml
@startuml
scale 1024 width
left to right direction
allow_mixing

package it.polito.ezshop {
    package it.polito.ezshop.gui {}
    
    package it.polito.ezshop.exceptions {}
   
    package it.polito.ezshop.data {
        interface it.polito.ezshop.data.EZShopInterface
        interface it.polito.ezshop.data.BalanceOperation
        interface it.polito.ezshop.data.Customer
        interface it.polito.ezshop.data.Order
        interface it.polito.ezshop.data.ProductType
        interface it.polito.ezshop.data.SaleTransaction
        interface it.polito.ezshop.data.TicketEntry
        interface it.polito.ezshop.data.User
        class it.polito.ezshop.data.MyBalanceOperation
        class it.polito.ezshop.data.MyCreditCard
        class it.polito.ezshop.data.MyCustomer
        class it.polito.ezshop.data.MyOrder
        class it.polito.ezshop.data.MyProductType
        class it.polito.ezshop.data.MySaleTransaction
        class it.polito.ezshop.data.MyTicketEntry
        class it.polito.ezshop.data.MyUser
        class it.polito.ezshop.data.EZShop
    }
}


it.polito.ezshop.data.EZShop .|> it.polito.ezshop.data.EZShopInterface
it.polito.ezshop.data.MyBalanceOperation .|> it.polito.ezshop.data.BalanceOperation
it.polito.ezshop.data.MyOrder .|> it.polito.ezshop.data.Order
it.polito.ezshop.data.MyUser .|> it.polito.ezshop.data.User
it.polito.ezshop.data.MyCustomer .|> it.polito.ezshop.data.Customer
it.polito.ezshop.data.MySaleTransaction .|> it.polito.ezshop.data.SaleTransaction
it.polito.ezshop.data.MyTicketEntry .|> it.polito.ezshop.data.TicketEntry
it.polito.ezshop.data.MyCreditCard .|> it.polito.ezshop.data.CreditCard
it.polito.ezshop.data.MyProductType .|> it.polito.ezshop.data.ProductType

it.polito.ezshop.data.MyBalanceOperation <.. "<<extends>>" it.polito.ezshop.data.MySaleTransaction
it.polito.ezshop.data.EZShop <-up- it.polito.ezshop.gui
it.polito.ezshop.exceptions <-- it.polito.ezshop.data.EZShop

@enduml
```

# Low level design

```plantuml
@startuml
top to bottom direction
scale 1024 width 
scale 768 height

package it.polito.ezshop.data{

    class MyUser <<Persistent>> {
        - id: Integer
        - username: String
        - password: String
        - role : enum{"Administrator"|"Cashier"|"ShopManager"}
    }

    class MyProductType <<Persistent>> {
        - id: Integer
        - productCode: String
        - description: String
        - pricePerUnit: double
        - quantity: Integer
        - notes: String
        - location: String
        
        + {static} validateProductCode()
    }

    class MyOrder <<Persistent>> {
        - orderId: Integer
        - productCode: String
        - pricePerUnit: double
        - quantity: Integer
        - status: enum{ISSUED|ORDERED|COMPLETED}
    }

    class MyCustomer <<Persistent>> {
        - customerId: Integer
        - loyaltyCardId: String
        - customerName: String
        - points: Integer
    }

    class MyBalanceOperation <<Persistent>> {
        - balanceId: Integer
        - date: LocalDate
        - money: double
        - type: enum{DEBIT|CREDIT} 
    }

    class MySaleTransaction {
        - transactionId: Integer
        - entries: List<MyTicketEntry>
        - discountRate: double
        - price: double
    }

    class MyCreditCard <<Persistent>> {
        - cardNumber: String
        - balance: double
        
        + {static} validateWithLuhn()
    }

    class MyTicketEntry {
        - barCode: String
        - productDescription: String
        - amount: int
        - pricePerUnit: double
        - discountRate: double
    }


    class EZShop{
        + reset()
        + createUser()
        + deleteUser()
        + getAllUsers()
        + getUser()
        + updateUserRights()
        + login()
        + logout()
        + createProductType()
        + updateProduct()
        + deleteProductType()
        + getAllProductTypes()
        + getProductTypeByBarCode()
        + getProductTypesByDescription()
        + updateQuantity()
        + updatePosition()
        + removePosition()
        + issueOrder()
        + payOrderFor()
        + payOrder()
        + recordOrderArrival()
        + getAllOrders()
        + defineCustomer()
        + modifyCustomer()
        + deleteCustomer()
        + getCustomer()
        + getAllCustomers()
        + createCard()
        + attachCardToCustomer()
        + modifyPointsOnCard()
        + startSaleTransaction()
        + addProductToSale()
        + deleteProductFromSale()
        + applyDiscountRateToProduct()
        + applyDiscountRateToSale()
        + computePointsForSale()
        + deleteSaleTransaction()
        + endSaleTransaction()
        + getSaleTransaction()
        + startReturnTransaction()
        + returnProduct()
        + endReturnTransaction()
        + deleteReturnTransaction()
        + receiveCashPayment()
        + receiveCreditCardPayment()
        + returnCashPayment()
        + returnCreditCardPayment()
        + recordBalanceUpdate()
        + getCreditsAndDebits()
        + computeBalance()
        + addProducttoSaleRFID()
        + deleteProductFromSaleRFID()
        + returnProductRFID()
        + recordOrderArrivalRFID()
    }
}

MySaleTransaction --> MyTicketEntry
MyTicketEntry --> "*" MyProductType
MyOrder "*" --> MyProductType
MySaleTransaction --|> MyBalanceOperation

@enduml
```

# Verification traceability matrix

| FR  | MyCustomer | MyProductType | MySaleTransaction | MyException | MyOrder | MyUser | MyTicketEntry | MyBalanceOperation | MyCreditCard | EZShop |
| --- | :--------: | :-----------: | :---------------: | :---------: | :-----: | :----: | :-----------: | :----------------: | :----------: | :----: |
| FR1 |            |               |                   |           x |         |      x |               |                    |              |      x |
| FR3 |            |             x |                   |           x |       x |      x |               |                  x |              |      x |
| FR4 |            |             x |                 x |           x |       x |      x |               |                  x |              |      x |
| FR5 |          x |               |                   |           x |         |      x |               |                    |              |      x |
| FR6 |          x |             x |                 x |           x |         |      x |             x |                  x |              |      x |
| FR7 |          x |               |                 x |           x |         |      x |               |                  x |            x |      x |
| FR8 |            |               |                   |           x |       x |      x |               |                  x |              |      x |

# Verification sequence diagrams

## Use Case 1

### Scenario 1-1

```plantuml
@startuml
autonumber
EZShopGUI -> EZShop : createProductType()
EZShop -> MyProductType : new MyProductType()
MyProductType --> EZShop : MyProductType
EZShopGUI <-- EZShop : Integer:id
EZShopGUI -> EZShop : updatePosition()
EZShop -> MyProductType : setPosition()
EZShopGUI <-- EZShop : boolean:true
@enduml
```

### Scenario 1-3

```plantuml
@startuml
autonumber
EZShopGUI -> EZShop : getProductTypeByBarcode()
EZShop -> MyProductType : new MyProductType() 
MyProductType --> EZShop : MyProductType
EZShop -> EZShopGUI : MyProductType
EZShopGUI -> EZShop : updateProduct()
EZShop -> EZShopGUI : boolean:true
@enduml
```

## Use Case 2

### Scenario 2-1

```plantuml
@startuml
autonumber
EZShopGUI -> EZShop : createUser()
EZShopGUI <-- EZShop : Integer :id
EZShopGUI -> EZShop : updateUserRights()
EZShop --> EZShopGUI : boolean : true
@enduml
```

### Scenario 2-2

```plantuml
@startuml
autonumber
EZShopGUI -> EZShop : deleteUser()
EZShopGUI <-- EZShop : boolean:true
@enduml
```

## Use Case 3

### Scenario 3-1

```plantuml
@startuml
autonumber
EZShopGUI -> EZShop : issueOrder()
EZShop -> EZShop : getProductTypeByBarcode()
EZShop -> MyProductType : new MyProductType()
MyProductType --> EZShop : MyProductType
EZShop --> EZShop : MyProductType
EZShopGUI <-- EZShop : Integer : id
@enduml
```

### Scenario 3-2

```plantuml
@startuml
autonumber
EZShopGUI -> EZShop : payOrder()
EZShop -> EZShop : recordBalanceUpdate()
EZShop --> EZShop : boolean:true
EZShop --> EZShopGUI : boolean:true
@enduml
```

### Scenario 3-3

```plantuml
@startuml
autonumber
EZShopGUI -> EZShop : recordOrderArrival()
EZShop -> EZShop : getProductTypeByBarcode()
EZShop -> MyProductType : new MyProductType()
MyProductType --> EZShop : MyProductType
EZShop --> EZShop : MyProductType
EZShop -> MyProductType : getLocation()
MyProductType --> EZShop : String
EZShop -> EZShop : updateQuantity()
EZShop --> EZShop : boolean: true
EZShop --> EZShopGUI : boolean: true
@enduml
```

## Use Case 4

### Scenario 4-1

```plantuml
@startuml
autonumber
EZShopGUI -> EZShop : defineCustomer()
EZShop --> EZShopGUI : Integer : id
@enduml
```

### Scenario 4-2

```plantuml
@startuml
autonumber
EZShopGUI -> EZShop : attachCardToCustomer()
EZShop -> EZShopGUI : boolean : true
@enduml
```

### Scenario 4-4

```plantuml
@startuml
autonumber
EZShopGUI -> EZShop : modifyCustomer()
EZShop -> MyProductType : static validateProductCode()
MyProductType --> EZShop : boolean:true
EZShop --> EZShopGUI : boolean:true
@enduml
```

## Use Cases 5

### Scenario 5-1

```plantuml
@startuml
autonumber
EZShopGUI -> EZShop : login()
EZShop -> MyUser : new MyUser()
EZShop <-- MyUser : MyUser
EZShop -> EZShopGUI : MyUser
@enduml
```

### Scenario 5-2

```plantuml
@startuml
autonumber
EZShopGUI -> EZShop : getUser()
EZShop -> MyUser : new MyUser()
EZShop <-- MyUser : MyUser
EZShopGUI <-- EZShop : MyUser
EZShopGUI -> EZShop : logout()
EZShopGUI <-- EZShop : boolean:true
@enduml
```

## Use Cases 6

### Scenario 6-1

```plantuml
@startuml
autonumber
EZShopGUI -> EZShop : startSaleTransaction()
EZShopGUI <-- EZShop : Integer:id
EZShopGUI -> EZShop : addProductToSale()
EZShop -> EZShop : getProductTypeByBarCode()
EZShop -> MyProductType : new MyProductType()
EZShop <-- MyProductType : MyProductType
EZShop -> EZShop : updateQuantity()
EZShop <-- EZShop : boolean:true
EZShopGUI -> EZShop : endSaleTransaction()
EZShopGUI <-- EZShop : boolean:true
EZShopGUI -> EZShop : UC7
EZShopGUI <-- EZShop : double: change>=0
@enduml
```

### Scenario 6-2

```plantuml
@startuml
autonumber
EZShopGUI -> EZShop : startSaleTransaction()
EZShopGUI -> EZShop : Integer: id
EZShopGUI -> EZShop : addProductToSale()
EZShop -> EZShop : getProductTypeByBarCode()
EZShop -> MyProductType : new MyProductType()
EZShop <-- MyProductType : MyProductType: MyProductType
EZShop -> EZShop : updateQuantity()
EZShop <-- EZShop : boolean:true
EZShopGUI -> EZShop : applyDiscountRateToProduct()
EZShopGUI <-- EZShop : boolean:true
EZShopGUI -> EZShop : endSaleTransaction()
EZShopGUI <-- EZShop : boolean:true
EZShopGUI -> EZShop : UC7
EZShopGUI <-- EZShop : double: change>=0
@enduml
```

### Scenario 6-4

```plantuml
@startuml
autonumber
EZShopGUI -> EZShop : startSaleTransaction()
EZShopGUI -> EZShop : Integer:id
EZShopGUI -> EZShop : addProductToSale()
EZShop -> EZShop : getProductTypeByBarCode()
EZShop -> MyProductType : new MyProductType()
EZShop <-- MyProductType : MyProductType
EZShop -> EZShop : updateQuantity()
EZShop <-- EZShop : boolean:true
EZShopGUI -> EZShop : endSaleTransaction()
EZShopGUI <-- EZShop : boolean: true 
EZShop -> EZShop : UC7
EZShop <-- EZShop : double: change>=0
EZShop <-- EZShop : boolean: true
EZShopGUI --> EZShop : computePointsForSale()
EZShopGUI <-- EZShop : Integer: points
EZShopGUI --> EZShop : modifyPointsOnCard()
EZShopGUI <-- EZShop : boolean:true
@enduml
```

### Scenario 6-5

```plantuml
@startuml
autonumber
EZShopGUI -> EZShop : startSaleTransaction()
EZShopGUI <-- EZShop : Integer:id 
EZShopGUI -> EZShop : addProductToSale()
EZShop -> EZShop : getProductTypeByBarCode()
EZShop -> MyProductType : MyProductType: new MyProductType()
EZShop <-- MyProductType : MyProductType
EZShop -> EZShop : updateQuantity()
EZShop <-- EZShop : boolean: true
EZShopGUI -> EZShop : endSaleTransaction() 
EZShopGUI <-- EZShop : boolean: true
EZShop -> EZShop : UC7
EZShop <-- EZShop : double: change=-1
EZShop <-- EZShop : boolean:false
EZShopGUI -> EZShop : deleteSaleTransaction()
EZShopGUI <-- EZShop : boolean:true
@enduml
```

## Use Case 7

### Scenario 7-4

```plantuml
@startuml
autonumber
EZShopGUI -> EZShop : receiveCashPayment()
EZShop -> EZShop : recordBalanceUpdate()
EZShop <-- EZShop : boolean: true
EZShop --> EZShopGUI : double: change
@enduml
```

## Use Case 8

### Scenario 8-1

```plantuml
@startuml
autonumber
EZShopGUI -> EZShop : startReturnTransaction()
EZShopGUI <-- EZShop : Integer: id
EZShop -> EZShop : getProductTypeByBarCode()
EZShop -> MyProductType : new MyProductType()
EZShop <-- MyProductType :  MyProductType
EZShopGUI -> EZShop : addProductToSale()
EZShopGUI <-- EZShop : boolean: true
EZShopGUI -> EZShop : returnProduct()
EZShopGUI <-- EZShop : boolean: true
EZShopGUI -> EZShop : endReturnTransaction()
EZShop -> EZShop : updateQuantity()
EZShop <-- EZShop : boolean: true
EZShopGUI <-- EZShop : boolean: true
EZShopGUI -> EZShop : returnCreditCardPayment
EZShop -> EZShop : recordBalanceUpdate()
EZShop <-- EZShop : boolean:true
EZShopGUI <-- EZShop : double: total
@enduml
```

## Use Case 9

### Scenario 9-1

```plantuml
@startuml
autonumber
EZShopGUI -> EZShop : getCreditsAndDebits()
EZShop -> MyBalanceOperation : new MyBalanceOperation()
EZShop <-- MyBalanceOperation : MyBalanceOperation: MyBalanceOperation
EZShop -> MyBalanceOperation : new MyBalanceOperation()
EZShop <-- MyBalanceOperation : MyBalanceOperation
EZShop -> MyBalanceOperation : ...
EZShop <-- MyBalanceOperation : ...
EZShopGUI <-- EZShop  : ArrayList<MyBalanceOperation>
@enduml
```

## Use Case 10

### Scenario 10-2

```plantuml
@startuml
autonumber
EZShopGUI -> EZShop : returnCashPayment()
EZShop -> EZShop : recordBalanceUpdate()
EZShop --> EZShop : boolean:true
EZShop --> EZShopGUI : double: return
@enduml
```
