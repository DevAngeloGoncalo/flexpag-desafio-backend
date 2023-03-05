package com.flexpag.paymentscheduler.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flexpag.paymentscheduler.entities.PaymentSchedulerEntity;

@Repository
public interface PaymentSchedulerRepository extends JpaRepository<PaymentSchedulerEntity, Long> {

}
