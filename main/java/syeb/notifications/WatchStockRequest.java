package syeb.notifications;


import lombok.Data;

@Data
public class WatchStockRequest
{
    private String userEmail;
    private String stockSymbol;
    private double averageStockPrice;

    private Pair<Double> priceRanges;

    @Data
    public static class Pair<T>{
        T low;
        T high;
        private Pair(T x , T y){
            this.low = x;
            this.high = y;
        }

        public T getLow(){
            return low;
        }
        public T getHigh(){
            return high;
        }
    }

}




