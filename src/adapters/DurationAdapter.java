package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;


public class DurationAdapter extends TypeAdapter<Duration> {


    public Duration read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        String durationS = reader.nextString();
        long durationL = Long.parseLong(durationS);
        return Duration.ofMinutes(durationL);
    }

    public void write(JsonWriter writer, Duration value) throws IOException {
        if (value == null) {
            writer.nullValue();
            return;
        }
        String duration = value.toString();
        writer.value(duration);
    }
}


