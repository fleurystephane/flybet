package com.sfl.flybet.utilities;


import com.sfl.flybet.domain.common.model.Amount;

public class AccountConvertor {


    public static int compare(Amount a, Amount b) {
        return a.getValue().compareTo(b.getValue());
    }

}
