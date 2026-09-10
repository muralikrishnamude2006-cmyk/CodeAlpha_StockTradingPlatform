import java.util.*;

class Stock {
    private String symbol;
    private String name;
    private double price;

    public Stock(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
    }

    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public double getPrice() { return price; }

    public void updatePrice() {
        // Market fluctuation between -3% and +3%
        double changePercent = (Math.random() * 6 - 3) / 100;
        this.price = Math.max(1.0, this.price * (1 + changePercent));
    }
}

class Portfolio {
    private double cashBalance;
    private Map<String, Integer> holdings;

    public Portfolio(double initialBalance) {
        this.cashBalance = initialBalance;
        this.holdings = new HashMap<>();
    }

    public double getCashBalance() { return cashBalance; }

    public boolean buyStock(Stock stock, int quantity) {
        double cost = stock.getPrice() * quantity;
        if (cost > cashBalance) {
            return false;
        }
        cashBalance -= cost;
        holdings.put(stock.getSymbol(), holdings.getOrDefault(stock.getSymbol(), 0) + quantity);
        return true;
    }

    public boolean sellStock(Stock stock, int quantity) {
        int owned = holdings.getOrDefault(stock.getSymbol(), 0);
        if (quantity > owned) {
            return false;
        }
        cashBalance += stock.getPrice() * quantity;
        if (owned == quantity) {
            holdings.remove(stock.getSymbol());
        } else {
            holdings.put(stock.getSymbol(), owned - quantity);
        }
        return true;
    }

    public void displayPortfolio(Map<String, Stock> market) {
        System.out.println("\n--- Your Portfolio ---");
        System.out.printf("Available Cash: $%.2f\n", cashBalance);
        System.out.printf("%-10s %-10s %-15s %-15s\n", "Symbol", "Shares", "Current Price", "Current Value");
        double totalStockValue = 0;
        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            Stock s = market.get(entry.getKey());
            double val = s.getPrice() * entry.getValue();
            totalStockValue += val;
            System.out.printf("%-10s %-10d $%-14.2f $%-14.2f\n", entry.getKey(), entry.getValue(), s.getPrice(), val);
        }
        System.out.printf("Total Portfolio Net Worth: $%.2f\n", (cashBalance + totalStockValue));
    }
}

public class StockTradingPlatform {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Map<String, Stock> market = new HashMap<>();
        market.put("AAPL", new Stock("AAPL", "Apple Inc.", 180.00));
        market.put("GOOGL", new Stock("GOOGL", "Alphabet Inc.", 140.00));
        market.put("TSLA", new Stock("TSLA", "Tesla Inc.", 220.00));
        market.put("AMZN", new Stock("AMZN", "Amazon.com Inc.", 150.00));

        Portfolio portfolio = new Portfolio(10000.00);

        while (true) {
            for (Stock s : market.values()) {
                s.updatePrice();
            }

            System.out.println("\n================ Market Data ================");
            System.out.printf("%-10s %-20s %-10s\n", "Symbol", "Company", "Price");
            for (Stock s : market.values()) {
                System.out.printf("%-10s %-20s $%-10.2f\n", s.getSymbol(), s.getName(), s.getPrice());
            }
            System.out.println("1. Buy Stock | 2. Sell Stock | 3. View Portfolio | 4. Exit");
            System.out.print("Select an option: ");

            String choice = sc.nextLine().trim();
            if (choice.equals("4")) break;

            switch (choice) {
                case "1":
                    System.out.print("Enter Stock Symbol (AAPL/GOOGL/TSLA/AMZN): ");
                    String buySym = sc.nextLine().trim().toUpperCase();
                    if (!market.containsKey(buySym)) {
                        System.out.println("Stock symbol not recognized.");
                        break;
                    }
                    System.out.print("Enter quantity to buy: ");
                    try {
                        int buyQty = Integer.parseInt(sc.nextLine());
                        if (portfolio.buyStock(market.get(buySym), buyQty)) {
                            System.out.println("Purchase successful!");
                        } else {
                            System.out.println("Insufficient funds.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid number.");
                    }
                    break;

                case "2":
                    System.out.print("Enter Stock Symbol: ");
                    String sellSym = sc.nextLine().trim().toUpperCase();
                    if (!market.containsKey(sellSym)) {
                        System.out.println("Stock symbol not recognized.");
                        break;
                    }
                    System.out.print("Enter quantity to sell: ");
                    try {
                        int sellQty = Integer.parseInt(sc.nextLine());
                        if (portfolio.sellStock(market.get(sellSym), sellQty)) {
                            System.out.println("Sale executed successfully!");
                        } else {
                            System.out.println("Insufficient shares in portfolio.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid number.");
                    }
                    break;

                case "3":
                    portfolio.displayPortfolio(market);
                    break;

                default:
                    System.out.println("Invalid selection. Enter 1, 2, 3, or 4.");
            }
        }
        sc.close();
    }
}
