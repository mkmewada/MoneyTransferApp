The Asset Management Digital Challenge
======================================

This is a Spring Boot RESTful application which can serve the following Account operations:

 * Creating a new account
 * Reading an account
 * Making a transfer between two accounts

How to run
----------

Build the project using gradle. From project root:

```
./gradlew clean build
```

Once the project is built, you can run the jar as follows:

```
java -jar build/libs/Java\ Challenge-0.0.1-SNAPSHOT.jar
```

Example request:

```
curl -i -X PUT \
   -H "Content-Type:application/json" \
   -d \
'{
  "accountFromId":"Id-1",
  "accountToId":"Id-2",
  "amount": "1.55"
}' \
 'http://localhost:18080/v1/accounts/transfer'
```

Assumptions
-----------

* You cannot make a transfer to the same account ID. In other words, you cannot send money to yourself.
* When making a transfer the amount is in pounds / dollars not in pence / cents. For example, 100.45.

Further improvements
--------------------

* In RESTful services, it is not unusual to see the same request coming twice. In a production system we should
handle those hiccups. One idea would be to perform a 2-step transfer. In the first step we create a transaction ID
and then we request a transfer given this transaction ID. Once a transfer is made the transaction ID cannot be used
anymore.
* Instead of having a concurrent hashmap, in production we could have a database for storing the accounts
as well as for scalability.
* Another advantage of using a database is that we can handle transactional atomic account updates.
We must have both accounts successfully updated or none.
* I assumed that an amount can be transferred regardless of the number of decimal places the amount has.
Ideally, there should be some business logic around handling requests with more than 2 decimal places (for example 100.254).
 That would probably be a Bad Request. A simpler approach could only take pence/cents as monetary amount, but to be consistent with
  Create/Read Account I used the decimal amount.