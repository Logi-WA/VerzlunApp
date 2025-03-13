package is.hi.hbv601g.verzlunapp.services.serviceimplementations;

import is.hi.hbv601g.verzlunapp.services.NetworkService;

public class NetworkServiceImpl implements NetworkService {
    @Override
    public String sendRequest(String endpoint, String jsonPayload) {
        // Perform the actual network request here
        return "Response from " + endpoint;
    }
}
