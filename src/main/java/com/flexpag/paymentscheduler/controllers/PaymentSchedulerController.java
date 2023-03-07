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

    @GetMapping("/listar")
    public ResponseEntity<List<PaymentSchedulerEntity>> listarAgendamentos() {
        List<PaymentSchedulerEntity> agendamentos = paymentSchedulerRepository.findAll();
        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/consultarPorIdAgendamento/{id}")
    public ResponseEntity<PaymentSchedulerEntity> listarAgendamentosPorId(@PathVariable Long id) {
        Optional<PaymentSchedulerEntity> agendamento = paymentSchedulerRepository.findById(id);
        return agendamento.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/listarPorIdCliente/{idCliente}")
    public ResponseEntity<List<PaymentSchedulerEntity>> listarAgendamentosPorIdCliente(@PathVariable Long idCliente) {
        List<PaymentSchedulerEntity> agendamentos = paymentSchedulerRepository.findAllByIdCliente(idCliente);
        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/listarPorStatusPagamento/{statusPagamento}")
    public ResponseEntity<List<PaymentSchedulerEntity>> listarPorStatusPagamento(@PathVariable EnumPaymentStatus statusPagamento) {
        List<PaymentSchedulerEntity> agendamentos = paymentSchedulerRepository.findAllByStatusPagamento(statusPagamento);
        return ResponseEntity.ok(agendamentos);
    }

    @PostMapping("/agendar")
    public ResponseEntity<String> agendarPagamento(@RequestBody PaymentSchedulerEntity agendamento) {
        try {
            agendamento.setDataAgendamentoDoPagamento(LocalDateTime.now());
            agendamento.setStatusPagamento(EnumPaymentStatus.Pending);
            agendamento.setFormaPagamento(null);
            PaymentSchedulerEntity agendamentoSalvo = paymentSchedulerRepository.save(agendamento);
            return ResponseEntity.status(HttpStatus.CREATED).body("Seu ID de Agendamento é: " + agendamentoSalvo.getId());   
        } catch (Exception e) {
            // TODO: handle exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar agendamento: " + e.getMessage());
        }
    }

    @PutMapping("/pagar/{id}")
    public ResponseEntity<String> pagarAgendamento(@PathVariable Long id, @RequestBody Map<String, String> request) {
        
        try
        {
            Optional<PaymentSchedulerEntity> agendamento = paymentSchedulerRepository.findById(id);
            
            if (agendamento.isPresent()) {

                PaymentSchedulerEntity agendamentoAtualizado = agendamento.get();

                if (agendamentoAtualizado.getStatusPagamento() == EnumPaymentStatus.Paid){

                    return ResponseEntity.badRequest().body(
                        "Pagamento já realizado!\nId de Agendamento: " + agendamentoAtualizado.getId() + 
                        "\nData do Pagamento: " + agendamentoAtualizado.getDataDoPagamento() +
                        "\nStatus: " + agendamentoAtualizado.getStatusPagamento() +
                        "\nValor: " + agendamentoAtualizado.getValor() + 
                        "\nForma de Pagamento: " + agendamentoAtualizado.getFormaPagamento());
                    
                }

                agendamentoAtualizado.setFormaPagamento(request.get("formaPagamento"));
                agendamentoAtualizado.setDataDoPagamento(LocalDateTime.now());
                agendamentoAtualizado.setStatusPagamento(EnumPaymentStatus.Paid);
                paymentSchedulerRepository.save(agendamentoAtualizado);
                return ResponseEntity.ok("Pagamento Finalizado");

            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao processar a solicitação");
        }
    }

    @PutMapping("/atualizarDataHoraAgendamento/{id}")
    public ResponseEntity<String> atualizarDataHoraAgendamento(@PathVariable Long id, @RequestBody PaymentSchedulerEntity agendamentoAtualizado) {
        
        try {
        
            Optional<PaymentSchedulerEntity> agendamento = paymentSchedulerRepository.findById(id);
            if (agendamento.isPresent()) {

                PaymentSchedulerEntity agendamentoExistente = agendamento.get();

                if (agendamentoExistente.getStatusPagamento() == EnumPaymentStatus.Paid){

                    return ResponseEntity.badRequest().body(
                        "Pagamento já realizado!\nVocê não pode modificar a data do Pagamento" +
                        "\nId de Agendamento: " + agendamentoExistente.getId() + 
                        "\nData do Pagamento: " + agendamentoExistente.getDataDoPagamento() +
                        "\nStatus: " + agendamentoExistente.getStatusPagamento() +
                        "\nValor: " + agendamentoExistente.getValor() + 
                        "\nForma de Pagamento: " + agendamentoExistente.getFormaPagamento());
                }
                
                agendamentoExistente.setDataAgendamentoDoPagamento(LocalDateTime.now());
                agendamentoExistente.setDataDoPagamento(agendamentoAtualizado.getDataDoPagamento());
                paymentSchedulerRepository.save(agendamentoExistente);
                return ResponseEntity.ok("Data alterada para: " + agendamentoExistente.getDataDoPagamento());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao processar a solicitação");
        }
    }

    @DeleteMapping("/Delete/{id}")
    public ResponseEntity<String> excluirAgendamento(@PathVariable("id") Long id) {
        Optional<PaymentSchedulerEntity> agendamento = paymentSchedulerRepository.findById(id);
        if (agendamento.isPresent()) {

            PaymentSchedulerEntity agendamentoAtualizado = agendamento.get();
            if (agendamentoAtualizado.getStatusPagamento() == EnumPaymentStatus.Paid){
                return ResponseEntity.badRequest().body("Não é possível excluir um agendamento com pagamento realizado!");
            }

            paymentSchedulerRepository.delete(agendamento.get());
            return ResponseEntity.ok("Agendamento excluído com sucesso!");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
