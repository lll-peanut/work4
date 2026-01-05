package com.peanut;

import com.peanut.controller.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = Work4Application.class)
@ActiveProfiles("test")
public class Test01 {
//
//    @Autowired
//    private VideoDao videoDao;

    @Autowired
    private UserController userDao;

    @Test
    void test1() {
    }

}
