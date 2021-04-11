package com.tecacet.springbatch.jobs;

import com.tecacet.springbatch.dto.BankTransaction;
import com.tecacet.springbatch.dto.MonthlyCashFlow;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@StepScope
public class CashFlowProcessor implements ItemProcessor<BankTransaction, MonthlyCashFlow> {

    private LocalDate lastDate = null;
    private List<BankTransaction> monthlyGroup = new ArrayList<>();

    @Override
    public MonthlyCashFlow process(BankTransaction transaction) {
        if (lastDate == null || sameMonth(transaction.getDate())) {
            monthlyGroup.add(transaction);
            lastDate = transaction.getDate();
            return null;
        }
        //this means that a new month has started
        MonthlyCashFlow cashFlow = aggregate(); // aggregate the collected flows
        lastDate = transaction.getDate(); //set the new month
        monthlyGroup = new ArrayList<>(); // create a new list
        monthlyGroup.add(transaction);
        return cashFlow;
    }

    private MonthlyCashFlow aggregate() {
        BigDecimal cashFlow = monthlyGroup.stream().map(this::getSignedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new MonthlyCashFlow(lastDate.getYear(), lastDate.getMonthValue(), cashFlow);
    }

    private BigDecimal getSignedAmount(BankTransaction transaction) {
        return transaction.getType() == BankTransaction.Type.CREDIT? transaction.getAmount() :
                transaction.getAmount().negate();
    }

    private boolean sameMonth(LocalDate date) {
        return lastDate.getMonthValue() == date.getMonthValue() && lastDate.getYear() == date.getYear();
    }
}
