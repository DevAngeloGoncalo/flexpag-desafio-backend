package com.flexpag.paymentscheduler.controllers;

import com.flexpag.paymentscheduler.entities.EnumPaymentStatus;
import com.flexpag.paymentscheduler.entities.PaymentSchedulerEntity;
import com.flexpag.paymentscheduler.repositories.PaymentSchedulerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/agendamento")
public class PaymentSchedulerController {

    @Autowired
    private PaymentSchedulerRepository paymentSchedulerRepository;

    //Retorna uma lista de todos os agendamentos existentes no sistema.
    @GetMapping("/listar")
    public ResponseEntity<List<PaymentSchedulerEntity>> listarAgendamentos() {

        // Busca todos os agendamentos de pagamento no banco de dados e retorna um OK junto com a lista
        List<PaymentSchedulerEntity> agendamentos = paymentSchedulerRepository.findAll();
        return ResponseEntity.ok(agendamentos);
    }

    //Retorna o agendamento com o ID fornecido como parâmetro.
    @GetMapping("/consultarPorIdAgendamento/{id}")
    public ResponseEntity<PaymentSchedulerEntity> listarAgendamentosPorId(@PathVariable Long id) {

        // Busca um agendamento de pagamento pelo id informado na URL
        Optional<PaymentSchedulerEntity> agendamento = paymentSchedulerRepository.findById(id);

        // Se o agendamento foi encontrado, retorna uma resposta com status OK e o objeto encontrado
        // Caso contrário, retorna uma resposta com status 404 (não encontrado)
        return agendamento.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    //Retorna uma lista de todos os agendamentos de um cliente com o ID fornecido como parâmetro.
    @GetMapping("/listarPorIdCliente/{idCliente}")
    public ResponseEntity<List<PaymentSchedulerEntity>> listarAgendamentosPorIdCliente(@PathVariable Long idCliente) {

        // Busca todos os agendamentos de pagamento associados ao id do cliente informado na URL e retorna um OK junto com a lista
        List<PaymentSchedulerEntity> agendamentos = paymentSchedulerRepository.findAllByIdCliente(idCliente);
        return ResponseEntity.ok(agendamentos);
    }

    //Retorna uma lista de todos os agendamentos que possuem o status de pagamento fornecido como parâmetro. (Paid ou Pending)
    @GetMapping("/listarPorStatusPagamento/{statusPagamento}")
    public ResponseEntity<List<PaymentSchedulerEntity>> listarPorStatusPagamento(@PathVariable EnumPaymentStatus statusPagamento) {
        
        // Busca todos os agendamentos de pagamento com o status informado na URL e retorna um OK junto com a lista 
        List<PaymentSchedulerEntity> agendamentos = paymentSchedulerRepository.findAllByStatusPagamento(statusPagamento);
        return ResponseEntity.ok(agendamentos);
    }
    
    /**
     * Esta é uma função que recebe um objeto PaymentSchedulerEntity 
     * como entrada e agenda um novo pagamento.
     * @param id ID do agendamento a ser pago
     * @param request Map contendo a forma de pagamento (formaPagamento)
     * @return ResponseEntity<String> Uma mensagem indicando que o pagamento foi finalizado ou uma mensagem de erro
     */
    @PostMapping("/agendar")
    public ResponseEntity<String> agendarPagamento(@RequestBody PaymentSchedulerEntity agendamento) {
        try {
            agendamento.setDataAgendamentoDoPagamento(LocalDateTime.now());
            agendamento.setStatusPagamento(EnumPaymentStatus.Pending);
            agendamento.setFormaPagamento(null);

            // Salva o agendamento no banco de dados
            PaymentSchedulerEntity agendamentoSalvo = paymentSchedulerRepository.save(agendamento);
            return ResponseEntity.status(HttpStatus.CREATED).body("Seu ID de Agendamento é: " + agendamentoSalvo.getId());   
        } catch (Exception e) {

            // Retorna uma resposta de erro com a mensagem de erro correspondente
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar agendamento: " + e.getMessage());
        }
    }

    /**
     * Esta é uma função que realiza o pagamento de um agendamento existente com base no ID fornecido.
     * @param id ID do agendamento a ser pago
     * @param request Map contendo a forma de pagamento (formaPagamento)
     * @return ResponseEntity<String> Uma mensagem indicando que o pagamento foi finalizado ou uma mensagem de erro
     */
    @PutMapping("/pagar/{id}")
    public ResponseEntity<String> pagarAgendamento(@PathVariable Long id, @RequestBody Map<String, String> request) {
        
        try
        {
            // Busca um agendamento com o ID fornecido no banco de dados
            Optional<PaymentSchedulerEntity> agendamento = paymentSchedulerRepository.findById(id);
            
            // Verifica se o agendamento existe
            if (agendamento.isPresent()) {

                // Obtém o agendamento existente do Optional
                PaymentSchedulerEntity agendamentoAtualizado = agendamento.get();
                
                // Verifica se o agendamento já foi pago anteriormente
                if (agendamentoAtualizado.getStatusPagamento() == EnumPaymentStatus.Paid){

                    return ResponseEntity.badRequest().body(
                        "Pagamento já realizado!\nId de Agendamento: " + agendamentoAtualizado.getId() + 
                        "\nData do Pagamento: " + agendamentoAtualizado.getDataDoPagamento() +
                        "\nStatus: " + agendamentoAtualizado.getStatusPagamento() +
                        "\nValor: " + agendamentoAtualizado.getValor() + 
                        "\nForma de Pagamento: " + agendamentoAtualizado.getFormaPagamento());
                    
                }

                // Atualiza o agendamento com a forma de pagamento e data de pagamento fornecidas e salva o agendamento no Banco de Dados
                agendamentoAtualizado.setFormaPagamento(request.get("formaPagamento"));
                agendamentoAtualizado.setDataDoPagamento(LocalDateTime.now());
                agendamentoAtualizado.setStatusPagamento(EnumPaymentStatus.Paid);
                paymentSchedulerRepository.save(agendamentoAtualizado);
                return ResponseEntity.ok("Pagamento Finalizado");

            } else {
                // Retorna uma resposta de erro se o agendamento com o ID fornecido não existir no banco de dados
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // Retorna uma resposta de erro com a mensagem correspondente se ocorrer um erro ao processar a solicitação
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao processar a solicitação");
        }
    }
    /**
     * Esta função recebe o ID do agendamento e um objeto PaymentSchedulerEntity
     * E contendo a nova data e hora do agendamento como entrada.
     * @param id ID do agendamento a ser atualizado.
     * @param agendamentoAtualizado Objeto PaymentSchedulerEntity contendo a nova data e hora do agendamento.
     * @return ResponseEntity com mensagem de sucesso ou erro.
     */
    @PutMapping("/atualizarDataHoraAgendamento/{id}")
    public ResponseEntity<String> atualizarDataHoraAgendamento(@PathVariable Long id, @RequestBody PaymentSchedulerEntity agendamentoAtualizado) {
        
        try {
            
            // Encontra o agendamento correspondente no banco de dados
            Optional<PaymentSchedulerEntity> agendamento = paymentSchedulerRepository.findById(id);

            // Verifica se o agendamento existe
            if (agendamento.isPresent()) {

                PaymentSchedulerEntity agendamentoExistente = agendamento.get();

                // Verifica se o agendamento já foi pago, se sim, não permite a alteração da data
                if (agendamentoExistente.getStatusPagamento() == EnumPaymentStatus.Paid){

                    return ResponseEntity.badRequest().body(
                        "Pagamento já realizado!\nVocê não pode modificar a data do Pagamento" +
                        "\nId de Agendamento: " + agendamentoExistente.getId() + 
                        "\nData do Pagamento: " + agendamentoExistente.getDataDoPagamento() +
                        "\nStatus: " + agendamentoExistente.getStatusPagamento() +
                        "\nValor: " + agendamentoExistente.getValor() + 
                        "\nForma de Pagamento: " + agendamentoExistente.getFormaPagamento());
                }
                
                // Define a nova data e hora do agendamento e salva no banco de dados
                agendamentoExistente.setDataAgendamentoDoPagamento(LocalDateTime.now());
                agendamentoExistente.setDataDoPagamento(agendamentoAtualizado.getDataDoPagamento());
                paymentSchedulerRepository.save(agendamentoExistente);
                return ResponseEntity.ok("Data alterada para: " + agendamentoExistente.getDataDoPagamento());
            } else {
                // Retorna uma resposta de erro informando que o agendamento não foi encontrado
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // Retorna uma resposta de erro genérica caso ocorra algum erro ao processar a solicitação
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao processar a solicitação");
        }
    }

/**
 * Este método é responsável por excluir um agendamento de pagamento a partir do seu ID.
 *
 * @param id O ID do agendamento que se deseja excluir.
 * @return Uma ResponseEntity com uma mensagem informando se o agendamento foi excluído com sucesso ou se ocorreu algum erro.
 */

    @DeleteMapping("/Delete/{id}")
    public ResponseEntity<String> excluirAgendamento(@PathVariable("id") Long id) {
        // Busca o agendamento pelo id fornecido
        Optional<PaymentSchedulerEntity> agendamento = paymentSchedulerRepository.findById(id);
        if (agendamento.isPresent()) {

            PaymentSchedulerEntity agendamentoAtualizado = agendamento.get();

            // Verifica se o pagamento já foi realizado
            if (agendamentoAtualizado.getStatusPagamento() == EnumPaymentStatus.Paid){
                return ResponseEntity.badRequest().body("Não é possível excluir um agendamento com pagamento realizado!");
            }

            // Exclui o agendamento do banco de dados e salva no banco de dados
            paymentSchedulerRepository.delete(agendamento.get());
            return ResponseEntity.ok("Agendamento excluído com sucesso!");
        } else {
            // Retorna uma resposta de erro informando que o agendamento não foi encontrado
            return ResponseEntity.notFound().build();
        }
    }
}
