package edu.hawaii.its.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.hawaii.its.api.service.HttpRequestService;
import edu.hawaii.its.api.util.MutableClock;
import edu.hawaii.its.groupings.configuration.SpringBootWebApplication;
import edu.hawaii.its.groupings.controller.WithMockUhUser;

@ActiveProfiles("localTest") @SpringBootTest(classes = { SpringBootWebApplication.class })
public class GeneralRestControllerTest {

    @Autowired private WebApplicationContext context;

    @MockBean private HttpRequestService httpRequestService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private DateTimeFormatter formatter;
    private static final ZoneId ZONE_ID = ZoneId.of("Pacific/Honolulu");

    @BeforeEach public void setUp() {
        mockMvc = webAppContextSetup(context).apply(springSecurity()).build();
        objectMapper = new ObjectMapper();
        formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
    }

    @Test @WithMockUhUser public void testAnnouncementTimingTransitions() throws Exception {
        // Fix t0 using mutable clock (Jan 1st, 2030 @ 00:00)
        Instant t0 = Instant.parse("2030-01-01T00:00:00Z");
        MutableClock mutableClock = new MutableClock(t0, ZONE_ID);
        LocalDateTime t0Local = LocalDateTime.ofInstant(t0, ZONE_ID);

        // Message 1 Start: Jan 2nd, 2030 @ 12:00
        // Message 1 End:   Jan 8th, 2030 @ 12:30
        LocalDateTime message1Start = t0Local.plusDays(1).plusHours(12);
        LocalDateTime message1End = message1Start.plusDays(6).plusMinutes(30);

        // Message 2 Start: Jan 8th, 2030 @ 12:00
        // Message 2 End:   Jan 13th, 2030 @ 18:30
        LocalDateTime message2Start = message1End.minusMinutes(30);
        LocalDateTime message2End = message2Start.plusDays(5).plusHours(6).plusMinutes(30);

        // Mock the HTTP request service to return responses based on clock
        when(httpRequestService.makeApiRequest(anyString(), eq(HttpMethod.GET))).thenAnswer(invocation -> {
            LocalDateTime now = LocalDateTime.ofInstant(mutableClock.instant(), ZONE_ID);
            String testJson =
                    createTestAnnouncementJsonWithTime(message1Start, message1End, message2Start, message2End, now);
            return new ResponseEntity<>(testJson, HttpStatus.OK);
        });

        // TEST 1: Before any messages show (both Future)
        mutableClock.setInstant(message1Start.minusDays(1).atZone(ZONE_ID).toInstant());
        MvcResult result1 = mockMvc.perform(get("/announcements").with(csrf())).andExpect(status().isOk()).andReturn();

        String response1 = result1.getResponse().getContentAsString();
        JsonNode jsonResponse1 = objectMapper.readTree(response1);
        JsonNode announcements1 = jsonResponse1.get("announcements");

        assertNotNull(announcements1);
        assertEquals(2, announcements1.size());

        JsonNode message1_1 = findAnnouncementByMessage(announcements1, "UH Groupings will be updated BEFORE.");
        assertNotNull(message1_1);
        assertEquals("Future", message1_1.get("state").asText());

        JsonNode message2_1 = findAnnouncementByMessage(announcements1, "UH Groupings has been updated as of AFTER.");
        assertNotNull(message2_1);
        assertEquals("Future", message2_1.get("state").asText());

        // TEST 2: When one message is up (message1 Active, message2 Future)
        mutableClock.setInstant(message1Start.plusHours(1).atZone(ZONE_ID).toInstant());
        MvcResult result2 = mockMvc.perform(get("/announcements").with(csrf())).andExpect(status().isOk()).andReturn();

        String response2 = result2.getResponse().getContentAsString();
        JsonNode jsonResponse2 = objectMapper.readTree(response2);
        JsonNode announcements2 = jsonResponse2.get("announcements");

        assertNotNull(announcements2);
        assertEquals(2, announcements2.size());

        JsonNode message1_2 = findAnnouncementByMessage(announcements2, "UH Groupings will be updated BEFORE.");
        assertNotNull(message1_2);
        assertEquals("Active", message1_2.get("state").asText());

        JsonNode message2_2 = findAnnouncementByMessage(announcements2, "UH Groupings has been updated as of AFTER.");
        assertNotNull(message2_2);
        assertEquals("Future", message2_2.get("state").asText());

        // TEST 3: When both messages are up (both Active)
        mutableClock.setInstant(message1End.minusMinutes(15).atZone(ZONE_ID).toInstant());
        MvcResult result3 = mockMvc.perform(get("/announcements").with(csrf())).andExpect(status().isOk()).andReturn();

        String response3 = result3.getResponse().getContentAsString();
        JsonNode jsonResponse3 = objectMapper.readTree(response3);
        JsonNode announcements3 = jsonResponse3.get("announcements");

        assertNotNull(announcements3);
        assertEquals(2, announcements3.size());

        JsonNode message1_3 = findAnnouncementByMessage(announcements3, "UH Groupings will be updated BEFORE.");
        assertNotNull(message1_3);
        assertEquals("Active", message1_3.get("state").asText());

        JsonNode message2_3 = findAnnouncementByMessage(announcements3, "UH Groupings has been updated as of AFTER.");
        assertNotNull(message2_3);
        assertEquals("Active", message2_3.get("state").asText());

        // Test 4: When one message goes down (message1 Expired, message2 Active)
        mutableClock.setInstant(message1End.plusMinutes(1).atZone(ZONE_ID).toInstant());
        MvcResult result4 = mockMvc.perform(get("/announcements").with(csrf())).andExpect(status().isOk()).andReturn();

        String response4 = result4.getResponse().getContentAsString();
        JsonNode jsonResponse4 = objectMapper.readTree(response4);
        JsonNode announcements4 = jsonResponse4.get("announcements");

        assertNotNull(announcements4);
        assertEquals(2, announcements4.size());

        JsonNode message1_4 = findAnnouncementByMessage(announcements4, "UH Groupings will be updated BEFORE.");
        assertNotNull(message1_4);
        assertEquals("Expired", message1_4.get("state").asText());

        JsonNode message2_4 = findAnnouncementByMessage(announcements4, "UH Groupings has been updated as of AFTER.");
        assertNotNull(message2_4);
        assertEquals("Active", message2_4.get("state").asText());

        // Test 5: When all messages are down (both Expired)
        mutableClock.setInstant(message2End.plusDays(1).atZone(ZONE_ID).toInstant());
        MvcResult result5 = mockMvc.perform(get("/announcements").with(csrf())).andExpect(status().isOk()).andReturn();

        String response5 = result5.getResponse().getContentAsString();
        JsonNode jsonResponse5 = objectMapper.readTree(response5);
        JsonNode announcements5 = jsonResponse5.get("announcements");

        assertNotNull(announcements5);
        assertEquals(2, announcements5.size());

        JsonNode message1_5 = findAnnouncementByMessage(announcements5, "UH Groupings will be updated BEFORE.");
        assertNotNull(message1_5);
        assertEquals("Expired", message1_5.get("state").asText());

        JsonNode message2_5 = findAnnouncementByMessage(announcements5, "UH Groupings has been updated as of AFTER.");
        assertNotNull(message2_5);
        assertEquals("Expired", message2_5.get("state").asText());
    }

    /**
     * Creates a test announcement JSON string with two announcements and their states based on current time.
     *
     * @param beforeStart the start time for the first announcement (BEFORE message)
     * @param beforeEnd   the end time for the first announcement (BEFORE message)
     * @param afterStart  the start time for the second announcement (AFTER message)
     * @param afterEnd    the end time for the second announcement (AFTER message)
     * @param currentTime the current time used to determine announcement states
     * @return a JSON string containing both announcements with their calculated states
     */
    private String createTestAnnouncementJsonWithTime(LocalDateTime beforeStart, LocalDateTime beforeEnd,
            LocalDateTime afterStart, LocalDateTime afterEnd, LocalDateTime currentTime) {
        return String.format("""
                        {
                          "resultCode": "SUCCESS",
                          "announcements": [
                            {
                              "message": "UH Groupings will be updated BEFORE.",
                              "start": "%s",
                              "end": "%s",
                              "state": "%s"
                            },
                            {
                              "message": "UH Groupings has been updated as of AFTER.",
                              "start": "%s",
                              "end": "%s",
                              "state": "%s"
                            }
                          ]
                        }""", beforeStart.format(formatter), beforeEnd.format(formatter),
                getStateForTime(beforeStart, beforeEnd, currentTime), afterStart.format(formatter),
                afterEnd.format(formatter), getStateForTime(afterStart, afterEnd, currentTime));
    }

    /**
     * Computes the state of an announcement based on the current time relative to its start and end times.
     *
     * @param start       the start time of the announcement
     * @param end         the end time of the announcement
     * @param currentTime the current time to compare against
     * @return "Future" if currentTime is before start, "Expired" if currentTime is after end, "Active" otherwise
     */
    private String getStateForTime(LocalDateTime start, LocalDateTime end, LocalDateTime currentTime) {
        if (currentTime.isBefore(start)) {
            return "Future";
        } else if (currentTime.isAfter(end)) {
            return "Expired";
        } else {
            return "Active";
        }
    }

    /**
     * Finds an announcement by matching its message text.
     *
     * @param announcements the JSON node containing an array of announcements
     * @param message       the message text to search for
     * @return the JsonNode matching the message, or null if not found
     */
    private JsonNode findAnnouncementByMessage(JsonNode announcements, String message) {
        for (JsonNode announcement : announcements) {
            if (message.equals(announcement.get("message").asText())) {
                return announcement;
            }
        }
        return null;
    }
}

