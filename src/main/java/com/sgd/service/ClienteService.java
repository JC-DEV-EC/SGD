package com.sgd.service;

import com.sgd.domain.Cliente;
import com.sgd.domain.ClienteRepository;
import com.sgd.web.ClientChargeRequest;
import com.sgd.web.ClientCreateRequest;
import com.sgd.web.ClientPaymentRequest;
import com.sgd.web.ClientUpdateRequest;
import com.sgd.web.ClienteDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private static final Logger log = LoggerFactory.getLogger(ClienteService.class);

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public List<ClienteDto> findAll() {
        return repository.findAll()
                .stream()
                .map(ClienteDto::fromEntity)
                .toList();
    }

    public Optional<ClienteDto> findById(Long id) {
        return repository.findById(id)
                .map(ClienteDto::fromEntity);
    }

    @Transactional
    public ClienteDto create(ClientCreateRequest request) {
        Cliente entity = new Cliente();
        entity.setFirstName(request.firstName());
        entity.setLastName(request.lastName());
        entity.setCity(request.city());
        entity.setRegistrationDate(request.registrationDate());

        BigDecimal initialDebt = request.initialDebt() != null ? request.initialDebt() : BigDecimal.ZERO;
        entity.setDebt(initialDebt);
        entity.setPayment(BigDecimal.ZERO);
        entity.setDiscount(Boolean.TRUE.equals(request.discount()));

        recalculateFinancials(entity);

        Cliente saved = repository.save(entity);
        log.info("Created client id={} name={} {} city={} initialDebt={} discount={}",
                saved.getId(), saved.getFirstName(), saved.getLastName(), saved.getCity(),
                saved.getDebt(), saved.getDiscount());

        return ClienteDto.fromEntity(saved);
    }

    @Transactional
    public Optional<ClienteDto> update(Long id, ClientUpdateRequest request) {
        return repository.findById(id)
                .map(entity -> {
                    entity.setFirstName(request.firstName());
                    entity.setLastName(request.lastName());
                    entity.setCity(request.city());
                    entity.setRegistrationDate(request.registrationDate());
                    if (request.discount() != null) {
                        entity.setDiscount(request.discount());
                    }
                    recalculateFinancials(entity);
                    Cliente saved = repository.save(entity);
                    log.info("Updated client id={} name={} {} city={} discount={} status={} totalAmount={}",
                            saved.getId(), saved.getFirstName(), saved.getLastName(), saved.getCity(),
                            saved.getDiscount(), saved.getStatus(), saved.getTotalAmount());
                    return ClienteDto.fromEntity(saved);
                });
    }

    @Transactional
    public Optional<ClienteDto> addCharge(Long id, ClientChargeRequest request) {
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Charge amount must be greater than zero");
        }
        return repository.findById(id)
                .map(entity -> {
                    BigDecimal currentDebt = nullSafe(entity.getDebt());
                    BigDecimal newDebt = currentDebt.add(request.amount());
                    entity.setDebt(newDebt);
                    recalculateFinancials(entity);
                    Cliente saved = repository.save(entity);
                    log.info("Added charge clientId={} amount={} newDebt={} totalAmount={} status={}",
                            saved.getId(), request.amount(), saved.getDebt(), saved.getTotalAmount(), saved.getStatus());
                    return ClienteDto.fromEntity(saved);
                });
    }

    @Transactional
    public Optional<ClienteDto> addPayment(Long id, ClientPaymentRequest request) {
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }
        return repository.findById(id)
                .map(entity -> {
                    BigDecimal currentPayment = nullSafe(entity.getPayment());
                    BigDecimal newPayment = currentPayment.add(request.amount());

                    BigDecimal grossDebt = nullSafe(entity.getDebt());
                    if (newPayment.compareTo(grossDebt) > 0) {
                        newPayment = grossDebt; // no permitir pagar más de la deuda bruta
                    }

                    entity.setPayment(newPayment);
                    recalculateFinancials(entity);
                    Cliente saved = repository.save(entity);
                    log.info("Added payment clientId={} amount={} paymentTotal={} remaining={} status={}",
                            saved.getId(), request.amount(), saved.getPayment(),
                            saved.getTotalAmount(), saved.getStatus());
                    return ClienteDto.fromEntity(saved);
                });
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
        log.info("Deleted client id={}", id);
    }

    private void recalculateFinancials(Cliente entity) {
        BigDecimal debt = nullSafe(entity.getDebt());
        BigDecimal payment = nullSafe(entity.getPayment());
        boolean hasDiscount = Boolean.TRUE.equals(entity.getDiscount());

        BigDecimal base = debt.subtract(payment);
        if (base.compareTo(BigDecimal.ZERO) < 0) {
            base = BigDecimal.ZERO;
        }

        BigDecimal net = hasDiscount ? base.multiply(new BigDecimal("0.9")) : base;
        entity.setTotalAmount(net);

        if (net.compareTo(BigDecimal.ZERO) <= 0) {
            entity.setStatus("CANCELLED");
        } else {
            entity.setStatus("ACTIVE");
        }
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
