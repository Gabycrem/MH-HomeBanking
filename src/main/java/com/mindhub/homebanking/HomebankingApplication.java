package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository){
		return (args -> {
			//Creando clientes 1 y 2
			Client client1 = new Client("Melba","Morel", "melba@mindhub.com");
			Client client2 = new Client("Nazarena", "Macre","lalala@gmail.com");

			//Guardando clientes en sus Repo
			clientRepository.save(client1);
			clientRepository.save(client2);

			//Creando cuentas 1 y 2
			Account account1 = new Account("VIN001",LocalDate.now(), 5000.0);
			Account account2 = new Account("VIN002", LocalDate.now().plus(Period.ofDays(1)), 7500.0);

			//Asociando cuentas 1 y 2  a cliente1
			client1.addAccount(account1);
			client1.addAccount(account2);

			//Guardando cuentas en repo
			accountRepository.save(account1);
			accountRepository.save(account2);



			//Nuevas Cuentas para Task3
			Account account3 = new Account("VIN003", LocalDate.now(), 2500.0);
			Account account4 = new Account("VIN004", LocalDate.now(), 15000.0);
			/*Account account5 = new Account("VIN005", LocalDate.now(), 30000.0);
			Account account6 = new Account("VIN006", LocalDate.now(), 100000.0);*/

			//Asociando cuentas 3 y 4 a client2
			client2.addAccount(account3);
			client2.addAccount(account4);
			/*client2.addAccount(account5);
			client2.addAccount(account6);*/

			//Guardando en repo cuentas 3y4
			accountRepository.save(account3);
			accountRepository.save(account4);
			/*accountRepository.save(account5);
			accountRepository.save(account6);*/

			//Generando nuevas Transactions
			Transaction transaction1 = new Transaction(TransactionType.DEBIT, -500.0, "Débito -500", LocalDateTime.now());
			account1.addTransaction(transaction1);
			transactionRepository.save(transaction1);

			Transaction transaction2 = new Transaction(TransactionType.CREDIT, 2500.0, "Crédito $2500", LocalDateTime.now());
			account2.addTransaction(transaction2);
			transactionRepository.save(transaction2);

			Transaction transaction3 = new Transaction(TransactionType.DEBIT, -100.0, "Débito $100", LocalDateTime.now());
			account4.addTransaction(transaction3);
			transactionRepository.save(transaction3);

			Transaction transaction4 = new Transaction(TransactionType.CREDIT, 1500.0, "Crédito $1500", LocalDateTime.now());
			account1.addTransaction(transaction4);
			transactionRepository.save(transaction4);

			Transaction transaction5 = new Transaction(TransactionType.DEBIT, -1500.0, "Débito $1500", LocalDateTime.now());
			account4.addTransaction(transaction5);
			transactionRepository.save(transaction5);
		});
	}

}
