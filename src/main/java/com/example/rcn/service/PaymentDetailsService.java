package com.example.rcn.service;

import com.example.rcn.model.PaymentDetails;
import com.example.rcn.repository.PaymentDetailsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentDetailsService {

    private static final Long SINGLETON_ID = PaymentDetails.SINGLETON_ID;

    private final PaymentDetailsRepository repository;

    public PaymentDetailsService(PaymentDetailsRepository repository) {
        this.repository = repository;
    }

    public PaymentDetails getSingleton() {
        return repository.findById(SINGLETON_ID)
                .orElseGet(() -> repository.save(PaymentDetails.defaultsTransient()));
    }

    public PaymentDetails save(PaymentDetails details) {
        return repository.save(details);
    }
}
