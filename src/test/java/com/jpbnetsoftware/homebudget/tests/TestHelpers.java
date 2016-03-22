package com.jpbnetsoftware.homebudget.tests;

import java.time.LocalDate;

/**
 * Created by pburzynski on 22/03/2016.
 */
public class TestHelpers {
    public static LocalDate getDate(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }
}
