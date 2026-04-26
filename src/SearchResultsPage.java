import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SearchResultsPage extends JPanel {

    private final JFrame frame;
    private final String query;
    private SortOrder sortOrder = SortOrder.DEFAULT;
    private JScrollPane scroll;

    public SearchResultsPage(JFrame frame, String query) {
        this.frame = frame;
        this.query = query;
        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        add(buildTopBar(), BorderLayout.NORTH);
        scroll = new JScrollPane(buildGrid());
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.getViewport().setBackground(Theme.BG);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(buildHeader(), BorderLayout.NORTH);
        wrap.add(buildToolbar(), BorderLayout.SOUTH);
        return wrap;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(Theme.HEADER);
        header.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        header.setPreferredSize(new Dimension(0, 58));

        JButton back = UIUtils.navIconButton("images/back.png", 24);
        back.addActionListener(e -> UIUtils.navigate(frame, new HomePage(frame)));
        header.add(back, BorderLayout.WEST);

        JLabel title = new JLabel("Results for \"" + query + "\"", SwingConstants.CENTER);
        title.setFont(Theme.bold(16f));
        title.setForeground(Theme.TEXT_ON_DARK);
        header.add(title, BorderLayout.CENTER);

        Supplier<JPanel> backHere = () -> new SearchResultsPage(frame, query);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(UIUtils.cartBadgePanel(frame, backHere));
        header.add(right, BorderLayout.EAST);

        return header;
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        bar.setBackground(Theme.SURFACE);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));

        List<Product> raw = ProductCatalog.search(query);
        Color countColor = raw.isEmpty() ? Theme.DANGER : Theme.SUCCESS;
        JLabel countLbl = new JLabel(raw.size() + (raw.size() == 1 ? " result" : " results"));
        countLbl.setFont(Theme.bold(13f));
        countLbl.setForeground(countColor);

        JLabel sep = new JLabel("|");
        sep.setForeground(Theme.TEXT_2);

        JLabel sortLbl = new JLabel("Sort by:");
        sortLbl.setFont(Theme.body(13f));
        sortLbl.setForeground(Theme.TEXT_2);

        JComboBox<SortOrder> sortBox = new JComboBox<>(SortOrder.values());
        sortBox.setFont(Theme.body(13f));
        sortBox.setForeground(Theme.TEXT);
        sortBox.setBackground(Theme.SURFACE);
        sortBox.addActionListener(e -> {
            sortOrder = (SortOrder) sortBox.getSelectedItem();
            rebuildGrid();
        });

        bar.add(countLbl);
        bar.add(sep);
        bar.add(sortLbl);
        bar.add(sortBox);
        return bar;
    }

    private List<Product> fetchProducts() {
        // 1. Try AI semantic search microservice
        try {
            String baseUrl = System.getenv("AI_SEARCH_URL") != null ? System.getenv("AI_SEARCH_URL") : "http://localhost:8000";
            String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
            URL url = new URL(baseUrl + "/search?q=" + encoded);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(1500);
            conn.setReadTimeout(3000);

            if (conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                // Parse simple JSON: {"results":["Phone","Laptop",...]}
                String json = sb.toString().trim();
                String inner = json.replaceAll(".*\\[(.*)\\].*", "$1").replace("\"", "").trim();
                List<Product> aiResults = new ArrayList<>();
                if (!inner.isBlank()) {
                    for (String name : inner.split(",")) {
                        Product p = ProductCatalog.getByName(name.trim());
                        if (p != null) aiResults.add(p);
                    }
                }
                if (!aiResults.isEmpty()) {
                    System.out.println("[AI Search] Query: \"" + query + "\" → " + aiResults.stream().map(p -> p.name).toList());
                    return aiResults;
                }
            }
        } catch (Exception e) {
            System.out.println("[AI Search] Microservice unavailable, falling back to local search. (" + e.getMessage() + ")");
        }

        // 2. Fallback: local string-match search
        return ProductCatalog.search(query);
    }

    private JPanel buildGrid() {
        List<Product> products = ProductCatalog.sort(fetchProducts(), sortOrder);

        if (products.isEmpty()) {
            JPanel empty = new JPanel(new GridBagLayout());
            empty.setBackground(Theme.BG);
            JLabel lbl = new JLabel("No products found for \"" + query + "\"");
            lbl.setFont(Theme.bold(16f));
            lbl.setForeground(Color.decode("#9CA3AF"));
            empty.add(lbl);
            return empty;
        }

        JPanel grid = new JPanel(new GridLayout(0, 3, 16, 16));
        grid.setBackground(Theme.BG);
        grid.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        for (Product p : products) grid.add(UIUtils.productCard(p, frame));
        return grid;
    }

    private void rebuildGrid() {
        scroll.setViewportView(buildGrid());
        scroll.revalidate();
        scroll.repaint();
    }
}
