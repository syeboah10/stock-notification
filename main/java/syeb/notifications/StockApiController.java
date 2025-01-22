package syeb.notifications;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/api/stock")
public class StockApiController {
    private static final String BASE_URL = "https://finnhub.io/api/v1/quote?symbol={symbol}&token={token}";
    private static final String API_TOKEN = null;

    private static FirebaseService firebaseService;
    private static NotificationService notificationService;
    private GmailApiEmailSender emailSender = new GmailApiEmailSender();

    @Autowired
    public StockApiController(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
        this.notificationService = notificationService;
    }
    //@GetMapping("/{symbol}")
    public ResponseEntity<StockResponsePOJO> getStockData( String symbol) {
        System.out.println("inside");
        RestTemplate restTemplate = new RestTemplate();
        try {
            // Making the API call
            StockResponsePOJO response = restTemplate.getForObject(BASE_URL, StockResponsePOJO.class, symbol, API_TOKEN);
            System.out.println(response.getC());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null); // Return error status
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> addStock(@RequestBody WatchStockRequest reqs) {
        try {
            firebaseService.addToWatchlist(reqs);
            return new ResponseEntity<>("Stock added to watchlist successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to add stock to watchlist.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Data // Lombok annotation to generate getters, setters, toString, etc.
    static class StockResponsePOJO {
        private double c; // Current price
        private double h;
        private double l;
        private double o;
        private double pc;

        public String toString(String symbol){
            return "Stock: " + symbol + "\n"
                    + "Current Price: " + Double.toString(c) + "\n";

        }
    }
//
//    @Data
//    private static class StockSearchPOJO {
//
//        private int count;
//        private List<ResultPOJO> results;
//
//    }
//    @Data
//    private static class ResultPOJO {
//        private String description;
//        private String displaySymbol;
//
//        private String symbol;
//
//    }
}


