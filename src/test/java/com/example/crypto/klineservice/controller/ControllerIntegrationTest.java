package com.example.crypto.klineservice.controller;

import com.example.crypto.klineservice.model.Interval;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @SneakyThrows
    @Test
    public void testLoadAndGetKline() {
        // test 1
        mvc.perform(MockMvcRequestBuilders
                        .post("/kline/range")
                        .param("symbol", "BTCUSDT")
                        .param("startTime", "1741143600000")
                        .param("endTime", "1741144499999")
                ).andDo(print())
                .andExpect(status().isCreated());

        // should include 3 kline obj since we enter 15 min range
        // with freq = 5m , 15 / 5 = 3
        mvc.perform(MockMvcRequestBuilders.get("/kline")
                        .param("startTime", "1741143600000")
                        .param("endTime", "1741144499999")
                        .param("symbol", "BTCUSDT")
                        .param("freq", Interval.fromCode("5m").getCode())
                ).andDo(print())
                .andExpect(status().isOk());

        // test 2
        mvc.perform(MockMvcRequestBuilders
                        .post("/kline/range")
                        .param("symbol", "BTCUSDT")
                        .param("startTime", "1741145580000")
                        .param("endTime", "1741145640000")
                ).andDo(print())
                .andExpect(status().isCreated());

        // should return 1 kline obj only
        mvc.perform(MockMvcRequestBuilders.get("/kline")
                        .param("startTime", "1741145580000")
                        .param("endTime", "1741145640000")
                        .param("symbol", "BTCUSDT")
                        .param("freq", Interval.fromCode("1m").getCode())
                ).andDo(print())
                .andExpect(status().isOk());


        // test 3
        mvc.perform(MockMvcRequestBuilders
                        .post("/kline/range")
                        .param("symbol", "BTCUSDT")
                        .param("startTime", "1741146540000")
                        .param("endTime", "1741148340000")
                ).andDo(print())
                .andExpect(status().isCreated());

        // should return 2 kline obj
        mvc.perform(MockMvcRequestBuilders.get("/kline")
                        .param("startTime", "1741146540000")
                        .param("endTime", "1741148340000")
                        .param("symbol", "BTCUSDT")
                        .param("freq", Interval.fromCode("15m").getCode())
                ).andDo(print())
                .andExpect(status().isOk());
    }


}
