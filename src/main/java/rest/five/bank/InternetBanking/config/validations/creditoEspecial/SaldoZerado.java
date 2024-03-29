package rest.five.bank.InternetBanking.config.validations.creditoEspecial;

import org.springframework.beans.factory.annotation.Autowired;
import rest.five.bank.InternetBanking.controller.dto.CreditoDTO;
import rest.five.bank.InternetBanking.controller.dto.FixedClienteDTO;
import rest.five.bank.InternetBanking.entities.ContaInterface;
import rest.five.bank.InternetBanking.entities.CreditoEspecialInterface;
import rest.five.bank.InternetBanking.model.Conta;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SaldoZerado implements ConstraintValidator<TemDinheiro, CreditoDTO> {
    @Autowired
    ContaInterface cInterface;
    @Autowired
    CreditoEspecialInterface credInterface;
    @Override
    public boolean isValid(CreditoDTO creditoDTO, ConstraintValidatorContext constraintValidatorContext) {
        Conta optC = cInterface.findByFkIdCliente(FixedClienteDTO.returnCliente());
        if (optC.getSaldoConta() < 0) {
            return false;
        } else return !(optC.getSaldoConta() > 0) || optC.isExisteEmprestimo();
    }
}
