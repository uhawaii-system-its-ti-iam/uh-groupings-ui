package edu.hawaii.its.holiday.type;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import edu.hawaii.its.holiday.util.Dates;

public class HolidayDateSerializer extends StdSerializer<Date> {
    private static final long serialVersionUID = 59L;
    private DateTimeFormatter format = DateTimeFormatter.ofPattern(Dates.DATE_FORMAT);

    public HolidayDateSerializer() {
        this(null);
    }

    public HolidayDateSerializer(Class<Date> t) {
        super(t);
    }

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider arg2)
            throws IOException, JsonProcessingException {
        LocalDate date = Dates.toLocalDate(value);
        gen.writeString(date.format(format));
    }
}