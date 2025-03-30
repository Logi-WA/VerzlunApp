package is.hi.hbv601g.verzlunapp.services;

public interface NetworkService {
    String sendRequest(String endpoint, String jsonPayload);

    String post(String endpoint, String jsonPayload);

    String get(String endpoint);

    String patch(String endpoint, String jsonPayload);

}
