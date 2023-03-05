package com.flexpag.paymentscheduler.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PaymentScheduler")
public class PaymentSchedulerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(name = "id_cconta")
    private Long idConta;

    @Column(name = "valor")
    private BigDecimal valor;

    @Column(name = "data_hora_pagamento")
    private LocalDateTime DataAgendamentoDoPagamento;

    @Column(name = "data_hora_agendamento")
    private LocalDateTime DataDoPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pagamento")
    private EnumPaymentStatus statusPagamento;

    @Column(name = "forma_pagamento")
    private String formaPagamento;

    @Column(name = "descricao")
    private String descricao;

    public Long getIdCliente() {
        return idCliente;
    }
    
    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }
    
    public Long getIdConta() {
        return idConta;
    }
    
    public void setIdConta(Long idConta) {
        this.idConta = idConta;
    }
    
    public LocalDateTime getDataAgendamentoDoPagamento() {
        return DataAgendamentoDoPagamento;
    }
    
    public void setDataAgendamentoDoPagamento(LocalDateTime dataAgendamentoDoPagamento) {
        this.DataAgendamentoDoPagamento = dataAgendamentoDoPagamento;
    }
    
    public EnumPaymentStatus getStatusPagamento() {
        return statusPagamento;
    }
    
    public void setStatusPagamento(EnumPaymentStatus statusPagamento) {
        this.statusPagamento = statusPagamento;
    }
    
    public String getFormaPagamento() {
        return formaPagamento;
    }
    
    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}