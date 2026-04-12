package com.peanut;

import com.peanut.controller.UserController;
import com.peanut.utils.DateTimeFormatsUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.TimeZone;


public class Test01 {
//
//    @Autowired
//    private VideoDao videoDao;


    @Test
    void test1() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        Date d1 = sdf.parse("2026-03-30 10:01:44");
        System.out.println("str -> date  : " + sdf.format(d1));
        System.out.println("str -> millis: " + d1.getTime());
        System.out.println("millis->str  : " + sdf.format(new Date(d1.getTime())));
    }

}
