package com.sfl.flybet.domain.common.model;

import com.sfl.flybet.utilities.AccountConvertor;

import java.math.BigDecimal;

public class Amount implements Comparable<Amount> {

    private final BigDecimal value;
    private final Devise devise;

    public Amount(BigDecimal val, Devise devise){

        this.value = val;
        this.devise = devise;
    }


    @Override
    public int compareTo(Amount o) {
        return AccountConvertor.compare(this, o);
    }

    public Devise getDevise() {
        return this.devise;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Amount{" +
                "value=" + value +
                ", devise=" + devise +
                '}';
    }
}
