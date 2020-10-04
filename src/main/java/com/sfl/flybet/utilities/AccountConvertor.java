package com.sfl.flybet.utilities;

import com.sfl.flybet.casestudy.domain.Amount;
import com.sfl.flybet.casestudy.domain.Devise;

import java.math.BigDecimal;

public class AccountConvertor {


    public static int compare(Amount a, Amount b) {
        return a.getValue().compareTo(b.getValue());
    }

}
