package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.Period;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository){
		return (args -> {
			Client client1 = new Client("Melba","Morel", "melba@mindhub.com");


			Client client2 = new Client("Nazarena", "Macre","lalala@gmail.com");


			clientRepository.save(client1);
			clientRepository.save(client2);



			Account account1 = new Account();
			account1.setNumber("VIN001");
			account1.setBalance(5000.0);
			account1.setCreationDate(LocalDate.now());

			Account account2 = new Account();
			account2.setNumber("VIN002");
			account2.setBalance(7500.0);
			account2.setCreationDate(LocalDate.now().plus(Period.ofDays(1)));

			client1.addAccount(account1);
			client1.addAccount(account2);
			accountRepository.save(account1);
			accountRepository.save(account2);
		});
	}

}
