package syeb.notifications;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseService {

    private final Firestore firestore;

    public FirebaseService(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * Add a stock to the user's watchlist in Firestore using WatchStockRequest.
     *
     * @param request The WatchStockRequest object containing the user ID, stock symbol, and price.
     */
    public void addToWatchlist(WatchStockRequest request) {
        try {
            String documentPath = "users/" + request.getUserEmail();

            DocumentReference userDocRef = firestore.document(documentPath);

            // Check if the user document exists
            ApiFuture<DocumentSnapshot> future = userDocRef.get();
            DocumentSnapshot document = future.get(); // This is blocking; consider alternatives for async

            if (!document.exists()) {
                // Initialize the user document if it doesn't exist
                Map<String, Object> userData = new HashMap<>();
                userDocRef.set(userData); // Creates an empty document for the user
                System.out.println("User document created: " + request.getUserEmail());
            }

            // Create data map for the stock
            Map<String, Object> stockData = new HashMap<>();
            stockData.put("stockSymbol", request.getStockSymbol());
            stockData.put("averageStockPrice", request.getAverageStockPrice());
            stockData.put("lowThreshold", request.getPriceRanges().getLow());
            stockData.put("highThreshold", request.getPriceRanges().getHigh());

            // Add the stock data to the user's watchlist
            userDocRef
                    .collection("stocks")
                    .document(request.getStockSymbol())
                    .set(stockData);

            System.out.println("Successfully added stock to watchlist for user: " + request.getUserEmail());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error adding to watchlist: " + e.getMessage());
        }
    }
    public  List<QueryDocumentSnapshot> getWatchlistData() throws ExecutionException, InterruptedException, ExecutionException {
        List<QueryDocumentSnapshot> allStocks = new ArrayList<>();

        // Get the "watchlists" collection
        CollectionReference watchlistCollection = firestore.collection("users");

        // Fetch all watchlist documents
        ApiFuture<QuerySnapshot> watchlistSnapshotFuture = watchlistCollection.get();
        QuerySnapshot watchlistSnapshot = watchlistSnapshotFuture.get();

        // Iterate through each watchlist document
        for (DocumentSnapshot userDocSnapshot : watchlistSnapshot.getDocuments()) {
            // Access the "stocks" subcollection for each user
            CollectionReference stocksCollection = userDocSnapshot.getReference().collection("stocks");

            ApiFuture<QuerySnapshot> stocksSnapshotFuture = stocksCollection.get();
            QuerySnapshot stocksSnapshot = stocksSnapshotFuture.get();

            allStocks.addAll(stocksSnapshot.getDocuments());
        }

        return allStocks;
    }
}