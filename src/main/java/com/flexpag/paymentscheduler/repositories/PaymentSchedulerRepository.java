package com.flexpag.paymentscheduler.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flexpag.paymentscheduler.entities.EnumPaymentStatus;
import com.flexpag.paymentscheduler.entities.PaymentSchedulerEntity;

@Repository
public interface PaymentSchedulerRepository extends JpaRepository<PaymentSchedulerEntity, Long> {
    // Busca todas as entidades PaymentSchedulerEntity associadas a um determinado cliente através do ID do cliente
    List<PaymentSchedulerEntity> findAllByIdCliente(Long idCliente);

    // Busca todas as entidades PaymentSchedulerEntity que possuem um determinado status de pagamento
    List<PaymentSchedulerEntity> findAllByStatusPagamento(EnumPaymentStatus statusPagamento);
}
