package syeb.notifications;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class NotificationService {
    private FirebaseService firebaseService;
    private StockApiController controller;
    private GmailApiEmailSender emailSender;
    public NotificationService(FirebaseService firebaseService, StockApiController controller ){
        this.firebaseService = firebaseService;
        this.controller = controller;
        this.emailSender = new GmailApiEmailSender();
        startScheduler();
    }

    public void startScheduler(){
        Runnable schedulerTask = () -> {
            List<QueryDocumentSnapshot> data = null;
            try {
                data = firebaseService.getWatchlistData();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(data.size());

            for (QueryDocumentSnapshot record : data ){
                System.out.println("hello");
                System.out.println(pollStock(record));

            }
        };
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(schedulerTask, 0 , 10 , TimeUnit.SECONDS);
    }

    public String pollStock(QueryDocumentSnapshot snapshot){
        System.out.println(snapshot);
        String stock = snapshot.getString("stockSymbol");
        Double upperBound = snapshot.getDouble("highThreshold");
        Double lowerBound = snapshot.getDouble("lowThreshold");

        ResponseEntity<StockApiController.StockResponsePOJO> req = controller.getStockData(stock);
        StockApiController.StockResponsePOJO stockResponse = req.getBody();

        if (stockResponse.getC() > upperBound){
            sendMail("UpperBound Passed Notification", stockResponse, stock);
        }
        else if (stockResponse.getC() < lowerBound) {
            sendMail("LowerBound Passed Notification", stockResponse, stock);
        }
        System.out.println(req);
        return req.toString();
    }

    public void sendMail(String messageType,  StockApiController.StockResponsePOJO response, String stock ){
        emailSender.sendEmail("sy433@cornell.edu", messageType, response.toString(stock));
    }

    //create another scheduler, that hust sendyou an update of your portfolio ever 6 hours without having to check

}
