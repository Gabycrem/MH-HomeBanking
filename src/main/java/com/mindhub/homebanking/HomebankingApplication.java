package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository,
									  TransactionRepository transactionRepository, LoanRepository loanRepository,
									  ClientLoanRepository clientLoanRepository, CardRepository cardRepository){
		return (args -> {

			//---------------------------------------------------------------------//
			//                        Creando clientes 1 y 2                       //
			//---------------------------------------------------------------------//
			Client client1 = new Client("Melba","Morel", "melba@mindhub.com");
			Client client2 = new Client("Nazarena", "Macre","lalala@gmail.com");

			//
			//---------------------------------------------------------------------//
			//                    Guardando clientes en sus Repo                   //
			//---------------------------------------------------------------------//
			clientRepository.save(client1);
			clientRepository.save(client2);


			//---------------------------------------------------------------------//
			//                        Creando cuentas 1 y 2                        //
			//---------------------------------------------------------------------//
			Account account1 = new Account("VIN001",LocalDate.now(), 5000.0);
			Account account2 = new Account("VIN002", LocalDate.now().plus(Period.ofDays(1)), 7500.0);


			//---------------------------------------------------------------------//
			//            Asociando cuentas 1 y 2  a cliente1                      //
			//---------------------------------------------------------------------//
			client1.addAccount(account1);
			client1.addAccount(account2);


			//---------------------------------------------------------------------//
			//                  Guardando cuentas en repo                          //
			//---------------------------------------------------------------------//
			accountRepository.save(account1);
			accountRepository.save(account2);


			//---------------------------------------------------------------------//
			//                    Nuevas Cuentas para Task3                        //
			//---------------------------------------------------------------------//
			Account account3 = new Account("VIN003", LocalDate.now(), 2500.0);
			Account account4 = new Account("VIN004", LocalDate.now(), 15000.0);
			Account account5 = new Account("VIN005", LocalDate.now(), 30000.0);
			Account account6 = new Account("VIN006", LocalDate.now(), 100000.0);


			//---------------------------------------------------------------------//
			//                  Asociando cuentas 3 y 4 a client2                  //
			//---------------------------------------------------------------------//
			client2.addAccount(account3);
			client2.addAccount(account4);
			client2.addAccount(account5);
			client2.addAccount(account6);


			//---------------------------------------------------------------------//
			//                 Guardando en repo cuentas 3y4                       //
			//---------------------------------------------------------------------//
			accountRepository.save(account3);
			accountRepository.save(account4);
			accountRepository.save(account5);
			accountRepository.save(account6);

			//---------------------------------------------------------------------//
			//                     Generando nuevas Transactions                   //
			//---------------------------------------------------------------------//
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


			//---------------------------------------------------------------------//
			//                        Créditos para Task4                          //
			//---------------------------------------------------------------------//
			Loan loan1 = new Loan("Hipotecario", 500000.0, List.of(12,24,36,48,60));
			loanRepository.save(loan1);
			Loan loan2 = new Loan("Personal", 100000.0, List.of(6,12,24));
			loanRepository.save(loan2);
			Loan loan3 = new Loan("Automotriz", 300000.0, List.of(6,12,24,36));
			loanRepository.save(loan3);

			//---------------------------------------------------------------------//
			//                  Créditos para Melba y segundo client               //
			//---------------------------------------------------------------------//
			ClientLoan clientLoan1 = new ClientLoan(400000.0, 60, client1, loan1);
			client1.addClientLoans(clientLoan1);
			loan1.addClientLoans(clientLoan1);
			clientLoanRepository.save(clientLoan1);

			ClientLoan clientLoan2 = new ClientLoan(50000.0, 12, client1, loan2);
			loan2.addClientLoans(clientLoan2);
			client1.addClientLoans(clientLoan2);
			clientLoanRepository.save(clientLoan2);

			ClientLoan clientLoan3 = new ClientLoan(100000.0, 24, client2, loan2);
			client2.addClientLoans(clientLoan3);
			loan2.addClientLoans(clientLoan3);
			clientLoanRepository.save(clientLoan3);

			ClientLoan clientLoan4 = new ClientLoan(200000.0, 36, client2, loan3);
			loan3.addClientLoans(clientLoan4);
			client2.addClientLoans(clientLoan4);
			clientLoanRepository.save(clientLoan4);

			//---------------------------------------------------------------------//
			//          Tarjetas de Crédito para Melba y segundo client            //
			//---------------------------------------------------------------------//

			//---------CARGA DE DATOS CARD1 ------------//
			Card card1 = new Card();
			card1.setCardHolder(client1.getFirstName().toUpperCase()+" "+client1.getLastName().toUpperCase());
			card1.setColor(CardColor.GOLD);
			card1.setType(CardType.DEBIT);
			card1.setFromDate(LocalDate.now());
			card1.setThruDate(LocalDate.now().plusYears(5));
			card1.setNumber(2569654823584568l);
			card1.setCvv((short) 654);
			//--Asignación a client y Save en Repo ---//
			client1.addCard(card1);
			cardRepository.save(card1);

			//---------CARGA DE DATOS CARD2 ------------//
			Card card2 = new Card();
			card2.setCardHolder(client1.getFirstName().toUpperCase()+" "+client1.getLastName().toUpperCase());
			card2.setColor(CardColor.TITANIUM);
			card2.setType(CardType.CREDIT);
			card2.setFromDate(LocalDate.now());
			card2.setThruDate(LocalDate.now().plusYears(5));
			card2.setNumber(456932157896541236l);
			card2.setCvv((short) 852);
			//--Asignación a client y Save en Repo ---//
			client1.addCard(card2);
			cardRepository.save(card2);

			//---------CARGA DE DATOS CARD3 ------------//
			Card card3 = new Card();
			card3.setCardHolder(client2.getFirstName().toUpperCase()+" "+client2.getLastName().toUpperCase());
			card3.setColor(CardColor.SILVER);
			card3.setType(CardType.CREDIT);
			card3.setFromDate(LocalDate.now());
			card3.setThruDate(LocalDate.now().plusYears(5));
			card3.setNumber(4569945621581238l);
			card3.setCvv((short) 321);
			//--Asignación a client y Save en Repo ---//
			client2.addCard(card3);
			cardRepository.save(card3);
		});
	}

}
