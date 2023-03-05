package com.flexpag.paymentscheduler.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flexpag.paymentscheduler.entities.EnumPaymentStatus;
import com.flexpag.paymentscheduler.entities.PaymentSchedulerEntity;

@Repository
public interface PaymentSchedulerRepository extends JpaRepository<PaymentSchedulerEntity, Long> {
    List<PaymentSchedulerEntity> findAllByIdCliente(Long idCliente);
    List<PaymentSchedulerEntity> findAllByStatusPagamento(EnumPaymentStatus statusPagamento);
}
