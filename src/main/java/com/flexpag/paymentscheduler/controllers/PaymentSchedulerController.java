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

    @GetMapping("/listarPorId/{id}")
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
    public ResponseEntity<PaymentSchedulerEntity> agendarPagamento(@RequestBody PaymentSchedulerEntity agendamento) {
        agendamento.setDataAgendamentoDoPagamento(LocalDateTime.now());
        agendamento.setStatusPagamento(EnumPaymentStatus.Pending);
        PaymentSchedulerEntity agendamentoSalvo = paymentSchedulerRepository.save(agendamento);
        return ResponseEntity.status(HttpStatus.CREATED).body(agendamentoSalvo);
    }

    @PutMapping("/pagar/{id}")
    public ResponseEntity<PaymentSchedulerEntity> pagarAgendamento(@PathVariable Long id) {
        Optional<PaymentSchedulerEntity> agendamento = paymentSchedulerRepository.findById(id);
        if (agendamento.isPresent()) {
            PaymentSchedulerEntity agendamentoAtualizado = agendamento.get();
            agendamentoAtualizado.setDataAgendamentoDoPagamento(LocalDateTime.now());
            agendamentoAtualizado.setStatusPagamento(EnumPaymentStatus.Paid);
            PaymentSchedulerEntity agendamentoSalvo = paymentSchedulerRepository.save(agendamentoAtualizado);
            return ResponseEntity.ok(agendamentoSalvo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/atualizarDataHoraAgendamento/{id}")
    public ResponseEntity<PaymentSchedulerEntity> atualizarDataHoraAgendamento(@PathVariable Long id, @RequestBody PaymentSchedulerEntity agendamentoAtualizado) {
        Optional<PaymentSchedulerEntity> agendamento = paymentSchedulerRepository.findById(id);
        if (agendamento.isPresent()) {
            PaymentSchedulerEntity agendamentoExistente = agendamento.get();
            agendamentoExistente.setDataDoPagamento(agendamentoAtualizado.getDataDoPagamento());
            PaymentSchedulerEntity agendamentoSalvo = paymentSchedulerRepository.save(agendamentoExistente);
            return ResponseEntity.ok(agendamentoSalvo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluirAgendamento(@PathVariable("id") Long id) {
        Optional<PaymentSchedulerEntity> agendamento = paymentSchedulerRepository.findById(id);
        if (agendamento.isPresent()) {
            paymentSchedulerRepository.delete(agendamento.get());
            return ResponseEntity.ok("Agendamento excluído com sucesso!");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
