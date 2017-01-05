package edu.hawaii.its.holiday.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class HolidayRestControllerSystemTest {

    final MediaType APPLICATION_JSON_UTF8 =
            new MediaType(MediaType.APPLICATION_JSON.getType(),
                    MediaType.APPLICATION_JSON.getSubtype(),
                    Charset.forName("utf8"));

    @Autowired
    private HolidayRestController restController;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(context).build();
    }

    @Test
    public void testConstruction() {
        assertNotNull(restController);
    }

    @Test
    public void httpGetHolidays() throws Exception {
        mockMvc.perform(get("/api/holidays"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data", hasSize(140)));
    }

    @Test
    public void httpGetHolidaysById() throws Exception {
        mockMvc.perform(get("/api/holidays/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("data.description").value("New Year's Day"))
                .andExpect(jsonPath("data.observedDate").value("January 01, 2013"))
                .andExpect(jsonPath("data.officialDate").value("January 01, 2013"))
                .andExpect(jsonPath("data.year").value("2013"))
                .andExpect(jsonPath("data.holidayTypes", hasSize(3)))
                .andExpect(jsonPath("data.holidayTypes[0].description").value("Bank"))
                .andExpect(jsonPath("data.holidayTypes[1].description").value("Federal"))
                .andExpect(jsonPath("data.holidayTypes[2].description").value("State"));
    }

    @Test
    public void httpGetTypes() throws Exception {
        mockMvc.perform(get("/api/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data", hasSize(4)))
                .andExpect(jsonPath("data[0].description").value("Bank"))
                .andExpect(jsonPath("data[1].description").value("Federal"))
                .andExpect(jsonPath("data[2].description").value("State"))
                .andExpect(jsonPath("data[3].description").value("UH"));
    }
}
