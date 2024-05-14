package bobr.mainMicroservice.camunda;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CamundaMapper {

    public Double readDouble(Map<String, Object> map, String var) {
        return Double.valueOf(readString(map, var));
    }

    public String readString(Map<String, Object> map, String var) {
        return map.get(var).toString();
    }

    public Boolean readBool(Map<String, Object> map, String var) {
        return Boolean.valueOf(readString(map, var));
    }

    public Integer readLongAsInt(Map<String, Object> map, String var) {
        return Math.toIntExact((Long) map.get(var));
    }

    public Integer readInt(Map<String, Object> map, String var) {
        return (Integer) map.get(var);
    }

}
