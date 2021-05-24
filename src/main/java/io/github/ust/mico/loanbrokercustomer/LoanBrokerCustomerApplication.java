package io.github.ust.mico.loanbrokercustomer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoanBrokerCustomerApplication {

    @Autowired
    static Sender sender;

    public static void main(String[] args) {
        SpringApplication.run(LoanBrokerCustomerApplication.class, args);

        new MessageGenerator(sender);
    }

}
