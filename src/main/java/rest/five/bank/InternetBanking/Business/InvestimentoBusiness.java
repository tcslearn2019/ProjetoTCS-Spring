package rest.five.bank.InternetBanking.Business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import rest.five.bank.InternetBanking.controller.dto.InvestimentoDTO;
import rest.five.bank.InternetBanking.entities.ContaInterface;
import rest.five.bank.InternetBanking.entities.InvestimentoInterface;
import rest.five.bank.InternetBanking.model.Conta;
import rest.five.bank.InternetBanking.model.Investimento;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@EnableScheduling
public class InvestimentoBusiness {
    @Autowired
    InvestimentoInterface investimentoInterface;
    @Autowired
    private ContaInterface contaInterface;


    //================================================================================
    // Verifica o tipo do investimento
    //================================================================================
    public String tipoDeInvestimento(Investimento investimento) {
        if (investimento.getNomeInvestimento().equals("POUPANCA")) {
            return "POUPANCA";
        } else if (investimento.getNomeInvestimento().equals("CDI")) {
            return "CDI";
        } else if (investimento.getNomeInvestimento().equals("IPCA")) {
            return "IPCA";
        } else return null;
    }

    //================================================================================
    // Realiza um novo investimento
    //================================================================================
    @Transactional
    public Object addInv(InvestimentoDTO investimentoDTO) {
        Optional<Conta> optConta = contaInterface.findById(investimentoDTO.getConta());
        if (existeInvestimento(investimentoDTO)) {
            if (validaInvestimento(investimentoDTO, optConta.get())) {
                Investimento investimento = investimentoDTO.retornaInvestimento(optConta.get());
                investimentoInterface.save(investimento);
            } else {
                return "Seu saldo é insuficiente";
            }
        }
        optConta.get().setSaldoConta(optConta.get().getSaldoConta() - investimentoDTO.getSaldo());
        return null;
    }

    //================================================================================
    // Verifica se existe o investimento
    //================================================================================
    @Transactional
    public boolean existeInvestimento(InvestimentoDTO investimentoDTO) {
        for (Investimento i : investimentoInterface.findAll()) {
            if (i.getConta().getNumConta().equals(investimentoDTO.getConta())) {
                if (i.getNomeInvestimento().equals(investimentoDTO.getNomeInvestimento().toUpperCase())) {
                    i.setSaldo(i.getSaldo() + investimentoDTO.getSaldo());
                    return false;
                }
            }
        }
        return true;
    }

    private Boolean validaInvestimento(InvestimentoDTO investimentoDTO, Conta conta) {
        return conta.getSaldoConta() >= investimentoDTO.getSaldo();
    }

    //================================================================================
    // Chama os valores para atualizar o investimento
    //================================================================================
    @Scheduled(cron = "0/5 * * * * ?")
    public void realizaInv() {
        List<Investimento> listaInv = retornaListaInv();
        for (Investimento inves : listaInv) {
            calcInvestimento(inves);
        }
    }

    //================================================================================
    // Retorna a lista de investimento
    //================================================================================
    public List<Investimento> retornaListaInv() {
        return investimentoInterface.findAll();
    }

    //================================================================================
    // Atualiza os valores dos investimento
    //================================================================================
    @Transactional
    public void calcInvestimento(Investimento inv) {
        if (inv.getNomeInvestimento().equals("CDI")) {
            inv.setSaldo(inv.getSaldo() * 1.05f);
        } else if (inv.getNomeInvestimento().equals("POUPANCA")) {
            inv.setSaldo(inv.getSaldo() * 1.01f);
        } else if (inv.getNomeInvestimento().equals("IPCA")) {
            inv.setSaldo(inv.getSaldo() * 1.03f);
        }
        investimentoInterface.save(inv);
    }

    //================================================================================
    // Deleta investimento
    //================================================================================
    public void deteleInv(Long id) {
        investimentoInterface.delete(investimentoInterface.findById(id).get());
    }

    //================================================================================
    // Devolve dinheiro
    //================================================================================
    @Transactional
    public Object tiraDinheiro(InvestimentoDTO invDto) {
        Optional<Investimento> inv = investimentoInterface.findById(invDto.getIdInvestimento());
        inv.get().setSaldo(inv.get().getSaldo() - invDto.getSaldo());
        Optional<Conta> optC = contaInterface.findById(inv.get().getConta().getNumConta());
        optC.get().setSaldoConta(optC.get().getSaldoConta() + invDto.getSaldo());
        if (inv.get().getSaldo() <= 0) {
            investimentoInterface.delete(inv.get());
        }
        return "Investimento retirando com sucesso";
    }

    //================================================================================
    // Lista os investimentos e retorna para o front
    //================================================================================
    public List<Investimento> retornaInvestimentos(Long numeroConta) {
        Optional<Conta> conta = contaInterface.findById(numeroConta);
        List<Investimento> listInvestimento = investimentoInterface.findAllByConta(conta.get());
        return listInvestimento;
    }
}
