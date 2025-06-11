package com.subwayit.gui;

import com.subwayit.dao.PenanggungDAO;
import com.subwayit.dao.TanggunganDAO;
import com.subwayit.model.Penanggung;
import com.subwayit.model.Tanggungan;
import com.subwayit.model.User;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class MembersPage {

    private Stage primaryStage;
    private User loggedInUser;
    private PenanggungDAO penanggungDAO;
    private TanggunganDAO tanggunganDAO;

    private ObservableList<User> familyMembers;
    private GridPane memberCardsGrid;

    // Theme colors
    private static final String PRIMARY_GREEN = "#86DA71";
    private static final String DARK_GREEN = "#6BB85A";
    private static final String LIGHT_GREEN = "#F0F9ED";
    private static final String TEXT_DARK = "#2D3748";
    private static final String TEXT_GRAY = "#64748B";

    public MembersPage(Stage primaryStage, User user) {
        this.primaryStage = primaryStage;
        this.loggedInUser = user;
        this.penanggungDAO = new PenanggungDAO();
        this.tanggunganDAO = new TanggunganDAO();
        this.familyMembers = FXCollections.observableArrayList();
    }

    public Scene createScene() {
        // Modern top navigation bar
        HBox topNav = createModernNavigationBar();

        // Main content area with modern styling
        VBox mainContent = createModernContentArea();
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white; -fx-background: white;");

        // Root layout with modern background
        BorderPane root = new BorderPane();
        root.setTop(topNav);
        root.setCenter(scrollPane);
        root.setStyle("-fx-background-color: linear-gradient(135deg, " + LIGHT_GREEN + " 0%, #ffffff 50%, " + LIGHT_GREEN + " 100%);");

        return new Scene(root, 1200, 800);
    }

    private HBox createModernNavigationBar() {
        HBox navBar = new HBox(20);
        navBar.setPadding(new Insets(15, 30, 15, 30));
        navBar.setAlignment(Pos.CENTER_LEFT);
        
        // Green navigation bar
        navBar.setStyle("-fx-background-color: " + PRIMARY_GREEN + "; " +
                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);");

        // Logo with modern styling
        Label logo = new Label("SUBWAYIT");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        logo.setTextFill(Color.WHITE);
        logo.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 1, 0, 0, 1);");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Modern navigation buttons
        Button homeBtn = createModernNavLink("Home", "🏠");
        homeBtn.setOnAction(e -> {
            DashboardPage dashboardPage = new DashboardPage(primaryStage, loggedInUser);
            primaryStage.setScene(dashboardPage.createScene());
            primaryStage.centerOnScreen();
        });

        Button dashboardsBtn = createModernNavLink("Dashboard", "📊");
        dashboardsBtn.setOnAction(e -> {
            DashboardPage dashboardPage = new DashboardPage(primaryStage, loggedInUser);
            primaryStage.setScene(dashboardPage.createScene());
            primaryStage.centerOnScreen();
        });

        Button membersBtn = createModernNavLink("Members", "👥");
        // Highlight current page
        membersBtn.setStyle(membersBtn.getStyle() + 
            "-fx-background-color: rgba(255, 255, 255, 0.2); " +
            "-fx-border-color: rgba(255, 255, 255, 0.4); " +
            "-fx-border-width: 1px;");

        Button debtBtn = createModernNavLink("Debt", "💳");

        navBar.getChildren().addAll(logo, spacer, homeBtn, dashboardsBtn, membersBtn, debtBtn);
        return navBar;
    }

    private Button createModernNavLink(String text, String icon) {
        Button btn = new Button(icon + " " + text);
        btn.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        btn.setPadding(new Insets(10, 16, 10, 16));
        btn.setStyle("-fx-background-color: transparent; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 8; " +
                    "-fx-border-radius: 8; " +
                    "-fx-cursor: hand;");
        
        // Hover effects
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + 
            "-fx-background-color: rgba(255,255,255,0.15);"));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle()
            .replace("-fx-background-color: rgba(255,255,255,0.15);", "")));
        
        return btn;
    }

    private VBox createModernContentArea() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(30, 40, 30, 40));
        content.setAlignment(Pos.TOP_CENTER);

        // Modern header section
        VBox headerSection = createModernHeaderSection();
        
        // Modern statistics cards
        HBox statsSection = createModernStatsSection();

        // Modern member cards grid
        VBox membersSection = createModernMembersSection();

        content.getChildren().addAll(headerSection, statsSection, membersSection);
        return content;
    }

    private VBox createModernHeaderSection() {
        VBox headerSection = new VBox(15);
        headerSection.setAlignment(Pos.CENTER_LEFT);

        // Title with green accent
        HBox titleRow = new HBox(15);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        
        // Decorative green circle
        Circle titleIcon = new Circle(25);
        titleIcon.setFill(Color.web(PRIMARY_GREEN, 0.2));
        titleIcon.setStroke(Color.web(PRIMARY_GREEN));
        titleIcon.setStrokeWidth(2);
        
        VBox titleContainer = new VBox(5);
        Label membersTitle = new Label("Family Financial Members 👨‍👩‍👧‍👦");
        membersTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        membersTitle.setTextFill(Color.web(TEXT_DARK));
        
        Label subtitle = new Label("Manage your family's financial overview and member details");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        subtitle.setTextFill(Color.web(TEXT_GRAY));
        
        titleContainer.getChildren().addAll(membersTitle, subtitle);
        titleRow.getChildren().addAll(titleIcon, titleContainer);

        // Decorative line
        Rectangle decorativeLine = new Rectangle(80, 3);
        decorativeLine.setFill(Color.web(PRIMARY_GREEN));
        decorativeLine.setArcWidth(3);
        decorativeLine.setArcHeight(3);

        headerSection.getChildren().addAll(titleRow, decorativeLine);
        return headerSection;
    }

    private HBox createModernStatsSection() {
        HBox statsSection = new HBox(25);
        statsSection.setAlignment(Pos.CENTER);

        // Average earnings card
        VBox earningsCard = createModernStatsCard(
            "💰 Average Monthly Earnings", 
            "$25,020.07", 
            "+12.5% from last month",
            PRIMARY_GREEN
        );

        // Average spending card
        VBox spendingCard = createModernStatsCard(
            "💸 Average Monthly Spending", 
            "$22,140.07", 
            "+8.3% from last month",
            "#ff6b6b"
        );

        // Net savings card
        VBox savingsCard = createModernStatsCard(
            "📈 Net Monthly Savings", 
            "$2,880.00", 
            "+45.2% from last month",
            DARK_GREEN
        );

        statsSection.getChildren().addAll(earningsCard, spendingCard, savingsCard);
        return statsSection;
    }

    private VBox createModernStatsCard(String title, String value, String change, String accentColor) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(20, 20, 20, 20));
        card.setPrefWidth(280);
        
        // Modern card styling
        card.setStyle("-fx-background-color: white; " +
                     "-fx-background-radius: 12; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2); " +
                     "-fx-border-color: #E2E8F0; " +
                     "-fx-border-width: 1; " +
                     "-fx-border-radius: 12;");

        // Title
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        titleLabel.setTextFill(Color.web(TEXT_GRAY));

        // Value
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        valueLabel.setTextFill(Color.web(TEXT_DARK));

        // Change indicator
        Label changeLabel = new Label(change);
        changeLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 12));
        changeLabel.setTextFill(Color.web(accentColor));

        // Accent line
        Rectangle accentLine = new Rectangle(40, 3);
        accentLine.setFill(Color.web(accentColor));
        accentLine.setArcWidth(3);
        accentLine.setArcHeight(3);

        card.getChildren().addAll(titleLabel, valueLabel, changeLabel, accentLine);

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + 
            "-fx-border-color: " + accentColor + ";"));
        card.setOnMouseExited(e -> card.setStyle(card.getStyle()
            .replace("-fx-border-color: " + accentColor + ";", "-fx-border-color: #E2E8F0;")));

        return card;
    }

    private VBox createModernMembersSection() {
        VBox membersSection = new VBox(20);

        // Section header
        HBox sectionHeader = new HBox(15);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);
        
        Rectangle accentBar = new Rectangle(4, 30);
        accentBar.setFill(Color.web(PRIMARY_GREEN));
        accentBar.setArcWidth(4);
        accentBar.setArcHeight(4);
        
        Label sectionTitle = new Label("Family Members");
        sectionTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        sectionTitle.setTextFill(Color.web(TEXT_DARK));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button addMemberBtn = new Button("➕ Add Member");
        addMemberBtn.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        addMemberBtn.setPadding(new Insets(10, 20, 10, 20));
        addMemberBtn.setTextFill(Color.WHITE);
        addMemberBtn.setStyle("-fx-background-color: " + PRIMARY_GREEN + "; " +
                             "-fx-background-radius: 8; " +
                             "-fx-cursor: hand; " +
                             "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");
        
        addMemberBtn.setOnMouseEntered(e -> addMemberBtn.setStyle(addMemberBtn.getStyle().replace(PRIMARY_GREEN, DARK_GREEN)));
        addMemberBtn.setOnMouseExited(e -> addMemberBtn.setStyle(addMemberBtn.getStyle().replace(DARK_GREEN, PRIMARY_GREEN)));
        
        addMemberBtn.setOnAction(e -> {
            AddMemberForm addMemberForm = new AddMemberForm();
            addMemberForm.display();
            loadAndDisplayMembers();
        });
        
        sectionHeader.getChildren().addAll(accentBar, sectionTitle, spacer, addMemberBtn);

        // Member cards grid
        memberCardsGrid = new GridPane();
        memberCardsGrid.setHgap(25);
        memberCardsGrid.setVgap(25);
        memberCardsGrid.setAlignment(Pos.CENTER);

        // Load and display members
        loadAndDisplayMembers();

        membersSection.getChildren().addAll(sectionHeader, memberCardsGrid);
        return membersSection;
    }

    private void loadAndDisplayMembers() {
        memberCardsGrid.getChildren().clear();
        familyMembers.clear();

        // Load members based on logged-in user
        if (loggedInUser instanceof Penanggung) {
            Penanggung penanggung = penanggungDAO.getPenanggungById(loggedInUser.getUserId());
            if (penanggung != null) {
                familyMembers.add(penanggung);
            }
        } else {
            Tanggungan tanggungan = tanggunganDAO.getTanggunganById(loggedInUser.getUserId());
            if (tanggungan != null) {
                familyMembers.add(tanggungan);
            }
        }

        // Add all dependents
        List<Tanggungan> allDependents = tanggunganDAO.getAllTanggunan();
        for(Tanggungan t : allDependents) {
            if (!t.getUserId().equals(loggedInUser.getUserId())) {
                familyMembers.add(t);
            }
        }

        // Populate grid with member cards
        int col = 0;
        int row = 0;
        for (User member : familyMembers) {
            VBox memberCard = createModernMemberCard(member);
            memberCardsGrid.add(memberCard, col, row);
            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }

        // Add the "Add Member" card
        VBox addMemberCard = createModernAddMemberCard();
        memberCardsGrid.add(addMemberCard, col, row);
    }

    private VBox createModernMemberCard(User member) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(20, 15, 20, 15));
        card.setPrefSize(220, 280);
        
        // Modern card styling
        card.setStyle("-fx-background-color: white; " +
                     "-fx-background-radius: 16; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 12, 0, 0, 4); " +
                     "-fx-border-color: #E2E8F0; " +
                     "-fx-border-width: 1; " +
                     "-fx-border-radius: 16;");

        // Profile picture container
        StackPane profileContainer = new StackPane();
        profileContainer.setPrefSize(80, 80);
        profileContainer.setStyle("-fx-background-color: " + PRIMARY_GREEN + "30; " +
                                 "-fx-background-radius: 40; " +
                                 "-fx-border-color: " + PRIMARY_GREEN + "; " +
                                 "-fx-border-width: 2; " +
                                 "-fx-border-radius: 40;");
        
        // Profile icon/initial
        Label profileIcon = new Label(member.getNama().substring(0, 1).toUpperCase());
        profileIcon.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        profileIcon.setTextFill(Color.web(PRIMARY_GREEN));
        
        profileContainer.getChildren().add(profileIcon);

        // Member info
        VBox infoContainer = new VBox(8);
        infoContainer.setAlignment(Pos.CENTER);
        
        // Name and age
        Label nameLabel = new Label(member.getNama());
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        nameLabel.setTextFill(Color.web(TEXT_DARK));
        nameLabel.setWrapText(true);
        
        Label ageLabel = new Label(member.getUmur() + " years old");
        ageLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        ageLabel.setTextFill(Color.web(TEXT_GRAY));

        // Financial info (placeholder)
        Label financialLabel = new Label("$XX,XXX.XX");
        financialLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        financialLabel.setTextFill(Color.web(DARK_GREEN));

        // Role badge
        HBox roleBadge = new HBox();
        roleBadge.setAlignment(Pos.CENTER);
        roleBadge.setPadding(new Insets(6, 12, 6, 12));
        roleBadge.setStyle("-fx-background-color: " + LIGHT_GREEN + "; " +
                          "-fx-background-radius: 12; " +
                          "-fx-border-color: " + PRIMARY_GREEN + "40; " +
                          "-fx-border-width: 1; " +
                          "-fx-border-radius: 12;");
        
        String roleText = member.getRole();
        if (member instanceof Penanggung) {
            roleText = "👨‍💼 " + roleText;
        } else if (member instanceof Tanggungan) {
            roleText = "👨‍🎓 " + roleText;
        }
        
        Label roleLabel = new Label(roleText);
        roleLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 12));
        roleLabel.setTextFill(Color.web(DARK_GREEN));
        
        roleBadge.getChildren().add(roleLabel);

        // Additional info
        String additionalInfo = "";
        if (member instanceof Penanggung) {
            additionalInfo = "Financial Guardian";
        } else if (member instanceof Tanggungan) {
            additionalInfo = ((Tanggungan) member).getPendidikan();
        }
        
        Label additionalLabel = new Label(additionalInfo);
        additionalLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        additionalLabel.setTextFill(Color.web(TEXT_GRAY));
        additionalLabel.setWrapText(true);
        additionalLabel.setAlignment(Pos.CENTER);

        infoContainer.getChildren().addAll(nameLabel, ageLabel, financialLabel, roleBadge, additionalLabel);
        card.getChildren().addAll(profileContainer, infoContainer);

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + 
            "-fx-border-color: " + PRIMARY_GREEN + "; -fx-scale-x: 1.02; -fx-scale-y: 1.02;"));
        card.setOnMouseExited(e -> card.setStyle(card.getStyle()
            .replace("-fx-border-color: " + PRIMARY_GREEN + ";", "-fx-border-color: #E2E8F0;")
            .replace("-fx-scale-x: 1.02; -fx-scale-y: 1.02;", "")));

        return card;
    }

    private VBox createModernAddMemberCard() {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20, 15, 20, 15));
        card.setPrefSize(220, 280);
        
        // Modern add card styling
        card.setStyle("-fx-background-color: white; " +
                     "-fx-background-radius: 16; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2); " +
                     "-fx-border-color: " + PRIMARY_GREEN + "60; " +
                     "-fx-border-width: 2; " +
                     "-fx-border-radius: 16; " +
                     "-fx-border-style: dashed; " +
                     "-fx-cursor: hand;");

        // Plus icon container
        StackPane iconContainer = new StackPane();
        iconContainer.setPrefSize(80, 80);
        iconContainer.setStyle("-fx-background-color: " + LIGHT_GREEN + "; " +
                              "-fx-background-radius: 40; " +
                              "-fx-border-color: " + PRIMARY_GREEN + "40; " +
                              "-fx-border-width: 2; " +
                              "-fx-border-radius: 40;");
        
        Label plusIcon = new Label("➕");
        plusIcon.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        plusIcon.setTextFill(Color.web(PRIMARY_GREEN));
        
        iconContainer.getChildren().add(plusIcon);

        // Add member text
        VBox textContainer = new VBox(8);
        textContainer.setAlignment(Pos.CENTER);
        
        Label addLabel = new Label("Add New Member");
        addLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        addLabel.setTextFill(Color.web(TEXT_DARK));
        
        Label descLabel = new Label("Click to add a new family member to your financial group");
        descLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        descLabel.setTextFill(Color.web(TEXT_GRAY));
        descLabel.setWrapText(true);
        descLabel.setAlignment(Pos.CENTER);
        
        textContainer.getChildren().addAll(addLabel, descLabel);
        card.getChildren().addAll(iconContainer, textContainer);

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + 
            "-fx-background-color: " + LIGHT_GREEN + "; -fx-scale-x: 1.02; -fx-scale-y: 1.02;"));
        card.setOnMouseExited(e -> card.setStyle(card.getStyle()
            .replace("-fx-background-color: " + LIGHT_GREEN + ";", "-fx-background-color: white;")
            .replace("-fx-scale-x: 1.02; -fx-scale-y: 1.02;", "")));

        // Action to open AddMemberForm
        card.setOnMouseClicked(e -> {
            AddMemberForm addMemberForm = new AddMemberForm();
            addMemberForm.display();
            loadAndDisplayMembers();
        });

        return card;
    }
}