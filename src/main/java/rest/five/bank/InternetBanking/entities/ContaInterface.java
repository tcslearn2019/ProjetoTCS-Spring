package rest.five.bank.InternetBanking.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import rest.five.bank.InternetBanking.model.Conta;

public interface ContaInterface extends JpaRepository<Conta, Long> {
    Conta findByFkIdCliente(Long idCliente);

    Boolean existsByFkIdCliente(Long idCliente);
}