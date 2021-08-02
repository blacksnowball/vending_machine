# Vending Machine Application

Vending Application is a command line interface program that simulates a vending machine allowing customers to purchase products, as well as specialised roles to make changes. 


## Features

Vending Application can be run using four different roles: **customer**, **seller**, **cashier**, and **owner**.

**Customer:**
- Create an account to retain previous purchases
- See list of products in the vending machine
- Make purchases
- Pay using card or cash

**Seller:**
- Add and modify products in the machine
- Create reports of available items and summary of products sold

**Cashier:**
- Modify the amount of cash and card details in the machine
- Create reports of amount of cash in the machine and list of transactions

**Owner:**
- All capabilities of seller and cashier
- Create reports of users and cancelled transactions


## Installation and Running

To run the program, Java 11 or later is required.

_As a client:_

The application can be executed by running the _vending_ file which can be found in `vending > bin` after unziping the _vending.zip_ folder.

_As a developer:_

[Gradle](https://gradle.org/install/) is required.

```gradle build```, then ```gradle run``` or ```gradle run --console=plain```(Recommended)


## List of Commands

***MENU COMMANDS***
- help: show this list of commands
- exit (or quit): shut down the machine
- login
- logout

***CUSTOMER COMMANDS***
- create account
- list products: preview of the vending machine contents
- make purchase

***SELLER COMMANDS***
- show inventory
- modify inventory / modify products
- inventory report
- sales report

***CASHIER COMMANDS***
- list change
- modify change
- change report
- transactions report

***OWNER COMMANDS***
- list cards
- modify cards
- add user
- remove user
- list users
- user report
- cancelled order report

## Testing

The unit tests for Vending Application can be run using the Gradle command:

```gradle test```

**Code coverage** information can be accessed at _build > reports > jacoco_

Information regarding **successful tests** can also be accessed at _build > reports > tests > test_

Note that ```gradle build``` needs to be executed to have access to these files.
