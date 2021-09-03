# Unit Testing Documentation

Authors: Roberto Alessi (290180), Michelangelo Bartolomucci (292422), Gianvito Marzo (281761), Roberto Torta (290184)

Date: 03/06/2021

Version: 1.1

# Contents

- [Black Box Unit Tests](#black-box-unit-tests)

- [White Box Unit Tests](#white-box-unit-tests)

# Black Box Unit Tests - EZShop

### **All *it.polito.ezshop.data* Classes - all methods *setters***

Despite we already tested the setters methods through JUnits tests, we decided to omit them from this documentation because their testing is very trivial since all the correct data checks are made in the DB with ad-hoc constraints and get handled by SQLExceptions in EZShop Class.

 ### **Class *it.polito.ezshop.data.MyProductType* - method *validateProductCode***

**Criteria for method *validateProductCode*:**
	
 - Validity of productCode

**Predicates for method *validateProductCode*:**

| Criteria                | Predicate      |
| ----------------------  | -------------- |
| Validity of productCode |      true      |
|                         |      false     |

**Boundaries**:

| Criteria                | Boundary values |
| ----------------------- | --------------- |
| Validity of productCode | none            |

**Combination of predicates**:
| Validity of productCode | Valid / Invalid | Description of the test case   | JUnit test case           |
|-------------------------|-----------------|--------------------------------|---------------------------|
|  Valid(14)              |   Valid         | T1("11234567890125")   ->true  | testValidationProductCode |
|  Invalid(14)            |   Invalid       | T2("12345678901234")   ->false | testValidationProductCode |
|  Valid(13)              |   Valid         | T3("1234567890128")    ->true  | testValidationProductCode |
|  Valid(12)              |   Valid         | T4("123456789012")     ->true  | testValidationProductCode |
|  Invalid(9)             |   Invalid       | T5("123456789")        ->false | testValidationProductCode |
|  Invalid(17)            |   Invalid       | T6("12345678901111112")->false | testValidationProductCode |
|  Valid(14)              |   Valid         | T7("11234567890200")   ->true  | testValidationProductCode |

### **Class *it.polito.ezshop.data.MyCreditCard* - method *validateWithLuhn***

**Criteria for method *validateWithLuhn*:**
	
 - Validity of cardNumber

**Predicates for method *validateWithLuhn*:**

| Criteria                | Predicate      |
| ----------------------  | -------------- |
| Validity of cardNumber  |      true      |
|                         |      false     |

**Boundaries**:

| Criteria               | Boundary values |
| ---------------------- | --------------- |
| Validity of cardNumber | none            |

**Combination of predicates**:
| Validity of cardNumber  | Valid / Invalid | Description of the test case | JUnit test case           |
|-------------------------|-----------------|------------------------------|---------------------------|
|  Valid                  |   Valid         | T1("4556737586899855")->true | testValidationWithLuhn    |
|  Invalid                |   Invalid       | T2("4324332424")      ->false| testValidationWithLuhn    |

# White Box Unit Tests - EZShop

### **Class *it.polito.ezshop.data.MyProductType* - method *validateProductCode***

There's no need to execute White Box tests for this method because with BlackBox testing we can reach the 100% coverage of the method's code.

### **Class *it.polito.ezshop.data.MyCreditCard* - method *validateWithLuhn***

There's no need to execute White Box tests for this method because with BlackBox testing we can reach the 100% coverage of the method's code.